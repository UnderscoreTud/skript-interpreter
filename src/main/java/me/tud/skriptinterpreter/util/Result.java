package me.tud.skriptinterpreter.util;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.Unmodifiable;

import java.util.*;
import java.util.function.IntFunction;

public abstract sealed class Result<T> permits Result.Keyed, Result.Numbered {

    public abstract boolean isEmpty();

    public abstract boolean isSingle();

    public abstract boolean isKeyed();

    public abstract @Nullable T getSingle();

    public final Optional<T> getOptionalSingle() {
        return Optional.ofNullable(getSingle());
    }

    public abstract @Nullable Map.Entry<String, T> getKeyedSingle();

    public final Optional<Map.Entry<String, T>> getOptionalKeyedSingle() {
        return Optional.ofNullable(getKeyedSingle());
    }

    public abstract T[] getArray();

    public abstract Map<String, T> getKeyed();

    @Contract("_ -> new")
    public static <T> Result<T> empty(IntFunction<T[]> arrayGenerator) {
        return of(arrayGenerator);
    }

    @SafeVarargs
    @SuppressWarnings("unchecked")
    @Contract("_ -> new")
    public static <T> Result<T> of(T @NotNull ... values) {
        return of(size -> (T[]) CollectionUtils.newArray(values.getClass().componentType(), size), values);
    }

    @SafeVarargs
    @Contract("_, _ -> new")
    public static <T> Result<T> of(IntFunction<T[]> arrayGenerator, T @NotNull ... values) {
        Objects.requireNonNull(arrayGenerator, "array generator cannot be null");
        Objects.requireNonNull(values, "values array cannot be null");
        if (values.length == 0) return new Numbered<>(values);
        List<T> list = new ArrayList<>(values.length);
        for (T value : values)
            if (value != null) list.add(value);
        if (list.size() == values.length) return new Numbered<>(values);
        return new Numbered<>(list.toArray(arrayGenerator));
    }

    @SafeVarargs
    public static <T> Result<T> keyed(IntFunction<T[]> arrayGenerator, Map.Entry<String, T> @NotNull ... values) {
        Objects.requireNonNull(arrayGenerator, "array generator cannot be null");
        Objects.requireNonNull(values, "values array cannot be null");
        Map<String, T> map = new LinkedHashMap<>();
        for (Map.Entry<String, T> value : values)
            if (value != null) map.put(value.getKey(), value.getValue());
        return keyed(arrayGenerator, map);
    }

    public static <T> Result<T> keyed(IntFunction<T[]> arrayGenerator, Map<String, T> values) {
        Objects.requireNonNull(arrayGenerator, "array generator cannot be null");
        Objects.requireNonNull(values, "values map cannot be null");
        return new Keyed<>(arrayGenerator, values);
    }

    static final class Numbered<T> extends Result<T> {

        private final T[] values;
        private transient Map<String, T> keyedMap;

        private Numbered(T[] value) {
            this.values = value;
        }

        @Override
        public boolean isEmpty() {
            return values.length == 0;
        }

        @Override
        public boolean isSingle() {
            return values.length <= 1;
        }

        @Override
        public boolean isKeyed() {
            return false;
        }

        @Override
        public @Nullable T getSingle() {
            if (isEmpty()) return null;
            if (isSingle()) return values[0];
            throw new IllegalStateException("Cannot call Result#getSingle on a non-single result");
        }

        @Override
        public @Nullable Map.Entry<String, T> getKeyedSingle() {
            return getOptionalSingle().map(value -> Map.entry("1", value)).orElse(null);
        }

        @Override
        public T[] getArray() {
            return values.clone();
        }

        @Override
        public Map<String, T> getKeyed() {
            if (keyedMap != null) return keyedMap;
            keyedMap = new LinkedHashMap<>();
            for (int i = 0; i < values.length; i++)
                keyedMap.put((i + 1) + "", values[i]);
            return keyedMap;
        }

    }

    static final class Keyed<T> extends Result<T> {

        private final IntFunction<T[]> arrayGenerator;
        private final Map<String, T> values;
        private transient T[] arrayValues;

        private Keyed(IntFunction<T[]> arrayGenerator, Map<String, T> values) {
            this.arrayGenerator = arrayGenerator;
            this.values = values;
        }

        @Override
        public boolean isEmpty() {
            return values.isEmpty();
        }

        @Override
        public boolean isSingle() {
            return values.size() <= 1;
        }

        @Override
        public boolean isKeyed() {
            return true;
        }

        @Override
        public @Nullable T getSingle() {
            return Optional.ofNullable(getKeyedSingle()).map(Map.Entry::getValue).orElse(null);
        }

        @Override
        public @Nullable Map.Entry<String, T> getKeyedSingle() {
            if (isEmpty()) return null;
            if (isSingle()) return values.entrySet().iterator().next();
            throw new IllegalStateException("Cannot call Result#getSingle on a non-single result");
        }

        @Override
        public T[] getArray() {
            if (arrayValues != null) return arrayValues;
            arrayValues = arrayGenerator.apply(values.size());
            Iterator<T> iterator = values.values().iterator();
            int index = 0;
            while (iterator.hasNext())
                arrayValues[index++] = iterator.next();
            return arrayValues;
        }

        @Override
        public @Unmodifiable Map<String, T> getKeyed() {
            return Map.copyOf(values);
        }

    }

}
