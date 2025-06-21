package me.tud.skriptinterpreter.runtime;

import me.tud.skriptinterpreter.Skript;
import me.tud.skriptinterpreter.lang.AsyncExpression;

import java.util.IdentityHashMap;
import java.util.Map;

public final class RuntimeContext<S> implements ExecutionContext {

    private final Skript skript;
    private final S source;
    private final Environment environment;
    private final Map<AsyncExpression<S, ?>, Object> resultCache = new IdentityHashMap<>();

    public RuntimeContext(Skript skript, S source, Environment environment) {
        this.skript = skript;
        this.source = source;
        this.environment = environment;
    }

    @Override
    public Skript skript() {
        return skript;
    }

    public S source() {
        return source;
    }

    @Override
    public Environment environment() {
        return environment;
    }

    public Map<AsyncExpression<S, ?>, Object> resultCache() {
        return resultCache;
    }

}
