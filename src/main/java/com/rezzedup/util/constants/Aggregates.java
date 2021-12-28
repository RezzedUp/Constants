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
import java.util.function.Consumer;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Aggregates constants.
 */
public class Aggregates
{
	private Aggregates() { throw new UnsupportedOperationException(); }
	
	private static final Set<Class<? extends Annotation>> SKIP_ANNOTATIONS =
		Set.of(AggregatedResult.class, NotAggregated.class);
	
	/**
	 * Visits all constants of a specific type matching the provided rules.
	 *
	 * @param source	the class containing constants
	 * @param type		type token of the type
	 * @param rules		criteria for filtering constants
	 * @param consumer	constant consumer
	 * @param <T>		the type
	 */
	@Deprecated(forRemoval = true)
	public static <T> void visit(Class<?> source, TypeCompatible<T> type, MatchRules rules, Consumer<Constant<T>> consumer)
	{
		Objects.requireNonNull(consumer, "consumer");
		from(source).constantsOfType(type).matching(rules).stream().forEach(consumer);
	}
	
	private static <T, C extends Collection<T>> C collect(Class<?> source, TypeCompatible<T> type, MatchRules rules, Supplier<C> constructor)
	{
		Objects.requireNonNull(constructor, "constructor");
		return from(source).constantsOfType(type).matching(rules).stream()
			.map(Constant::value).collect(Collectors.toCollection(constructor));
	}
	
	/**
	 * Collects all constants of a specific type matching the provided rules into an immutable {@code Set}.
	 *
	 * @param source		the class containing constants
	 * @param type			type token of the type
	 * @param rules			criteria for filtering constants
	 * @param constructor	new {@code Set} constructor
	 * @param <T>			the type
	 * @return	an immutable copy of the constructed set containing the matched constants
	 */
	@Deprecated(forRemoval = true)
	public static <T> Set<T> set(Class<?> source, TypeCompatible<T> type, MatchRules rules, Supplier<Set<T>> constructor)
	{
		return Set.copyOf(collect(source, type, rules, constructor));
	}
	
	/**
	 * Collects all constants of a specific type matching the provided rules into an immutable {@code Set}.
	 *
	 * @param source	the class containing constants
	 * @param type		type token of the type
	 * @param rules		criteria for filtering constants
	 * @param <T>		the type
	 * @return an immutable set containing the matched constants
	 */
	@Deprecated(forRemoval = true)
	public static <T> Set<T> set(Class<?> source, TypeCompatible<T> type, MatchRules rules)
	{
		return set(source, type, rules, HashSet::new);
	}
	
	/**
	 * Collects all constants of a specific type into an immutable {@code Set}.
	 * Constants within collections will <b>not</b> be aggregated.
	 *
	 * @param source	the class containing constants
	 * @param type		type token of the type
	 * @param <T>		the type
	 * @return	an immutable set containing the matched constants
	 */
	@Deprecated(forRemoval = true)
	public static <T> Set<T> set(Class<?> source, TypeCompatible<T> type)
	{
		return set(source, type, MatchRules.DEFAULT);
	}
	
	/**
	 * Collects all constants of a specific type matching the provided rules into an immutable {@code List}.
	 *
	 * @param source		the class containing constants
	 * @param type			type token of the type
	 * @param rules			criteria for filtering constants
	 * @param constructor	new {@code List} constructor
	 * @param <T>			the type
	 * @return	an immutable copy of the constructed list containing the matched constants
	 */
	@Deprecated(forRemoval = true)
	public static <T> List<T> list(Class<?> source, TypeCompatible<T> type, MatchRules rules, Supplier<List<T>> constructor)
	{
		return List.copyOf(collect(source, type, rules, constructor));
	}
	
	/**
	 * Collects all constants of a specific type matching the provided rules into an immutable {@code List}.
	 *
	 * @param source	the class containing constants
	 * @param type		type token of the type
	 * @param rules		criteria for filtering constants
	 * @param <T>		the type
	 * @return	an immutable list containing the matched constants
	 */
	@Deprecated(forRemoval = true)
	public static <T> List<T> list(Class<?> source, TypeCompatible<T> type, MatchRules rules)
	{
		return list(source, type, rules, ArrayList::new);
	}
	
