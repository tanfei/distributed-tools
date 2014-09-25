package org.systemsbiology.xtandem.testing.comparison;


import com.lordjoe.utilities.*;
import org.systemsbiology.xml.*;
import org.systemsbiology.xtandem.*;
import org.systemsbiology.xtandem.bioml.*;
import org.systemsbiology.xtandem.scoring.*;

import java.io.*;
import java.util.*;
import java.util.Map.*;

/**
 * org.systemsbiology.xtandem.testing.comparison.CompareX2JTandemResults
 * compares XTandem and JXTamdem files
 * User: Micheal miller
 * Date: Oct 2, 2011
 */
public class CompareX2JTandemResults {

    private static final int MAX_CHARGES = 20;
	public static final double FRACTION_ALLOWED_SCORE_ERROR = 0.03;
    public static final double FRACTION_ALLOWED_EXPECTED_ERROR = 2.0;

    /**
     * for the given tandem infile, score {@link ScoredScan}s according to the criteria
     * and direction properties.  write out the count property number of the ids to the outfile.
     * <p/>
     * if no outfile is given, create the name from the XTandem file name by appending '.compare'
     *
     * @param args an XTandem input file name, a JTandem input file name and an optional outfile name
     * @throws Exception
     */
    public static void main(String[] args) throws Exception {
 
        if (  1 > args.length  ) {
            XMLUtilities.outputLine("usage: CompareX2JTandemResults <xtandem infile> <jtandem infile> [<outfile path>]");
            return;
          }
        File xinfile = new File(args[0]);
        if (!xinfile.exists()) {
            XMLUtilities.outputLine(args[0] + " is not a valid file path");
            return;
           }

        File jinfile = null;
         if (args.length > 1)
             jinfile =  new File(args[1]);
         else
             jinfile =  FileUtilities.getLatestFileWithExtension("scans") ;


          if (!jinfile.exists()) {
              XMLUtilities.outputLine(jinfile.getAbsolutePath() + " is not a valid file path");
            return;
        }

        File outfile = null;
        if (2 < args.length && 0 < args[2].length()) {
            outfile = new File(args[2]);
        }
        else {
            outfile = new File(args[0] + ".compare");
        }
        XMLUtilities.outputLine("Output will be saved in " + outfile.getPath());

        XMLUtilities.outputLine("begin parsing xtandem");
        XTandemScoringReport xreport = XTandemUtilities.readXTandemFile(xinfile.getAbsolutePath());
        XMLUtilities.outputLine("\nbegin parsing jtandem");
        XTandemScoringReport jreport = XTandemUtilities.readScanScoring(jinfile.getAbsolutePath());

        ScoredScan[] xscans = xreport.getScans();
        ScoredScan[] jscans = jreport.getScans();
        int[] numberUnmatchedCharge = new int[MAX_CHARGES];
        Arrays.fill(numberUnmatchedCharge, 0);
        int[] binMatches = new int[BinMatchType.values().length];
        Arrays.fill(binMatches, 0);
        int[] topBinMatches = new int[BinMatchType.values().length];
        Arrays.fill(topBinMatches, 0);
        XMLUtilities.outputLine("match up peptides");
        Map<ClassificationType, Set<Record>> records = createRecords(xscans, jscans, numberUnmatchedCharge, binMatches, topBinMatches);

        XMLUtilities.outputLine();
        String unmatches = "";
        for (int i = 0; i < numberUnmatchedCharge.length; i++) {
		    if (0 == numberUnmatchedCharge[i]) {
				continue;
			}
		    unmatches += "[charge " + i + ": " + numberUnmatchedCharge[i] + "] ";
	    }

        String bins = "";
        for (int i = 0; i < binMatches.length; i++) {
		    if (0 == binMatches[i]) {
				continue;
			}
		    bins += "[" + BinMatchType.values()[i] + ": " + binMatches[i] + "] ";
	    }

        String topBins = "";
        for (int i = 0; i < topBinMatches.length; i++) {
		    if (0 == topBinMatches[i]) {
				continue;
			}
		    topBins += "[" + BinMatchType.values()[i] + ": " + topBinMatches[i] + "] ";
	    }
        writeRecords(outfile, records, xreport, jreport, unmatches, bins, topBins);

        XMLUtilities.outputLine();
        XMLUtilities.outputText("\nwithin " + (int)(FRACTION_ALLOWED_SCORE_ERROR * 100) + "%: " + records.get(ClassificationType.perfect_match).size() + "\ntotal top matches: " + (records.get(ClassificationType.perfect_match).size() +
                records.get(ClassificationType.perfect_match_different_expected).size()));
        XMLUtilities.outputText("\nmatching lower ranked: " + records.get(ClassificationType.partial_match).size() + "\nno match of peptides: " + records.get(ClassificationType.other_match).size());
        XMLUtilities.outputText("\nno matching scan: " + records.get(ClassificationType.no_match).size() + '\n');
        XMLUtilities.outputText("\nunmatched by charge: " + unmatches + '\n');
        XMLUtilities.outputText("Bin matches: " + bins + '\n');
        XMLUtilities.outputText("Top bin matches: " + topBins + '\n');
     }

