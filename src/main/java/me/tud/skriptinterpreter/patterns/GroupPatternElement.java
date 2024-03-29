package me.tud.skriptinterpreter.patterns;

import me.tud.skriptinterpreter.Skript;
import me.tud.skriptinterpreter.util.StringReader;

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
    protected boolean matches(StringReader reader, MatchResult.DataContainer dataContainer, boolean exhaust) {
        MatchResult.DataContainer childContainer = dataContainer.child();
        int start = reader.cursor();
        if (pattern.head().match(reader, childContainer, false)) {
            dataContainer.combine(childContainer);
            return true;
        }
        if (!optional) return false;
        reader.cursor(start);
        return true;
    }

    public SkriptPattern pattern() {
        return pattern;
    }

    public boolean optional() {
        return optional;
    }

    @Override
    public String toString() {
        return optional ? "[" + pattern + "]" : "(" + pattern + ")";
    }

    private static Compiler<GroupPatternElement> compiler(char opening, char closing, boolean optional) {
        return (reader, context) -> {
            if (reader.peek() != opening) return null;
            String groupedPattern = reader.readEnclosed(opening, closing, '\\');
            if (groupedPattern == null)
                // Incorrect amount of braces
                // TODO maybe throw an exception?
                return null;
            Skript skript = context.skript();
            SkriptPattern compiledPattern = context.compilePattern(groupedPattern);
            return new GroupPatternElement(skript, compiledPattern, optional);
        };
    }

}
