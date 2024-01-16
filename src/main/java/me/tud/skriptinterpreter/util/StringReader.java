package me.tud.skriptinterpreter.util;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.Nullable;

public class StringReader implements Cloneable {

    private final String string;
    private final int length;
    private int cursor;

    public StringReader(String string) {
        this.string = string;
        this.length = string.length();
    }

    public String getString() {
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

    @Contract(mutates = "this")
    public @Nullable String readEnclosed(char opening, char closing) {
        if (peek() != opening) return null;
        int start = cursor + 1, depth = 0;
        do {
            char c = read();
            if (c == opening) depth++;
            else if (c == closing) depth--;
        } while (depth != 0 && canRead());
        if (depth != 0) return null;
        return string.substring(start, cursor - 1);
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
