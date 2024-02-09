package me.tud.skriptinterpreter;

import me.tud.skriptinterpreter.lang.Effect;
import me.tud.skriptinterpreter.lang.Expression;
import me.tud.skriptinterpreter.patterns.PatternCompiler;
import me.tud.skriptinterpreter.registration.SyntaxRegistry;
import me.tud.skriptinterpreter.registration.TypeRegistry;
import org.jetbrains.annotations.Contract;

public interface Skript {

    void init();

    void cleanup();

    // TODO replace with some kind of key->registry system
    SyntaxRegistry<Expression<?, ?>> expressions();

    SyntaxRegistry<Effect<?>> effects();

    TypeRegistry typeRegistry();

    PatternCompiler patterCompiler();

    @Contract(value = " -> new")
    static Skript create() {
        return new SkriptImpl();
    }

}