    public static enum BinMatchType {
    	xsingularj {
            @Override
            public String toString() {
                return "SINGULAR MATCH  ";
            }
        },
    	xequalj {
            @Override
            public String toString() {
                return "MULTIBIN MATCH  ";
            }
        },
    	xsubsetj {
            @Override
            public String toString() {
                return "X SUBSET OF J   ";
            }
        },
    	jsubsetx {
            @Override
            public String toString() {
                return "J SUBSET OF X   ";
            }
        },
    	xjoverlap {
            @Override
            public String toString() {
                return "X AND J OVERLAP  ";
            }
        },
    	nooverlap {
            @Override
            public String toString() {
                return "NO OVERLAP       ";
            }
        }
    }

    public static enum ClassificationType {
           perfect_match {
                 @Override
                 public String toString() {
                     return "Matched top score end expected  ";
                 }
             },
           perfect_match_different_expected {
                 @Override
                 public String toString() {
                     return "Matched top score different expected  ";
                 }
             },
               partial_match {
                @Override
                public String toString() {
                    return "Matched lower score ";
                }
            },
            other_match {
                @Override
                public String toString() {
                    return "No matching peptide ";
                }
            },
            no_match {
                @Override
                public String toString() {
                    return "No matching scan    ";
                }
            };
        }
       
