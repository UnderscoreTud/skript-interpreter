package me.tud.skriptinterpreter.patterns;

import me.tud.skriptinterpreter.Skript;
import me.tud.skriptinterpreter.util.StringReader;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RegexPatternElement extends AbstractPatternElement {

    public static final Compiler<RegexPatternElement> COMPILER = (reader, context) -> {
        if (reader.peek() != '<') return null;
        String regexPattern = reader.readEnclosed('<', '>');
        if (regexPattern == null) return null; // TODO maybe throw exception?
        return new RegexPatternElement(context.skript(), Pattern.compile(regexPattern));
    };

    private final Pattern regexPattern;

    public RegexPatternElement(Skript skript, Pattern regexPattern) {
        super(skript);
        this.regexPattern = regexPattern;
    }

    @Override
    protected boolean matches(StringReader reader, MatchResult.DataContainer dataContainer, boolean exhaust) {
        Matcher matcher = regexPattern.matcher(reader.input());
        int start = reader.cursor();
        // if this is the last element then just match the rest of the string with the regex pattern
        if (next() == null) reader.cursor(reader.length() - 1);
        while (reader.canRead()) {
            reader.skip();
            matcher.region(start, reader.cursor());
            if (!matcher.matches()) continue;
            StringReader newReader = reader.clone();
            MatchResult.DataContainer childContainer = dataContainer.child();
            if (!matchNext(newReader, childContainer, exhaust)) continue;
            dataContainer.getOrCreate(Data.class, Data::new).results().add(matcher.toMatchResult());
            reader.cursor(newReader.cursor());
            dataContainer.combine(childContainer);
            return true;
        }
        return false;
    }

    @Override
    public boolean match(StringReader reader, MatchResult.DataContainer dataContainer, boolean exhaust) {
        return matches(reader, dataContainer, exhaust);
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
