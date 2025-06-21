package me.tud.skriptinterpreter.lexer;

import me.tud.skriptinterpreter.util.StringReader;

import java.util.ArrayList;
import java.util.List;

public class LexicalAnalyzer {

    private final StringReader reader;

    public LexicalAnalyzer(String input) {
        this.reader = new StringReader(input);
    }

    public List<Token> tokenize() {
        List<Token> tokens = new ArrayList<>();
        Token token, lastToken = null;
        while ((token = nextToken()) != null) {
            if (token.type() == TokenType.WORD && lastToken != null && lastToken.type() == TokenType.WORD) {
                String mergedValue = lastToken.value() + token.value();
                lastToken = new Token(TokenType.WORD, mergedValue, lastToken.line(), lastToken.column());
                tokens.set(tokens.size() - 1, lastToken);
                continue;
            }
            tokens.add(token);
            lastToken = token;
        }
        return tokens;
    }

    public TokenIterator iterator() {
        return new TokenIterator(reader.input(), tokenize().toArray(new Token[0]));
    }

    public Token nextToken() {
        if (!reader.canRead())
            return null;

        int start = reader.cursor();
        char current = reader.peek();
        int line = reader.line();
        int column = reader.column();

        if (current == '\n') {
            String lines = reader.readUntil(c -> c != '\n');
            return new Token(TokenType.NEWLINE, lines, line, column);
        } else if (Character.isWhitespace(current)) {
            String whitespace = reader.readUntil(c -> !Character.isWhitespace(c));
            return new Token(TokenType.WHITESPACE, whitespace, line, column);
        } else if (current == '#') {
            String comment = reader.readUntil(c -> c == '\n');
//            return new Token(TokenType.COMMENT, comment, line, column);
            return nextToken();
        } else if (current == '"') {
            String string = readString();
            if (string != null)
                return new Token(TokenType.STRING, string, line, column);
            reader.cursor(start);
        } else if (current >= '0' && current <= '9' || reader.canRead(2) && (current == '-' || current == '.')) {
            String number = readNumber();
            if (number != null)
                return new Token(TokenType.NUMBER, number, line, column);
            reader.cursor(start);
        }
        switch (current) {
            case '+', '-', '*', '/', '^', '%'-> {
                reader.skip();
                return new Token(TokenType.OPERATOR, String.valueOf(current), line, column);
            }
            case '=', '<', '>', '!' -> {
                StringBuilder operator = new StringBuilder(String.valueOf(current));
                reader.skip();
                if (reader.canRead() && reader.peek() == '=')
                    operator.append(reader.read());
                return new Token(TokenType.OPERATOR, operator.toString(), line, column);
            }
            case ',', ';', ':', '.', '(', ')', '[', ']', '{', '}' -> {
                reader.skip();
                return new Token(TokenType.PUNCTUATION, String.valueOf(current), line, column);
            }
        }

        reader.skip();
        return new Token(TokenType.WORD, String.valueOf(current), line, column);
    }

    private String readString() {
        reader.skip();
        StringBuilder builder = new StringBuilder();
        boolean escape = false;
        boolean inExpression = false;
        while (reader.canRead()) {
            if (escape) {
                builder.append(reader.read());
                escape = false;
                continue;
            }
            char c = reader.peek();
            if (c == '"') {
                if (inExpression) {
                    String innerString = readString();
                    if (innerString == null)
                        return null;
                    builder.append('"').append(innerString).append('"');
                } else {
                    reader.skip();
                    return builder.toString();
                }
            } else if (c == '\\') {
                escape = true;
                reader.skip();
            } else {
                if (reader.canRead(2) && c == '%' && reader.peek(1) != '%') inExpression = !inExpression;
                builder.append(c);
                reader.skip();
            }
        }
        return null;
    }

    private String readNumber() {
        StringBuilder number = new StringBuilder();
        if (reader.peek() == '-')
            number.append(reader.read());
        boolean containsDigits = false;
        boolean hasDecimal = false;
        char c;
        while (reader.canRead() && isValidDigitPart(c = reader.peek())) {
            if (c == '.') {
                if (hasDecimal)
                    break;
                hasDecimal = true;
            } else if (c >= '0' && c <= '9') {
                containsDigits = true;
            }
            number.append(c);
            reader.skip();
        }
        if (!containsDigits)
            return null;
        return number.toString();
    }

    private boolean isValidDigitPart(char c) {
        return (c >= '0' && c <= '9') || c == '.';
    }

}
