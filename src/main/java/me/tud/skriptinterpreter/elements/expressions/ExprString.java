package me.tud.skriptinterpreter.elements.expressions;

import me.tud.skriptinterpreter.Skript;
import me.tud.skriptinterpreter.context.Context;
import me.tud.skriptinterpreter.lang.AbstractExpression;
import me.tud.skriptinterpreter.lang.Expressions;
import me.tud.skriptinterpreter.patterns.MatchResult;
import me.tud.skriptinterpreter.util.Result;

public class ExprString<S> extends AbstractExpression<S, String> {

    public static final Parser<ExprString<?>> PARSER = (parser, reader) -> {
        if (reader.read() != '"') return null;
        StringBuilder builder = new StringBuilder();
        while (reader.canRead()) {
            char c = reader.read();
            if (c != '"') {
                builder.append(c);
                continue;
            }
            if (!builder.isEmpty() && reader.canRead() && reader.peek() == '"') {
                builder.append(reader.read());
                continue;
            }
            return new ExprString<>(builder.toString());
        }
        return null;
    };

    private final String string;

    public ExprString(String string) {
        this.string = string;
    }

    @Override
    public boolean init(Skript skript, Expressions<S> expressions, MatchResult matchResult) {
        return true;
    }

    @Override
    public Result<String> get(Context<S> context) {
        return Result.of(String[]::new, string);
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
