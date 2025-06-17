package me.tud.skriptinterpreter.lang;

public interface Expression<S, T> extends SyntaxElement<S> {

    T evaluate(S context);

}
