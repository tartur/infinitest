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
package org.infinitest.plugin;

import static org.infinitest.util.FakeEnvironments.*;
import static org.junit.Assert.*;

import org.infinitest.*;
import org.infinitest.filter.*;
import org.infinitest.parser.*;
import org.junit.*;

public class WhenConfiguringBuilder {
	protected InfinitestCoreBuilder builder;
	private EventSupport eventSupport;
	private TestFilter filterUsedToCreateCore;

	@Before
	public final void mustProvideRuntimeEnvironmentAndEventQueue() {
		RuntimeEnvironment environment = new RuntimeEnvironment(fakeBuildPaths(), fakeWorkingDirectory(), systemClasspath(), currentJavaHome());
		builder = new InfinitestCoreBuilder(environment, new FakeEventQueue());
		builder.setFilter(new InfinitestTestFilter());
	}

	@Test
	public void canUseCustomFilterToRemoveTestsFromTestRun() {
		TestFilter testFilter = new InfinitestTestFilter();
		builder = new InfinitestCoreBuilder(fakeEnvironment(), new FakeEventQueue()) {

			@Override
			protected TestDetector createTestDetector(TestFilter testFilter) {
				filterUsedToCreateCore = testFilter;
				return super.createTestDetector(testFilter);
			}
		};
		builder.setFilter(testFilter);
		builder.createCore();
		assertSame(filterUsedToCreateCore, testFilter);
	}

	@Test
	public void canSetCoreName() {
		builder.setName("myCoreName");
		InfinitestCore core = builder.createCore();
		assertEquals("myCoreName", core.getName());
	}

	@Test
	public void shouldUseBlankCoreNameByDefault() {
		InfinitestCore core = builder.createCore();
		assertEquals("", core.getName());
	}

	@Test
	public void shouldSetRuntimeEnvironment() {
		InfinitestCore core = builder.createCore();
		assertEquals(fakeEnvironment(), core.getRuntimeEnvironment());
	}

	protected EventSupport createEventSupport(InfinitestCore core) {
		EventSupport event = new EventSupport(5000);
		core.addTestQueueListener(event);
		core.addTestResultsListener(event);
		return event;
	}

	protected void runTests() throws InterruptedException {
		InfinitestCore core = builder.createCore();
		eventSupport = createEventSupport(core);
		core.update();
		eventSupport.assertRunComplete();
	}
}
