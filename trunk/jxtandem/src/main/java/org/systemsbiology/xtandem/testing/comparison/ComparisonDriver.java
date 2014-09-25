package org.systemsbiology.xtandem.testing.comparison;

import org.systemsbiology.xml.*;
import org.systemsbiology.xtandem.*;

/**
 * hardcoded comparison test for xtandem vs jxtandem output
 * @author michael
 */
public class ComparisonDriver {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		try {
			CompareX2JTandemResults.main(new String[] {"C:\\tpp\\testing\\2011_09_27_comparison\\Sample2\\yeast_orfs_all_REV01.2011_09_26_15_38_29.t.xml",
					"C:\\tpp\\testing\\2011_09_27_comparison\\Sample2\\yeast_orfs_all_REV01.2011_09_269_14_18_22.t.xml.scans"});
		} catch (Exception e) {
			e.printStackTrace();
		}
		XMLUtilities.outputLine("=============================================");
		try {
			CompareX2JTandemResults.main(new String[] {"C:\\tpp\\testing\\2011_09_27_comparison\\TestSetForSteveLewis1\\yeast_orfs_all_REV01.2011_09_26_15_38_29.t.xml",
					"C:\\tpp\\testing\\2011_09_27_comparison\\TestSetForSteveLewis1\\yeast_orfs_all_REV01.2011_09_269_14_18_22.t.xml.scans"});
		} catch (Exception e) {
			e.printStackTrace();
		}
		XMLUtilities.outputLine("=============================================");
		try {
			CompareX2JTandemResults.main(new String[] {"C:\\tpp\\testing\\2011_09_27_comparison\\VentnerEcoli\\ecolisilac_short.2011_09_26_14_32_32.t.xml",
					"C:\\tpp\\testing\\2011_09_27_comparison\\VentnerEcoli\\ecolisilac_short.2011_09_269_10_53_39.t.scans.xml"});
		} catch (Exception e) {
			e.printStackTrace();
		}
		XMLUtilities.outputLine("=============================================");
		try {
			CompareX2JTandemResults.main(new String[] {"C:\\tpp\\testing\\2011_09_27_comparison\\VentnerEcoli\\ecolisilac_short2.xml",
					"C:\\tpp\\testing\\2011_09_27_comparison\\VentnerEcoli\\ecolisilac_short.2011_09_269_10_53_39.t.scans.xml"});
		} catch (Exception e) {
			e.printStackTrace();
		}
		XMLUtilities.outputLine("=============================================");
		try {
			CompareX2JTandemResults.main(new String[] {"C:\\tpp\\testing\\2011_09_27_comparison\\VentnerEcoli\\ecolisilac_short3.xml",
					"C:\\tpp\\testing\\2011_09_27_comparison\\VentnerEcoli\\ecolisilac_short.2011_09_269_10_53_39.t.scans.xml"});
		} catch (Exception e) {
			e.printStackTrace();
		}
		XMLUtilities.outputLine("=============================================");
		try {
			CompareX2JTandemResults.main(new String[] {"C:\\tpp\\testing\\2011_09_27_comparison\\VentnerEcoli\\ecolisilac_short4.xml",
					"C:\\tpp\\testing\\2011_09_27_comparison\\VentnerEcoli\\ecolisilac_short.2011_09_269_10_53_39.t.scans.xml"});
		} catch (Exception e4) {
			e4.printStackTrace();
		}
		XMLUtilities.outputLine("=============================================");
		try {
			CompareX2JTandemResults.main(new String[] {"C:\\tpp\\testing\\2011_09_27_comparison\\VentnerEcoli\\ecolisilac_short5.xml",
					"C:\\tpp\\testing\\2011_09_27_comparison\\VentnerEcoli\\ecolisilac_short.2011_09_269_10_53_39.t.scans.xml"});
		} catch (Exception e3) {
			e3.printStackTrace();
		}
		XMLUtilities.outputLine("=============================================");
		try {
			CompareX2JTandemResults.main(new String[] {"C:\\tpp\\testing\\2011_09_27_comparison\\VentnerEcoli\\ecolisilac_short6.xml",
					"C:\\tpp\\testing\\2011_09_27_comparison\\VentnerEcoli\\ecolisilac_short.2011_09_269_10_53_39.t.scans.xml"});
		} catch (Exception e2) {
			e2.printStackTrace();
		}
		XMLUtilities.outputLine("=============================================");
		try {
			CompareX2JTandemResults.main(new String[] {"C:\\tpp\\testing\\2011_09_27_comparison\\VentnerEcoli\\ecolisilac_short7.xml",
					"C:\\tpp\\testing\\2011_09_27_comparison\\VentnerEcoli\\ecolisilac_short.2011_09_269_10_53_39.t.scans.xml"});
		} catch (Exception e1) {
			e1.printStackTrace();
		}
		XMLUtilities.outputLine("=============================================");
		try {
			CompareX2JTandemResults.main(new String[] {"C:\\tpp\\testing\\2011_09_27_comparison\\VentnerEcoli\\ecolisilac_short8.xml",
					"C:\\tpp\\testing\\2011_09_27_comparison\\VentnerEcoli\\ecolisilac_short.2011_09_269_10_53_39.t.scans.xml"});
		} catch (Exception e) {
			e.printStackTrace();
		}
		XMLUtilities.outputLine("=============================================");
	}
}
