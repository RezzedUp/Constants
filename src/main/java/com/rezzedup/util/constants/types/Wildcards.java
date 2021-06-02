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
    
    private static final TypeCapture<Class<?>> CLASS = new TypeCapture<>() {};
    
    /**
     * Wildcard {@code Class} type capture.
     *
     * @return  wildcard type capture
     */
    public static TypeCapture<Class<?>> classType() { return CLASS; }
    
    private static final TypeCapture<List<?>> LIST = new TypeCapture<>() {};
    
    /**
     * Wildcard {@code List} type capture.
     *
     * @return  wildcard type capture
     */
    public static TypeCapture<List<?>> listType() { return LIST; }
    
    private static final TypeCapture<Set<?>> SET = new TypeCapture<>() {};
    
    /**
     * Wildcard {@code Set} type capture.
     *
     * @return  wildcard type capture
     */
    public static TypeCapture<Set<?>> setType() { return SET; }
    
    private static final TypeCapture<Collection<?>> COLLECTION = new TypeCapture<>() {};
    
    /**
     * Wildcard {@code Collection} type capture.
     *
     * @return  wildcard type capture
     */
    public static TypeCapture<Collection<?>> collectionType() { return COLLECTION; }
    
    private static final TypeCapture<Map<?, ?>> MAP = new TypeCapture<>() {};
    
    /**
     * Wildcard {@code Map} type capture.
     *
     * @return  wildcard type capture
     */
    public static TypeCapture<Map<?, ?>> mapType() { return MAP; }
}
