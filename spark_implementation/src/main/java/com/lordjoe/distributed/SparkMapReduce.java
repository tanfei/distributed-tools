package com.lordjoe.distributed;

import org.apache.spark.*;
import org.apache.spark.api.java.*;
import org.apache.spark.api.java.function.*;
import scala.*;

import java.io.Serializable;
import java.nio.file.*;
import java.util.*;

/**
 * com.lordjoe.distributed.SparkMapReduce
 * User: Steve
 * Date: 8/25/2014
 */
public class SparkMapReduce<VALUEIN extends Serializable, K extends Serializable, V extends Serializable> extends AbstractMapReduceEngine<VALUEIN, K, V> implements Serializable {

    private SparkConf sparkConf;
    private JavaSparkContext ctx;

    public SparkMapReduce(final IMapperFunction<VALUEIN, K, V> mapper, final IReducerFunction<K, V> reducer) {
        //noinspection unchecked
        this(mapper, reducer, IPartitionFunction.HASH_PARTITION);
    }

    public SparkMapReduce(final IMapperFunction<VALUEIN, K, V> pMapper,
                          final IReducerFunction<K, V> pRetucer,
                          IPartitionFunction<K> pPartitioner,
                          IKeyValueConsumer<K,V>... pConsumer) {
        setMap(pMapper);
        setReduce(pRetucer);
        setPartitioner(pPartitioner);

        for (int i = 0; i < pConsumer.length; i++) {
            IKeyValueConsumer<K, V> cns = pConsumer[i];
            addConsumer(cns);

        }
        sparkConf = new SparkConf().setAppName("JavaWordCount");
        sparkConf.setMaster("local");
        //  sparkConf.setExecutorEnv("spark.executor.extraClassPath","/SparkExamples/target/classes");
        // String[] jars = { "/SparkExamples/target/word-count-examples_2.10-1.0.0.jar" };
        //  sparkConf.setJars(jars);

        ctx = new JavaSparkContext(sparkConf);
    }


    protected Partitioner sparkPartitioner = new Partitioner() {
        @Override public int numPartitions() {
            return getNumberReducers();
        }

        @Override public int getPartition(final Object key) {
            IPartitionFunction<K> partitioner = getPartitioner();
            int value = partitioner.getPartition((K) key);
            return value % numPartitions();
        }
    } ;

    public SparkConf getSparkConf() {
        return sparkConf;
    }

    public JavaSparkContext getCtx() {
        return ctx;
    }

//    /**
//     * all the work is done here
//     *
//     * @param source
//     * @param sink
//     */
//    @Override
    public void performMapReduce(final Path source, final Path sink) {
//        Iterable<KeyValueObject<K, V>> holder = performMap(source);
//        Iterable<List<KeyValueObject<K, V>>> partition = partition(holder);
//        performReduce(partition);
//
   }



