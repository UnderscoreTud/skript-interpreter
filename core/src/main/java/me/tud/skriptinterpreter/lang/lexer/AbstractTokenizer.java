package me.tud.skriptinterpreter.lang.lexer;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import me.tud.skriptinterpreter.lang.SourceSpan;
import org.apache.commons.text.StringEscapeUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * An abstract implementation of a tokenizer that provides common functionality.
 *
 * @param <T> the type of tokens produced by this tokenizer
 */
public abstract class AbstractTokenizer<T extends Token<?>> implements Tokenizer<T> {

    protected static final char NEW_LINE = '\n';

    protected final String origin;
    protected final String input;
    protected final Position position;

    /**
     * @param origin the origin of the source code (e.g., file name)
     * @param input  the source code to tokenize
     */
    public AbstractTokenizer(String origin, String input) {
        this.origin = origin;
        this.input = input;
        this.position = new Position();
    }

    @Override
    public List<T> tokenize() throws TokenizationException {
        List<T> tokens = new ArrayList<>();
        T token;
        do {
            token = nextToken();
            tokens.add(token);
        } while (!token.type().isTerminal());
        return tokens;
    }

    /**
     * @return the next token in the input
     */
    protected abstract T nextToken();

    /**
     * Creates a tokenization exception.
     *
     * @param message the error message
     * @param span    the span where the error occurred
     * @return the tokenization exception
     */
    protected abstract TokenizationException createException(String message, SourceSpan span);

    /**
     * @return a span starting at the current position and ending at the current position
     */
    protected SourceSpan span() {
        return span(position);
    }

    /**
     * @param start the start position of the span
     * @return a span starting at the given position and ending at the current position
     */
    protected SourceSpan span(Position start) {
        return new SourceSpan(
                origin,
                start.index,
                position.index,
                start.line,
                position.line,
                start.column,
                position.column
        );
    }

    /**
     * @return {@code true} if there are more characters to read, {@code false} otherwise
     */
    protected boolean canRead() {
        return canRead(0);
    }

    /**
     * @param offset the offset from the current position
     * @return {@code true} if there are more characters to read at the given offset, {@code false} otherwise
     */
    protected boolean canRead(int offset) {
        return position.index + offset < input.length();
    }

    /**
     * @return the character at the current position without advancing
     */
    protected char peek() {
        return peek(0);
    }

    /**
     * @param offset the offset from the current position
     * @return the character at the given offset without advancing
     */
    protected char peek(int offset) {
        char c = input.charAt(position.index + offset);
        if (c == '\r' || c == '\n')
            return NEW_LINE;
        return c;
    }

    /**
     * Reads the next character and advances the position.
     *
     * @return the character read
     */
    protected char read() {
        char c = input.charAt(position.index++);
        if (c == '\r') {
            if (canRead() && input.charAt(position.index) == '\n')
                position.index++;
            position.line += 1;
            position.column = 1;
            return NEW_LINE;
        } else if (c == '\n') {
            position.line += 1;
            position.column = 1;
            return NEW_LINE;
        }
        position.column++;
        return c;
    }

    /**
     * Advances the position by one character.
     */
    protected void skip() {
        read();
    }

    /**
     * Expects a specific character at the current position, advances if it matches, or throws an exception if it doesn't.
     *
     * @param c    the expected character
     * @param span the span where the character is expected
     * @throws TokenizationException if the character doesn't match or if the end of input is reached
     */
    protected void expectOrThrow(char c, SourceSpan span) {
        if (!canRead())
            throw createException("Expected '" + c + "', but got EOF", span);
        char got = read();
        if (c != got)
            throw createException("Expected '" + c
                    + "', but got '" + StringEscapeUtils.escapeJava(String.valueOf(got)) + "'", span);
    }

    /**
     * Represents a position in the source code.
     */
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    protected static class Position {

        public int index, line = 1, column = 1;

        /**
         * @return a backup of this position
         */
        public Position backup() {
            return new Position(index, line, column);
        }

        /**
         * Applies the values of another position to this one.
         *
         * @param position the position to apply
         */
        public void apply(Position position) {
            this.index = position.index;
            this.line = position.line;
            this.column = position.column;
        }

    }

}
