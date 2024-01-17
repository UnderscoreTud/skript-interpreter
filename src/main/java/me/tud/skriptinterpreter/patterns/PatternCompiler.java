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
        StringReader reader = new StringReader(pattern);
        PatternElement first = null, current = null;
        StringBuilder lookbehindBuilder = new StringBuilder();
        boolean escaped = false;
        while (reader.canRead()) {
            if (escaped) {
                escaped = false;
                lookbehindBuilder.append(reader.read());
                continue;
            } else if (reader.peek() == '\\') {
                escaped = true;
                reader.skip();
                continue;
            }
            Lookbehind lookbehind = new Lookbehind(lookbehindBuilder.toString());

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
                lookbehindBuilder = new StringBuilder();
                break;
            }

            if (reader.canRead()) lookbehindBuilder.append(reader.read());
        }

        if (!lookbehindBuilder.isEmpty())
            current = append(current, new LiteralPatternElement(skript, lookbehindBuilder.toString()));
        if (first == null) first = current;
        return new SkriptPattern(pattern, first);
    }

    @Override
    public Skript skript() {
        return skript;
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
