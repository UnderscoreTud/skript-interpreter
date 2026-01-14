package me.tud.skriptinterpreter.lang.lexer.pattern;

import me.tud.skriptinterpreter.lang.lexer.TokenType;

/**
 * Represents the type of pattern token.
 */
public enum PatternTokenType implements TokenType<PatternTokenType> {
    LITERAL,
    LITERAL_TAG,
    DYNAMIC_TAG,
    GROUP,
    OPTIONAL_GROUP,
    PLACEHOLDER,
    PIPE,
    WHITESPACE,
    EOF;

    @Override
    public boolean isTerminal() {
        return this == EOF;
    }

}
