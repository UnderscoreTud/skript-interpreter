package me.tud.skriptinterpreter.util;

import java.lang.reflect.Array;

public final class CollectionUtils {

    private CollectionUtils() {
        throw new UnsupportedOperationException();
    }

    @SuppressWarnings("unchecked")
    public static <T> T[] newArray(Class<T> componentType, int length) {
        return (T[]) Array.newInstance(componentType, length);
    }

    @SuppressWarnings("unchecked")
    public static <T> T[] newArray(Class<T> componentType, int... dimensions) {
        return (T[]) Array.newInstance(componentType, dimensions);
    }

}
