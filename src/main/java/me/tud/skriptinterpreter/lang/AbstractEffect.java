package me.tud.skriptinterpreter.lang;

import me.tud.skriptinterpreter.context.Context;

public abstract class AbstractEffect<S> extends AbstractStatement<S> implements Effect<S> {

    @Override
    public Statement<S> walk(Context<S> context) {
        execute(context);
        return getNext();
    }

}
