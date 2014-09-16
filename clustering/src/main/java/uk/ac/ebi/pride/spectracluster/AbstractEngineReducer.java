package  uk.ac.ebi.pride.spectracluster;

import com.lordjoe.algorithms.*;
import com.lordjoe.distributed.*;
import uk.ac.ebi.pride.spectracluster.cluster.*;
import uk.ac.ebi.pride.spectracluster.engine.*;
import uk.ac.ebi.pride.spectracluster.hadoop.*;
import uk.ac.ebi.pride.spectracluster.keys.*;

import java.util.*;

/**
 * main.uk.ac.ebi.pride.spectracluster.AbstractEngineReducer
 * User: Steve
 * Date: 9/2/2014
 */
public abstract class AbstractEngineReducer  implements IReducerFunction< IKeyable, ICluster> {

    private IKeyable currentkey;
    private IWideBinner binner = HadoopDefaults.DEFAULT_WIDE_MZ_BINNER;
    private IncrementalClusteringEngineFactory factory = new IncrementalClusteringEngineFactory();
    private IIncrementalClusteringEngine engine;

    protected void replaceEngine(IIncrementalClusteringEngine newengine, final IKeyValueConsumer< IKeyable, ICluster>... consumers)
    {
        if(engine != null) {
              for (ICluster cluster : engine.getClusters()) {
                  KeyValueObject<IKeyable, ICluster> kv = new KeyValueObject<>(currentkey, cluster);
                  for (int i = 0; i < consumers.length; i++) {
                      IKeyValueConsumer<IKeyable, ICluster> consumer = consumers[i];
                      consumer.consume(kv);

                  }
              }
        }
        engine = newengine;
    }


    protected void maybeReplaceEngine(final IKeyable key, final IKeyValueConsumer< IKeyable, ICluster>... consumer) {
        boolean doReplace = engine == null;
        IKeyable usedKey = currentkey;
        if(currentkey != null) {
            doReplace |= !keysMatch(currentkey,key);
        }
        else {
            doReplace = true;
            usedKey = key;
        }
        IIncrementalClusteringEngine engine = buildClusteringEngine(key);
        replaceEngine(engine,consumer);
        if(currentkey != usedKey)
            currentkey = usedKey;
    }

    protected abstract IIncrementalClusteringEngine buildClusteringEngine(final IKeyable pKey);

    public IKeyable getCurrentkey() {
        return currentkey;
    }

    public IWideBinner getBinner() {
        return binner;
    }

    public IncrementalClusteringEngineFactory getFactory() {
        return factory;
    }

    public IIncrementalClusteringEngine getEngine() {
        return engine;
    }

    /**
     * do these keys match
     * @param pCurrentkey
     * @param pKey
     * @return
     */
    protected abstract boolean keysMatch(final IKeyable pCurrentkey, final IKeyable pKey);


    /**
     * this is what a reducer does
     *
     * @param key
     * @param values
     * @param consumer @return iterator over mapped key values
     */
    @Override public void handleValues(final IKeyable key, final Iterable<ICluster> values, final IKeyValueConsumer<IKeyable, ICluster>... consumer) {
        maybeReplaceEngine( key, consumer);
         for (ICluster cluster : values) {
             final Collection<ICluster> removedClusters = engine.addClusterIncremental(cluster);
             for (ICluster removedCluster : removedClusters) {
                 KeyValueObject<IKeyable, ICluster> kv = new KeyValueObject<>(currentkey, removedCluster);
                 for (int i = 0; i < consumer.length; i++) {
                     IKeyValueConsumer<IKeyable, ICluster> cns = consumer[i];
                     cns.consume(kv);

                 }
              }

         }

    }


}
