package me.tud.skriptinterpreter.patterns;

import java.util.Objects;

public record SkriptPattern(String pattern, PatternElement head) {

    public SkriptPattern {
        Objects.requireNonNull(pattern, "pattern");
        Objects.requireNonNull(head, "head");
    }

    @Override
    public String toString() {
        return pattern;
    }

}
