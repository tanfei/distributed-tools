package com.lordjoe.distributed.hydra.scoring;

import com.lordjoe.distributed.*;
import com.lordjoe.distributed.tandem.*;
import com.lordjoe.utilities.*;
import org.apache.spark.api.java.*;
import org.systemsbiology.xtandem.*;
import org.systemsbiology.xtandem.hadoop.*;
import org.systemsbiology.xtandem.scoring.*;

import javax.annotation.*;

/**
 * com.lordjoe.distributed.hydra.scoring.ProteinFragmentReducer
 * User: Steve
 * Date: 10/6/2014
 */
public class ProteinFragmentReducer extends AbstractTandemFunction  implements IReducerFunction<String,String,String,IScoredScan > {
    private FastaHadoopLoader m_Loader;
       private int m_Proteins;
       private int m_ProteinsReported;
       private boolean m_GenerateDecoys;
       private boolean m_ShowProteins;

    public ProteinFragmentReducer(XTandemMain app) {
         super(app);
     }    
    
       public FastaHadoopLoader getLoader() {
           return m_Loader;
       }

       public boolean isShowProteins() {
           return m_ShowProteins;
       }

       public void setShowProteins(boolean showProteins) {
           m_ShowProteins = showProteins;
       }

       public boolean isGenerateDecoys() {
           return m_GenerateDecoys;
       }

       public void setGenerateDecoys(boolean generateDecoys) {
           m_GenerateDecoys = generateDecoys;
       }

       public void incrementNumberMappedProteins( ) {
           incrementCounter("Parser", "TotalProteins");
         }

       public static final boolean COUNT_AMINO_ACIDS = true;

       public void incrementNumberAminoAcids(  String sequence) {
           incrementCounter("Parser", "TotalAminoAcids",sequence.length());

           if (COUNT_AMINO_ACIDS) {
               int[] aaCount = new int[20];
               for (int i = 0; i < sequence.length(); i++) {
                   FastaAminoAcid aa = null;
                   try {
                       aa = FastaAminoAcid.fromChar(sequence.charAt(i));
                   } catch (BadAminoAcidException e) {
                       continue;
                   }
                   if (aa == null)
                       continue;
                   int index = FastaAminoAcid.asIndex(aa);
                   if (index < 0 || index >= 20)
                       continue;
                   aaCount[index]++;
               }
               for (int i = 0; i < aaCount.length; i++) {
                   int aaCounts = aaCount[i];
                   FastaAminoAcid aa = FastaAminoAcid.fromIndex(i);
                   incrementCounter("Parser", "AminoAcid" + aa,aaCounts);
                  }
           }
       }


       public void incrementNumberDecoysProteins( ) {
           incrementCounter("Parser", "TotalDecoyProteins");
         }

       /**
        * Called once at the beginning of the task.
        */
       @Override
       public void setup( JavaSparkContext ctx)   {
           super.setup( ctx);
           XTandemMain application = getApplication();
           //    application.loadTaxonomy();
           m_Loader = new FastaHadoopLoader(application);

           setGenerateDecoys(application.getBooleanParameter(XTandemUtilities.CREATE_DECOY_PEPTIDES_PROPERTY, Boolean.FALSE));

           setShowProteins(application.getBooleanParameter("org.systemsbiology.useSingleFastaItemSplit", false));

         }


    /**
     * this is what a reducer does
     *
     * @param key
     * @param values
     * @param consumer @return iterator over mapped key values
     */
    @Nonnull
    @Override
    public void handleValues(@Nonnull final String key, @Nonnull final Iterable<String> values, final IKeyValueConsumer<String, IScoredScan>... consumer) {


           String label = key.toString();
           boolean isDecoy = false;
           label = XTandemUtilities.conditionProteinLabel(label);
           for (String sequence : values) {

               // to deal with problems we need to show the protein handled
               if (isShowProteins()) {
                   System.err.println("Label:" + label);
                   System.err.println("Sequence:" + sequence);
               }
               // drop terminating *
               if (sequence.endsWith("*"))
                   sequence = sequence.substring(0, sequence.length() - 1);

               // if this returns true than the protein is already a decoy
               String decoyLabel = XTandemHadoopUtilities.asDecoy(label);
               if (decoyLabel != null) {
                   label = decoyLabel;
                   incrementNumberDecoysProteins();
                   isDecoy = true;
               }

               FastaHadoopLoader loader = getLoader();

               incrementNumberAminoAcids( sequence);
               incrementNumberMappedProteins( );
               loader.handleProtein(label, sequence   );

               // make a decoy
               if (isGenerateDecoys() && !isDecoy) {
                   // reverse the sequence
                   String reverseSequence = new StringBuffer(sequence).reverse().toString();
                   incrementNumberAminoAcids( reverseSequence);
                   incrementNumberMappedProteins( );
                   loader.handleProtein("DECOY_" + label, reverseSequence );
               }



           }
       }



       private void showStatistics() {
           ElapsedTimer elapsed = getElapsed();
           elapsed.showElapsed("Processed " + (m_Proteins - m_ProteinsReported) + " proteins at " + XTandemUtilities.nowTimeString() +
                   " total " + m_Proteins);
           // how much timeis in my code
           m_ProteinsReported = m_Proteins;

           elapsed.reset();
       }

       /**
        * Called once at the end of the task.
        */
       @Override
       public void cleanup( JavaSparkContext ctx)   {
           super.cleanup(ctx);    //To change body of overridden methods use File | Settings | File Templates.
           System.err.println("cleanup up Parser map");
           FastaHadoopLoader loader = getLoader();
           long numberPeptides = loader.getFragmentIndex();
           incrementCounter("Parser", "NumberFragments",numberPeptides);
             System.err.println("cleanup up Parser map done");
       }

}
