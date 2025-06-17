package me.tud.skriptinterpreter.pattern;

import me.tud.skriptinterpreter.lexer.LexicalAnalyzer;
import me.tud.skriptinterpreter.lexer.Token;
import me.tud.skriptinterpreter.lexer.TokenIterator;

import java.util.List;

public class LiteralPatternNode extends PatternNode {

    private final String input;
    private final List<Token> tokens;

    public LiteralPatternNode(String input) {
        this(input, new LexicalAnalyzer(input).tokenize());
    }

    protected LiteralPatternNode(String input, List<Token> tokens) {
        this.input = input;
        this.tokens = tokens;
    }

    @Override
    public String value() {
        return input;
    }

    @Override
    public Class<? extends PatternNode> type() {
        return LiteralPatternNode.class;
    }

    public String input() {
        return input;
    }

    @Override
    public boolean matches(TokenIterator tokens, MatchResult.Metadata metadata) {
        if (this.tokens.size() > tokens.tokensLeft())
            return false;

        for (Token token : this.tokens) {
            if (!token.softEquals(tokens.next()))
                return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "LiteralPatternNode[input=" + input + "]";
    }

}
