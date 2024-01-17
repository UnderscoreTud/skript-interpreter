package me.tud.skriptinterpreter.patterns;

import me.tud.skriptinterpreter.Skript;
import me.tud.skriptinterpreter.util.StringReader;
import org.jetbrains.annotations.Nullable;

public class GroupPatternElement extends AbstractPatternElement {

    public static final Compiler<GroupPatternElement> COMPILER = compiler('(', ')', false);
    public static final Compiler<GroupPatternElement> OPTIONAL_COMPILER = compiler('[', ']', true);

    private final SkriptPattern pattern;
    private final boolean optional;

    public GroupPatternElement(Skript skript, SkriptPattern pattern, boolean optional) {
        super(skript);
        this.pattern = pattern;
        this.optional = optional;
    }

    @Override
    public @Nullable MatchResult match(String input, MatchResult previousMatch) {
        return null;
    }

    @Override
    public String toString() {
        return optional ? "[" + pattern + "]" : "(" + pattern + ")";
    }

    private static Compiler<GroupPatternElement> compiler(char opening, char closing, boolean optional) {
        return (skript, reader, lookbehind) -> {
            if (reader.peek() != opening) return null;
            String groupedPattern = reader.readEnclosed(opening, closing, '\\');
            if (groupedPattern == null)
                // Incorrect amount of braces
                // TODO maybe throw an exception?
                return null;
            return new GroupPatternElement(skript, skript.patterCompiler().compile(groupedPattern), optional);
        };
    }

}
