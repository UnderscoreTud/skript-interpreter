package me.tud.skriptinterpreter.patterns;

import me.tud.skriptinterpreter.Skript;
import me.tud.skriptinterpreter.util.StringReader;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RegexPatternElement extends AbstractPatternElement {

    public static final Compiler<RegexPatternElement> COMPILER = (pattern, reader, lookbehind) -> {
        if (reader.peek() != '<') return null;
        String regexPattern = reader.readEnclosed('<', '>');
        if (regexPattern == null) return null; // TODO maybe throw exception?
        return new RegexPatternElement(pattern.skript(), Pattern.compile(regexPattern));
    };

    private final Pattern regexPattern;

    public RegexPatternElement(Skript skript, Pattern regexPattern) {
        super(skript);
        this.regexPattern = regexPattern;
    }

    @Override
    protected boolean matches(StringReader reader, MatchResult.Builder builder) {
        Matcher matcher = regexPattern.matcher(reader.getString());
        int start = reader.cursor();
        // if this is the last element then just match the rest of the string with the regex pattern
        if (next() == null) reader.cursor(reader.length() - 1);
        while (reader.canRead()) {
            reader.skip();
            matcher.region(start, reader.cursor());
            if (!matcher.matches()) continue;
            StringReader newReader = reader.clone();
            MatchResult.Builder newBuilder = MatchResult.fromBuilder(builder);
            if (!matchNext(newReader, newBuilder)) continue;
            builder.getOrCreateData(Data.class, Data::new).results().add(matcher.toMatchResult());
            reader.cursor(newReader.cursor());
            builder.combine(newBuilder);
            return true;
        }
        return false;
    }

    @Override
    public boolean match(StringReader reader, MatchResult.Builder builder) {
        return matches(reader, builder);
    }

    public Pattern regexPattern() {
        return regexPattern;
    }

    @Override
    public String toString() {
        return "<" + regexPattern.pattern() + ">";
    }

    public record Data(List<java.util.regex.MatchResult> results) implements MatchResult.Data {

        public Data() {
            this(new ArrayList<>());
        }

        @Override
        public void combine(MatchResult.Data other) {
            if (other instanceof Data otherData) results().addAll(otherData.results());
        }

    }

}
