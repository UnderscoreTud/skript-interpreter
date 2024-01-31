package me.tud.skriptinterpreter.patterns;

import me.tud.skriptinterpreter.Skript;
import me.tud.skriptinterpreter.util.StringReader;

import java.util.ListIterator;

public class ParseTagPatternElement extends AbstractPatternElement {

    public static final Compiler<ParseTagPatternElement> COMPILER = (pattern, reader, lookbehind) -> {
        if (reader.read() != ':') return null;
        String tag = lookbehind.consume();
        return new ParseTagPatternElement(pattern.skript(), tag);
    };

    private String tag;
    private final int mark;

    public ParseTagPatternElement(Skript skript, String tag) {
        this(skript, tag, !tag.isEmpty() ? tag.chars().allMatch(c -> '0' <= c && c <= '9') ? Integer.parseInt(tag) : 0 : 0);
    }

    public ParseTagPatternElement(Skript skript, String tag, int mark) {
        super(skript);
        this.tag = tag;
        this.mark = mark;
    }

    @Override
    protected boolean matches(StringReader reader, MatchResult.Builder builder) {
        if (tag != null && !tag.isEmpty()) builder.addData(new TagData(tag));
        builder.getOrCreateData(MarkData.class, MarkData::new).apply(mark);
        return true;
    }

    @Override
    public void next(PatternElement element) {
        if (tag == null || !tag.isBlank()) {
            super.next(element);
            return;
        }

        if (element instanceof LiteralPatternElement literal) {
            tag = literal.toString().trim();
            super.next(element);
            return;
        }

        if (!(element instanceof GroupPatternElement group)) {
            super.next(element);
            return;
        }

        PatternElement inner = group.pattern().head();
        if (!(inner instanceof ChoicePatternElement choicePattern)) {
            super.next(element);
            return;
        }

        ListIterator<PatternElement> choiceIterator = choicePattern.choices().listIterator();
        while (choiceIterator.hasNext()) {
            PatternElement choice = choiceIterator.next();
            if (!(choice instanceof LiteralPatternElement literal)) continue;
            String tag = literal.toString().trim();
            if (tag.isEmpty()) continue;
            ParseTagPatternElement newTag = new ParseTagPatternElement(skript(), tag);
            newTag.next(literal);
            choiceIterator.set(newTag);
        }
        tag = null;
        super.next(element);
    }

    public String tag() {
        return tag;
    }

    public int mark() {
        return mark;
    }

    @Override
    public String toString() {
        return tag != null ? tag + ":" : "";
    }

    public record TagData(String tag) implements MatchResult.Data {}

    public static class MarkData implements MatchResult.Data {

        private int mark = 0;

        public int mark() {
            return mark;
        }

        private void apply(int mark) {
            this.mark ^= mark;
        }

    }
    
}
