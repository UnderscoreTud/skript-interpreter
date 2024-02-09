package me.tud.skriptinterpreter.registration;

import me.tud.skriptinterpreter.Skript;
import me.tud.skriptinterpreter.types.Type;
import me.tud.skriptinterpreter.types.TypeResult;

import java.util.*;

class TypeRegistryImpl implements TypeRegistry {

    private final Skript skript;
    private final Map<Class<?>, Type<?>> types = new HashMap<>();
    private final Map<Class<?>, Type<?>> typesCache = new HashMap<>();
    private final Map<String, Type<?>> codename2Type = new HashMap<>();
    private final Map<String, Type<?>> pluralCodename2Type = new HashMap<>();

    TypeRegistryImpl(Skript skript) {
        this.skript = skript;
    }

    @Override
    public boolean register(Type<?> type) {
        Objects.requireNonNull(type, "type cannot be null");
        if (getByExactClass(type.underlyingClass()).isPresent()) return false;
        types.put(type.underlyingClass(), type);
        codename2Type.put(type.codename(), type);
        pluralCodename2Type.put(type.codename(true), type);
        return true;
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T> Optional<Type<? super T>> getByClass(Class<T> type) {
        if (type == null) return Optional.empty();
        return Optional.ofNullable((Type<? super T>) typesCache.computeIfAbsent(type, k -> {
            Class<? super T> current = type;
            while (current != null) {
                Type<? super T> superType = getByExactClass(current).orElse(null);
                if (superType != null) return superType;
                current = current.getSuperclass();
            }
            return null;
        }));
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T> Optional<Type<T>> getByExactClass(Class<T> type) {
        if (type == null) return Optional.empty();
        return Optional.ofNullable((Type<T>) types.get(type));
    }

    @Override
    public Optional<TypeResult<?>> getByCodename(String codename) {
        if (codename == null) return Optional.empty();
        Type<?> type = codename2Type.get(codename);
        boolean plural = false;
        if (type == null) {
            type = pluralCodename2Type.get(codename);
            plural = true;
        }
        if (type == null) return Optional.empty();
        return Optional.of(new TypeResult<>(type, plural, codename));
    }

    @Override
    public Optional<TypeResult<?>> parseInput(String input) {
        if (input == null) return Optional.empty();
        for (Type<?> type : types()) {
            for (Map.Entry<String, String> entry : type.names().entrySet()) {
                if (input.equals(entry.getKey())) return Optional.of(new TypeResult<>(type, false, input));
                if (input.equals(entry.getValue())) return Optional.of(new TypeResult<>(type, true, input));
            }
        }
        return Optional.empty();
    }

    @Override
    public Collection<Type<?>> types() {
        return Collections.unmodifiableCollection(types.values());
    }

    @Override
    public Map<Class<?>, Type<?>> mapView() {
        return Collections.unmodifiableMap(types);
    }

    @Override
    public Skript skript() {
        return skript;
    }

}
