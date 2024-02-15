package me.tud.skriptinterpreter.patterns;

import me.tud.skriptinterpreter.Skript;
import me.tud.skriptinterpreter.util.StringReader;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;

public abstract class AbstractPatternElement implements PatternElement {

    private final Skript skript;
    private @Nullable PatternElement next;

    public AbstractPatternElement(Skript skript) {
        this.skript = skript;
    }

    protected abstract boolean matches(StringReader reader, MatchResult.DataContainer dataContainer, boolean exhaust);

    @Override
    public boolean match(StringReader reader, MatchResult.DataContainer dataContainer, boolean exhaust) {
        return matches(reader, dataContainer, exhaust) && matchNext(reader, dataContainer, exhaust);
    }

    @Override
    public @Nullable PatternElement next() {
        return next;
    }

    @Override
    public void next(PatternElement element) {
        if (next != null) throw new IllegalStateException("Element '" + this + "' already has a 'next' element");
        next = Objects.requireNonNull(element, "element");
    }

    @Override
    public Skript skript() {
        return skript;
    }

    protected boolean matchNext(StringReader reader, MatchResult.DataContainer dataContainer, boolean exhaust) {
        if (next == null) return !exhaust || !reader.canRead();
        return next.match(reader, dataContainer, exhaust);
    }

    @Override
    public abstract String toString();

    @Override
    public String toFullString() {
        StringBuilder builder = new StringBuilder();
        PatternElement current = this;
        while (current != null) {
            builder.append(current);
            current = current.next();
        }
        return builder.toString();
    }

}
