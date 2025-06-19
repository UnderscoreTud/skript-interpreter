package me.tud.skriptinterpreter.lang;

import me.tud.skriptinterpreter.runtime.RuntimeContext;

public interface Expression<S, T> extends SyntaxElement<S> {

    T evaluate(RuntimeContext<S> context);

}
