package me.tud.skriptinterpreter.lang;

import me.tud.skriptinterpreter.Skript;

public interface SyntaxElement<S> {

    boolean init(Expressions<S> expressions, Skript skript);

}
