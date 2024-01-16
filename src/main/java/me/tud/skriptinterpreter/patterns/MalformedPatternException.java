package me.tud.skriptinterpreter.patterns;

public class MalformedPatternException extends RuntimeException {

    public MalformedPatternException() {
    }

    public MalformedPatternException(String message) {
        super(message);
    }

    public MalformedPatternException(String message, Throwable cause) {
        super(message, cause);
    }

    public MalformedPatternException(Throwable cause) {
        super(cause);
    }

    public MalformedPatternException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }

}
