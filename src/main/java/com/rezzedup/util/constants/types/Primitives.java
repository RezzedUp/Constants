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
        Aggregates.set(Primitives.class, Wildcards.classType(), Aggregates.matching().collections(true));
    
    public static Set<Class<?>> boxedTypes()
    {
        return BOXES;
    }
    
    public static boolean isBoxed(@NullOr Object object)
    {
        return object != null && boxedTypes().contains(object.getClass());
    }
}
