package com.rezzedup.util.constants.types;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class PrimitivesTests
{
    @Test
    public void testBoxedTypes()
    {
        // All 8 primitive wrappers are properly included
        assertEquals(8, Primitives.boxedTypes().size());
        
        // Ensure immutable
        assertThrows(RuntimeException.class, () -> Primitives.boxedTypes().add(Number.class));
    }
    
    @Test
    public void testAutoboxing()
    {
        assertTrue(Primitives.isBoxed((byte) 255));
        assertTrue(Primitives.isBoxed((short) 256));
        assertTrue(Primitives.isBoxed(5));
        assertTrue(Primitives.isBoxed(6L));
        assertTrue(Primitives.isBoxed(7.1F));
        assertTrue(Primitives.isBoxed(8.2D));
        assertTrue(Primitives.isBoxed(true));
        assertTrue(Primitives.isBoxed('a'));
        
        assertFalse(Primitives.isBoxed("a"));
        assertFalse(Primitives.isBoxed(null));
        assertFalse(Primitives.isBoxed(new Object()));
    }
}
