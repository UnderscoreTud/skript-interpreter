package me.tud.skriptinterpreter.runtime;

import me.tud.skriptinterpreter.Skript;

public record RuntimeContext<S>(Skript skript, S source, Environment environment) implements ExecutionContext {}
