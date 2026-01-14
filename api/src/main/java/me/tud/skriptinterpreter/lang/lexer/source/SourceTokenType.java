package me.tud.skriptinterpreter.lang.lexer.source;

import me.tud.skriptinterpreter.lang.lexer.TokenType;

/**
 * Represents the type of a source token.
 */
public enum SourceTokenType implements TokenType<SourceTokenType> {
    /**
     * A word or a sequence of non-symbol characters.
     */
    WORD,
    /**
     * A literal string.
     */
    STRING,
    /**
     * A numeric literal.
     */
    NUMBER,
    /**
     * A variable reference.
     */
    VARIABLE,
    /**
     * A colon ':'.
     */
    COLON,
    /**
     * A left parenthesis '('.
     */
    LPAREN,
    /**
     * A right parenthesis ')'.
     */
    RPAREN,
    /**
     * A comma ','.
     */
    COMMA,
    /**
     * A miscellaneous symbol.
     */
    SYMBOL,
    /**
     * A newline character or sequence.
     */
    NEWLINE,
    /**
     * An indentation (increase in leading whitespace).
     */
    INDENT,
    /**
     * A dedentation (decrease in leading whitespace).
     */
    DEDENT,
    /**
     * The end of the input.
     */
    EOF;

    @Override
    public boolean isTerminal() {
        return this == EOF;
    }

}
