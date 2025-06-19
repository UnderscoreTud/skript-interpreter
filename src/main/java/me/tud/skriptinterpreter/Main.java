package me.tud.skriptinterpreter;

import me.tud.skriptinterpreter.elements.effects.EffPrint;
import me.tud.skriptinterpreter.elements.effects.EffWait;
import me.tud.skriptinterpreter.runtime.RuntimeContext;
import me.tud.skriptinterpreter.runtime.coroutine.CoroutineManager;

import java.util.List;
import java.util.concurrent.TimeUnit;

public class Main {

    public static void main(String[] args) {
        Skript skript = Skript.create();
        RuntimeContext<Void> runtimeContext = new RuntimeContext<>(skript, null, skript.globalEnvironment());
        CoroutineManager<Void> manager = new CoroutineManager<>();
        manager.start();
        manager.startCoroutine(runtimeContext, List.of(
                new EffPrint("start"),
                new EffWait(1, TimeUnit.SECONDS),
                new EffPrint("end")
        )).join();
        manager.shutdown();
    }

}
