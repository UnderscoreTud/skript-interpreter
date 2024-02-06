package me.tud.skriptinterpreter.lang;

public class Expressions<S> {

    private final Expression<S, ?>[] expressions;

    @SafeVarargs
    public Expressions(Expression<S, ?>... expressions) {
        this.expressions = expressions;
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

}
