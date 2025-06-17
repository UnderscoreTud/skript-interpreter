package me.tud.skriptinterpreter.pattern;

import me.tud.skriptinterpreter.lang.Expressions;

import java.util.Arrays;

public record MatchResult(boolean success, String pattern, String input, Metadata metadata) {

    public static MatchResult success(String pattern, String input, Metadata metadata) {
        return new MatchResult(true, pattern, input, metadata);
    }

    public static MatchResult fail(String input) {
        return new MatchResult(false, null, input, null);
    }

    public static final class Metadata {

        private Expressions expressions;
        private java.util.regex.MatchResult[] regexes;

        public Metadata() {
            this(null, null);
        }

        public Metadata(Expressions expressions, java.util.regex.MatchResult[] regexes) {
            this.expressions = expressions;
            this.regexes = regexes;
        }

        void allocateExpressions(int expressionCount) {
            expressions = new Expressions(new Object[expressionCount]);
        }

        void allocateRegexes(int regexCount) {
            regexes = new java.util.regex.MatchResult[regexCount];
        }

        public Expressions expressions() {
            return expressions;
        }

        public java.util.regex.MatchResult[] regexes() {
            return regexes;
        }

        @Override
        public String toString() {
            return "Metadata[" +
                    "expressions=" + expressions + ", " +
                    "regexes=" + Arrays.toString(regexes) + ']';
        }

    }

}
