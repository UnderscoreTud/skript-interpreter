package me.tud.skriptinterpreter.lexer;

import java.util.Iterator;

public class TokenIterator implements Iterator<Token>, Cloneable {

    private final String input;
    private final Token[] tokens;
    private int position;

    public TokenIterator(String input, Token[] tokens) {
        this.input = input;
        this.tokens = tokens;
        this.position = 0;
    }

    public boolean hasNext() {
        return position < tokens.length;
    }

    public Token next() {
        if (!hasNext())
            throw new IllegalStateException("No more tokens available");
        return tokens[position++];
    }

    public Token peek() {
        if (!hasNext())
            throw new IllegalStateException("No more tokens available");
        return tokens[position];
    }

    public String input() {
        return input;
    }

    public Token[] tokens() {
        return tokens;
    }

    public int position() {
        return position;
    }

    public void position(int position) {
        if (position < 0 || position >= tokens.length)
            throw new IndexOutOfBoundsException("Position out of bounds: " + position);
        this.position = position;
    }

    public TokenIterator subIterator(int start, int end) {
        if (start < 0 || end > tokens.length || start >= end)
            throw new IndexOutOfBoundsException("Invalid sub-iterator range: " + start + " to " + end);
        Token[] subTokens = new Token[end - start];
        System.arraycopy(tokens, start, subTokens, 0, end - start);
        return new TokenIterator(input, subTokens);
    }

    @Override
    public TokenIterator clone() {
        try {
            return (TokenIterator) super.clone();
        } catch (CloneNotSupportedException e) {
            throw new AssertionError();
        }
    }

}
