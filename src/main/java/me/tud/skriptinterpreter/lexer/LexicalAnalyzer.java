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
            String string = reader.readEnclosed('"', '"', '\\');
            if (string != null)
                return new Token(TokenType.STRING, string, line, column);
        } else if (current >= '0' && current <= '9' || reader.canRead(2) && (current == '-' || current == '.')) {
            String number = readNumber();
            return new Token(TokenType.NUMBER, number, line, column);
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

    private String readNumber() {
        StringBuilder number = new StringBuilder();
        if (reader.peek() == '-')
            number.append(reader.read());
        boolean hasDecimal = false;
        char c;
        while (reader.canRead() && isValidDigitPart(c = reader.peek())) {
            if (c == '.') {
                if (hasDecimal)
                    break;
                hasDecimal = true;
            }
            number.append(c);
            reader.skip();
        }
        return number.toString();
    }

    private boolean isValidDigitPart(char c) {
        return (c >= '0' && c <= '9') || c == '.';
    }

}
