package me.tud.skriptinterpreter.lang.lexer.pattern;

import me.tud.skriptinterpreter.lang.lexer.Tokenizer;

import java.util.List;

/**
 * Represents a tokenizer for patterns.
 */
public interface PatternTokenizer extends Tokenizer<PatternToken> {

    @Override
    List<PatternToken> tokenize() throws PatternTokenizationException;

}
