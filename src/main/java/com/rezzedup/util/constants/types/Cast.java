/*
 * Copyright © 2021, RezzedUp <https://github.com/RezzedUp/Constants>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package com.rezzedup.util.constants.types;

import pl.tlinkowski.annotation.basic.NullOr;

import java.util.Objects;
import java.util.Optional;
import java.util.function.Function;

/**
 * Utilities for casting objects.
 */
@SuppressWarnings("OptionalUsedAsFieldOrParameterType")
public class Cast
{
	private Cast() { throw new UnsupportedOperationException(); }
	
	private static final Unsafe UNSAFE = new Unsafe();
	
	/**
	 * Attempts to cast an object into the specified type.
	 *
	 * @param type		type to cast into
	 * @param object	object to cast
	 * @param <T>		the type
	 * @return	the successfully cast object, otherwise empty
	 */
	@SuppressWarnings("unchecked")
	public static <T> Optional<T> as(Class<T> type, @NullOr Object object)
	{
		Objects.requireNonNull(type, "type");
		return (type.isInstance(object)) ? Optional.of((T) object) : Optional.empty();
	}
	
	/**
	 * Creates a function that attempts to cast objects into the specified type.
	 *
	 * @param type	type to cast into
	 * @param <T>	the type
	 * @return	a function that casts objects into the specified type
	 * @see #as(Class, Object)
	 */
	public static <T> Function<@NullOr Object, Optional<T>> as(Class<T> type)
	{
		Objects.requireNonNull(type, "type");
		return object -> as(type, object);
	}
	
	/**
	 * Attempts to cast an {@code Optional}'s contents into the specified type.
	 *
	 * @param type		type to cast into
	 * @param optional	optional to cast
	 * @param <T>		the type
	 * @return	a present optional if its contents were successfully cast, otherwise empty
	 */
	@SuppressWarnings("unchecked")
	public static <T> Optional<T> optional(Class<T> type, Optional<?> optional)
	{
		Objects.requireNonNull(type, "type");
		Objects.requireNonNull(optional, "optional");
		
		return (optional.isPresent() && type.isInstance(optional.get()))
			? (Optional<T>) optional
			: Optional.empty();
	}
	
	/**
	 * Gets the unsafe casting utilities instance.
	 *
	 * <h2>Warning!</h2>
	 * <p>The unsafe instance <b>cannot</b> guarantee accurate casts.</p>
	 *
	 * @return	the unsafe casting utilities instance
	 */
	public static Unsafe unsafe()
	{
		return UNSAFE;
	}
	
	/**
	 * Unsafe utilities for casting objects.
	 * This class <b>cannot</b> guarantee accurate casts.
	 */
	public static final class Unsafe
	{
		private Unsafe() {}
		
		/**
		 * Attempts to cast an object into the specified generic type.
		 *
		 * <p><b>Warning:</b> since generic type nformation is erased, casting will "succeed"
		 * for <b>any</b> instance of a generic class.</p>
		 *
		 * @param type		generic type to cast into
		 * @param object	object to cast
		 * @param <T>		the generic type
		 * @return	the potentially cast object, otherwise empty
		 */
		@SuppressWarnings("unchecked")
		public <T> Optional<T> generic(TypeCompatible<T> type, @NullOr Object object)
		{
			Class<?> raw = TypeCapture.type(type).raw();
			return (Optional<T>) as(raw, object);
		}
		
		/**
		 * Creates a function that attempts to cast objects into the specified generic type.
		 *
		 * <p><b>Warning:</b> since generic type information is erased, casting will "succeed"
		 * for <b>any</b> instance of a generic class.</p>
		 *
		 * @param type	generic type to cast into
		 * @param <T>	the generic type
		 * @return	a function that casts objects into the specified generic type
		 * @see #generic(TypeCompatible, Object)
		 */
		public <T> Function<@NullOr Object, Optional<T>> generic(TypeCompatible<T> type)
		{
			Objects.requireNonNull(type, "type");
			return object -> generic(type, object);
		}
		
		/**
		 * Attempts to cast an {@code Optional}'s contents into the specified generic type.
		 *
		 * <p><b>Warning:</b> since generic type information is erased, casting will "succeed"
		 * for <b>any</b> instance of a generic class.</p>
		 *
		 * @param type		generic type to cast into
		 * @param optional	optional to cast
		 * @param <T>		the generic type
		 * @return	a present Optional if its contents were successfully cast, otherwise empty
		 */
		@SuppressWarnings("unchecked")
		public <T> Optional<T> genericOptional(TypeCompatible<T> type, Optional<?> optional)
		{
			Class<?> raw = TypeCapture.type(type).raw();
			return (Optional<T>) optional(raw, optional);
		}
	}
}
