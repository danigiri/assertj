/*
 * Created on Jan 2, 2010
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
package org.fest.assertions.internal.objects;

import static org.fest.assertions.error.ShouldNotBeIn.shouldNotBeIn;
import static org.fest.util.ErrorMessages.*;
import static org.fest.util.FailureMessages.actualIsNull;
import static org.fest.util.ObjectArrayFactory.emptyArray;
import static org.fest.assertions.test.TestData.someInfo;
import static org.fest.util.TestFailures.failBecauseExpectedAssertionErrorWasNotThrown;
import static org.fest.util.Arrays.array;

import static org.mockito.Mockito.verify;

import org.junit.BeforeClass;
import org.junit.Test;

import org.fest.assertions.core.AssertionInfo;
import org.fest.assertions.internal.Objects;
import org.fest.assertions.internal.ObjectsBaseTest;

/**
 * Tests for <code>{@link Objects#assertIsNotIn(AssertionInfo, Object, Object[])}</code>.
 * 
 * @author Joel Costigliola
 * @author Alex Ruiz
 * @author Yvonne Wang
 */
public class Objects_assertIsNotIn_with_array_Test extends ObjectsBaseTest {

  private static String[] values;

  @BeforeClass
  public static void setUpOnce() {
    values = array("Yoda", "Leia");
  }

  @Test
  public void should_throw_error_if_array_is_null() {
    thrown.expectNullPointerException(arrayIsNull());
    Object[] array = null;
    objects.assertIsNotIn(someInfo(), "Yoda", array);
  }

  @Test
  public void should_throw_error_if_array_is_empty() {
    thrown.expectIllegalArgumentException(arrayIsEmpty());
    objects.assertIsNotIn(someInfo(), "Yoda", emptyArray());
  }

  @Test
  public void should_pass_if_actual_is_in_not_array() {
    objects.assertIsNotIn(someInfo(), "Luke", values);
  }

  @Test
  public void should_fail_if_actual_is_null() {
    thrown.expectAssertionError(actualIsNull());
    objects.assertIsNotIn(someInfo(), null, values);
  }

  @Test
  public void should_fail_if_actual_is_not_in_array() {
    AssertionInfo info = someInfo();
    try {
      objects.assertIsNotIn(info, "Yoda", values);
    } catch (AssertionError e) {
      verify(failures).failure(info, shouldNotBeIn("Yoda", values));
      return;
    }
    failBecauseExpectedAssertionErrorWasNotThrown();
  }

  @Test
  public void should_pass_if_actual_is_in_not_array_according_to_custom_comparison_strategy() {
    objectsWithCustomComparisonStrategy.assertIsNotIn(someInfo(), "Luke", values);
  }

  @Test
  public void should_fail_if_actual_is_not_in_array_according_to_custom_comparison_strategy() {
    AssertionInfo info = someInfo();
    try {
      objectsWithCustomComparisonStrategy.assertIsNotIn(info, "YODA", values);
    } catch (AssertionError e) {
      verify(failures).failure(info, shouldNotBeIn("YODA", values, customComparisonStrategy));
      return;
    }
    failBecauseExpectedAssertionErrorWasNotThrown();
  }
}
