package me.tud.skriptinterpreter.elements.effects;

import me.tud.skriptinterpreter.lang.Expressions;
import me.tud.skriptinterpreter.lang.Statement;
import me.tud.skriptinterpreter.parser.ParseContext;
import me.tud.skriptinterpreter.parser.exceptions.ParseException;
import me.tud.skriptinterpreter.runtime.RuntimeContext;
import me.tud.skriptinterpreter.runtime.coroutine.Coroutine;
import me.tud.skriptinterpreter.runtime.coroutine.CoroutineManager;
import me.tud.skriptinterpreter.runtime.exceptions.ExecutionException;

import java.util.concurrent.TimeUnit;

public class EffWait implements Statement<Void> {

    private final long delay;
    private final TimeUnit unit;

    public EffWait(long delay, TimeUnit unit) {
        this.delay = delay;
        this.unit = unit;
    }

    @Override
    public boolean init(Expressions<Void> expressions, ParseContext context) throws ParseException {
        return true;
    }

    @Override
    public void execute(RuntimeContext<Void> context, CoroutineManager<Void> manager, Coroutine<Void> coroutine) throws ExecutionException {
        if (delay < 0) {
            throw new ExecutionException("Delay cannot be negative: " + delay);
        }

        manager.suspendCoroutine(coroutine);
        manager.scheduler().schedule(() -> {
            manager.resumeCoroutine(coroutine);
        }, delay, unit);
    }

}
