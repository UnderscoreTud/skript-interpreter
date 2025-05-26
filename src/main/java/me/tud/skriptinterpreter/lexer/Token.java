package me.tud.skriptinterpreter.lexer;

public record Token(TokenType type, String value, int line, int column) {}
