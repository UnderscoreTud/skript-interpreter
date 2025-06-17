package me.tud.skriptinterpreter;

import me.tud.skriptinterpreter.pattern.PatternTrie;
import org.jetbrains.annotations.Contract;

public interface Skript {

    void init();

    void cleanup();

    /**
     * Returns the pattern trie used for matching patterns.
     *
     * @return the pattern trie
     */
    PatternTrie patternTrie();

    @Contract(value = " -> new")
    static Skript create() {
        return new SkriptImpl();
    }

}
