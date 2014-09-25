package org.systemsbiology.xtandem;

import java.util.*;

/**
 * org.systemsbiology.xtandem.XTandemProcess
 *
 * @author Steve Lewis
 * @date Jan 4, 2011
 */
/*
   Modified 2010 Insilicos LLC for MapReduce X!Tandem
*/

/*
 * the process object coordinates the function of tandem. it contains the information
 * loaded from the input XML file in the m_xmlValues object and performance
 * information in the m_xmlPerformance object. The mass spectra to be analyzed are
 * in the m_vSpectra vector container. A set of input parameters are used to
 * initialize constants that are used in processing the mass spectra.
 * NOTE: see tandem.cpp for an example of how to use an mprocess class
 * NOTE: mprocess uses cout to report errors. This may not be appropriate for
 *       many applications. Feel free to change this to a more appropriate mechanism
 */

public class XTandemProcess
{
    public static XTandemProcess[] EMPTY_ARRAY = {};
    public static Class THIS_CLASS = XTandemProcess.class;

/*
 * MSemiState is a specialty class used to store information necessary for the state machine that
 * mprocess uses to perform semi-enzymatic cleavages
 */

    static class mpyrostate
    {
          public boolean m_bPotential;
        public boolean m_bPyro;
        public double m_dModMass;
        public char m_cRes;
    }


    public static  class MSemiState
    {
//private:  for serialization access bpratt
        boolean m_bActive;
        long m_lStart;
        long m_lEnd;
        long m_lStartI;
        long m_lEndI;
        boolean m_bStart;
        long m_lLimit;
        long m_lLastCleave;

          MSemiState() {
            m_lLastCleave = -1;
            m_lStart = 0;
            m_lEnd = 0;
            m_lStartI = 0;
            m_lEndI = 0;
            m_bStart = true;
            m_lLimit = 5;
            m_bActive = false;
          }
             boolean activate(final boolean _b)	{
              m_bActive = _b;
              return m_bActive;
          }

          boolean is_active()	{
              return m_bActive;
          }

          long limit(final long _l)	{
              if(_l >= 0)	{
                  m_lLimit = _l;
              }
              return m_lLimit;
          }

          public  boolean reset(final long _s,final long _e,final long _c)	{
              if(!m_bActive)	{
                  return false;
              }
              m_lLastCleave = _c;
              m_lStartI = _s;
              m_lEndI =_e;
              m_lStart = _s;
              m_lEnd = _e;
              m_lEnd--;
              m_bStart = true;
              return true;
          }

          public  boolean next(long[]_s,long[]_e)	{
              if(!m_bActive)	{
                  return false;
              }
              if(m_bStart)	{
                  _s[0] = m_lStart;
                  _e[0] = m_lEnd;
                  m_lEnd--;
                  if(m_lEnd == m_lLastCleave || m_lEnd - m_lStart < m_lLimit)	{
                      m_bStart = false;
                      m_lEnd = m_lEndI;
                      m_lStart++;
                  }
                  return true;
              }
              else	{
                  if((m_lLastCleave != -1 && m_lStart > m_lLastCleave) || m_lEnd - m_lStart < m_lLimit)	{
                      _s[0] = m_lStartI;
                      _e[0] = m_lEndI;
                      return false;
                  }
                  _s[0] = m_lStart;
                  _e[0] = m_lEnd;
                  m_lStart++;
                  return true;
              }
            }

    }

/*booleanean contrast()
{
	int a = 0;
	int tSize = m_vSpectra.size();
	int tSpecSize = 0;
	int b = 0;
	int c = 0;
	long lValue = 0;
	int lSize = 200;
	float *pArray = new float[lSize];
	float *pAA = new float[lSize];
	b = 0;
	while(b < lSize)	{
		pAA[b] = 0;
		b++;
	}
	pAA[71] = 1;
	pAA[115] = 1;
	pAA[103] = 1;
	pAA[115] = 1;
	pAA[129] = 1;
	pAA[147] = 1;
	pAA[57] = 1;
	pAA[137] = 1;
	pAA[113] = 1;
	pAA[128] = 1;
	pAA[113] = 1;
	pAA[131] = 1;
	pAA[114] = 1;
	pAA[97] = 1;
	pAA[128] = 1;
	pAA[156] = 1;
	pAA[87] = 1;
	pAA[101] = 1;
	pAA[180] = 1;
	pAA[99] = 1;
	pAA[186] = 1;
	pAA[163] = 1;

	float fMax;
	List<String> vStrings;
	String strValue;
	char pValue[256];
	b = 0;
	while(b < lSize)	{
		vStrings.push_back(strValue);
		b++;
	}
	ofstream ofLog;
	long lLowCount = 0;
	ofLog.open("c:\\thegpm\\autocor.csv");
	List<Spectrum>::iterator itStart = m_vSpectra.begin();
	while(itStart != m_vSpectra.end())	{
		b = 0;
		while(b < lSize)	{
			pArray[b] = 0;
			b++;
		}
		b = 0;
		tSpecSize = itStart.m_vMI.size();
		fMax = 0;
		while(b < tSpecSize)	{
			c = 0;
			while(c < tSpecSize)	{
				if(c != b)	{
					lValue = (long)(0.5+Math.abs(itStart.m_vMI[b].m_fM - itStart.m_vMI[c].m_fM));
					if(lValue < lSize    lValue > 50)	{
						pArray[lValue] += itStart.m_vMI[b].m_fI * itStart.m_vMI[c].m_fI;
						if(pArray[lValue] > fMax)	{
							fMax = pArray[lValue];
						}
					}
				}
				c++;
			}
			b++;
		}
		b = 0;
		float fValue = 0.0;
		while(b < lSize)	{
			sprintf(pValue,"%f,",pArray[b]*pAA[b]/fMax);
			if(pArray[b]*pAA[b]/fMax > fValue)	{
				fValue = pArray[b]*pAA[b]/fMax;
			}
			vStrings[b] += pValue;
			b++;
		}
		ofLog  << itStart.m_tId << "[" << fValue << "],";
		if(fValue < 0.3    itStart.m_fZ < 2.5)	{
			itStart = m_vSpectra.erase(itStart);
			lLowCount++;
		}
		else	{
			itStart++;
		}
	}
	ofLog << "0\n";
	b = 0;
	cout << "<BR>" << lLowCount << "/" << m_vSpectra.size() << "<BR>";
	while(b < lSize)	{
		ofLog << vStrings[b] << "0\n";
		b++;
	}
	delete pArray;
	delete pAA;
	ofLog.close();
	return true;
}*/


    public static class MRefine {
        public MRefine() {
            throw new UnsupportedOperationException("Fix This"); // ToDo
        }
    }

      public static class ProteinSequenceServer {
        public ProteinSequenceServer() {
            throw new UnsupportedOperationException("Fix This"); // ToDo
        }

        public long load_file(String strTaxonPath,String s) {
            throw new UnsupportedOperationException("Fix This"); // ToDo
        }
      }


/*
 * global less than operators to be used in sort operations
 */
    
     private final  Map<String,String> m_xmlPerformance = new HashMap<String,String>(); // stores process performance parameters
     private final  Map<String,String> m_xmlValues = new HashMap<String,String>(); // store process input parameters
     private final List<Spectrum> m_vSpectra = new ArrayList<Spectrum>(); // store spectra to be analyzed
     private final Map<String,String>  m_mapSequences = new HashMap<String,String>(); // a map containing all of the protein sequences discovered, indexed by their m_tUid value
     private final List<ProteinSequence> m_vseqBest = new ArrayList<ProteinSequence>(); // a vector of msequences used in the model refinement process
     private final List<String> m_vstrModifications = new ArrayList<String>(); //a vector containing the strings defining fixed modifications for a protein
    private int m_tRefineModels; // total number of models generated by refinement
    private int m_tRefineInput; // total number of sequences included in a refinement session
    private int m_tRefinePartial; // the number of models discovered to have partial cleavage
    private int m_tRefineUnanticipated; // the number of models discovered to have unanticpated cleavage
    private int m_tRefineNterminal; // the number of models discovered to have modified N-terminii
    private int m_tRefineCterminal; // the number of models discovered to have modified C-terminii
    private int m_tRefinePam; // the number of models discovered to have point mutations
    private double m_dRefineTime; // the time required to perform a refinement
    private int m_tActive;	// total number of models remaining after each refinement step
    private boolean m_bRefineCterm;  //true if processing 'refine, potential C-terminus modifications'. Set in MRefine::refine and
                        //checked in score(), so the start position can be set to the length of the protein sequence
                        // minus the value for 'refine, potential N-terminus modification position limit' before performing cleavage
    private final List<Integer> m_viQuality = new ArrayList<Integer>();; // contains the data quality scoring vector
    private boolean m_bReversedOnly;
    private boolean m_bSaps;
     private boolean m_bAnnotation;
 
    //protected: changed for serialization
    private  String m_strLastMods;
    private int m_iCurrentRound;
    private boolean m_bPermute;
    private boolean m_bPermuteHigh;
    private boolean m_bCrcCheck;
     private final Set<Integer> m_setRound = new HashSet<Integer>();
    // single polymorphisms
    private final  List<String> m_vstrSaps = new ArrayList<String>();;
     private final List<String> m_vstrMods = new ArrayList<String>();;
     private final Map<String,String> m_mapAnnotation = new HashMap<String,String>();;
     private SemiState m_semiState; // maintains the state of the semi-enzymatic cleavage state machine
     private mpyrostate m_pyroState; // maintains the state of the pyrolidone carboxylic acid detection state machine
 //    private   final XTandemErrors m_errValues = new XTandemErrors();
    private double m_dSearchTime; // total time elapsed during a protein modeling session process
    private long m_lIonCount; // minimum sum of detected ions that are significant enough to store a sequence
    private int m_lThread; // thread number of this object
     private int m_lThreads; // the total number of threads current active
     private long m_lReversed; // the total number of peptides found where the reversed sequence was better than the forward sequence
     private double m_dThreshold; // the current expectation value threshold
    private  double m_tContrasted; // the number of spectra subtracted using contrast angle redundancy detection
    private  long m_lStartMax; // set the maximum distance from N-terminus for a peptide
                      // normally set at an impossibly large value = 100,000,000
                      // for ragged N-terminii with potential modifications, set at a low but plausible value = 50
    private long m_lCStartMax;
    private int m_pSeq; // a character pointer, used for temporary sequence information
    private boolean m_bUn; // if true, cleave at all residues. if false, use cleavage specification in data input.
    private boolean m_bUseHomologManagement; // set to true to use homologue management
    private int m_tMinResidues; // the minimum peptide length that will be scored
    private int m_tMissedCleaves; // the maximum number of cleavage sites that can be missed
    private int m_tPeptideCount; // the total number of peptide sequences generated during a process
    private int m_tPeptideScoredCount; // the total number of peptide sequences scored during a process
    private int m_tProteinCount; // the total number of protein sequences considered during a process
    private int m_tSpectra; // the total number of spectra being modeled
    private int m_tSpectraTotal; // the total number of spectra in the input file
    private int m_tValid; // the number of valid peptide models
    private int m_tTotalResidues; // the number of residues read
    private int m_tSeqSize; // current length of the m_pSeq character array
    private int m_tUnique; // the number of unique peptides found in a result
    private String m_strOutputPath; // the path name of the XML output file
    private ProteinClevage m_Cleave; // the specification for a cleavage peptide bond
    private  ProteinSequence m_seqCurrent; // the  ProteinSequence object that is currently being scored
 
    private  ProteinSequenceServer m_svrSequences; // the  ProteinSequenceServer object that provides  ProteinSequences to  ProteinSequenceCollection
 
    private SpectrumCondition m_specCondition; // the mspectrumcondition object that cleans up and normalized
                                        // spectra for further processing
   private MScore m_pScore; // the object that is used to score sequences and spectra
    private MRefine m_pRefine; // the object that is used to refine models
    private XTamdemProcessLog m_prcLog;
    
    
    public static boolean lessThanSequence(final   ProteinSequence  _l,final   ProteinSequence  _r)
    {
        return _l.getdExpect() < _r.getdExpect();
    }

    public static boolean lessThanSequenceUid(final   ProteinSequence  _l,final   ProteinSequence  _r)
    {
        return _l.gettUid() < _r.gettUid();
    }

    public static boolean lessThanSequenceDes(final   ProteinSequence  _l,final   ProteinSequence  _r)
    {
        return _l.getStrDes().length() < _r.getStrDes().length();
    }

    public static boolean lessThanSpectrum(final  Spectrum  _l,final  Spectrum  _r)
    {
        return _l.getdProteinExpect() < _r.getdProteinExpect();
    }

    public static boolean lessThanSpectruProteinSequence(final  Spectrum  _l,final  Spectrum  _r)
    {
        if(_l.getSpectrumCount() == 0)
            return false;
        if(_r.getSpectrumCount() == 0)
            return true;
        return _l.getdExpect() < _r.getdExpect();
    }

    public static boolean lessThanOrder(final  Spectrum  _l,final  Spectrum  _r)
    {
        if(_l.getSpectrumCount() == 0)
             return false;
         if(_r.getSpectrumCount() == 0)
             return true;
         return _l.getBestSequence().getvDomains().get(0).getlS() < _r.getBestSequence().getvDomains().get(0).getlS();
    }

//    public static boolean lessThanMass(final  mi  _l,final  mi  _r)
//    {
//        return _l.m_fM < _r.m_fM;
//    }

    public XTandemProcess()
    {

        m_iCurrentRound = 1;
        m_lReversed = -1;
        m_tMissedCleaves = 1;
/*
 * record the process start time
 */
          String strKey;
           strKey = "process, start time";
         m_xmlPerformance.put(strKey,new Date().toString());
/*
 * record the version of the software
 */
        strKey = "process, version";
        String strValue = "jx! tandem ";
         strValue += XTandemConstants.VERSION;
        m_xmlPerformance.put(strKey,strValue);
          m_lThreads = 1;
      //   m_tSeqSize = 4096*4;
      //  m_pSeq = new char[m_tSeqSize];
        m_lStartMax = 100000000;
        m_dThreshold = 1000.0;
         m_bUseHomologManagement = false;
         m_lCStartMax = 50;
      }

