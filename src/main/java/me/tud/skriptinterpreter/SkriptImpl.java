package me.tud.skriptinterpreter;

import me.tud.skriptinterpreter.lang.Effect;
import me.tud.skriptinterpreter.lang.Expression;
import me.tud.skriptinterpreter.patterns.*;
import me.tud.skriptinterpreter.registration.SyntaxRegistry;
import me.tud.skriptinterpreter.registration.TypeRegistry;

class SkriptImpl implements Skript {

    private final SyntaxRegistry<Expression<?, ?>> expressions = SyntaxRegistry.of(this, Expression.class);
    private final SyntaxRegistry<Effect<?>> effects = SyntaxRegistry.of(this, Effect.class);
    private final TypeRegistry typeRegistry = TypeRegistry.create(this);
    private final PatternCompiler patternCompiler = new PatternCompiler(this);

    @Override
    public void init() {
        patternCompiler.register(ChoicePatternElement.COMPILER);
        patternCompiler.register(ExpressionPatternElement.COMPILER);
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
    public TypeRegistry typeRegistry() {
        return typeRegistry;
    }

    @Override
    public PatternCompiler patterCompiler() {
        return patternCompiler;
    }

}
