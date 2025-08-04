package me.tud.skriptinterpreter.pattern;

public class MalformedPatternException extends RuntimeException {

    public MalformedPatternException(String message) {
        super(message);
    }

    public MalformedPatternException(String message, Throwable cause) {
        super(message, cause);
    }

}
