package org.systemsbiology.xtandem;

/**
 * org.systemsbiology.xtandem.XTandemConstants
 *   String constants used in the XTandem parameters file
 * @author Steve Lewis
   */
public class XTandemConstants
{
    public static XTandemConstants[] EMPTY_ARRAY = {};
    public static Class THIS_CLASS = XTandemConstants.class;

    public static final String VERSION = "0.001" ;

    public final String INPUT_01 = "list path, default parameters"; //default_input.xml");
    public final String INPUT_02 = "list path, taxonomy information"; //taxonomy.xml");
    public final String INPUT_03 = "protein, taxon"; //yeast");
    public final String INPUT_04 = "spectrum, path"; //test_spectra.mgf");
    public final String INPUT_05 = "output, path"; //output.xml");
    public final String INPUT_06 = "output, results"; //valid");

    public final String PARAM_000 = "list path, default parameters'"; // default_input.xml");
    public final String PARAM_001 = "list path, taxonomy information'"; // taxonomy.xml");
    public final String PARAM_002 = "spectrum, fragment monoisotopic mass error'"; // 0.4");
    public final String PARAM_003 = "spectrum, parent monoisotopic mass error plus'"; // 100");
    public final String PARAM_004 = "spectrum, parent monoisotopic mass error minus'"; // 100");
    public final String PARAM_005 = "spectrum, parent monoisotopic mass isotope error'"; // yes");
    public final String PARAM_006 = "spectrum, fragment monoisotopic mass error units'"; // Daltons");
    public final String PARAM_007 = "spectrum, parent monoisotopic mass error units'"; // ppm");
    public final String PARAM_008 = "spectrum, fragment mass type'"; // monoisotopic");
    public final String PARAM_009 = "spectrum, dynamic range'"; // 100.0");
    public final String PARAM_010 = "spectrum, total peaks'"; // 50");
    public final String PARAM_011 = "spectrum, maximum m_ParentStream charge'"; // 4");
    public final String PARAM_012 = "spectrum, use noise suppression'"; // yes");
    public final String PARAM_013 = "spectrum, minimum m_ParentStream m+h'"; // 500.0");
    public final String PARAM_014 = "spectrum, minimum fragment mz'"; // 150.0");
    public final String PARAM_015 = "spectrum, minimum peaks'"; // 15");
    public final String PARAM_016 = "spectrum, threads'"; // 1");
    public final String PARAM_017 = "spectrum, sequence batch size'"; // 1000");
    public final String PARAM_018 = "residue, modification mass'"; // 57.022@C");
    public final String PARAM_019 = "residue, potential modification mass'"; // ");
    public final String PARAM_020 = "residue, potential modification motif'"; // ");
    public final String PARAM_021 = "protein, taxon'"; // other mammals");
    public final String PARAM_022 = "protein, cleavage site'"; // [RK]|{P}");
    public final String PARAM_023 = "protein, modified residue mass file'"; // ");
    public final String PARAM_024 = "protein, cleavage C-terminal mass change'"; // +17.002735");
    public final String PARAM_025 = "protein, cleavage N-terminal mass change'"; // +1.007825");
    public final String PARAM_026 = "protein, N-terminal residue modification mass'"; // 0.0");
    public final String PARAM_027 = "protein, C-terminal residue modification mass'"; // 0.0");
    public final String PARAM_028 = "protein, homolog management'"; // no");
    public final String PARAM_029 = "refine'"; // yes");
    public final String PARAM_030 = "refine, modification mass'"; // ");
    public final String PARAM_031 = "refine, sequence path'"; // ");
    public final String PARAM_032 = "refine, tic percent'"; // 20");
    public final String PARAM_033 = "refine, spectrum synthesis'"; // yes");
    public final String PARAM_034 = "refine, maximum valid expectation value'"; // 0.1");
    /*
   public final String PARAM_000 ="refine, potential N-terminus modifications'"; // +42.010565@[");
   public final String PARAM_000 ="refine, potential C-terminus modifications'"; // ");
   public final String PARAM_000 ="refine, unanticipated cleavage'"; // yes");
   public final String PARAM_000 ="refine, potential modification mass'"; // ");
   public final String PARAM_000 ="refine, point mutations'"; // no");
   public final String PARAM_000 ="refine, use potential modifications for full refinement'"; // no");
   public final String PARAM_000 ="refine, point mutations'"; // no");
   public final String PARAM_000 ="refine, potential modification motif'"; // ");
   public final String PARAM_000 ="scoring, minimum ion count'"; // 4");
   public final String PARAM_000 ="scoring, maximum missed cleavage sites'"; // 1");
   public final String PARAM_000 ="scoring, x ions'"; // no");
   public final String PARAM_000 ="scoring, y ions'"; // yes");
   public final String PARAM_000 ="scoring, z ions'"; // no");
   public final String PARAM_000 ="scoring, a ions'"; // no");
   public final String PARAM_000 ="scoring, b ions'"; // yes");
   public final String PARAM_000 ="scoring, c ions'"; // no");
   public final String PARAM_000 ="scoring, cyclic permutation'"; // no");
   public final String PARAM_000 ="scoring, include reverse'"; // no");
   public final String PARAM_000 ="scoring, cyclic permutation'"; // no");
   public final String PARAM_000 ="scoring, include reverse'"; // no");
   public final String PARAM_000 ="output, log path'"; // ");
   public final String PARAM_000 ="output, message'"; // testing 1 2 3");
   public final String PARAM_000 ="output, one sequence copy'"; // no");
   public final String PARAM_000 ="output, sequence path'"; // ");
   public final String PARAM_000 ="output, path'"; // output.xml");
   public final String PARAM_000 ="output, sort results by'"; // protein");
   public final String PARAM_000 ="output, path hashing'"; // yes");
   public final String PARAM_000 ="output, xsl path'"; // tandem-style.xsl");
   public final String PARAM_000 ="output, parameters'"; // yes");
   public final String PARAM_000 ="output, performance'"; // yes");
   public final String PARAM_000 ="output, spectra'"; // yes");
   public final String PARAM_000 ="output, histograms'"; // yes");
   public final String PARAM_000 ="output, proteins'"; // yes");
   public final String PARAM_000 ="output, sequences'"; // yes");
   public final String PARAM_000 ="output, one sequence copy'"; // no");
   public final String PARAM_000 ="output, results'"; // valid");
   public final String PARAM_000 ="output, maximum valid expectation value'"; // 0.1");
   public final String PARAM_000 ="output, histogram column width'"; // 30");
      */
}
