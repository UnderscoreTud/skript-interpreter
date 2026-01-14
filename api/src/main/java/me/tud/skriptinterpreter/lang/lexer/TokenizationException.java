package me.tud.skriptinterpreter.lang.lexer;

/**
 * Thrown when an error occurs during tokenization.
 */
public class TokenizationException extends RuntimeException {

    /**
     * @param message the exception message
     */
    public TokenizationException(String message) {
        super(message);
    }

}
