package me.tud.skriptinterpreter.lang;

import me.tud.skriptinterpreter.context.Context;

public interface Effect<S> extends Statement<S> {

    void execute(Context<S> context);

}
