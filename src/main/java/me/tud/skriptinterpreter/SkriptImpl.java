package me.tud.skriptinterpreter;

import me.tud.skriptinterpreter.pattern.PatternTrie;

class SkriptImpl implements Skript {

    private final PatternTrie patternTrie = new PatternTrie();

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

}
