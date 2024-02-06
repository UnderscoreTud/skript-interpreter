package me.tud.skriptinterpreter.lang;

import me.tud.skriptinterpreter.Skript;
import me.tud.skriptinterpreter.parser.SkriptParser;
import me.tud.skriptinterpreter.patterns.MatchResult;
import me.tud.skriptinterpreter.util.StringReader;

public interface SyntaxElement<S> {

    boolean init(Skript skript, Expressions<S> expressions, MatchResult matchResult);

    interface Parser<E extends SyntaxElement<?>> {

        E parse(SkriptParser parser, StringReader reader);

    }
    
}
