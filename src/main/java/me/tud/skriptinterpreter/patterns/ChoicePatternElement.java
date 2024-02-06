package me.tud.skriptinterpreter.patterns;

import me.tud.skriptinterpreter.Skript;
import me.tud.skriptinterpreter.util.StringReader;

import java.util.ArrayList;
import java.util.List;

public class ChoicePatternElement extends AbstractPatternElement {

    public static final Compiler<ChoicePatternElement> COMPILER = (pattern, reader, lookbehind) -> {
        if (reader.read() != '|') return null;
        Skript skript = pattern.skript();
        PatternCompiler compiler = skript.patterCompiler();

        SkriptPattern left = new SkriptPattern(skript, pattern.head());
        left.append(new LiteralPatternElement(skript, lookbehind.consume()));

        SkriptPattern right = compiler.compile(reader.finish());

        pattern.clear();
        return new ChoicePatternElement(skript, getAllChoices(left, right));
    };

    private final List<PatternElement> choices;

    public ChoicePatternElement(Skript skript, List<PatternElement> choices) {
        super(skript);
        this.choices = choices;
    }

    @Override
    protected boolean matches(StringReader reader, MatchResult.Builder builder, boolean exhaust) {
        MatchResult.Builder copy = MatchResult.fromBuilder(builder);
        int start = reader.cursor();
        for (PatternElement choice : choices) {
            if (choice.match(reader, copy, false)) {
                builder.combine(copy);
                return true;
            }
            reader.cursor(start);
            copy.clear();
        }
        return false;
    }

    @Override
    public boolean match(StringReader reader, MatchResult.Builder builder, boolean exhaust) {
        return matches(reader, builder, exhaust);
    }
    
    public List<PatternElement> choices() {
        return choices;
    }
    
    @Override
    public String toString() {
        return String.join("|", choices.stream().map(PatternElement::toFullString).toList());
    }

    private static List<PatternElement> getAllChoices(SkriptPattern left, SkriptPattern right) {
        if (!(right.head() instanceof ChoicePatternElement inner)) return List.of(left.head(), right.head());
        List<PatternElement> choices = new ArrayList<>();
        choices.add(left.head());
        choices.addAll(inner.choices);
        return choices;
    }

}
