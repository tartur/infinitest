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

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

/**
 * Configurator defines a base class that parses infinitest.filters file
 * and convert it to a configuration.
 * @author tarek.turki@gmail.com
 * Date: 4/15/13
 * Time: 11:15 PM
 */
public abstract class Configurator<T> {
    private static final File FILTERFILE = new File("infinitest.filters");
    protected File file = null;

    protected Configurator(File file) {
        this.file = file;
        if (file == null) {
            this.file = FILTERFILE;
        }
    }

    public void updateFilterList() {
        if (file == null) {
            return;
        }

        if (file.exists()) {
            tryToReadFilterFile();
        }
    }

    public abstract T getConfig();

    private void tryToReadFilterFile() {
        try {
            readFilterFile();
        } catch (IOException e) {
            throw new RuntimeException("Something horrible happened to the filter file", e);
        }
    }

    private void readFilterFile() throws IOException {
        BufferedReader reader = null;
        try {
            reader = new BufferedReader(new FileReader(file));
            String line;
            do {
                line = reader.readLine();
                if (line != null) {
                    addFilter(line);
                }
            } while (line != null);
        } finally {
            if (reader != null) {
                reader.close();
            }
        }
    }

    protected abstract void addFilter(String line);
}
