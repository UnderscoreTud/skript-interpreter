package me.tud.skriptinterpreter.lang.lexer.source;

import lombok.Getter;
import lombok.experimental.Delegate;
import me.tud.skriptinterpreter.lang.SourceSpan;
import me.tud.skriptinterpreter.lang.lexer.AbstractTokenizer;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.text.StringEscapeUtils;
import org.jetbrains.annotations.Nullable;

import java.util.*;

/**
 * Default implementation of the {@link SourceTokenizer} interface.
 */
public class SourceTokenizerImpl extends AbstractTokenizer<SourceToken> implements SourceTokenizer {

    private final Deque<SourceToken> pending = new ArrayDeque<>();
    private final Deque<StateContext> states = new ArrayDeque<>();
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

    @Override
    protected SourceToken nextToken() {
        if (!pending.isEmpty())
            return pending.poll();

        if (!states.isEmpty()) {
            StateContext state = states.element();
            if (!canRead())
                throw createException(state.unterminated(null), span(state.start()));

            if (state.state() == State.IN_INTERPOLATION && readWhitespace() != null)
                return nextToken();

            char c = peek();
            if (c == NEW_LINE)
                throw createException(state.unterminated(c), span(state.start()));

            if (c == state.closing()) {
                Position start = position.backup();
                states.pop();
                return new SourceToken(state.closingTokenType(), String.valueOf(read()), span(start));
            }

            if (state.state() == State.IN_STRING || state.state() == State.IN_VARIABLE) {
                return nextLiteralPart();
            } else {
                return nextSourceToken();
            }
        }

        if (!canRead()) {
            if (indentationLevel-- > 0)
                return new SourceToken(SourceTokenType.DEDENT, span());
            return new SourceToken(SourceTokenType.EOF, span());
        }

        SourceToken token = readNewLines();
        if (token != null && parenDepth == 0)
            return token;

        Position start = position.backup();

        if (position.column == 1 && parenDepth == 0 && handleIndentation())
            return nextToken();

        if (readWhitespace() != null)
            return nextToken();

        if (handleComment()) {
            if (start.column == 1)
                skip(); // don't emit NEWLINE for comment-only lineStart
            return nextToken();
        }

        return nextSourceToken();
    }

