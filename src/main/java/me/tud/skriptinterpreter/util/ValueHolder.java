package me.tud.skriptinterpreter.util;

import java.util.Optional;

public interface ValueHolder<T> {

    T get();

    default Optional<T> getOptional() {
        return Optional.ofNullable(get());
    }

    void set(T t);

}
