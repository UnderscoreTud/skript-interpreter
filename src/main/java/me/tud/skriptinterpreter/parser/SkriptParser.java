package me.tud.skriptinterpreter.parser;

import me.tud.skriptinterpreter.Skript;
import me.tud.skriptinterpreter.SkriptProperty;
import me.tud.skriptinterpreter.lang.SyntaxElement;
import me.tud.skriptinterpreter.registration.SyntaxRegistry;

import java.util.Arrays;
import java.util.EnumSet;
import java.util.Iterator;
import java.util.function.Predicate;

public interface SkriptParser extends SkriptProperty {

    default <S extends SyntaxElement<?>> S parse(SyntaxRegistry<S> registry) {
        return parse(registry.iterator());
    }

    default <S extends SyntaxElement<?>> S parse(SyntaxRegistry<S> registry, Predicate<S> predicate) {
        return parse(registry.iterator(), predicate);
    }

    default <S extends SyntaxElement<?>> S parse(Iterator<SyntaxRegistry.SyntaxInfo<? extends S>> iterator) {
        return parse(iterator, element -> true);
    }

    <S extends SyntaxElement<?>> S parse(Iterator<SyntaxRegistry.SyntaxInfo<? extends S>> iterator, Predicate<S> predicate);

    String input();

    EnumSet<Flag> flags();

    default boolean hasFlag(Flag flag) {
        return flags().contains(flag);
    }

    default SkriptParser withInput(String input) {
        return create(skript(), input, flags());
    }

    default SkriptParser withFlags(Flag... flags) {
        return create(skript(), input(), flags);
    }

    default SkriptParser withFlags(EnumSet<Flag> flags) {
        return create(skript(), input(), flags);
    }

    static SkriptParser create(Skript skript, String input, Flag... flags) {
        return create(skript, input, EnumSet.copyOf(Arrays.asList(flags)));
    }

    static SkriptParser create(Skript skript, String input, EnumSet<Flag> flags) {
        return new SkriptParserImpl(skript, input, flags);
    }

    enum Flag {

        PARSE_LITERALS,
        PARSE_NON_LITERALS,
        PARSE_VARIABLES,

    }
    
}
