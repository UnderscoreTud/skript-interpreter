package me.tud.skriptinterpreter.patterns;

import me.tud.skriptinterpreter.Skript;
import org.jetbrains.annotations.Nullable;

public class ParseTagPatternElement extends AbstractPatternElement {

    public static final Compiler<ParseTagPatternElement> COMPILER = (skript, reader, lookbehind) -> {
        if (reader.peek() != ':') return null;
        String tag = lookbehind.consume();
        if (!tag.isEmpty() && tag.chars().allMatch(c -> '0' <= c && c <= '9')) {
            try {
                return new ParseTagPatternElement(skript, Integer.parseInt(tag));
            } catch (NumberFormatException ignore) {
                // most likely overflow, do nothing and return as string
            }
        }
        return new ParseTagPatternElement(skript, tag);
    };

    private final String tag;
    private final int mark;

    public ParseTagPatternElement(Skript skript, String tag) {
        this(skript, tag, -1);
    }

    public ParseTagPatternElement(Skript skript, int mark) {
        this(skript, null, mark);
    }

    private ParseTagPatternElement(Skript skript, String tag, int mark) {
        super(skript);
        this.tag = tag;
        this.mark = mark;
    }

    @Override
    public @Nullable MatchResult match(String input, MatchResult match) {
        return null;
    }

    @Override
    public String toString() {
        return (tag != null ? tag : mark) + ":";
    }

}