    /**
     * Reads the next source token.
     *
     * @return the next source token
     */
    private SourceToken nextSourceToken() {
        Position start = position.backup();
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
                states.push(State.IN_STRING.at(start));
                return new SourceToken(SourceTokenType.STRING_START, String.valueOf(read()), span(start));
            }
            case '{' -> {
                states.push(State.IN_VARIABLE.at(start));
                return new SourceToken(SourceTokenType.VARIABLE_START, String.valueOf(read()), span(start));
            }
        }

        if (isDigit(c) || c == '-' || c == '.') {
            SourceToken token = readNumber();
            if (token != null)
                return token;
            position.apply(start);
        }

        while (canRead() && isWordPart(peek()))
            skip();

        if (start.index != position.index)
            return new SourceToken(SourceTokenType.WORD, input.substring(start.index, position.index), span(start));

        return new SourceToken(SourceTokenType.SYMBOL, String.valueOf(read()), span(start));
    }

    /**
     * Reads the next part of a literal (string or variable reference).
     *
     * @return the next literal part token
     */
    private SourceToken nextLiteralPart() {
        Position start = position.backup();
        StateContext state = states.element();
        StringBuilder builder = new StringBuilder();
        while (canRead()) {
            char c = peek();
            if (c == NEW_LINE)
                break;

            if (state.isEscapable(c) && canRead(1) && peek(1) == c) {
                builder.append(read());
                skip();
                continue;
            }

            if (c == state.closing() || c == '%') {
                SourceSpan contentSpan = span(start);
                if (c == '%') {
                    Position exprStart = position.backup();
                    states.push(State.IN_INTERPOLATION.at(exprStart));
                    pending.add(new SourceToken(SourceTokenType.INTERPOLATION_START, String.valueOf(read()), span(exprStart)));
                }
                if (builder.isEmpty())
                    return nextToken();
                return new SourceToken(SourceTokenType.LITERAL_TEXT, builder.toString(), contentSpan);
            }

            builder.append(read());
        }
        throw createException(state.unterminated(canRead() ? peek() : null), span(state.start()));
    }

    /**
     * Reads a number token if possible.
     *
     * @return the number token, or {@code null} if no number could be read
     */
    private @Nullable SourceToken readNumber() {
        Position start = position.backup();
        boolean hasDigit = false;
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
            hasDigit = true;
            skip();
        }
        if (!hasDigit)
            return null;
        return new SourceToken(SourceTokenType.NUMBER, input.substring(start.index, position.index), span(start));
    }

    /**
     * Skips over comments if present.
     *
     * @return {@code true} if a comment was handled, {@code false} otherwise
     */
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

    /**
     * Reads consecutive newline characters.
     *
     * @return the newline token, or {@code null} if no newlines were found
     */
    private @Nullable SourceToken readNewLines() {
        Position start = position.backup();
        while (canRead() && peek() == NEW_LINE)
            skip();

        if (start.index != position.index)
            return new SourceToken(SourceTokenType.NEWLINE, span(start));
        return null;
    }

    /**
     * Handles indentation and dedentation.
     *
     * @return {@code true} if indentation was handled, {@code false} otherwise
     * @throws SourceTokenizationException if the indentation is invalid
     */
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

    /**
     * Reads horizontal whitespace (spaces and tabs).
     *
     * @return the whitespace string, or {@code null} if no whitespace was found
     */
    private @Nullable String readWhitespace() {
        int start = position.index;
        while (canRead() && isWhitespace(peek()))
            skip();
        return start != position.index ? input.substring(start, position.index) : null;
    }

    private static boolean isDigit(char c) {
        return '0' <= c && c <= '9';
    }

    private static boolean isWhitespace(char c) {
        return Character.isWhitespace(c) && c != NEW_LINE;
    }

    private static boolean isWordPart(char c) {
        return ('a' <= c && c <= 'z') || ('A' <= c && c <= 'Z');
    }

    @Override
    protected SourceTokenizationException createException(String message, SourceSpan span) {
        return new SourceTokenizationException(message, span, input);
    }

    @Getter
    private enum State {
        IN_STRING("string", '"', SourceTokenType.STRING_START, SourceTokenType.STRING_END, '"', '%'),
        IN_VARIABLE("variable reference", '{', '}', SourceTokenType.VARIABLE_START, SourceTokenType.VARIABLE_END, '%'),
        IN_INTERPOLATION("expression", '%', SourceTokenType.INTERPOLATION_START, SourceTokenType.INTERPOLATION_END) {
            @Override
            public String unterminated(@Nullable Character next) {
                return super.unterminated(next) + ". If you meant to write a single (%), then double it to escape it (%%)";
            }
        };

        private final String type;
        private final char opening, closing;
        private final SourceTokenType openingTokenType, closingTokenType;
        private final char[] escapable;

        State(String type, char delimiter, SourceTokenType openingTokenType, SourceTokenType closingTokenType, char... escapable) {
            this(type, delimiter, delimiter, openingTokenType, closingTokenType, escapable);
        }

        State(String type, char opening, char closing, SourceTokenType openingTokenType, SourceTokenType closingTokenType, char... escapable) {
            this.type = type;
            this.opening = opening;
            this.closing = closing;
            this.openingTokenType = openingTokenType;
            this.closingTokenType = closingTokenType;
            this.escapable = escapable;
        }

        public boolean isEscapable(char c) {
            return ArrayUtils.contains(escapable, c);
        }

        public StateContext at(Position position) {
            return new StateContext(this, position);
        }

        public String unterminated(@Nullable Character next) {
            String message = "Unterminated " + type + ": ";
            if (next == null) {
                message += "Reached end of file before closing '" + closing + "'";
            } else if (next == NEW_LINE) {
                message += "Reached end of line before closing '" + closing + "'";
            } else {
                message += "Expected closing '" + closing + "', got '" + StringEscapeUtils.escapeJava(String.valueOf(next)) + "'";
            }
            return message;
        }
    }

    private record StateContext(@Delegate State state, Position start) {}

}
