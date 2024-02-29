package me.tud.skriptinterpreter.patterns;

import me.tud.skriptinterpreter.Skript;
import me.tud.skriptinterpreter.SkriptProperty;
import me.tud.skriptinterpreter.util.ValueHolder;
import me.tud.skriptinterpreter.util.StringReader;

import java.util.*;

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
        return compile(pattern, new Context());
    }

    public SkriptPattern compile(String pattern, Context context) {
        SkriptPattern compiledPattern = context.pattern = new SkriptPattern(skript);
        if (pattern.isEmpty()) return compiledPattern.append(new LiteralPatternElement(skript, ""));
        StringReader reader = new StringReader(pattern);
        StringBuilder lookbehindBuilder = new StringBuilder();
        boolean escaped = false;
        outer:
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
            Lookbehind lookbehind = context.lookbehind = new Lookbehind(lookbehindBuilder.toString());

            for (AbstractPatternElement.Compiler<?> compiler : compilers) {
                StringReader clone = reader.clone();
                lookbehind.consume = false;
                PatternElement element = compiler.compile(clone, context);
                if (element == null) continue;

                if (!lookbehind.consume && !lookbehind.get().isEmpty())
                    compiledPattern.append(new LiteralPatternElement(skript, lookbehind.get()));
                compiledPattern.append(element);
                reader.cursor(clone.cursor());
                lookbehindBuilder = new StringBuilder();
                continue outer;
            }

            if (reader.canRead()) lookbehindBuilder.append(reader.read());
        }

        if (!lookbehindBuilder.isEmpty())
            compiledPattern.append(new LiteralPatternElement(skript, lookbehindBuilder.toString()));
        return compiledPattern;
    }

    @Override
    public Skript skript() {
        return skript;
    }

    public class Context implements SkriptProperty { 

        private final Map<Object, Object> data;
        private SkriptPattern pattern;
        private Lookbehind lookbehind;

        public Context() {
            this(new HashMap<>());
        }

        public Context(Map<Object, Object> data) {
            this.data = data;
        }

        public <K, V> ValueHolder<V> getData(K key) {
            return new ValueHolder<>() {
                @Override
                @SuppressWarnings("unchecked")
                public V get() {
                    return (V) data.get(key);
                }

                @Override
                public void set(V value) {
                    data.put(key, value);
                }
            };
        }

        public Context newScope() {
            return new Context(data);
        }

        public SkriptPattern compilePattern(String pattern) {
            return compile(pattern, newScope());
        }

        public SkriptPattern pattern() {
            return pattern;
        }

        public Lookbehind lookbehind() {
            return lookbehind;
        }

        @Override
        public Skript skript() {
            return skript;
        }

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
