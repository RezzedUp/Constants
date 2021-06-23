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
import java.util.ArrayList;
import java.util.List;

public class Constants
{
    private Constants() { throw new UnsupportedOperationException(); }
    
    public static boolean isConstant(@NullOr Field field)
    {
        return field != null
            && Modifier.isStatic(field.getModifiers())
            && Modifier.isFinal(field.getModifiers());
    }
    
    private static List<Field> constantsOf(Class<?> clazz, boolean accessibleOnly)
    {
        @NullOr List<Field> all = null;
        
        for (Field field : clazz.getDeclaredFields())
        {
            if (isConstant(field))
            {
                if (accessibleOnly && !field.canAccess(null)) { continue; }
                if (all == null) { all = new ArrayList<>(); }
                all.add(field);
            }
        }
        
        return (all == null) ? List.of() : List.copyOf(all);
    }
    
    public static List<Field> all(Class<?> clazz)
    {
        return constantsOf(clazz, false);
    }
    
    public static List<Field> accessible(Class<?> clazz)
    {
        return constantsOf(clazz, true);
    }
}
