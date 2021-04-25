/*
 * Copyright © 2021, RezzedUp <https://github.com/RezzedUp/Constants>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package com.rezzedup.util.constants;

import com.rezzedup.util.constants.annotations.Aggregated;
import com.rezzedup.util.constants.exceptions.AggregationException;
import pl.tlinkowski.annotation.basic.NullOr;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.function.Supplier;

public class Aggregates
{
    private Aggregates() { throw new UnsupportedOperationException(); }
    
    private static final MatchRules ALL = new MatchRules();
    
    public static MatchRules matching() { return ALL; }
    
    private static boolean fieldHasSkipAnnotation(Field field)
    {
        return field.isAnnotationPresent(Aggregated.class)
            || field.isAnnotationPresent(Aggregated.Result.class)
            || field.isAnnotationPresent(Aggregated.Skip.class);
    }
    
    public static <T> void visit(Class<?> source, TypeCompatible<T> type, MatchRules rules, BiConsumer<String, T> consumer)
    {
        Objects.requireNonNull(source, "source");
        Objects.requireNonNull(type, "type");
        Objects.requireNonNull(rules, "rules");
        Objects.requireNonNull(consumer, "consumer");
        
        TypeCapture<T> capture = TypeCapture.type(type);
        
        for (Field field : source.getDeclaredFields())
        {
            if (!Modifier.isStatic(field.getModifiers())) { continue; }
            if (!rules.matches(field.getName())) { continue; }
            if (fieldHasSkipAnnotation(field)) { continue; }
            
            field.setAccessible(true);
            
            try
            {
                @NullOr Object value = field.get(null);
                if (value == null) { continue; }
                
                if (value instanceof Collection && rules.isVisitingCollectionsAllowed())
                {
                    ((Collection<?>) value).stream()
                        .flatMap(element -> TypeCapture.unsafeRawTypeCast(capture, element).stream())
                        .forEach(element -> consumer.accept(field.getName(), element));
                }
                else
                {
                    TypeCapture.unsafeRawTypeCast(capture, value)
                        .ifPresent(element -> consumer.accept(field.getName(), element));
                }
            }
            catch (IllegalAccessException e) { throw new AggregationException(e); }
        }
    }
    
    private static <T, C extends Collection<T>> C collect(Class<?> source, TypeCompatible<T> type, MatchRules rules, Supplier<C> constructor)
    {
        Objects.requireNonNull(constructor, "constructor");
        C collection = Objects.requireNonNull(constructor.get(), "constructor returned null");
        visit(source, type, rules, (name, element) -> collection.add(element));
        return collection;
    }
    
    public static <T> Set<T> set(Class<?> source, TypeCompatible<T> type, MatchRules rules, Supplier<Set<T>> constructor)
    {
        return Set.copyOf(collect(source, type, rules, constructor));
    }
    
    public static <T> Set<T> set(Class<?> source, TypeCompatible<T> type, MatchRules rules)
    {
        return set(source, type, rules, HashSet::new);
    }
    
    public static <T> Set<T> set(Class<?> source, TypeCompatible<T> type)
    {
        return set(source, type, ALL);
    }
    
    public static <T> List<T> list(Class<?> source, TypeCompatible<T> type, MatchRules rules, Supplier<List<T>> constructor)
    {
        return List.copyOf(collect(source, type, rules, constructor));
    }
    
    public static <T> List<T> list(Class<?> source, TypeCompatible<T> type, MatchRules rules)
    {
        return list(source, type, rules, ArrayList::new);
    }
    
    public static <T> List<T> list(Class<?> source, TypeCompatible<T> type)
    {
        return list(source, type, ALL);
    }
    
    public static class MatchRules
    {
        private final Set<String> all;
        private final Set<String> any;
        private final Set<String> not;
        private final boolean collections;
    
        private MatchRules(Set<String> all, Set<String> any, Set<String> not, boolean collections)
        {
            this.all = Set.copyOf(all);
            this.any = Set.copyOf(any);
            this.not = Set.copyOf(not);
            this.collections = collections;
        }
        
        private MatchRules()
        {
            this(Set.of(), Set.of(), Set.of(), true);
        }
        
        public MatchRules all(String ... required)
        {
            if (required.length <= 0) { return this; }
            Set<String> allModified = new HashSet<>(all);
            Collections.addAll(allModified, required);
            return new MatchRules(allModified, any, not, collections);
        }
        
        public MatchRules any(String ... optional)
        {
            if (optional.length <= 0) { return this; }
            Set<String> anyModified = new HashSet<>(any);
            Collections.addAll(anyModified, optional);
            return new MatchRules(all, anyModified, not, collections);
        }
        
        public MatchRules not(String ... excluded)
        {
            if (excluded.length <= 0) { return this; }
            Set<String> notModified = new HashSet<>(not);
            Collections.addAll(notModified, excluded);
            return new MatchRules(all, any, notModified, collections);
        }
        
        public MatchRules includingCollections()
        {
            if (collections) { return this; }
            return new MatchRules(all, any, not, true);
        }
        
        public MatchRules skippingCollections()
        {
            if (!collections) { return this; }
            return new MatchRules(all, any, not, false);
        }
        
        public boolean matches(String name)
        {
            return (all.isEmpty() || all.stream().allMatch(name::contains))
                && (any.isEmpty() || any.stream().anyMatch(name::contains))
                && (not.isEmpty() || not.stream().noneMatch(name::contains));
        }
        
        public boolean isVisitingCollectionsAllowed() { return collections; }
    }
}