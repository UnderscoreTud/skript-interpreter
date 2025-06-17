package me.tud.skriptinterpreter.lang;

import me.tud.skriptinterpreter.runtime.RuntimeContext;
import me.tud.skriptinterpreter.runtime.exceptions.ExecutionException;

public interface Statement<S> extends SyntaxElement<S> {

    void execute(RuntimeContext<S> context) throws ExecutionException;

}
