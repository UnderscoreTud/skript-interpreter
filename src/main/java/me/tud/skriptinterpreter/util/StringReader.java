package me.tud.skriptinterpreter.util;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;
import java.util.function.Predicate;

public class StringReader implements Cloneable {

    private final String string;
    private final int length;
    private int cursor;

    public StringReader(String string) {
        this.string = string;
        this.length = string.length();
    }

    public String input() {
        return string;
    }

    public int length() {
        return length;
    }

    public int cursor() {
        return cursor;
    }

    public void cursor(int cursor) {
        this.cursor = cursor;
    }

    public String readUntil(Predicate<Character> predicate) {
        int start = cursor;
        while (canRead() && !predicate.test(peek()))
            skip();
        return string.substring(start, cursor);
    }

    @Contract(mutates = "this")
    public @Nullable String readEnclosed(char opening, char closing) {
        return readEnclosed(opening, closing, null);
    }

    @Contract(mutates = "this")
    public @Nullable String readEnclosed(char opening, char closing, @Nullable Character escapeChar) {
        if (peek() != opening) return null;
        skip();
        StringBuilder builder = new StringBuilder();
        int depth = 1;
        boolean escape = false;
        while (canRead()) {
            char c = read();
            if (escape) {
                escape = false;
                builder.append(c);
                continue;
            } else if (canRead() && Objects.equals(c, escapeChar) && (peek() == opening || peek() == closing)) {
                escape = true;
                continue;
            }
            if (c == closing && --depth == 0) return builder.toString();
            else if (c == opening) depth++;
            builder.append(c);
        }
        return null;
    }

    public char read() {
        return string.charAt(cursor++);
    }

    public char peek() {
        return peek(0);
    }

    public char peek(int offset) {
        return string.charAt(cursor + offset);
    }

    public void skip() {
        cursor++;
    }

    public boolean canRead() {
        return cursor < length;
    }

    public String finish() {
        int cursor = this.cursor;
        this.cursor = length;
        return string.substring(cursor);
    }

    @Override
    public StringReader clone() {
        try {
            return (StringReader) super.clone();
        } catch (CloneNotSupportedException e) {
            throw new AssertionError();
        }
    }

}