    /**
     * @param xscans
     * @param jscans
     * @param binMatches 
     * @param topBinMatches 
     * @return
     */
    private static Map<ClassificationType, Set<Record>> createRecords(ScoredScan[] xscans, ScoredScan[] jscans, int[] numberUnmatchedCharge, int[] binMatches, int[] topBinMatches) {
        // now do the match up from XTandem to JXTandem
        Map<ClassificationType, Set<Record>> ret = new HashMap<ClassificationType, Set<Record>>();
         for (ClassificationType c  : ClassificationType.values()) {
            ret.put(c, new TreeSet<Record>(new RecordComparator()));
        }

        Map<String, ScoredScan> jid2scan = buildScanToIdMap(jscans);
        for (int i = 0; i < xscans.length; i++) {
            ScoredScan xscan = xscans[i];
            String xid = xscan.getId();
            int xcharge = xscan.getCharge();
            ISpectralMatch[] xmatches = xscans[i].getSpectralMatches();
            double expectedValue = xscan.getExpectedValue();

            ScoredScan jscan = jid2scan.get(xscan.getId() + '.' + xscans[i].getCharge());
            if (null == jscan) {
                numberUnmatchedCharge[xcharge]++;
                Set<Record> records = ret.get(ClassificationType.no_match);
                for (int j = 0; j < xmatches.length; j++) {
                    records.add(new Record(ClassificationType.no_match, BinMatchType.nooverlap, xid, xcharge, xmatches[j].getPeptide().getSequence(),
                            xmatches[j].getHyperScore(), expectedValue,0));
                }
                binMatches[BinMatchType.nooverlap.ordinal()]++;
            }
            else {
                double jExpectedValue = jscan.getExpectedValue();
                ISpectralMatch[] jmatches = jscan.getSpectralMatches();
                 for (int j = 0; j < xmatches.length; j++) {
             		Map<Double, Set<ISpectralMatch>> xbins = binMatches(xmatches);
            		Map<Double, Set<ISpectralMatch>> jbins = binMatches(jmatches);
            		
            		// do bin match up by peptides for the top ranked xbin against each jbin
            		Map<BinMatchType, Entry<Double, Set<ISpectralMatch>>> jbinmatches = new HashMap<BinMatchType, Entry<Double, Set<ISpectralMatch>>>();
            		Entry<Double, Set<ISpectralMatch>> xentry = xbins.entrySet().iterator().next();
        			Set<ISpectralMatch> unmatched = new HashSet<ISpectralMatch>(xentry.getValue());
            		int curJMatch = 0;
            		boolean topJMatch = false;
        			for (Entry<Double, Set<ISpectralMatch>> jentry : jbins.entrySet()) {
        				Entry<BinMatchType, Entry<Double, Set<ISpectralMatch>>> retVal = compare(xentry, jentry, unmatched);
        				if (BinMatchType.nooverlap != retVal.getKey()) {
							jbinmatches.put(retVal.getKey(), retVal.getValue());
							if (0 == curJMatch) {
								topJMatch = true;
							}
						}
        				
        				if (0 == unmatched.size()) {
							break;
						}
        				curJMatch++;
					}
        			
        			/*
        			 * possible states:
        			 * curJMatch topJMatch  jbinmatches
        			 *    0         true             ==> perfect top match
        			 *   >0         true             ==> partial top match
        			 *   >0         false      >0    ==> some sort of lower ranking match
        			 *   >0         false       0    ==> no match
        			 */
        			if (0 == jbinmatches.size()) {
						if (0 == curJMatch || topJMatch) {
							throw new IllegalStateException("should not have found a match but have no bins for " + xscan.getId() + '.' + xscan.getCharge());
						}
                        addNoMatchRecord(ret, xscan, xid, xcharge, expectedValue, jExpectedValue, binMatches);
					}
        			else if (topJMatch) {
						addTopMatchRecord(ret, xscan, xid, xcharge, jscan, jExpectedValue, jbinmatches, curJMatch, binMatches, topBinMatches);
            		} 
            		else if (0 < jbinmatches.size()) {
                        addLowRankMatchRecord(ret, xscan, xid, xcharge, jscan, jbinmatches, curJMatch, binMatches);
					}
            		else {
            			throw new IllegalStateException("unexpected combination: " + topJMatch + "-" + curJMatch + "-" + jbinmatches.size());
            		}
                }
            }
        }
        return ret;
    }

	/**
	 * @param ret
	 * @param xscan
	 * @param xid
	 * @param xcharge
	 * @param expectedValue
	 * @param jExpectedValue
	 * @param binMatches
	 */
	private static void addNoMatchRecord(Map<ClassificationType, Set<Record>> ret, ScoredScan xscan, String xid, int xcharge, double expectedValue, double jExpectedValue, int[] binMatches) {
		Set<Record> records = ret.get(ClassificationType.other_match);
		String sequence = xscan.getBestMatch().getPeptide().getSequence();
		double xHyperscore = xscan.getBestMatch().getHyperScore();
		BinMatchType bin = BinMatchType.nooverlap;
		records.add(new Record(ClassificationType.other_match, bin, xid, xcharge, sequence, xHyperscore, expectedValue, jExpectedValue));
		binMatches[BinMatchType.nooverlap.ordinal()]++;
	}

