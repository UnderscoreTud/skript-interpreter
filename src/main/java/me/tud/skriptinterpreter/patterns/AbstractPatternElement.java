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

    protected abstract boolean matches(StringReader reader, MatchResult.Builder builder);

    @Override
    public boolean match(StringReader reader, MatchResult.Builder builder) {
        return matches(reader, builder) && matchNext(reader, builder);
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

    protected boolean matchNext(StringReader reader, MatchResult.Builder builder) {
        return next != null ? next.match(reader, builder) : !reader.canRead();
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