	/**
	 * Collects all constants of a specific type into an immutable {@code List}.
	 * Constants within collections will <b>not</b> be aggregated.
	 *
	 * @param source	the class containing constants
	 * @param type		type token of the type
	 * @param <T>		the type
	 * @return	an immutable list containing the matched constants
	 */
	@Deprecated(forRemoval = true)
	public static <T> List<T> list(Class<?> source, TypeCompatible<T> type)
	{
		return list(source, type, MatchRules.DEFAULT);
	}
	
	public static Pending.ConstantType from(Class<?> sourceClass)
	{
		return new Aggregator<>(sourceClass);
	}
	
	public static Pending.ConstantType fromThisClass()
	{
		for (StackTraceElement element : Thread.currentThread().getStackTrace())
		{
			String name = element.getClassName();
			
			if (element.isNativeMethod()) { continue; }
			if (name.startsWith("java.")) { continue; }
			if (name.equals(Aggregates.class.getName())) { continue; }
			
			try { return from(Class.forName(name)); }
			catch (ClassNotFoundException e) { throw new AggregationException(e); }
		}
		
		throw new IllegalStateException("Could not resolve class");
	}
	
	public interface Pending
	{
		interface ConstantType extends Pending
		{
			<T> Aggregation<T> constantsOfType(TypeCompatible<T> type);
			
			default <T> Aggregation<T> constantsOfType(Class<T> clazz)
			{
				return constantsOfType(TypeCapture.type(clazz));
			}
		}
		
		interface Aggregation<T> extends Pending
		{
			Aggregation<T> matching(MatchRules rules);
			
			Stream<Constant<T>> stream();
			
			default <C extends Collection<T>> C toCollection(Supplier<C> constructor)
			{
				return stream().map(Constant::value).collect(Collectors.toCollection(constructor));
			}
			
			default List<T> toList()
			{
				return List.copyOf(toCollection(ArrayList::new));
			}
			
			default Set<T> toSet()
			{
				return Set.copyOf(toCollection(HashSet::new));
			}
		}
	}
	
	private static class Aggregator<T> implements Pending.ConstantType, Pending.Aggregation<T>
	{
		private final Class<?> source;
		private @NullOr TypeCapture<T> type = null;
		private MatchRules rules = MatchRules.DEFAULT;
		
		Aggregator(Class<?> source)
		{
			this.source = Objects.requireNonNull(source, "source");
		}
		
		@SuppressWarnings("unchecked")
		@Override
		public <P> Pending.Aggregation<P> constantsOfType(TypeCompatible<P> type)
		{
			Objects.requireNonNull(type, "type");
			this.type = (TypeCapture<T>) TypeCapture.type(type);
			return (Aggregator<P>) this;
		}
		
		@Override
		public Aggregator<T> matching(MatchRules rules)
		{
			this.rules = Objects.requireNonNull(rules, "rules");
			return this;
		}
		
		@Override
		public Stream<Constant<T>> stream()
		{
			if (type == null) { throw new IllegalStateException("Skipped step: Pending.ConstantType"); }
			
			return Constants.in(source).streamAllFields()
				.filter(field -> rules.matches(field.getName()))
				.filter(field -> SKIP_ANNOTATIONS.stream().noneMatch(field::isAnnotationPresent))
				.flatMap(field ->
				{
					field.setAccessible(true);
					
					try
					{
						@NullOr Object value = field.get(null);
						if (value == null) { return Stream.empty(); }
						
						if (value instanceof Collection && rules.isAggregatingFromCollections())
						{
							return ((Collection<?>) value).stream()
								.flatMap(element -> Cast.unsafe().generic(type, element).stream())
								.map(element -> new Constants.Impl<>(source, field.getName(), element, true));
						}
						else
						{
							return Cast.unsafe().generic(type, value)
								.map(element -> new Constants.Impl<>(source, field.getName(), element, false))
								.stream();
						}
					}
					catch (Exception e) { throw new AggregationException(e); }
				});
		}
	}
}
