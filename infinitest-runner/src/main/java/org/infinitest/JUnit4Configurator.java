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

import java.io.File;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * JUnit4Configurator defines the junit4 configurator. It configures
 * included and excluded categories
 * @author tarek.turki@gmail.com
 * Date: 4/15/13
 * Time: 11:05 PM
 */
public class JUnit4Configurator extends Configurator<JUnit4Configuration> {
    private static final String SUFFIX = "\\s?=\\s?(.+)";
    private static final String PREFIX = "^\\s*#+\\s?";
    private static final String EXCLUDED_CATEGORIES = "excluded-categories";
    private static final String INCLUDED_CATEGORIES = "categories";
    private static final Pattern EXCLUDED = Pattern.compile(PREFIX + EXCLUDED_CATEGORIES + SUFFIX);
    private static final Pattern INCLUDED = Pattern.compile(PREFIX + INCLUDED_CATEGORIES + SUFFIX);
    private JUnit4Configuration configuration;

    public JUnit4Configurator() {
        this(null);
    }

    public JUnit4Configurator(File file) {
        super(file);
        configuration = new JUnit4Configuration();
        updateFilterList();
    }

    public JUnit4Configuration getConfig() {
        return configuration;
    }

    @Override
    protected void addFilter(String line) {
        Matcher matcher = EXCLUDED.matcher(line.trim());
        if (matcher.matches()) {
            String excludedCategories = matcher.group(1);
            configuration.setExcludedCategories(excludedCategories);
        } else {
            matcher = INCLUDED.matcher(line);
            if (matcher.matches()) {
                String includedGroups = matcher.group(1).trim();
                configuration.setCategories(includedGroups);
            }
        }
    }
}
