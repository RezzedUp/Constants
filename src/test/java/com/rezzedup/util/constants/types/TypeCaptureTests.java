/*
 * Copyright © 2021, RezzedUp <https://github.com/RezzedUp/Constants>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package com.rezzedup.util.constants.types;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("TypeCapture")
public class TypeCaptureTests
{
	public static void distinctTypesThoroughlyEqual(TypeCapture<?> expected, TypeCapture<?> duplicate)
	{
		assertNotSame(expected, duplicate);
		assertEquals(expected.type(), duplicate.type());
		assertEquals(expected.raw(), duplicate.raw());
		assertEquals(expected.generics(), duplicate.generics());
		assertEquals(expected.isGeneric(), duplicate.isGeneric());
		assertEquals(expected.isWildcard(), duplicate.isWildcard());
		assertEquals(expected.toString(), duplicate.toString());
		assertEquals(expected.hashCode(), duplicate.hashCode());
		assertEquals(expected, duplicate);
	}
	
	@Nested
	@DisplayName("when capturing a direct type like String")
	public class DirectType
	{
		final TypeCapture<String> stringType = TypeCapture.type(String.class);
		
		@Test
		@DisplayName("returns the same object instance for both 'type' and 'raw type'")
		public void typeAndRawAreTheSame()
		{
			assertEquals(String.class, stringType.type());
			assertEquals(String.class, stringType.raw());
			assertSame(stringType.type(), stringType.raw());
		}
		
		@Test
		@DisplayName("is not generic")
		public void isNotGeneric()
		{
			assertFalse(stringType.isGeneric());
			assertFalse(stringType.isWildcard());
		}
		
		@Test
		@DisplayName("has a standard fully qualified type name when calling toString()")
		public void checkToString()
		{
			assertEquals("java.lang.String", stringType.toString());
		}
		
		@Test
		@DisplayName("is equal to a distinct capture of the same class")
		public void equalsDuplicateCapture()
		{
			distinctTypesThoroughlyEqual(stringType, TypeCapture.type("test".getClass()));
		}
	}
	
	@Nested
	@DisplayName("when capturing a simple generic type like List<String>")
	public class SimpleGenericType
	{
		final TypeCapture<List<String>> listType = new TypeCapture<>() { };
		
		@Test
		@DisplayName("returns a different instance for 'type' and 'raw type'")
		public void typeAndRawAreDifferent()
		{
			assertEquals(List.class, listType.raw());
			assertNotEquals(listType.type(), listType.raw());
			assertNotSame(listType.type(), listType.raw());
		}
		
		@Test
		@DisplayName("is generic")
		public void isGeneric()
		{
			assertTrue(listType.isGeneric());
			assertFalse(listType.isWildcard());
			
			assertEquals(1, listType.generics().size());
			
			TypeCapture<?> genericType = listType.generics().get(0);
			assertEquals(String.class, genericType.raw());
			assertEquals(TypeCapture.type(String.class), genericType);
		}
		
		@Test
		@DisplayName("has generic information when calling toString()")
		public void checkToString()
		{
			assertEquals("java.util.List<java.lang.String>", listType.toString());
		}
		
		@Test
		@DisplayName("is equal to a distinct capture of the same generic class")
		public void equalsDuplicateCapture()
		{
			distinctTypesThoroughlyEqual(listType, new TypeCapture<List<String>>() { });
		}
	}
}
