package me.tud.skriptinterpreter.runtime.coroutine;

public interface CoroutineListener<S> {

    default void onStart(Coroutine<S> coroutine) {}

    default void onResume(Coroutine<S> coroutine) {}

    default void onSuspend(Coroutine<S> coroutine) {}

    default void onComplete(Coroutine<S> coroutine) {}

    default void onError(Coroutine<S> coroutine, Throwable error) {}

}
