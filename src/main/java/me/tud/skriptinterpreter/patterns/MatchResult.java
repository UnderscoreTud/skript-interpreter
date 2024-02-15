package me.tud.skriptinterpreter.patterns;

import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.function.Supplier;

public class MatchResult {

    private final String input;
    private final DataContainer dataContainer;

    public MatchResult(String input, DataContainer dataContainer) {
        this.input = input;
        this.dataContainer = dataContainer;
    }

    public String input() {
        return input;
    }

    public <D extends Data> D getData(Class<D> dataClass) {
        D data = dataContainer.get(dataClass);
        if (data == null) throw new IllegalStateException("Data '" + dataClass + "' was not found");
        return data;
    }

    public <D extends Data> Optional<D> getOptionalData(Class<D> dataClass) {
        return Optional.ofNullable(dataContainer.get(dataClass));
    }

    public interface Data {

        void combine(Data other);

    }

    public static class DataContainer {

        private final @Nullable DataContainer parent;
        private final Map<Class<? extends Data>, Data> elementDataMap = new HashMap<>();

        public DataContainer() {
            this(null);
        }

        private DataContainer(@Nullable DataContainer parent) {
            this.parent = parent;
        }

        @SuppressWarnings("unchecked")
        public <T extends Data> T getOrCreate(Class<T> dataClass, Supplier<T> dataFactory) {
            return (T) elementDataMap.computeIfAbsent(dataClass, k -> dataFactory.get());
        }

        @SuppressWarnings("unchecked")
        public <T extends Data> @Nullable T get(Class<T> dataClass) {
            return (T) elementDataMap.get(dataClass);
        }

        @SuppressWarnings({"unchecked", "rawtypes"})
        public void combine(DataContainer other) {
            other.elementDataMap.forEach((dataClass, otherData) -> {
                Data data = get(dataClass);
                if (data == null) {
                    getOrCreate((Class) dataClass, () -> otherData);
                    return;
                }
                data.combine(otherData);
            });
        }

        public void clear() {
            elementDataMap.clear();
        }
        
        public DataContainer child() {
            return new DataContainer(this);
        }

        public @Nullable DataContainer parent() {
            return parent;
        }

        public DataContainer root() {
            DataContainer current = this;
            while (current.parent != null)
                current = current.parent;
            return current;
        }

    }

}
