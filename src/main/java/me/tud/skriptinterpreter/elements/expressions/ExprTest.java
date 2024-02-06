package me.tud.skriptinterpreter.elements.expressions;

import me.tud.skriptinterpreter.Skript;
import me.tud.skriptinterpreter.context.Context;
import me.tud.skriptinterpreter.lang.AbstractExpression;
import me.tud.skriptinterpreter.lang.Expressions;
import me.tud.skriptinterpreter.patterns.MatchResult;
import me.tud.skriptinterpreter.util.Result;

public class ExprTest<S> extends AbstractExpression<S, String> {

    @Override
    public boolean init(Skript skript, Expressions<S> expressions, MatchResult matchResult) {
        return true;
    }

    @Override
    public Result<String> get(Context<S> context) {
        return Result.of("test");
    }

    @Override
    public boolean isSingle() {
        return true;
    }

    @Override
    public Class<? extends String> returnType() {
        return String.class;
    }

}
