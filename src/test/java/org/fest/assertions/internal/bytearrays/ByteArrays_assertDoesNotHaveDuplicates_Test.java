/*
 * Created on Dec 14, 2010
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the
 * License. You may obtain a copy of the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an "AS IS"
 * BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the specific language
 * governing permissions and limitations under the License.
 * 
 * Copyright @2010-2011 the original author or authors.
 */
package org.fest.assertions.internal.bytearrays;

import static org.fest.assertions.error.ShouldNotHaveDuplicates.shouldNotHaveDuplicates;
import static org.fest.util.ByteArrayFactory.*;
import static org.fest.util.FailureMessages.actualIsNull;
import static org.fest.assertions.test.TestData.someInfo;
import static org.fest.util.TestFailures.failBecauseExpectedAssertionErrorWasNotThrown;
import static org.fest.util.Collections.set;

import static org.mockito.Mockito.verify;

import org.junit.Test;

import org.fest.assertions.core.AssertionInfo;
import org.fest.assertions.internal.ByteArrays;
import org.fest.assertions.internal.ByteArraysBaseTest;

/**
 * Tests for <code>{@link ByteArrays#assertDoesNotHaveDuplicates(AssertionInfo, byte[])}</code>.
 * 
 * @author Alex Ruiz
 * @author Joel Costigliola
 */
public class ByteArrays_assertDoesNotHaveDuplicates_Test extends ByteArraysBaseTest {

  @Override
  protected void initActualArray() {
    actual = array(6, 8);
  }

  @Test
  public void should_pass_if_actual_does_not_have_duplicates() {
    arrays.assertDoesNotHaveDuplicates(someInfo(), actual);
  }

  @Test
  public void should_pass_if_actual_is_empty() {
    arrays.assertDoesNotHaveDuplicates(someInfo(), emptyArray());
  }

  @Test
  public void should_fail_if_actual_is_null() {
    thrown.expectAssertionError(actualIsNull());
    arrays.assertDoesNotHaveDuplicates(someInfo(), null);
  }

  @Test
  public void should_fail_if_actual_contains_duplicates() {
    AssertionInfo info = someInfo();
    actual = array(6, 8, 6, 8);
    try {
      arrays.assertDoesNotHaveDuplicates(info, actual);
    } catch (AssertionError e) {
      verify(failures).failure(info, shouldNotHaveDuplicates(actual, set((byte) 6, (byte) 8)));
      return;
    }
    failBecauseExpectedAssertionErrorWasNotThrown();
  }

  @Test
  public void should_pass_if_actual_does_not_have_duplicates_according_to_custom_comparison_strategy() {
    arraysWithCustomComparisonStrategy.assertDoesNotHaveDuplicates(someInfo(), actual);
  }

  @Test
  public void should_pass_if_actual_is_empty_whatever_custom_comparison_strategy_is() {
    arraysWithCustomComparisonStrategy.assertDoesNotHaveDuplicates(someInfo(), emptyArray());
  }

  @Test
  public void should_fail_if_actual_is_null_whatever_custom_comparison_strategy_is() {
    thrown.expectAssertionError(actualIsNull());
    arraysWithCustomComparisonStrategy.assertDoesNotHaveDuplicates(someInfo(), null);
  }

  @Test
  public void should_fail_if_actual_contains_duplicates_according_to_custom_comparison_strategy() {
    AssertionInfo info = someInfo();
    actual = array(6, -8, 6, -8);
    try {
      arraysWithCustomComparisonStrategy.assertDoesNotHaveDuplicates(info, actual);
    } catch (AssertionError e) {
      verify(failures).failure(info, shouldNotHaveDuplicates(actual, set((byte) 6, (byte) -8), absValueComparisonStrategy));
      return;
    }
    failBecauseExpectedAssertionErrorWasNotThrown();
  }
}
