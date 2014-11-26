package com.lordjoe.distributed.hydra;

import com.esotericsoftware.kryo.*;
import org.apache.spark.serializer.*;

import javax.annotation.*;
import java.io.*;
import java.lang.reflect.*;
import java.util.*;

/**
 * com.lordjoe.distributed.hydra.HydraKryoSerializer
 * User: Steve
 * Date: 10/28/2014
 */
public class HydraKryoSerializer implements KryoRegistrator, Serializable {

    /**
     * register a class indicated by name but only if not already registered
     *
     * @param kryo
     * @param s       name of a class - might not exist
     * @param handled Set of classes already handles
     */
    protected void doRegistration(@Nonnull Kryo kryo, @Nonnull String s, @Nonnull Set<Class> handled) {
        Class c;
        try {
            c = Class.forName(s);
        }
        catch (ClassNotFoundException e) {
            return;
        }

        // do not register interfaces
        if (c.isInterface())
            return;

        // do not register enums
        if (c.isEnum())
            return;

        // do not register abstract classes
        if (Modifier.isAbstract(c.getModifiers()))
            return;

       // validateClass(c);

        if (handled.contains(c))
            return;
        handled.add(c);
        if (kryo != null)
            kryo.register(c);
    }


    public static final Class[] EMPTY_ARGS = {};

    /**
     * make sure class is OK
     *
     * @param pC
     */
    private void validateClass(final Class pC) {
        Constructor declaredConstructor;
        try {
            declaredConstructor = pC.getDeclaredConstructor(EMPTY_ARGS);
            declaredConstructor.setAccessible(true);
        }
        catch (NoSuchMethodException e) {
            Constructor[] declaredConstructors = pC.getDeclaredConstructors();
            System.err.println("No Empty Constructor " + pC);
                return;
           // throw new IllegalArgumentException("No Empty Constructor " + pC);
        }

        if (!Serializable.class.isAssignableFrom(pC))
            throw new IllegalArgumentException("Not Serializable " + pC);

        try {
            ObjectOutputStream os = new ObjectOutputStream(new ByteArrayOutputStream());
            Object o = declaredConstructor.newInstance();
            os.writeObject(o);
        }
        catch (IOException e) {
            throw new IllegalArgumentException("Cant Serialize " + pC);
        }
        catch (InstantiationException e) {
            throw new IllegalArgumentException("Cant instantiate " + pC);

        }
        catch (InvocationTargetException e) {
            throw new IllegalArgumentException("Cant access " + pC);
        }
        catch (IllegalAccessException e) {
            throw new IllegalArgumentException("Cant access " + pC);
        }

    }


