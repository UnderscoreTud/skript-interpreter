package me.tud.skriptinterpreter.lang;

import org.jetbrains.annotations.Nullable;

public abstract class AbstractStatement<S> implements Statement<S> {

    private @Nullable Statement<S> parent, next;

    public @Nullable Statement<S> getParent() {
        return parent;
    }

    public void setParent(@Nullable Statement<S> parent) {
        this.parent = parent;
    }
    
    @Override
    public @Nullable Statement<S> getNext() {
        return next;
    }

    public void setNext(@Nullable Statement<S> next) {
        this.next = next;
    }

}
