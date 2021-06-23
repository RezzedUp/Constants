/*
 * Copyright © 2021, RezzedUp <https://github.com/RezzedUp/Constants>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package com.rezzedup.util.constants.types;

import com.rezzedup.util.constants.Aggregates;
import com.rezzedup.util.constants.annotations.AggregatedResult;
import pl.tlinkowski.annotation.basic.NullOr;

import java.util.Set;

/**
 * Constants and utilities for boxed primitives.
 */
public class Primitives
{
    private Primitives() { throw new UnsupportedOperationException(); }
    
    private static final Set<Class<? extends Number>> NUMBERS =
        Set.of(
            Byte.class, Short.class, Integer.class,
            Long.class, Float.class, Double.class
        );
    
    private static final Class<Boolean> BOOLEAN = Boolean.class;
    
    private static final Class<Character> CHARACTER = Character.class;
    
    @AggregatedResult
    private static final Set<Class<?>> BOXES =
        Aggregates.set(Primitives.class, Wildcards.CLASS, Aggregates.matching().collections(true));
    
    /**
     * Gets all boxed primitive types.
     *
     * @return  an immutable {@code Set} containing all
     *          boxed primitive types
     */
    public static Set<Class<?>> boxedTypes()
    {
        return BOXES;
    }
    
    /**
     * Checks if an object is an instance of a
     * boxed primitive.
     *
     * @param object    the object
     *
     * @return  {@code true} if the object isn't null and
     *          its class is a boxed primitive type,
     *          otherwise {@code false}
     */
    public static boolean isBoxed(@NullOr Object object)
    {
        return object != null && boxedTypes().contains(object.getClass());
    }
}
