package me.tud.skriptinterpreter;

import me.tud.skriptinterpreter.pattern.PatternTrie;
import me.tud.skriptinterpreter.runtime.Environment;
import me.tud.skriptinterpreter.runtime.coroutine.CoroutineManager;

class SkriptImpl implements Skript {

    private final PatternTrie patternTrie = new PatternTrie();
    private final Environment globalEnvironment = new Environment(input -> null);
    private final CoroutineManager coroutineManager = new CoroutineManager();

    @Override
    public void init() {
        coroutineManager.start();
    }

    @Override
    public void cleanup() {
        coroutineManager.shutdown();
    }

    @Override
    public PatternTrie patternTrie() {
        return patternTrie;
    }

    @Override
    public Environment globalEnvironment() {
        return globalEnvironment;
    }

    @Override
    public CoroutineManager coroutineManager() {
        return coroutineManager;
    }

}
