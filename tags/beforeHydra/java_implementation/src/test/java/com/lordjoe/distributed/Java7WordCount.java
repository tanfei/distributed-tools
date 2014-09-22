package com.lordjoe.distributed;

import com.lordjoe.distributed.test.*;
import org.junit.*;

/**
 * com.lordjoe.distributed.Java7WordCount
 * User: Steve
 * Date: 8/25/2014
 */
public class Java7WordCount {


    @Test
    public void testWordCount() {
        WordCountOperator.validateWordCount(JavaMapReduce.FACTORY);
      }

}