 /*
* add spectra is used to load spectra into the m_vSpectra vector
*/
    public boolean add_spectra(List<Spectrum>  _v)
    {
        m_vSpectra.addAll(_v);
           return true;
    }
/*
 * clean_sequences is used to maintain the m_mapSequences container. If a sequence
 * has been removed from the m_vSpectra list of sequences, then that sequence is
 * subsequently deleted from the m_mapSequences collection
*/
   public  boolean clean_sequences()
    {
        throw new UnsupportedOperationException("Fix This"); // ToDo
//         Map<Integer,Integer> mapValue;
//         Map<Integer,Integer>::iterator itMap;
//        int a = 0;
//        int b = 0;
//        int tLength = m_vSpectra.size();
//        int tBest = 0;
//        while(a < tLength)	{
//            b = 0;
//            tBest = m_vSpectra[a].m_vseqBest.size();
//            while(b < tBest)	{
//                mapValue[m_vSpectra[a].m_vseqBest[b].m_tUid] = 1;
//                b++;
//            }
//            a++;
//        }
//        SEQMAP::iterator itValue = m_mapSequences.begin();
//        while(itValue != m_mapSequences.end())	{
//            itMap = mapValue.find((*itValue).first);
//            if(itMap == mapValue.end())	{
//                m_mapSequences.erase(itValue);
//                itValue = m_mapSequences.begin();
//            }
//            else	{
//                itValue++;
//            }
//        }
//        return true;
    }
/*
* clear is used to reset any vector or  Map object necessary to reset the mprocess object
*/
    public boolean clear()
    {
        m_vSpectra.clear();
        if (m_pScore != null)
            m_pScore.clear();
        return true;
    }

/*
* create_rollback is used to create a vector of Spectrum objects that serves as
* the record of values to be used by the rollback method.
*/
    public boolean create_rollback(List<Spectrum>  _v)
    {
        _v.clear();
        _v.addAll(m_vSpectra);
//        int a = 0;
//        final  int tSize = m_vSpectra.size();
//        Spectrum spTemp;
//        double dExpect = 0;
//        _v.reserve(tSize);
//        while(a < tSize)	{
//            _v.push_back(spTemp);
//            _v.back() *= m_vSpectra[a];
//            m_vSpectra[a].m_hHyper.model();
//            m_vSpectra[a].m_hHyper.set_protein_factor(1.0);
//            dExpect = (double)m_vSpectra[a].m_hHyper.expect_protein(m_pScore.hconvert(m_vSpectra[a].m_fHyper));
//            _v.back().m_dExpect = dExpect;
//            a++;
//        }
        return true;
    }
/*
 * create_score takes an  ProteinSequence object and a start and an end sequence position
 * and saves that  ProteinSequence, its mdomain and scoring in the spectrum that has just
 * been scored. equivalent  ProteinSequence objects (based on their hyper score)
 * are stored sequentially in a List in the Spectrum object
 */
    public boolean create_score(final   ProteinSequence  _s,final  int _v,final  int _w,final  long _m,boolean _p)
    {
        long lIonCount = 0;
        float fScore = -1.0F;
        float fHyper = -1.0F;
        int a = 0;
        int b = 0;
        int c = 0;
        boolean bOk = false;
        boolean bDom = false;
        long lCount = 0;
        boolean bIonCheck = false;
        boolean bMassCheck = false;
        throw new UnsupportedOperationException("Fix This"); // ToDo
///*
// * score each Spectrum identified as a candidate in the m_pScore.m_State object
// */
//        while(lCount < m_pScore.m_State.m_lEqualsS)	{
//            a = m_pScore.m_State.m_plEqualsS[lCount];
//            lCount++;
//            lIonCount = 0;
///*
//* this check is needed to keep tandem consistent whether running on
//* single-threaded, multi-threaded or on a cluster.  otherwise, when
//* there are multiple spectra matching a sequence (which is more common
//* the fewer mprocess objects there are) one can cause others to score
//* more permutation sequences.
//*/
//            if (!_p  &&  m_vSpectra[a].m_hHyper.m_ulCount >= 400)
//                continue;
//            fScore = 1.0F;
//            fHyper = 1.0F;
//            m_pScore.m_lMaxCharge = (long)(m_vSpectra[a].m_fZ+0.1);
///*
// * in versions prior to 2004.03.01, spectra with m_bActive == false were
// * rejected at this point, to save time   because of a problem with
// * multiple recording of the same sequence. starting with 2004.03.01,
// * the later problem has been corrected, and because of point mutation
// * analysis, it has become important to reexamine all sequences.
// */
//            fScore = m_pScore.score(a);
//            fHyper = m_pScore.m_fHyper;
///*
// * If the convolution score is greater than 2.0, record information in the ion-type histograms
// */
//            if(fScore > 2.0)	{
//                m_tPeptideScoredCount++;
//                lIonCount = m_pScore.m_plCount[mscore::S_B] + m_pScore.m_plCount[mscore::S_Y];
//                lIonCount += m_pScore.m_plCount[mscore::S_C] + m_pScore.m_plCount[mscore::S_Z];
//                lIonCount += m_pScore.m_plCount[mscore::S_A] + m_pScore.m_plCount[mscore::S_X];
//                m_vSpectra[a].m_hHyper.add(m_pScore.hconvert(fHyper));
//                m_vSpectra[a].m_hConvolute.add(m_pScore.hconvert(fScore));
//                m_vSpectra[a].m_chBCount.add(m_pScore.m_plCount[mscore::S_A] + 
//                                    m_pScore.m_plCount[mscore::S_B] + 
//                                    m_pScore.m_plCount[mscore::S_C]);
//                m_vSpectra[a].m_chYCount.add(m_pScore.m_plCount[mscore::S_X] + 
//                                    m_pScore.m_plCount[mscore::S_Y] + 
//                                    m_pScore.m_plCount[mscore::S_Z]);
//
//            }
///*
//* this check is must be outside the above conditional to keep tandem
//* consistent whether running on single-threaded, multi-threaded or on
//* a cluster.  otherwise, when there are multiple spectra matching a
//* sequence (which is more common the fewer mprocess objects there are)
//* one can cause others to score differently from how they would score
//* alone.
//*/
//            if (m_vSpectra[a].m_hHyper.m_ulCount < 400)     {
//                if(m_bCrcCheck  &  _p)   {
//                    m_bPermute = true;
//                }
//                else if(m_vSpectra[a].m_dMH > 3000.0)     {
//                    m_bPermuteHigh = true;
//                }
//            }
//            bIonCheck = false;
//            if(lIonCount > m_lIonCount)	{
////			if( (m_pScore.m_plCount[mscore::S_A] || m_pScore.m_plCount[mscore::S_B] || m_pScore.m_plCount[mscore::S_C])    
////				(m_pScore.m_plCount[mscore::S_X] || m_pScore.m_plCount[mscore::S_Y] || m_pScore.m_plCount[mscore::S_Z]))	{
//                    bIonCheck = true;
////			}
//            }
//            bMassCheck = m_errValues.check(m_vSpectra[a].m_dMH,m_pScore.seq_mh());
//            if(!_p)	{
//                bMassCheck = false;
//            }
///*
// * if the same score has been recorded for another peptide in the same  ProteinSequence object,
// * add a domain to that object, but do not update the entire object
// */
//            if(bMassCheck    bIonCheck    fHyper == m_vSpectra[a].m_fHyper    m_vSpectra[a].m_tCurrentSequence == _s.m_tUid)	{
//                List<maa> vAa;
//                mdomain domValue;
//                domValue.m_vAa.clear();
//                double dDelta = 0.0;
//                if(m_pScore.get_aa(vAa,_v,dDelta))	{
//                    b = 0;
//                    while(b < vAa.size())	{
//                        domValue.m_vAa.push_back(vAa[b]);
//                        b++;
//                    }
//                }
//                if(fabs(dDelta) > 2.5)	{
//                    domValue.m_fScore = fScore;
//                    domValue.m_fHyper = fHyper;
//                    domValue.m_dMH = m_pScore.seq_mh();
//                    // m_fDelta was changed to m_dDelta in 2006.02.01
//                    domValue.m_dDelta = m_vSpectra[a].m_dMH - m_pScore.seq_mh();
//                    domValue.m_lE = _w;
//                    domValue.m_lS = _v;
//                    domValue.m_lMissedCleaves = _m;
//                    domValue.m_bUn = m_bUn;
//                    int lType = 1;
//                    int sType = 1;
//                    while(lType < m_pScore.m_lType+1)	{
//                        domValue.m_mapScore[lType] = m_pScore.m_pfScore[sType];
//                        domValue.m_mapCount[lType] = m_pScore.m_plCount[sType];
//                        lType *= 2;
//                        sType++;
//                    }
//                    b = 0;
//                    bOk = true;
//                    while(bOk    b < m_vSpectra[a].m_vseqBest.back().m_vDomains.size())	{
//                        if(domValue == m_vSpectra[a].m_vseqBest.back().m_vDomains[b]){	
//                            bOk = false;
//                        }
//                        b++;
//                    }
//                    if(bOk)	{
//                        m_vSpectra[a].m_vseqBest.back().m_vDomains.push_back(domValue);
//                    }
//                }
//            }
///*
// * if the same hyper score has been recorded for a different  ProteinSequence object, retain that
// * object and add the new  ProteinSequence to the back of the m_vseqBest List
// */
//            else if(bMassCheck  &&  bIonCheck  &&  fHyper == m_vSpectra[a].m_fHyper)	{
//                List<maa> vAa;
//                 ProteinSequence seqValue;
//                mdomain domValue;
//                domValue.m_vAa.clear();
//                double dDelta = 0.0;
//                if(m_pScore.get_aa(vAa,_v,dDelta))	{
//                    b = 0;
//                    while(b < vAa.size())	{
//                        domValue.m_vAa.push_back(vAa[b]);
//                        b++;
//                    }
//                }
//                if(fabs(dDelta) > 2.5)	{
//                    domValue.m_fScore = fScore;
//                    domValue.m_fHyper = fHyper;
//                    domValue.m_dMH = m_pScore.seq_mh();
//                    // m_fDelta was changed to m_dDelta in 2006.02.01
//                    domValue.m_dDelta = m_vSpectra[a].m_dMH - m_pScore.seq_mh();
//                    domValue.m_lE = _w;
//                    domValue.m_lS = _v;
//                    domValue.m_lMissedCleaves = _m;
//                    domValue.m_bUn = m_bUn;
//                    int lType = 1;
//                    int sType = 1;
//                    while(lType < m_pScore.m_lType+1)	{
//                        domValue.m_mapScore[lType] = m_pScore.m_pfScore[sType];
//                        domValue.m_mapCount[lType] = m_pScore.m_plCount[sType];
//                        lType *= 2;
//                        sType++;
//                    }
//                    seqValue = _s;
//                    seqValue.m_strSeq = " ";
//                    m_mapSequences.insert(SEQMAP::value_type(_s.m_tUid,_s.m_strSeq));
//                    seqValue.m_vDomains.clear();
//                    seqValue.m_vDomains.push_back(domValue);
//                    seqValue.format_description();
//                    bOk = true;
//                    b = 0;
//                    while(bOk    b < m_vSpectra[a].m_vseqBest.size())	{
//                        if(m_vSpectra[a].m_vseqBest[b].m_tUid == _s.m_tUid)	{
//                            c = 0;
//                            bDom = true;
//                            while(bDom    c < m_vSpectra[a].m_vseqBest[b].m_vDomains.size())	{
//                                if(m_vSpectra[a].m_vseqBest[b].m_vDomains[c] == domValue)	{
//                                    bDom = false;
//                                    bOk = false;
//                                }
//                                c++;
//                            }
//                            if(bDom)	{
//                                m_vSpectra[a].m_vseqBest[b].m_vDomains.push_back(domValue);
//                                bOk = false;
//                            }
//                        }
//                        b++;
//                    }
//                    if(bOk)	{
//                        m_vSpectra[a].m_tCurrentSequence = _s.m_tUid;
//                        seqValue.m_iRound = m_iCurrentRound;
//                        m_vSpectra[a].m_vseqBest.push_back(seqValue);
//                    }
//                }
//            }
///*
// * if the hyper score is the best found so far for the Spectrum, delete the old  ProteinSequence
// * objects and record this one.
// */
//            else if(bMassCheck    bIonCheck    fHyper > m_vSpectra[a].m_fHyper    lIonCount > m_lIonCount)	{
//                List<maa> vAa;
//                 ProteinSequence seqValue;
//                mdomain domValue;
//                domValue.m_vAa.clear();
//                double dDelta = 0.0;
//                if(m_pScore.get_aa(vAa,_v,dDelta))	{
//                    b = 0;
//                    while(b < vAa.size())	{
//                        domValue.m_vAa.push_back(vAa[b]);
//                        b++;
//                    }
//                }
//                if(Math.abs(dDelta) > 2.5)	{
//                    m_vSpectra[a].m_fScoreNext = m_vSpectra[a].m_fScore;
//                    m_vSpectra[a].m_fHyperNext = m_vSpectra[a].m_fHyper;
//                    m_vSpectra[a].m_fScore = fScore;
//                    m_vSpectra[a].m_fHyper = fHyper;
//                    domValue.m_fScore = fScore;
//                    domValue.m_fHyper = fHyper;
//                    domValue.m_lE = _w;
//                    domValue.m_lS = _v;
//                    domValue.m_lMissedCleaves = _m;
//                    domValue.m_dMH = m_pScore.seq_mh();
//                    // m_fDelta was changed to m_dDelta in 2006.02.01
//                    domValue.m_dDelta = m_vSpectra[a].m_dMH - m_pScore.seq_mh();
//                    domValue.m_bUn = m_bUn;
//                    int lType = 1;
//                    int sType = 1;
//                    while(lType < m_pScore.m_lType+1)	{
//                        domValue.m_mapScore[lType] = m_pScore.m_pfScore[sType];
//                        domValue.m_mapCount[lType] = m_pScore.m_plCount[sType];
//                        lType *= 2;
//                        sType++;
//                    }
//                    seqValue = _s;
//                    seqValue.m_strSeq = " ";
//                    m_mapSequences.insert(SEQMAP::value_type(_s.m_tUid,_s.m_strSeq));
//                    seqValue.m_vDomains.clear();
//                    seqValue.m_vDomains.push_back(domValue);
//                    seqValue.format_description();
//                    m_vSpectra[a].m_tCurrentSequence = _s.m_tUid;
//                    m_vSpectra[a].m_vseqBest.clear();
//                    seqValue.m_iRound = m_iCurrentRound;
//                    m_vSpectra[a].m_vseqBest.push_back(seqValue);
//                }
//            }
//            else if (fScore > 2.0    fHyper > m_vSpectra[a].m_fHyperNext)
//            {
//                m_vSpectra[a].m_fScoreNext = fScore;
//                m_vSpectra[a].m_fHyperNext = fHyper;
//            }
//        }
//        return true;
    }
//
//	This method is used to perform an inner product between two mass spectra. The fragment
//	ion mass accuracy is used to set the binning for the two vectors
//
    private double dot(final  int _f,final  int _s,final  float _r,final  boolean _t)
    { 
        throw new UnsupportedOperationException("Fix This"); // ToDo
//        float fValue = 0.0;
//        List<mi>::iterator itA = m_vSpectra[_f].m_vMI.begin();
//        List<mi>::iterator itB = m_vSpectra[_s].m_vMI.begin();
//        List<mi>::final _iterator itAEnd = m_vSpectra[_f].m_vMI.end(); 
//        List<mi>::final _iterator itBEnd = m_vSpectra[_s].m_vMI.end();
//        // if _t == true, then the mass error, _r, is given in Daltons
//        if(_t)	{
//            while(itA != itAEnd)	{
//                while(itB != itBEnd)	{
//                    if(Math.abs(itB.m_fM - itA.m_fM) <= _r)	{
//                        fValue += itB.m_fI*itA.m_fI;
//                    }
//                    if(itB.m_fM > itA.m_fM)	{
//                        break;
//                    }
//                    itB++;
//                }
//                itA++;
//            }
//        }
//        // deal with the case where _r is in ppm
//        else	{
//            final  float fRes = (float)(_r/1.0e6);
//            float fW = fRes;
//            while(itA != itAEnd)	{
//                fW = itA.m_fM * fRes;
//                while(itB != itBEnd)	{
//                    if(Math.abs(itB.m_fM - itA.m_fM) <= fW)	{
//                        fValue += itB.m_fI*itA.m_fI;
//                    }
//                    if(itB.m_fM > itA.m_fM)	{
//                        break;
//                    }
//                    itB++;
//                }
//                itA++;
//            }
//        }
//        return (double)fValue;	
    }
/*
 * expect_protein is used to assign the expectation value for a protein, if more
 * than one peptide has been found for that protein. the expectation values for
 * the peptides are combined with a simple Bayesian model for the probability of
 * having two peptides from the same protein having the best score in different
 * spectra.
 */
    double expect_protein(final  int _c,final  int _t,
                                    final  int _n,final  double _d)
    {
        double dValue = _d+ Math.log10((double)m_tProteinCount);
        if(_c == 1 && _d < 0.0)	{
            return _d;
        }
        else if(_c == 1)	{
            return 1.0;
        }
        if(_c == 0)	{
            return 1.0;
        }
        double dN = _n;
        double dK = _c;
        double dV = _t;
        int a = 0;
        while(a < _c)	{	
            dValue +=  Math.log10((dV - a)/(dK - a));
            a++;
        }
        dValue -= Math.log10(dV);
        dValue -= (dK-1.0)* Math.log10(dN);
        double dP = dN/(double)m_tPeptideCount;
        if(dP >= 1.0)
            dP = 0.9999999;
        double dLog = dK* Math.log10(dP)+(dV-dK)* Math.log10(1.0-dP);
        dValue += dLog;
        return dValue;
    }

/*
 * get_peptide_count returns the total number of peptides that have been scored
 */
    public int get_peptide_count()
    {
        return m_tPeptideCount;
    }

/*
 * get_protein_count returns the total number of proteins that have been scored
 */
    public int get_protein_count()
    {
        return m_tProteinCount;
    }

/*
 * get_reversed returns the number of significant reverse peptide sequences
 */
    public long get_reversed()
    {
        return m_lReversed;
    }

/*
 * get_thread returns the thread number for the object
 */
    public int get_thread()
    {
        return m_lThread;
    }
/*
 * get_threads returns the total number of threads currently in use
 */
    public int get_threads()
    {
        return m_lThreads;
    }

/*
 * get_threshold returns the current value of the expectation value threshold
 */
    double get_threshold()
    {
        return m_dThreshold;
    }
/*
 * get_total_residues returns the total number of residues that have been processed
 */
    public int get_total_residues()
    {
        return m_tTotalResidues;
    }

/*
 * get_valid returns the number of unique models
 */
    public int get_unique()
    {
        return m_tUnique;
    }

/*
 * get_valid returns the number of valid models
 */
    public int get_valid()
    {
        return m_tValid;
    }
/*
 * load takes a path name to the input XML parameter file and uses that file name
 * to initialize an XmlParameters object
 */
    public boolean load(final  String _f,XTandemProcess P_p)
    {
/*
 * check the String
 */
        if(_f == null)
            return false;
        String strFile = _f;
        throw new UnsupportedOperationException("Fix This"); // ToDo
///*
// * load the m_xmlValues object
// */
//        boolean bReturn = m_xmlValues.load(strFile);
//        if(!bReturn)	{
//            cout << "The input parameter file \"" << strFile.c_str() << "\" could not be located.\nCheck the file path name and try again.\n";
//            return false;
//        }
///*
// * check for the specification of a default parameter list
// */
//        String strValue;
//        String strKey = "list path, default parameters";
//        if(m_xmlValues.get(strKey,strValue))	{
///*
// * if there is a default parameter list, load it and then reload the input list
// * the input list will over ride all settings in the default list
// */
//            m_xmlValues.load(strValue);
//            m_xmlValues.load(strFile);
//            strKey = "list path, default parameters";
//            m_xmlValues.get(strKey,strValue);
//        }
///*
// * if a parameter list was found, load the  ProteinSequenceServer object with the taxonomy information
// */
//        if(bReturn)	{
//            bReturn = taxonomy();
//        }
///*
// * if the  ProteinSequenceServer object was loaded, create the scoring object
// */
//        if (bReturn) {
//            m_pScore = mscoremanager::create_mscore(m_xmlValues);
//            if (m_pScore != null) {
//                bReturn = m_pScore.load_param(m_xmlValues);
//            }
//            else {
//                bReturn = false;
//            }
//        }
///*
// * if the scoring object was loaded, load parameters
// */
//        if (bReturn) {
//            bReturn = (m_specCondition.load(m_xmlValues));
//        }
///*
// * if  the  ProteinSequenceServer object was loaded, obtain the tandem MS spectra to analyze
// */
//        if(bReturn)	{
//            bReturn = spectra();
//            strKey = "spectrum, check all charges";
//            m_xmlValues.get(strKey,strValue);
//            if(bReturn    strValue == "yes")	{
//                charge();
//                cout << "#";
//            }
//        }
//        if(bReturn)	{
//            bReturn = load_saps(_p);
//        }
//        if(bReturn)	{
//            bReturn = load_annotation(_p);
//        }
///*
// * load the  ProteinSequenceutilities object in the m_pScore member class with the amino acid
// * modification information
// */
//        if(bReturn)	{
//            bReturn = modify();
//        }
//        return bReturn;
    }
/*
 * charge adds additional charge states to the List of spectra, generating
 * +1, +2 and +3 charge states
 */
    public boolean charge()
    {
        throw new UnsupportedOperationException("Fix This"); // ToDo
//        int a = 0;
//        int tLength = m_vSpectra.size();
//        while(a < tLength)	{
//            if(m_vSpectra[a].m_tId > 100000000)	{
//                return true;
//            }
//            a++;
//        }
//        a = 0;
//        int tTest = 0;
//        int iZ = 1;
//        double dProton = 1.007276;
//        double dMH = 0.0;
//        while(a < tLength)	{
//            tTest = m_vSpectra[a].m_tId + 100000000;
//            iZ = (int)(m_vSpectra[a].m_fZ+0.5);
//            if(iZ == 2)	{
//                m_vSpectra.push_back(m_vSpectra[a]);
//                m_vSpectra.back().m_fZ = 3.0;
//                dMH = dProton + ((m_vSpectra[a].m_dMH - dProton)/m_vSpectra[a].m_fZ);
//                m_vSpectra.back().m_dMH = dProton + ((dMH - dProton)*m_vSpectra.back().m_fZ);
//                m_vSpectra.back().m_tId = tTest;
//                m_vSpectra.push_back(m_vSpectra[a]);
//                m_vSpectra.back().m_fZ = 1.0;
//                dMH = dProton + ((m_vSpectra[a].m_dMH - dProton)/m_vSpectra[a].m_fZ);
//                m_vSpectra.back().m_dMH = dProton + ((dMH - dProton)*m_vSpectra.back().m_fZ);
//                m_vSpectra.back().m_tId = tTest + 100000000;
//            }
//            else if(iZ == 3)	{
//                m_vSpectra.push_back(m_vSpectra[a]);
//                m_vSpectra.back().m_fZ = 2.0;
//                dMH = dProton + ((m_vSpectra[a].m_dMH - dProton)/m_vSpectra[a].m_fZ);
//                m_vSpectra.back().m_dMH = dProton + ((dMH - dProton)*m_vSpectra.back().m_fZ);
//                m_vSpectra.back().m_tId = tTest;
//                m_vSpectra.push_back(m_vSpectra[a]);
//                m_vSpectra.back().m_fZ = 1.0;
//                dMH = dProton + ((m_vSpectra[a].m_dMH - dProton)/m_vSpectra[a].m_fZ);
//                m_vSpectra.back().m_dMH = dProton + ((dMH - dProton)*m_vSpectra.back().m_fZ);
//                m_vSpectra.back().m_tId = tTest + 100000000;
//            }
//            else if(iZ == 1)	{
//                m_vSpectra.push_back(m_vSpectra[a]);
//                m_vSpectra.back().m_fZ = 2.0;
//                dMH = dProton + ((m_vSpectra[a].m_dMH - dProton)/m_vSpectra[a].m_fZ);
//                m_vSpectra.back().m_dMH = dProton + ((dMH - dProton)*m_vSpectra.back().m_fZ);
//                m_vSpectra.back().m_tId = tTest;
//                m_vSpectra.push_back(m_vSpectra[a]);
//                m_vSpectra.back().m_fZ = 3.0;
//                dMH = dProton + ((m_vSpectra[a].m_dMH - dProton)/m_vSpectra[a].m_fZ);
//                m_vSpectra.back().m_dMH = dProton + ((dMH - dProton)*m_vSpectra.back().m_fZ);
//                m_vSpectra.back().m_tId = tTest + 100000000;
//            }
//            a++;
//        }
//        return true;
    }

/*
 *  load_best_vector loads the m_vseqBest List with a list of sequences
 * that correspond to assigned, valid peptide models. spectra that have
 * produced valid models are marked as inactive, so that they are
 * not reassigned the same peptides again.
 */
   public  boolean load_best_vector()
    {
        String strKey = "refine, maximum valid expectation value";
        String strValue;
        strValue = m_xmlValues.get(strKey);
        throw new UnsupportedOperationException("Fix This"); // ToDo
//        double dMaxExpect = 0.01;
//        if(strValue.size() > 0)	{
//            dMaxExpect = atof(strValue.c_str());
//        }
//        int a = 0;
//        while(a < m_vSpectra.size())	{
//            m_vSpectra[a].m_hHyper.model();
//            m_vSpectra[a].m_hHyper.set_protein_factor(1.0);
//            a++;
//        }
//        a = 0;
//        double dExpect = 1.0;
//        while(a < m_vSpectra.size())	{
//            dExpect = (double)m_vSpectra[a].m_hHyper.expect_protein(m_pScore.hconvert(m_vSpectra[a].m_fHyper));
//            if(dExpect <= dMaxExpect)	{
//                m_vSpectra[a].m_bActive = false;
//            }
//            a++;
//        }
//        return !m_vseqBest.empty();
    }
/*
 * mark_repeats is used to determine which models are simply repeats of each other. finding
 * the same model more than once does not help with confirming the model: a stochastic match
 * to the same pattern twice is actually quite likely if the pattern is repeated in
 * subsequent spectra.
 */
    public boolean mark_repeats()
    {
        int a = 0;
        int b = 0;
        String strValue;
        String strCurrent;
        float fMH = 0.0F;
        int tStart = 0;
        int tEnd = 0;
        int tUid = 0;
        int tUidB = 0;
        int tStartB = 0;
        int tEndB = 0;
        int tLength = m_vSpectra.size();
        double dBestExpect = 0.0;
        throw new UnsupportedOperationException("Fix This"); // ToDo
//        SEQMAP::iterator itValue;
//        int tTics = 0;
//        int tTicLength = (int)((double) tLength/5.0);
//        while(a < tLength)	{
//            dBestExpect = 1.0e32;
//            tTics++;
//            if(tTics >= tTicLength)	{
//                cout << ".";
//                cout.flush();
//                tTics = 0;
//            }
//            if(!m_vSpectra[a].m_bRepeat    !m_vSpectra[a].m_vseqBest.empty())	{
//                tStart = m_vSpectra[a].m_vseqBest[0].m_vDomains[0].m_lS;
//                tEnd = m_vSpectra[a].m_vseqBest[0].m_vDomains[0].m_lE;
//                tUid = m_vSpectra[a].m_vseqBest[0].m_tUid;
//                dBestExpect = m_vSpectra[a].m_dExpect;
//            }
//            if(!m_vSpectra[a].m_bRepeat    !m_vSpectra[a].m_vseqBest.empty())	{
//                b = a + 1;
//                while(b < tLength)	{
//                    if(!m_vSpectra[b].m_bRepeat    !m_vSpectra[b].m_vseqBest.empty())	{
//                        tStartB = m_vSpectra[b].m_vseqBest[0].m_vDomains[0].m_lS;
//                        tEndB = m_vSpectra[b].m_vseqBest[0].m_vDomains[0].m_lE;
//                        tUidB = m_vSpectra[b].m_vseqBest[0].m_tUid;
//                        if(tEndB == tEnd    tStartB == tStart    tUidB == tUid)	{
//                            if(dBestExpect <= m_vSpectra[b].m_dExpect)	{
//                                m_vSpectra[b].m_bRepeat = true;
//                            }
//                            else	{
//                                dBestExpect = m_vSpectra[b].m_dExpect;
//                            }
//                        }
//                    }
//                    b++;
//                }
//                if(dBestExpect < m_vSpectra[a].m_dExpect)	{
//                    m_vSpectra[a].m_bRepeat = true;
//                }
//            }
//            a++;
//        }
//        return true;
    }
/*
 * merge_map adds new values to the existing m_mapSequences  Map.
*/
    public boolean merge_map(Map<String,String>  _s)
    {
        m_mapSequences.putAll(_s);
//        Map<String,String>::iterator itValue = _s.begin();
//        Map<String,String>::iterator itEnd = _s.end();
//        while(itValue != itEnd)	{
//            if(m_mapSequences.find(itValue.first) == m_mapSequences.end())	{
//                m_mapSequences.insert(*itValue);
//            }
//            itValue++;
//        }
        return true;
    }

/*
 * merge_spectra takes an Spectrum List from an external source and merges it
 * with the m_vSpectra List. this method is used to combine information obtained
 * from other threads with this mprocess object
 */
    boolean merge_spectra()
    {
        String strKey = "refine, maximum valid expectation value";
        String strValue = m_xmlValues.get(strKey );
        double dMaxExpect = 0.01;
        throw new UnsupportedOperationException("Fix This"); // ToDo
//        if(strValue.length() > 0)	{
//            dMaxExpect = atof(strValue.c_str());
//        }
//        int a = 0;
//        int b = 0;
//        int c = 0;
//        {
//            while(a < m_vSpectra.size())	{
//                m_vSpectra[a].m_hHyper.model();
//                m_vSpectra[a].m_hHyper.set_protein_factor(1.0);
////			if(m_bUseHomologManagement    m_vSpectra[a].m_vseqBest.size() > 5)	{
////				m_vSpectra[a].m_vseqBest.erase(m_vSpectra[a].m_vseqBest.begin()+5,m_vSpectra[a].m_vseqBest.end());
////			}
//                a++;
//            }
//            a = 0;
//            double dExpect = 1.0;
//            Map<String,String>::iterator itValue;
//            while(a < m_vSpectra.size())	{
//                b = 0;
//                dExpect = (double)m_vSpectra[a].m_hHyper.expect_protein(m_pScore.hconvert(m_vSpectra[a].m_fHyper));
//                if(dExpect <= dMaxExpect)	{
//                    m_vSpectra[a].m_bActive = false;
//                    while(b < m_vSpectra[a].m_vseqBest.size())	{
//                        c = 0;
//                        while(c < m_vseqBest.size())	{
//                            if(m_vSpectra[a].m_vseqBest[b].m_tUid == m_vseqBest[c].m_tUid)	{
//                                break;
//                            }
//                            c++;
//                        }
//                        if(c == m_vseqBest.size())	{
//                            m_vseqBest.push_back(m_vSpectra[a].m_vseqBest[b]);
//                            itValue = m_mapSequences.find(m_vseqBest[c].m_tUid);
//                            m_vseqBest[c].m_strSeq = ((*itValue).second.c_str());
//                            m_vseqBest[c].m_vDomains.clear();
//                        }
//                        b++;
//                        if(m_bUseHomologManagement   && ( b  >= 5))	{
//                            break;
//                        }
//                    }
//                }
//                a++;
//            }
//        }
//        return true;
    }

