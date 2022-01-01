/*
 * Copyright © 2021-2022, RezzedUp <https://github.com/RezzedUp/Constants>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package com.rezzedup.util.constants.exceptions;

/**
 * Represents an exception that occurred during aggregation.
 */
public class AggregationException extends RuntimeException
{
	/**
	 * Wraps an exception that occurred during aggregation.
	 *
	 * @param caught	the exception
	 */
	public AggregationException(Throwable caught) { super(caught); }
}
