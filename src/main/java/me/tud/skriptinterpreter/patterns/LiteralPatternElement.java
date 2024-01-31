package me.tud.skriptinterpreter.patterns;

import me.tud.skriptinterpreter.Skript;
import me.tud.skriptinterpreter.util.StringReader;

public class LiteralPatternElement extends AbstractPatternElement {

    private final String literal;

    public LiteralPatternElement(Skript skript, String literal) {
        super(skript);
        this.literal = process(literal);
    }

    @Override
    protected boolean matches(StringReader reader, MatchResult.Builder builder) {
        if (literal.isEmpty()) return true;
        if (reader.cursor() > 0 && literal.charAt(0) == ' ' && reader.peek(-1) == ' ')
            reader.cursor(reader.cursor() - 1);
        for (int i = 0, size = literal.length(); i < size; i++) {
            if (!reader.canRead()) return i + 1 == size && literal.charAt(i) == ' ';
            char c = Character.toLowerCase(reader.read());
            if (c != literal.charAt(i)) return false;
            if (c == ' ') reader.readUntil(character -> character != ' ');
        }
        return true;
    }

    @Override
    public String toString() {
        return literal;
    }

    private static String process(String string) {
        if (string.isBlank()) return string.isEmpty() ? "" : " ";
        return string.replaceAll("\\s{2,}", " ").toLowerCase();
    }

}
