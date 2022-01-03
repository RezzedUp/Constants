/*
 * Copyright © 2021-2022, RezzedUp <https://github.com/RezzedUp/Constants>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package com.rezzedup.util.constants.types;

import pl.tlinkowski.annotation.basic.NullOr;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.lang.reflect.WildcardType;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * A so-called "super" type token capable of capturing generic type information.
 *
 * <p>In order to capture generic types, this class <b>must</b> be extended with explicit type parameters.
 * This should typically be done by instantiating an anonymous subclass and assigning it to a constant.</p>
 *
 * <p>For example:</p>
 *
 * <pre>{@code
 * public static final TypeCapture<List<String>> STRING_LIST_TYPE = new TypeCapture<>() {};
 * }</pre>
 *
 * @param <T>   the type
 */
public abstract class TypeCapture<T> implements TypeCompatible<T>
{
    private static @NullOr TypeCapture<Object> ANY;
    
    /**
     * Gets the constant {@code Object} type capture.
     *
     * @return object class type capture
     */
    public static TypeCapture<Object> any()
    {
        if (ANY == null) { ANY = new Captured<>(Object.class); }
        return ANY;
    }
    
    @SuppressWarnings("unchecked")
    private static <T> TypeCapture<T> anything()
    {
        return (TypeCapture<T>) any();
    }
    
    /**
     * Captures the type directly from a non-generic class.
     *
     * @param type  the class
     * @param <T>   non-generic type
     *
     * @return the captured type
     */
    public static <T> TypeCapture<T> type(Class<T> type)
    {
        if (type == Object.class) { return anything(); }
        return new Captured<>(type);
    }
    
    /**
     * Captures an unknown type.
     *
     * @param type  the unknown type
     *
     * @return the captured type
     */
    public static TypeCapture<?> type(Type type)
    {
        if (type == Object.class) { return anything(); }
        return new Captured<>(type);
    }
    
    /**
     * Converts an alternative compatible type token into a type captures.
     *
     * @param type  the compatible type token
     * @param <T>   generic type
     *
     * @return the captured type
     */
    public static <T> TypeCapture<T> type(TypeCompatible<T> type)
    {
        if (type instanceof TypeCapture) { return (TypeCapture<T>) type; }
        if (type.type() == Object.class) { return anything(); }
        return new Captured<>(type.type());
    }
    
    private final Type type;
    private final Class<? super T> raw;
    private final List<TypeCapture<?>> generics;
    
    TypeCapture(Type type)
    {
        Objects.requireNonNull(type, "type");
        this.type = type;
        this.raw = resolveRawType(type);
        this.generics = resolveGenericParameters(type);
    }
    
    /**
     * Automatically retrieves type information based on reified generic types (only applicable for
     * subclasses with <b>explicitly declared</b> type parameters).
     */
    public TypeCapture()
    {
        Type superclass = getClass().getGenericSuperclass();
        this.type = ((ParameterizedType) superclass).getActualTypeArguments()[0];
        this.raw = resolveRawType(type);
        this.generics = resolveGenericParameters(type);
    }
    
    @Override
    public final Type type() { return type; }
    
    /**
     * Gets the "raw" type, or, in other words: its direct, non-generic class.
     *
     * @return the class representing this type
     */
    public final Class<? super T> raw() { return raw; }
    
    /**
     * Gets the captured generic type parameters.
     *
     * @return an immutable list of captured generic type parameters, otherwise empty
     */
    public final List<TypeCapture<?>> generics() { return generics; }
    
    /**
     * Checks if generic type parameters are captured or not.
     *
     * @return {@code true} if generic type parameters are captured, otherwise {@code false}
     */
    public final boolean isGeneric() { return !generics.isEmpty(); }
    
    /**
     * Checks if the captured type is a wildcard ({@code ?}) or not.
     *
     * @return {@code true} if the captured type is an instance of {@link WildcardType}, otherwise {@code false}
     */
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
            && generics.equals(that.generics);
    }
    
    @Override
    public final int hashCode() { return type.hashCode(); }
    
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
        throw new IllegalArgumentException(
            "Unsupported type: " + type + " (" + type.getClass().getName() + ")"
        );
    }
    
    private static List<TypeCapture<?>> resolveGenericParameters(Type type)
    {
        if (!(type instanceof ParameterizedType)) { return List.of(); }
        
        ParameterizedType generic = (ParameterizedType) type;
        Type[] parameters = generic.getActualTypeArguments();
        
        if (parameters.length <= 0) { return List.of(); }
        
        List<TypeCapture<?>> resolved =
        Arrays.stream(parameters).map(TypeCapture::type).collect(Collectors.toList());
        
        return List.copyOf(resolved);
    }
    
    private static final class Captured<T> extends TypeCapture<T>
    {
        Captured(Type type) { super(type); }
    }
}
