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
	 * Checks whether or not a field is a
	 * {@code static final} constant.
	 *
	 * @param field the possible constant
	 * @return {@code true} if the field is {@code static}
	 * and {@code final}, otherwise {@code false}
	 */
	public static boolean isConstant(@NullOr Field field)
	{
		return field != null
			&& Modifier.isStatic(field.getModifiers())
			&& Modifier.isFinal(field.getModifiers());
	}
	
	/**
	 * Streams all the constants of a specific class.
	 *
	 * <p><b>Note:</b> fields in the stream may not
	 * necessarily be accessible.</p>
	 *
	 * @param clazz the class containing constants
	 * @return a stream containing all constant fields
	 * from the provided class
	 * @see #isConstant(Field)
	 */
	public static Stream<Field> all(Class<?> clazz)
	{
		Objects.requireNonNull(clazz, "clazz");
		return Arrays.stream(clazz.getDeclaredFields()).filter(Constants::isConstant);
	}
	
	/**
	 * Streams accessible {@code public} constants
	 * of a specific class.
	 *
	 * @param clazz the class containing constants
	 * @return a stream containing public constant
	 * fields from the provided class
	 * @see #isConstant(Field)
	 */
	public static Stream<Field> accessible(Class<?> clazz)
	{
		return all(clazz).filter(field -> field.canAccess(null));
	}
}
