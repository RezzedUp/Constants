/*
 * Copyright © 2021, RezzedUp <https://github.com/RezzedUp/Constants>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package com.rezzedup.util.constants;

import com.rezzedup.util.constants.annotations.AggregatedResult;
import com.rezzedup.util.constants.annotations.NotAggregated;
import com.rezzedup.util.constants.exceptions.AggregationException;
import com.rezzedup.util.constants.types.Cast;
import com.rezzedup.util.constants.types.TypeCapture;
import com.rezzedup.util.constants.types.TypeCompatible;
import pl.tlinkowski.annotation.basic.NullOr;

import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.function.Supplier;

/**
 * Aggregates constants.
 */
public class Aggregates
{
    private Aggregates() { throw new UnsupportedOperationException(); }
    
    private static final MatchRules ALL = new MatchRules();
    
    private static final Set<Class<? extends Annotation>> SKIP_ANNOTATIONS =
        Set.of(AggregatedResult.class, NotAggregated.class);
    
    /**
     * Specifies criteria for filtering constants
     * based on their name and other settings. By default,
     * the rules will match all names but won't visit
     * the contents of constant collections.
     *
     * <p>It should be noted that since all {@link MatchRules}
     * instances are immutable, the same default instance
     * is always returned by this method. Any additional
     * criteria will construct entirely new instances.</p>
     *
     * @return  the default immutable rules instance
     */
    public static MatchRules matching() { return ALL; }
    
    /**
     * Visits all constants of a specific
     * type matching the provided rules.
     *
     * @param source    the class containing constants
     * @param type      type token of the type
     * @param rules     criteria for filtering constants
     * @param consumer  constant consumer
     * @param <T>       the type
     */
    public static <T> void visit(Class<?> source, TypeCompatible<T> type, MatchRules rules, BiConsumer<String, T> consumer)
    {
        Objects.requireNonNull(source, "source");
        Objects.requireNonNull(type, "type");
        Objects.requireNonNull(rules, "rules");
        Objects.requireNonNull(consumer, "consumer");
        
        TypeCapture<T> capture = TypeCapture.type(type);
        
        Constants.all(source)
            .filter(field -> rules.matches(field.getName()))
            .filter(field -> SKIP_ANNOTATIONS.stream().noneMatch(field::isAnnotationPresent))
            .forEach(field ->
            {
                field.setAccessible(true);
                
                try
                {
                    @NullOr Object value = field.get(null);
                    if (value == null) { return; }
                    
                    if (value instanceof Collection && rules.isAggregatingFromCollections())
                    {
                        ((Collection<?>) value).stream()
                            .flatMap(element -> Cast.unsafe().generic(capture, element).stream())
                            .forEach(element -> consumer.accept(field.getName(), element));
                    }
                    else
                    {
                        Cast.unsafe().generic(capture, value)
                            .ifPresent(element -> consumer.accept(field.getName(), element));
                    }
                }
                catch (Exception e) { throw new AggregationException(e); }
            });
    }
    
    private static <T, C extends Collection<T>> C collect(Class<?> source, TypeCompatible<T> type, MatchRules rules, Supplier<C> constructor)
    {
        Objects.requireNonNull(constructor, "constructor");
        C collection = Objects.requireNonNull(constructor.get(), "constructor returned null");
        visit(source, type, rules, (name, element) -> collection.add(element));
        return collection;
    }
    
    /**
     * Collects all constants of a specific type matching the
     * provided rules into an immutable {@code Set}.
     *
     * @param source        the class containing constants
     * @param type          type token of the type
     * @param rules         criteria for filtering constants
     * @param constructor   new {@code Set} constructor
     * @param <T>           the type
     *
     * @return  an immutable copy of the constructed {@code Set}
     *          containing the matched constants
     */
    public static <T> Set<T> set(Class<?> source, TypeCompatible<T> type, MatchRules rules, Supplier<Set<T>> constructor)
    {
        return Set.copyOf(collect(source, type, rules, constructor));
    }
    
    /**
     * Collects all constants of a specific type matching the
     * provided rules into an immutable {@code Set}.
     *
     * @param source    the class containing constants
     * @param type      type token of the type
     * @param rules     criteria for filtering constants
     * @param <T>       the type
     *
     * @return  an immutable {@code Set} containing
     *          the matched constants
     */
    public static <T> Set<T> set(Class<?> source, TypeCompatible<T> type, MatchRules rules)
    {
        return set(source, type, rules, HashSet::new);
    }
    
    /**
     * Collects all constants of a specific type into an
     * immutable {@code Set}. Constants within collections
     * will <b>not</b> be aggregated.
     *
     * @param source    the class containing constants
     * @param type      type token of the type
     * @param <T>       the type
     *
     * @return  an immutable {@code Set} containing
     *          the matched constants
     */
    public static <T> Set<T> set(Class<?> source, TypeCompatible<T> type)
    {
        return set(source, type, ALL);
    }
    
