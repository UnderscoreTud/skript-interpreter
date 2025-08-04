package me.tud.skriptinterpreter.pattern;

import me.tud.skriptinterpreter.lexer.LexicalAnalyzer;

public class RootPatternNode extends PatternNode {

    @Override
    public String value() {
        return "";
    }

    @Override
    public Class<? extends PatternNode> type() {
        return RootPatternNode.class;
    }

    @Override
    public boolean matches(LexicalAnalyzer tokens, MatchResult.Metadata metadata) {
        return true;
    }

    @Override
    public String toString() {
        return "RootPatternNode[]";
    }

}
