package me.tud.skriptinterpreter.parser;

import me.tud.skriptinterpreter.Skript;
import me.tud.skriptinterpreter.lang.*;
import me.tud.skriptinterpreter.patterns.ExpressionPatternElement;
import me.tud.skriptinterpreter.patterns.MatchResult;
import me.tud.skriptinterpreter.patterns.SkriptPattern;
import me.tud.skriptinterpreter.registration.SyntaxRegistry;
import me.tud.skriptinterpreter.util.StringReader;
import org.jetbrains.annotations.Nullable;

import java.util.EnumSet;
import java.util.Iterator;
import java.util.Optional;
import java.util.function.Predicate;

record SkriptParserImpl(Skript skript, String input, EnumSet<Flag> flags) implements SkriptParser {

    @Override
    public <S extends SyntaxElement<?>> S parse(Iterator<SyntaxRegistry.SyntaxInfo<? extends S>> iterator, Predicate<S> predicate) {
        while (iterator.hasNext()) {
            SyntaxRegistry.SyntaxInfo<? extends S> info = iterator.next();
            ParseResult<? extends S> result = parse(info);
            if (result == null) continue;
            if (result.element() instanceof Expression<?, ?> expr && !isValidExpression(expr)) continue;
            if (!predicate.test(result.element())) continue;
            if (!result.init(skript)) continue;
            return result.element();
        }
        return null;
    }

    private <S extends SyntaxElement<?>> @Nullable ParseResult<S> parse(SyntaxRegistry.SyntaxInfo<S> info) {
        if (info.parser() != null) {
            StringReader reader = new StringReader(input);
            S element = info.parser().parse(this, reader);
            if (element == null || reader.canRead()) return null;
            return new ParseResult<>(element, null);
        }
        for (SkriptPattern pattern : info.patterns()) {
            MatchResult result = pattern.match(input);
            if (result == null) continue;
            return new ParseResult<>(info.newInstance(), result);
        }
        return null;
    }

    private boolean isValidExpression(Expression<?, ?> expression) {
        return expression instanceof Literal<?, ?> ? hasFlag(Flag.PARSE_LITERALS) : hasFlag(Flag.PARSE_NON_LITERALS);
    }

    private record ParseResult<S extends SyntaxElement<?>>(S element, MatchResult matchResult) {

        @SuppressWarnings({"rawtypes", "unchecked"})
        public boolean init(Skript skript) {
            Expressions expressions = Optional.ofNullable(matchResult)
                    .flatMap(result -> result.getOptionalData(ExpressionPatternElement.Data.class))
                    .map(ExpressionPatternElement.Data::expressions)
                    .map(exprs -> new Expressions(exprs))
                    .orElse(null);
            return matchResult == null || element.init(skript, expressions, matchResult);
        }
        
    }

}
