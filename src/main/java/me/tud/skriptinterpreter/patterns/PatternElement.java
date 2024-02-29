package me.tud.skriptinterpreter.patterns;

import me.tud.skriptinterpreter.SkriptProperty;
import me.tud.skriptinterpreter.util.StringReader;
import org.jetbrains.annotations.Nullable;

public interface PatternElement extends SkriptProperty {

    boolean match(StringReader reader, MatchResult.DataContainer dataContainer, boolean exhaust);

    @Nullable PatternElement next();

    void next(PatternElement element);

    String toFullString();

    @FunctionalInterface
    interface Compiler<P extends PatternElement> {
        @Nullable P compile(StringReader reader, PatternCompiler.Context context);
    }

}
