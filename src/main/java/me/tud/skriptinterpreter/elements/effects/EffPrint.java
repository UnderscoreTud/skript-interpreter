package me.tud.skriptinterpreter.elements.effects;

import me.tud.skriptinterpreter.lang.Expression;
import me.tud.skriptinterpreter.lang.Expressions;
import me.tud.skriptinterpreter.lang.Statement;
import me.tud.skriptinterpreter.parser.ParseContext;
import me.tud.skriptinterpreter.parser.exceptions.ParseException;
import me.tud.skriptinterpreter.runtime.RuntimeContext;
import me.tud.skriptinterpreter.runtime.coroutine.Coroutine;
import me.tud.skriptinterpreter.runtime.coroutine.CoroutineManager;
import me.tud.skriptinterpreter.runtime.exceptions.ExecutionException;

import java.util.Collection;
import java.util.Collections;

public class EffPrint<S> implements Statement<S> {

    private final Expression<S, String> message;

    public EffPrint(Expression<S, String> message) {
        this.message = message;
    }

    @Override
    public boolean init(Expressions<S> expressions, ParseContext context) throws ParseException {
        return true;
    }

    @Override
    public Collection<Expression<S, ?>> awaitingExpressions() {
        return Collections.singleton(message);
    }

    @Override
    public void execute(RuntimeContext<S> context, CoroutineManager manager, Coroutine<S> coroutine) throws ExecutionException {
        System.out.println(message.evaluate(context));
    }

}
