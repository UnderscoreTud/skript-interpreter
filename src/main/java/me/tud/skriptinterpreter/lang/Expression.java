package me.tud.skriptinterpreter.lang;

import me.tud.skriptinterpreter.context.Context;
import me.tud.skriptinterpreter.util.Result;

public interface Expression<S, T> extends SyntaxElement<S> {

    Result<T>  get(Context<S> context);

    boolean isSingle();

    Class<? extends T> returnType();

    <U> Expression<S, U> asSubtype(Class<U> subtype);

}
