/*
 * Copyright © 2021-2022, RezzedUp <https://github.com/RezzedUp/Constants>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package com.rezzedup.util.constants;

import com.rezzedup.util.constants.annotations.AggregatedResult;
import com.rezzedup.util.constants.annotations.NotAggregated;
import com.rezzedup.util.constants.types.TypeCapture;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import pl.tlinkowski.annotation.basic.NullOr;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class ReadMeExampleTests
{
    // Imagine you have constants of some kind of complicated generic type.
    public static class Example
    {
        public static final ComplexObject<String> STRING_CONSTANT =
            ComplexObject.builder("abc").example("xyz").enabled(true).build();
        
        // This one has an integer... the last one had a string!
        public static final ComplexObject<Integer> INTEGER_CONSTANT =
            ComplexObject.builder(1).example(-1).enabled(true).build();
        
        // Well, this one won't be aggregated.
        @NotAggregated
        public static final ComplexObject<Float> FLOAT_CONSTANT =
            ComplexObject.builder(1.0F).example(-1.0F).enabled(false).build();
        
        public static final ComplexObject<Double> DOUBLE_CONSTANT =
            ComplexObject.builder(1.0).example(-1.0).enabled(true).build();
        
        // And here we go!
        // All the constants are collected into this immutable list,
        // and their generic type is preserved.
        @AggregatedResult
        public static final List<ComplexObject<?>> VALUES =
            Aggregates.fromThisClass()
                .constantsOfType(new TypeCapture<ComplexObject<?>>() {})
                .toList();
    }
    
    public static class ComplexObject<T>
    {
        public static <T> Builder<T> builder(T initial)
        {
            return new Builder<>(initial);
        }
        
        private final T initial;
        private final T example;
        private final boolean enabled;
        
        private ComplexObject(T initial, T example, boolean enabled)
        {
            this.initial = initial;
            this.example = example;
            this.enabled = enabled;
        }
        
        public T initial() { return initial; }
        
        public T example() { return example; }
        
        public boolean enabled() { return enabled; }
        
        public static class Builder<T>
        {
            final T initial;
            @NullOr T example;
            boolean enabled;
            
            private Builder(T initial)
            {
                this.initial = initial;
            }
            
            public Builder<T> example(T example)
            {
                this.example = example;
                return this;
            }
            
            public Builder<T> enabled(boolean enabled)
            {
                this.enabled = enabled;
                return this;
            }
            
            public ComplexObject<T> build()
            {
                if (example == null) { throw new IllegalStateException("missing example"); }
                return new ComplexObject<>(initial, example, enabled);
            }
        }
    }
    
    @Nested
    public class ExampleTest
    {
        @Test
        public void hasThreeConstants()
        {
            assertEquals(3, Example.VALUES.size());
        }
    }
}