	/**
	 * @param ret
	 * @param xscan
	 * @param xid
	 * @param xcharge
	 * @param jscan
	 * @param jExpectedValue
	 * @param jbinmatches
	 * @param curJMatch
	 * @param binMatches
	 * @param topBinMatches
	 */
	private static void addTopMatchRecord(Map<ClassificationType, Set<Record>> ret, ScoredScan xscan, String xid, int xcharge, ScoredScan jscan, double jExpectedValue,
		Map<BinMatchType, Entry<Double, Set<ISpectralMatch>>> jbinmatches, int curJMatch, int[] binMatches, int[] topBinMatches) {
		if (1 != jbinmatches.size()) {
			throw new IllegalStateException("for perfect match expected only one bin for " + xscan.getId() + '.' + xscan.getCharge());
		}

		Entry<BinMatchType, Entry<Double, Set<ISpectralMatch>>> entry = jbinmatches.entrySet().iterator().next();
		if (0 == curJMatch) {
			if (BinMatchType.xsingularj != entry.getKey() && BinMatchType.xequalj != entry.getKey() && BinMatchType.xsubsetj != entry.getKey()) {
				throw new IllegalStateException("unexpected type for perfect match for " + xscan.getId() + '.' + xscan.getCharge());
			}
		}
		else { // 0 < curJMatch
			if (BinMatchType.jsubsetx != entry.getKey() && BinMatchType.xjoverlap != entry.getKey() && BinMatchType.xsubsetj != entry.getKey()) {
				throw new IllegalStateException("unexpected type for perfect partial match for " + xscan.getId() + '.' + xscan.getCharge());
			}
		}

		Entry<Double, Set<ISpectralMatch>> innerEntry = entry.getValue();
		ClassificationType type = ClassificationType.perfect_match;
		double xHyperscore = xscan.getBestMatch().getHyperScore();
		double jHyperscore = jscan.getBestMatch().getHyperScore();
		double relativeHyperScoreError = Math.abs((xHyperscore - jHyperscore) / xHyperscore);
		if (FRACTION_ALLOWED_SCORE_ERROR >= relativeHyperScoreError) {
			Set<Record> records = ret.get(ClassificationType.perfect_match);
			Record rec = new Record(type, entry.getKey(), xid, xcharge, getPeptides(innerEntry.getValue()), xHyperscore, jHyperscore, xscan.getExpectedValue(), jExpectedValue);
			records.add(rec);
		} else {
			Set<Record> records = ret.get(ClassificationType.perfect_match_different_expected);
			Record rec = new Record(type, entry.getKey(), xid, xcharge, getPeptides(innerEntry.getValue()), xHyperscore, jHyperscore, xscan.getExpectedValue(), jExpectedValue);
			//XMLUtilities.outputLine(rec);
			records.add(rec);
		}
		binMatches[entry.getKey().ordinal()]++;
		topBinMatches[entry.getKey().ordinal()]++;
	}

	/**
	 * @param ret
	 * @param xscan
	 * @param xid
	 * @param xcharge
	 * @param jscan
	 * @param jbinmatches
	 * @param curJMatch
	 * @param binMatches
	 */
	private static void addLowRankMatchRecord(Map<ClassificationType, Set<Record>> ret, ScoredScan xscan, String xid, int xcharge, ScoredScan jscan,
		Map<BinMatchType, Entry<Double, Set<ISpectralMatch>>> jbinmatches, int curJMatch, int[] binMatches) {
		ClassificationType type = ClassificationType.partial_match;
		// usually only one but otherwise pick one arbitrarily
		Entry<BinMatchType, Entry<Double, Set<ISpectralMatch>>> entry = jbinmatches.entrySet().iterator().next();
		Entry<Double, Set<ISpectralMatch>> innerEntry = entry.getValue();

		double xHyperscore = xscan.getBestMatch().getHyperScore();
		double jHyperscore = innerEntry.getKey();
		ISpectralMatch jBestMatch = jscan.getBestMatch();
		double jBestHyperscore = jBestMatch.getHyperScore();
		String jBestSequence = jBestMatch.getPeptide().getSequence();
		Set<Record> records = ret.get(type);
		Record rec = new Record(type, entry.getKey(), xid, xcharge, getPeptides(innerEntry.getValue()), xHyperscore, jBestSequence, jBestHyperscore, jHyperscore, curJMatch, xscan.getExpectedValue(), -1);
		//XMLUtilities.outputLine(rec);
		records.add(rec);
		binMatches[entry.getKey().ordinal()]++;
	}

    protected static Map<String, ScoredScan> buildScanToIdMap(final ScoredScan[] jscans) {
        Map<String, ScoredScan> jid2scan = new HashMap<String, ScoredScan>();
        for (int i = 0; i < jscans.length; i++) {
            jid2scan.put(jscans[i].getId() + '.' + jscans[i].getCharge(), jscans[i]);
        }
        return jid2scan;
    }

