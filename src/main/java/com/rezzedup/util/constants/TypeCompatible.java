package com.rezzedup.util.constants;

import java.lang.reflect.Type;
import java.util.Optional;

@FunctionalInterface
public interface TypeCompatible<T>
{
    @SuppressWarnings("unchecked")
    static <T> Optional<T> unsafeRawTypeCast(TypeCompatible<T> type, Object object)
    {
        return (type.capture().raw().isAssignableFrom(object.getClass()))
            ? Optional.of((T) object)
            : Optional.empty();
    }
    
    Type type();
    
    default TypeCapture<T> capture()
    {
        return new TypeCapture.Captured<>(type());
    }
}
