package me.tud.skriptinterpreter.lang;

import me.tud.skriptinterpreter.runtime.RuntimeContext;
import me.tud.skriptinterpreter.runtime.coroutine.Coroutine;
import me.tud.skriptinterpreter.runtime.coroutine.CoroutineManager;
import me.tud.skriptinterpreter.runtime.exceptions.ExecutionException;

public interface Statement<S> extends SyntaxElement<S> {

    void execute(RuntimeContext<S> context, CoroutineManager manager, Coroutine<S> coroutine) throws ExecutionException;

}
