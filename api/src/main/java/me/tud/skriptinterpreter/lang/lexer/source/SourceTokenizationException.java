package me.tud.skriptinterpreter.lang.lexer.source;

import me.tud.skriptinterpreter.lang.SourceSpan;
import me.tud.skriptinterpreter.lang.lexer.TokenizationException;

/**
 * Thrown when an error occurs during source tokenization.
 */
public class SourceTokenizationException extends TokenizationException {

    /**
     * Creates a new tokenization exception.
     *
     * @param message the error message
     * @param span    the span where the error occurred
     * @param source  the source code
     */
    public SourceTokenizationException(String message, SourceSpan span, String source) {
        super(format(message, span, source));
    }

    /**
     * Formats the error message with the source span and line underlining.
     *
     * @param message the error message
     * @param span    the span where the error occurred
     * @param source  the source code
     * @return the formatted error message
     */
    private static String format(String message, SourceSpan span, String source) {
        String fileLineCol = span.origin() + ":" + span.lineStart() + ":" + span.columnStart();
        String header = fileLineCol + ": error: " + message;
        if (source == null || source.isEmpty())
            return header;
        String indent = "  ";
        return header + "\n" + span.underline(source, indent);
    }

}
