package me.tud.skriptinterpreter.runtime.coroutine;

import me.tud.skriptinterpreter.lang.Statement;
import me.tud.skriptinterpreter.runtime.RuntimeContext;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;

public class CoroutineManager<S> {

    private final Map<String, Coroutine<S>> activeCoroutines = new ConcurrentHashMap<>();
    private final Queue<Coroutine<S>> readyQueue = new ArrayDeque<>();
    private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(2);
    private final Set<CoroutineListener<S>> globalListeners = new HashSet<>();
    private volatile boolean running = false;

    public void start() {
        if (running) {
            throw new IllegalStateException("CoroutineManager is already running");
        }
        running = true;
        addGlobalListener(new CoroutineListener<>() {
            private void attemptShutdown() {
                if (activeCoroutineCount() == 0) {
                    System.out.println("All coroutines completed, shutting down CoroutineManager.");
                    shutdown();
                }
            }

            @Override
            public void onComplete(Coroutine<S> coroutine) {
                attemptShutdown();
            }

            @Override
            public void onError(Coroutine<S> coroutine, Throwable error) {
                attemptShutdown();
            }
        });
        scheduler.scheduleAtFixedRate(this::processCoroutines, 0, 1, TimeUnit.MILLISECONDS);
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

    public void startCoroutine(Coroutine<S> coroutine) {
        checkRunning();
        if (coroutine.state() != Coroutine.State.CREATED)
            throw new IllegalStateException("Coroutine must be in CREATED state to start");
        coroutine.addListener(new CoroutineListener<>() {
            @Override
            public void onStart(Coroutine<S> coroutine) {
                readyQueue.add(coroutine);
                notifyListeners(listener -> listener.onStart(coroutine));
            }

            @Override
            public void onResume(Coroutine<S> coroutine) {
                readyQueue.add(coroutine);
                notifyListeners(listener -> listener.onResume(coroutine));
            }

            @Override
            public void onSuspend(Coroutine<S> coroutine) {
                notifyListeners(listener -> listener.onSuspend(coroutine));
            }

            @Override
            public void onComplete(Coroutine<S> coroutine) {
                activeCoroutines.remove(coroutine.id());
                notifyListeners(listener -> listener.onComplete(coroutine));
            }

            @Override
            public void onError(Coroutine<S> coroutine, Throwable error) {
                activeCoroutines.remove(coroutine.id());
                notifyListeners(listener -> listener.onError(coroutine, error));
            }
        });
        coroutine.state(Coroutine.State.READY);
    }

    public void resumeCoroutine(Coroutine<S> coroutine) {
        checkRunning();
        if (coroutine.state() != Coroutine.State.SUSPENDED)
            throw new IllegalStateException("Coroutine must be in SUSPENDED state to resume");
        coroutine.state(Coroutine.State.READY);
    }

    public void suspendCoroutine(Coroutine<S> coroutine) {
        checkRunning();
        if (coroutine.state() != Coroutine.State.READY)
            throw new IllegalStateException("Coroutine must be in READY state to suspend");
        coroutine.state(Coroutine.State.SUSPENDED);
    }

    private void processCoroutines() {
        if (!running || readyQueue.isEmpty()) return;

        Coroutine<S> coroutine = readyQueue.poll();
        if (coroutine == null) return;
        while (coroutine.state() == Coroutine.State.READY)
            coroutine.step(this);
    }

    public void shutdown() {
        if (!running)
            throw new IllegalStateException("CoroutineManager is not running");
        scheduler.shutdown();
        try {
            if (!scheduler.awaitTermination(5, TimeUnit.SECONDS)) {
                System.err.println("CoroutineManager did not terminate in the specified time, forcing shutdown.");
                scheduler.shutdownNow();
            }
        } catch (InterruptedException e) {
            System.err.println("CoroutineManager shutdown interrupted, forcing shutdown.");
            scheduler.shutdownNow();
            Thread.currentThread().interrupt();
        }
    }

    public ScheduledExecutorService scheduler() {
        return scheduler;
    }

    public void addGlobalListener(CoroutineListener<S> listener) {
        globalListeners.add(listener);
    }

    public void removeGlobalListener(CoroutineListener<S> listener) {
        globalListeners.remove(listener);
    }

    public Collection<Coroutine<S>> activeCoroutines() {
        return Collections.unmodifiableCollection(activeCoroutines.values());
    }

    public int activeCoroutineCount() {
        return activeCoroutines.size();
    }

    private void notifyListeners(Consumer<CoroutineListener<S>> action) {
        for (CoroutineListener<S> listener : globalListeners)
            action.accept(listener);
    }

    private void checkRunning() {
        if (!running)
            throw new IllegalStateException("CoroutineManager is not running");
    }

}
