package me.tud.skriptinterpreter.lang;

import me.tud.skriptinterpreter.context.Context;
import org.jetbrains.annotations.Nullable;

public abstract class AbstractExpression<S, T> implements Expression<S, T> {

    @Override
    public @Nullable T getSingle(Context<S> context) {
        T[] returnValue = get(context);
        if (returnValue == null || returnValue.length != 1)
            return null;
        return returnValue[0];
    }

    @Override
    @SuppressWarnings("unchecked")
    public <U> Expression<S, U> asSubtype(Class<U> subtype) {
        Class<? extends T> returnType = returnType();
        if (subtype.isAssignableFrom(returnType))
            return (Expression<S, U>) this;
        throw new ClassCastException(returnType.toString());
    }

}
