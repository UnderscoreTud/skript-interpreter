package me.tud.skriptinterpreter.lang.lexer;

/**
 * Represents the type of a lexical token.
 */
public enum TokenType {
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
    EOF
}
