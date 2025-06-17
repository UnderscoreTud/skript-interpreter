package me.tud.skriptinterpreter.pattern;

import me.tud.skriptinterpreter.lexer.LexicalAnalyzer;
import me.tud.skriptinterpreter.lexer.TokenIterator;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;

public abstract class PatternNode {

    private final Map<Key, PatternNode> children = new HashMap<>();
    boolean terminal = false;
    @Nullable String pattern;
    int expressionCount;
    int regexCount;

    public abstract String value();

    public abstract Class<? extends PatternNode> type();

    public boolean defersMatching() {
        return false;
    }

    public Key key() {
        return new Key(type(), value());
    }

    public Map<Key, PatternNode> children() {
        return children;
    }

    public boolean matches(String input, MatchResult.Metadata metadata) {
        return matches(new LexicalAnalyzer(input).iterator(), metadata);
    }

    public abstract boolean matches(TokenIterator tokens, MatchResult.Metadata metadata);

    public record Key(Class<? extends PatternNode> type, String value) {}

}
