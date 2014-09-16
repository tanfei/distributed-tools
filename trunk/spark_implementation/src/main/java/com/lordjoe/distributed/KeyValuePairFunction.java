package com.lordjoe.distributed;

import org.apache.spark.api.java.function.*;
import scala.*;

import java.io.Serializable;

/**
* com.lordjoe.distributed.KeyValuePairFunction
* User: Steve
* Date: 9/12/2014
*/
class KeyValuePairFunction<K extends Serializable, V extends Serializable> implements PairFunction<KeyValueObject<K, V>, K, V>,Serializable {
    @Override public Tuple2<K, V> call(final KeyValueObject<K, V> kv) throws Exception {
          return new Tuple2<K, V>(kv.key,kv.value);
    }
}
