package me.tud.skriptinterpreter.lang.lexer;

import java.util.List;

/**
 * Represents a tokenizer that converts source code into a list of tokens.
 *
 * @param <T> the type of tokens produced by this tokenizer
 */
public interface Tokenizer<T extends Token<?>> {

    /**
     * Tokenizes the source code.
     *
     * @return a list of tokens
     * @throws TokenizationException if an error occurs during tokenization
     */
    List<T> tokenize() throws TokenizationException;

}