    /**
     * all the work is done here
     *
     * @param source
     * @param sink
     */
    //@Override
    public void performSourceMapReduce(final JavaRDD<VALUEIN> pInputs) {

        FlatMapFunction<Iterator<KeyValueObject<K, V>>, Tuple2<K, V>> partitioner = (FlatMapFunction<Iterator<KeyValueObject<K, V>>, Tuple2<K, V>>) new PartitionAdaptor(getPartitioner());

        throw new UnsupportedOperationException("Fix This"); // ToDo
//        JavaRDD<Tuple2<K, V>> mappedKeys = pInputs.mapToPair(new PairFunction<VALUEIN, Object, Object>() {
//
//            @Override public Tuple2<Object, Object> call(final VALUEIN pVALUEIN) throws Exception {
//                return null;
//            }
//        });
//        JavaRDD<KeyValueObject<K, V>> mappedKeys = pInputs.flatMap(new MapFunctionAdaptor<VALUEIN, K, V>(getMap()));
//
//        JavaPairRDD<K, V> asTuples = mappedKeys.mapToPair(new KeyValuePairFunction<K, V>());
//
//        //  SparkUtilities.showPairRDD(asTuples); // stop and look
//
//
//        JavaPairRDD<K, V> kvJavaPairRDD =  asTuples.reduceByKey(new Function2<V, V, V>() {
//            @Override public V call(final V pV, final V pV2) throws Exception {
//                return null;
//            }
//        });
// //       JavaPairRDD<K, V> kvJavaPairRDD = asTuples.partitionBy(sparkPartitioner);
//
//        // SparkUtilities.showPairRDD(asTuples); // stop and look
//
//
//
//        JavaRDD javaRDD = kvJavaPairRDD.mapPartitions(new ReduceFunctionAdaptor(getReduce()));
//
//        SparkUtilities.showRDD(javaRDD); // stop and look
    }

//
//        List collect = javaRDD.collect();
//
//
//        showOutput();
//    }
//
//    protected void showOutput() {
//        List<IKeyValueConsumer<K, V>> consumers = getConsumers();
//        ListKeyValueConsumer<K, V> cnsmr = (ListKeyValueConsumer<K, V>) consumers.get(0);
//        List<KeyValueObject<K, V>> output = cnsmr.getList();
//        Collections.sort(output, KeyValueObject.KEY_COMPARATOR);
//        for (KeyValueObject<K, V> kv : output) {
//            System.out.println(kv.key + ":" + kv.value);
//        }
//    }
//
//    protected void performReduce(Iterable<List<KeyValueObject<K, V>>> partitions) {
//        for (List<KeyValueObject<K, V>> partition : partitions) {
//            handlePartition(partition);
//        }
//    }
//
//
//    protected void handlePartition(Iterable<KeyValueObject<K, V>> partition) {
//        IReducerFunction reduce = getReduce();
//        List<IKeyValueConsumer<K, V>> consumers1 = getConsumers();
//        IKeyValueConsumer<K, V>[] consumers = consumers1.toArray(new IKeyValueConsumer[consumers1.size()]);
//          K key = null;
//        List<V> holder = new ArrayList();
//        for (KeyValueObject<K, V> kv : partition) {
//            if (!kv.key.equals(key)) {
//                if (!holder.isEmpty()) {
//                    reduce.handleValues(key, holder, consumers);// todo this is values
//                }
//                holder.clear();
//                key = kv.key;
//            }
//            holder.add(kv.value);
//        }
//    }
//
//
//    protected Iterable<KeyValueObject<K, V>> performMap(final Path source) {
//        ISourceFunction source1 = getSource();
//        Iterable<VALUEIN> inputs = source1.readInput(source);
//        return performSourceMap(inputs);
//    }
//
//    protected Iterable<KeyValueObject<K, V>> performSourceMap(final Iterable<VALUEIN> pInputs) {
//        IMapperFunction map = getMap();
//        List<KeyValueObject<K, V>> holder = new ArrayList();
//        for (VALUEIN input : pInputs) {
//            Iterable<KeyValueObject<K, V>> iterable = map.mapValues(input);
//            for (KeyValueObject<K, V> kv : iterable) {
//                holder.add(kv);
//            }
//        }
//        return holder;
//    }
//
//    protected List<KeyValueObject<K, V>>[] buildPartitions() {
//        int numberReducers = getNumberReducers();
//        List<KeyValueObject<K, V>>[] partitions = new List[numberReducers];
//        for (int i = 0; i < partitions.length; i++) {
//            partitions[i] = new ArrayList<KeyValueObject<K, V>>();
//
//        }
//        return partitions;
//    }
//
//    /**
//     * perform shuffle in memory end up with
//     *
//     * @param maps
//     * @return
//     */
//    protected Iterable<List<KeyValueObject<K, V>>> partition(Iterable<KeyValueObject<K, V>> maps) {
//        IPartitionFunction partitioner = getPartitioner();
//        int numberReducers = getNumberReducers();
//        List<KeyValueObject<K, V>>[] partitions = buildPartitions();
//
//        for (KeyValueObject<K, V> kv : maps) {
//            int index = partitioner.getPartition(kv.key) % numberReducers;
//            partitions[index].add(kv);
//        }
//        for (List<KeyValueObject<K, V>> partition : partitions) {
//            Collections.sort(partition, KeyValueObject.KEY_COMPARATOR);
//        }
//        return Arrays.asList(partitions);
//    }


}
