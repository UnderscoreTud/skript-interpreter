package me.tud.skriptinterpreter.patterns;

import org.jetbrains.annotations.Contract;

public class MatchResult {

    private final String input;

    private MatchResult(String input) {
        this.input = input;
    }

    @Contract(value = "_ -> new", pure = true)
    static Builder builder(String input) {
        return new Builder(input);
    }

    static class Builder {

        private final String input;

        Builder(String input) {
            this.input = input;
        }

        public MatchResult build() {
            return new MatchResult(input);
        }

    }
    
}
