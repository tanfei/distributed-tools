package com.lordjoe.distributed;

import org.apache.hadoop.io.*;
import org.systemsbiology.hadoop.*;

import java.io.*;

/**
 * com.lordjoe.distributed.MapperAdaptor
 * User: Steve
 * Date: 9/19/2014
 */
public class MapperAdaptor<KEYIN extends Serializable,VALUEIN extends Serializable,KOUT extends Serializable,VOUT extends Serializable> extends AbstractParameterizedMapper<Text> {

    private final IMapperFunction<KEYIN ,VALUEIN ,KOUT ,VOUT >  mapper;

    public MapperAdaptor(final IMapperFunction<KEYIN, VALUEIN, KOUT, VOUT> pMapper) {
        mapper = pMapper;
    }

    @Override
    protected void map(final Text key, final Text value, final Context context) throws IOException, InterruptedException {


        KEYIN k = null;
        VALUEIN v = null;

        Iterable<KeyValueObject<KOUT, VOUT>> out  = mapper.mapValues(k,v);
        for (KeyValueObject<KOUT, VOUT> x : out) {
             HadoopDistributedUtilities.writeKeyValueObject(x,context);
        }
    }
}
