package me.tud.skriptinterpreter;

import me.tud.skriptinterpreter.patterns.PatternCompiler;
import org.jetbrains.annotations.Contract;

public interface Skript {

    void init();
    
    void cleanup();
    
    PatternCompiler patterCompiler();

    @Contract(" -> new")
    static Skript create() {
        return new SkriptImpl();
    }

}