    boolean merge_spectra(List<Spectrum>  _s)
    {
        String strKey = "refine, maximum valid expectation value";
        String strValue;
        strValue = m_xmlValues.get(strKey );
        double dMaxExpect = 0.01;
        throw new UnsupportedOperationException("Fix This"); // ToDo
//        if(strValue.length() > 0)	{
//            dMaxExpect = atof(strValue.c_str());
//        }
//        int a = 0;
//        int b = 0;
//        int c = 0;
//        {
//            while(a < _s.size())	{
//                _s[a].m_hHyper.model();
//                _s[a].m_hHyper.set_protein_factor(1.0);
//                if(m_bUseHomologManagement    _s[a].m_vseqBest.size() > 5)	{
//                    _s[a].m_vseqBest.erase(_s[a].m_vseqBest.begin()+5,_s[a].m_vseqBest.end());
//                }
//                a++;
//            }
//            a = 0;
//            double dExpect = 1.0;
//            Map<String,String>::iterator itValue;
//            while(a < _s.size())	{
//                b = 0;
//                dExpect = (double)_s[a].m_hHyper.expect_protein(m_pScore.hconvert(_s[a].m_fHyper));
//                if(dExpect <= dMaxExpect)	{
//                    while(b < _s[a].m_vseqBest.size())	{
//                        c = 0;
//                        while(c < m_vseqBest.size())	{
//                            if(_s[a].m_vseqBest[b].m_tUid == m_vseqBest[c].m_tUid)	{
//                                break;
//                            }
//                            c++;
//                        }
//                        if(c == m_vseqBest.size())	{
//                            m_vseqBest.push_back(_s[a].m_vseqBest[b]);
//                            itValue = m_mapSequences.find(m_vseqBest[c].m_tUid);
//                            m_vseqBest[c].m_strSeq = ((*itValue).second.c_str());
//                            m_vseqBest[c].m_vDomains.clear();
//                        }
//                        b++;
//                    }
//                }
//                a++;
//            }
//        }
//        return true;
    }
/*
 * merge_statistics updates the processing statistics with external statistics.
 * this method is used to combine information obtained from other threads with 
 * this mprocess object
 */
    boolean merge_statistics(final  XTandemProcess _p)
    { 
        throw new UnsupportedOperationException("Fix This"); // ToDo
//        m_tPeptideCount += _p.m_tPeptideCount;
//        m_tRefineInput += _p.m_tRefineInput;
//        m_tRefinePartial += _p.m_tRefinePartial;
//        m_tRefineUnanticipated += _p.m_tRefineUnanticipated;
//        m_tRefineNterminal += _p.m_tRefineNterminal;
//        m_tRefineCterminal += _p.m_tRefineCterminal;
//        m_tRefinePam += _p.m_tRefinePam;
//        m_dRefineTime = max(m_dRefineTime, _p.m_dRefineTime); // bpratt 9/20/2010
//        m_dSearchTime = max(m_dSearchTime, _p.m_dSearchTime); // bpratt 9/20/2010
//        return true;
    }
/*
 * modify checks the input parameters for known parameters that are use to modify 
 * a protein sequence. these parameters are stored in the m_pScore member object's
 *  ProteinSequenceutilities member object
 */
    boolean modify()
    {
        String strKey = "residue, modification mass";
        String strValue;
        throw new UnsupportedOperationException("Fix This"); // ToDo
//        m_vstrModifications.clear();
//        if(m_xmlValues.get(strKey,strValue)    strValue.size() > 0) {
//            m_vstrModifications.push_back(strValue);
//        }
//        else	{
//            strValue = "";
//            m_vstrModifications.push_back(strValue);
//        };
//        int a = 1;
//        char *pLine = new char[256];
//        sprintf(pLine,"residue, modification mass %i",a);
//        strKey = pLine;
//        while(m_xmlValues.get(strKey,strValue)    strValue.size() > 0) {
//            m_vstrModifications.push_back(strValue);
//            a++;
//            sprintf(pLine,"residue, modification mass %i",a);
//            strKey = pLine;
//        }
//        delete[] pLine;
//        strKey = "residue, potential modification mass";
//        if(m_xmlValues.get(strKey,strValue)) {
//            m_pScore.m_seqUtil.modify_maybe(strValue);
//            m_pScore.m_seqUtilAvg.modify_maybe(strValue);
//        }
//        strKey = "residue, potential modification motif";
//        if(m_xmlValues.get(strKey,strValue)) {
//            m_pScore.m_seqUtil.modify_motif(strValue);
//            m_pScore.m_seqUtilAvg.modify_motif(strValue);
//        }
//        strKey = "protein, N-terminal residue modification mass";
//        if(m_xmlValues.get(strKey,strValue)) {
//            m_pScore.m_seqUtil.modify_n((float)atof(strValue.c_str()));
//            m_pScore.m_seqUtilAvg.modify_n((float)atof(strValue.c_str()));
//        }
//        strKey = "protein, C-terminal residue modification mass";
//        if(m_xmlValues.get(strKey,strValue)) {
//            m_pScore.m_seqUtil.modify_c((float)atof(strValue.c_str()));
//            m_pScore.m_seqUtilAvg.modify_c((float)atof(strValue.c_str()));
//        }
//        strKey = "protein, cleavage N-terminal mass change";
//        if(m_xmlValues.get(strKey,strValue)) {
//            m_pScore.m_seqUtil.m_dCleaveN = atof(strValue.c_str());
//            m_pScore.m_seqUtilAvg.m_dCleaveN = atof(strValue.c_str());
//        }
//        strKey = "protein, cleavage C-terminal mass change";
//        if(m_xmlValues.get(strKey,strValue)) {
//            m_pScore.m_seqUtil.m_dCleaveC = atof(strValue.c_str());
//            m_pScore.m_seqUtilAvg.m_dCleaveC = atof(strValue.c_str());
//        }
//        return true;
    }
/*
 * process carries out the protein identification
 */
    boolean process()
    {
        if(m_vSpectra.size() < 1)
            return false;
        String strKey;
        String strValue;
        throw new UnsupportedOperationException("Fix This"); // ToDo
////	m_pScore.set_mini(true);
//    #ifdef PLUGGABLE_SCORING
//        strKey = "scoring, pluggable scoring";
//        strValue = "yes";
//        m_xmlValues.set(strKey,strValue);
//    #else
//        strKey = "scoring, pluggable scoring";
//        strValue = "no";
//        m_xmlValues.set(strKey,strValue);
//    #endif
//
//        strKey = "output, path";
//        m_xmlValues.get(strKey,strValue);
//        strKey = "output path: ";
//        strKey += strValue;
//        m_prcLog.log(strKey);
//        strKey = "spectrum, path";
//        m_xmlValues.get(strKey,strValue);
//        strKey = "input path: ";
//        strKey += strValue;
//        m_prcLog.log(strKey);
//
//    #ifndef X_P3
//        strKey = "protein, saps";
//        m_bSaps = false;
//        m_xmlValues.get(strKey,strValue);
//        if(strValue == "yes")	{
//            m_bSaps = true;
//        }
//    #endif
//
//        strKey = "protein, homolog management";
//        m_xmlValues.get(strKey,strValue);
//        m_bUseHomologManagement = false;
//        if(strValue == "yes")	{
//            m_bUseHomologManagement = true;
//        }
//        strKey = "scoring, cyclic permutation";
//        m_xmlValues.get(strKey,strValue);
//        m_bCrcCheck = false;
//        if(strValue == "yes")	{
//            m_bCrcCheck = true;
//        }
///*
// * Detect the presence of ion type parameters: default is b + y ions
// */
//        int lType = 0;
//        strKey = "scoring, include reverse";
//        m_xmlValues.get(strKey,strValue);
//        m_lReversed = -1;
//        m_bReversedOnly = false;
//        if(strValue == "yes")	{
//            m_lReversed = 0;
//        }
//        else if(strValue == "only")		{
//            m_bReversedOnly = true;
//            m_lReversed = 0;
//        }
//        strKey = "scoring, a ions";
//        m_xmlValues.get(strKey,strValue);
//        if(strValue == "yes")	{
//            lType |= mscore::T_A;
//        }
//        strKey = "scoring, a ions";
//        m_xmlValues.get(strKey,strValue);
//        if(strValue == "yes")	{
//            lType |= mscore::T_A;
//        }
//        strKey = "scoring, b ions";
//        m_xmlValues.get(strKey,strValue);
//        if(strValue == "yes")	{
//            lType |= mscore::T_B;
//        }
//        strKey = "scoring, c ions";
//        m_xmlValues.get(strKey,strValue);
//        if(strValue == "yes")	{
//            lType |= mscore::T_C;
//        }
//        strKey = "scoring, x ions";
//        m_xmlValues.get(strKey,strValue);
//        if(strValue == "yes")	{
//            lType |= mscore::T_X;
//        }
//        strKey = "scoring, z ions";
//        m_xmlValues.get(strKey,strValue);
//        if(strValue == "yes")	{
//            lType |= mscore::T_Z;
//        }
//        strKey = "scoring, y ions";
//        m_xmlValues.get(strKey,strValue);
//        if(strValue == "yes")	{
//            lType |= mscore::T_Y;
//        }
//        if(lType == 0)	{
//            lType = mscore::T_B | mscore::T_Y;
//        }
//        m_pScore.set_type(lType);
//        strKey = "refine, spectrum synthesis";
//        m_xmlValues.get(strKey,strValue);
//        if(strValue == "yes")	{
//            m_pScore.m_seqUtil.synthesis(true);
//        }
//        else	{
//            m_pScore.m_seqUtil.synthesis(false);
//        }
///*
// * Determine how to process the m_ParentStream and fragment error numbers:
// * 1. absolute errors in Daltons; or
// * 2. relative errors in ppm.
// */
//        strKey = "spectrum, m_ParentStream monoisotopic mass error units";
//        m_xmlValues.get(strKey,strValue);
//        lType = 0;
//        m_errValues.m_bPpm = false;
//        if(strValue == "Daltons")	{
//            lType |= mscore::T_PARENT_DALTONS;
//            m_errValues.m_bPpm = false;
//        }
//        else if(strValue == "ppm")	{
//            lType |= mscore::T_PARENT_PPM;
//            m_errValues.m_bPpm = true;
//        }
//        strKey = "spectrum, fragment mass error units";
//        m_xmlValues.get(strKey,strValue);
//        if (strValue.empty()) {
//            // try old parameter name
//            strKey = "spectrum, fragment monoisotopic mass error units";
//            m_xmlValues.get(strKey,strValue);
//        }
//        if(strValue == "Daltons")	{
//            lType |= mscore::T_FRAGMENT_DALTONS;
//        }
//        else if(strValue == "ppm")	{
//            lType |= mscore::T_FRAGMENT_PPM;
//        }
//        if(lType == 0)	{
//            lType = mscore::T_PARENT_DALTONS | mscore::T_FRAGMENT_DALTONS;
//        }
//        m_pScore.set_error(lType);
///*
// * check the m_xmlValues parameters for a set of known input parameters, and
// * substitute default values if they are not found 
// */
//        strKey = "spectrum, fragment mass error";
//        m_xmlValues.get(strKey,strValue);
//        if (strValue.empty()) {
//            // try old parameter name
//            strKey = "spectrum, fragment monoisotopic mass error";
//            m_xmlValues.get(strKey,strValue);
//        }
//        float fErrorValue = (float)atof(strValue.c_str());
//        if(fErrorValue <= 0.0)
//            fErrorValue = (float)0.45;
//        m_pScore.set_fragment_error(fErrorValue);
//        strKey = "spectrum, m_ParentStream monoisotopic mass error plus";
//        m_xmlValues.get(strKey,strValue);
//        fErrorValue = (float)atof(strValue.c_str());
//        fErrorValue = (float)Math.abs(atof(strValue.c_str()));
//        m_errValues.m_fPlus = fErrorValue;
//        if(m_errValues.m_bPpm)	{
//            if(fErrorValue < 95.0)	{
//                m_bCrcCheck = true;
//            }
//            if(fErrorValue < 10.0)	{
//                fErrorValue = 10.0;
//            }
//        }
//        else	{
//            if(fErrorValue < 0.095)	{
//                m_bCrcCheck = true;
//            }
//            if(fErrorValue < 0.01)	{
//                fErrorValue = (float)0.01;
//            }
//        }
//        m_pScore.set_parent_error(fErrorValue,true);
//        strKey = "spectrum, homology error";
//        m_xmlValues.get(strKey,strValue);
//        fErrorValue = (float)atof(strValue.c_str());
//        if(fErrorValue <= 0.0)
//            fErrorValue = (float)4.5;
//        m_pScore.set_homo_error(fErrorValue);
//        strKey = "spectrum, parent monoisotopic mass error minus";
//        m_xmlValues.get(strKey,strValue);
//        fErrorValue = (float)Math.abs(atof(strValue.c_str()));
//        m_errValues.m_fMinus = (float)(-1.0*fErrorValue);
//        if(m_errValues.m_bPpm)	{
//            if(fErrorValue < 95.0)	{
//                m_bCrcCheck = true;
//            }
//            if(fErrorValue < 10.0)	{
//                fErrorValue = 10.0;
//            }
//        }
//        else	{
//            if(fErrorValue < 0.095)	{
//                m_bCrcCheck = true;
//            }
//            if(fErrorValue < 0.01)	{
//                fErrorValue = (float)0.01;
//            }
//        }
//        m_pScore.set_parent_error(fErrorValue,false);
//        strKey = "spectrum, m_ParentStream monoisotopic mass isotope error";
//        m_xmlValues.get(strKey,strValue);
//        if(strValue == "yes")	{
//            m_errValues.m_bIsotope = true;
//            m_pScore.set_isotope_error(true);
//        }
//        else	{
//            m_pScore.set_isotope_error(false);
//            m_errValues.m_bIsotope = false;
//        }
//        strKey = "protein, cleavage N-terminal limit";
//        if(m_xmlValues.get(strKey,strValue)) {
//            if(atoi(strValue.c_str()) > 0)	{
//                m_lStartMax = atoi(strValue.c_str());
//            }
//        }
//        strKey = "protein, modified residue mass file";
//        if(m_xmlValues.get(strKey,strValue))	{
//            m_pScore.m_seqUtil.set_aa_file(strValue);
//            m_pScore.m_seqUtilAvg.set_aa_file(strValue);
//        }
//        strKey = "protein, cleavage site";
//        m_xmlValues.get(strKey,strValue);
//        m_Cleave.load(strValue);
//        strKey = "protein, cleavage semi";
//        m_xmlValues.get(strKey,strValue);
//        if(strValue == "yes")	{
//            m_semiState.activate(true);
//        }
//        strKey = "scoring, minimum ion count";
//        m_xmlValues.get(strKey,strValue);
//        m_lIonCount = (int)atoi(strValue.c_str()) - 1;
//        strKey = "scoring, maximum missed cleavage sites";
//        m_xmlValues.get(strKey,strValue);
//        m_tMissedCleaves = atoi(strValue.c_str());
//        strKey = "spectrum, sequence batch size";
//        m_xmlValues.get(strKey,strValue);
//        int tBatch = atoi(strValue.c_str());
//        if(tBatch < 1)	{
//            tBatch = 1000;
//        }
//        m_svrSequences.initialize(tBatch);
//        strKey = "protein, use annotations";
//        m_xmlValues.get(strKey,strValue);
//        m_bAnnotation = false;
//        m_strLastMods.clear();
//        if(strValue == "yes")	{
//            m_bAnnotation = true;
//        }
//        int tLength = 0;
//        int a = 0;
//        m_tProteinCount = 0;
//        m_tPeptideCount = 0;
//        m_tPeptideScoredCount = 0;
//        strKey = "output, http";
//        m_xmlValues.get(strKey,strValue);
//        a = 0;
//        long lTics = 0;
///*
// * record the spectrum m/z - intensity pairs into the mscore object. This
// * is done here to speed up processing many spectra, as they are only
// * loaded and processed once.
// */
//        m_tSpectra = m_vSpectra.size();
//        while(a < m_tSpectra)	{
//            m_pScore.add_details(m_vSpectra[a]);
//            a++;
//        }
//        m_pScore.sort_details();
//        a =0;
//        while(a < m_tSpectra)	{
//            m_pScore.add_mi(m_vSpectra[a]);
//            a++;
//        }
//        a = 0;
//        long b = 0;
//        boolean bForward = true;
//        char pLine[32];
//        pLine[0] = '\0';
//        char cBs = 0x08;
///*
// * esitimate the smallest number of residues that could possibily correspond to the
// * smallest m_ParentStream ion in the m_vSpectra List
// */
//        residues();
///*
// * record the start time for the identification process. 
// */
//        m_dSearchTime = clock();
//        int lServerCount = 0;
//        int lWaste = 0;
//        long lReadTime = 0;
//        long lTempTime = 0;
//        char *pOut = new char[256];
//        sprintf(pOut,"Spectrum-to-sequence matching process in progress");
//        strKey = "output, message";
//        m_xmlValues.get(strKey,strValue);
//        if(strValue.size() > 0)	{
//            if(strValue.size() > 255)	{
//                delete pOut;
//                pOut = new char[strValue.size()+1];
//            }
//            sprintf(pOut,"%s",strValue.c_str());
//        }
//        long lOut = 0;
//        long lOutLimit = (long)strlen(pOut);
///*
// * use the  ProteinSequenceServer to get a list of proteins to analyze 
// */
//        while(!m_svrSequences.done())	{
//            m_svrSequences.next(true);
//            lServerCount++;
//            score_each_sequence();
///*
// * this section produces the "still alive" messages that are displayed so
// * that a user doesn't get anxious. this implementaion only throws messages
// * from the 0th thread, and uses a character String
// * to generate characters that get flushed to the console.
// */
//            if(m_lThread == 0 || m_lThread == 0xFFFFFFFF)	{
//                lTics++;
//                if(lTics == 50)	{
//                    cout << " | " << (int)m_tProteinCount/1000 << " ks \n";
//                    cout.flush();
//                    m_prcLog.log(".");
//                    lTics = 0;
//                }
//                else	{
//                    if(lTics == 1)	{
//                        cout << "\t";
//                    }
//                    cout << pOut[lOut];
//                    cout.flush();
//                    m_prcLog.log(".");
//                    lOut++;
//                    if(lOut >= lOutLimit)	{
//                        lOut = 0;
//                    }
//                }
//            }
//            clean_sequences();
//        }
//        delete pOut;
///*
// * record the total protein modelling session time 
// */
//        m_dSearchTime = (clock() - m_dSearchTime)/(double)CLOCKS_PER_SEC;
//        m_svrSequences.clear();
///*
// * process the scoring histograms in each spectrum, so that expectation values
// * can be calculated. first a survival function is calculated, replacing the
// * original scoring histogram. then, the high scoring tail of the survival function
// * is modeled. 
// * NOTE: only the hyper score histogram is used in the modelling process.
// * NOTE: the protein factor is not used to weigth scores - it is present for future use
// */
//        return true;
    }

/*
 * refine controls the sequence refinement process.
 */
    boolean refine()
    {
        throw new UnsupportedOperationException("Fix This"); // ToDo
//        int tTime = clock();
//        m_pScore.set_mini(false);
//        // Check for output path for sequences to be loaded to a bioml file
//        // Writing only done on the base thread
//        String strKey = "output, sequence path";
//        String strValue;
//        m_xmlValues.get(strKey,strValue);
//        if(strValue.length() > 0 &&   (m_lThread == 0 || m_lThread == 0xFFFFFFFF))	{
//            mbiomlreport rptCurrent;
//            rptCurrent.setpath(strValue);
//            rptCurrent.write(m_vseqBest);
//        }
//        strKey = "refine";
//        m_xmlValues.get(strKey,strValue);
//        boolean bReturn = false;
//        m_lStartMax = 100000000;
//        if(strValue == "yes")	{
//            bReturn = refine_model();
//            tTime = clock() - tTime;
//            m_dRefineTime = (double)tTime/(double)(CLOCKS_PER_SEC);
//        }
//        return bReturn;
    }
/*
 * refine_models takes the model peptides that have been found and tries to
 * find other spectra that can be described by the proteins that these models
 * come from. several layers of modelling are available in the current implementation:
 * 1. comparison with multiple potential modificiation
 * 2. full [X]|[X] cleavage
 * 3. N-terminal modifications
 * 4. C-terminal modifications
 */
    boolean refine_model()
    {
        throw new UnsupportedOperationException("Fix This"); // ToDo
//        m_pRefine = mrefinemanager::create_mrefine(m_xmlValues);
//        if (m_pRefine == null) {
//            cout << "Failed to create MRefine\n";
//            return false;
//        }
//        m_pRefine.set_mprocess(this);
//        m_pRefine.refine();
//        return true;
    }
/*
 * report outputs the information obtained during the process method, using
 * an mreport object and the reporting parameter settings obtained from the
 * input parameter list. many applications may need to customize this method.
 * two customized output types are given: one which emphasizes protein sequences
 * and one which emphasizes spectra.
 */
    boolean report()
    {
        m_prcLog.log("creating report");
        throw new UnsupportedOperationException("Fix This"); // ToDo
//        List<Spectrum>::iterator itS = m_vSpectra.begin();
//        m_pScore.clear();
//        while(itS != m_vSpectra.end())	{
//            itS.m_hHyper.model();
//            itS.m_hHyper.set_protein_factor(1.0);
//            itS++;
//        }
//        int a = 0;
//         Map<Integer,int> mapSpec;
//        pair<Integer,int> prSpec;
//         Map<Integer,int>::iterator itMap;
//        while(a < m_vSpectra.size())	{
//            prSpec.first = m_vSpectra[a].m_tId;
//            prSpec.second = a;
//            mapSpec.insert(prSpec);
//            if(!m_vSpectra[a].m_vseqBest.empty()    !m_vSpectra[a].m_vseqBest[0].m_vDomains.empty())	{
//                m_vSpectra[a].m_dExpect = m_vSpectra[a].m_hHyper.expect(m_pScore.hconvert(m_vSpectra[a].m_vseqBest[0].m_vDomains[0].m_fHyper));
//            }
//            a++;
//        }
//        int tTest = 0;
//        itS = m_vSpectra.begin();
//        while(itS != m_vSpectra.end())	{
//            if(itS.m_tId < 100000000)	{
//                tTest = itS.m_tId + 100000000;
//                itMap = mapSpec.find(tTest);
//                if(itMap != mapSpec.end())	{
//                    a = itMap.second;
//                    if(itS.m_dExpect <= m_vSpectra[a].m_dExpect)	{
//                        m_vSpectra[a].m_dExpect = 1000;
//                        m_vSpectra[a].m_fScore = 0.0;
//                        mapSpec.erase(tTest);
//                        tTest = tTest + 100000000;
//                        itMap = mapSpec.find(tTest);
//                        if(itMap != mapSpec.end())	{
//                            a = itMap.second;
//                            if(itS.m_dExpect <= m_vSpectra[a].m_dExpect)	{
//                                m_vSpectra[a].m_dExpect = 1000;
//                                m_vSpectra[a].m_fScore = 0.0;
//                                mapSpec.erase(tTest);
//                            }
//                            else	{
//                                itS.m_dExpect = 1000;
//                                itS.m_fScore = 0.0;
//                                mapSpec.erase(tTest);
//                            }
//                        }
//                    }
//                    else	{
//                        itS.m_dExpect = 1000;
//                        itS.m_fScore = 0.0;
//                        mapSpec.erase(tTest);
//                        tTest = tTest + 100000000;
//                        itMap = mapSpec.find(tTest);
//                        if(itMap != mapSpec.end())	{
//                            int b = itMap.second;
//                            if(m_vSpectra[a].m_dExpect <= m_vSpectra[b].m_dExpect)	{
//                                m_vSpectra[b].m_dExpect = 1000;
//                                m_vSpectra[b].m_fScore = 0.0;
//                                mapSpec.erase(tTest);
//                            }
//                            else	{
//                                m_vSpectra[a].m_dExpect = 1000;
//                                m_vSpectra[a].m_fScore = 0.0;
//                                mapSpec.erase(tTest);
//                            }
//                        }
//                    }
//                }
//            }
//            itS++;
//        }
//        mapSpec.clear();
//        a = 0;
//        itS = m_vSpectra.begin();
//        m_viQuality.clear();
//        while(a < 255)	{
//            m_viQuality.push_back(0);
//            a++;
//        }
//        a = 0;
//        int iIndex;
//        int iQ = 0;
//        while(itS != m_vSpectra.end())	{
//            a = 0;
//            if(itS.m_fScore > 0.0    itS.m_dExpect < 1.0)	{
//                iIndex = int(-1.0*log(itS.m_dExpect));
//                if(iIndex >= 0    iIndex < 255)	{
//                    m_viQuality[iIndex]++;
//                    iQ++;
//                }
//            }
//            while(a < itS.m_vMINeutral.size())	{
//                itS.m_vMI.push_back(itS.m_vMINeutral[a]);
//                a++;
//            }
//            if(a > 0)	{
//                sort(itS.m_vMI.begin(),itS.m_vMI.end(),lessThanMass);
//            }
//            itS++;
//        }
//        char *pLine = new char[256];
//        String strKey = "quality values";
//        String strValue;
//        a = 0;
//        while(a < 20)	{
//            if(a != 0)	{
//                strValue += " ";
//            }
//            sprintf(pLine,"%i",m_viQuality[a]);
//            strValue += pLine;
//            a++;
//        }
//        m_xmlPerformance.put(strKey,strValue);
///*
// * store information in the m_smlPerformance object for reporting
// */
//        strKey = "timing, initial modelling total (sec)";
//        sprintf(pLine,"%.2lf",m_dSearchTime);
//        strValue = pLine;
//        m_xmlPerformance.put(strKey,strValue);
//        strKey = "timing, initial modelling/spectrum (sec)";
//        sprintf(pLine,"%.3lf",m_dSearchTime/(double)m_tSpectraTotal);
//        strValue = pLine;
//        m_xmlPerformance.put(strKey,strValue);
//        strKey = "timing, load sequence models (sec)";
//        sprintf(pLine,"%.2lf",m_svrSequences.get_time());
//        strValue = pLine;
//        m_xmlPerformance.put(strKey,strValue);
//        strKey = "modelling, total spectra used";
//        sprintf(pLine,"%lu",(int)m_tSpectraTotal);
//        strValue = pLine;
//        m_xmlPerformance.put(strKey,strValue);
//        strKey = "modelling, total proteins used";
//        sprintf(pLine,"%lu",(int)m_tProteinCount);
//        strValue = pLine;
//        m_xmlPerformance.put(strKey,strValue);
//        strKey = "modelling, total peptides used";
//        sprintf(pLine,"%lu",(int)m_tPeptideCount);
//        strValue = pLine;
//        m_xmlPerformance.put(strKey,strValue);
//        if(m_specCondition.get_noise_suppression())	{
//            strKey = "modelling, spectrum noise suppression ratio";
//            sprintf(pLine,"%.2lf",(double)(m_tSpectraTotal - m_vSpectra.size())/(double)m_tSpectraTotal);
//            strValue = pLine;
//            m_xmlPerformance.put(strKey,strValue);
//        }
//        int tSeq = 0;
//        while(tSeq < m_svrSequences.m_vstrFasta.size())	{
//            sprintf(pLine,"list path, sequence source #%i",(int)(tSeq+1));
//            strKey = pLine;
//            strValue = m_svrSequences.m_vstrFasta[tSeq];
//            m_xmlPerformance.put(strKey,strValue);
//            if(tSeq < m_svrSequences.m_vstrDesc.size())	{
//                sprintf(pLine,"list path, sequence source description #%i",(int)(tSeq+1));
//                strKey = pLine;
//                strValue = m_svrSequences.m_vstrDesc[tSeq];
//                m_xmlPerformance.put(strKey,strValue);
//            }
//            tSeq++;
//        }
//        tSeq = 0;
//        while(tSeq < m_vstrSaps.size())	{
//            sprintf(pLine,"list path, saps source #%i",(int)(tSeq+1));
//            strKey = pLine;
//            strValue = m_vstrSaps[tSeq];
//            m_xmlPerformance.put(strKey,strValue);
//            tSeq++;
//        }
//        tSeq = 0;
//        while(tSeq < m_vstrMods.size())	{
//            sprintf(pLine,"list path, mods source #%i",(int)(tSeq+1));
//            strKey = pLine;
//            strValue = m_vstrMods[tSeq];
//            m_xmlPerformance.put(strKey,strValue);
//            tSeq++;
//        }
//        strKey = "output, maximum valid expectation value";
//        m_xmlValues.get(strKey,strValue);
//        double dMaxExpect = 0.01;
//        if(strValue.size() > 0)	{
//            dMaxExpect = atof(strValue.c_str());
//        }
//        m_dThreshold = dMaxExpect;
//
//        strKey = "refining, # input models";
//        sprintf(pLine,"%u",(int)m_tRefineModels);
//        strValue = pLine;
//        m_xmlPerformance.put(strKey,strValue);
//	
//        strKey = "refining, # input spectra";
//        sprintf(pLine,"%u",(int)(m_tRefineInput));
//        strValue = pLine;
//        m_xmlPerformance.put(strKey,strValue);
//
//        strKey = "refining, # partial cleavage";
//        sprintf(pLine,"%u",(int)(m_tRefinePartial));
//        strValue = pLine;
//        m_xmlPerformance.put(strKey,strValue);
//
//        strKey = "refining, # unanticipated cleavage";
//        sprintf(pLine,"%u",(int)(m_tRefineUnanticipated));
//        strValue = pLine;
//        m_xmlPerformance.put(strKey,strValue);
//
//        strKey = "refining, # potential N-terminii";
//        sprintf(pLine,"%u",(int)(m_tRefineNterminal));
//        strValue = pLine;
//        m_xmlPerformance.put(strKey,strValue);
//
//        strKey = "refining, # potential C-terminii";
//        sprintf(pLine,"%u",(int)(m_tRefineCterminal));
//        strValue = pLine;
//        m_xmlPerformance.put(strKey,strValue);
//
//        strKey = "refining, # point mutations";
//        sprintf(pLine,"%u",(int)(m_tRefinePam));
//        strValue = pLine;
//        m_xmlPerformance.put(strKey,strValue);
//	
//        strKey = "timing, refinement/spectrum (sec)";
//        sprintf(pLine,"%.3lf",m_dRefineTime/(double)m_vSpectra.size());
//        strValue = pLine;
//        m_xmlPerformance.put(strKey,strValue);
//
//        strKey = "spectrum, use contrast angle";
//        m_xmlValues.get(strKey,strValue);
//        if(strValue == "yes")	{
//            strKey = "modelling, contrast angle rejection ratio";
//            sprintf(pLine,"%.2lf",(double)m_tContrasted/((double)m_vSpectra.size()+(double)m_tContrasted));
//            strValue = pLine;
//            m_xmlPerformance.put(strKey,strValue);
//        }
///*
// * calculate the expectation values for the proteins
// */
//        m_prcLog.log("calculating expectation values");
//
//        report_expect(dMaxExpect);
///*
// * now, using the expectation values for the proteins, sort the results into proteins or spectra
// */
//        m_prcLog.log("sorting peptides");
//        report_sort();
///*
// * using the retrieved values, create an mreport and use it to create the output
// */
//
//        dMaxExpect =  Math.log10(dMaxExpect);
//        strKey = "output, results";
//        m_xmlValues.get(strKey,strValue);
//        String strResults = strValue;
//        cout << "\twriting results ";
//        cout.flush();
//        m_prcLog.log("writing results");
//        if(strResults != "all"    strResults != "valid"    strResults != "stochastic")
//            strResults = "all";
//
//        if(strResults == "all")	{
//            report_all();
//        }
//        else if(strResults == "valid")	{
//            report_valid(dMaxExpect);
//        }
//        else if(strResults == "stochastic")	{
//            report_stochastic(dMaxExpect);
//        }
//        cout << "..... done.\n";
//        cout.flush();
//        m_prcLog.log("report complete");
//        delete pLine;
//        return false;
    }

