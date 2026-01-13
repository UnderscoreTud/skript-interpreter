package me.tud.skriptinterpreter.lang.lexer;

import java.util.List;

/**
 * Represents a tokenizer that converts source code into a list of tokens.
 */
public interface Tokenizer {

    /**
     * Tokenizes the source code.
     *
     * @return a list of tokens
     * @throws TokenizationException if an error occurs during tokenization
     */
    List<Token> tokenize() throws TokenizationException;

}
