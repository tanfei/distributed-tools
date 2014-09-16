package uk.ac.ebi.pride.spectracluster;

import com.lordjoe.distributed.*;
import uk.ac.ebi.pride.spectracluster.cluster.*;
import uk.ac.ebi.pride.spectracluster.keys.*;

/**
 * main.uk.ac.ebi.pride.spectracluster.ConsolidatingReducer
 * simply send the result to the consumer
 * User: Steve
 * Date: 9/2/2014
 */
public class ConsolidatingReducer implements IReducerFunction< IKeyable, ICluster> {


    /**
     * this is what a reducer does
     *
     * @param key
     * @param values
     * @param consumer @return iterator over mapped key values
     */
    @Override public void handleValues(final IKeyable key, final Iterable<ICluster> values, final IKeyValueConsumer<IKeyable, ICluster> consumer) {
         for (ICluster cluster : values) {
             consumer.consume(new KeyValueObject<IKeyable, ICluster>(key,cluster));
         }
    }
}
