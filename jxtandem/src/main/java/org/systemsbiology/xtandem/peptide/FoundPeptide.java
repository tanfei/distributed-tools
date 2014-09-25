package org.systemsbiology.xtandem.peptide;

/**
* org.systemsbiology.xtandem.peptide.FoundPeptide
* User: Steve
* Date: 8/15/12
*/
public class FoundPeptide {
    public static final FoundPeptide[] EMPTY_ARRAY = {};


    public static IPolypeptide toPolyPeptide(String s)   {
       String[] items = s.split("/");
        IPolypeptide pp =  Polypeptide.fromString(items[0]);
       return pp;

    }
    public static int toCharge(String s)   {
        String[] items = s.split("/");
        return  Integer.parseInt(items[1]);

    }

    private final IPolypeptide m_Peptide;
    private final String m_ProteinId;
    private final int m_Charge;

    public FoundPeptide(final IPolypeptide peptide, final String proteinId, int charge) {
        m_Peptide = peptide;
        m_ProteinId = proteinId;
        m_Charge = charge;
    }

    public FoundPeptide(final String peptide, final String proteinId) {
          this( toPolyPeptide(peptide),proteinId, toCharge(peptide));
      }

    public IPolypeptide getPeptide() {
        return m_Peptide;
    }

    public String getProteinId() {
        return m_ProteinId;
    }

    public int getCharge() {
        return m_Charge;
    }

    @Override
    public String toString() {
        return
                 m_Peptide.toString() +  "\t"
                  + m_ProteinId + "\t"
                  + m_Charge
                 ;
    }
}
