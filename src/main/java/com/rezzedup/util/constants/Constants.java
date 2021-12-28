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
	
	public static ConstantStream in(Class<?> source)
	{
		Objects.requireNonNull(source, "source");
		return () -> source;
	}
	
	/**
	 * Streams all the constants of a specific class.
	 *
	 * <p><b>Note:</b> fields in the stream may not necessarily be accessible.</p>
	 *
	 * @param clazz		the class containing constants
	 * @return	a stream containing all constant fields from the provided class
	 * @see #isConstant(Field)
	 */
	@Deprecated(forRemoval = true)
	public static Stream<Field> all(Class<?> clazz)
	{
		Objects.requireNonNull(clazz, "clazz");
		return Arrays.stream(clazz.getDeclaredFields()).filter(Constants::isConstant);
	}
	
	/**
	 * Streams accessible {@code public} constants
	 * of a specific class.
	 *
	 * @param clazz		the class containing constants
	 * @return	a stream containing public constant fields from the provided class
	 * @see #isConstant(Field)
	 */
	@Deprecated(forRemoval = true)
	public static Stream<Field> accessible(Class<?> clazz)
	{
		return all(clazz).filter(field -> field.canAccess(null));
	}
	
	@FunctionalInterface
	public interface ConstantStream
	{
		Class<?> source();
		
		default Stream<Field> streamAllFields()
		{
			return Arrays.stream(source().getDeclaredFields()).filter(Constants::isConstant);
		}
		
		default Stream<Field> streamPublicFields()
		{
			return streamAllFields().filter(field -> Modifier.isPublic(field.getModifiers()));
		}
		
		private static Stream<Constant<?>> fieldsToConstants(Class<?> source, Stream<Field> fields)
		{
			return fields.map(field ->
				{
					try
					{
						@NullOr Object value = field.get(source);
						if (value == null) { return null; }
						return new Impl<>(source, field.getName(), value, false);
					}
					catch (IllegalAccessException e) { return (Constant<?>) null; }
				})
				.filter(Objects::nonNull);
		}
		
		default Stream<Constant<?>> streamAllConstants()
		{
			return fieldsToConstants(source(), streamAllFields());
		}
		
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
