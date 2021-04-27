/*
 * Copyright © 2021, RezzedUp <https://github.com/RezzedUp/Constants>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package com.rezzedup.util.constants;

import com.rezzedup.util.constants.annotations.Aggregated;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Aggregates")
public class AggregatesTests
{
    public static class SimpleData
    {
        static final String FIRST_NAME = "John";
        
        @Aggregated.Skip
        static final String FAKE_NAME = "Dummy";
        
        static final String LAST_NAME = "Doe";
        
        static final int LUCKY_NUMBER = 7;
        
        static final String BINGO_NAME_O = "Bingo";
        
        static final int CURSED_NUMBER = 13;
        
        static final String MAGIC_WORD = "Soon";
        
        static final Set<String> GREETING_WORDS = Set.of("Hello", "Hey", "Hi");
        
        static final Set<String> GOODBYE_WORDS = Set.of("Goodbye", "Bye");
        
        static final Set<String> CURSE_WORDS = Set.of("***", "****", "*****");
        
        @Aggregated.Result
        static final List<String> NAMES =
            Aggregates.list(SimpleData.class, TypeCapture.type(String.class), Aggregates.matching().all("NAME"));
        
        @Aggregated.Result
        static final Set<String> WORDS =
            Aggregates.set(
                SimpleData.class,
                TypeCapture.type(String.class),
                Aggregates.matching()
                    .all("WORD").not("CURSE").collections(true)
            );
    }
    
    @Nested
    @DisplayName("with simple data")
    public class SimpleDataTest
    {
        @Test
        @DisplayName("has 3 'NAME' elements")
        public void hasNames()
        {
            // All non-annotated names should be included, of which there are 3:
            assertEquals(3, SimpleData.NAMES.size());
            
            // Assert that all 3 valid names are included
            assertTrue(SimpleData.NAMES.contains(SimpleData.FIRST_NAME));
            assertTrue(SimpleData.NAMES.contains(SimpleData.LAST_NAME));
            assertTrue(SimpleData.NAMES.contains(SimpleData.BINGO_NAME_O));
            
            // Annotated with @Aggregated.Skip, so NAMES should not include the 'fake' name
            assertFalse(SimpleData.NAMES.contains(SimpleData.FAKE_NAME));
        }
        
        @Test
        @DisplayName("is equivalent to re-aggregating 'NAMES' with same parameters")
        public void namesAreEquivalent()
        {
            List<?> alsoNames =
                Aggregates.list(SimpleData.class, TypeCapture.type(String.class), Aggregates.matching().all("NAME"));
            
            assertEquals(SimpleData.NAMES, alsoNames);
        }
        
        @Test
        @DisplayName("has 6 'WORD' elements, excluding 'CURSE'")
        public void hasWords()
        {
            // All non-curse words should be included, of which there are 6:
            assertEquals(6, SimpleData.WORDS.size());
            
            // Assert that all 6 valid words are included
            assertTrue(SimpleData.WORDS.contains(SimpleData.MAGIC_WORD));
            assertTrue(SimpleData.WORDS.containsAll(SimpleData.GREETING_WORDS));
            assertTrue(SimpleData.WORDS.containsAll(SimpleData.GOODBYE_WORDS));
            
            // MatchRules disallow 'CURSE' words, so assert that WORDS doesn't contain any
            assertFalse(SimpleData.WORDS.stream().anyMatch(SimpleData.CURSE_WORDS::contains));
        }
        
        @Test
        @DisplayName("has 5 rude 'WORD' elements")
        public void rudeWords()
        {
            Set<String> rude = Aggregates.set(
                SimpleData.class,
                TypeCapture.type(String.class),
                Aggregates.matching()
                    .any("WORD").not("GREETING", "MAGIC").collections(true)
            );
            
            assertEquals(5, rude.size());
            
            assertTrue(rude.containsAll(SimpleData.GOODBYE_WORDS));
            assertTrue(rude.containsAll(SimpleData.CURSE_WORDS));
            
            assertFalse(rude.stream().anyMatch(SimpleData.GREETING_WORDS::contains));
            assertFalse(rude.contains(SimpleData.MAGIC_WORD));
        }
        
        @Test
        @DisplayName("has valid remaining elements")
        public void miscAggregates()
        {
            // both NUMBER's
            List<Integer> numbers = Aggregates.list(SimpleData.class, TypeCapture.type(Integer.class));
            assertEquals(2, numbers.size());
            
            // CURSED_NUMBER + all 3 CURSE_WORDS
            List<?> curses = Aggregates.list(
                SimpleData.class, TypeCapture.any(), Aggregates.matching().all("CURSE").collections(true)
            );
            
            assertEquals(4, curses.size());
        }
    }
}
