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

import org.junit.experimental.categories.Category;
import org.junit.runner.Description;
import org.junit.runner.manipulation.Filter;

import java.util.*;

/**
 * JUnit4CategoryFilter is a JUnit4 filter implementation
 * that will be used to filter tests that were filtered through the settings.
 * @author tarek.turki@gmail.com
 * Date: 4/17/13
 * Time: 11:14 PM
 */
public class JUnit4CategoryFilter extends Filter {

    private Collection<Class<?>> includedCategories = Collections.emptySet();
    private Collection<Class<?>> excludedCategories = Collections.emptySet();

    public void setIncludedCategories(String includedCategories) {
        if (includedCategories != null)
            this.includedCategories = toClasses(includedCategories);
    }

    public void setExcludedCategories(String excludedCategories) {
        if (excludedCategories != null)
            this.excludedCategories = toClasses(excludedCategories);
    }

    @Override
    public boolean shouldRun(Description description) {
        if (description.isEmpty()) {
            return false;
        } else {
            Category category = description.getAnnotation(Category.class);
            return shouldRunCategory(category);
        }
    }

    @Override
    public String describe() {
        return "Excluded categories: " + excludedCategories + " - Included categories: " + includedCategories;
    }

    /**
     * Excluded wins over included
     *
     * @param category the category that values would be verified
     * @return true if should run this category according to settings and false otherwise
     */
    private boolean shouldRunCategory(Category category) {
        boolean isExcluded = false;
        boolean isIncluded = true;
        if (category != null) {
            List<Class<?>> categories = Arrays.asList(category.value());
            if (includedCategories != null) {
                isIncluded = hasIncludedCategory(categories);
            }
            if (excludedCategories != null) {
                isExcluded = hasExcludedCategory(categories);
            }
        }
        return !isExcluded && isIncluded;
    }

    private boolean hasExcludedCategory(List<Class<?>> categories) {
        boolean isExcluded = false;
        for (Class<?> actualCategory : categories) {
            Iterator<Class<?>> excludedIterator = excludedCategories.iterator();
            while (!isExcluded && excludedIterator.hasNext()) {
                isExcluded = excludedIterator.next().isAssignableFrom(actualCategory);
            }
        }
        return isExcluded;
    }

    private boolean hasIncludedCategory(List<Class<?>> categories) {
        boolean isIncluded = includedCategories.isEmpty();
        for (Class<?> actualCategory : categories) {
            Iterator<Class<?>> includedIterator = includedCategories.iterator();
            while (!isIncluded && includedIterator.hasNext()) {
                isIncluded = includedIterator.next().isAssignableFrom(actualCategory);
            }
        }
        return isIncluded;
    }

    private Collection<Class<?>> toClasses(String categories) {
        Collection<Class<?>> result = new HashSet<Class<?>>();
        if (categories != null && !categories.isEmpty()) {
            String[] names = categories.split("\\s*,\\s*");
            for (String className : names) {
                try {
                    result.add(Class.forName(className));
                } catch (ClassNotFoundException e) {
                    e.printStackTrace();
                }
            }
        }
        return result;
    }

}