    /**
     * Collects all constants of a specific type matching the
     * provided rules into an immutable {@code List}.
     *
     * @param source        the class containing constants
     * @param type          type token of the type
     * @param rules         criteria for filtering constants
     * @param constructor   new {@code List} constructor
     * @param <T>           the type
     *
     * @return  an immutable copy of the constructed {@code List}
     *          containing the matched constants
     */
    public static <T> List<T> list(Class<?> source, TypeCompatible<T> type, MatchRules rules, Supplier<List<T>> constructor)
    {
        return List.copyOf(collect(source, type, rules, constructor));
    }
    
    /**
     * Collects all constants of a specific type matching the
     * provided rules into an immutable {@code List}.
     *
     * @param source    the class containing constants
     * @param type      type token of the type
     * @param rules     criteria for filtering constants
     * @param <T>       the type
     *
     * @return  an immutable {@code List} containing
     *          the matched constants
     */
    public static <T> List<T> list(Class<?> source, TypeCompatible<T> type, MatchRules rules)
    {
        return list(source, type, rules, ArrayList::new);
    }
    
    /**
     * Collects all constants of a specific type into an
     * immutable {@code List}. Constants within collections
     * will <b>not</b> be aggregated.
     *
     * @param source    the class containing constants
     * @param type      type token of the type
     * @param <T>       the type
     *
     * @return  an immutable {@code List} containing
     *          the matched constants
     */
    public static <T> List<T> list(Class<?> source, TypeCompatible<T> type)
    {
        return list(source, type, ALL);
    }
    
    /**
     * Immutable criteria for filtering constants based on
     * their name and other settings for aggregation.
     */
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
            this(Set.of(), Set.of(), Set.of(), false);
        }
    
        /**
         * Appends required strings to the existing rules.
         * A constant will only match this rule if its name
         * contains <b>all</b> of the specified strings.
         *
         * @param required  all the strings a constant name
         *                  must contain in order to match
         *
         * @return  new instance containing the amended rules
         *          or itself if no new rules are specified
         */
        public MatchRules all(String ... required)
        {
            if (required.length <= 0) { return this; }
            Set<String> allModified = new HashSet<>(all);
            Collections.addAll(allModified, required);
            return new MatchRules(allModified, any, not, collections);
        }
    
        /**
         * Appends optional strings to the existing rules.
         * A constant will only match this rule if its name
         * contains <b>any</b> (at least one) of the
         * specified strings.
         *
         * @param optional  strings a constant name is expected
         *                  to contain at least one of in order
         *                  to match
         *
         * @return  new instance containing the amended rules
         *          or itself if no new rules are specified
         */
        public MatchRules any(String ... optional)
        {
            if (optional.length <= 0) { return this; }
            Set<String> anyModified = new HashSet<>(any);
            Collections.addAll(anyModified, optional);
            return new MatchRules(all, anyModified, not, collections);
        }
    
        /**
         * Appends excluded strings to the existing rules.
         * A constant will only match this rule if its name
         * contains <b>none</b> of the specified strings.
         *
         * @param excluded  all the strings a constant name
         *                  must not contain in order to match
         *
         * @return  new instance containing the amended rules
         *          or itself if no new rules are specified
         */
        public MatchRules not(String ... excluded)
        {
            if (excluded.length <= 0) { return this; }
            Set<String> notModified = new HashSet<>(not);
            Collections.addAll(notModified, excluded);
            return new MatchRules(all, any, notModified, collections);
        }
    
        /**
         * Sets whether or not the contents of constant
         * collections should be aggregated.
         *
         * @param visit     {@code true} if collections should
         *                  be visited or {@code false} if
         *                  they should not
         *
         * @return  new instance containing the amended rules
         *          or itself if no new rules are specified
         */
        public MatchRules collections(boolean visit)
        {
            if (collections == visit) { return this; }
            return new MatchRules(all, any, not, visit);
        }
    
        /**
         * Checks if the provided name matches the
         * criteria contained within these rules.
         *
         * @param name  the name to check
         *
         * @return  {@code true} if the name matches,
         *          otherwise {@code false}
         */
        public boolean matches(String name)
        {
            return (all.isEmpty() || all.stream().allMatch(name::contains))
                && (any.isEmpty() || any.stream().anyMatch(name::contains))
                && (not.isEmpty() || not.stream().noneMatch(name::contains));
        }
        
        /**
         * Gets whether aggregating from the contents of constant
         * collections is allowed by these rules or not.
         *
         * @return  {@code true} if allowed, otherwise {@code false}
         */
        public boolean isAggregatingFromCollections() { return collections; }
    
        @Override
        public String toString()
        {
            return "MatchRules{" +
                "all=" + all +
                ", any=" + any +
                ", not=" + not +
                ", collections=" + collections +
                '}';
        }
        
        @Override
        public boolean equals(@NullOr Object o)
        {
            if (this == o) { return true; }
            if (o == null || getClass() != o.getClass()) { return false; }
            MatchRules that = (MatchRules) o;
            return collections == that.collections && all.equals(that.all) && any.equals(that.any) && not.equals(that.not);
        }
    
        @Override
        public int hashCode()
        {
            return Objects.hash(all, any, not, collections);
        }
    }
}
