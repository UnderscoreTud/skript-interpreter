package me.tud.skriptinterpreter.lang;

import me.tud.skriptinterpreter.parser.ParseContext;
import me.tud.skriptinterpreter.parser.exceptions.ParseException;

import java.util.Collection;

public interface SyntaxElement<S> {

    boolean init(Expressions<S> expressions, ParseContext context) throws ParseException;

    Collection<Expression<S, ?>> awaitingExpressions();

}
