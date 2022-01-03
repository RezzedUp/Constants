/*
 * Copyright © 2021-2022, RezzedUp <https://github.com/RezzedUp/Constants>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package com.rezzedup.util.constants;

/**
 * Represents a constant value.
 *
 * @param <T>   constant type
 */
public interface Constant<T>
{
    /**
     * Gets the source class from which this constant originates.
     *
     * @return the source class
     */
    Class<?> source();
    
    /**
     * Gets the name of this constant.
     *
     * @return the constant's name
     */
    String name();
    
    /**
     * Gets the non-null value of this constant.
     *
     * @return the constant's value
     */
    T value();
    
    /**
     * Gets whether this constant was retrieved from a constant collection.
     *
     * @return {@code true} if this constant is from a collection, otherwise {@code false}
     */
    boolean isFromCollection();
}
