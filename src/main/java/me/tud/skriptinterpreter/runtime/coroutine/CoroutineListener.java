package me.tud.skriptinterpreter.runtime.coroutine;

public interface CoroutineListener<S> {

    void onStart(Coroutine<S> coroutine);

    void onResume(Coroutine<S> coroutine);

    void onSuspend(Coroutine<S> coroutine);

    void onComplete(Coroutine<S> coroutine);

    void onError(Coroutine<S> coroutine, Throwable error);

}
