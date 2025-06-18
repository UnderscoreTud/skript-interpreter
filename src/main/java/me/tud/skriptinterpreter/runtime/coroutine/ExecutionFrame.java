package me.tud.skriptinterpreter.runtime.coroutine;

import me.tud.skriptinterpreter.lang.Statement;

import java.util.List;

public final class ExecutionFrame<S> {

    private final List<Statement<S>> statements;
    private final String name;
    private int currentIndex;

    public ExecutionFrame(List<Statement<S>> statements, String name) {
        this.statements = statements;
        this.name = name;
    }

    public List<Statement<S>> statements() {
        return statements;
    }

    public String name() {
        return name;
    }

    public int currentIndex() {
        return currentIndex;
    }

    public boolean hasNext() {
        return currentIndex < statements.size();
    }

    public Statement<S> next() {
        if (!hasNext())
            throw new IllegalStateException("No more statements available in frame: " + name);
        return statements.get(currentIndex++);
    }

    public void reset() {
        currentIndex = 0;
    }

    public void jumpTo(int index) {
        if (index < 0 || index >= statements.size())
            throw new IndexOutOfBoundsException("Index out of bounds: " + index);
        currentIndex = index;
    }

}
