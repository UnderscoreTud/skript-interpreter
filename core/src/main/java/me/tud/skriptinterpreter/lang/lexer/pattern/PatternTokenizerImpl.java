package me.tud.skriptinterpreter.lang.lexer.pattern;

import me.tud.skriptinterpreter.lang.SourceSpan;
import me.tud.skriptinterpreter.lang.lexer.AbstractTokenizer;
import org.apache.commons.text.StringEscapeUtils;
import org.jetbrains.annotations.Nullable;

/**
 * An implementation of {@link PatternTokenizer}.
 */
public class PatternTokenizerImpl extends AbstractTokenizer<PatternToken> implements PatternTokenizer {

    /**
     * @param input the pattern input to tokenize
     */
    public PatternTokenizerImpl(String input) {
        this("<unknown>", input);
    }

    /**
     * @param origin the origin of the pattern
     * @param input  the pattern input to tokenize
     */
    public PatternTokenizerImpl(String origin, String input) {
        super(origin, input);
    }

    @Override
    protected PatternToken nextToken() {
        if (!canRead())
            return new PatternToken(PatternTokenType.EOF, span());

        Position start = position.backup();

        while (canRead() && peek() == ' ')
            skip();

        if (start.index != position.index)
            return new PatternToken(PatternTokenType.WHITESPACE, input.substring(start.index, position.index), span(start));

        if (Character.isWhitespace(peek()))
            throw createException("Encountered an unexpected whitespace character '"
                    + StringEscapeUtils.escapeJava(String.valueOf(peek())) + "'", span(start));

        return switch (peek()) {
            case '(' -> new PatternToken(PatternTokenType.GROUP, readGroup('(', ')'), span(start));
            case '[' -> new PatternToken(PatternTokenType.OPTIONAL_GROUP, readGroup('[', ']'), span(start));
            case '<' -> readPlaceholder();
            case '|' -> new PatternToken(PatternTokenType.PIPE, String.valueOf(read()), span(start));
            case ':' -> new PatternToken(PatternTokenType.DYNAMIC_TAG, String.valueOf(read()), span(start));
            default -> {
                String literal = readLiteral();
                if (literal == null)
                    throw createException("Encountered an unexpected character '"
                            + StringEscapeUtils.escapeJava(String.valueOf(peek())) + "'", span(start));
                if (canRead() && peek() == ':')
                    yield new PatternToken(PatternTokenType.LITERAL_TAG, literal + read(), span(start));
                yield new PatternToken(PatternTokenType.LITERAL, literal, span(start));
            }
        };
    }

    private String readGroup(char opening, char closing) {
        Position start = position.backup();
        expectOrThrow(opening, span(start));
        while (canRead()) {
            if (peek() == closing) {
                skip();
                return input.substring(start.index, position.index);
            }

            nextToken();
        }
        throw createException("Unterminated group: Reached end of line before closing '" + closing + "'", span(start));
    }

    private PatternToken readPlaceholder() {
        Position start = position.backup();
        expectOrThrow('<', span(start));
        readLiteral();
        if (canRead() && peek() == ':') {
            skip();
            readLiteral();
        }
        expectOrThrow('>', span(start));
        return new PatternToken(PatternTokenType.PLACEHOLDER, input.substring(start.index, position.index), span(start));
    }

    private @Nullable String readLiteral() {
        Position start = position.backup();
        boolean escaped = false;
        while (canRead()) {
            char c = peek();

            if (escaped) {
                if (!canBeEscaped(c))
                    throw createException("Cannot escape '" + c
                            + "'. If you meant to escape a backslash (\\), then double it (\\\\)", span(start));
                escaped = false;
                skip();
                continue;
            }

            if (c == '\\') {
                escaped = true;
                skip();
                continue;
            }

            if (isSpecialCharacter(c) || Character.isWhitespace(c))
                break;

            skip();
        }
        if (start.index == position.index)
            return null;
        return input.substring(start.index, position.index);
    }

    private boolean canBeEscaped(char c) {
        return isSpecialCharacter(c) || c == '\\';
    }

    private boolean isSpecialCharacter(char c) {
        return switch (c) {
            case '(', ')', '[', ']', '<', '>', '|', ':' -> true;
            default -> false;
        };
    }

    @Override
    protected PatternTokenizationException createException(String message, SourceSpan span) {
        return new PatternTokenizationException(message, span, input);
    }

}
