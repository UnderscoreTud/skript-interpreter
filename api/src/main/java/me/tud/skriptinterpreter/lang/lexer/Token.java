package me.tud.skriptinterpreter.lang.lexer;

import me.tud.skriptinterpreter.lang.SourceSpan;

/**
 * Represents a lexical token.
 *
 * @param <T> the type of token type
 */
public interface Token<T extends TokenType<?>> {

    /**
     * @return the type of this token
     */
    T type();

    /**
     * @return the text of this token
     */
    String text();

    /**
     * @return the source span of this token
     */
    SourceSpan span();
    
    /**
     * Calculates the spacing between this token and another token.
     *
     * @param other the other token
     * @return the spacing between the tokens
     */
    default int spacing(Token<T> other) {
        return other.span().start() - span().end();
    }

    /**
     * Checks if this token is adjacent to another token.
     *
     * @param other the other token
     * @return {@code true} if the tokens are adjacent, {@code false} otherwise
     */
    default boolean adjacent(Token<T> other) {
        return spacing(other) == 0;
    }

}
