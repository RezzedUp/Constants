package com.rezzedup.util.constants;

import java.lang.reflect.Type;

@FunctionalInterface
public interface TypeCompatible<T>
{
    Type type();
    
    default TypeCapture<T> capture()
    {
        return new TypeCapture.Captured<>(type());
    }
}
