package me.tud.skriptinterpreter.types;

public record TypeResult<T>(Type<T> type, boolean plural, String input) {}
