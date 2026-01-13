package me.tud.skriptinterpreter.lang.lexer;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import me.tud.skriptinterpreter.lang.SourceSpan;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.text.StringEscapeUtils;
import org.jetbrains.annotations.Nullable;

import java.util.*;

/**
 * Default implementation of the {@link Tokenizer} interface.
 */
public class TokenizerImpl implements Tokenizer {
    
    private static final char NEW_LINE = '\n';

    private final String file;
    private final String input;
    private final Deque<Token> pending = new ArrayDeque<>();
    private final Position position;
    private String indentation;
    private int indentationLevel;
    private int parenDepth;

    /**
     * Creates a new tokenizer for the given input.
     *
     * @param input the input to tokenize
     */
    public TokenizerImpl(String input) {
        this("<unknown>", input);
    }

    /**
     * Creates a new tokenizer for the given input from the specified file.
     *
     * @param file  the path to the file
     * @param input the input to tokenize
     */
    public TokenizerImpl(String file, String input) {
        this.file = file;
        this.input = input;
        this.position = new Position();
    }

    @Override
    public List<Token> tokenize() throws TokenizationException {
        List<Token> tokens = new ArrayList<>();
        Token token;
        do {
            token = nextToken();
            tokens.add(token);
        } while (token.type() != TokenType.EOF);
        return tokens;
    }

    private Token nextToken() {
        if (!pending.isEmpty())
            return pending.poll();

        if (!canRead()) {
            if (indentationLevel-- > 0)
                return new Token(TokenType.DEDENT, span());
            return new Token(TokenType.EOF, span());
        }

        Token token = readNewLines();
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
                return new Token(TokenType.COLON, String.valueOf(read()), span(start));
            }
            case '(' -> {
                parenDepth++;
                return new Token(TokenType.LPAREN, String.valueOf(read()), span(start));
            }
            case ')' -> {
                parenDepth = Math.max(0, parenDepth - 1);
                return new Token(TokenType.RPAREN, String.valueOf(read()), span(start));
            }
            case ',' -> {
                return new Token(TokenType.COMMA, String.valueOf(read()), span(start));
            }
            case '"' -> {
                return new Token(TokenType.STRING, readString(), span(start));
            }
            case '{' -> {
                return new Token(TokenType.VARIABLE, readVariable(), span(start));
            }
        }

        if (isDigit(c) || c == '-' || c == '.')
            return readNumber();

        while (canRead() && isWordPart(peek()))
            skip();

        if (start.index != position.index)
            return new Token(TokenType.WORD, input.substring(start.index, position.index), span(start));

        return new Token(TokenType.SYMBOL, String.valueOf(read()), span(start));
    }

    private String readString() {
        return readQuoted("string", '"', '"');
    }

    private String readVariable() {
        return readQuoted("variable", '{', '}');
    }

    private String readQuoted(String type, char opening, char closing) throws TokenizationException {
        Position start = position.backup();
        expectOrThrow(opening, span(start));
        StringBuilder builder = new StringBuilder();
        boolean escaped = false, inExpression = false;
        Position expressionStart = null;
        while (canRead()) {
            char c = peek();

            if (c == NEW_LINE)
                break;

            if (escaped) {
                escaped = false;
                builder.append(read());
                continue;
            }

            if ((c == '"' || c == '{') && inExpression) {
                builder.append(c == '"' ? readString() : readVariable());
                if (!canRead())
                    break;
                continue;
            }

            if (c == closing) {
                if (opening == closing && canRead(1) && peek(1) == closing) {
                    skip();
                    escaped = true;
                    continue;
                }
                skip(); // skip closing quote
                return opening + builder.toString() + closing;
            }

            if (c == '%') {
                if (canRead(1) && peek(1) == '%') {
                    skip();
                    escaped = true;
                    continue;
                }
                inExpression = !inExpression;
                if (inExpression)
                    expressionStart = position.backup();
            }
            builder.append(read());
        }
        if (inExpression)
            throw new TokenizationException("Unterminated expression: If you meant to write a single (%), then double it to escape it (%%)", span(expressionStart), input);
        String message = "Unterminated " + type + ": ";
        if (canRead()) {
            message += "Expected closing '" + closing + "', got '" + StringEscapeUtils.escapeJava(String.valueOf(peek())) + "'";
        } else {
            message += "Reached end of line before closing '" + closing + "'";
        }
        throw new TokenizationException(message, span(start), input);
    }
    
    private Token readNumber() {
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
        return new Token(TokenType.NUMBER, input.substring(start.index, position.index), span(start));
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

    private @Nullable Token readNewLines() {
        Position start = position.backup();
        while (canRead() && isNewLine(peek()))
            skip();

        if (start.index != position.index)
            return new Token(TokenType.NEWLINE, span(start));
        return null;
    }

    private boolean isNewLine(char c) {
        return c == NEW_LINE;
    }

    private boolean handleIndentation() throws TokenizationException {
        Position start = position.backup();
        String indentation = readWhitespace();

        if (indentation == null || !canRead() || peek() == '#')
            return false;

        if (this.indentation == null) {
            this.indentation = indentation;
            indentationLevel = 1;
            pending.add(new Token(TokenType.INDENT, indentation, span(start)));
            return true;
        }

        int level = StringUtils.countMatches(indentation, this.indentation);
        if (this.indentation.length() * level != indentation.length()) {
            throw new TokenizationException("Indentation must be made of repeated \""
                    + StringEscapeUtils.escapeJava(this.indentation)
                    + "\" but got \"" + StringEscapeUtils.escapeJava(indentation) + "\"", span(start), input);
        }
        int diff = level - indentationLevel;
        if (diff == 0)
            return false;
        if (diff > 1) {
            throw new TokenizationException("Unexpected indentation: jumped from level "
                    + indentationLevel + " to " + level, span(start), input);
        }

        indentationLevel = level;
        if (diff == 1) {
            pending.add(new Token(TokenType.INDENT, indentation, span(start)));
            return true;
        }
        for (int i = 0; i < -diff; i++)
            pending.add(new Token(TokenType.DEDENT, indentation, span(start)));
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

    private SourceSpan span() {
        return span(position);
    }

    private SourceSpan span(Position start) {
        return new SourceSpan(
                file,
                start.index,
                position.index,
                start.line,
                position.line,
                start.column,
                position.column
        );
    }

    private boolean canRead() {
        return canRead(0);
    }

    private boolean canRead(int offset) {
        return position.index + offset < input.length();
    }

    private char peek() {
        return peek(0);
    }

    private char peek(int offset) {
        return input.charAt(position.index + offset);
    }

    private char read() {
        char c = input.charAt(position.index++);
        if (c == '\r') {
            if (canRead() && peek() == '\n')
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

    private void skip() {
        read();
    }

    private boolean expect(char c) {
        return canRead() && read() == c;
    }
    
    private void expectOrThrow(char c, SourceSpan span) {
        if (!canRead())
            throw new TokenizationException("Expected '" + c + "', but got EOF", span, input);
        char got = read();
        if (c != got)
            throw new TokenizationException("Expected '" + c
                    + "', but got '" + StringEscapeUtils.escapeJava(String.valueOf(got)) + "'", span, input);
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    private static class Position {

        private int index, line = 1, column = 1;
    
        public Position backup() {
            return new Position(index, line, column);
        }

        public void apply(Position position) {
            this.index = position.index;
            this.line = position.line;
            this.column = position.column;
        }

    }

}
