package me.tud.skriptinterpreter.elements.effects;

import me.tud.skriptinterpreter.lang.Expressions;
import me.tud.skriptinterpreter.lang.Statement;
import me.tud.skriptinterpreter.parser.ParseContext;
import me.tud.skriptinterpreter.parser.exceptions.ParseException;
import me.tud.skriptinterpreter.runtime.RuntimeContext;
import me.tud.skriptinterpreter.runtime.coroutine.Coroutine;
import me.tud.skriptinterpreter.runtime.coroutine.CoroutineManager;
import me.tud.skriptinterpreter.runtime.exceptions.ExecutionException;

public class EffPrint implements Statement<Void> {

    private final String message;

    public EffPrint(String message) {
        this.message = message;
    }

    @Override
    public boolean init(Expressions<Void> expressions, ParseContext context) throws ParseException {
        return true;
    }

    @Override
    public void execute(RuntimeContext<Void> context, CoroutineManager<Void> manager, Coroutine<Void> coroutine) throws ExecutionException {
        System.out.println(message);
    }

}
