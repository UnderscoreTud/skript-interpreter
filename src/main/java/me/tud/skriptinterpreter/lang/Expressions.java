package me.tud.skriptinterpreter.lang;

import java.util.Collection;

public class Expressions {

    private final Object[] expressions;

    public Expressions(Collection<Object> expressions) {
        this(expressions.toArray(new Object[0]));
    }

    public Expressions(Object... expressions) {
        this.expressions = expressions;
    }

    public Object get(int position) {
        return expressions[position];
    }

    public <T> Object get(int position, Class<T> type) {
        return get(position); // TODO
    }

    public Object[] getAll() {
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
