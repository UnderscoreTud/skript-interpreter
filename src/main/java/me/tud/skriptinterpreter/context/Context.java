package me.tud.skriptinterpreter.context;

import me.tud.skriptinterpreter.Skript;

/**
 * @param <S> The source
 */
public record Context<S>(Skript skript, S source) {}
