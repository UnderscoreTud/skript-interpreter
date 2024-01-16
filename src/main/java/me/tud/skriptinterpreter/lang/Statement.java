package me.tud.skriptinterpreter.lang;

import me.tud.skriptinterpreter.context.Context;
import org.jetbrains.annotations.Nullable;

public interface Statement<S> extends SyntaxElement<S> {

    @Nullable Statement<S> walk(Context<S> context);

    @Nullable Statement<S> getParent();

    void setParent(@Nullable Statement<S> parent);

    @Nullable Statement<S> getNext();

    void setNext(@Nullable Statement<S> next);

}
