package me.tud.skriptinterpreter;

import me.tud.skriptinterpreter.lang.Effect;
import me.tud.skriptinterpreter.lang.Expression;
import me.tud.skriptinterpreter.patterns.*;
import me.tud.skriptinterpreter.registration.SyntaxRegistry;

class SkriptImpl implements Skript {

    private final PatternCompiler patternCompiler = new PatternCompiler(this);
    private final SyntaxRegistry<Expression<?, ?>> expressions = SyntaxRegistry.of(this, Expression.class);
    private final SyntaxRegistry<Effect<?>> effects = SyntaxRegistry.of(this, Effect.class);

    @Override
    public void init() {
        patternCompiler.register(ChoicePatternElement.COMPILER);
        patternCompiler.register(GroupPatternElement.COMPILER);
        patternCompiler.register(GroupPatternElement.OPTIONAL_COMPILER);
        patternCompiler.register(ParseTagPatternElement.COMPILER);
        patternCompiler.register(RegexPatternElement.COMPILER);
    }

    @Override
    public void cleanup() {
    }

    @Override
    public SyntaxRegistry<Expression<?, ?>> expressions() {
        return expressions;
    }

    @Override
    public SyntaxRegistry<Effect<?>> effects() {
        return effects;
    }

    @Override
    public PatternCompiler patterCompiler() {
        return patternCompiler;
    }

}
