package me.tud.skriptinterpreter.parser;

import me.tud.skriptinterpreter.Skript;
import me.tud.skriptinterpreter.lang.*;
import me.tud.skriptinterpreter.patterns.MatchResult;
import me.tud.skriptinterpreter.patterns.SkriptPattern;
import me.tud.skriptinterpreter.registration.SyntaxRegistry;
import org.jetbrains.annotations.Nullable;

import java.util.EnumSet;
import java.util.Iterator;

record SkriptParserImpl(Skript skript, String input, EnumSet<Flag> flags) implements SkriptParser {

    @Override
    public <S extends SyntaxElement<?>> S parse(Iterator<SyntaxRegistry.SyntaxInfo<? extends S>> iterator) {
        while (iterator.hasNext()) {
            SyntaxRegistry.SyntaxInfo<? extends S> info = iterator.next();
            ParseResult<? extends S> result = parse(info);
            if (result == null) continue;
            if (result.element() instanceof Expression<?, ?> expr && !isValidExpression(expr)) continue;
            if (!result.element().init(skript, new Expressions<>(), result.matchResult())) continue;
            return result.element();
        }
        return null;
    }

    private <S extends SyntaxElement<?>> @Nullable ParseResult<S> parse(SyntaxRegistry.SyntaxInfo<S> info) {
        for (SkriptPattern pattern : info.patterns()) {
            MatchResult result = pattern.match(input);
            if (result == null) continue;
            return new ParseResult<>(info.newInstance(), result);
        }
        return null;
    }

    private boolean isValidExpression(Expression<?, ?> expression) {
        return expression instanceof Literal<?, ?> ? hasFlag(Flag.PARSE_LITERALS) : hasFlag(Flag.PARSE_NONLITERALS);
    }
 
    private record ParseResult<S>(S element, MatchResult matchResult) {}

}
