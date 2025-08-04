package me.tud.skriptinterpreter.pattern;

import me.tud.skriptinterpreter.lexer.LexicalAnalyzer;
import me.tud.skriptinterpreter.lexer.Token;
import me.tud.skriptinterpreter.lexer.TokenIterator;
import me.tud.skriptinterpreter.lexer.TokenType;
import me.tud.skriptinterpreter.pattern.peg.PegPatternParser;
import me.tud.skriptinterpreter.pattern.peg.node.PegNode;
import me.tud.skriptinterpreter.util.StringReader;
import org.jetbrains.annotations.Nullable;

public class PatternTrie { // TODO FIX PATTERN MATCHING (probably needs to be rewritten (maybe use a different approach?)) /shrug

    private final RootPatternNode root = new RootPatternNode();

    public RootPatternNode root() {
        return root;
    }

    public void registerPattern(String pattern) {
        registerPattern(pattern, null);
    }

    public void registerPattern(String pattern, @Nullable PatternTrie expressionTrie) {
        PegPatternParser parser = new PegPatternParser(pattern);
        PegNode parsed = parser.parse();
        PatternInfo patternInfo = new PatternInfo(pattern, parser.expressionCount(), parser.regexCount());
        for (String expandedPattern : parsed.expand())
            insertPattern(root, new StringReader(expandedPattern.trim()), patternInfo, expressionTrie);
    }


    public MatchResult match(String input) {
        return match(new LexicalAnalyzer(input));
    }

    public MatchResult match(LexicalAnalyzer tokens) {
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

    private MatchResult match(PatternNode node, LexicalAnalyzer tokens, MatchResult.Metadata metadata) {
        if (node == root)
            return matchChildren(node, tokens, metadata);

        if (node.defersMatching() && !node.children().isEmpty())
            return deferMatch(node, tokens, metadata);

        if (!node.matches(tokens, metadata))
            return MatchResult.fail(tokens.input());

        if (tokens.hasNext() || !node.terminal)
            return matchChildren(node, tokens, metadata);

        metadata.allocateExpressions(node.patternInfo.expressionCount());
        metadata.allocateRegexes(node.patternInfo.regexCount());
        metadata.ready();
        return MatchResult.success(node.patternInfo.pattern(), tokens.input(), metadata);
    }

    private MatchResult deferMatch(PatternNode node, LexicalAnalyzer tokens, MatchResult.Metadata metadata) {
        int start = tokens.cursor();
        int remaining = tokens.input().length() - tokens.cursor();
        for (int offset = 1; offset < remaining; offset++) {
            tokens.cursor(start + offset);
            MatchResult result = matchChildren(node, tokens, metadata);
            if (!result.success())
                continue;
            LexicalAnalyzer expressionTokens = new LexicalAnalyzer(tokens.input().substring(start, start + offset));
            if (node.matches(expressionTokens, metadata))
                return result;
        }
        tokens.cursor(start);
        return MatchResult.fail(tokens.input());
    }

    private boolean skipToClosingParenthesis(LexicalAnalyzer tokens) {
        int start = tokens.cursor();
        int depth = 0;
        while (tokens.hasNext()) {
            Token token = tokens.next();
            if (token.type() != TokenType.PUNCTUATION)
                continue;
            if (token.value().equals("(")) {
                depth++;
            } else if (token.value().equals(")")) {
                depth--;
                if (depth == 0)
                    return true;
            }
        }
        tokens.cursor(start);
        return false;
    }

    private MatchResult matchChildren(PatternNode node, LexicalAnalyzer tokens, MatchResult.Metadata metadata) {
        if (!tokens.hasNext())
            return MatchResult.fail(tokens.input());

        int start = tokens.cursor();
        MatchResult result = matchLiteral(node, tokens, metadata);
        if (result.success())
            return result;
        tokens.cursor(start);

        for (PatternNode child : node.children().values()) {
            if (child.type() == LiteralPatternNode.class)
                continue;

            start = tokens.cursor();
            result = match(child, tokens.clone(), metadata);
            if (result.success())
                return result;
            tokens.cursor(start);
        }

        return MatchResult.fail(tokens.input());
    }

    private MatchResult matchLiteral(PatternNode node, LexicalAnalyzer tokens, MatchResult.Metadata metadata) {
        if (!tokens.hasNext())
            return MatchResult.fail(tokens.input());

        int start = tokens.cursor();
        Token token = tokens.next();
        int length = token.value().length();
        for (int offset = length; offset > 0; offset--) {
            PatternNode child = node.children().get(PatternNode.Key.literal(token.value().substring(0, offset)));
            if (child != null) {
                tokens.cursor(start + offset);
                return match(child, tokens, metadata);
            }
        }
        tokens.cursor(start);
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
            return new LiteralPatternNode(" ");
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
        LexicalAnalyzer lexer = new LexicalAnalyzer(reader.input().substring(reader.cursor()));
        Token token = lexer.next();
        if (!token.value().isEmpty()) {
            reader.cursor(reader.cursor() + token.value().length());
            return new LiteralPatternNode(token.value());
        }
        throw new MalformedPatternException("Unexpected character in pattern: " + current + " in " + reader.input());
    }

}
