package me.tud.skriptinterpreter.elements.expressions;

import me.tud.skriptinterpreter.Skript;
import me.tud.skriptinterpreter.context.Context;
import me.tud.skriptinterpreter.lang.AbstractExpression;
import me.tud.skriptinterpreter.lang.Expressions;
import me.tud.skriptinterpreter.util.Result;

public class ExprString<S> extends AbstractExpression<S, String> {

    private final String string;

    public ExprString(String string) {
        this.string = string;
    }

    @Override
    public boolean init(Expressions<S> expressions, Skript skript) {
        return true;
    }

    @Override
    public Result<String> get(Context<S> context) {
        return Result.numbered(String[]::new, string);
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
