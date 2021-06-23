/*
 * Copyright © 2021, RezzedUp <https://github.com/RezzedUp/Constants>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package com.rezzedup.util.constants.types;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Constant type captures for common generic wildcard types.
 */
public class Wildcards
{
    private Wildcards() { throw new UnsupportedOperationException(); }
    
    /**
     * Wildcard {@code Class} type capture.
     */
    public static final TypeCapture<Class<?>> CLASS = new TypeCapture<>() {};
    
    /**
     * Wildcard {@code List} type capture.
     */
    public static final TypeCapture<List<?>> LIST = new TypeCapture<>() {};
    
    /**
     * Wildcard {@code Set} type capture.
     */
    public static final TypeCapture<Set<?>> SET = new TypeCapture<>() {};
    
    /**
     * Wildcard {@code Collection} type capture.
     */
    public static final TypeCapture<Collection<?>> COLLECTION = new TypeCapture<>() {};
    
    /**
     * Wildcard {@code Map} type capture.
     */
    public static final TypeCapture<Map<?, ?>> MAP = new TypeCapture<>() {};
}
