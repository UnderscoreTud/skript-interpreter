package me.tud.skriptinterpreter.patterns;

import me.tud.skriptinterpreter.Skript;
import me.tud.skriptinterpreter.lang.Expression;
import me.tud.skriptinterpreter.parser.SkriptParser;
import me.tud.skriptinterpreter.registration.TypeRegistry;
import me.tud.skriptinterpreter.types.TypeResult;
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
        TypeRegistry registry = pattern.skript().typeRegistry();
        String[] codenames = string.split("/");
        TypeResult<?>[] types = new TypeResult<?>[codenames.length];
        for (int i = 0; i < codenames.length; i++) {
            String codename = codenames[i];
            types[i] = registry.parseInput(codename).orElseThrow(() ->
                    new MalformedPatternException("Couldn't find type with codename '" + codename + "'"));
        }
        return new ExpressionPatternElement(pattern.skript(), types, flags);
    };

    private final TypeResult<?>[] types;
    private final EnumSet<Flag> flags;

    public ExpressionPatternElement(Skript skript, TypeResult<?>[] types, EnumSet<Flag> flags) {
        super(skript);
        this.types = types;
        this.flags = flags;
    }

    @Override
    protected boolean matches(StringReader reader, MatchResult.DataContainer dataContainer, boolean exhaust) {
        StringBuilder stringBuilder = new StringBuilder();

        EnumSet<SkriptParser.Flag> parserFlags;
        if (flags.contains(Flag.NON_LITERAL)) {
            parserFlags = EnumSet.of(SkriptParser.Flag.PARSE_NON_LITERALS, SkriptParser.Flag.PARSE_VARIABLES);
        } else if (flags.contains(Flag.LITERAL)) {
            parserFlags = EnumSet.of(SkriptParser.Flag.PARSE_LITERALS);
        } else if (flags.contains(Flag.VARIABLE)) {
            parserFlags = EnumSet.of(SkriptParser.Flag.PARSE_VARIABLES);
        } else {
            parserFlags = EnumSet.allOf(SkriptParser.Flag.class);
        }

        SkriptParser parser = SkriptParser.create(skript(), "", parserFlags);
        while (reader.canRead()) {
            stringBuilder.append(reader.read());
            parser = parser.withInput(stringBuilder.toString());
            Expression<?, ?> expression = parser.parse(skript().expressions(), this::check);
            if (expression == null) continue;

            StringReader newReader = reader.clone();
            MatchResult.DataContainer childContainer = dataContainer.child();
            if (!matchNext(newReader, childContainer, exhaust)) continue;
            // TODO modify data
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

    private boolean check(Expression<?, ?> expression) {
        for (TypeResult<?> typeResult : types) {
            if (!typeResult.plural() && !expression.isSingle()) continue;
            // TODO replace with converters
            if (!typeResult.type().underlyingClass().isAssignableFrom(expression.returnType())) continue;
            return true;
        }
        return false;
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder("%");
        for (Flag flag : flags)
            builder.append(flag.sign);
        for (int i = 0; i < types.length; i++) {
            if (i > 0) builder.append('/');
            builder.append(types[i].input());
        }
        return builder + "%";
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

    public static class Data implements MatchResult.Data {

        private final List<Expression<?, ?>> expressions = new ArrayList<>();

        public List<Expression<?, ?>> expressions() {
            return expressions;
        }

        public Expression<?, ?>[] expressionsArray() {
            return expressions().toArray(new Expression[0]);
        }

        @Override
        public void combine(MatchResult.Data other) {
            if (other instanceof Data otherData) expressions.addAll(otherData.expressions);
        }

    }
    
}
