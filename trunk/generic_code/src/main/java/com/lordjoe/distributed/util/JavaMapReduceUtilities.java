package com.lordjoe.distributed.util;

import java.io.*;
import java.util.*;

/**
 * com.lordjoe.distributed.util.StreamUtilitiesTest
 * User: Steve
 * Date: 8/27/2014
 */
public class JavaMapReduceUtilities {

    /**
     * Drop the %^(%^&*%$ IOException
     * @param br
     * @return
     */
    public static String readLineRuntimeException(BufferedReader br)   {
        try {
            return br.readLine();
        }
        catch (IOException e) {
            throw new RuntimeException(e);

        }
    }

      public static Iterable<String> fromPath(InputStream is) {
           final  BufferedReader br = new BufferedReader(new InputStreamReader(is));

           return new Iterable<String>() {
                /**
                * Returns an iterator over a set of elements of type T.
                *
                * @return an Iterator.
                */
               @Override public Iterator<String> iterator() {
                   return new Iterator<String>() {
                       String line = readLineRuntimeException(br);

                       public boolean hasNext() {
                           return line != null;
                       }

                       /**
                        * Returns the next element in the iteration.
                        *
                        * @return the next element in the iteration
                        * @throws java.util.NoSuchElementException if the iteration has no more elements
                        */
                       @Override public String next() {
                           String ret = line;
                           line = readLineRuntimeException(br);
                           return ret;
                       }


                       @Override public void remove() {
                           throw new UnsupportedOperationException("Remove not supported");
                       }
                   };
               }
           };
    }


}
