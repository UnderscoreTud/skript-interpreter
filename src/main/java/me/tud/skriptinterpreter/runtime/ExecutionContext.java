package me.tud.skriptinterpreter.runtime;

import me.tud.skriptinterpreter.Skript;

public interface ExecutionContext {

    Skript skript();

    Environment environment();

}
