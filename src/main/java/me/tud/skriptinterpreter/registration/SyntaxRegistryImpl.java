package me.tud.skriptinterpreter.registration;

import me.tud.skriptinterpreter.Skript;
import me.tud.skriptinterpreter.lang.SyntaxElement;
import me.tud.skriptinterpreter.patterns.SkriptPattern;
import org.jetbrains.annotations.NotNull;

import java.util.*;
import java.util.function.Supplier;

class SyntaxRegistryImpl<E extends SyntaxElement<?>> implements SyntaxRegistry<E> {

    private final Skript skript;
    private final Class<E> elementType;
    private final Map<Class<? extends E>, SyntaxInfo<? extends E>> factories = new LinkedHashMap<>();

    SyntaxRegistryImpl(Skript skript, Class<E> elementType) {
        this.skript = skript;
        this.elementType = elementType;
    }

    @Override
    public <T extends E> void register(Class<T> elementType, Supplier<T> factory, String... patterns) {
        factories.put(elementType, new SyntaxInfoImpl<>(skript, elementType, factory, patterns));
    }

    @Override
    public List<SyntaxInfo<? extends E>> syntaxes() {
        return List.copyOf(factories.values());
    }

    @Override
    public @NotNull Iterator<SyntaxInfo<? extends E>> iterator() {
        return factories.values().iterator();
    }

    @Override
    public Skript skript() {
        return skript;
    }

    private static class SyntaxInfoImpl<T extends SyntaxElement<?>> implements SyntaxInfo<T> {

        private final Skript skript;
        private final Class<T> type;
        private final Supplier<T> factory;
        private final String[] rawPatterns;
        private final SkriptPattern[] patterns;

        private SyntaxInfoImpl(Skript skript, Class<T> type, Supplier<T> factory, String[] patterns) {
            this.skript = skript;
            this.type = type;
            this.factory = factory;
            this.rawPatterns = patterns;
            this.patterns = new SkriptPattern[patterns.length];
            for (int i = 0; i < patterns.length; i++)
                this.patterns[i] = skript.patterCompiler().compile(patterns[i]);
        }

        @Override
        public Class<T> type() {
            return type;
        }

        @Override
        public Supplier<T> factory() {
            return factory;
        }

        @Override
        public String[] rawPatterns() {
            return rawPatterns;
        }

        @Override
        public SkriptPattern[] patterns() {
            return patterns;
        }

        @Override
        public Skript skript() {
            return skript;
        }

        @Override
        public String toString() {
            return new StringJoiner(", ", SyntaxInfoImpl.class.getSimpleName() + "[", "]")
                    .add("type=" + type)
                    .add("rawPatterns=" + Arrays.toString(rawPatterns))
                    .toString();
        }

    }

}
