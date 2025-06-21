package me.tud.skriptinterpreter.lang;

import me.tud.skriptinterpreter.runtime.RuntimeContext;
import me.tud.skriptinterpreter.runtime.exceptions.ExecutionException;

import java.util.concurrent.CompletableFuture;

public interface AsyncExpression<S, T> extends Expression<S, T> {

    CompletableFuture<T> evaluateAsync(RuntimeContext<S> context) throws ExecutionException;

    default CompletableFuture<T> evaluateAsyncAndCache(RuntimeContext<S> context) throws ExecutionException {
        return evaluateAsync(context).whenComplete((result, error) -> {
            if (error == null)
                context.resultCache().put(this, result);
        });
    }

    @Override
    default T evaluate(RuntimeContext<S> context) throws ExecutionException {
        if (!context.resultCache().containsKey(this))
            throw new ExecutionException("Expression result not cached. Use evaluateAsync() to get the result asynchronously. " +
                    "Or include the expression in awaitingExpressions() to ensure it is evaluated before this point.");
        //noinspection unchecked
        return (T) context.resultCache().get(this);
    }

}
