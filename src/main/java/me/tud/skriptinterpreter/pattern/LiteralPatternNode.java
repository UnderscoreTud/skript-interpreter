package me.tud.skriptinterpreter.pattern;

import me.tud.skriptinterpreter.lexer.LexicalAnalyzer;

public class LiteralPatternNode extends PatternNode {

    private final String input;

    public LiteralPatternNode(String input) {
        this.input = input;
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
    public boolean matches(LexicalAnalyzer tokens, MatchResult.Metadata metadata) {
        return true;
    }

    @Override
    public String toString() {
        return "LiteralPatternNode[input=" + input + "]";
    }

}
