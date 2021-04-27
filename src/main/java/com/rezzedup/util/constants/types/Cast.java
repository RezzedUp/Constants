package com.rezzedup.util.constants.types;

import pl.tlinkowski.annotation.basic.NullOr;

import java.util.Objects;
import java.util.Optional;
import java.util.function.Function;

public class Cast
{
    private Cast() { throw new UnsupportedOperationException(); }
    
    @SuppressWarnings("unchecked")
    public static <T> Optional<T> as(Class<T> type, @NullOr Object object)
    {
        Objects.requireNonNull(type, "type");
        return Optional.ofNullable(object)
            .filter(o -> type.isAssignableFrom(o.getClass()))
            .map(o -> (T) o);
    }
    
    public static <T> Function<@NullOr Object, Optional<T>> as(Class<T> type)
    {
        Objects.requireNonNull(type, "type");
        return object -> as(type, object);
    }
    
    @SuppressWarnings("unchecked")
    public static <T> Optional<T> unsafe(TypeCompatible<T> type, @NullOr Object object)
    {
        Class<?> raw = TypeCapture.type(type).raw();
        return (Optional<T>) as(raw, object);
    }
    
    public static <T> Function<@NullOr Object, Optional<T>> unsafe(TypeCompatible<T> type)
    {
        Objects.requireNonNull(type, "type");
        return object -> unsafe(type, object);
    }
}