    boolean report_all()
    {
/*
 * check the input parameters for known output information
 */
        String strKey = "output, histogram column width";
        String strValue = m_xmlValues.get(strKey );
        long lHistogramColumns = 30;
        int val = Integer.parseInt(strValue);
        if(val > 0)
            lHistogramColumns = val;

        strKey = "output, spectra";
       strValue =  m_xmlValues.get(strKey );
        boolean bSpectra = false;
        if(strValue == "yes")
            bSpectra = true;

        strKey = "output, histograms";
        strValue =  m_xmlValues.get(strKey );
        boolean bHistograms = false;
        if(strValue == "yes")
            bHistograms = true;

        strKey = "output, sequences";
        strValue =  m_xmlValues.get(strKey );
        boolean bSequences = false;
        if(strValue == "yes")
            bSequences = true;

        strKey = "output, proteins";
        strValue =  m_xmlValues.get(strKey );
        boolean bProteins = false;
        if(strValue == "yes")
            bProteins = true;

        strKey = "output, parameters";
        strValue =  m_xmlValues.get(strKey );
        boolean bInput = false;
        if(strValue == "yes")
            bInput = true;

        strKey = "output, performance";
        strValue =  m_xmlValues.get(strKey );
         boolean bPerf = false;
        if(strValue == "yes")
            bPerf = true;

        strKey = "output, one sequence copy";
        strValue =  m_xmlValues.get(strKey );
        boolean bCompress = false;
        if( "yes".equals(strValue))
            bCompress = true;

        throw new UnsupportedOperationException("Fix This"); // ToDo
//        mreport rptValue(*m_pScore);
//        rptValue.set_compression(bCompress);
//        rptValue.set_columns(lHistogramColumns);
//        rptValue.start(m_xmlValues);
//        int a = 0;	
//        int tLength = m_vSpectra.size();
//        int b = 0;
//        Map<String,String>::iterator itValue;
//
//        while(a < tLength)	{
//            b = 0;
//            while(b < m_vSpectra[a].m_vseqBest.size())	{
//                itValue = m_mapSequences.find(m_vSpectra[a].m_vseqBest[b].m_tUid);
//                m_vSpectra[a].m_vseqBest[b].m_strSeq = (*itValue).second;
//                b++;
//            }
//            if(bSpectra || bHistograms || bProteins)
//                rptValue.group(m_vSpectra[a]);
//            if(bProteins)
//                rptValue.sequence(m_vSpectra[a],bSequences);
//            if(bHistograms)
//                rptValue.histogram(m_vSpectra[a]);
//            if(bSpectra)
//                rptValue.spectrum(m_vSpectra[a]);
//            if(bSpectra || bHistograms || bProteins)
//                rptValue.endgroup();
//            m_vSpectra[a].m_vseqBest.clear();
//            a++;
//        }
//        if(bInput)
//            rptValue.info(m_xmlValues);
//        if(bPerf)
//            rptValue.performance(m_xmlPerformance);
//        //CONSIDER(bmaclean): Report average masses too, if using average for fragments?
//        if(m_pScore.m_pSeqUtilFrag.is_modified())	{
//            rptValue.masses(*(m_pScore.m_pSeqUtilFrag));
//        }
//        return rptValue.end();
    }

