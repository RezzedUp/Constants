/*
 * Copyright © 2021, RezzedUp <https://github.com/RezzedUp/Constants>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package com.rezzedup.util.constants;

import pl.tlinkowski.annotation.basic.NullOr;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.Arrays;
import java.util.Objects;
import java.util.stream.Stream;

/**
 * Utilities for constant fields.
 */
public class Constants
{
	private Constants() { throw new UnsupportedOperationException(); }
	
	/**
	 * Checks whether a field is a {@code static final} constant.
	 *
	 * @param field		the possible constant
	 * @return	{@code true} if the field is {@code static} and {@code final}, otherwise {@code false}
	 */
	public static boolean isConstant(@NullOr Field field)
	{
		return field != null
			&& Modifier.isStatic(field.getModifiers())
			&& Modifier.isFinal(field.getModifiers());
	}
	
	/**
	 * Get the constants contained within the provided class.
	 *
	 * @param source	the source class
	 * @return	an entrypoint for streaming the class's constants
	 */
	public static ConstantStream in(Class<?> source)
	{
		Objects.requireNonNull(source, "source");
		return () -> source;
	}
	
	/**
	 * Generates streams containing constants from a specific source class.
	 */
	@FunctionalInterface
	public interface ConstantStream
	{
		/**
		 * Provides the source class, from which constants are retrieved.
		 *
		 * @return	the source class
		 */
		Class<?> source();
		
		/**
		 * Streams all the constant fields from the source class.
		 *
		 * <p><b>Note:</b> fields in the stream may not necessarily be accessible.</p>
		 *
		 * @return	a stream containing all constant fields
		 * @see #isConstant(Field)
		 */
		default Stream<Field> streamAllFields()
		{
			return Arrays.stream(source().getDeclaredFields()).filter(Constants::isConstant);
		}
		
		/**
		 * Streams accessible {@code public} constant fields from the source class.
		 *
		 * @return	a stream containing public constant fields
		 * @see #isConstant(Field)
		 */
		default Stream<Field> streamPublicFields()
		{
			return streamAllFields().filter(field -> Modifier.isPublic(field.getModifiers()));
		}
		
		private static Stream<Constant<?>> fieldsToConstants(Class<?> source, Stream<Field> fields)
		{
			return fields.map(field ->
				{
					field.setAccessible(true);
					
					try
					{
						@NullOr Object value = field.get(source);
						return (value == null) ? null : new Impl<>(source, field.getName(), value, false);
					}
					catch (IllegalAccessException e) { return (Constant<?>) null; }
				})
				.filter(Objects::nonNull);
		}
		
		/**
		 * Streams all constants from the source class.
		 *
		 * @return	a stream containing all constants
		 */
		default Stream<Constant<?>> streamAllConstants()
		{
			return fieldsToConstants(source(), streamAllFields());
		}
		
		/**
		 * Streams accessible {@code public} constants from the source class.
		 *
		 * @return	a stream containing public constants
		 */
		default Stream<Constant<?>> streamPublicConstants()
		{
			return fieldsToConstants(source(), streamPublicFields());
		}
	}
	
	static final class Impl<T> implements Constant<T>
	{
		private final Class<?> source;
		private final String name;
		private final T value;
		private final boolean isFromCollection;
		
		Impl(Class<?> source, String name, T value, boolean isFromCollection)
		{
			this.source = source;
			this.name = name;
			this.value = value;
			this.isFromCollection = isFromCollection;
		}
		
		@Override
		public Class<?> source() { return source; }
		
		@Override
		public String name() { return name; }
		
		@Override
		public T value() { return value; }
		
		@Override
		public boolean isFromCollection() { return isFromCollection; }
	}
}
