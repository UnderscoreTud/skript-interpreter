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

    protected synchronized void state(State state) {
        if (!isValidTransition(this.state, state))
            throw new IllegalStateException("Invalid state transition from " + this.state + " to " + state + " for coroutine " + id);
        State previousState = this.state;
        this.state = state;
        notifyStateChange(previousState, state);
    }

    private boolean isValidTransition(State from, State to) {
        return switch (from) {
            case CREATED -> to == State.READY;
            case READY -> to == State.SUSPENDED || to == State.COMPLETED || to == State.ERROR;
            case SUSPENDED -> to == State.READY || to == State.COMPLETED || to == State.ERROR;
            case COMPLETED, ERROR -> false;
        };
    }

    private void notifyStateChange(State from, State to) {
        switch (to) {
            case READY -> {
                if (from == State.CREATED) {
                    notifyListeners(CoroutineListener::onStart);
                } else if (from == State.SUSPENDED) {
                    notifyListeners(CoroutineListener::onResume);
                }
            }
            case SUSPENDED -> notifyListeners(CoroutineListener::onSuspend);
            case COMPLETED -> notifyListeners(CoroutineListener::onComplete);
            case ERROR -> notifyListeners((listener, coroutine) -> listener.onError(coroutine, throwable));
        }
    }

    public Throwable throwable() {
        return throwable;
    }

    public void error(Throwable throwable) {
        this.throwable = throwable;
        state(State.ERROR);
    }

    public void complete() {
        state(State.COMPLETED);
    }

    public void step(CoroutineManager manager) {
        if (state != State.READY)
            return;

        ExecutionFrame<S> frame = context.currentFrame();
        if (frame == null || !frame.hasNext()) {
            context.popFrame();
            frame = context.currentFrame();
            if (frame == null) {
                complete();
                return;
            }
        }

        if (!frame.hasNext())
            return;

        try {
            Statement<S> statement = frame.next();
            statement.execute(context.runtimeContext(), manager, this);
        } catch (Throwable throwable) {
            error(throwable);
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