    boolean report_valid(final  double _d)
    {
/*
 * check the input parameters for known output information
 */
        String strKey = "output, histogram column width";
        String strValue;
        strValue = m_xmlValues.get(strKey );
        long lHistogramColumns = 30;
        if(Integer.parseInt(strValue) > 0)
            lHistogramColumns = Integer.parseInt(strValue);

        strKey = "output, spectra";
        strValue = m_xmlValues.get(strKey );
        boolean bSpectra = false;
        if("yes".equals(strValue))
            bSpectra = true;

        strKey = "output, histograms";
        strValue = m_xmlValues.get(strKey );
        boolean bHistograms = false;
        if("yes".equals(strValue))
            bHistograms = true;

        strKey = "output, sequences";
        strValue = m_xmlValues.get(strKey );
        boolean bSequences = false;
        if("yes".equals(strValue))
            bSequences = true;

        strKey = "output, proteins";
        strValue = m_xmlValues.get(strKey );
        boolean bProteins = false;
        if("yes".equals(strValue))
            bProteins = true;

        strKey = "output, parameters";
        strValue = m_xmlValues.get(strKey );
        boolean bInput = false;
        if("yes".equals(strValue))
            bInput = true;

        strKey = "output, performance";
        strValue = m_xmlValues.get(strKey );
        boolean bPerf = false;
        if("yes".equals(strValue))
            bPerf = true;

        strKey = "output, one sequence copy";
        strValue = m_xmlValues.get(strKey );
         boolean bCompress = false;
        if("yes".equals(strValue))
            bCompress = true;

        throw new UnsupportedOperationException("Fix This"); // ToDo
//        mreport rptValue(*m_pScore);
//        rptValue.set_compression(bCompress);
//        rptValue.set_columns(lHistogramColumns);
//        rptValue.start(m_xmlValues);
//        int a = 0;	
//        int tLength = m_vSpectra.size();
//        int tActive = 0;
//        double dValue = 0.0;
//        int b = 0;
//        Map<String,String>::iterator itValue;
//        m_tValid = 0;
//        m_tUnique = 1;
//        int tLast = 0;
//        double dProteinMax = pow(10,_d);
//        strKey = "output, maximum valid protein expectation value";
//        m_xmlValues.get(strKey,strValue);
//        if(strValue.size() > 0)	{
//            dProteinMax = atof(strValue.c_str());
//        }
//        dProteinMax =  Math.log10(dProteinMax);
//        double dProtein = 0.0;
//        while(a < tLength)	{
//            dValue = 3.0;
//            long c = 0;
//            if(m_vSpectra[a].m_fScore > 0.0    m_vSpectra[a].m_vseqBest.size() > 0    m_vSpectra[a].m_vseqBest[0].m_vDomains.size() > 0)	{
//                dValue = m_vSpectra[a].m_hHyper.expect(m_pScore.hconvert(m_vSpectra[a].m_vseqBest[0].m_vDomains[0].m_fHyper));
//                dValue =  Math.log10(dValue);
//                dProtein = m_vSpectra[a].m_dProteinExpect;
//            }
//            if(m_vSpectra[a].m_vseqBest.size() > 0    dValue <= _d    dProtein <= dProteinMax)	{
//                b = 0;
//                while(b < m_vSpectra[a].m_vseqBest.size())	{
//                    itValue = m_mapSequences.find(m_vSpectra[a].m_vseqBest[b].m_tUid);
//                    m_vSpectra[a].m_vseqBest[b].m_strSeq = (*itValue).second;
//                    b++;
//                }
//                if(tLast > 0)	{
//                    if(m_vSpectra[a].m_vseqBest[0].m_vDomains[0].m_lS != m_vSpectra[tLast].m_vseqBest[0].m_vDomains[0].m_lS)	{
//                        if(m_vSpectra[a].m_vseqBest[0].m_vDomains[0].m_lE != m_vSpectra[tLast].m_vseqBest[0].m_vDomains[0].m_lE)	{
//                            m_tUnique++;
//                            if(m_lReversed != -1    !m_vSpectra[tLast].m_vseqBest[0].m_bForward)	{
//                                m_lReversed++;
//                            }
//                        }
//                    }
//                }
//                tLast = a;
//                m_tValid++;
//                tActive++;
//                if(bSpectra || bHistograms || bProteins)
//                    rptValue.group(m_vSpectra[a]);
//                if(bProteins)
//                    rptValue.sequence(m_vSpectra[a],bSequences);
//                if(bHistograms)
//                    rptValue.histogram(m_vSpectra[a]);
//                if(bSpectra)
//                    rptValue.spectrum(m_vSpectra[a]);
//                if(bSpectra || bHistograms || bProteins)
//                    rptValue.endgroup();
//            }
//            a++;
//        }
//        if(m_tValid == 0)	{
//            m_tUnique = 0;
//        }
//        strKey = "modelling, total spectra assigned";
//        char *pLine = new char[256];
//        sprintf(pLine,"%u",(int)m_tValid);
//        strValue = pLine;
//        m_xmlPerformance.put(strKey,strValue);
//        strKey = "modelling, total unique assigned";
//        sprintf(pLine,"%u",(int)m_tUnique);
//        strValue = pLine;
//        m_xmlPerformance.put(strKey,strValue);
//        if(m_lReversed != -1)	{
//            strKey = "modelling, reversed sequence false positives";
//            sprintf(pLine,"%i",m_lReversed);
//            strValue = pLine;
//            m_xmlPerformance.put(strKey,strValue);
//        }
//        int lE = (int)(0.5+(double)m_tUnique/(1.0+1.0/m_dThreshold));
//        int lEe = (int)(0.5 + sqrt((double)lE));
//        if(lEe == 0)	{
//            lEe = 1;
//        }
//        strKey = "modelling, estimated false positives";
//        sprintf(pLine,"%u",lE);
//        strValue = pLine;
//        m_xmlPerformance.put(strKey,strValue);
//
//        if(bInput)
//            rptValue.info(m_xmlValues);
//        if(bPerf)
//            rptValue.performance(m_xmlPerformance);
//        //TODO(bmaclean): Report average masses too, if using average for fragments?
//        if(m_pScore.m_pSeqUtilFrag.is_modified())	{
//            rptValue.masses(*(m_pScore.m_pSeqUtilFrag));
//        }
//        delete pLine;
//        return rptValue.end();
    }

    boolean report_stochastic(final  double _d)
    {
/*
 * check the input parameters for known output information
 */
        String strKey = "output, histogram column width";
        String strValue;
        strValue = m_xmlValues.get(strKey );
         long lHistogramColumns = 30;
        if(Integer.parseInt(strValue) > 0)
            lHistogramColumns = Integer.parseInt(strValue);

        strKey = "output, spectra";
        strValue = m_xmlValues.get(strKey );
          boolean bSpectra = false;
        if("yes".equals(strValue))
            bSpectra = true;

        strKey = "output, histograms";
        strValue = m_xmlValues.get(strKey );
          boolean bHistograms = false;
        if("yes".equals(strValue))
            bHistograms = true;

        strKey = "output, sequences";
        strValue = m_xmlValues.get(strKey );
         boolean bSequences = false;
        if("yes".equals(strValue))
            bSequences = true;

        strKey = "output, proteins";
        strValue = m_xmlValues.get(strKey );
         boolean bProteins = false;
        if("yes".equals(strValue))
            bProteins = true;

        strKey = "output, parameters";
         strValue = m_xmlValues.get(strKey );
 ;
        boolean bInput = false;
        if("yes".equals(strValue))
            bInput = true;

        strKey = "output, performance";
         strValue = m_xmlValues.get(strKey );
 ;
        boolean bPerf = false;
        if("yes".equals(strValue))
            bPerf = true;

        strKey = "output, one sequence copy";
         strValue = m_xmlValues.get(strKey );
 ;
        boolean bCompress = false;
        if("yes".equals(strValue))
            bCompress = true;

        throw new UnsupportedOperationException("Fix This"); // ToDo
//        mreport rptValue(*m_pScore);
//        rptValue.set_compression(bCompress);
//        rptValue.set_columns(lHistogramColumns);
//        rptValue.start(m_xmlValues);
//        int a = 0;
//        int b = 0;
//        Map<String,String>::iterator itValue;
//        int tLength = m_vSpectra.size();
//        double dValue = 0.0;
//
//        while(a < tLength)	{
//            dValue = 3.0;
//            if(m_vSpectra[a].m_vseqBest.size() > 0    m_vSpectra[a].m_vseqBest[0].m_vDomains.size() > 0)	{
//                dValue = m_vSpectra[a].m_hHyper.expect(m_pScore.hconvert(m_vSpectra[a].m_vseqBest[0].m_vDomains[0].m_fHyper));
//                dValue =  Math.log10(dValue);
//            }
//            if(m_vSpectra[a].m_vseqBest.size() == 0 || dValue > _d)	{
//                b = 0;
//                while(b < m_vSpectra[a].m_vseqBest.size())	{
//                    itValue = m_mapSequences.find(m_vSpectra[a].m_vseqBest[b].m_tUid);
//                    m_vSpectra[a].m_vseqBest[b].m_strSeq = (*itValue).second;
//                    b++;
//                }
//                if(bSpectra || bHistograms || bProteins)
//                    rptValue.group(m_vSpectra[a]);
//                if(bProteins)
//                    rptValue.sequence(m_vSpectra[a],bSequences);
//                if(bHistograms)
//                    rptValue.histogram(m_vSpectra[a]);
//                if(bSpectra)
//                    rptValue.spectrum(m_vSpectra[a]);
//                if(bSpectra || bHistograms || bProteins)
//                    rptValue.endgroup();
//            }
//            m_vSpectra[a].m_vseqBest.clear();
//            a++;
//        }
//        if(bInput)
//            rptValue.info(m_xmlValues);
//        if(bPerf)
//            rptValue.performance(m_xmlPerformance);
//        //TODO(bmaclean): Report average masses too, if using average for fragments?
//        if(m_pScore.m_pSeqUtilFrag.is_modified() )	{
//            rptValue.masses(*(m_pScore.m_pSeqUtilFrag));
//        }
//        return rptValue.end();
    }
/*
 * report_expect calculates the expectation value for proteins
 */
    boolean report_expect(final  double _m)
    {
//        cout << "\tinitial calculations ";
//        cout.flush();
        int tLength = m_vSpectra.size();
        int a = 0;
        m_tValid = 0;
        int b = 0;
        int tMaxUid = 0;
        throw new UnsupportedOperationException("Fix This"); // ToDo
//        Map<String,String>::iterator itMap = m_mapSequences.begin();
//        while(itMap != m_mapSequences.end())	{
//            if(itMap.first > tMaxUid)	{
//                tMaxUid = itMap.first;
//            }
//            itMap++;
//        }
//        tMaxUid += 10;
//        double *pdExpect = new double[tMaxUid];
//        int *plCount = new int[tMaxUid];
//        final  double dfinal ant = 2.0e32;
//        while(a < tMaxUid)	{
//            pdExpect[a] = dfinal ant;
//            plCount[a] = 0;
//            a++;
//        }
//        double dExpect = 0.0;
//        double dSum = 0;
//        a = 0;
//        while(a < tLength)	{
//            dExpect = (double)m_vSpectra[a].m_hHyper.expect_protein(m_pScore.hconvert(m_vSpectra[a].m_fHyper));
//            dSum += m_vSpectra[a].m_hHyper.sum();
//            if(dExpect <= _m)	{
//                m_tValid++;
//            }
//            m_vSpectra[a].m_dExpect = dExpect;
//            m_vSpectra[a].m_dProteinExpect =  Math.log10(dExpect);
//            a++;
//        }
//        List<Spectrum>::iterator itStart = m_vSpectra.begin();
//        cout << " ..... done.\n\tsorting ";
//        cout.flush();
//        String strKey = "output, results";
//        String strValue;
//         strValue = m_xmlValues.get(strKey );
// ;
//        if(strValue == "valid")	{
//            sort(m_vSpectra.begin(),m_vSpectra.end(),lessThanSpectrum);
//            while(itStart != m_vSpectra.end()    itStart.m_dExpect <= 0.95*_m)	{
//                itStart++;
//            }
//            if(itStart != m_vSpectra.end())	{
//                m_vSpectra.erase(itStart,m_vSpectra.end());
//            }
//        }
//        strKey = "output, sort best scores by";
//         strValue = m_xmlValues.get(strKey );
// ;
//        boolean bSortScores = (strValue != "sequence");
//        int lSum = (int)(0.5+dSum/(double)tLength);
//        cout << " ..... done.\n\tfinding repeats ";
//        cout.flush();
//        mark_repeats();
//        cout << " done.\n\tevaluating results ";
//        cout.flush();
//        a = 0;
//        tLength = m_vSpectra.size();
//        int tUid;
//        int tBest;
//        int tTicLength = (int)((double)tLength/5.0);
//        int tTics = 0;
//        List<mdomain>::iterator itDom;
//        List<mdomain>::iterator itDomEnd;
//        boolean bRound = false;
//        while(a < tLength)	{
//            tTics++;
//            if(tTics >= tTicLength)	{
//                cout << ".";
//                cout.flush();
//                tTics = 0;
//            }
//            dExpect =  Math.log10(m_vSpectra[a].m_dExpect);
//            b = 0;
//            tBest = m_vSpectra[a].m_vseqBest.size();
//            while(b < tBest)	{
//                tUid = m_vSpectra[a].m_vseqBest[b].m_tUid;
//                bRound = false;
//                if(m_vSpectra[a].m_vseqBest[b].m_iRound < 3 || m_lReversed != -1)	{
//                    bRound = true;
//                    m_setRound.insert(tUid);
//                }
//                if(!m_vSpectra[a].m_bRepeat    dExpect <= -1.0)	{
//                    if(pdExpect[tUid] != dfinal ant)	{
//                        if(bRound)	{
//                            plCount[tUid]++;
//                        }
//                        pdExpect[tUid] += dExpect;
//                    }
//                    else if(bRound)	{
//                        plCount[tUid] = 1;
//                        if(dExpect <= -1.0)	{
//                            pdExpect[tUid] = dExpect;
//                        }
//                        else	{
//                            pdExpect[tUid] = 0.0;
//                        }
//                    }
//                }
//                b++;
//            }
//            a++;
//        }
//        a = 0;
//        List< ProteinSequence>::iterator itSeq;
//        double dBias = (double)m_tPeptideCount/(double)m_tProteinCount;
//        dBias /= (double)m_tTotalResidues/(double)m_tProteinCount;
//        Map<String,String>::iterator itValue;
//        cout << " done.\n\tcalculating expectations ";
//        cout.flush();
//        tTics = 0;
//         Map<Integer,double> mapExpect;
//        pair<Integer,double> pairExpect;
//        set<int>::iterator itRound = m_setRound.end();
//        while(a < tLength)	{
//            b = 0;
//            tTics++;
//            if(tTics >= tTicLength)	{
//                cout << ".";
//                cout.flush();
//                tTics = 0;
//            }
//            itSeq = m_vSpectra[a].m_vseqBest.begin();
//            while(itSeq != m_vSpectra[a].m_vseqBest.end())	{
//                tUid = itSeq.m_tUid;
//                if(m_setRound.find(tUid) != itRound)	{
//                    if(pdExpect[tUid] != dfinal ant)	{
//                        if(mapExpect.find(tUid) == mapExpect.end())	{
//                            itSeq.m_dExpect = expect_protein(plCount[tUid],
//                                (int)tLength,lSum,
//                                pdExpect[tUid]);
//                            pairExpect.first = tUid;
//                            pairExpect.second = itSeq.m_dExpect;
//                            mapExpect.insert(pairExpect);
//                        }
//                        else	{
//                            itSeq.m_dExpect = mapExpect.find(tUid).second;
//                        }
//                    }
//                    else	{
//                        itSeq.m_dExpect = 0.0;
//                    }
//                    if(plCount[tUid] > 1)	{
//                        itValue= m_mapSequences.find(tUid);
//                        if((*itValue).second.size()*dBias < 1.0)	{
//                            itSeq.m_dExpect += plCount[tUid]* Math.log10((*itValue).second.size()*dBias);
//                        }
//                    }
//                    itSeq++;
//                }
//                else	{
//                    itSeq = m_vSpectra[a].m_vseqBest.erase(itSeq);
//                }
//            }
//            if(!m_vSpectra[a].m_vseqBest.empty())	{
//                if(bSortScores)	{
//                    sort(m_vSpectra[a].m_vseqBest.begin(),m_vSpectra[a].m_vseqBest.end(),lessThanSequence);
//                }
//                List< ProteinSequence>::iterator itA = m_vSpectra[a].m_vseqBest.begin();
//                List< ProteinSequence>::iterator itB = m_vSpectra[a].m_vseqBest.begin();
//                while(itA != m_vSpectra[a].m_vseqBest.end())	{
//                    itB++;
//                    if(itB == m_vSpectra[a].m_vseqBest.end() || itB.m_dExpect != itA.m_dExpect)	{
//                        sort(itA,itB,lessThanSequenceUid);
//                        itA = itB;
//                    }
//                }
//                m_vSpectra[a].m_dProteinExpect = m_vSpectra[a].m_vseqBest[0].m_dExpect;
//            }
//            a++;
//        }
//        a = 0;
//        b = 0;
//        while(a < tMaxUid)	{
//            pdExpect[a] = dfinal ant;
//            a++;
//        }
//        a = 0;
//        while(a < tLength)	{
//            b = 0;
//            tBest = m_vSpectra[a].m_vseqBest.size();
//            while(b < tBest)	{
//                tUid = m_vSpectra[a].m_vseqBest[b].m_tUid;
//                if(pdExpect[tUid] == dfinal ant)	{
//                    pdExpect[tUid] = m_vSpectra[a].m_vdStats[0];
//                }
//                else	{
//                    pdExpect[tUid] += m_vSpectra[a].m_vdStats[0];
//                }
//                b++;
//            }
//            a++;
//        }
//        a = 0;
//        b = 0;
//        while(a < tLength)	{
//            b = 0;
//            tBest = m_vSpectra[a].m_vseqBest.size();
//            while(b < tBest)	{
//                m_vSpectra[a].m_vseqBest[b].m_fIntensity = (float)(pdExpect[m_vSpectra[a].m_vseqBest[b].m_tUid]);
//                b++;
//            }
//            a++;
//        }
//        cout << " done.\n";
//        cout.flush();
//        delete plCount;
//        delete pdExpect;
//        return true;
    }
/*
 * report_sort contains the logic for sorting the output results, if sorting
 * is desired.
 */
    boolean report_sort()
    {
        String strKey = "output, sort results by";
        String strValue;
         strValue = m_xmlValues.get(strKey );
        throw new UnsupportedOperationException("Fix This"); // ToDo
//        List< ProteinSequence>::iterator itSeq;
//        List< ProteinSequence>::iterator itSort;
//        if(strValue == "protein")	{
//            sort(m_vSpectra.begin(),m_vSpectra.end(),lessThanSpectrum);
//            List<Spectrum>::iterator itStart = m_vSpectra.begin();
//            List<Spectrum>::iterator itEnd = itStart;
//            while(itStart != m_vSpectra.end()    itEnd != m_vSpectra.end())	{
//                while(itStart != m_vSpectra.end()    itStart.m_vseqBest.empty())
//                    itStart++;
//                if(itStart == m_vSpectra.end())
//                    break;
//                itEnd = itStart + 1;
//                while(itEnd != m_vSpectra.end())	{
//                    if(!itEnd.m_vseqBest.empty())	{
//                        if(itStart.m_vseqBest[0].m_tUid == itEnd.m_vseqBest[0].m_tUid)	{
//                            itEnd++;
//                        }
//                        else	{
//                            break;
//                        }
//                    }
//                    else	{
//                        break;
//                    }
//                }
//                if(itEnd != itStart + 1)	{
//                    sort(itStart,itEnd,lessThanOrder);
//                }
//                itStart = itEnd;
//            }
//        }
//        return true;
    }

/*
 * residues is used to estimate the minimum number of residues that are required for a peptide
 * to match the lowest mass spectrum in the m_Spectra List. this estimate is made to
 * improve performance, by simply rejecting very small sequences from consideration by the
 * calculationally intensive scoring routines.
 */
   public  boolean residues()
    {
/*
 * make a very conservative estimate of the minimum number of residues
 * As of version 2004.03.01, the more complex version of this function has been
 * altered to return simply a final ant value. The older method of estimating
 * the minimum number of residues was found to fail badly when there were
 * large moeities added as modifications to the peptide sequences.
 */

        m_tMinResidues = 4; 
        return true;
    }

/*
* rollback is a method that is used to reverse changes that have been made to the
* m_vSpectra List during the refinement process. If a new result, discovered during
* the refinement process, is not sufficiently significant (as determined by _f), then
* the m_vSpectra entry is "rolled back" to the value it had after the initial 
* survey round.
*/
    public boolean rollback(List<Spectrum>  _v,final  double _m,final  double _f)
    {
        if(_v.isEmpty())	{
            return false;
        }
        int a = 0;
        final  int tSize = m_vSpectra.size();
        double dExpect = 1.0;
        double dExpectLast = 1.0;
        boolean bDone = false;
        throw new UnsupportedOperationException("Fix This"); // ToDo
//        while(a < tSize)	{
//            if(!m_vSpectra[a].m_vseqBest.empty()    !_v[a].m_vseqBest.empty())	{
//                bDone = false;
//                m_vSpectra[a].m_hHyper.model();
//                m_vSpectra[a].m_hHyper.set_protein_factor(1.0);
//                dExpect = (double)m_vSpectra[a].m_hHyper.expect_protein(m_pScore.hconvert(m_vSpectra[a].m_fHyper));
//                dExpectLast = (double)m_vSpectra[a].m_hHyper.expect_protein(m_pScore.hconvert(_v[a].m_fHyper));
//                if(dExpect > _m)	{
//                    m_vSpectra[a] *= _v[a];
//                    bDone = true;
//                }
//                else if(dExpect <= _m     dExpect/dExpectLast > _f)	{
//                    m_vSpectra[a] *= _v[a];
//                    bDone = true;
//                }
//                else if(!bDone    m_vSpectra[a].m_fHyper == _v[a].m_fHyper)	{
//                    m_vSpectra[a] *= _v[a];
//                }
//            }
//            a++;
//        }
//        _v.clear();
//       return true;
    }

/*
 * score takes an  ProteinSequence object and sequentially creates all possible cleavage peptides
 * from that  ProteinSequence. each peptide is then tested and scored.
 */
    public boolean score(final   ProteinSequence  _s)
    {
        int m = 0;
        String strValue;
        throw new UnsupportedOperationException("Fix This"); // ToDo
//        if(!m_vstrModifications.isEmpty())	{
//            strValue = m_vstrModifications.get(m);
//            m_pScore.m_seqUtil.modify_all(strValue);
//            m_pScore.m_seqUtilAvg.modify_all(strValue);
//        }
//        boolean bReturn = score_single(_s);
//        m++;
//        while(m < m_vstrModifications.size())	{
//            strValue =  m_vstrModifications.get(m);
//            m_pScore.m_seqUtil.modify_all(strValue);
//            m_pScore.m_seqUtilAvg.modify_all(strValue);
//            bReturn = score_single(_s);
//            m++;
//        }
//        return bReturn;
    }

