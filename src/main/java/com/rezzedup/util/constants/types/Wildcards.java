package com.rezzedup.util.constants.types;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class Wildcards
{
    private Wildcards() { throw new UnsupportedOperationException(); }
    
    private static final TypeCapture<Class<?>> CLASS = new TypeCapture<>() {};
    
    private static final TypeCapture<List<?>> LIST = new TypeCapture<>() {};
    
    private static final TypeCapture<Set<?>> SET = new TypeCapture<>() {};
    
    private static final TypeCapture<Collection<?>> COLLECTION = new TypeCapture<>() {};
    
    private static final TypeCapture<Map<?, ?>> MAP = new TypeCapture<>() {};
    
    public static TypeCapture<Class<?>> classType() { return CLASS; }
    
    public static TypeCapture<List<?>> listType() { return LIST; }
    
    public static TypeCapture<Collection<?>> collectionType() { return COLLECTION; }
    
    public static TypeCapture<Map<?, ?>> mapType() { return MAP; }
}
