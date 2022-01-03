/*
 * Copyright © 2021-2022, RezzedUp <https://github.com/RezzedUp/Constants>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package com.rezzedup.util.constants.types;

import pl.tlinkowski.annotation.basic.NullOr;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.function.BiConsumer;

/**
 * Constants and utilities for boxed primitives.
 */
public class Primitives
{
    private Primitives() { throw new UnsupportedOperationException(); }
    
    private static final Map<Class<?>, Class<?>> PRIMITIVE_TO_BOX;
    private static final Map<Class<?>, Class<?>> BOX_TO_PRIMITIVE;
    
    static
    {
        Map<Class<?>, Class<?>> boxToPrimitive = new HashMap<>();
        Map<Class<?>, Class<?>> primitiveToBox = new HashMap<>();
        
        BiConsumer<Class<?>, Class<?>> types = (primitive, box) -> {
            primitiveToBox.put(primitive, box);
            boxToPrimitive.put(box, primitive);
        };
        
        types.accept(byte.class, Byte.class);
        types.accept(short.class, Short.class);
        types.accept(int.class, Integer.class);
        types.accept(long.class, Long.class);
        types.accept(float.class, Float.class);
        types.accept(double.class, Double.class);
        types.accept(boolean.class, Boolean.class);
        types.accept(char.class, Character.class);
        
        PRIMITIVE_TO_BOX = Map.copyOf(primitiveToBox);
        BOX_TO_PRIMITIVE = Map.copyOf(boxToPrimitive);
    }
    
    /**
     * Gets all directly-primitive types. These are unboxed, purely-primitive types.
     *
     * @return an immutable set containing all directly-primitive types
     * @see #boxedTypes()
     */
    public static Set<Class<?>> unboxedPrimitiveTypes()
    {
        return PRIMITIVE_TO_BOX.keySet();
    }
    
    /**
     * Gets all boxed primitive types.
     *
     * @return an immutable set containing all boxed primitive types
     */
    public static Set<Class<?>> boxedTypes()
    {
        return BOX_TO_PRIMITIVE.keySet();
    }
    
    /**
     * Checks if an object is an instance of a boxed primitive.
     *
     * @param object    the object
     * @return {@code true} if the object isn't null and its class is a boxed primitive type, otherwise {@code false}
     */
    public static boolean isBoxed(@NullOr Object object)
    {
        return object != null && BOX_TO_PRIMITIVE.containsKey(object.getClass());
    }
}
