package me.tud.skriptinterpreter.runtime.coroutine;

import me.tud.skriptinterpreter.runtime.RuntimeContext;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import java.util.Stack;
import java.util.concurrent.CompletableFuture;

public final class CoroutineContext<S> {

    private final RuntimeContext<S> runtimeContext;
    private final Stack<ExecutionFrame<S>> callStack = new Stack<>();

    public CoroutineContext(RuntimeContext<S> runtimeContext) {
        this.runtimeContext = Objects.requireNonNull(runtimeContext, "runtimeContext");
    }

    public void pushFrame(ExecutionFrame<S> frame) {
        callStack.push(frame);
    }

    public ExecutionFrame<S> popFrame() {
        return callStack.isEmpty() ? null : callStack.pop();
    }

    public ExecutionFrame<S> currentFrame() {
        return callStack.isEmpty() ? null : callStack.peek();
    }

    public RuntimeContext<S> runtimeContext() {
        return runtimeContext;
    }

}
