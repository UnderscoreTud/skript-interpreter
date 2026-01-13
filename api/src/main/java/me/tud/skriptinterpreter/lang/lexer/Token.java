package me.tud.skriptinterpreter.lang.lexer;

import me.tud.skriptinterpreter.lang.SourceSpan;

/**
 * Represents a lexical token.
 *
 * @param type the type of the token
 * @param text the raw text of the token
 * @param span the span of the token in the source code
 */
public record Token(TokenType type, String text, SourceSpan span) {

    /**
     * Creates a new token with an empty text.
     *
     * @param type the type of the token
     * @param span the span of the token in the source code
     */
    public Token(TokenType type, SourceSpan span) {
        this(type, "", span);
    }

}
