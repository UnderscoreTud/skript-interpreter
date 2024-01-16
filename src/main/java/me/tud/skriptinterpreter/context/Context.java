package me.tud.skriptinterpreter.context;

import me.tud.skriptinterpreter.Skript;
import me.tud.skriptinterpreter.SkriptProperty;

/**
 * @param <S> The source
 */
public record Context<S>(Skript skript, S source) implements SkriptProperty {}
