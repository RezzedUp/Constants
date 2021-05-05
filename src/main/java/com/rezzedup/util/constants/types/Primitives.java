package com.rezzedup.util.constants.types;

import com.rezzedup.util.constants.Aggregates;
import com.rezzedup.util.constants.annotations.Aggregated;
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
    
    @Aggregated.Result
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