	/**
	 * @param matches
	 * @param bins
	 */
	private static Map<Double, Set<ISpectralMatch>> binMatches(ISpectralMatch[] matches) {
		Map<Double, Set<ISpectralMatch>> bins = new TreeMap<Double, Set<ISpectralMatch>>(new Comparator<Double>(){
			/**
			 * sort descending
			 * @param o1
			 * @param o2
			 * @return -o1.compareTo(o2)
			 */
			@Override
			public int compare(Double o1, Double o2) {
				return -o1.compareTo(o2);
			}
		});
		
		for (int k = 0; k < matches.length; k++) {
			Set<Double> keys = bins.keySet();
			boolean match = false;
			for (Double key : keys) {
				if (XTandemUtilities.equivalentDouble(key, matches[k].getHyperScore())) {
					bins.get(key).add(matches[k]);
					match = true;
					break;
				}
			}
			if (!match) {
				bins.put(matches[k].getHyperScore(), new HashSet<ISpectralMatch>(Arrays.asList(new ISpectralMatch[] {matches[k]})));
			}
		}
		return bins;
	}
 
	/**
	 * @param xentry
	 * @param jentry
	 * @param unmatched 
	 * @return
	 */
	private static Entry<BinMatchType, Entry<Double, Set<ISpectralMatch>>> compare(final Entry<Double, Set<ISpectralMatch>> xentry, final Entry<Double, Set<ISpectralMatch>> jentry, Set<ISpectralMatch> unmatched) {
		Set<ISpectralMatch> value = new HashSet<ISpectralMatch>();

		class ReturnValue implements Entry<BinMatchType, Map.Entry<Double,Set<ISpectralMatch>>> {
			BinMatchType key = BinMatchType.nooverlap;
			Entry<Double, Set<ISpectralMatch>> value = null;
			
			@Override
			public Entry<Double, Set<ISpectralMatch>> setValue(Entry<Double, Set<ISpectralMatch>> value) {
				this.value = value;
				return value;
			}
			
			@Override
			public Entry<Double, Set<ISpectralMatch>> getValue() {
				return value;
			}
			
			@Override
			public BinMatchType getKey() {
				return key;
			}
		};
		ReturnValue retVal = new ReturnValue();

		Entry<Double, Set<ISpectralMatch>> topValue = new Entry<Double, Set<ISpectralMatch>>() {
			Double key = jentry.getKey();
			Set<ISpectralMatch> value = new HashSet<ISpectralMatch>();
			
			@Override
			public Set<ISpectralMatch> setValue(Set<ISpectralMatch> value) {
				this.value = value;
				return value;
			}
			
			@Override
			public Set<ISpectralMatch> getValue() {
				return value;
			}
			
			@Override
			public Double getKey() {
				return key;
			}
		};
		topValue.setValue(value);
		
		for (ISpectralMatch jmatch : jentry.getValue()) {
			for (ISpectralMatch xmatch : xentry.getValue()) {
				if (jmatch.getPeptide().getSequence().equals(xmatch.getPeptide().getSequence())) {
					value.add(jmatch);
					if (!unmatched.remove(xmatch)) {
						// not expecting to see same peptide matched twice
						throw new IllegalStateException("saw " + xmatch.getPeptide().getSequence() + " twice");
					}
					// for multiple matches, this gets set more than once but it's the same object so no harm
					retVal.setValue(topValue);
				}
			}
		}
		
		int matchCount = value.size();
		if (0 != matchCount) {
			int jCount = jentry.getValue().size();
			int xCount = xentry.getValue().size();
			
			if (matchCount == jCount) {
				if (matchCount == xCount) {
					if (1 == matchCount) {
						retVal.key = BinMatchType.xsingularj;
					}
					else {
						retVal.key = BinMatchType.xequalj;
					}
				}
				else if (matchCount > xCount) {
					retVal.key = BinMatchType.xsubsetj;
				}
				else { // (matchCount < xCount) 
					retVal.key = BinMatchType.jsubsetx;
				}
			}
			else if (matchCount == xCount) {
				if (matchCount > jCount) {
					retVal.key = BinMatchType.jsubsetx;
				}
				else { // (matchCount < jCount) 
					retVal.key = BinMatchType.xsubsetj;
				}
			}
			else if (matchCount < jCount && matchCount < xCount) {
				retVal.key = BinMatchType.xjoverlap;
			}
			else {
				throw new IllegalStateException("unexpected state: " + matchCount + "-" + jCount + "-" + xCount);
			}
		}

		return retVal;
	}

