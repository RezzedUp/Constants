/*
 * Copyright © 2021-2022, RezzedUp <https://github.com/RezzedUp/Constants>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package com.rezzedup.util.constants.types;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class PrimitivesTests
{
	@Test
	public void testBoxedTypes()
	{
		// All 8 primitive wrappers are properly included
		assertEquals(8, Primitives.boxedTypes().size());
		
		// Ensure immutable
		assertThrows(RuntimeException.class, () -> Primitives.boxedTypes().add(Number.class));
	}
	
	@Test
	public void testAutoboxing()
	{
		assertTrue(Primitives.isBoxed((byte) 255));
		assertTrue(Primitives.isBoxed((short) 256));
		assertTrue(Primitives.isBoxed(5));
		assertTrue(Primitives.isBoxed(6L));
		assertTrue(Primitives.isBoxed(7.1F));
		assertTrue(Primitives.isBoxed(8.2D));
		assertTrue(Primitives.isBoxed(true));
		assertTrue(Primitives.isBoxed('a'));
		
		assertFalse(Primitives.isBoxed("a"));
		assertFalse(Primitives.isBoxed(null));
		assertFalse(Primitives.isBoxed(new Object()));
	}
}
