package me.tud.skriptinterpreter.lang.lexer.pattern;

import me.tud.skriptinterpreter.lang.lexer.TokenType;

/**
 * Represents the type of pattern token.
 */
public enum PatternTokenType implements TokenType<PatternTokenType> {
    /**
     * A literal string.
     */
    LITERAL,
    /**
     * A literal tag (e.g., literal:).
     */
    LITERAL_TAG,
    /**
     * A dynamic tag (e.g., :(a|b)).
     */
    DYNAMIC_TAG,
    /**
     * A group (e.g., (group)).
     */
    GROUP,
    /**
     * An optional group (e.g., [optional group]).
     */
    OPTIONAL_GROUP,
    /**
     * A placeholder (e.g., <placeholder>).
     */
    PLACEHOLDER,
    /**
     * A pipe '|'.
     */
    PIPE,
    /**
     * A whitespace character.
     */
    WHITESPACE,
    /**
     * The end of the input.
     */
    EOF;

    @Override
    public boolean isTerminal() {
        return this == EOF;
    }

}
