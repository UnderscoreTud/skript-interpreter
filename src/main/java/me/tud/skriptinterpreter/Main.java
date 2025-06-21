package me.tud.skriptinterpreter;

import me.tud.skriptinterpreter.elements.effects.EffPrint;
import me.tud.skriptinterpreter.elements.effects.EffWait;
import me.tud.skriptinterpreter.elements.expressions.ExprHttpRequest;
import me.tud.skriptinterpreter.elements.expressions.ExprString;
import me.tud.skriptinterpreter.runtime.RuntimeContext;
import me.tud.skriptinterpreter.runtime.coroutine.CoroutineManager;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

public class Main {

    public static void main(String[] args) {
        Skript skript = Skript.create();
        skript.init();
        CoroutineManager manager = skript.coroutineManager();
        RuntimeContext<Object> runtimeContext = new RuntimeContext<>(skript, new Object(), skript.globalEnvironment());
        CompletableFuture<Void> task1 = manager.startCoroutine(runtimeContext, List.of(
                new EffPrint<>(new ExprString<>("task 1 start")),
                new EffWait<>(1, TimeUnit.SECONDS),
                new EffPrint<>(new ExprString<>("task 1 end"))
        ));
        CompletableFuture<Void> task2 = manager.startCoroutine(runtimeContext, List.of(
                new EffPrint<>(new ExprHttpRequest<>("task 2 start")),
                new EffWait<>(200, TimeUnit.MILLISECONDS),
                new EffPrint<>(new ExprHttpRequest<>("task 2 middle")),
                new EffWait<>(800, TimeUnit.MILLISECONDS),
                new EffPrint<>(new ExprHttpRequest<>("task 2 end"))
        ));
        CompletableFuture.allOf(task1, task2).join();
        skript.cleanup();
    }

}
