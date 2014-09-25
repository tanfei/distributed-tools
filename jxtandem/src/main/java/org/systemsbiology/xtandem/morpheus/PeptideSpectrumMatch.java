package org.systemsbiology.xtandem.morpheus;

import org.systemsbiology.xtandem.*;
import org.systemsbiology.xtandem.ionization.*;
import org.systemsbiology.xtandem.peptide.*;

/**
 * org.systemsbiology.xtandem.morpheus.PeptideSpectrumMatch
 * stolen from the same class in Morpheus
 * User: steven
 * Date: 2/5/13
 */
public class PeptideSpectrumMatch {
    private static MassType precursorMassType = MassType.monoisotopic;

    private final ITheoreticalSpectrum m_Spectrum;
    private final IPolypeptide m_Peptide;
    private final double m_PrecursorMassErrorDa;
    private final double m_PrecursorMassErrorPpm;
    private int m_MatchingProducts;
    private int m_TotalProducts;
    private double m_MatchingProductsFraction;
    private double m_MatchingIntensity;
    private double m_MatchingIntensityFraction;

    private double m_MorpheusScore;


    //    private static readonly ProductTypes PRODUCT_TYPES = ProductTypes.Instance;

    public static void setPrecursorMassType(MassType precursorMassType) {
        PeptideSpectrumMatch.precursorMassType = precursorMassType;
    }


    public PeptideSpectrumMatch(final ITheoreticalSpectrum spectrum, IPolypeptide peptide, double productMassTolerance) {
        m_Spectrum = spectrum;
        m_Peptide = peptide;

        double spectrumMass = spectrum.getPeptide().getMass();

        m_PrecursorMassErrorDa = spectrumMass - (precursorMassType == MassType.average ? peptide.getMass() : peptide.getMass());
        m_PrecursorMassErrorPpm = m_PrecursorMassErrorDa / (precursorMassType == MassType.average ? peptide.getMass() : peptide.getMass()) * 1e6;

        ScoreMatch(productMassTolerance);
    }


