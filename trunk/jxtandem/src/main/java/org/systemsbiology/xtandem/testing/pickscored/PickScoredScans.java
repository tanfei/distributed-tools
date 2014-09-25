/**
 * 
 */
package org.systemsbiology.xtandem.testing.pickscored;

import org.systemsbiology.xml.*;
import org.systemsbiology.xtandem.*;
import org.systemsbiology.xtandem.bioml.*;
import org.systemsbiology.xtandem.scoring.*;

import java.io.*;
import java.util.*;

/**
 * class to use a given criteria to pick n {@link ScoredScan}s based on the
 * class properties
 * @author michael
 */
public class PickScoredScans {
	/**
     * for the given tandem infile, score {@link ScoredScan}s according to the criteria
     * and direction properties.  write out the count property number of the ids to the outfile.
     * <p>
     * if no outfile is given, create the name from the infile name by appending '.score'
     * @param args an infile name and an optional outfile name
	 * @throws Exception 
     */
    public static void main(String[] args) throws Exception {
    	if (null == args || 0 == args.length || null == args[0] || "" == args[0]) {
            XMLUtilities.outputLine("usage: PickTopSpectrum <infile> [<outfile>]");
			System.exit(1);
		}
    	File infile = new File(args[0]);
    	if (!infile.exists()) {
            XMLUtilities.outputLine(args[0] + " is not a valid file path");
			System.exit(2);
		}
    	
    	File outfile = null;
    	if (1 < args.length && 0 < args[1].length()) {
			outfile = new File(args[1]);
		}
    	else {
			outfile = new File(args[0] + ".score"); 
		}
        XMLUtilities.outputLine("Output will be saved in " + outfile.getPath());
    	
    	Properties properties = new Properties();
    	properties.load(PickScoredScans.class.getResourceAsStream ("scoring.properties"));
    	int count = Integer.parseInt(properties.getProperty("picktopscoredscans.count", "10"));
    	String criteriaClassName = properties.getProperty("picktopscoredscans.criteria", "org.systemsbiology.xtandem.testing.pickscored.HyperscoreCriteria");
    	boolean descending = "descending".equalsIgnoreCase(properties.getProperty("picktopscoredscans.direction", "descending"));

    	@SuppressWarnings("unchecked")
		Class<TopscoredCriteria> clazz = (Class<TopscoredCriteria>) Class.forName(criteriaClassName);
    	TopscoredCriteria criteria = clazz.newInstance();
    	
    	ScoredScan[] scoredScans = new ScoredScan[count];
    	XTandemScoringReport report = XTandemUtilities.readXTandemFile(infile.getAbsolutePath());
        ScoredScan[] scans = report.getScans();
        for (int i = 0; i < scans.length; i++) {
            ScoredScan scan = scans[i];
            int curPlace = count;
            for (int j = count-1; j > -1; j--) {
				if (null == scoredScans[j]) {
					curPlace = j;
				} else {
					int compare = criteria.compare(scan, scoredScans[j]);
					if (0 == compare) {
						curPlace = j+1;
						break;
					} else if ((descending && 0 > compare) || (!descending && 0 < compare)) {
						curPlace = j;
					}
					else {
						break;
					}
				}
			}
            if (count > curPlace) {
				for (int j = scoredScans.length-2; j >= curPlace ; j--) {
					scoredScans[j+1] = scoredScans[j];
				}
				scoredScans[curPlace] = scan;
			}
        }
        
        for (int i = 0; i < scans.length; i++) {
			if (null != scans[i]) {
                XMLUtilities.outputText(scans[i].getBestMatch().getHyperScore() + " ");
			}
		}
        XMLUtilities.outputLine("\n");
        
        for (int i = 0; i < scoredScans.length; i++) {
			if (null != scoredScans[i]) {
                XMLUtilities.outputText(scoredScans[i].getBestMatch().getHyperScore() + " ");
			}
		}
        XMLUtilities.outputLine("\n");

        if (null != scoredScans[0]) {
            // write out the ids to the output file
            FileWriter writer = new FileWriter(outfile);
			writer.write(scoredScans[0].getId());
			for (int i = 1; i < scoredScans.length; i++) {
				if (null == scoredScans[i]) {
					break;
				}
				writer.write(' ' + scoredScans[i].getId());
			}
			writer.write('\n');
			writer.flush();
			writer.close();
		}
    }
}
