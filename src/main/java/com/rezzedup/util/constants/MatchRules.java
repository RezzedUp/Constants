/*
 * Copyright © 2021, RezzedUp <https://github.com/RezzedUp/Constants>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package com.rezzedup.util.constants;

import pl.tlinkowski.annotation.basic.NullOr;

import java.util.Collections;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

/**
 * Immutable criteria for filtering constants based on their name and other settings for aggregation.
 */
public class MatchRules
{
	static final MatchRules DEFAULT = new MatchRules();
	
	/**
	 * Specifies criteria for filtering constants based on their name and other settings. By default,
	 * the rules will match all names but won't visit the contents of constant collections.
	 *
	 * <p>It should be noted that since all {@link MatchRules} instances are immutable, the same default
	 * instance is always returned by this method. Any additional criteria will construct entirely
	 * new instances.</p>
	 *
	 * @return the default immutable rules instance
	 */
	public static MatchRules of() { return DEFAULT; }
	
	private final Set<String> all;
	private final Set<String> any;
	private final Set<String> not;
	private final boolean collections;
	
	private MatchRules(Set<String> all, Set<String> any, Set<String> not, boolean collections)
	{
		this.all = Set.copyOf(all);
		this.any = Set.copyOf(any);
		this.not = Set.copyOf(not);
		this.collections = collections;
	}
	
	MatchRules()
	{
		this(Set.of(), Set.of(), Set.of(), false);
	}
	
	/**
	 * Appends required strings to the existing rules. A constant will only match this rule if its name
	 * contains <b>all</b> of the specified strings.
	 *
	 * @param required	all the strings a constant name must contain in order to match
	 * @return	new instance containing the amended rules or itself if no new rules are specified
	 */
	public MatchRules all(String ... required)
	{
		if (required.length <= 0) { return this; }
		Set<String> allModified = new HashSet<>(all);
		Collections.addAll(allModified, required);
		return new MatchRules(allModified, any, not, collections);
	}
	
	/**
	 * Appends optional strings to the existing rules. A constant will only match this rule if its name
	 * contains <b>any</b> (at least one) of the specified strings.
	 *
	 * @param optional	strings a constant name is expected to contain at least one of in order to match
	 * @return	new instance containing the amended rules or itself if no new rules are specified
	 */
	public MatchRules any(String ... optional)
	{
		if (optional.length <= 0) { return this; }
		Set<String> anyModified = new HashSet<>(any);
		Collections.addAll(anyModified, optional);
		return new MatchRules(all, anyModified, not, collections);
	}
	
	/**
	 * Appends excluded strings to the existing rules. A constant will only match this rule if its name
	 * contains <b>none</b> of the specified strings.
	 *
	 * @param excluded	all the strings a constant name must not contain in order to match
	 * @return	new instance containing the amended rules or itself if no new rules are specified
	 */
	public MatchRules not(String ... excluded)
	{
		if (excluded.length <= 0) { return this; }
		Set<String> notModified = new HashSet<>(not);
		Collections.addAll(notModified, excluded);
		return new MatchRules(all, any, notModified, collections);
	}
	
	/**
	 * Sets whether the contents contained within constant collections should be aggregated.
	 *
	 * @param visit		{@code true} if collections should be visited or {@code false} to disable
	 * @return	new instance containing the amended rules or itself if no new rules are specified
	 */
	public MatchRules collections(boolean visit)
	{
		if (collections == visit) { return this; }
		return new MatchRules(all, any, not, visit);
	}
	
	/**
	 * Checks if the provided name matches the criteria contained within these rules.
	 *
	 * @param name	the name to check
	 * @return	{@code true} if the name matches, otherwise {@code false}
	 */
	public boolean matches(String name)
	{
		return (all.isEmpty() || all.stream().allMatch(name::contains))
			&& (any.isEmpty() || any.stream().anyMatch(name::contains))
			&& (not.isEmpty() || not.stream().noneMatch(name::contains));
	}
	
	/**
	 * Gets whether aggregating from the contents of constant collections is allowed by these rules or not.
	 *
	 * @return	{@code true} if allowed, otherwise {@code false}
	 */
	public boolean isAggregatingFromCollections() { return collections; }
	
	@Override
	public String toString()
	{
		return "MatchRules{" +
			"all=" + all + ", " +
			"any=" + any + ", " +
			"not=" + not + ", " +
			"collections=" + collections +
			'}';
	}
	
	@Override
	public boolean equals(@NullOr Object o)
	{
		if (this == o) { return true; }
		if (o == null || getClass() != o.getClass()) { return false; }
		MatchRules that = (MatchRules) o;
		return collections == that.collections && all.equals(that.all) && any.equals(that.any) && not.equals(that.not);
	}
	
	@Override
	public int hashCode()
	{
		return Objects.hash(all, any, not, collections);
	}
}
