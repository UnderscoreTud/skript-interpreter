package me.tud.skriptinterpreter.lexer;

import me.tud.skriptinterpreter.util.StringReader;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class LexicalAnalyzer implements Iterator<Token>, Cloneable {

    private final StringReader reader;

    private Token tokenCache;

    public LexicalAnalyzer(String input) {
        this(new StringReader(input));
    }

    public LexicalAnalyzer(StringReader reader) {
        this.reader = reader;
    }

    public String input() {
        return reader.input();
    }

    public StringReader reader() {
        return reader;
    }

    public int cursor() {
        return reader.cursor();
    }

    public void cursor(int cursor) {
        reader.cursor(cursor);
        tokenCache = null;
    }

    public List<Token> tokenize() {
        List<Token> tokens = new ArrayList<>();
        while (hasNext())
            tokens.add(next());
        return tokens;
    }

    public TokenIterator iterator() {
        return new TokenIterator(reader.input(), tokenize().toArray(new Token[0]));
    }

    @Override
    public boolean hasNext() {
        return reader.canRead() || tokenCache != null;
    }

    @Override
    public Token next() {
        if (tokenCache != null) {
            Token token = tokenCache;
            tokenCache = null;
            return token;
        }

        if (!hasNext())
            throw new IllegalStateException("No more tokens available");

        Token token = nextToken0();
        if (token != null)
            return token;

        int start = reader.cursor(), end;
        int line = reader.line();
        int column = reader.column();
        do {
            reader.skip();
            end = reader.cursor();
        } while (reader.canRead() && (token = nextToken0()) == null);

        tokenCache = token;
        return new Token(TokenType.WORD, input().substring(start, end), line, column);
    }

    private Token nextToken0() {
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
            return next();
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

        return null;
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

    @Override
    public LexicalAnalyzer clone() {
        return new LexicalAnalyzer(reader.clone());
    }

}
