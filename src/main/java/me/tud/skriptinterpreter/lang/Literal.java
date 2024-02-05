package me.tud.skriptinterpreter.lang;

import me.tud.skriptinterpreter.util.Result;

public interface Literal<S, T> extends Expression<S, T> {

    Result<T> get();

}
