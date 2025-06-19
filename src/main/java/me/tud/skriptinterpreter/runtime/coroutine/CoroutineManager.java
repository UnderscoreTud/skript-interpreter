package me.tud.skriptinterpreter.runtime.coroutine;

import me.tud.skriptinterpreter.lang.Statement;
import me.tud.skriptinterpreter.runtime.RuntimeContext;

import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Consumer;

public class CoroutineManager<S> {

    private final Map<String, Coroutine<S>> activeCoroutines = new ConcurrentHashMap<>();
    private final BlockingQueue<Coroutine<S>> readyQueue = new LinkedBlockingQueue<>();
    private final ExecutorService processingExecutor = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());
    private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(Runtime.getRuntime().availableProcessors());
    private final Set<CoroutineListener<S>> globalListeners = new HashSet<>();
    private final AtomicBoolean running = new AtomicBoolean(false);
    private final AtomicInteger processingThreads = new AtomicInteger(0);
    private volatile Future<?> processingTask;

    public void start() {
        if (!running.compareAndSet(false, true))
            throw new IllegalStateException("CoroutineManager is already running");
        startProcessingLoop();
    }

    private void startProcessingLoop() {
        processingTask = processingExecutor.submit(() -> {
            while (running.get() && !Thread.currentThread().isInterrupted()) {
                try {
                    Coroutine<S> coroutine = readyQueue.take();
                    processingThreads.incrementAndGet();
                    processCoroutine(coroutine);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    break;
                } finally {
                    processingThreads.decrementAndGet();
                }
            }
        });
    }

    private void processCoroutine(Coroutine<S> coroutine) {
        while (coroutine.state() == Coroutine.State.READY)
            coroutine.step(this);
        switch (coroutine.state()) {
            case COMPLETED -> {
                activeCoroutines.remove(coroutine.id());
                notifyListeners(listener -> listener.onComplete(coroutine));
            }
            case ERROR -> {
                activeCoroutines.remove(coroutine.id());
                notifyListeners(listener -> listener.onError(coroutine, coroutine.throwable()));
            }
        }
    }

    public Coroutine<S> createCoroutine(RuntimeContext<S> runtimeContext, List<Statement<S>> statements) {
        checkRunning();
        return createCoroutine(UUID.randomUUID().toString(), runtimeContext, statements);
    }

    public Coroutine<S> createCoroutine(String id, RuntimeContext<S> runtimeContext, List<Statement<S>> statements) {
        checkRunning();
        CoroutineContext<S> context = new CoroutineContext<>(runtimeContext);
        context.pushFrame(new ExecutionFrame<>(statements, "main"));
        Coroutine<S> coroutine = new Coroutine<>(id, context);
        activeCoroutines.put(coroutine.id(), coroutine);
        return coroutine;
    }

    public CompletableFuture<Void> startCoroutine(RuntimeContext<S> runtimeContext, List<Statement<S>> statements) {
        return startCoroutine(UUID.randomUUID().toString(), runtimeContext, statements);
    }

    public CompletableFuture<Void> startCoroutine(String id, RuntimeContext<S> runtimeContext, List<Statement<S>> statements) {
        checkRunning();
        CompletableFuture<Void> future = new CompletableFuture<>();
        Coroutine<S> coroutine = createCoroutine(id, runtimeContext, statements);
        coroutine.addListener(new CoroutineListener<>() {
            @Override
            public void onComplete(Coroutine<S> coroutine) {
                future.complete(null);
            }

            @Override
            public void onError(Coroutine<S> coroutine, Throwable error) {
                future.completeExceptionally(error);
            }
        });
        startCoroutine(coroutine);
        return future;
    }

    public void startCoroutine(Coroutine<S> coroutine) {
        checkRunning();
        coroutine.state(Coroutine.State.READY);
        activeCoroutines.put(coroutine.id(), coroutine);
        notifyListeners(listener -> listener.onStart(coroutine));
        readyQueue.add(coroutine);
    }

    public void resumeCoroutine(Coroutine<S> coroutine) {
        checkRunning();
        if (!activeCoroutines.containsKey(coroutine.id()))
            throw new IllegalArgumentException("Coroutine not found: " + coroutine.id());
        coroutine.state(Coroutine.State.READY);
        notifyListeners(listener -> listener.onResume(coroutine));
        readyQueue.add(coroutine);
    }

    public void suspendCoroutine(Coroutine<S> coroutine) {
        checkRunning();
        if (!activeCoroutines.containsKey(coroutine.id()))
            throw new IllegalArgumentException("Coroutine not found: " + coroutine.id());
        notifyListeners(listener -> listener.onSuspend(coroutine));
        coroutine.state(Coroutine.State.SUSPENDED);
    }

    public void shutdown() {
        shutdown(30, TimeUnit.SECONDS);
    }

    public void shutdown(long timeout, TimeUnit unit) {
        if (!running.compareAndSet(true, false))
            throw new IllegalStateException("CoroutineManager is not running");
        processingTask.cancel(true);
        long deadline = System.currentTimeMillis() + unit.toMillis(timeout);
        while (processingThreads.get() > 0 && System.currentTimeMillis() < deadline) {
            try {
                Thread.sleep(50);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                break;
            }
        }

        long remainingTime = Math.max(0, deadline - System.currentTimeMillis());
        processingExecutor.shutdown();
        scheduler.shutdown();

        try {
            if (!processingExecutor.awaitTermination(remainingTime / 2, unit))
                processingExecutor.shutdownNow();
            if (!scheduler.awaitTermination(remainingTime / 2, unit))
                scheduler.shutdownNow();
        } catch (InterruptedException e) {
            processingExecutor.shutdownNow();
            Thread.currentThread().interrupt();
        }
    }

    public void addGlobalListener(CoroutineListener<S> listener) {
        globalListeners.add(listener);
    }

    public void removeGlobalListener(CoroutineListener<S> listener) {
        globalListeners.remove(listener);
    }

    public ScheduledExecutorService scheduler() {
        return scheduler;
    }

    public Collection<Coroutine<S>> activeCoroutines() {
        return Collections.unmodifiableCollection(activeCoroutines.values());
    }

    public int activeCoroutineCount() {
        return activeCoroutines.size();
    }

    public int suspendedCoroutineCount() {
        return (int) activeCoroutines.values().stream()
                .filter(coroutine -> coroutine.state() == Coroutine.State.SUSPENDED)
                .count();
    }

    private void notifyListeners(Consumer<CoroutineListener<S>>action) {
        for (CoroutineListener<S> listener : globalListeners)
            action.accept(listener);
    }

    private void checkRunning() {
        if (!running.get())
            throw new IllegalStateException("CoroutineManager is not running");
    }

}
