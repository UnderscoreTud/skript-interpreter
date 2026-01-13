package me.tud.skriptinterpreter.lang;

import org.jetbrains.annotations.Nullable;

/**
 * Represents a span of text in a source file.
 *
 * @param file        the path to the file
 * @param start       the absolute start index
 * @param end         the absolute end index
 * @param lineStart   the line number where the span starts
 * @param lineEnd     the line number where the span ends
 * @param columnStart the column number where the span starts
 * @param columnEnd   the column number where the span ends
 */
public record SourceSpan(String file, int start, int end, int lineStart, int lineEnd, int columnStart, int columnEnd) {

    /**
     * Creates a string representation of the span by underlining it in the source code.
     *
     * @param source the source code
     * @param indent an optional indentation to prepend to each line
     * @return the formatted string
     * @throws StringIndexOutOfBoundsException if the span is invalid for the given source
     */
    public String underline(String source, @Nullable String indent) {
        if (start < 0 || end < 0 || start > end || end > source.length()) {
            throw new StringIndexOutOfBoundsException(
                    "Invalid span [" + start + ", " + end + ") for source length " + source.length());
        }

        if (indent == null)
            indent = "";

        int lineStartIndex = source.lastIndexOf('\n', Math.max(0, start - 1)) + 1;
        int lineEndIndex = source.indexOf('\n', start);
        if (lineEndIndex == -1)
            lineEndIndex = source.length();
        int lineContentEnd = (lineEndIndex > lineStartIndex && source.charAt(lineEndIndex - 1) == '\r') ? lineEndIndex - 1 : lineEndIndex;
        String line = source.substring(lineStartIndex, lineContentEnd);

        boolean multiline = end - 1 > lineEndIndex;
        int endWithinLine = Math.min(end, lineContentEnd);

        int column1 = clamp(start - lineStartIndex + 1, 1, line.length() + 1);
        int column2 = endWithinLine - lineStartIndex + 1;

        int pad = column1 - 1;
        int caretCount = clamp(column2 - column1, 1, line.length() + 1 - column1);

        String carets = "^".repeat(caretCount);
        String caretPad = " ".repeat(pad);

        StringBuilder builder = new StringBuilder();
        builder.append(indent).append(line).append('\n')
                .append(indent).append(caretPad).append(carets);
        if (multiline)
            builder.append(" …");
        return builder.toString();
    }
    
    private int clamp(int n, int from, int to) {
        return Math.max(from, Math.min(n, to));
    }

}
