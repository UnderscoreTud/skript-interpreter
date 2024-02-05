package me.tud.skriptinterpreter.lang;

public abstract class AbstractExpression<S, T> implements Expression<S, T> {

    @Override
    @SuppressWarnings("unchecked")
    public <U> Expression<S, U> asSubtype(Class<U> subtype) {
        Class<? extends T> returnType = returnType();
        if (subtype.isAssignableFrom(returnType))
            return (Expression<S, U>) this;
        throw new ClassCastException(returnType.toString());
    }

}
