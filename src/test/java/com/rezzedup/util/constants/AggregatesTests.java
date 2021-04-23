package com.rezzedup.util.constants;

import com.rezzedup.util.constants.annotations.Aggregated;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;

import java.util.List;

@DisplayName("Aggregates")
class AggregatesTests
{
    static class SimpleData
    {
        static final String FIRST_NAME = "John";
        
        static final String LAST_NAME = "Doe";
        
        static final int LUCKY_NUMBER = 7;
        
        static final String BINGO_NAME_O = "Bingo";
        
        static final int CURSED_NUMBER = 13;
        
        static final List<String> GREETINGS = List.of("Hello", "Hey", "Hi");
        
        @Aggregated.Result
        static final List<String> NAMES =
            Aggregates.list(SimpleData.class, TypeCapture.type(String.class), Aggregates.matching().all("NAME"));
    }
    
    @Nested
    @DisplayName("with simple data")
    class SimpleDataTest
    {
    
    }
}
