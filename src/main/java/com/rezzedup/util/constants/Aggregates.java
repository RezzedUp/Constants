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
	
	private static final Set<Class<? extends Annotation>> SKIP_ANNOTATIONS =
		Set.of(AggregatedResult.class, NotAggregated.class);
	
	/**
	 * Visits all constants of a specific
	 * type matching the provided rules.
	 *
	 * @param source   the class containing constants
	 * @param type     type token of the type
	 * @param rules    criteria for filtering constants
	 * @param consumer constant consumer
	 * @param <T>      the type
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
	 * @param source      the class containing constants
	 * @param type        type token of the type
	 * @param rules       criteria for filtering constants
	 * @param constructor new {@code Set} constructor
	 * @param <T>         the type
	 * @return an immutable copy of the constructed {@code Set}
	 * containing the matched constants
	 */
	public static <T> Set<T> set(Class<?> source, TypeCompatible<T> type, MatchRules rules, Supplier<Set<T>> constructor)
	{
		return Set.copyOf(collect(source, type, rules, constructor));
	}
	
	/**
	 * Collects all constants of a specific type matching the
	 * provided rules into an immutable {@code Set}.
	 *
	 * @param source the class containing constants
	 * @param type   type token of the type
	 * @param rules  criteria for filtering constants
	 * @param <T>    the type
	 * @return an immutable {@code Set} containing
	 * the matched constants
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
	 * @param source the class containing constants
	 * @param type   type token of the type
	 * @param <T>    the type
	 * @return an immutable {@code Set} containing
	 * the matched constants
	 */
	public static <T> Set<T> set(Class<?> source, TypeCompatible<T> type)
	{
		return set(source, type, MatchRules.DEFAULT);
	}
	
	/**
	 * Collects all constants of a specific type matching the
	 * provided rules into an immutable {@code List}.
	 *
	 * @param source      the class containing constants
	 * @param type        type token of the type
	 * @param rules       criteria for filtering constants
	 * @param constructor new {@code List} constructor
	 * @param <T>         the type
	 * @return an immutable copy of the constructed {@code List}
	 * containing the matched constants
	 */
	public static <T> List<T> list(Class<?> source, TypeCompatible<T> type, MatchRules rules, Supplier<List<T>> constructor)
	{
		return List.copyOf(collect(source, type, rules, constructor));
	}
	
	/**
	 * Collects all constants of a specific type matching the
	 * provided rules into an immutable {@code List}.
	 *
	 * @param source the class containing constants
	 * @param type   type token of the type
	 * @param rules  criteria for filtering constants
	 * @param <T>    the type
	 * @return an immutable {@code List} containing
	 * the matched constants
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
	 * @param source the class containing constants
	 * @param type   type token of the type
	 * @param <T>    the type
	 * @return an immutable {@code List} containing
	 * the matched constants
	 */
	public static <T> List<T> list(Class<?> source, TypeCompatible<T> type)
	{
		return list(source, type, MatchRules.DEFAULT);
	}
	
}
