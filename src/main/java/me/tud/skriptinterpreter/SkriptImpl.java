package me.tud.skriptinterpreter;

import me.tud.skriptinterpreter.pattern.PatternTrie;
import me.tud.skriptinterpreter.runtime.Environment;

class SkriptImpl implements Skript {

    private final PatternTrie patternTrie = new PatternTrie();
    private final Environment globalEnvironment = new Environment(input -> null);

    @Override
    public void init() {
    }

    @Override
    public void cleanup() {
    }

    @Override
    public PatternTrie patternTrie() {
        return patternTrie;
    }

    @Override
    public Environment globalEnvironment() {
        return globalEnvironment;
    }

}