	private static String getPeptides(Set<ISpectralMatch> value) {
		String peptides = 1 < value.size()? "[" : "";
		Iterator<ISpectralMatch> iter = value.iterator();
		peptides += iter.next().getPeptide().getSequence();
		while (iter.hasNext()) {
			peptides += ',' + iter.next().getPeptide().getSequence();
		}
		peptides += 1 < value.size()? "]" : "";
		return peptides;
	}

    private static void writeRecords(File outfile, Map<ClassificationType, Set<Record>> records, XTandemScoringReport xreport, XTandemScoringReport jreport, String unmatches, String bins, String topBins)
            throws IOException {
        OutputStreamWriter out = new FileWriter(outfile);
        BufferedWriter writer = new BufferedWriter(out);
//        String[] type = {"MATCHES TOP (HYPERSCORES WITHIN 20%)\n", "MATCHES TOP (HYPERSCORES MORE THAN 20%)\n", "MATCHES LOWER RANKED\n",
//                "NO MATCHING XJTANDEM PEPTIDE\n", "NO MATCHING XJTANDEM SCAN\n"};
        //String[] headers = {Record.exactHeader(), Record.exactHeader(), Record.lowerHeader(), Record.nomatchHeader(), Record.nomatchHeader()};
        @SuppressWarnings("unchecked")
		Set<Record>[] asArray = new Set[ClassificationType.values().length];
        int index = 0;
        for (ClassificationType c : ClassificationType.values()) {
            XMLUtilities.outputLine("\n" + c.toString());
            writer.write(c.toString());
            Set<Record> recordSet = records.get(c);
            asArray[index++] = recordSet;
            printSet(recordSet, c.toString(), writer);
            writer.write("\n\n");
        }

        writer.write("xtandem scan count: " + xreport.getScans().length + "\njtandem scan count: " + jreport.getScans().length);
        writer.write("\nwithin " + (int)(FRACTION_ALLOWED_SCORE_ERROR * 100) +  "%: " + asArray[0].size() + "\ntotal top matches: " + (asArray[0].size() + asArray[1].size()));
        writer.write("\nmatching lower ranked: " + asArray[2].size() + "\nno match of peptides: " + asArray[3].size());
        writer.write("\nno matching scan: " + asArray[4].size() + '\n');
        writer.write("Unmatched by charge: " + unmatches + '\n');
        writer.write("Bin matches: " + bins + '\n');
        writer.write("Top bin matches: " + topBins + '\n');
        writer.flush();
        writer.close();
        out.close();
    }

    private static int printSet(Set<Record> recordSet, String header, BufferedWriter writer) throws IOException {
        int count = 0;
        for (Record record : recordSet) {
            if (0 == count % 30) {
                writer.write('\n' + header + '\n');
            }
            if (0 == count++ % 128) {
                XMLUtilities.outputText(".");
            }
            writer.write(record.toString());
            writer.write('\n');
        }
        return count;
    }

    private static class Record {
        private final ClassificationType type;
        private final BinMatchType binType;
        private final int id;
        private int charge;
        private final String peptide;
        private double xHyperscore = -1;
        private String topPeptide = null;
        private double topHyperscore ;
        private double matchHyperscore ;
        private int matchRank = -1;
        private final double xExpected ;
       // private final double jExpected ;

        /**
         * @param type
         * @param bin 
         * @param id
         * @param xHyperscore
         * @param jHyperscore
         * @param xExpected
         */
        public Record(ClassificationType type, BinMatchType bin, String id, int charge, String peptide, double xHyperscore, double jHyperscore, double xExpected, double jExpected) {
            this.type = type;
            this.binType = bin;
            this.id = Integer.valueOf(id);
            this.charge = charge;
            this.peptide = peptide;
            this.xHyperscore = xHyperscore;
            this.matchHyperscore = jHyperscore;
            this.xExpected = xExpected;
            //this.jExpected = jExpected;
            }

