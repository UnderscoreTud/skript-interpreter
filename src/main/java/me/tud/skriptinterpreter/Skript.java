package me.tud.skriptinterpreter;

import org.jetbrains.annotations.Contract;

public interface Skript {

    void init();
    
    void cleanup();

    @Contract(" -> new")
    static Skript create() {
        return new SkriptImpl();
    }

}
