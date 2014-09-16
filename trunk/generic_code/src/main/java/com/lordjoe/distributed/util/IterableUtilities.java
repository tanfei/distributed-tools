package com.lordjoe.distributed.util;

import java.util.*;

/**
 * com.lordjoe.distributed.util.IterableUtilities
 * User: Steve
 * Date: 8/28/2014
 */
public class IterableUtilities {


    public static <K> Iterable<K>  asIterable(K... inp)
    {
        List<K> holder = new ArrayList<K>();
        for (int i = 0; i < inp.length; i++) {
            holder.add(inp[i]);
           }

        return holder;
    }
}
