package me.tud.skriptinterpreter.runtime.coroutine;

import me.tud.skriptinterpreter.lang.Statement;

import java.util.*;
import java.util.function.BiConsumer;

public class Coroutine<S> {

    private final String id;
    private final CoroutineContext<S> context;
    private final List<CoroutineListener<S>> listeners = new ArrayList<>();
    private State state = State.CREATED;
    private Throwable throwable;

    public Coroutine(CoroutineContext<S> context) {
        this(UUID.randomUUID().toString(), context);
    }

    public Coroutine(String id, CoroutineContext<S> context) {
        this.id = id;
        this.context = context;
    }

    public void addListener(CoroutineListener<S> listener) {
        listeners.add(listener);
    }

    public void removeListener(CoroutineListener<S> listener) {
        listeners.remove(listener);
    }

    public String id() {
        return id;
    }

    public CoroutineContext<S> context() {
        return context;
    }

    public State state() {
        return state;
    }

    protected void state(State state) {
        switch (state) {
            case CREATED -> throw new IllegalStateException("Cannot set state to CREATED, it is the initial state.");
            case READY -> {
                if (this.state != State.CREATED && this.state != State.SUSPENDED)
                    throw new IllegalStateException("Cannot set state to READY from " + this.state);
                if (this.state == State.CREATED) notifyListeners(CoroutineListener::onStart);
                else notifyListeners(CoroutineListener::onResume);
            }
            case SUSPENDED -> {
                if (this.state != State.READY)
                    throw new IllegalStateException("Cannot set state to SUSPENDED from " + this.state);
                notifyListeners(CoroutineListener::onSuspend);
            }
            case COMPLETED -> {
                if (this.state != State.READY && this.state != State.SUSPENDED)
                    throw new IllegalStateException("Cannot set state to COMPLETED from " + this.state);
                notifyListeners(CoroutineListener::onComplete);
            }
            case ERROR -> {
                if (this.state != State.READY && this.state != State.SUSPENDED)
                    throw new IllegalStateException("Cannot set state to ERROR from " + this.state);
                notifyListeners((listener, coroutine) -> listener.onError(coroutine, throwable));
            }
        }
        this.state = state;
    }

    public Throwable throwable() {
        return throwable;
    }

    public void error(Throwable throwable) {
        state = State.ERROR;
        this.throwable = throwable;
        notifyListeners((listener, coroutine) -> listener.onError(coroutine, throwable));
    }

    public void complete() {
        state = State.COMPLETED;
        notifyListeners(CoroutineListener::onComplete);
    }

    public boolean step(CoroutineManager<S> manager) {
        if (state != State.READY)
            return false;

        ExecutionFrame<S> frame = context.currentFrame();
        if (frame == null || !frame.hasNext()) {
            context.popFrame();
            frame = context.currentFrame();
            if (frame == null) {
                complete();
                return false;
            }
        }

        if (!frame.hasNext())
            return false;

        try {
            Statement<S> statement = frame.next();
            statement.execute(context.runtimeContext(), manager, this);
            return true;
        } catch (Throwable throwable) {
            error(throwable);
            return false;
        }
    }

    protected void notifyListeners(BiConsumer<CoroutineListener<S>, Coroutine<S>> action) {
        for (CoroutineListener<S> listener : listeners)
            action.accept(listener, this);
    }

    public enum State {
        CREATED,
        READY,
        SUSPENDED,
        COMPLETED,
        ERROR
    }

}
