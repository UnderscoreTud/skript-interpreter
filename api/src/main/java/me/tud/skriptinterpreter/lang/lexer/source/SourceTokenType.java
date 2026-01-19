package me.tud.skriptinterpreter.lang.lexer.source;

import me.tud.skriptinterpreter.lang.lexer.TokenType;

/**
 * Represents the type of source token.
 */
public enum SourceTokenType implements TokenType<SourceTokenType> {
    /**
     * A word or a sequence of non-symbol characters.
     */
    WORD,
    /**
     * A numeric literal.
     */
    NUMBER,
    /**
     * A literal text part of a string or variable.
     */
    LITERAL_TEXT,
    /**
     * The start of a string literal.
     */
    STRING_START,
    /**
     * The end of a string literal.
     */
    STRING_END,
    /**
     * The start of a variable reference.
     */
    VARIABLE_START,
    /**
     * The end of a variable reference.
     */
    VARIABLE_END,
    /**
     * The start of an interpolation expression.
     */
    INTERPOLATION_START,
    /**
     * The end of an interpolation expression.
     */
    INTERPOLATION_END,
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
