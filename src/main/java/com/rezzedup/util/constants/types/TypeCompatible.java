/*
 * Copyright © 2021-2022, RezzedUp <https://github.com/RezzedUp/Constants>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package com.rezzedup.util.constants.types;

import java.lang.reflect.Type;

/**
 * Conversion layer for alternative type token implementations.
 *
 * @param <T>	the generic type
 */
@FunctionalInterface
public interface TypeCompatible<T>
{
	/**
	 * Gets the underlying type.
	 *
	 * @return	the type
	 */
	Type type();
}
