package me.tud.skriptinterpreter.registration;

import me.tud.skriptinterpreter.Skript;
import me.tud.skriptinterpreter.SkriptProperty;
import me.tud.skriptinterpreter.types.Type;
import me.tud.skriptinterpreter.types.TypeResult;

import java.util.Collection;
import java.util.Map;
import java.util.Optional;

public interface TypeRegistry extends SkriptProperty {

    boolean register(Type<?> type);

    <T> Optional<Type<? super T>> getByClass(Class<T> type);

    <T> Optional<Type<T>> getByExactClass(Class<T> type);

    Optional<TypeResult<?>> getByCodename(String codename);

    Optional<TypeResult<?>> parseInput(String input);

    Collection<Type<?>> types();

    Map<Class<?>, Type<?>> mapView();

    static TypeRegistry create(Skript skript) {
        return new TypeRegistryImpl(skript);
    }
    
}
