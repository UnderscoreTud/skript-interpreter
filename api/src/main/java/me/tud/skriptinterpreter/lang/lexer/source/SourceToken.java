package me.tud.skriptinterpreter.lang.lexer.source;

import me.tud.skriptinterpreter.lang.SourceSpan;
import me.tud.skriptinterpreter.lang.lexer.Token;

/**
 * Represents a token in a source code.
 *
 * @param type the type of the token
 * @param text the text of the token
 * @param span the source span of the token
 */
public record SourceToken(SourceTokenType type, String text, SourceSpan span) implements Token<SourceTokenType> {

    /**
     * Creates a new token with an empty text.
     *
     * @param type the type of the token
     * @param span the span of the token in the source code
     */
    public SourceToken(SourceTokenType type, SourceSpan span) {
        this(type, "", span);
    }

}
