package com.rezzedup.util.constants;

import pl.tlinkowski.annotation.basic.NullOr;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.lang.reflect.WildcardType;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public abstract class TypeCapture<T>
{
    public static <T> TypeCapture<T> type(Class<T> type)
    {
        return new Captured<>(type);
    }
    
    public static TypeCapture<?> type(Type type)
    {
        return new Captured<>(type);
    }
    
    private final Type type;
    private final Class<? super T> raw;
    private final List<TypeCapture<?>> generics;
    private final int hashCode;
    
    protected TypeCapture(Type type)
    {
        Objects.requireNonNull(type, "type");
        this.type = type;
        this.raw = resolveRawType(type);
        this.generics = resolveGenericParameters(type);
        this.hashCode = type.hashCode();
    }
    
    public TypeCapture()
    {
        Type superclass = getClass().getGenericSuperclass();
        this.type = ((ParameterizedType) superclass).getActualTypeArguments()[0];
        this.raw = resolveRawType(type);
        this.generics = resolveGenericParameters(type);
        this.hashCode = type.hashCode();
    }
    
    public final Type type() { return type; }
    
    public final Class<? super T> raw() { return raw; }
    
    public final List<TypeCapture<?>> generics() { return generics; }
    
    public final boolean isGeneric() { return !generics.isEmpty(); }
    
    public final boolean isWildcard() { return type instanceof WildcardType; }
    
    @Override
    public final String toString() { return type.getTypeName(); }
    
    @Override
    public final boolean equals(@NullOr Object o)
    {
        if (this == o) { return true; }
        if (!(o instanceof TypeCapture)) { return false; }
        
        TypeCapture<?> that = (TypeCapture<?>) o;
        
        return type.equals(that.type)
            && raw.equals(that.raw)
            && generics.equals(that.generics)
            && hashCode == that.hashCode;
    }
    
    @Override
    public final int hashCode() { return hashCode; }
    
    @SuppressWarnings("unchecked")
    private static <T> Class<? super T> resolveRawType(Type type)
    {
        if (type instanceof Class)
        {
            return (Class<? super T>) type;
        }
        else if (type instanceof ParameterizedType)
        {
            ParameterizedType generic = (ParameterizedType) type;
            return (Class<? super T>) generic.getRawType();
        }
        else if (type instanceof WildcardType)
        {
            WildcardType wildcard = (WildcardType) type;
            return resolveRawType(wildcard.getUpperBounds()[0]);
        }
        
        // All else fails.
        throw new IllegalStateException(
            "Unsupported type: " + type + " (" + type.getClass().getName() + ")"
        );
    }
    
    private static List<TypeCapture<?>> resolveGenericParameters(Type type)
    {
        if (!(type instanceof ParameterizedType)) { return List.of(); }
        
        ParameterizedType generic = (ParameterizedType) type;
        Type[] parameters = generic.getActualTypeArguments();
        
        if (parameters.length <= 0) { return List.of(); }
        
        List<TypeCapture<?>> resolved = Arrays.stream(parameters).map(Captured::new).collect(Collectors.toList());
        return List.copyOf(resolved);
    }
    
    private static final class Captured<T> extends TypeCapture<T>
    {
        Captured(Type type) { super(type); }
    }
}
