package me.tud.skriptinterpreter.patterns;

import me.tud.skriptinterpreter.Skript;
import org.jetbrains.annotations.Nullable;

public class LiteralPatternElement extends AbstractPatternElement {

    private final String literal;

    public LiteralPatternElement(Skript skript, String literal) {
        super(skript);
        this.literal = process(literal);
    }

    @Override
    public @Nullable MatchResult match(String input, MatchResult previousMatch) {
        return null;
    }

    @Override
    public String toString() {
        return literal;
    }

    private static String process(String string) {
        if (string.isBlank()) return string.isEmpty() ? "" : " ";
        return string.replaceAll("\\s{2,}", " ");
    }

}