   public  boolean score_terminus(final  String  _s)
    {
        int m = 0;
        String strValue;
        throw new UnsupportedOperationException("Fix This"); // ToDo
//        if(!m_vstrModifications.isEmpty())	{
//            strValue =  m_vstrModifications.get(m);
//            m_pScore.m_seqUtil.modify_all(strValue);
//            m_pScore.m_seqUtilAvg.modify_all(strValue);
//        }
//        boolean bReturn = score_terminus_single(_s);
//        m++;
//        while(m < m_vstrModifications.size())	{
//            strValue =  m_vstrModifications.get(m);
//            m_pScore.m_seqUtil.modify_all(strValue);
//            m_pScore.m_seqUtilAvg.modify_all(strValue);
//            bReturn = score_terminus_single(_s);
//            m++;
//        }
//        return bReturn;
    }

    public boolean score_single(final   ProteinSequence  _s)
    {
        throw new UnsupportedOperationException("Fix This"); // ToDo
//        m_pScore.m_seqUtil.motif_set(_s);
//        if(m_bAnnotation  &&  !m_mapAnnotation.empty())	{
//             Map <String,String>::iterator itMod;
//            if(_s.m_strDes.find("IPI") != _s.m_strDes.npos)	{
//                int tFind = _s.m_strDes.find("IPI");
//                int tEnd = _s.m_strDes.find(".",tFind);
//                itMod = m_mapAnnotation.find(_s.m_strDes.substr(tFind,tEnd-tFind));
//            }
//            else	{
//                itMod = m_mapAnnotation.find(_s.m_strDes);
//            }
//            String strMods;
//            if(itMod != m_mapAnnotation.end())	{
//                strMods = itMod.second;
//            }
//            else	{
//                strMods.clear();
//            }
//            if(m_strLastMods != strMods)	{
//                m_pScore.m_seqUtil.modify_annotation(strMods);
//            }
//            m_strLastMods = strMods;
//        }
//        String strDesc = _s.m_strDes;
//        m_pScore.set_saps(m_bSaps,strDesc);
//        long lLength = (long)_s.m_strSeq.size();
//        if(m_tSeqSize < (int)(lLength+1))	{
//            delete m_pSeq;
//            m_tSeqSize = 4096*(int)(ceil((double)lLength/4096.0)+1);
//            m_pSeq = new char[m_tSeqSize];
//        }
//        strcpy(m_pSeq,_s.m_strSeq.c_str());
//        char cValue;
//        m_tTotalResidues += lLength;
//        long lStart = 0;
//        if(m_bRefineCterm)	{
//            lStart = lLength - m_lCStartMax;
//            if(lStart < 0)	{
//                lStart = 0;
//            }
//        }
//        long lEnd = 0;
//        boolean bBreak = false;
//        final  long lSpectra = (long) m_tSpectra;
//        boolean bIsFirst = true;
//        long lMissedCleaves = 0;
//        long lNextStart = 0;
///*
// * set up variables and make some final  local copies of frequently used member variables
// */
//        final  long lMissedMax = (long)m_tMissedCleaves;
//        final  long lMinAa = (long)m_tMinResidues;
//        long lLastCleave = -1;
//        float fMinMax = 0;
//        m_semiState.limit(lMinAa);
//        if(m_Cleave.m_lType   0x01)	{
//            m_semiState.activate(false);
//        }
//        final  char cAster = '*';
///*
// * continue operations until the start cursor (the beginning of the new peptide) reaches
// * the end of the sequence
// */
//        while(lStart < lLength    lStart < m_lStartMax)	{
//            bBreak = false;
//            while(m_pSeq[lStart] == cAster    lStart < m_lStartMax)	{
//                lStart++;
//            }
//            lEnd = lStart;
//            if(lEnd >= lLength)
//                lEnd = lLength - 1;
//            bIsFirst = true;
//            lMissedCleaves = 0;
//            lLastCleave = -1;
///*
// * with a given lStart, vary the end cursor (lEnd), using the cleavage rules, until
// * none of the spectra have a m_ParentStream ion as large as the peptide (tEligible == 0),
// * or the number of missed cleavages is larger than the maximum allowed
// */
//            while(lEnd < lLength)	{
///*
// * get the next allowed lEnd
// */
//                while(lEnd < lLength)	{
//                    if(m_pSeq[lEnd] == cAster)	{
//                        if(lMissedCleaves == 0)
//                            lNextStart = lEnd+1;
//                        lMissedCleaves = lMissedMax;
//                        lEnd--;
//                        break;
//                    }
//                    if(m_Cleave.m_lType   0x02)	{
//                        if(m_pSeq[lEnd+1] != 'P')	{
//                            if(m_pSeq[lEnd] == 'K' || m_pSeq[lEnd] == 'R')	{
//                                if(lMissedCleaves == 0)
//                                    lNextStart = lEnd+1;
//                                break;
//                            }
//                        }
//                    }
//                    else if(m_Cleave.m_lType   0x01)	{
//                        if(lMissedCleaves == 0)
//                            lNextStart = lEnd+1;
//                        break;
//                    }
//                    else if(m_Cleave.test(m_pSeq[lEnd],m_pSeq[lEnd+1]))	{
//                        if(lMissedCleaves == 0)
//                            lNextStart = lEnd+1;
//                        break;
//                    }
//                    lEnd++;
//                }
//                if(lEnd == lLength    lMissedCleaves == 0)	{
//                        lNextStart = lEnd+1;
//                }
//                lMissedCleaves++;
///*
// * update the peptide sequence in m_pScore
// */
//                if(lEnd >= lLength)
//                    lEnd = lLength - 1;
//                if(lEnd - lStart >= lMinAa    lEnd < lLength)	{
//                    m_semiState.reset(lStart,lEnd,lLastCleave);
//                    fMinMax = 0.0;
//                    do	{
//                        cValue = m_pSeq[lEnd+1];
//                        m_pSeq[lEnd+1] = '\0';
//                        pyro_check(*(m_pSeq+lStart));
//                        m_pScore.set_pos(lStart);
//                        if(bIsFirst || m_semiState.is_active())	{
//                            m_pScore.set_seq(m_pSeq+lStart,lStart == 0,lEnd == lLength-1,lEnd-lStart+1,lStart);
//                        }
//                        else	{
//                            m_pScore.add_seq(m_pSeq+lStart,lStart == 0,lEnd == lLength-1,lEnd-lStart+1,lStart);
//                        }
//                        m_pSeq[lEnd+1] = cValue;
//                        bIsFirst = false;
//        /*
//        * use the m_pScore state machine to obtain allowed modified peptides that have
//        * the same sequence. then use create_score to score relavent spectra
//        */
//                        while(m_pScore.load_next())	{
//                            m_tPeptideCount += m_pScore.m_State.m_lEqualsS;
//                            m_bPermute = false;
//                            m_bPermuteHigh = false;
//                            create_score(_s,lStart,lEnd,lMissedCleaves - 1,true);
//                            if(m_bPermute    m_bCrcCheck || m_bPermuteHigh)	{
//                                m_pScore.reset_permute();
//                                while(m_pScore.permute())	{
//                                    create_score(_s,lStart,lEnd,lMissedCleaves - 1,false);
//                                }
//                            }
//                        }
//                        if(m_pyroState.m_bPyro)	{
//                            pyro_reset();
//                        }
//                        if(m_pScore.m_fMinMass > fMinMax)	{
//                            fMinMax = m_pScore.m_fMinMass;
//                        }
//        /*
//        * as of version 2004.03.01, the test for the number of missed cleavage sites (see the next note)
//        * was moved outside of this test.
//        */
//                    } while(m_semiState.next(lStart,lEnd));
//                    if(fMinMax - m_pScore.m_fMaxMass > 100.0)	{
//                        break;
//                    }
//                }
//                lLastCleave = lEnd;
///*
// * as of version 2004.03.01, the test for the number of missed cleavage sites was moved to
// * out of the previous if structure, because it could occasionally result in peptides being
// * considered that contained too many missed cleavage sites.
// */
//                if(lMissedCleaves > lMissedMax)	{
//                    break;
//                }
//                lEnd++;
//            }
///*
// * get the next allowed value for the start peptide cursor
// */
//            if(m_Cleave.m_lType   0x02)	{
//                if(m_pSeq[lStart+1] != 'P')	{
//                    if(m_pSeq[lStart] == 'K' || m_pSeq[lStart] == 'R')	{
//                        lStart++;
//                    }
//                    else	{
//                        lStart = lNextStart;
//                    }
//                }
//                else	{
//                    lStart = lNextStart;
//                }
//            }
//            else if(m_Cleave.test(m_pSeq[lStart],m_pSeq[lStart+1]))	{
//                lStart++;
//            }
//            else	{
//                lStart = lNextStart;
//            }
//        }
//        return true;
    }

    private boolean pyro_check(final  char _c)
    {
        throw new UnsupportedOperationException("Fix This"); // ToDo
//        if(m_pScore.m_seqUtil.m_pdAaMod['['] != 0.0)	{
//            m_pyroState.m_bPyro = false;
//            return false;
//        }
//        if(_c == 'Q')	{
//            m_pyroState.m_dModMass = -1.0*m_pScore.m_seqUtil.m_dAmmonia;
//            m_pScore.m_seqUtil.m_pdAaMod['['] = m_pyroState.m_dModMass;
//            m_pScore.m_seqUtilAvg.m_pdAaMod['['] = -1.0*m_pScore.m_seqUtilAvg.m_dAmmonia;
//            m_pyroState.m_bPotential = m_pScore.m_seqUtil.m_bPotential;
//            m_pScore.m_seqUtil.m_bPotential = true;
//            m_pScore.m_seqUtilAvg.m_bPotential = true;
//            m_pyroState.m_bPyro = true;
//            m_pyroState.m_cRes = 'Q';
//            return true;
//        }
//        else if(_c == 'E')	{
//            m_pyroState.m_dModMass = -1.0*m_pScore.m_seqUtil.m_dWater;
//            m_pScore.m_seqUtil.m_pdAaMod['['] = m_pyroState.m_dModMass;
//            m_pScore.m_seqUtilAvg.m_pdAaMod['['] = -1.0*m_pScore.m_seqUtilAvg.m_dWater;
//            m_pyroState.m_bPotential = m_pScore.m_seqUtil.m_bPotential;
//            m_pScore.m_seqUtil.m_bPotential = true;
//            m_pScore.m_seqUtilAvg.m_bPotential = true;
//            m_pyroState.m_bPyro = true;
//            m_pyroState.m_cRes = 'E';
//            return true;
//        }
//        else if(_c == 'C'    (long)(m_pScore.m_seqUtil.m_pdAaFullMod['C']) == 57)	{
//            m_pyroState.m_dModMass = -1.0*m_pScore.m_seqUtil.m_dAmmonia;
//            m_pScore.m_seqUtil.m_pdAaMod['['] = m_pyroState.m_dModMass;
//            m_pScore.m_seqUtilAvg.m_pdAaMod['['] = -1.0*m_pScore.m_seqUtilAvg.m_dAmmonia;
//            m_pyroState.m_bPotential = m_pScore.m_seqUtil.m_bPotential;
//            m_pScore.m_seqUtil.m_bPotential = true;
//            m_pScore.m_seqUtilAvg.m_bPotential = true;
//            m_pyroState.m_bPyro = true;
//            m_pyroState.m_cRes = 'C';
//            return true;
//        }
//        m_pyroState.m_bPyro = false;
//        return false;
    }

