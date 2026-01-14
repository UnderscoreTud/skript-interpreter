package me.tud.skriptinterpreter.lang.lexer;

/**
 * Represents the type of token.
 *
 * @param <T> the enum type
 */
public interface TokenType<T extends Enum<T>> {

    /**
     * @return {@code true} if this token type is terminal, {@code false} otherwise
     */
    boolean isTerminal();

}