        /**
         * @param type
         * @param bin 
         * @param id
         * @param charge
         * @param peptide
         * @param xHyperscore
         * @param topHyperscore
         * @param matchHyperscore
         * @param matchRank
         * @param xExpected
         */
        public Record(ClassificationType type, BinMatchType bin, String id, int charge, String peptide, double xHyperscore, String topPeptide, double topHyperscore,
                      double matchHyperscore, int matchRank, double xExpected, double jExpected) {
            this.type = type;
            this.binType = bin;
            this.id = Integer.valueOf(id);
            this.charge = charge;
            this.peptide = peptide;
            this.xHyperscore = xHyperscore;
            this.topPeptide = topPeptide;
            this.topHyperscore = topHyperscore;
            this.matchHyperscore = matchHyperscore;
            this.matchRank = matchRank;
            this.xExpected = xExpected;
            //this.jExpected = xExpected;
        }

        /**
         * @param type
         * @param bin 
         * @param id
         * @param charge
         * @param peptide
         * @param xHyperscore
         * @param xExpected
         */
        public Record(ClassificationType type, BinMatchType bin, String id, int charge, String peptide, double xHyperscore, double xExpected, double jExpected) {
            this.type = type;
            this.binType = bin;
            this.id = Integer.valueOf(id);
            this.charge = charge;
            this.peptide = peptide;
            this.xHyperscore = xHyperscore;
            this.xExpected = xExpected;
            //this.jExpected = xExpected;
          }
/*
        public static String lowerHeader() {
            return "type                id    peptide xHyperscore topPeptide topHyperscore matchHyperscore matchRank xExpected";
        }

        public static String exactHeader() {
            return "type                id    peptide xHyperscore matchHyperScore xExpected";
        }

        public static String nomatchHeader() {
            return "type                id    peptide xHyperscore xExpected";
        }
*/
        @Override
        public String toString() {
            if (ClassificationType.partial_match == type   ) {
                 return type + String.valueOf(id) + "." + String.valueOf(charge) + " " + binType + " " + peptide + " " + xHyperscore + " " +
                         topPeptide + " " + topHyperscore + " " + matchHyperscore + " " + matchRank + " " + xExpected;
             }
            if (  ClassificationType.perfect_match_different_expected == type ) {
                 return type + String.valueOf(id) + "." + String.valueOf(charge) + " " + binType + " " + peptide + " " + xHyperscore + " " +
                         topPeptide + " " + topHyperscore + " " + matchHyperscore + " " + matchRank + " " + xExpected;
             }
              else if (ClassificationType.perfect_match == type) {
                return type + String.valueOf(id) + "." + String.valueOf(charge) + " " + binType + " " + peptide + " " +
                        xHyperscore + " " + matchHyperscore + " " + xExpected;
            }
            else {
                return type + String.valueOf(id) + "." + String.valueOf(charge) + " " + binType + " " + peptide + " " + xHyperscore + " " + xExpected;
            }
        }
    }

    private static class RecordComparator implements Comparator<Record> {
        @Override
        public int compare(Record o1, Record o2) {
            if (o1.id == o2.id) {
                if (o1.charge == o2.charge && -1 != o1.xHyperscore) {
                    if (null == o1.peptide && null == o2.peptide) {
                        // don't really care since there isn't anything significant
                        return 0;
                    }
                    if (null == o1.peptide) {
                        return -1;
                    }
                    if (null == o2.peptide) {
                        return 1;
                    }
                    if (0 == o1.peptide.compareTo(o2.peptide)) {
						// this actually occurs
						//throw new IllegalStateException("did not expect to compare equal: " + o1.id + "." + o1.charge + " " + o1.peptide);
                        if (o1.matchHyperscore != o2.matchHyperscore) {
							throw new IllegalStateException("did not expect to compare equal: " + o1.id + "." + o1.charge + " " + o1.peptide);
						}
						return 0;
                    }
                    return o1.peptide.compareTo(o2.peptide);
                }
                return o1.charge - o2.charge;
            }
            return o1.id - o2.id;
        }
    }
}