    private boolean pyro_reset()
    {
        throw new UnsupportedOperationException("Fix This"); // ToDo
//        m_pScore.m_seqUtil.m_bPotential = m_pyroState.m_bPotential;
//        m_pScore.m_seqUtilAvg.m_bPotential = m_pyroState.m_bPotential;
//        m_pyroState.m_bPyro = false;
//        m_pyroState.m_cRes = '\0';
//        m_pScore.m_seqUtil.m_pdAaMod['['] = 0.0;
//        m_pScore.m_seqUtilAvg.m_pdAaMod['['] = 0.0;
//        m_pyroState.m_dModMass = 0.0;
//        return true;
    }

/*
 * score_each_sequence runs through the list of sequences in m_svrSequences and
 * generates scores for the peptides in those sequences.
 */
    public boolean score_each_sequence()
    {
        throw new UnsupportedOperationException("Fix This"); // ToDo
//        int tLength = m_svrSequences.m_pCol.size();
//        int a = 0;
////	 Map <String,String>::iterator itMod;
////	String strMods;
//        String strValue;
///*
// * go through the  ProteinSequenceCollection object and score each  ProteinSequence
// */
//        while(a < tLength)	{
//            if(!m_bReversedOnly)	{
//                m_svrSequences.m_pCol.m_vASequences[a].m_tUid = m_tProteinCount+1;
//                m_svrSequences.m_pCol.m_vASequences[a].m_bForward = true;
//                score(m_svrSequences.m_pCol.m_vASequences[a]);
//                m_tProteinCount++;
//            }
//            if(m_lReversed != -1)	{
//                m_svrSequences.m_pCol.m_vASequences[a].m_tUid = m_tProteinCount+1;
//                m_svrSequences.m_pCol.m_vASequences[a].m_bForward = false;
//                String strTemp;
//                String::reverse_iterator itS = m_svrSequences.m_pCol.m_vASequences[a].m_strSeq.rbegin();
//                String::reverse_iterator itE = m_svrSequences.m_pCol.m_vASequences[a].m_strSeq.rend();
//                while(itS != itE)	{
//                    strTemp += *itS;
//                    itS++;
//                }
//                m_svrSequences.m_pCol.m_vASequences[a].m_strSeq = strTemp;
//                m_svrSequences.m_pCol.m_vASequences[a].m_strDes += ":reversed";
//                score(m_svrSequences.m_pCol.m_vASequences[a]);
//                m_tProteinCount++;
//            }
//            a++;
//        }
//        return true;
    }

/*
 *  score_terminus is used to attempt to find modified terminii. the modifications
 * are entered as a String, e.g.
 *		_s = "42@[,27@["
 * means that the N-terminus may be modified by 42 or 27 daltons. Similarly:
 *		_s = -1@]"
 * means that the C-terminus may be modified by -1 dalton. each potential modification
 * is used separately. the protein sequence is cleaved using [X]|[X].
 */
    public boolean score_terminus_single(final  String  _s)
    {
        if(_s.length() == 0)	{
            return false;
        }
        int tStart = 0;
        int tAt = 0;
        throw new UnsupportedOperationException("Fix This"); // ToDo
//        String strValue = _s.substr(tStart,_s.size()-tStart);
//        double dValue = atof(strValue.c_str());
//        String strKey = "refine, tic percent";
//         strValue = m_xmlValues.get(strKey );
// ;
//        double dTicPercent = atof(strValue.c_str());
//        if(dTicPercent == 0)	{
//            dTicPercent = 20.0;
//        }
//        int tTicMax = (int)((double)m_vseqBest.size()*dTicPercent/100.0);
//        if(tTicMax < 1)	{
//            tTicMax = 1;
//        }
//        int a = 0;
//        int tPips = 0;
//        boolean bPotential = m_pScore.m_seqUtil.m_bPotential;
//        while(Math.abs(dValue) > 0.001)	{
//            tAt = _s.find('@',tStart);
//            if(tAt == _s.npos)
//                break;
//            tAt++;
//            m_pScore.m_seqUtil.m_bPotential = true;
//            m_pScore.m_seqUtilAvg.m_bPotential = true;
//            m_pScore.m_seqUtil.m_pdAaMod[_s[tAt]] = dValue;
//            m_pScore.m_seqUtilAvg.m_pdAaMod[_s[tAt]] = dValue;
//            a = 0;
//            tPips = 0;
//            while(a < m_vseqBest.size())	{
//                score(m_vseqBest[a]);
//                tPips++;
//                if(tPips == tTicMax)	{
//                    if(m_lThread == 0 || m_lThread == 0xFFFFFFFF)	{
//                        cout << ".";
//                        cout.flush();
//                        m_prcLog.log(".");
//                    }
//                    tPips = 0;
//                }
//                a++;
//            }
//            tStart = _s.find(',',tAt);
//            if(tStart == _s.npos)
//                break;
//            cout << ". ";
//            cout.flush();
//            tStart++;
//            strValue = _s.substr(tStart,_s.size()-tStart);
//            dValue = atof(strValue.c_str());
//        }
//        m_pScore.m_seqUtil.m_bPotential = bPotential;
//        m_pScore.m_seqUtilAvg.m_bPotential = bPotential;
//        return true;
    }
/*
 * set_thread tells the mprocess it's assigned thread number. this number
 * is used to determine which parts of the sequence list are to be
 * processed by this mprocess object
 */
    public boolean set_thread(final  int _t)
    {
        m_lThread = _t;
        return true;
    }

