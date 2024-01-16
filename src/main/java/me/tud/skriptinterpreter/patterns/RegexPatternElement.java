package me.tud.skriptinterpreter.patterns;

import me.tud.skriptinterpreter.Skript;
import org.jetbrains.annotations.Nullable;

import java.util.regex.Pattern;

public class RegexPatternElement extends AbstractPatternElement {

    public static final Compiler<RegexPatternElement> COMPILER = (skript, reader, lookbehind) -> {
        if (reader.peek() != '<') return null;
        String pattern = reader.readEnclosed('<', '>');
        if (pattern == null) return null; // TODO maybe throw exception?
        return new RegexPatternElement(skript, Pattern.compile(pattern));
    };

    private final Pattern regexPattern;

    public RegexPatternElement(Skript skript, Pattern regexPattern) {
        super(skript);
        this.regexPattern = regexPattern;
    }

    @Override
    public @Nullable MatchResult match(String input, MatchResult match) {
        return null;
    }

    @Override
    public String toString() {
        return "<" + regexPattern.pattern() + ">";
    }

}
