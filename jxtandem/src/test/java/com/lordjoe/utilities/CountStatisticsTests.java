package com.lordjoe.utilities;

import org.junit.*;

import java.util.*;

/**
 * com.lordjoe.utilities.CountStatisticsTests
 * User: Steve
 * Date: Apr 11, 2011
 */
public class CountStatisticsTests {
    public static final CountStatisticsTests[] EMPTY_ARRAY = {};

    public static final Random RND = new Random();

    public static final int MAX_VALUE = 5000;
    public static final int OTHER_VALUES = 20;
    public static final int COUNTED_VALUES = 5;
    public static final int NEVER_SEEN_VALUE = 42;

    @Test
    public void testCountStatisticsFixed() {
        CountStatistics cs = new CountStatistics(MAX_VALUE);
        for (int i = 0; i < MAX_VALUE; i++) {
            if(i == NEVER_SEEN_VALUE)
                continue;
            for (int j = 0; j < COUNTED_VALUES; j++) {
                cs.addItem(i);

            }


        }
        for (int j = 0; j < OTHER_VALUES; j++) {
            cs.addItem(chooseIntGreaterThan(MAX_VALUE));

        }
        Assert.assertEquals(OTHER_VALUES, cs.getOtherValues());
        for (int i = 0; i < MAX_VALUE; i++) {
            if(i == NEVER_SEEN_VALUE)
               Assert.assertEquals(0, cs.getCountAtValue(i));
            else
                Assert.assertEquals(COUNTED_VALUES, cs.getCountAtValue(i));
        }
        int[] values = cs.getValuesAsArray();
        Assert.assertEquals(MAX_VALUE, values.length);

        for (int i = 0; i < values.length; i++) {
            if(i == NEVER_SEEN_VALUE)
               Assert.assertEquals(0, cs.getCountAtValue(i));
            else
                Assert.assertEquals(COUNTED_VALUES, cs.getCountAtValue(i));
        }


    }

    public int chooseIntGreaterThan(int value) {
        int ret = 0;
        while (ret < value)
            ret = RND.nextInt();
        return ret;
    }
}
