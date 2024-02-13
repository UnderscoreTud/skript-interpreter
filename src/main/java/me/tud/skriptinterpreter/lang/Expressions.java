package me.tud.skriptinterpreter.lang;

import java.util.*;

public class Expressions<S> {

    private final Expression<S, ?>[] expressions;

    @SuppressWarnings("unchecked")
    public Expressions(Collection<Expression<S, ?>> expressions) {
        this(expressions.toArray(new Expression[0]));
    }

    @SafeVarargs
    public Expressions(Expression<S, ?>... expressions) {
        this.expressions = Objects.requireNonNull(expressions, "expressions array cannot be null");
    }

    public Expression<S, ?> get(int position) {
        return expressions[position];
    }

    public <T> Expression<S, T> get(int position, Class<T> type) {
        return expressions[position].asSubtype(type);
    }

    public Expression<S, ?>[] getAll() {
        return expressions;
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder("Expressions[");
        for (int i = 0; i < expressions.length; i++) {
            builder.append(expressions[i]);
            if (i > 0) builder.append(", ");
        }
        return builder + "]";
    }

}
