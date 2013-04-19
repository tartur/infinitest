package org.infinitest.testrunner;

import org.fest.assertions.Assertions;
import org.infinitest.testrunner.categories.Slow;
import org.junit.Test;
import org.junit.experimental.categories.Category;

/**
 * SlowJUnit4Test defines
 * @author tarek.turki@gmail.com
 * Date: 4/19/13
 * Time: 1:39 AM
 */
@Category(Slow.class)
public class SlowJUnit4Test {
    public static boolean fail;

    @Test
    public void hasNoCategorySet() throws Exception {
        Assertions.assertThat(fail).isFalse();
    }
}
