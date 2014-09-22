package com.lordjoe.distributed.util;

import java.util.*;
import java.util.stream.*;

/**
 * com.lordjoe.distributed.util.RestrictedGenerator
 * User: Steve
 * @see http://codereview.stackexchange.com/questions/42473/project-euler-even-fibonacci-numbers-in-java-8
 * Date: 8/27/2014
 */
public  class StreamGenerator<T> implements Iterator<T> {

    private final Iterator<Stream<T>> iterator;
    private Iterator<T> current = null;
    private StreamGenerator(final Stream<Stream<T>> inp)
    {
        iterator = inp.iterator();
        if(iterator.hasNext())
            current = iterator.next().iterator();
    }

    @Override
    public   boolean hasNext()
    {
        if(current == null)
            return false; // at end
        boolean ret = current.hasNext();
        if(ret)
            return true;
        current = null; // at end of current Stream;
        if(iterator.hasNext())
              current = iterator.next().iterator();
        return hasNext(); // try again
    }

    @Override
    public T next() {
        if(current != null)
            return current.next();
        else
            return null;
    }

    public static <T> Stream<T> toStream(final Stream<Stream<T>> inp ) {
        return StreamSupport.stream(
                Spliterators.spliteratorUnknownSize(new StreamGenerator(inp),0 ), false
        );
    }

    public static <T> Stream<T> toParallelStream(final Stream<Stream<T>> inp) {
        return StreamSupport.stream(
                Spliterators.spliteratorUnknownSize(new StreamGenerator(inp),0), true
        );
    }


}