package me.tud.skriptinterpreter.lang;

import me.tud.skriptinterpreter.context.Context;
import org.jetbrains.annotations.Nullable;

public interface Expression<S, T> extends SyntaxElement<S> {

    T @Nullable [] get(Context<S> context);

    @Nullable T getSingle(Context<S> context);

    boolean isSingle();

    Class<? extends T> returnType();

    <U> Expression<S, U> asSubtype(Class<U> subtype);

}
