package me.tud.skriptinterpreter;

import me.tud.skriptinterpreter.pattern.PatternTrie;
import me.tud.skriptinterpreter.runtime.Environment;
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

    /**
     * Returns the global environment used for storing variables and functions.
     *
     * @return the global environment
     */
    Environment globalEnvironment();

    @Contract(value = " -> new")
    static Skript create() {
        return new SkriptImpl();
    }

}
