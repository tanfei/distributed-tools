package com.lordjoe.distributed.hydra.scoring;

import com.lordjoe.distributed.*;
import com.lordjoe.distributed.tandem.*;
import org.apache.spark.api.java.*;
import org.systemsbiology.xtandem.*;
import org.systemsbiology.xtandem.hadoop.*;
import org.systemsbiology.xtandem.peptide.*;
import org.systemsbiology.xtandem.scoring.*;

import javax.annotation.*;
import java.util.*;

/**
 * com.lordjoe.distributed.hydra.scoring.ScoreCombiningReducer
 * User: Steve
 * Date: 10/6/2014
 */
public class ScoreCombiningReducer extends AbstractTandemFunction implements IReducerFunction<String, MultiScorer,String, ScoredScan>{
    public ScoreCombiningReducer(XTandemMain app) {
        super(app);
    }
    
    private int m_TotalScoredScans;

  
    public int getTotalScoredScans() {
        return m_TotalScoredScans;
    }

    public void addTotalScoredScans(final int added) {
        m_TotalScoredScans += added;
    }


    

    @Override
    public void setup( JavaSparkContext context)   {
        super.setup(context);
        // read configuration lines

        XTandemMain application = getApplication();

        boolean doHardCoded = application.getBooleanParameter(JXTandemLauncher.HARDCODED_MODIFICATIONS_PROPERTY,true);
        PeptideModification.setHardCodeModifications(doHardCoded);


        int NMatches = application.getIntParameter(JXTandemLauncher.NUMBER_REMEMBERED_MATCHES,XTandemHadoopUtilities.DEFAULT_CARRIED_MATCHES);
        XTandemHadoopUtilities.setNumberCarriedMatches(NMatches);

    }
    
    private boolean gPrintThisLine = false;


    /**
     * this is what a reducer does
     *
     * @param key
     * @param values
     * @param consumer @return iterator over mapped key values
     */
    @Nonnull
    @Override
    public void handleValues(@Nonnull final String keyStr, @Nonnull final Iterable<MultiScorer> values, final IKeyValueConsumer<String, ScoredScan>... consumer) {
          String id = "";

        try {
            // key ads charge to treat different charge states differently
            id =  keyStr;
        }
        catch (NumberFormatException e) {
            return; // todo fix

        }


        final XTandemMain app = getApplication();
        final Scorer scorer = app.getScoreRunner();
        Iterator<MultiScorer> textIterator = values.iterator();
        if(!textIterator.hasNext())
            return; // I don't think this can happen
        
        MultiScorer start = textIterator.next();

        boolean isScanAdded = false;
        /**
         * add each scan to the first producing a global scan
         */
        while (textIterator.hasNext()) {
            MultiScorer next = textIterator.next();
           start.addTo(next);
            isScanAdded = true;
        }

      // todo Save Start MultiScorer
        if(true)
            throw new UnsupportedOperationException("Fix This"); // ToDo

        getApplication().clearRetainedData();

    }


    @Override
    public void cleanup(JavaSparkContext context )  {
        super.cleanup(context );
    }
    

}
