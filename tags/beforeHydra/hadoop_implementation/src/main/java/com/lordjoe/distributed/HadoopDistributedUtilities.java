package com.lordjoe.distributed;

import org.apache.hadoop.io.*;
import org.apache.hadoop.mapreduce.*;

import java.io.*;

/**
 * com.lordjoe.distributed.HadoopDistributedUtilities
 * User: Steve
 * Date: 9/19/2014
 */
public class HadoopDistributedUtilities {

    private static Text onlyKey;
    private static Text onlyValue;


    public static <K extends Serializable,V extends Serializable>  IKeyValueConsumer<K,V>  contextConsumer(final TaskInputOutputContext ctxt)
    {
        return new IKeyValueConsumer<K,V>() {

            @Override
            public void consume(final KeyValueObject<K, V> kv) {
                try {
                    writeKeyValueObject(  kv, ctxt);
                }
                catch (IOException e) {
                    throw new RuntimeException(e);

                }
                catch (InterruptedException e) {
                    throw new RuntimeException(e);

                }
            }
        };
    }

    public static String asHadoopText(Object o)
    {
        // todo add special serialization cases
        return o.toString();
    }


    public static Object fromHadoopText(String o)
    {
        // todo add special serialization cases
        return o.toString();
    }


    /**
     *
     * @param kv
     * @param ctxt
     * @throws IOException
     * @throws InterruptedException
     */
    public static void writeKeyValueObject(KeyValueObject kv,TaskInputOutputContext ctxt) throws IOException, InterruptedException {
        String keyStr = asHadoopText(kv.key);
        String valueStr = asHadoopText(kv.value);

         synchronized (onlyKey) {
             onlyKey.set(keyStr);
             onlyValue.set(valueStr);
             ctxt.write(onlyKey,onlyValue);
          }
    }

}
