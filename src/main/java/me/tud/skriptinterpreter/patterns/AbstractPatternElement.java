package me.tud.skriptinterpreter.patterns;

import me.tud.skriptinterpreter.Skript;
import org.jetbrains.annotations.Nullable;

public abstract class AbstractPatternElement implements PatternElement {

    private final Skript skript;
    private @Nullable PatternElement next;

    public AbstractPatternElement(Skript skript) {
        this.skript = skript;
    }

    @Override
    public @Nullable PatternElement getNext() {
        return next;
    }

    @Override
    public void setNext(@Nullable PatternElement next) {
        if (this.next != null)
            throw new IllegalStateException("Pattern Element already has a 'next' element");
        this.next = next;
    }

    @Override
    public Skript skript() {
        return skript;
    }

    @Override
    public abstract String toString();

}
