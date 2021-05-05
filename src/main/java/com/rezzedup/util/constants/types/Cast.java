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

public class Cast
{
    private Cast() { throw new UnsupportedOperationException(); }
    
    private static final Unsafe UNSAFE = new Unsafe();
    
    @SuppressWarnings("unchecked")
    public static <T> Optional<T> as(Class<T> type, @NullOr Object object)
    {
        Objects.requireNonNull(type, "type");
        return Optional.ofNullable(object)
            .filter(o -> type.isAssignableFrom(o.getClass()))
            .map(o -> (T) o);
    }
    
    public static <T> Function<@NullOr Object, Optional<T>> as(Class<T> type)
    {
        Objects.requireNonNull(type, "type");
        return object -> as(type, object);
    }
    
    public static Unsafe unsafe()
    {
        return UNSAFE;
    }
    
    public static final class Unsafe
    {
        private Unsafe() {}
        
        @SuppressWarnings("unchecked")
        public <T> Optional<T> generic(TypeCompatible<T> type, @NullOr Object object)
        {
            Class<?> raw = TypeCapture.type(type).raw();
            return (Optional<T>) as(raw, object);
        }
        
        public <T> Function<@NullOr Object, Optional<T>> generic(TypeCompatible<T> type)
        {
            Objects.requireNonNull(type, "type");
            return object -> generic(type, object);
        }
    }
}
