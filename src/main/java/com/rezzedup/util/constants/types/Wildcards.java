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

public class Wildcards
{
    private Wildcards() { throw new UnsupportedOperationException(); }
    
    private static final TypeCapture<Class<?>> CLASS = new TypeCapture<>() {};
    
    public static TypeCapture<Class<?>> classType() { return CLASS; }
    
    private static final TypeCapture<List<?>> LIST = new TypeCapture<>() {};
    
    public static TypeCapture<List<?>> listType() { return LIST; }
    
    private static final TypeCapture<Set<?>> SET = new TypeCapture<>() {};
    
    private static final TypeCapture<Collection<?>> COLLECTION = new TypeCapture<>() {};
    
    public static TypeCapture<Collection<?>> collectionType() { return COLLECTION; }
    
    private static final TypeCapture<Map<?, ?>> MAP = new TypeCapture<>() {};
    
    public static TypeCapture<Map<?, ?>> mapType() { return MAP; }
}
