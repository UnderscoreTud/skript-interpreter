package me.tud.skriptinterpreter.elements.effects;

import me.tud.skriptinterpreter.Skript;
import me.tud.skriptinterpreter.context.Context;
import me.tud.skriptinterpreter.lang.AbstractEffect;
import me.tud.skriptinterpreter.lang.Expression;
import me.tud.skriptinterpreter.lang.Expressions;

import java.util.logging.Level;

public class EffPrint<S> extends AbstractEffect<S> {

    private Expression<S, String> string;

    @Override
    public boolean init(Expressions<S> expressions, Skript skript) {
        string = expressions.get(0, String.class);
        return true;
    }

    @Override
    public void execute(Context<S> context) {
        System.out.println(string.getSingle(context));
    }

}
