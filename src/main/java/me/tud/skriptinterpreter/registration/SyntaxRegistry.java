package me.tud.skriptinterpreter.registration;

import me.tud.skriptinterpreter.Skript;
import me.tud.skriptinterpreter.SkriptProperty;
import me.tud.skriptinterpreter.lang.SyntaxElement;
import me.tud.skriptinterpreter.patterns.SkriptPattern;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.function.Supplier;

public interface SyntaxRegistry<E extends SyntaxElement<?>> extends
        Iterable<SyntaxRegistry.SyntaxInfo<? extends E>>, SkriptProperty {

    <T extends E> void register(Class<T> elementType, Supplier<T> factory, String... patterns);

    <T extends E> void register(Class<T> elementType, SyntaxElement.Parser<? extends T> parser);

    Class<E> elementType();
    
    List<SyntaxInfo<? extends E>> syntaxes();

    @SuppressWarnings("unchecked")
    @Contract("_, _ -> new")
    static <E extends SyntaxElement<?>, R extends SyntaxRegistry<? extends E>> R of(Skript skript, Class<E> elementType) {
        return (R) new SyntaxRegistryImpl<>(skript, elementType);
    }

    interface SyntaxInfo<T extends SyntaxElement<?>> extends SkriptProperty {

        Class<T> type();

        @Nullable SyntaxElement.Parser<? extends T> parser();

        Supplier<T> factory();

        default T newInstance() {
            return factory().get();
        }

        String[] rawPatterns();

        SkriptPattern[] patterns();

    }

}
