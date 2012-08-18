/*
 * Created on Oct 21, 2010
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
package org.fest.assertions.internal.bytes;

import static org.fest.assertions.error.ShouldBeGreaterOrEqual.shouldBeGreaterOrEqual;
import static org.fest.util.FailureMessages.actualIsNull;
import static org.fest.assertions.test.TestData.someInfo;
import static org.fest.util.TestFailures.failBecauseExpectedAssertionErrorWasNotThrown;

import static org.mockito.Mockito.verify;

import org.junit.Test;

import org.fest.assertions.core.AssertionInfo;
import org.fest.assertions.internal.Bytes;
import org.fest.assertions.internal.BytesBaseTest;

/**
 * Tests for <code>{@link Bytes#assertGreaterThanOrEqualTo(AssertionInfo, Byte, byte)}</code>.
 * 
 * @author Alex Ruiz
 * @author Joel Costigliola
 */
public class Bytes_assertGreaterThanOrEqualTo_Test extends BytesBaseTest {

  @Test
  public void should_fail_if_actual_is_null() {
    thrown.expectAssertionError(actualIsNull());
    bytes.assertGreaterThanOrEqualTo(someInfo(), null, (byte) 8);
  }

  @Test
  public void should_pass_if_actual_is_greater_than_other() {
    bytes.assertGreaterThanOrEqualTo(someInfo(), (byte) 8, (byte) 6);
  }

  @Test
  public void should_pass_if_actual_is_equal_to_other() {
    bytes.assertGreaterThanOrEqualTo(someInfo(), (byte) 6, (byte) 6);
  }

  @Test
  public void should_fail_if_actual_is_less_than_other() {
    AssertionInfo info = someInfo();
    try {
      bytes.assertGreaterThanOrEqualTo(info, (byte) 6, (byte) 8);
    } catch (AssertionError e) {
      verify(failures).failure(info, shouldBeGreaterOrEqual((byte) 6, (byte) 8));
      return;
    }
    failBecauseExpectedAssertionErrorWasNotThrown();
  }

  @Test
  public void should_fail_if_actual_is_null_whatever_custom_comparison_strategy_is() {
    thrown.expectAssertionError(actualIsNull());
    bytesWithAbsValueComparisonStrategy.assertGreaterThanOrEqualTo(someInfo(), null, (byte) 8);
  }

  @Test
  public void should_pass_if_actual_is_greater_than_other_according_to_custom_comparison_strategy() {
    bytesWithAbsValueComparisonStrategy.assertGreaterThanOrEqualTo(someInfo(), (byte) -8, (byte) 6);
  }

  @Test
  public void should_pass_if_actual_is_equal_to_other_according_to_custom_comparison_strategy() {
    bytesWithAbsValueComparisonStrategy.assertGreaterThanOrEqualTo(someInfo(), (byte) -6, (byte) 6);
  }

  @Test
  public void should_fail_if_actual_is_less_than_other_according_to_custom_comparison_strategy() {
    AssertionInfo info = someInfo();
    try {
      bytesWithAbsValueComparisonStrategy.assertGreaterThanOrEqualTo(info, (byte) 6, (byte) -8);
    } catch (AssertionError e) {
      verify(failures).failure(info, shouldBeGreaterOrEqual((byte) 6, (byte) -8, absValueComparisonStrategy));
      return;
    }
    failBecauseExpectedAssertionErrorWasNotThrown();
  }
}
