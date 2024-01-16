package me.tud.skriptinterpreter.patterns;

import me.tud.skriptinterpreter.Skript;
import me.tud.skriptinterpreter.SkriptProperty;
import me.tud.skriptinterpreter.util.StringReader;
import org.jetbrains.annotations.Nullable;

import java.util.LinkedHashSet;
import java.util.Set;

public class PatternCompiler implements SkriptProperty {

    private final Skript skript;
    private final Set<AbstractPatternElement.Compiler<?>> compilers = new LinkedHashSet<>();

    public PatternCompiler(Skript skript) {
        this.skript = skript;
    }

    public void register(AbstractPatternElement.Compiler<?> compiler) {
        if (!compilers.add(compiler))
            throw new IllegalArgumentException("Pattern Element Compiler '" + compiler + "' is already registered");
    }

    public SkriptPattern compile(String pattern) {
        if (pattern.isEmpty()) return new SkriptPattern("", new LiteralPatternElement(skript, ""));
        // TODO implement escaping
        StringReader reader = new StringReader(pattern);
        PatternElement first = null, current = null;
        int previousEnd = 0;
        while (reader.canRead()) {
            Lookbehind lookbehind = new Lookbehind(reader.getString().substring(previousEnd, reader.cursor()));
            for (AbstractPatternElement.Compiler<?> compiler : compilers) {
                StringReader clone = reader.clone();
                lookbehind.consume = false;
                PatternElement element = compiler.compile(skript, clone, lookbehind);
                if (element == null) continue;

                if (!lookbehind.consume && !lookbehind.get().isEmpty()) {
                    current = append(current, new LiteralPatternElement(skript, lookbehind.get()));
                    if (first == null) first = current;
                }
                current = append(current, element);
                if (first == null) first = current;
                reader.cursor(clone.cursor());
                previousEnd = reader.cursor();
                break;
            }
            reader.skip();
        }

        if (previousEnd != reader.length())
            current = append(current, new LiteralPatternElement(skript, pattern.substring(previousEnd)));
        if (first == null) first = current;
        return new SkriptPattern(pattern, first);
    }

    @Override
    public Skript skript() {
        return null;
    }

    private static PatternElement append(@Nullable PatternElement first, PatternElement second) {
        if (first != null) first.setNext(second);
        return second;
    }

    public static class Lookbehind {

        private final String value;
        private boolean consume = false;

        private Lookbehind(String value) {
            this.value = value;
        }

        public String get() {
            return value;
        }

        public String consume() {
            consume = true;
            return value;
        }

    }

}
