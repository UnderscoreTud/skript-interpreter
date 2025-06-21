package me.tud.skriptinterpreter.elements.expressions;

import me.tud.skriptinterpreter.lang.AsyncExpression;
import me.tud.skriptinterpreter.lang.Expression;
import me.tud.skriptinterpreter.lang.Expressions;
import me.tud.skriptinterpreter.parser.ParseContext;
import me.tud.skriptinterpreter.parser.exceptions.ParseException;
import me.tud.skriptinterpreter.runtime.RuntimeContext;
import me.tud.skriptinterpreter.runtime.exceptions.ExecutionException;

import java.util.Collection;
import java.util.Collections;
import java.util.concurrent.CompletableFuture;

public class ExprHttpRequest<S> implements AsyncExpression<S, String> {

    private final String value;

    public ExprHttpRequest(String value) {
        this.value = value;
    }

    @Override
    public boolean init(Expressions<S> expressions, ParseContext context) throws ParseException {
        return true;
    }

    @Override
    public Collection<Expression<S, ?>> awaitingExpressions() {
        return Collections.emptySet();
    }

    @Override
    public CompletableFuture<String> evaluateAsync(RuntimeContext<S> context) throws ExecutionException {
        return CompletableFuture.supplyAsync(() -> {
            try {
                System.out.println("Fetching HTTP request: " + value);
                Thread.sleep(2000); // Simulate network delay
                System.out.println("HTTP request fetched: " + value);
                return value;
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        });
    }

}
