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

import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;

import static org.fest.assertions.Assertions.assertThat;

public class JUnit4ConfiguratorTest {
	private static final String CATEGORIES = "quick";
	private static final String EXCLUDED = "slow, broken, manual";
	private static final String EXCLUDEDLINE = "## excluded-categories=" + EXCLUDED;
	private static final String CATEGORIESLINE = "## categories=" + CATEGORIES;

	@Test
	public void canExcludeNothing() {
		JUnit4Configuration config = new JUnit4Configuration();

		assertThat(config.getExcludedCategories()).isNull();
	}

	@Test
	public void canIncludeNothing() {
        JUnit4Configuration config = new JUnit4Configuration();

		assertThat(config.getCategories()).isNull();
	}

	@Test
	public void canFilterFromFile() throws IOException {
		File file = file(EXCLUDEDLINE);

        JUnit4Configuration config = new JUnit4Configurator(file).getConfig();

		assertThat(config.getExcludedCategories()).isEqualTo(EXCLUDED);
	}

	@Test
	public void testWithOne() throws IOException {
		File file = file(EXCLUDEDLINE.substring(1));

        JUnit4Configuration config = new JUnit4Configurator(file).getConfig();

		assertThat(config.getExcludedCategories()).isEqualTo(EXCLUDED);
	}

	@Test
	public void testWithThree() throws IOException {
		File file = file("#" + EXCLUDEDLINE);

        JUnit4Configuration config = new JUnit4Configurator(file).getConfig();

		assertThat(config.getExcludedCategories()).isEqualTo(EXCLUDED);
	}

	@Test
	public void testReadingIncludedGroup() throws IOException {
		File file = file(CATEGORIESLINE);

        JUnit4Configuration config = new JUnit4Configurator(file).getConfig();

		assertThat(config.getCategories()).isEqualTo(CATEGORIES);
	}

	@Test
	public void testReadingIncludedAndExcludedGroups() throws IOException {
		File file = file(CATEGORIESLINE, EXCLUDEDLINE);

        JUnit4Configuration config = new JUnit4Configurator(file).getConfig();

		assertThat(config.getCategories()).isEqualTo(CATEGORIES);
		assertThat(config.getExcludedCategories()).isEqualTo(EXCLUDED);
	}

	@Test
	public void testEmptyGroups() throws IOException {
		File file = file("## excluded-categories= ");

        JUnit4Configuration config = new JUnit4Configurator(file).getConfig();

		assertThat(config.getExcludedCategories()).isNull();
	}

	@Test
	public void testSpacesInGroupsLine() throws IOException {
		String halloCategory = "hallo";
		File file = file("##excluded-categories = " + halloCategory + " ");

        JUnit4Configuration config = new JUnit4Configurator(file).getConfig();

		assertThat(config.getExcludedCategories()).isEqualTo(halloCategory);
	}

	@Test
	public void testEmptyFile() {
		File file = new File("junit4.config");

        JUnit4Configuration config = new JUnit4Configurator(file).getConfig();

		assertThat(config.getExcludedCategories()).isNull();
	}

	private static File file(String... additionalLines) throws IOException {
		File file = File.createTempFile("filter", "conf");
		file.deleteOnExit();
		PrintWriter writer = new PrintWriter(file);
		try {
			writer.println("## JUnit4 Configuration");
			for (String line : additionalLines) {
				writer.println(line);
			}
			writer.println("#foo.bar");
			writer.println("Some other content");
		} finally {
			writer.close();
		}
		return file;
	}
}
