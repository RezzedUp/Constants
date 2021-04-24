package com.rezzedup.util.constants;

import java.lang.reflect.Type;

@FunctionalInterface
public interface TypeCompatible<T>
{
    Type type();
}
