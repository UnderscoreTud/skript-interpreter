package me.tud.skriptinterpreter.lang;

public class Expressions<S> {

    private final Expression<S, ?>[] expressions;

    @SafeVarargs
    public Expressions(Expression<S, ?>... expressions) {
        this.expressions = expressions;
    }

    public <T> Expression<S, T> get(int position, Class<T> type) {
        return expressions[position].asSubtype(type);
    }

}
