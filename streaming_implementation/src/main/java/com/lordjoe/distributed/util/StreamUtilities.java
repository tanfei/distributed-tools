package com.lordjoe.distributed.util;

import com.lordjoe.distributed.*;

import javax.annotation.*;
import java.io.*;
import java.nio.file.*;
import java.util.*;
import java.util.stream.*;

/**
 * com.lordjoe.distributed.util.StreamUtliities
 * User: Steve
 * Date: 8/26/2014
 */
public class StreamUtilities {


    /**
     * todo Handle HDFS better
     * todo Handle Huge files  better
     *
     * @param path path to a File containing the object -
     * @param ser  seriaiizer fo rthe object
     * @param <T>  type of stream
     * @return stream of objects
     */
    @Nonnull
    public static <T> Stream<T> objectsFromFile(@Nonnull Path path, @Nonnull final IStringSerializer<T> ser) {
        try {
            if (Files.isDirectory(path)) {
                throw new UnsupportedOperationException("Fix This"); // ToDo
//                return Files.walk(path).map(new Function<Path, T>(  ) {
//                    @Override public T apply(final Path t) {
//                        return objectsFromFile(t,ser));
//                    }
//                });
            }
            else {
                BufferedReader br = Files.newBufferedReader(path);
                LineNumberReader rdr = new LineNumberReader(br);
                return StreamSupport.stream(new FileObjectIterator<T>(rdr, ser).spliterator(), false);

            }
        }
        catch (IOException e) {
            throw new RuntimeException(e);

        }
    }


    /**
     * force a stream to evaluate then return the results as a stream
     *
     * @param inp this is an RDD - usually one you want to examine during debugging
     * @param <T> whatever inp is a list of
     * @return non-null RDD of the same values but realized
     */
    @Nonnull
    public static  <T> Stream<T> realizeAndReturn(final Stream<T> inp) {
        List<T> collect = inp.collect(Collectors.toList());
        return collect.stream();
    }


    /**
     * convert a Stream containing Streams into a single stream
     *
     * @param inp
     * @param <T>
     * @return
     */
    public static
    @Nonnull
    <T> Stream<T> streamsToStream(final Stream<Stream<T>> inp) {
        return StreamGenerator.toStream(inp);
    }

    /**
     * convert a Stream containing Streams into a single stream
     *
     * @param inp
     * @param <T>
     * @return
     */
    @SuppressWarnings("UnusedDeclaration")
    @Nonnull
    public static <T> Stream<T> streamsToParallelStream(final Stream<Stream<T>> inp) {
        return StreamGenerator.toParallelStream(inp);
    }


}