    boolean set_threads(final  int _t)
    {
        m_lThreads = _t;
        if(m_lThreads == 1)	{
            m_lThread = 0xFFFFFFFF;
        }
        return true;
    }
/*
 * using the input parameters from the input XML file, retrieve the tandem MS spectra
 */
    public boolean spectra()
    {
        String strValue;
        String strKey;
        strKey = "spectrum, threads";
        throw new UnsupportedOperationException("Fix This"); // ToDo
//
//    #ifdef MAP_REDUCE
//        boolean useOne = the_mapreducehelper().mode_mapreduce();
//    #else
//        boolean useOne = false;
//    #endif // MAP_REDUCE
//
//        if (useOne) { // multi processor instead of multithread
//            strValue="1";
//            m_xmlValues.set(strKey, strValue);
//        } else {
//             strValue = m_xmlValues.get(strKey );
// ;
//        }
//        int lThreads = Integer.parseInt(strValue);
//        if(lThreads > 1)	{
//            m_lThreads = lThreads;
//        }
//        else	{
//            m_lThread = 0xFFFFFFFF;
//        }
//        strKey = "output, log path";
//        strValue = "no";
//         strValue = m_xmlValues.get(strKey );
// ;
//        if(m_lThread == 0 || m_lThread == 0xFFFFFFFF)	{
//            if(strValue.size() > 0)	{
//                m_prcLog.open(strValue);
//                strKey = "output, path";
//                 strValue = m_xmlValues.get(strKey );
// ;
//                m_prcLog.log("X! Tandem starting");
//            }
//        }
//        if(m_vSpectra.size() > 0)	{
//            m_tSpectraTotal = m_vSpectra.size();
//            return true;
//        }
//        m_vSpectra.clear();
//        Spectrum spCurrent;
//        boolean bContinue = true;
//        m_tSpectraTotal = 0;
//        strKey = "spectrum, path";
//         strValue = m_xmlValues.get(strKey );
// ;
//        FILE *pStream;
//        boolean bCommon = false;
//        pStream = fopen(strValue.c_str(),"r");
//        char *pValue = new char[1028];
//        memset(pValue,0,1028);
//        int tRead = 256;
//        if(pStream)	{
//            tRead = fread(pValue,1,256,pStream);
//        }
//        if(pStream)	{
//            fclose(pStream);
//            if(pValue[0] == 1    pValue[1] == -95 || (pValue[3] == 'F'    pValue[5] == 'i'    pValue[7] == 'n') )	{
//                cout << "\nFailed to read spectrum file: " << strValue.c_str() << "\n";
//                cout << "Most likely cause: using a Finnigan raw spectrum.\nUse dta, pkl, mgf, mzdata (v.1.05) or mzxml (v.2.0) files ONLY! (1)\n\n";
//                cout.flush();
//                m_prcLog.log("error reading spectrum file 1");
//                delete pValue;
//                return false;
//            }
//            else if(strstr(pValue,"CMN ") == pValue)	{
//                bCommon = true;
//            }
//            else 	{
//                int d = 0;
//                while(d < tRead)	{
//                    if(pValue[d] == '\0')	{
//                        cout << "\nFailed to read spectrum file: " << strValue.c_str() << "\n";
//                        cout << "Most likely cause: using a binary spectrum file.\nUse dta, pkl, mgf, mzdata (v.1.05) or mzxml (v.2.0) files ONLY! (2)\n\n";
//                        cout.flush();
//                        m_prcLog.log("error reading spectrum file 2");
//                        delete pValue;
//                        return false;
//                    }
//                    d++;
//                }
//            }
//            if(strstr(pValue,"<HTML") != null || strstr(pValue,"<!DOCTYPE HTML") != null || strstr(pValue,"<html") != null)	{
//                    cout << "\nFailed to read spectrum file: " << strValue.c_str() << "\n";
//                    cout << "Most likely cause: using an HTML file.\nUse dta, pkl, mgf, mzdata (v.1.05) or mzxml (v.2.0) files ONLY! (2)\n\n";
//                    cout.flush();
//                    m_prcLog.log("error reading spectrum file 3");
//                    delete pValue;
//                    return false;
//            }
//        }
//
//        ifstream ifTest;
//        ifTest.open(strValue.c_str());
//        ifTest.getline(pValue,1024);
//        if(strlen(pValue) == 1023)	{
//            ifTest.close();
//            ifTest.clear();
//            ifTest.open(strValue.c_str());
//            pValue[0] = '\0';
//            ifTest.getline(pValue,1024,'\r');
//            ifTest.close();
//            if(strlen(pValue) == 1023    !strchr(pValue,'<'))	{
//                cout << "\nFailed to read spectrum file: " << strValue.c_str() << "\n";
//                cout << "Most likely: an unsupported data file type:\nUse dta, pkl, mgf, mzdata (v.1.05) or mzxml (v.2.0) files ONLY! (3)\n\n";
//                cout.flush();
//                m_prcLog.log("error reading spectrum file 3");
//                delete pValue;
//                return false;
//            }
//        }
//        ifTest.close();
//        delete pValue;
//        long lLoaded = 0;
//        long lLimit = 2000;
///*
// * check the input file for GAML encoded spectra
// */
//        m_prcLog.log("loading spectra");
//        if(bCommon)	{
//            loadcmn ldCmn;
//            if(ldCmn.open(strValue))	{
//                while(ldCmn.get(spCurrent))	{
//                    m_tSpectraTotal++;
//                    lLoaded++;
//                    if(lLoaded == lLimit)	{
//                        cout << ".";
//                        cout.flush();
//                        lLoaded = 0;
//                        m_prcLog.log(".\n");
//                    }
//                    if(m_specCondition.condition(spCurrent, *m_pScore))	{
//                        m_vSpectra.push_back(spCurrent);
//                    }
//                }
//                if(spCurrent.m_vMI.size() > 0)	{
//                    m_tSpectraTotal++;
//                    m_vSpectra.push_back(spCurrent);
//                }
//                bContinue = false;
//            }
//        }
//        if(bContinue){
//            boolean bState = m_specCondition.m_bCondition;
//            m_specCondition.use_condition(false);
//            loadgaml ldGaml(m_vSpectra, m_specCondition, *m_pScore);
//            String strV;
//            m_xmlValues.getpath(strV);
//            if(ldGaml.open(strV))	{
//                ldGaml.get();
//                m_tSpectraTotal = m_vSpectra.size();
//                bContinue = false;
//            }
//            m_specCondition.use_condition(bState);
//        }
///*
// * if the input file did not contain GAML spectra, test for the existence of a
// * "spectrum, path type" parameter. If it exists, use that file format type
// * to force the use of that format.
// * Added in the 2005.08.15 release
// */
//        if(bContinue)	{
//            strKey = "spectrum, path type";
//            String strType;
//            m_xmlValues.get(strKey,strType);
//            if(strType.size() > 0)	{
//                return spectra_force(strType,strValue);
//            }
//        }
///*
// * if the input file did not contain GAML spectra, test the spectrum path parameter
// * for aGAML spectrum information
// * the ability to use a GAML formated spectrum file was added in v 2004-04-01
// */
//        if(bContinue)	{
//            boolean bState = m_specCondition.m_bCondition;
//            m_specCondition.use_condition(false);
//            loadgaml ldGaml(m_vSpectra, m_specCondition, *m_pScore);
//            if(ldGaml.open(strValue))	{
//                ldGaml.get();
//                m_tSpectraTotal = m_vSpectra.size();
//                bContinue = false;
//            }
//            m_specCondition.use_condition(bState);
//        }
///*
// * if the spectrum file did not contain GAML spectra, test the spectrum path parameter
// * for a Matrix Science format file
// */
//        if(bContinue)	{
//            loadmatrix ldMatrix;
//            if(ldMatrix.open(strValue))	{
//                while(ldMatrix.get(spCurrent))	{
//                    m_tSpectraTotal++;
//                    lLoaded++;
//                    if(lLoaded == lLimit)	{
//                        cout << ".";
//                        cout.flush();
//                        lLoaded = 0;
//                        m_prcLog.log(".\n");
//                    }
//                    if(m_specCondition.condition(spCurrent, *m_pScore))
//                        m_vSpectra.push_back(spCurrent);
//                }
//                if(spCurrent.m_vMI.size() > 0)	{
//                    m_tSpectraTotal++;
//                    if(m_specCondition.condition(spCurrent, *m_pScore))
//                        m_vSpectra.push_back(spCurrent);
//                }
//                bContinue = false;
//            }
//        }
///*
// * if no Matrix Science format data was found, test for PKL format information
// */
//        if(bContinue)	{
//            loadpkl ldPkl;
//            if(ldPkl.open(strValue))	{
//                while(ldPkl.get(spCurrent))	{
//                    m_tSpectraTotal++;
//                    lLoaded++;
//                    if(lLoaded == lLimit)	{
//                        cout << ".";
//                        cout.flush();
//                        lLoaded = 0;
//                        m_prcLog.log(".\n");
//
//                    }
//                    if(m_specCondition.condition(spCurrent, *m_pScore))
//                        m_vSpectra.push_back(spCurrent);
//                }
//                if(spCurrent.m_vMI.size() > 0)	{
//                    m_tSpectraTotal++;
//                    if(m_specCondition.condition(spCurrent, *m_pScore))
//                        m_vSpectra.push_back(spCurrent);
//                }
//                bContinue = false;
//            }
//        }
//    #ifdef XMLCLASS
//      /*
//       * if no PKl format data was found, test for mzxml format information
//       * Celui ci sera different des autres.
//       * La boucle while(ldPkl.get(spCurrent))
//       * est remplacee par le travail de la machine xml provenant de mzxml2other
//       * ldMzxml.open instantie la machine
//       * tandis que ldMzxml.get l'enclenche
//       */
//      if(bContinue)	{
//        loadmzxml ldMzxml( m_vSpectra, m_specCondition, *m_pScore);
//        if(ldMzxml.open(strValue))	{
//          //Ce .get est different, il va chercher tous les spectres du fichier.
//          ldMzxml.get();
//          m_tSpectraTotal = m_vSpectra.size();
//          bContinue = false;
//        }
//
//      }
//      /*
//       * if no MzXML format data was found, test for MzData format information
//       * Comme pour mzxml
//       */
//      if(bContinue)	{
//       loadmzdata ldMzdata( m_vSpectra, m_specCondition, *m_pScore);
//        if(ldMzdata.open(strValue))  {
//          //Ce .get est different, il va chercher tous les spectres du fichier.
//          ldMzdata.get();
//          m_tSpectraTotal = m_vSpectra.size();
//          bContinue = false;
//        }
//
//      }
//    #endif
//    #ifdef HAVE_PWIZ_MZML_LIB
//      /*
//       * if no mzdata format data was found, test for pwiz-supported format
//       */
//          if(bContinue)	{
//            loadpwiz lspwiz;
//            if(lspwiz.open(strValue))	{
//                while(lspwiz.get(spCurrent))	{
//                    m_tSpectraTotal++;
//                    lLoaded++;
//                    if(lLoaded == lLimit)	{
//                        cout << ".";
//                        cout.flush();
//                        lLoaded = 0;
//                        m_prcLog.log(".\n");
//                    }
//                    if(m_specCondition.condition(spCurrent, *m_pScore))
//                        m_vSpectra.push_back(spCurrent);
//                }
//                bContinue = false;
//            }
//        }
//    #endif // ifdef HAVE_PWIZ_MZML_LIB
//
//      /*
//       * if no mzxml format data was found, test for dta format information
//       */
//        if(bContinue)	{
//            loaddta ldSpec;
//            if(!ldSpec.open(strValue))	{
///*
// * report an error if no DTA information was found: there are no more default file types to check
// */
//                cout << "\nFailed to read spectrum file: " << strValue.c_str() << "\n";
//                cout << "Most likely: an unsupported data file type:\nUse dta, pkl, mgf, mzdata (v.1.05) or mxzml (v.2.0) files ONLY! (4)\n\n";
//                cout.flush();
//                m_prcLog.log("error loading spectrum file 4");
//                return false;
//            }
//            else	{
//                while(ldSpec.get(spCurrent))	{
//                    m_tSpectraTotal++;
//                    lLoaded++;
//                    if(lLoaded == lLimit)	{
//                        cout << ".";
//                        cout.flush();
//                        lLoaded = 0;
//                        m_prcLog.log(".\n");
//                    }
//                    if(m_specCondition.condition(spCurrent, *m_pScore))
//                        m_vSpectra.push_back(spCurrent);
//                }
//                if(spCurrent.m_vMI.size() > 0)	{
//                    m_tSpectraTotal++;
//                    if(m_specCondition.condition(spCurrent, *m_pScore))
//                        m_vSpectra.push_back(spCurrent);
//                }
//            }
//        }
//        strKey = "spectrum, use contrast angle";
//         strValue = m_xmlValues.get(strKey );
// ;
//        if("yes".equals(strValue))	{
//            // remove spectra that are redundant
//            subtract();
//        }
//        m_prcLog.log("spectra loaded");
//        return true;
    }
/*
 * This method loads sequences from a bioml sequence data file.
 * It was first introduced in version 2006.04.01
*/
   public  boolean load_sequences()
    {
        String strKey = "refine, sequence path";
        String strValue;
        int a = 0;
        // Check for input sequences for refinement to be loaded from a bioml file
        // Loading done for each thread
         strValue = m_xmlValues.get(strKey );
 ;
        throw new UnsupportedOperationException("Fix This"); // ToDo
//        if(strValue.length() > 0)	{
//            SAXBiomlHandler saxFile;
//            saxFile.setFileName(strValue.c_str());
//            saxFile.parse();
//            a = 0;
//            while(a < saxFile.m_vseqBest.size())	{
//                if(m_mapSequences.find(saxFile.m_vseqBest[a].m_tUid) == m_mapSequences.end())	{
//                    m_vseqBest.push_back(saxFile.m_vseqBest[a]);
//                    m_mapSequences.insert(SEQMAP::value_type(saxFile.m_vseqBest[a].m_tUid,saxFile.m_vseqBest[a].m_strSeq));
//                }
//                a++;
//            }
//        }
//        return true;
    }


/*
 * This method forces the use of a particular file format, based on the value of the
 * input parameter "spectrum, path type" parameter. This can be useful if
 * for some reason the file type detection routines do not correctly identify your
 * file type. It may cause bad behavior if your file does not match the file type
 * specified by this parameter. 
 * Currently supported values for the "spectrum, path type parameter" are as follows:
 * "dta", "pkl", "mgf", "gaml", "mzdata", "mzxml"
 */
    public boolean spectra_force(String  _t,String  _v)
    {
        String strValue = _v;
        String strKey;
        Spectrum spCurrent;
        long lLoaded = 0;
        long lLimit = 2000;
        throw new UnsupportedOperationException("Fix This"); // ToDo
//        if(_t == "gaml")	{
//            boolean bState = m_specCondition.m_bCondition;
//            m_specCondition.use_condition(false);
//            loadgaml ldGaml(m_vSpectra, m_specCondition, *m_pScore);
//            if(ldGaml.open_force(strValue))	{
//                ldGaml.get();
//                m_tSpectraTotal = m_vSpectra.size();
//            }
//            m_specCondition.use_condition(bState);
//        }
//        else if(_t == "cmn")	{
//            loadcmn ldCmn;
//            if(ldCmn.open(strValue))	{
//                while(ldCmn.get(spCurrent))	{
//                    m_tSpectraTotal++;
//                    lLoaded++;
//                    if(lLoaded == lLimit)	{
//                        cout << ".";
//                        cout.flush();
//                        lLoaded = 0;
//                        m_prcLog.log(".\n");
//                    }
//                    m_vSpectra.push_back(spCurrent);
//                }
//                if(spCurrent.m_vMI.size() > 0)	{
//                    m_tSpectraTotal++;
//                    m_vSpectra.push_back(spCurrent);
//                }
//            }
//        }
//
///*
// * if the spectrum file did not contain GAML spectra, test the spectrum path parameter
// * for a Matrix Science format file
// */
//        else if(_t == "mgf")	{
//            loadmatrix ldMatrix;
//            if(ldMatrix.open_force(strValue))	{
//                while(ldMatrix.get(spCurrent))	{
//                    m_tSpectraTotal++;
//                    lLoaded++;
//                    if(lLoaded == lLimit)	{
//                        cout << ".";
//                        cout.flush();
//                        lLoaded = 0;
//                        m_prcLog.log(".");
//                    }
//                    if(m_specCondition.condition(spCurrent, *m_pScore))
//                        m_vSpectra.push_back(spCurrent);
//                }
//                if(spCurrent.m_vMI.size() > 0)	{
//                    m_tSpectraTotal++;
//                    if(m_specCondition.condition(spCurrent, *m_pScore))
//                        m_vSpectra.push_back(spCurrent);
//                }
//            }
//        }
///*
// * if no Matrix Science format data was found, test for PKL format information
// */
//        else if(_t == "pkl")	{
//            loadpkl ldPkl;
//            if(ldPkl.open_force(strValue))	{
//                while(ldPkl.get(spCurrent))	{
//                    m_tSpectraTotal++;
//                    lLoaded++;
//                    if(lLoaded == lLimit)	{
//                        cout << ".";
//                        cout.flush();
//                        lLoaded = 0;
//                        m_prcLog.log(".");
//                    }
//                    if(m_specCondition.condition(spCurrent, *m_pScore))
//                        m_vSpectra.push_back(spCurrent);
//                }
//                if(spCurrent.m_vMI.size() > 0)	{
//                    m_tSpectraTotal++;
//                    if(m_specCondition.condition(spCurrent, *m_pScore))
//                        m_vSpectra.push_back(spCurrent);
//                }
//            }
//        }
//    #ifdef XMLCLASS
//        else if(_t == "mzxml")	{
//            loadmzxml ldMzxml( m_vSpectra, m_specCondition,	*m_pScore);
//            if(ldMzxml.open_force(strValue))	{
//                //Ce .get est different, il	va chercher	tous les spectres du fichier.
//                ldMzxml.get();
//                m_tSpectraTotal	= m_vSpectra.size();
//            }
//
//        }
//        else if(_t == "mzml")	{
//            loadmzxml ldMzml( m_vSpectra, m_specCondition,	*m_pScore);
//            if(ldMzml.open_force(strValue))	{
//                //Ce .get est different, il	va chercher	tous les spectres du fichier.
//                ldMzml.get();
//                m_tSpectraTotal	= m_vSpectra.size();
//            }
//
//        }
//        else if(_t == "mzdata")	{
//            loadmzdata ldMzdata( m_vSpectra, m_specCondition, *m_pScore);
//            if(ldMzdata.open_force(strValue))	 {
//                //Ce .get est different, il	va chercher	tous les spectres du fichier.
//                ldMzdata.get();
//                m_tSpectraTotal	= m_vSpectra.size();
//            }
//        }
//    #endif
//        else if(_t == "dta")	{
//            loaddta ldSpec;
//            if(ldSpec.open_force(strValue))	{
//                while(ldSpec.get(spCurrent))	{
//                    m_tSpectraTotal++;
//                    lLoaded++;
//                    if(lLoaded == lLimit)	{
//                        cout << ".";
//                        cout.flush();
//                        m_prcLog.log(".");
//                        lLoaded = 0;
//                    }
//                    if(m_specCondition.condition(spCurrent, *m_pScore))
//                        m_vSpectra.push_back(spCurrent);
//                }
//                if(spCurrent.m_vMI.size() > 0)	{
//                    m_tSpectraTotal++;
//                    if(m_specCondition.condition(spCurrent, *m_pScore))
//                        m_vSpectra.push_back(spCurrent);
//                }
//            }
//        }
//        else	{
//                cout << "\n" << "The file type \"" << _t.c_str() << " is not supported.\n";
//                cout << "Supported values: pkl, dta, mgf, gaml, mzxml, mzdata\n";
//                cout.flush();
//                m_prcLog.log("error loading forced spectrum file 5");
//                return false;
//        }
//        strKey = "spectrum, use contrast angle";
//         strValue = m_xmlValues.get(strKey );
// ;
//        if("yes".equals(strValue))	{
//            // remove spectra that are redundant
//            subtract();
//        }
//        m_prcLog.log("spectra loaded");
//        return true;
    }
//
// This method is an experiment to decrease the number of redundant spectra that
// enter the calculation. It calculates the inner product (using ::dot) between
// two spectrum vectors and then calculates the cosine of the opening
// angle between the vectors. If the arccos is greater than the parameter
// "spectrum, contrast angle", then the spectrum is considered to be non-redundant and kept.
//
//	The inner product is calculated for m_ParentStream ion masses within 1000 ppm of each
//  other. If the arccos between two spectrum vectors is below the contrast angle parameter,
//  then the two spectra's summed intensities are compared. The spectrum with the lowest
//  summed intensity is placed in a list of rejected spectra and the process is repeated.
//  Once all of the spectra to be rejected have been marked, a new Spectrum List is created
//  and copied into m_vSpectra.
//
    public boolean subtract()
    {
        if(m_vSpectra.size() == 0)
            return false;
        throw new UnsupportedOperationException("Fix This"); // ToDo
//        cout << "+";
//        cout.flush();
//        String strValue;
//        String strKey = "spectrum, fragment mass error";
//         strValue = m_xmlValues.get(strKey );
// ;
//        if (strValue.empty()) {
//            // try old parameter name
//            strKey = "spectrum, fragment monoisotopic mass error";
//             strValue = m_xmlValues.get(strKey );
// ;
//        }
//        float fRes = (float)atof(strValue.c_str());
//        if(fRes <= 0.0)	{
//            fRes = 0.5;
//        }
//        boolean bType = true;
//        strKey = "spectrum, fragment mass error units";
//         strValue = m_xmlValues.get(strKey );
// ;
//        if (strValue.empty()) {
//            // try old parameter name
//            strKey = "spectrum, fragment monoisotopic mass error units";
//             strValue = m_xmlValues.get(strKey );
// ;
//        }
//        if(strValue != "Daltons")	{
//            bType = false;
//        }
//        // retrieve contrast angle
//        strKey = "spectrum, contrast angle";
//         strValue = m_xmlValues.get(strKey );
// ;
//        double dLimit = atof(strValue.c_str());
//        // apply limits to the value of the angle
//        if(dLimit < 0.0)	{
//            dLimit = 0.0;
//        }
//        if(dLimit > 90.0)	{
//            dLimit = 90.0;
//        }
//        dLimit = cos(dLimit * 3.1415/180.0);
//        final  int tSpectra = m_vSpectra.size();
//        int a = 0;
//        int b = 0;
//        double dValue = 0.0;
//        long lCount = 0;
//        double dDot1 = 0.0;
//        double dDot2 = 0.0;
//        List<double> vdDot1;
//        // calculate the length of all of the spectrum vectors
//        List<mi>::iterator itM = m_vSpectra[b].m_vMI.begin();
//        List<mi>::iterator itMEnd = m_vSpectra[b].m_vMI.end();
//        while(b < m_vSpectra.size())	{
//            dDot1 = 0.0;
//            itM = m_vSpectra[b].m_vMI.begin();
//            itMEnd = m_vSpectra[b].m_vMI.end();
//            while(itM != itMEnd)	{
//                dDot1 += itM.m_fI*itM.m_fI;
//                itM++;
//            }
//            vdDot1.push_back(sqrt(dDot1));
//            b++;
//        }
//        a  = 0;
//        long c = 0;
//        double dTotal = 0.;
//        long lMax = 0;
//        set<int> vDelete;
//        double dMax = 0.0;
//        int tLastMax;
//        double dLastSum;
//        int tLastA;
//        float fMH = 0.0;
//        final  double dFactor = 0.001;
//        double dDelta = 1.0;
//        List<Spectrum>::iterator itA = m_vSpectra.begin();
//        List<Spectrum>::iterator itB = itA;
//        // calculated the dot products between the spectrum List pairs
//        while(a < tSpectra)	{
//            // calculate only the pairs below the diagonal on the pairs matrix
//            // the values on the diagonal are all 1.0
//            // the values above the diagonal repeat the below diagonal elements, d(i,j) = d(j,i)
//            b = a+1;
//            itB = itA;
//            itB++;
//            lCount = 0;
//            dMax  = itA.m_vdStats[0];
//            tLastMax = itA.m_tId;
//            fMH = (float)itA.m_dMH;
//            dDelta = dFactor*fMH;
//            dLastSum = itA.m_vdStats[0];
//            tLastA = 0;
//            while(b < tSpectra)	{
//                // only calculate dValue if the m_ParentStream ion mass is appropriate
//                // and if the spectrum has not already been rejected
//                if(Math.abs(fMH - (float)itB.m_dMH) < dDelta    vDelete.find(itB.m_tId) == vDelete.end())	{
//                    dValue = dot(a,b,fRes,bType)/(vdDot1[a]*vdDot1[b]);
//                    if(dValue > dLimit)	{
//                        if(dMax < itB.m_vdStats[0])	{
//                            dMax = itB.m_vdStats[0];
//                            tLastA = b;
//                            vDelete.insert(tLastMax);
//                            tLastMax = itB.m_tId;
//                        }
//                        else	{
//                            vDelete.insert(itB.m_tId);
//                            dLastSum += itB.m_vdStats[0];
//                            tLastA = a;
//                        }
//                        lCount++;
//                    }
//                }
//                b++;
//                itB++;
//            }
//            if(tLastA != 0)	{
//                m_vSpectra[tLastA].m_vdStats[0] += dLastSum;
//            }
//            if(lCount > lMax)	{
//                lMax = lCount;
//            }
//            if(c > 1000)	{
//                if(m_lThread == 0 || m_lThread == 0xFFFFFFFF)	{
//                    cout << "+";
//                    cout.flush();
//                }
//                c = 0;
//            }
//            c++;
//            a++;
//            itA++;
//            while(a < tSpectra    vDelete.find(itA.m_tId) != vDelete.end())	{
//                a++;
//                itA++;
//                if(c > 1000)	{
//                    cout << "+";
//                    cout.flush();
//                    c = 0;
//                }
//                c++;
//            }
//        }
//        List<Spectrum>::iterator itStart = m_vSpectra.begin();
//        List <Spectrum> vTemp;
//        vTemp.reserve(m_vSpectra.size() - vDelete.size()+1);
//        m_tContrasted = 0;
//        // create a new list of spectra, vTemp and replace m_vSpectra with
//        // those spectra. This is much faster than deleting the spectra
//        // from the intact m_vSpectra list
//        while(itStart != m_vSpectra.end())	{
//            if(vDelete.find(itStart.m_tId) == vDelete.end())	{
//                vTemp.push_back(*itStart);
//            }
//            itStart++;
//        }
//        m_tContrasted = (double)(m_vSpectra.size() - vTemp.size());
//        m_vSpectra.clear();
//        m_vSpectra.reserve(vTemp.size() + 1);
//        m_vSpectra = vTemp;
//        return true;
    }

/*
 * taxonomy uses the taxonomy information in the input XML file to load
 * the  ProteinSequenceServer member object with file path names to the required
 * sequence list files (FASTA format only in the initial release). If these
 */
    public boolean taxonomy()
    {
        String strValue;
        String strKey = "list path, taxonomy information";
         strValue = m_xmlValues.get(strKey );
 ;
        String strTaxonPath = strValue;
        strKey = "protein, taxon";
         strValue = m_xmlValues.get(strKey );
 ;
        long lReturn = m_svrSequences.load_file(strTaxonPath,strValue);
/*
 * return false if the load_file method fails
 */
        if(lReturn == 1)	{
//            cout << "\nThe taxonomy parameter file \"" << strTaxonPath.c_str();
//            cout << "\" could not be found.\nCheck your settings and try again.\n";
            return false;
        }
        else if(lReturn == 2)	{
//            cout << "\nThe taxonomy parameter file \"" << strTaxonPath.c_str();
//            cout << "\" did not contain the value \"" << strValue.c_str() << "\".\nCheck your settings and try again.\n";
            return false;
        }
        else if(lReturn == 3)	{
//            cout << "\nThe taxonomy parameter file \"" << strTaxonPath.c_str();
//            cout << "\" contained incorrect entries\nfor the protein sequence files associated with the name: \"" << strValue.c_str() << "\".\nCheck the file names in the taxonomy file and try again.\n";
            return false;
        }
        return true;
    }
//
// new code, version 2007/04/01, Ron Beavis
// this section finds the file names associated with the taxonomy specification
// associated with Single Amino acid Polymorphisms (SAPs). The information
// is parsed using SAXSapHandler and stored in the m_mapSap object of mscore.
// This object will be used to control the state machine that generates the
// peptides associated with these polymorphisms
// When the mprocess pointer is null, the SAP object is loaded from the object
// in the original file. If the pointer is not null, it is assumed that the mprocess
// object contains the necessary SAP information, which is simply copied from memory.
//
   public  boolean load_saps(XTandemProcess _p)
    {
        String strValue;
        String strKey = "list path, taxonomy information";
         strValue = m_xmlValues.get(strKey );
 ;
        String strTaxonPath = strValue;
        strKey = "protein, taxon";
         strValue = m_xmlValues.get(strKey );
 ;
        throw new UnsupportedOperationException("Fix This"); // ToDo
//        XmlTaxonomy xmlTax;
//        String strType = "saps"; //this is the format attribute for the <file> objects in the tax file
//        if(!xmlTax.load(strTaxonPath,strValue,strType))
//            return true;
//        // bail out without error if there aren't any SAP files specified.
//        int a = 0;
//         Map<String,multimap<Integer,prSap> >::iterator itMap;
//         Map<String,multimap<Integer,prSap> >::iterator itValue;
//        pair<String,multimap<Integer,prSap> > pairMap;
//        multimap<Integer,prSap>::iterator itMulti;
//        // copy from _p if it exists
//        if(_p != null)	{
//            itMap = _p.m_pScore.m_Sap.m_mapSap.begin();
//            // load the mscore m_Sap object with the new values
//            while(itMap != _p.m_pScore.m_Sap.m_mapSap.end())	{
//                pairMap.first = itMap.first;
//                pairMap.second.clear();
//                itValue = m_pScore.m_Sap.m_mapSap.find(pairMap.first);
//                if(itValue == m_pScore.m_Sap.m_mapSap.end())	{
//                    m_pScore.m_Sap.m_mapSap.insert(pairMap);
//                    itValue = m_pScore.m_Sap.m_mapSap.find(pairMap.first);
//                }
//                itMulti = itMap.second.begin();
//                while(itMulti != itMap.second.end())	{
//                    itValue.second.insert(*itMulti);
//                    itMulti++;
//                }
//                itMap++;
//            }
//            return true;
//        }
//        m_vstrSaps.clear();
//        // obtain the lists from files, if no valid mprocess value was passed through _p
//        while(a < xmlTax.m_vstrPaths.size())	{
//            ifstream ifTest;
//            ifTest.open(xmlTax.m_vstrPaths[a].c_str());
//            if(!ifTest.fail())	{
//                m_vstrSaps.push_back(xmlTax.m_vstrPaths[a]);
//                ifTest.close();
//            }
//            ifTest.clear();
//            a++;
//        }
//        if(!m_vstrSaps.empty())	{
//            cout << " loaded.\nLoading SAPs ";
//            cout.flush();
//        }
//        a = 0;
//        while(a < m_vstrSaps.size())	{
//            SAXSapHandler sapXml;
//            sapXml.setFileName(xmlTax.m_vstrPaths[a].data());
//            sapXml.parse();
//            itMap = sapXml.m_mapSap.begin();
//            // load the mscore m_Sap object with the new values
//            while(itMap != sapXml.m_mapSap.end())	{
//                pairMap.first = itMap.first;
//                pairMap.second.clear();
//                itValue = m_pScore.m_Sap.m_mapSap.find(pairMap.first);
//                if(itValue == m_pScore.m_Sap.m_mapSap.end())	{
//                    m_pScore.m_Sap.m_mapSap.insert(pairMap);
//                    itValue = m_pScore.m_Sap.m_mapSap.find(pairMap.first);
//                }
//                itMulti = itMap.second.begin();
//                while(itMulti != itMap.second.end())	{
//                    itValue.second.insert(*itMulti);
//                    itMulti++;
//                }
//                itMap++;
//            }
//            cout << ".";
//            cout.flush();
//            a++;
//        }
//        return true;
    }

//
// new code, version 2007/04/01, Ron Beavis
// this section finds the file names associated with the taxonomy specification
// associated with Single Amino acid Polymorphisms (SAPs). The information
// is parsed using SAXSapHandler and stored in the m_mapSap object of mscore.
// This object will be used to control the state machine that generates the
// peptides associated with these polymorphisms
// When the mprocess pointer is null, the SAP object is loaded from the object
// in the original file. If the pointer is not null, it is assumed that the mprocess
// object contains the necessary SAP information, which is simply copied from memory.
//
    public boolean load_annotation(XTandemProcess _p)
    {
        String strValue;
        String strKey = "list path, taxonomy information";
         strValue = m_xmlValues.get(strKey );
 
        String strTaxonPath = strValue;
        strKey = "protein, taxon";
         strValue = m_xmlValues.get(strKey );

        throw new UnsupportedOperationException("Fix This"); // ToDo
//        XmlTaxonomy xmlTax;
//        String strType = "mods"; //this is the format attribute for the <file> objects in the tax file
//        if(!xmlTax.load(strTaxonPath,strValue,strType))
//            return true;
//        // bail out without error if there aren't any mods files specified.
//        int a = 0;
//         Map<String,String>::iterator itMods;
//        if(_p != null)	{
//            itMods = _p.m_mapAnnotation.begin();
//            // load the m_mapAnnotation object with the new values
//            while(itMods != _p.m_mapAnnotation.end())	{
//                m_mapAnnotation[itMods.first] = itMods.second;
//                itMods++;
//            }
//            return true;
//        }
//        m_vstrMods.clear();
//        // obtain the lists from files, if no valid mprocess value was passed through _p
//        while(a < xmlTax.m_vstrPaths.size())	{
//            ifstream ifTest;
//            ifTest.open(xmlTax.m_vstrPaths[a].c_str());
//            if(!ifTest.fail())	{
//                m_vstrMods.push_back(xmlTax.m_vstrPaths[a]);
//                ifTest.close();
//            }
//            ifTest.clear();
//            a++;
//        }
//        if(!m_vstrMods.empty())	{
//            cout << " loaded.\nLoading annotation ";
//            cout.flush();
//        }
//        a = 0;
//        while(a < m_vstrMods.size())	{
//            SAXModHandler modXml;
//            modXml.setFileName(xmlTax.m_vstrPaths[a].data());
//            modXml.parse();
//            itMods = modXml.m_mapMod.begin();
//            // load the mscore m_Sap object with the new values
//            while(itMods != modXml.m_mapMod.end())	{
//                m_mapAnnotation[itMods.first] = itMods.second;
//                itMods++;
//            }
//            cout << ".";
//            cout.flush();
//            a++;
//        }
//        return true;
    }
 }

