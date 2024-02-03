package me.tud.skriptinterpreter.patterns;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.function.Supplier;

public class MatchResult {

    private final String input;
    private final Map<Class<? extends Data>, Data> elementDataMap;

    private MatchResult(String input, Map<Class<? extends Data>, Data> elementDataMap) {
        this.input = input;
        this.elementDataMap = elementDataMap;
    }

    public String input() {
        return input;
    }

    public <D extends Data> D getData(Class<D> dataClass) {
        D data = getData0(dataClass);
        if (data == null) throw new IllegalStateException("Data '" + dataClass + "' was not found");
        return data;
    }

    public <D extends Data> Optional<D> getOptionalData(Class<D> dataClass) {
        return Optional.ofNullable(getData0(dataClass));
    }

    @SuppressWarnings("unchecked")
    private <D extends Data> D getData0(Class<D> dataClass) {
        return (D) elementDataMap.get(dataClass);
    }

    @Contract(value = "_ -> new", pure = true)
    public static Builder fromBuilder(Builder builder) {
        return builder(builder.input);
    }

    @Contract(value = "_ -> new", pure = true)
    public static Builder builder(String input) {
        return new Builder(input);
    }

    public interface Data {

        void combine(Data other);

    }

    public static class Builder {

        private final String input;
        private final Map<Class<? extends Data>, Data> elementDataMap = new HashMap<>();

        private Builder(String input) {
            this.input = input;
        }

        @SuppressWarnings("unchecked")
        public <T extends Data> T getOrCreateData(Class<T> dataClass, Supplier<T> dataFactory) {
            return (T) elementDataMap.computeIfAbsent(dataClass, k -> dataFactory.get());
        }

        @SuppressWarnings("unchecked")
        public <T extends Data> @Nullable T getData(Class<T> dataClass) {
            return (T) elementDataMap.get(dataClass);
        }

        @SuppressWarnings({"unchecked", "rawtypes"})
        public void combine(Builder other) {
            other.elementDataMap.forEach((dataClass, otherData) -> {
                Data data = getData(dataClass);
                if (data == null) {
                    getOrCreateData((Class) dataClass, () -> otherData);
                    return;
                }
                data.combine(otherData);
            });
        }

        public void clear() {
            elementDataMap.clear();
        }
        
        public MatchResult build() {
            return new MatchResult(input, elementDataMap);
        }

    }

}
