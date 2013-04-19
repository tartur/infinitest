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
package org.infinitest;

import java.io.*;
import java.util.*;
import java.util.regex.*;

public class TestNGConfigurator extends Configurator<TestNGConfiguration> {
	private static final String SUFFIX = "\\s?=\\s?(.+)";
	private static final String PREFIX = "^\\s*#+\\s?";
	private static final String EXCLUDED_GROUPS = "excluded-groups";
	private static final String INCLUDED_GROUPS = "groups";
	private static final String LISTENERS = "listeners";
	private static final Pattern EXCLUDED = Pattern.compile(PREFIX + EXCLUDED_GROUPS + SUFFIX);
	private static final Pattern INCLUDED = Pattern.compile(PREFIX + INCLUDED_GROUPS + SUFFIX);
	private static final Pattern LISTENER = Pattern.compile(PREFIX + LISTENERS + SUFFIX);

	private final TestNGConfiguration testNGConfiguration;

    public TestNGConfigurator() {
        this(null);
	}

	public TestNGConfigurator(File filterFile) {
        super(filterFile);
		testNGConfiguration = new TestNGConfiguration();
		updateFilterList();
	}

    @Override
    public TestNGConfiguration getConfig() {
		return testNGConfiguration;
	}

    @Override
    protected void addFilter(String line) {
		Matcher matcher = EXCLUDED.matcher(line.trim());
		if (matcher.matches()) {
			String excludedGroups = matcher.group(1);
			testNGConfiguration.setExcludedGroups(excludedGroups);
		} else {
			matcher = INCLUDED.matcher(line);
			if (matcher.matches()) {
				String includedGroups = matcher.group(1).trim();
				testNGConfiguration.setGroups(includedGroups);
			} else {
				matcher = LISTENER.matcher(line);
				if (matcher.matches()) {
					final List<Object> listenerList = createListenerList(matcher.group(1).trim());
					testNGConfiguration.setListeners(listenerList);
				}
			}
		}
	}

	private List<Object> createListenerList(String listeners) {
		final String[] listenerTypes = listeners.split("\\s*,\\s*");
		final List<Object> listenerList = new ArrayList<Object>();
		for (final String listenername : listenerTypes) {
			try {
				listenerList.add(Class.forName(listenername).newInstance());
			} catch (InstantiationException e) {
				// unable to add this listener, just continue with the next.
				e.printStackTrace();
			} catch (IllegalAccessException e) {
				// unable to add this listener, just continue with the next.
				e.printStackTrace();
			} catch (ClassNotFoundException e) {
				// unable to add this listener, just continue with the next.
				e.printStackTrace();
			}
		}
		return listenerList;
	}
}
