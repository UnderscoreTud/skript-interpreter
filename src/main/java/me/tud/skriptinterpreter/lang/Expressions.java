package me.tud.skriptinterpreter.lang;

import java.util.Collection;

public class Expressions<S> {

    private final Expression<S, ?>[] expressions;

    public Expressions(Collection<Expression<S, ?>> expressions) {
        //noinspection unchecked
        this(expressions.toArray(new Expression[0]));
    }

    @SafeVarargs
    public Expressions(Expression<S, ?>... expressions) {
        this.expressions = expressions;
    }

    public Expression<S, ?> get(int position) {
        return expressions[position];
    }

    public <T> Expression<S, T> get(int position, Class<T> type) {
        //noinspection unchecked
        return (Expression<S, T>) expressions[position];
    }

    public Expression<S, ?>[] getAll() {
        return expressions;
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder("Expressions[");
        for (int i = 0; i < expressions.length; i++) {
            if (i > 0) builder.append(", ");
            builder.append(expressions[i]);
        }
        return builder + "]";
    }

}
