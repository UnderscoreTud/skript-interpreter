package me.tud.skriptinterpreter.elements.expressions;

import me.tud.skriptinterpreter.Skript;
import me.tud.skriptinterpreter.context.Context;
import me.tud.skriptinterpreter.lang.AbstractExpression;
import me.tud.skriptinterpreter.lang.Expressions;
import me.tud.skriptinterpreter.patterns.MatchResult;
import me.tud.skriptinterpreter.patterns.RegexPatternElement;
import me.tud.skriptinterpreter.util.Result;

public class ExprTest2<S> extends AbstractExpression<S, String> {

    private String value;
    
    @Override
    public boolean init(Skript skript, Expressions<S> expressions, MatchResult matchResult) {
        value = matchResult.getData(RegexPatternElement.Data.class).results().get(0).group(1);
        return true;
    }

    @Override
    public Result<String> get(Context<S> context) {
        return Result.of(value);
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
