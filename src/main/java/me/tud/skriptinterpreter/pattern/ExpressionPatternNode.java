package me.tud.skriptinterpreter.pattern;

import me.tud.skriptinterpreter.lang.Expression;
import me.tud.skriptinterpreter.lang.Expressions;
import me.tud.skriptinterpreter.lexer.TokenIterator;
import me.tud.skriptinterpreter.parser.ParseContext;
import me.tud.skriptinterpreter.parser.exceptions.ParseException;
import org.jetbrains.annotations.NotNull;

public class ExpressionPatternNode extends PatternNode {

    private final String value;
    private final String[] types;
    private final int index;
    private final PatternTrie trie;

    public ExpressionPatternNode(String[] types, int index, PatternTrie trie) {
        this.value = String.join("/", types);
        this.types = types;
        this.index = index;
        this.trie = trie;
    }

    @Override
    public String value() {
        return value;
    }

    @Override
    public Class<? extends PatternNode> type() {
        return ExpressionPatternNode.class;
    }

    @Override
    public boolean defersMatching() {
        return true;
    }

    public String[] types() {
        return types;
    }

    public int index() {
        return index;
    }

    @Override
    public boolean matches(TokenIterator tokens, MatchResult.Metadata metadata) {
        MatchResult result = trie.match(tokens);
        if (!result.success())
            return false;
        metadata.expressions().getAll()[index()] = new Expression<>() {
            @Override
            public boolean init(Expressions<Object> expressions, ParseContext context) throws ParseException {
                return true;
            }

            @Override
            public Object evaluate(Object context) {
                return result.pattern();
            }

            @Override
            public String toString() {
                return "EXPRESSION:" + evaluate(null).toString();
            }
        };
        return true;
    }

    @Override
    public String toString() {
        return "ExpressionPatternNode[types=" + String.join("/", types) + ", index=" + index + "]";
    }

    public static ExpressionPatternNode parse(String input, int index, @NotNull PatternTrie trie) {
        String[] types = input.split("/", -1);
        for (int i = 0; i < types.length; i++) {
            String type = types[i].trim();
            if (type.isEmpty())
                throw new IllegalArgumentException("Expression type cannot be empty: " + input);
            types[i] = type;
        }
        return new ExpressionPatternNode(types, index, trie);
    }

}
