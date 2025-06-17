package me.tud.skriptinterpreter.pattern;

import me.tud.skriptinterpreter.lexer.LexicalAnalyzer;
import me.tud.skriptinterpreter.lexer.TokenIterator;
import me.tud.skriptinterpreter.pattern.peg.PegPatternParser;
import me.tud.skriptinterpreter.pattern.peg.node.PegNode;
import me.tud.skriptinterpreter.util.StringReader;
import org.jetbrains.annotations.Nullable;

public class PatternTrie {

    private final RootPatternNode root = new RootPatternNode();

    public RootPatternNode root() {
        return root;
    }

    public void insert(String pattern) {
        insert(pattern, null);
    }

    public void insert(String pattern, @Nullable PatternTrie expressionTrie) {
        PegPatternParser parser = new PegPatternParser(pattern);
        PegNode parsed = parser.parse();
        PatternInfo patternInfo = new PatternInfo(pattern, parser.expressionCount(), parser.regexCount());
        for (String expandedPattern : parsed.expand())
            insertPattern(root, new StringReader(expandedPattern.trim()), patternInfo, expressionTrie);
    }

    public MatchResult match(String input) {
        return match(new LexicalAnalyzer(input).iterator());
    }

    public MatchResult match(TokenIterator tokens) {
        return match(root, tokens, new MatchResult.Metadata());
    }

    private void insertPattern(PatternNode node, StringReader reader, PatternInfo patternInfo, @Nullable PatternTrie expressionTrie) {
        if (!reader.canRead()) {
            if (node.terminal && !patternInfo.equals(node.patternInfo))
                throw new IllegalStateException("Pattern '" + patternInfo.pattern() + "' conflicts with existing pattern '" + node.patternInfo.pattern() + "'");
            node.terminal = true;
            node.patternInfo = patternInfo;
            return;
        }

        PatternNode next = parseNextNode(reader, expressionTrie);
        insertPattern(node.children().computeIfAbsent(next.key(), k -> next), reader, patternInfo, expressionTrie);
    }

    private MatchResult match(PatternNode node, TokenIterator tokens, MatchResult.Metadata metadata) {
        if (node == root)
            return matchChildren(node, tokens, metadata);

        if (node.defersMatching() && !node.children().isEmpty() && tokens.tokensLeft() > 1) {
            int start = tokens.position();
            do {
                tokens.next();
                MatchResult result = matchChildren(node, tokens, metadata);
                if (!result.success())
                    continue;
                TokenIterator subTokens = tokens.subIterator(start, tokens.position() - 1);
                if (node.matches(subTokens, metadata))
                    return result;
            } while (tokens.hasNext());
            return MatchResult.fail(tokens.input());
        }

        if (!node.matches(tokens, metadata))
            return MatchResult.fail(tokens.input());

        if (node.terminal) {
            metadata.allocateExpressions(node.patternInfo.expressionCount());
            metadata.allocateRegexes(node.patternInfo.regexCount());
            return MatchResult.success(node.patternInfo.pattern(), tokens.input(), metadata);
        }

        return matchChildren(node, tokens, metadata);
    }

    private MatchResult matchChildren(PatternNode node, TokenIterator tokens, MatchResult.Metadata metadata) {
        if (!tokens.hasNext())
            return MatchResult.fail(tokens.input());

        for (PatternNode child : node.children().values()) {
            int start = tokens.position();
            MatchResult result = match(child, tokens, metadata);
            if (result.success())
                return result;
            tokens.position(start);
        }
        return MatchResult.fail(tokens.input());
    }

    public void print() {
        print(root, 0);
    }

    private static void print(PatternNode node, int indent) {
        StringBuilder sb = new StringBuilder("  ".repeat(Math.max(0, indent)));
        if (indent > 0)
            sb.append("└─");
        sb.append(node.toString());
        if (node.terminal)
            sb.append(" (terminal)");
        System.out.println(sb);

        node.children().values().forEach(child -> print(child, indent + 1));
    }

    private static PatternNode parseNextNode(StringReader reader, @Nullable PatternTrie expressionTrie) {
        char current = reader.peek();
        if (Character.isWhitespace(current)) {
            reader.readUntil(c -> !Character.isWhitespace(c));
            return new WhitespacePatternNode();
        }
        switch (current) {
            case '%' -> {
                String expression = reader.readEnclosed('%', '%', '\\');
                if (expression == null)
                    throw new MalformedPatternException("Unmatched percent signs in pattern: " + reader.input());
                if (expressionTrie == null)
                    throw new IllegalArgumentException("Expressions requires an expression pattern trie");
                String[] parts = expression.split(":", 2);
                return ExpressionPatternNode.parse(parts[0], Integer.parseInt(parts[1]), expressionTrie);
            }
            case '<' -> {
                String regex = reader.readEnclosed('<', '>', '\\');
                if (regex == null)
                    throw new MalformedPatternException("Unmatched angle brackets in pattern: " + reader.input());
                String[] parts = regex.split(":", 2);
                return RegexPatternNode.parse(parts[0], Integer.parseInt(parts[1]));
            }
        }
        String literal = reader.readUntil(c -> c == '%' || c == '<' || Character.isWhitespace(c));
        if (!literal.isEmpty())
            return new LiteralPatternNode(literal);
        throw new MalformedPatternException("Unexpected character in pattern: " + current + " in " + reader.input());
    }

}
