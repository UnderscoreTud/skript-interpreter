package me.tud.skriptinterpreter.lang.lexer.pattern;

import me.tud.skriptinterpreter.lang.SourceSpan;
import me.tud.skriptinterpreter.lang.lexer.Token;
import me.tud.skriptinterpreter.lang.lexer.TokenType;

/**
 * Represents a token in a pattern.
 *
 * @param type the type of the token
 * @param text the text of the token
 * @param span the source span of the token
 */
public record PatternToken(PatternTokenType type, String text, SourceSpan span) implements Token<PatternTokenType> {

    /**
     * @param type the type of the token
     * @param span the source span of the token
     */
    public PatternToken(PatternTokenType type, SourceSpan span) {
        this(type, "", span);
    }

}
