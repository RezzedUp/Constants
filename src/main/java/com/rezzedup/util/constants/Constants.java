/*
 * Copyright © 2021, RezzedUp <https://github.com/RezzedUp/Constants>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package com.rezzedup.util.constants;

import pl.tlinkowski.annotation.basic.NullOr;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Constants
{
    private Constants() { throw new UnsupportedOperationException(); }
    
    public static boolean isConstant(@NullOr Field field)
    {
        return field != null
            && Modifier.isStatic(field.getModifiers())
            && Modifier.isFinal(field.getModifiers());
    }
    
    public static Stream<Field> streamAll(Class<?> clazz)
    {
        Objects.requireNonNull(clazz, "clazz");
        return Arrays.stream(clazz.getDeclaredFields()).filter(Constants::isConstant);
    }
    
    public static Stream<Field> streamAccessible(Class<?> clazz)
    {
        return streamAll(clazz).filter(field -> field.canAccess(null));
    }
    
    private static List<Field> list(Stream<Field> stream)
    {
        return List.copyOf(stream.collect(Collectors.toList()));
    }
    
    public static List<Field> listAll(Class<?> clazz)
    {
        return list(streamAll(clazz));
    }
    
    public static List<Field> listAccessible(Class<?> clazz)
    {
        return list(streamAccessible(clazz));
    }
}
