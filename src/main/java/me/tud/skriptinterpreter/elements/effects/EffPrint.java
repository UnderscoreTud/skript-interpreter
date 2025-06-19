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

public class EffPrint implements Statement<Object> {

    private final Expression<Object, String> message;

    public EffPrint(Expression<Object, String> message) {
        this.message = message;
    }

    @Override
    public boolean init(Expressions<Object> expressions, ParseContext context) throws ParseException {
        return true;
    }

    @Override
    public void execute(RuntimeContext<Object> context, CoroutineManager<Object> manager, Coroutine<Object> coroutine) throws ExecutionException {
        System.out.println(message.evaluate(context));
    }

}
