package me.tud.skriptinterpreter.lang.lexer.source;

import me.tud.skriptinterpreter.lang.SourceSpan;
import me.tud.skriptinterpreter.lang.lexer.AbstractTokenizer;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.text.StringEscapeUtils;
import org.jetbrains.annotations.Nullable;

import java.util.*;

/**
 * Default implementation of the {@link SourceTokenizer} interface.
 */
public class SourceTokenizerImpl extends AbstractTokenizer<SourceToken> implements SourceTokenizer {

    private final Deque<SourceToken> pending = new ArrayDeque<>();
    private String indentation;
    private int indentationLevel;
    private int parenDepth;

    /**
     * Creates a new tokenizer for the given input.
     *
     * @param input the input to tokenize
     */
    public SourceTokenizerImpl(String input) {
        this("<unknown>", input);
    }

    /**
     * Creates a new tokenizer for the given input from the specified origin.
     *
     * @param origin the path to the origin
     * @param input  the input to tokenize
     */
    public SourceTokenizerImpl(String origin, String input) {
        super(origin, input);
    }

    protected SourceToken nextToken() {
        if (!pending.isEmpty())
            return pending.poll();

        if (!canRead()) {
            if (indentationLevel-- > 0)
                return new SourceToken(SourceTokenType.DEDENT, span());
            return new SourceToken(SourceTokenType.EOF, span());
        }

        SourceToken token = readNewLines();
        if (token != null && parenDepth == 0)
            return token;

        Position start = position.backup();

        if (position.column == 1 && parenDepth == 0 && handleIndentation()) {
            return nextToken();
        }

        readWhitespace();

        if (handleComment()) {
            if (start.column == 1)
                skip(); // don't emit NEWLINE for comment-only lineStart
            return nextToken();
        }

        start = position.backup();
        
        char c = peek();
        switch (c) {
            case ':' -> {
                return new SourceToken(SourceTokenType.COLON, String.valueOf(read()), span(start));
            }
            case '(' -> {
                parenDepth++;
                return new SourceToken(SourceTokenType.LPAREN, String.valueOf(read()), span(start));
            }
            case ')' -> {
                parenDepth = Math.max(0, parenDepth - 1);
                return new SourceToken(SourceTokenType.RPAREN, String.valueOf(read()), span(start));
            }
            case ',' -> {
                return new SourceToken(SourceTokenType.COMMA, String.valueOf(read()), span(start));
            }
            case '"' -> {
                return new SourceToken(SourceTokenType.STRING, readString(), span(start));
            }
            case '{' -> {
                return new SourceToken(SourceTokenType.VARIABLE, readVariable(), span(start));
            }
        }

        if (isDigit(c) || c == '-' || c == '.')
            return readNumber();

        while (canRead() && isWordPart(peek()))
            skip();

        if (start.index != position.index)
            return new SourceToken(SourceTokenType.WORD, input.substring(start.index, position.index), span(start));

        return new SourceToken(SourceTokenType.SYMBOL, String.valueOf(read()), span(start));
    }

    private String readString() {
        return readQuoted("string", '"', '"');
    }

    private String readVariable() {
        return readQuoted("variable", '{', '}');
    }

    private String readQuoted(String type, char opening, char closing) throws SourceTokenizationException {
        Position start = position.backup();
        expectOrThrow(opening, span(start));
        StringBuilder builder = new StringBuilder();
        boolean inExpression = false;
        Position expressionStart = null;
        while (canRead()) {
            char c = peek();

            if (c == NEW_LINE)
                break;

            if ((c == '"' || c == '{') && inExpression) {
                builder.append(c == '"' ? readString() : readVariable());
                if (!canRead())
                    break;
                continue;
            }

            if (c == closing) {
                if (opening == closing && canRead(1) && peek(1) == closing) {
                    builder.append(read()); // read first quote
                    builder.append(read()); // read second quote
                    continue;
                }
                skip(); // skip closing quote
                return opening + builder.toString() + closing;
            }

            if (c == '%') {
                if (canRead(1) && peek(1) == '%') {
                    builder.append(read()); // read first %
                    builder.append(read()); // read second %
                    continue;
                }
                inExpression = !inExpression;
                if (inExpression)
                    expressionStart = position.backup();
            }
            builder.append(read());
        }
        if (inExpression)
            throw createException("Unterminated expression: If you meant to write a single (%), then double it to escape it (%%)", span(expressionStart));
        String message = "Unterminated " + type + ": ";
        if (canRead()) {
            message += "Expected closing '" + closing + "', got '" + StringEscapeUtils.escapeJava(String.valueOf(peek())) + "'";
        } else {
            message += "Reached end of line before closing '" + closing + "'";
        }
        throw createException(message, span(start));
    }