    @Override
    public void registerClasses(@Nonnull Kryo kryo) {
        Set<Class> handler = new HashSet<Class>();
        doRegistration(kryo, "com.lordjoe.algorithms.CountedMap", handler);
        //    doRegistration(kryo, "com.lordjoe.distributed.AbstractLoggingFunction", handler);
        //    doRegistration(kryo, "com.lordjoe.distributed.AbstractLoggingFunction2", handler);
        //   doRegistration(kryo, "com.lordjoe.distributed.AbstractLoggingPairFunction", handler);
        doRegistration(kryo, "com.lordjoe.distributed.SparkUtilities", handler);
        doRegistration(kryo, "com.lordjoe.distributed.SparkUtilities$IdentityFunction", handler);
        doRegistration(kryo, "com.lordjoe.distributed.SparkUtilities$TupleValues", handler);
        doRegistration(kryo, "com.lordjoe.distributed.database.IDatabaseBean", handler);
        doRegistration(kryo, "com.lordjoe.distributed.database.PeptideDatabaseWriter", handler);
        doRegistration(kryo, "com.lordjoe.distributed.database.PeptideDatabaseWriter$ContinueCombiner", handler);
        doRegistration(kryo, "com.lordjoe.distributed.database.PeptideDatabaseWriter$MZPartitioner", handler);
        doRegistration(kryo, "com.lordjoe.distributed.database.PeptideDatabaseWriter$MergeCombiner", handler);
        doRegistration(kryo, "com.lordjoe.distributed.database.PeptideDatabaseWriter$StartCombiner", handler);
        doRegistration(kryo, "com.lordjoe.distributed.hydra.SparkFileOpener", handler);
        doRegistration(kryo, "com.lordjoe.distributed.hydra.SparkXTandemMain", handler);
        doRegistration(kryo, "com.lordjoe.distributed.hydra.peptide.PeptideSchemaBean", handler);
        doRegistration(kryo, "com.lordjoe.distributed.hydra.peptide.PeptideSchemaBean$1", handler);
        doRegistration(kryo, "com.lordjoe.distributed.hydra.peptide.PeptideSchemaBean$2", handler);
        doRegistration(kryo, "com.lordjoe.distributed.hydra.peptide.PeptideSchemaBean$3", handler);
        doRegistration(kryo, "com.lordjoe.distributed.hydra.protein.PolypeptideCombiner$MergePolyPeptides", handler);
        doRegistration(kryo, "com.lordjoe.distributed.input.FastaInputFormat", handler);
        doRegistration(kryo, "com.lordjoe.distributed.protein.DigestProteinFunction", handler);
        doRegistration(kryo, "com.lordjoe.distributed.spark.IdentityFunction", handler);
        doRegistration(kryo, "com.lordjoe.distributed.tandem.LibraryBuilder", handler);
       doRegistration(kryo, "com.lordjoe.distributed.tandem.LibraryBuilder$MapPolyPeptideToSequenceKeys", handler);
        doRegistration(kryo, "com.lordjoe.distributed.tandem.LibraryBuilder$PeptideByStringToByMass", handler);
        doRegistration(kryo, "com.lordjoe.distributed.tandem.LibraryBuilder$parsedProteinToProtein", handler);
        doRegistration(kryo, "com.lordjoe.distributed.tandem.LibraryBuilder$processByKey", handler);
        doRegistration(kryo, "com.lordjoe.lib.xml.RequiredAttributeNotFoundException", handler);
        doRegistration(kryo, "org.systemsbiology.hadoop.DelegatingFileStreamOpener", handler);
        doRegistration(kryo, "org.systemsbiology.hadoop.FileStreamOpener", handler);
        doRegistration(kryo, "org.systemsbiology.hadoop.HadoopMajorVersion", handler);
        doRegistration(kryo, "org.systemsbiology.hadoop.HadoopUtilities$KeepAliveEnum", handler);
        doRegistration(kryo, "org.systemsbiology.hadoop.HadoopUtilities$TaskFailException", handler);
        doRegistration(kryo, "org.systemsbiology.hadoop.IParameterHolder", handler);
        doRegistration(kryo, "org.systemsbiology.hadoop.ISetableParameterHolder", handler);
        //      doRegistration(kryo, "org.systemsbiology.hadoop.IStreamOpener", handler);
        doRegistration(kryo, "org.systemsbiology.hadoop.JobSizeEnum", handler);
        doRegistration(kryo, "org.systemsbiology.hadoop.StreamOpeners$ResourceStreamOpener", handler);
        doRegistration(kryo, "org.systemsbiology.sax.IXMLAppender", handler);
        doRegistration(kryo, "org.systemsbiology.sax.XMLAppender", handler);
        doRegistration(kryo, "org.systemsbiology.xtandem.AbstractParameterHolder", handler);
        doRegistration(kryo, "org.systemsbiology.xtandem.AbstractScoringAlgorithm", handler);
        doRegistration(kryo, "org.systemsbiology.xtandem.BadAminoAcidException", handler);
        doRegistration(kryo, "org.systemsbiology.xtandem.FastaAminoAcid", handler);
        doRegistration(kryo, "org.systemsbiology.xtandem.IEquivalent", handler);
        doRegistration(kryo, "org.systemsbiology.xtandem.IMainData", handler);
        doRegistration(kryo, "org.systemsbiology.xtandem.IMeasuredSpectrum", handler);
        doRegistration(kryo, "org.systemsbiology.xtandem.IScanPrecursorMZ", handler);
        doRegistration(kryo, "org.systemsbiology.xtandem.IScoringAlgorithm", handler);
        doRegistration(kryo, "org.systemsbiology.xtandem.ISpectrum", handler);
        doRegistration(kryo, "org.systemsbiology.xtandem.ISpectrumPeak", handler);
        doRegistration(kryo, "org.systemsbiology.xtandem.ITandemScoringAlgorithm", handler);
        doRegistration(kryo, "org.systemsbiology.xtandem.MassCalculator", handler);
        doRegistration(kryo, "org.systemsbiology.xtandem.MassCalculator$MassPair", handler);
        doRegistration(kryo, "org.systemsbiology.xtandem.MassSpecRun", handler);
        doRegistration(kryo, "org.systemsbiology.xtandem.MassType", handler);
        doRegistration(kryo, "org.systemsbiology.xtandem.RawPeptideScan", handler);
        doRegistration(kryo, "org.systemsbiology.xtandem.SequenceUtilities", handler);
        doRegistration(kryo, "org.systemsbiology.xtandem.SpectrumCondition", handler);
        doRegistration(kryo, "org.systemsbiology.xtandem.TandemKScoringAlgorithm", handler);
        doRegistration(kryo, "org.systemsbiology.xtandem.TandemKScoringAlgorithm$KScoringConverter", handler);
        doRegistration(kryo, "org.systemsbiology.xtandem.TandemScoringAlgorithm", handler);
        doRegistration(kryo, "org.systemsbiology.xtandem.TaxonomyProcessor", handler);
        doRegistration(kryo, "org.systemsbiology.xtandem.XTandemMain", handler);
        doRegistration(kryo, "org.systemsbiology.xtandem.ionization.ITheoreticalPeak", handler);
        doRegistration(kryo, "org.systemsbiology.xtandem.ionization.IonType", handler);
        doRegistration(kryo, "org.systemsbiology.xtandem.ionization.IonTypeScorer", handler);
        doRegistration(kryo, "org.systemsbiology.xtandem.ionization.IonUseCounter", handler);
        doRegistration(kryo, "org.systemsbiology.xtandem.ionization.IonUseScore", handler);
        doRegistration(kryo, "org.systemsbiology.xtandem.peptide.AminoTerminalType", handler);
        doRegistration(kryo, "org.systemsbiology.xtandem.peptide.DigesterDescription", handler);
        doRegistration(kryo, "org.systemsbiology.xtandem.peptide.IModifiedPeptide", handler);
        doRegistration(kryo, "org.systemsbiology.xtandem.peptide.IPeptideDigester", handler);
        doRegistration(kryo, "org.systemsbiology.xtandem.peptide.IPolypeptide", handler);
        doRegistration(kryo, "org.systemsbiology.xtandem.peptide.IProtein", handler);
        doRegistration(kryo, "org.systemsbiology.xtandem.peptide.IProteinPosition", handler);
        doRegistration(kryo, "org.systemsbiology.xtandem.peptide.ModifiedPolypeptide", handler);
        doRegistration(kryo, "org.systemsbiology.xtandem.peptide.PeptideBondDigester", handler);
        doRegistration(kryo, "org.systemsbiology.xtandem.peptide.PeptideBondDigester$LysineC", handler);
        doRegistration(kryo, "org.systemsbiology.xtandem.peptide.PeptideBondDigester$Trypsin", handler);
        doRegistration(kryo, "org.systemsbiology.xtandem.peptide.PeptideDigester", handler);
        doRegistration(kryo, "org.systemsbiology.xtandem.peptide.PeptideModification", handler);
        doRegistration(kryo, "org.systemsbiology.xtandem.peptide.PeptideModificationRestriction", handler);
        doRegistration(kryo, "org.systemsbiology.xtandem.peptide.PeptideValidity", handler);
        doRegistration(kryo, "org.systemsbiology.xtandem.peptide.Polypeptide", handler);
        doRegistration(kryo, "org.systemsbiology.xtandem.peptide.Protein", handler);
        doRegistration(kryo, "org.systemsbiology.xtandem.peptide.ProteinPosition", handler);
        doRegistration(kryo, "org.systemsbiology.xtandem.scoring.IExtendedSpectralMatch", handler);
        doRegistration(kryo, "org.systemsbiology.xtandem.scoring.IMZToInteger", handler);
        doRegistration(kryo, "org.systemsbiology.xtandem.scoring.IScoredScan", handler);
        doRegistration(kryo, "org.systemsbiology.xtandem.scoring.ISpectralMatch", handler);
        doRegistration(kryo, "org.systemsbiology.xtandem.scoring.Scorer", handler);
        doRegistration(kryo, "org.systemsbiology.xtandem.scoring.ScoringModifications", handler);
        doRegistration(kryo, "org.systemsbiology.xtandem.taxonomy.ITaxonomy", handler);
        doRegistration(kryo, "org.systemsbiology.xtandem.taxonomy.Taxonomy", handler);
        doRegistration(kryo, "com.lordjoe.distributed.SparkUtilities", handler);
        doRegistration(kryo, "com.lordjoe.distributed.SparkHydraUtilities", handler);
        doRegistration(kryo, "com.lordjoe.distributed.hydra.scoring.SparkMapReduceScoringHandler", handler);
        doRegistration(kryo, "com.lordjoe.distributed.spark.SparkAccumulators", handler);
        doRegistration(kryo, "com.lordjoe.distributed.spark.Statistics", handler);
        doRegistration(kryo, "com.lordjoe.distributed.spark.LongAccumulableParam", handler);
        doRegistration(kryo, "com.lordjoe.distributed.spark.StringAccumulableParam", handler);
        doRegistration(kryo, "com.lordjoe.distributed.spark.StringSetAccumulableParam", handler);
        doRegistration(kryo, "com.lordjoe.distributed.spark.MachineUseAccumulator", handler);



    }


    public static void main(String[] args) {
        // Does not work and I am not sure we need it
        // this will fail if there are issues with any registered classes
        new HydraKryoSerializer().registerClasses(null);
    }

}


