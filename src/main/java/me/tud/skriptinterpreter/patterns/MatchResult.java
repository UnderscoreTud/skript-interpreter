package me.tud.skriptinterpreter.patterns;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.function.Supplier;

public class MatchResult {

    private final String input;
    private final Map<Class<? extends Data>, DataContainer<?>> elementDataMap;

    private MatchResult(String input, Map<Class<? extends Data>, DataContainer<?>> elementDataMap) {
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

    private <D extends Data> D getData0(Class<D> dataClass) {
        List<D> container = getDataContainer(dataClass);
        if (container.size() > 1)
            throw new IllegalStateException("There's more than one instance of '" + dataClass + "'. Use MatchResult#getDataContainer(Class) instead");
        return container.isEmpty() ? null : container.get(0);
    }

    public <D extends Data> List<D> getDataContainer(Class<D> dataClass) {
        List<D> container = getDataContainer0(dataClass);
        return container != null ? container : new ArrayList<>();
    }

    @SuppressWarnings("unchecked")
    private @Nullable <D extends Data> List<D> getDataContainer0(Class<D> dataClass) {
        DataContainer<D> container = (DataContainer<D>) elementDataMap.get(dataClass);
        return container != null ? container.data : null;
    }

    @Contract(value = "_ -> new", pure = true)
    public static Builder fromBuilder(Builder builder) {
        return builder(builder.input);
    }

    @Contract(value = "_ -> new", pure = true)
    public static Builder builder(String input) {
        return new Builder(input);
    }

    public interface Data {}

    public static class DataContainer<T extends Data> {

        private final List<T> data;

        public DataContainer() {
            this(new ArrayList<>());
        }

        private DataContainer(List<T> data) {
            this.data = data;
        }

    }

    public static class Builder {

        private final String input;
        private final Map<Class<? extends Data>, DataContainer<?>> elementDataMap = new HashMap<>();

        private Builder(String input) {
            this.input = input;
        }

        public <T extends Data> T getOrCreateData(Class<T> dataClass, Supplier<T> dataFactory) {
            List<T> container = getContainer(dataClass);
            if (container.size() > 1)
                throw new IllegalStateException("There's more than one instance of '" + dataClass + "'. Use MatchResult$Builder#getContainer(Class) instead");
            if (container.size() == 1)
                return container.get(0);
            T data = dataFactory.get();
            container.add(data);
            return data;
        }

        @SuppressWarnings("unchecked")
        public void addData(Data data) {
            Objects.requireNonNull(data, "data");
            ((List<Data>) getContainer(data.getClass())).add(data);
        }

        @SuppressWarnings("unchecked")
        public <T extends Data> List<T> getContainer(Class<T> dataClass) {
            return (List<T>) elementDataMap.computeIfAbsent(dataClass, k -> new DataContainer<>()).data;
        }

        @SuppressWarnings("unchecked")
        public void combine(Builder other) {
            other.elementDataMap.forEach((dataClass, container) ->
                    ((List<Data>) getContainer(dataClass)).addAll(container.data));
        }

        public void clear() {
            elementDataMap.clear();
        }
        
        public MatchResult build() {
            return new MatchResult(input, elementDataMap);
        }

    }

}
