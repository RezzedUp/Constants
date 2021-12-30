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
import java.util.function.Supplier;
import java.util.function.UnaryOperator;
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
	 * Aggregates constants from the provided source class.
	 *
	 * @param source	the source class
	 * @return	next step: pending constant type
	 */
	public static Pending.ConstantType from(Class<?> source)
	{
		return new Aggregator<>(source);
	}
	
	/**
	 * Initiates constant aggregation from "this" class (whichever class happens to be calling this method).
	 * Constants are sourced from the class found directly beneath the {@code Aggregates} class on the call stack.
	 *
	 * @return	next step: pending constant type
	 */
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
	
	/**
	 * A pending aggregation step.
	 */
	public interface Pending
	{
		/**
		 * Step: provide the desired constant type to aggregate.
		 */
		interface ConstantType extends Pending
		{
			/**
			 * Sets the type of constant to aggregate.
			 *
			 * @param type 	generic type token
			 * @param <T>  	constant type
			 * @return	next step: pending aggregation
			 */
			<T> Aggregation<T> constantsOfType(TypeCompatible<T> type);
			
			/**
			 * Sets the type of constant to aggregate.
			 *
			 * @param clazz 	class of constant type
			 * @param <T>   	constant type
			 * @return	next step: pending aggregation
			 */
			default <T> Aggregation<T> constantsOfType(Class<T> clazz)
			{
				return constantsOfType(TypeCapture.type(clazz));
			}
		}
		
		/**
		 * Step: update settings further or aggregate constants.
		 *
		 * @param <T>	constant type
		 */
		interface Aggregation<T> extends Pending
		{
			/**
			 * Sets the match rules, overwriting any existing rules.
			 *
			 * @param rules 	match rules
			 * @return	self (for method chaining)
			 */
			Aggregation<T> matching(MatchRules rules);
			
			/**
			 * Updates the match rules, appending to any previously set rules.
			 *
			 * @param match 	rules update operation
			 * @return	self (for method chaining)
			 */
			Aggregation<T> matching(UnaryOperator<MatchRules> match);
			
			/**
			 * Streams all constants matching the specified type and previously-defined rules.
			 *
			 * @return	stream of all applicable constants
			 */
			Stream<Constant<T>> stream();
			
			/**
			 * Collects all constant values matching the specified type and previously-defined rules
			 * directly into the collection provided by the constructor.
			 *
			 * @param constructor	collection constructor
			 * @param <C>        	collection type
			 * @return	collection containing all applicable constant values
			 */
			default <C extends Collection<T>> C toCollection(Supplier<C> constructor)
			{
				return stream().map(Constant::value).collect(Collectors.toCollection(constructor));
			}
			
			/**
			 * Creates an immutable list containing all constant values matching the specified type
			 * and previously-defined rules.
			 *
			 * @return	immutable list containing all applicable constant values
			 */
			default List<T> toList()
			{
				return List.copyOf(toCollection(ArrayList::new));
			}
			
			/**
			 * Creates an immutable set containing all constant values matching the specified type
			 * and previously-defined rules.
			 *
			 * @return	immutable set containing all applicable constant values
			 */
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
		public Aggregation<T> matching(UnaryOperator<MatchRules> match)
		{
			Objects.requireNonNull(match, "match");
			this.rules = Objects.requireNonNull(match.apply(rules));
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
							return Cast.unsafe().generic(type, value).stream()
								.map(element -> new Constants.Impl<>(source, field.getName(), element, false));
						}
					}
					catch (Exception e) { throw new AggregationException(e); }
				});
		}
	}
}
