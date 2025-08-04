package me.tud.skriptinterpreter.pattern;

import me.tud.skriptinterpreter.lang.Expression;
import me.tud.skriptinterpreter.lang.Expressions;

import java.util.*;

public record MatchResult(boolean success, String pattern, String input, Metadata metadata) {

    public static MatchResult success(String pattern, String input, Metadata metadata) {
        return new MatchResult(true, pattern, input, metadata);
    }

    public static MatchResult fail(String input) {
        return new MatchResult(false, null, input, null);
    }

    public static final class Metadata {

        private boolean ready;
        private Expressions<?> expressions;
        private java.util.regex.MatchResult[] regexes;
        private final Queue<Runnable> whenReady = new ArrayDeque<>();

        public Metadata() {
            this(null, null);
        }

        public Metadata(Expressions<?> expressions, java.util.regex.MatchResult[] regexes) {
            this.expressions = expressions;
            this.regexes = regexes;
        }

        void ready() {
            if (ready)
                return;
            ready = true;
            for (Runnable runnable : whenReady)
                runnable.run();
        }

        void allocateExpressions(int expressionCount) {
            //noinspection unchecked,rawtypes
            expressions = new Expressions<>(new Expression[expressionCount]);
        }

        void allocateRegexes(int regexCount) {
            regexes = new java.util.regex.MatchResult[regexCount];
        }

        public <T> Expressions<T> expressions() {
            //noinspection unchecked
            return (Expressions<T>) expressions;
        }

        public java.util.regex.MatchResult[] regexes() {
            return regexes;
        }

        public void whenReady(Runnable runnable) {
            if (ready) {
                runnable.run();
                return;
            }
            whenReady.offer(runnable);
        }

        @Override
        public String toString() {
            return "Metadata[" +
                    "expressions=" + expressions + ", " +
                    "regexes=" + Arrays.toString(regexes) + ']';
        }

    }

}
