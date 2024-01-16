package me.tud.skriptinterpreter.patterns;

import me.tud.skriptinterpreter.Skript;
import me.tud.skriptinterpreter.SkriptProperty;
import me.tud.skriptinterpreter.util.StringReader;
import org.jetbrains.annotations.Nullable;

public interface PatternElement extends SkriptProperty {

    @Nullable MatchResult match(String input, MatchResult match);

    @Nullable PatternElement getNext();

    void setNext(@Nullable PatternElement patternElement);

    @FunctionalInterface
    interface Compiler<P extends PatternElement> {
        @Nullable P compile(Skript skript, StringReader reader, PatternCompiler.Lookbehind lookbehind);
    }

}
