package me.tud.skriptinterpreter.lang;

import me.tud.skriptinterpreter.Skript;
import me.tud.skriptinterpreter.patterns.MatchResult;

public interface SyntaxElement<S> {

    boolean init(Skript skript, Expressions<S> expressions, MatchResult matchResult);

}
