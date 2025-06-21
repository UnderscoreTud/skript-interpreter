package me.tud.skriptinterpreter.lang;

import me.tud.skriptinterpreter.runtime.RuntimeContext;
import me.tud.skriptinterpreter.runtime.exceptions.ExecutionException;

public interface Expression<S, T> extends SyntaxElement<S> {

    T evaluate(RuntimeContext<S> context) throws ExecutionException;

}