    private SourceToken readNumber() {
        Position start = position.backup();
        if (peek() == '-')
            skip();
        boolean hasDecimal = false;
        while (canRead()) {
            char c = peek();
            if (c == '.') {
                if (hasDecimal)
                    break;
                hasDecimal = true;
                skip();
                continue;
            }
            if (!isDigit(c))
                break;
            skip();
        }
        return new SourceToken(SourceTokenType.NUMBER, input.substring(start.index, position.index), span(start));
    }

    private boolean isDigit(char c) {
        return '0' <= c && c <= '9';
    }

    private boolean handleComment() {
        if (peek() != '#')
            return false;
        if (canRead(2) && peek(1) == '#' && peek(2) == '#') {
            int delimiter = input.indexOf("###", position.index + 3);
            position.index = delimiter == -1 ? input.length() - 1 : delimiter + 3;
            return true;
        }
        do {
            skip();
        } while (canRead() && (peek() != '\r' && peek() != '\n'));
        return true;
    }

    private @Nullable SourceToken readNewLines() {
        Position start = position.backup();
        while (canRead() && isNewLine(peek()))
            skip();

        if (start.index != position.index)
            return new SourceToken(SourceTokenType.NEWLINE, span(start));
        return null;
    }

    private boolean isNewLine(char c) {
        return c == NEW_LINE;
    }

    private boolean handleIndentation() throws SourceTokenizationException {
        Position start = position.backup();
        String indentation = readWhitespace();

        if (!canRead() || peek() == '#')
            return false;

        if (this.indentation == null) {
            if (indentation == null)
                return false;
            this.indentation = indentation;
            indentationLevel = 1;
            pending.add(new SourceToken(SourceTokenType.INDENT, indentation, span(start)));
            return true;
        }

        int level = 0;
        if (indentation != null) {
            level = StringUtils.countMatches(indentation, this.indentation);
            if (this.indentation.length() * level != indentation.length()) {
                throw createException("Indentation must be made of repeated \""
                        + StringEscapeUtils.escapeJava(this.indentation)
                        + "\" but got \"" + StringEscapeUtils.escapeJava(indentation) + "\"", span(start));
            }
        }
        int diff = level - indentationLevel;
        if (diff == 0)
            return false;
        if (diff > 1) {
            throw createException("Unexpected indentation: jumped from level "
                    + indentationLevel + " to " + level, span(start));
        }

        indentationLevel = level;
        if (diff == 1) {
            pending.add(new SourceToken(SourceTokenType.INDENT, indentation, span(start)));
            return true;
        }
        for (int i = 0; i < -diff; i++)
            pending.add(new SourceToken(SourceTokenType.DEDENT, indentation == null ? "" : indentation, span(start)));
        return true;
    }

    private @Nullable String readWhitespace() {
        int start = position.index;
        while (canRead() && isWhitespace(peek()))
            skip();
        return start != position.index ? input.substring(start, position.index) : null;
    }

    private boolean isWhitespace(char c) {
        return Character.isWhitespace(c) && !isNewLine(c);
    }

    private static boolean isWordPart(char c) {
        return ('a' <= c && c <= 'z') || ('A' <= c && c <= 'Z');
    }

    @Override
    protected SourceTokenizationException createException(String message, SourceSpan span) {
        return new SourceTokenizationException(message, span, input);
    }

}
