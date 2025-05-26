package me.tud.skriptinterpreter.lexer;

public record Token(TokenType type, String value, int line, int column) {

    public boolean softEquals(Token other) {
        if (type != other.type)
            return false;
        if (type == TokenType.WHITESPACE)
            return true; // Treat all whitespaces as equal
        return value.equalsIgnoreCase(other.value);
    }

}
