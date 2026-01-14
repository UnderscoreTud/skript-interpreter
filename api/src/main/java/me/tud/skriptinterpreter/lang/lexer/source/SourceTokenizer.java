package me.tud.skriptinterpreter.lang.lexer.source;

import me.tud.skriptinterpreter.lang.lexer.Tokenizer;

import java.util.List;

/**
 * Represents a tokenizer for source code.
 */
public interface SourceTokenizer extends Tokenizer<SourceToken> {

    @Override
    List<SourceToken> tokenize() throws SourceTokenizationException;

}
