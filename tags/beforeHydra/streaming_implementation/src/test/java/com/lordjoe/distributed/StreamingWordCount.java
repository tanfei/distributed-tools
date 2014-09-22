package com.lordjoe.distributed;

import com.lordjoe.distributed.test.*;
import org.junit.*;

/**
 * com.lordjoe.distributed.StreamigWordCount
 * User: Steve
 * Date: 8/25/2014
 */
public class StreamingWordCount {

    @Test
    public void testWordCount() {
        WordCountOperator.validateWordCount(StreamingMapReduce.FACTORY);
    }

}
