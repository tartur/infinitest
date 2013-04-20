/*
 * Infinitest, a Continuous Test Runner.
 *
 * Copyright (C) 2010-2013
 * "Ben Rady" <benrady@gmail.com>,
 * "Rod Coffin" <rfciii@gmail.com>,
 * "Ryan Breidenbach" <ryan.breidenbach@gmail.com>
 * "David Gageot" <david@gageot.net>, et al.
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
package org.infinitest.testrunner;

import org.infinitest.JUnit4Configuration;
import org.infinitest.MissingClassException;
import org.infinitest.testrunner.categories.Fast;
import org.infinitest.testrunner.categories.Manual;
import org.infinitest.testrunner.categories.Mixed;
import org.infinitest.testrunner.categories.Slow;
import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import static com.google.common.collect.Iterables.*;
import static org.fest.assertions.Assertions.assertThat;
import static org.infinitest.testrunner.TestEvent.methodFailed;
import static org.junit.Assert.*;

public class WhenRunningJUnitTests {
    private static final Class<?> TEST_CLASS = TestThatThrowsExceptionInConstructor.class;
    private JUnit4Runner runner;
    private JUnit4Configuration config;

    @Before
    public void inContext() {
        TestThatThrowsExceptionInConstructor.fail = true;
        FailingTest.fail = true;
        TestNGTest.fail = true;
        JUnit4TestWithCategories.fail = false;
        runner = new JUnit4Runner();
        config = new JUnit4Configuration();
        runner.setJUnit4Configuration(config);
    }

    @After
    public void cleanup() {
        TestThatThrowsExceptionInConstructor.fail = false;
        FailingTest.fail = false;
    }

    @Test
    public void shouldFireNoEventsIfAllMethodsPass() {
        TestResults results = runner.runTest(PassingTestCase.class.getName());
        assertTrue(isEmpty(results));
    }

    @Test
    public void shouldFireEventsToReportFailingResults() {
        TestResults results = runner.runTest(FailingTest.class.getName());
        TestEvent expectedEvent = methodFailed("", FailingTest.class.getName(), "shouldFail", new AssertionError());
        assertEventsEquals(expectedEvent, getOnlyElement(results));
    }

    @Test
    public void shouldIgnoreSuiteMethods() {
        TestResults results = runner.runTest(JUnit3TestWithASuiteMethod.class.getName());
        assertTrue(isEmpty(results));
    }

    @Test
    public void shouldDetectFailureInBeforeMethod() {
        TestResults results = runner.runTest(FailingJUnit4TestWithBefore.class.getName());
        assertFalse(isEmpty(results));
    }

    @Test
    public void shouldDetectFailureInBeforeClassMethod() {
        TestResults results = runner.runTest(FailingJUnit4TestWithBeforeClass.class.getName());
        assertFalse(isEmpty(results));
    }

    @Test
    public void shouldTreatUninstantiableTestsAsFailures() {
        Iterable<TestEvent> events = runner.runTest(TEST_CLASS.getName());
        TestEvent expectedEvent = methodFailed(null, TEST_CLASS.getName(), "shouldPass", new IllegalStateException());
        assertEventsEquals(expectedEvent, getOnlyElement(events));
    }

    @Test
    public void shouldIncludeTimingsForMethodRuns() {
        TestResults results = runner.runTest(MultiTest.class.getName());
        assertEquals(2, size(results.getMethodStats()));
        MethodStats methodStats = get(results.getMethodStats(), 0);
        assertTrue(methodStats.startTime <= methodStats.stopTime);
    }

    @Test(expected = MissingClassException.class)
    public void shouldThrowExceptionIfTestDoesNotExist() {
        runner.runTest("test");
    }

    @Test
    public void shouldSupportTestNG() {
        Iterable<TestEvent> events = runner.runTest(TestNGTest.class.getName());
        TestEvent expectedEvent = methodFailed(TestNGTest.class.getName(), "shouldFail", new AssertionError("expected [false] but found [true]"));
        assertEventsEquals(expectedEvent, getOnlyElement(events));
    }

    @Test
    public void shouldFailIfBadTestsAreNotFiltered() throws Exception {
        JUnit4TestWithCategories.fail = true;
        final Set<String> failingMethods = new HashSet<String>(Arrays.asList("shouldNotBeTestedCategory1", "shouldNotBeTestedCategory2", "shouldNotBeTestedCategory3", "shouldNoBeTestedDueToInheritanceOnFilteredCategory"));
        TestResults results = runner.runTest(JUnit4TestWithCategories.class.getName());
        int counter = 0;
        for (TestEvent testEvent : results) {
            counter++;
            assertThat(testEvent.getTestMethod()).isIn(failingMethods);
            assertThat(testEvent.getFullErrorClassName()).isEqualTo(AssertionError.class.getName());
            assertThat(testEvent.getTestName()).isEqualTo(JUnit4TestWithCategories.class.getName());
        }
        assertThat(counter).isEqualTo(failingMethods.size());
    }

    @Test
    public void shouldNotFailWithFilteredCategories() {
        JUnit4TestWithCategories.fail = true;
        config.setExcludedCategories(Slow.class.getName() + ", " + Manual.class.getName());
        TestResults results = runner.runTest(JUnit4TestWithCategories.class.getName());
        assertThat(results).isEmpty();
    }

    @Test
    public void shouldExecuteOnlyTheSpecifiedCategory() {
        JUnit4TestWithCategories.fail = true;
        config.setCategories(Slow.class.getName());
        TestResults results = runner.runTest(JUnit4TestWithCategories.class.getName());
        assertThat(results).hasSize(3);

        config.setCategories(Fast.class.getName());
        results = runner.runTest(JUnit4TestWithCategories.class.getName());
        assertThat(results).isEmpty();
    }

    @Test
    public void combineIncludedAndExcludedCategories() {
        JUnit4TestWithCategories.fail = true;
        config.setCategories(Slow.class.getName());
        config.setExcludedCategories(Mixed.class.getName());
        TestResults results = runner.runTest(JUnit4TestWithCategories.class.getName());
        assertThat(results).hasSize(2);
    }

    @Ignore("Due to org.junit.runner.manipulation.Filter.initializationError")
    @Test
    public void shouldHandleExcludedCategoryAnnotatedOnClass() throws Exception {
        SlowJUnit4Test.fail = true;
        FastJUnit4Test.fail = true;
        config.setExcludedCategories(Slow.class.getName());
        TestResults results = runner.runTest(SlowJUnit4Test.class.getName());
        print(results);
        //GET a InitializationError on Filter class, when we have no test to run!
        assertThat(results).isEmpty();

        results = runner.runTest(FastJUnit4Test.class.getName());
        print(results);
        assertThat(results).hasSize(1);
    }

    private void print(TestResults results) {
        StringBuilder msg = new StringBuilder("TestResults : ");
        if (results == null) {
            msg.append("<null>");
        } else {
            msg.append("\nEVENTS\n");
            for (TestEvent e : results) {
                msg.append('[').append(e.getType()).append(']');
                msg.append("[test-name = ").append(e.getTestName());
                msg.append("][message = ").append(e.getMessage());
                msg.append("][test-method = ").append(e.getTestMethod());
                msg.append("][error-class-name = ").append(e.getErrorClassName());
                msg.append("][full-error-class-name = ").append(e.getFullErrorClassName()).append("]\n");
            }
        }
        System.out.println(msg);
    }

    private void assertEventsEquals(TestEvent expected, TestEvent actual) {
        assertEquals(expected, actual);
        assertEquals(expected.getMessage(), actual.getMessage());
        assertEquals(expected.getType(), actual.getType());
        assertEquals(expected.getErrorClassName(), actual.getErrorClassName());
    }
}
