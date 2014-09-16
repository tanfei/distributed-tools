package com.lordjoe.distributed.util;

import java.util.*;
import java.util.function.*;
import java.util.stream.*;

/**
 * com.lordjoe.distributed.util.RestrictedGenerator
 * User: Steve
 * @see http://codereview.stackexchange.com/questions/42473/project-euler-even-fibonacci-numbers-in-java-8
 * Date: 8/27/2014
 */
public abstract class RestrictedGenerator<T> implements Iterator<T> {
    protected final Predicate<T> predicate;

    protected RestrictedGenerator(final Predicate<T> predicate) {
        this.predicate = predicate;
    }

    @Override
    public abstract boolean hasNext();

    @Override
    public abstract T next();

    protected static <T> Stream<T> toStream(final RestrictedGenerator<T> generator, final int charasterics) {
        return StreamSupport.stream(
                Spliterators.spliteratorUnknownSize(generator, charasterics), false
        );
    }

    protected static <T> Stream<T> toParallelStream(final RestrictedGenerator<T> generator, final int charasterics) {
        return StreamSupport.stream(
                Spliterators.spliteratorUnknownSize(generator, charasterics), true
        );
    }
}