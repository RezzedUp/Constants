package com.rezzedup.util.constants;

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
class TypeCompatibleTests
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
    void isDirectTypeCompatible()
    {
        SuperTypeToken<String> superString = new SuperTypeToken<>() {};
        TypeCompatible<String> compatibleString = superString::getType;
        TypeCapture<String> capturedString = compatibleString.capture();
        
        assertEquals(superString.getType(), capturedString.type());
    }
    
    @Test
    @DisplayName("is compatible with simple generic types")
    void isSimpleGenericTypeCompatible()
    {
        SuperTypeToken<List<String>> superList = new SuperTypeToken<>() {};
        TypeCompatible<List<String>> compatibleList = superList::getType;
        TypeCapture<List<String>> capturedList = compatibleList.capture();
        
        assertEquals(superList.getType(), capturedList.type());
    }
    
    @Test
    @DisplayName("is compatible with complex generic types")
    void isComplexGenericTypeCompatible()
    {
        SuperTypeToken<Map<Class<? extends Number>, BiConsumer<Collection<? super Number>, String>>> superMap = new SuperTypeToken<>() {};
        TypeCompatible<Map<Class<? extends Number>, BiConsumer<Collection<? super Number>, String>>> compatibleMap = superMap::getType;
        TypeCapture<Map<Class<? extends Number>, BiConsumer<Collection<? super Number>, String>>> capturedMap = compatibleMap.capture();
        
        assertEquals(superMap.getType(), capturedMap.type());
    }
}
