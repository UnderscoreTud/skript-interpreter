package me.tud.skriptinterpreter.patterns;

import me.tud.skriptinterpreter.Skript;
import me.tud.skriptinterpreter.lang.Expression;
import me.tud.skriptinterpreter.parser.SkriptParser;
import me.tud.skriptinterpreter.util.StringReader;

import java.util.*;

public class ExpressionPatternElement extends AbstractPatternElement {

    public static final Compiler<ExpressionPatternElement> COMPILER = (pattern, reader, lookbehind) -> {
        if (reader.read() != '%') return null;
        String string = reader.readUntil(c -> c == '%');
        reader.skip(); // Skip the last %
        if (string == null || string.isBlank()) return null;
        EnumSet<Flag> flags = EnumSet.noneOf(Flag.class);
        while (true) {
            Flag flag = Flag.byChar(string.charAt(0)).orElse(null);
            if (flag == null) break;
            string = string.substring(1);
            flags.add(flag);
        }
        return new ExpressionPatternElement(pattern.skript(), string.split("/"), flags);
    };

    // TODO make some sort of type class
    private final String[] types;
    private final EnumSet<Flag> flags;

    public ExpressionPatternElement(Skript skript, String[] types, EnumSet<Flag> flags) {
        super(skript);
        this.types = types;
        this.flags = flags;
    }

    @Override
    protected boolean matches(StringReader reader, MatchResult.Builder builder, boolean exhaust) {
        StringBuilder stringBuilder = new StringBuilder();
        while (reader.canRead()) {
            stringBuilder.append(reader.read());
            SkriptParser parser = SkriptParser.create(skript(), stringBuilder.toString(), EnumSet.allOf(SkriptParser.Flag.class));
            Expression<?, ?> expression = parser.parse(skript().expressions());
            if (expression == null) continue;
            StringReader newReader = reader.clone();
            MatchResult.Builder newBuilder = MatchResult.fromBuilder(builder);
            if (!matchNext(newReader, newBuilder, exhaust)) continue;
            reader.cursor(newReader.cursor());
            builder.combine(newBuilder);
            return true;
        }
        return false;
    }

    @Override
    public boolean match(StringReader reader, MatchResult.Builder builder, boolean exhaust) {
        return matches(reader, builder, exhaust);
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        for (Flag flag : flags)
            builder.append(flag.sign);
        return builder + String.join("/", types);
    }

    public enum Flag {

        OPTIONAL('-'),
        LITERAL('*'),
        NON_LITERAL('~'),
        VARIABLE('^'),
        CONDITION('=');

        private static final Map<Character, Flag> charToFlag = new HashMap<>(5);

        static {
            for (Flag flag : values())
                charToFlag.put(flag.sign, flag);
        }

        private final char sign;

        Flag(char sign) {
            this.sign = sign;
        }

        public static Optional<Flag> byChar(char sign) {
            return Optional.ofNullable(charToFlag.get(sign));
        }

    }
    
}
