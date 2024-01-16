package me.tud.skriptinterpreter.patterns;

import me.tud.skriptinterpreter.Skript;
import org.jetbrains.annotations.Nullable;

public class ChoicePatternElement extends AbstractPatternElement {

    public static final Compiler<ChoicePatternElement> COMPILER = (skript, reader, lookbehind) -> {
        if (reader.read() != '|') return null;
        PatternCompiler compiler = skript.patterCompiler();
        SkriptPattern left = compiler.compile(lookbehind.consume());
        SkriptPattern right = compiler.compile(reader.finish());
        return new ChoicePatternElement(skript, left, right);
    };

    private final SkriptPattern left, right;

    public ChoicePatternElement(Skript skript, SkriptPattern left, SkriptPattern right) {
        super(skript);
        this.left = left;
        this.right = right;
    }

    @Override
    public @Nullable MatchResult match(String input, MatchResult match) {
        return null;
    }

    @Override
    public String toString() {
        return left + "|" + right;
    }

}
