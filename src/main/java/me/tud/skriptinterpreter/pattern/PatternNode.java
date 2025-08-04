package me.tud.skriptinterpreter.pattern;

import me.tud.skriptinterpreter.lexer.LexicalAnalyzer;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;

public abstract class PatternNode {

    private final Map<Key, PatternNode> children = new HashMap<>();
    boolean terminal = false;
    PatternInfo patternInfo;

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
        return matches(new LexicalAnalyzer(input), metadata);
    }

    public abstract boolean matches(LexicalAnalyzer tokens, MatchResult.Metadata metadata);

    public record Key(Class<? extends PatternNode> type, String value) implements Comparable<Key> {

        @Override
        public int compareTo(@NotNull PatternNode.Key o) {
            int rank = Integer.compare(typeRank(), o.typeRank());
            if (rank != 0)
                return -rank;
            return value.compareTo(o.value);
        }

        private int typeRank() {
            if (type == LiteralPatternNode.class) {
                return 0;
            } else if (type == ExpressionPatternNode.class) {
                return 1;
            } else if (type == RegexPatternNode.class) {
                return 2;
            }
            return 3;
        }

        public static Key literal(String key) {
            return new Key(LiteralPatternNode.class, key);
        }

    }

}
