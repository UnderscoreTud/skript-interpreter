package me.tud.skriptinterpreter.types;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.function.Function;

public class Type<T> {

    private final Class<T> underlyingClass;
    private final String codename;
    private final String pluralCodename;
    private final Map<String, String> names;
    private final @Nullable Function<String, T> literalParser;

    protected Type(
            Class<T> underlyingClass,
            String codename,
            Map<String, String> names,
            @Nullable Function<String, T> literalParser
    ) {
        this.underlyingClass = Objects.requireNonNull(underlyingClass, "underlyingClass cannot be null");
        this.codename = Objects.requireNonNull(codename, "codename cannot be null");
        this.pluralCodename = codename + "s"; // TODO change to some sort of simple format
        this.names = names;
        this.literalParser = literalParser;
    }

    public Class<T> underlyingClass() {
        return underlyingClass;
    }

    public String codename() {
        return codename(false);
    }

    public String codename(boolean plural) {
        return plural ? pluralCodename : codename;
    }

    public Map<String, String> names() {
        return Collections.unmodifiableMap(names);
    }

    public Optional<Function<String, T>> literalParser() {
        return Optional.ofNullable(literalParser);
    }

    public Optional<T> parse(String input) {
        return literalParser().map(parser -> parser.apply(input));
    }
    
    @Override
    public String toString() {
        return "Type[class=" + underlyingClass + ", codename=" + codename + "]"; 
    }

    public static <T> Builder<T> builder(Class<T> type) {
        return new Builder<>(type);
    }
    
    public static class Builder<T> {

        private final Class<T> type;
        private final Map<String, String> names = new HashMap<>();
        private String codename;
        private Function<String, T> literalParser;

        protected Builder(Class<T> type) {
            this.type = type;
        }

        public Builder<T> codename(String codename) {
            this.codename = codename;
            return this;
        }

        public Builder<T> name(String singular, String plural) {
            names.put(singular, plural);
            return this;
        }

        public Builder<T> literalParser(Function<String, T> literalParser) {
            this.literalParser = literalParser;
            return this;
        }
        
        @Contract(value = " -> new", pure = true)
        public Type<T> build() {
            return new Type<>(type, codename, names, literalParser);
        }

    }

}
