package me.tud.skriptinterpreter.pattern;

import me.tud.skriptinterpreter.lexer.TokenIterator;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RegexPatternNode extends PatternNode {

    private final Pattern pattern;
    private final int index;

    public RegexPatternNode(Pattern pattern, int index) {
        this.pattern = pattern;
        this.index = index;
    }

    @Override
    public String value() {
        return pattern().pattern();
    }

    @Override
    public Class<? extends PatternNode> type() {
        return RegexPatternNode.class;
    }

    @Override
    public boolean defersMatching() {
        return true;
    }

    public Pattern pattern() {
        return pattern;
    }

    @Override
    public boolean matches(TokenIterator tokens, MatchResult.Metadata metadata) {
        StringBuilder input = new StringBuilder();
        while (tokens.hasNext())
            input.append(tokens.next().value());

        Matcher matcher = pattern.matcher(input.toString().trim());
        if (!matcher.matches())
            return false;
        metadata.regexes()[index] = matcher.toMatchResult();
        return true;
    }

    @Override
    public String toString() {
        return "RegexPatternNode[pattern=" + pattern() + "]";
    }

    public static RegexPatternNode parse(String input, int index) {
        Pattern pattern = Pattern.compile(input);
        return new RegexPatternNode(pattern, index);
    }

}