    private void ScoreMatch(double productMassTolerance) {
        if (true)
            throw new UnsupportedOperationException("Fix This"); // ToDo
//          double[] theoretical_product_masses = m_Peptide.CalculateProductMasses(PRODUCT_TYPES[m_Spectrum.FragmentationMethod]).ToArray();
//          m_TotalProducts = theoretical_product_masses.length;
//
//          // speed optimizations
//          int num_theoretical_products = theoretical_product_masses.length;
//          ITheoreticalPeak[] theoreticalPeaks = m_Spectrum.getTheoreticalPeaks();
//          int num_experimental_peaks = theoreticalPeaks.length;
//          double[] experimental_masses = m_Spectrum.Masses;
//          double[] experimental_intensities = m_Spectrum.Intensities;
//          double product_mass_tolerance_value = productMassTolerance;
////            double product_mass_tolerance_value = productMassTolerance.Value;
////         m_MassToleranceUnits product_mass_tolerance_units = productMassTolerance.Units;
//
//          m_MatchingProducts = 0;
//          int t = 0;
//          int e = 0;
//          while(t < num_theoretical_products && e < num_experimental_peaks)
//          {
//              double mass_difference = experimental_masses[e] - theoretical_product_masses[t];
//              if(Math.abs(mass_difference) <= product_mass_tolerance_value)
//              {
//                  m_MatchingProducts++;
//                  t++;
//              }
//              else if(mass_difference < 0)
//              {
//                  e++;
//              }
//              else if(mass_difference > 0)
//              {
//                  t++;
//              }
//          }
//          m_MatchingProductsFraction = (double)m_MatchingProducts / m_TotalProducts;
//
//          m_MatchingIntensity = 0.0;
//          int e2 = 0;
//          int t2 = 0;
//          while(e2 < num_experimental_peaks && t2 < num_theoretical_products)
//          {
//              double mass_difference = experimental_masses[e2] - theoretical_product_masses[t2];
//                if(Math.abs(mass_difference) <= product_mass_tolerance_value)
//              {
//                  m_MatchingIntensity += experimental_intensities[e2];
//                  e2++;
//              }
//              else if(mass_difference < 0)
//              {
//                  e2++;
//              }
//              else if(mass_difference > 0)
//              {
//                  t2++;
//              }
//          }
//          m_MatchingIntensityFraction = m_MatchingIntensity / m_Spectrum.TotalIntensity;
//
//          m_MorpheusScore = m_MatchingProducts + m_MatchingIntensityFraction;
    }
//
//      public static int AscendingSpectrumNumberComparison(PeptideSpectrumMatch left, PeptideSpectrumMatch right)
//      {
//          return left.m_Spectrum.ScanNumber.CompareTo(right.m_Spectrum.ScanNumber);
//      }
//
//      public static int DescendingMorpheusScoreComparison(PeptideSpectrumMatch left, PeptideSpectrumMatch right)
//      {
//          int comparison = -(left.MorpheusScore.compareTo(right.MorpheusScore));
//          if(comparison != 0)
//          {
//              return comparison;
//          }
//          else
//          {
//              return left.Target.CompareTo(right.Target);
//          }
//      }

//      public static readonly string Header = "Filename\tScan Number\tRetention Time (min)\tPrecursor m/z\tPrecursor Charge\tPrecursor Mass (Da)\tExperimental Peaks\tTotal Intensity"
//          + "\tPeptide Sequence\tBase Peptide Sequence\tProtein Description\tStart Residue Number\tStop Residue Number\tMissed Cleavages"
//          + "\tTheoretical Mass (Da)\tPrecursor Mass Error (Da)\tPrecursor Mass Error (ppm)"
//          + "\tMatching Products\tTotal Products\tRatio of Matching Products\tMatching Intensity\tFraction of Intensity Matching\tMorpheus Score";
//
//      public override string ToString()
//      {
//          StringBuilder sb = new StringBuilder();
//
//          sb.Append(m_Spectrum.Filename + '\t');
//          sb.Append(m_Spectrum.ScanNumber.ToString() + '\t');
//          sb.Append(m_Spectrum.RetentionTime.ToString() + '\t');
//          sb.Append(m_Spectrum.PrecursorMZ.ToString() + '\t');
//          sb.Append(m_Spectrum.PrecursorCharge.ToString() + '\t');
//          sb.Append(m_Spectrum.PrecursorMass.ToString() + '\t');
//          sb.Append(m_Spectrum.Masses.Length.ToString() + '\t');
//          sb.Append(m_Spectrum.TotalIntensity.ToString() + '\t');
//          sb.Append(m_Peptide.ExtendedSequence + '\t');
//          sb.Append(m_Peptide.BaseSequence + '\t');
//          sb.Append(m_Peptide.Parent.Description + '\t');
//          sb.Append(m_Peptide.StartResidueNumber.ToString() + '\t');
//          sb.Append(m_Peptide.EndResidueNumber.ToString() + '\t');
//          sb.Append(m_Peptide.MissedCleavages.ToString() + '\t');
//          sb.Append((precursorMassType == MassType.Average ? m_Peptide.AverageMass : m_Peptide.MonoisotopicMass).ToString() + '\t');
//          sb.Append(m_PrecursorMassErrorDa.ToString() + '\t');
//          sb.Append(PrecursorMassErrorPpm.ToString() + '\t');
//          sb.Append(MatchingProducts.ToString() + '\t');
//          sb.Append(TotalProducts.ToString() + '\t');
//          sb.Append(MatchingProductsFraction.ToString() + '\t');
//          sb.Append(MatchingIntensity.ToString() + '\t');
//          sb.Append(MatchingIntensityFraction.ToString() + '\t');
//          sb.Append(MorpheusScore.ToString());
//
//          return sb.ToString();
//      }
}