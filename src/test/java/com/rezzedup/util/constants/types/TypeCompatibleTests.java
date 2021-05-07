/*
 * Copyright © 2021, RezzedUp <https://github.com/RezzedUp/Constants>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package com.rezzedup.util.constants.types;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.function.BiConsumer;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("TypeCompatible")
public class TypeCompatibleTests
{
    // Basic, barebones type token
    static abstract class SuperTypeToken<T>
    {
        final Type type;
        
        SuperTypeToken()
        {
            Type superclass = getClass().getGenericSuperclass();
            this.type = ((ParameterizedType) superclass).getActualTypeArguments()[0];
        }
        
        Type getType() { return type; }
    }
    
    @Test
    @DisplayName("is compatible with direct types")
    public void isDirectTypeCompatible()
    {
        SuperTypeToken<String> superString = new SuperTypeToken<>() {};
        TypeCompatible<String> compatibleString = superString::getType;
        TypeCapture<String> capturedString = TypeCapture.type(compatibleString);
        
        assertEquals(superString.getType(), capturedString.type());
    }
    
    @Test
    @DisplayName("is compatible with simple generic types")
    public void isSimpleGenericTypeCompatible()
    {
        SuperTypeToken<List<String>> superList = new SuperTypeToken<>() {};
        TypeCompatible<List<String>> compatibleList = superList::getType;
        TypeCapture<List<String>> capturedList = TypeCapture.type(compatibleList);
        
        assertEquals(superList.getType(), capturedList.type());
    }
    
    @Test
    @DisplayName("is compatible with complex generic types")
    public void isComplexGenericTypeCompatible()
    {
        SuperTypeToken<Map<Class<? extends Number>, BiConsumer<Collection<? super Number>, String>>> superMap = new SuperTypeToken<>() {};
        TypeCompatible<Map<Class<? extends Number>, BiConsumer<Collection<? super Number>, String>>> compatibleMap = superMap::getType;
        TypeCapture<Map<Class<? extends Number>, BiConsumer<Collection<? super Number>, String>>> capturedMap = TypeCapture.type(compatibleMap);
        
        assertEquals(superMap.getType(), capturedMap.type());
    }
}
