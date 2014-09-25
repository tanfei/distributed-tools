package org.systemsbiology.xtandem.morpheus;

import org.junit.*;
import org.systemsbiology.xtandem.*;
import org.systemsbiology.xtandem.ionization.*;
import org.systemsbiology.xtandem.peptide.*;
import org.systemsbiology.xtandem.scoring.*;

import java.util.*;

/**
 * org.systemsbiology.xtandem.morpheus.MorpheusScoringTest
 * User: steven
 * Date: 2/5/13
 */
public class MorpheusScoringTest {
    public static final MorpheusScoringTest[] EMPTY_ARRAY = {};

    public static SpectrumPeak[] THEORETICAL_PEAKS = {
            new SpectrumPeak(97.05276386, 1.0),
            new SpectrumPeak(146.1055276942, 1.0),
            new SpectrumPeak(196.12117778, 1.0),
            new SpectrumPeak(233.1375561242, 1.0),
            new SpectrumPeak(293.17394164, 1.0),
            new SpectrumPeak(346.2216201242, 1.0),
            new SpectrumPeak(433.2536485542, 1.0),
            new SpectrumPeak(456.23727024, 1.0),
            new SpectrumPeak(596.3169771542, 1.0),
            new SpectrumPeak(619.30059884, 1.0),
            new SpectrumPeak(693.3697410142, 1.0),
            new SpectrumPeak(747.39556184, 1.0),
            new SpectrumPeak(824.4102256142, 1.0),
            new SpectrumPeak(861.43848934, 1.0),
            new SpectrumPeak(881.4316893442, 1.0),
            new SpectrumPeak(975.48141684, 1.0),
            new SpectrumPeak(995.4746168442, 1.0),
            new SpectrumPeak(1032.50288057, 1.0),
            new SpectrumPeak(1109.5175443442, 1.0),
            new SpectrumPeak(1163.54336517, 1.0),
            new SpectrumPeak(1237.6125073442, 1.0),
            new SpectrumPeak(1260.59612903, 1.0),
            new SpectrumPeak(1400.6758359442, 1.0),
            new SpectrumPeak(1423.65945763, 1.0),
            new SpectrumPeak(1510.69148606, 1.0),
            new SpectrumPeak(1563.7391645442, 1.0),
            new SpectrumPeak(1623.77555006, 1.0),
            new SpectrumPeak(1660.7919284042, 1.0),
            new SpectrumPeak(1710.80757849, 1.0),
            new SpectrumPeak(1759.8603423242, 1.0),
    };

    public static SpectrumPeak[] EXPERIMENTAL_PEAKS = {
            new SpectrumPeak(99.5012609124805, 1566.62829589844),
            new SpectrumPeak(100.356317675176, 1545.49462890625),
            new SpectrumPeak(108.919817125859, 1613.63806152344),
            new SpectrumPeak(109.063799059453, 14719.54296875),
            new SpectrumPeak(112.156961596074, 1652.955078125),
            new SpectrumPeak(114.099764025273, 1578.29113769531),
            new SpectrumPeak(114.777246630254, 2089.97314453125),
            new SpectrumPeak(119.07337394959, 7522.970703125),
            new SpectrumPeak(128.094865953984, 20623.35546875),
            new SpectrumPeak(135.067873156133, 3579.79809570313),
            new SpectrumPeak(144.373171007695, 1751.52795410156),
            new SpectrumPeak(146.105409777227, 28567.59375),
            new SpectrumPeak(166.085802233281, 4412.88623046875),
            new SpectrumPeak(170.071031725469, 1498.38549804688),
            new SpectrumPeak(170.080507433477, 1362.29626464844),
            new SpectrumPeak(172.669862902227, 1644.31518554688),
            new SpectrumPeak(174.111177599492, 3228.322265625),
            new SpectrumPeak(181.331026232305, 1493.24096679688),
            new SpectrumPeak(184.157365953984, 16809.125),
            new SpectrumPeak(185.115190661016, 2099.18676757813),
            new SpectrumPeak(194.079347765508, 3771.4794921875),
            new SpectrumPeak(198.173036730352, 60150.46875),
            new SpectrumPeak(199.083330309453, 1935.80505371094),
            new SpectrumPeak(212.15271202332, 7768.95166015625),
            new SpectrumPeak(214.131456530156, 15671.6640625),
            new SpectrumPeak(215.090044176641, 2173.01342773438),
            new SpectrumPeak(218.140871203008, 4589.4287109375),
            new SpectrumPeak(226.167711412969, 20897.072265625),
            new SpectrumPeak(238.107134020391, 1619.55480957031),
            new SpectrumPeak(242.126466906133, 11501.1669921875),
            new SpectrumPeak(243.085741198125, 3582.52563476563),
            new SpectrumPeak(252.087404406133, 2734.19409179688),
            new SpectrumPeak(259.189775621953, 18245.037109375),
            new SpectrumPeak(260.147386705937, 2756.4052734375),
            new SpectrumPeak(265.593919909062, 1518.99450683594),
            new SpectrumPeak(267.634843981328, 1871.85913085938),
            new SpectrumPeak(307.164476549687, 1871.28845214844),
            new SpectrumPeak(313.198381578984, 2390.2041015625),
            new SpectrumPeak(318.445207750859, 1450.80737304688),
            new SpectrumPeak(351.195757067266, 2391.26635742188),
            new SpectrumPeak(355.211290514531, 8422.392578125),
            new SpectrumPeak(358.257433092656, 11554.0859375),
            new SpectrumPeak(390.192094957891, 2165.126953125),
            new SpectrumPeak(406.1519033075, 2943.82788085938),
            new SpectrumPeak(407.199876940312, 1458.61560058594),
            new SpectrumPeak(410.748705065312, 1542.6640625),
            new SpectrumPeak(426.087389147344, 1669.91015625),
            new SpectrumPeak(426.278185045781, 1599.0947265625),
            new SpectrumPeak(450.680589830937, 2559.78735351563),
            new SpectrumPeak(459.686693346562, 4289.3408203125),
            new SpectrumPeak(464.23539940125, 1815.34252929688),
            new SpectrumPeak(465.700609362187, 2894.1220703125),
            new SpectrumPeak(469.743364489141, 1683.24743652344),
            new SpectrumPeak(471.344530260625, 2744.85766601563),
            new SpectrumPeak(487.715776598516, 2854.81713867188),
            new SpectrumPeak(496.270189440312, 1765.14221191406),
            new SpectrumPeak(502.740373766484, 2511.92749023438),
            new SpectrumPeak(516.229417955937, 11302.94140625),
            new SpectrumPeak(519.251085436406, 5697.017578125),
            new SpectrumPeak(530.227281725469, 3224.97607421875),
            new SpectrumPeak(532.280016100469, 2247.85595703125),
            new SpectrumPeak(533.2163564325, 2985.65844726563),
            new SpectrumPeak(537.253648912969, 2050.66943359375),
            new SpectrumPeak(557.744615709844, 3693.04150390625),
            new SpectrumPeak(563.776109850469, 2246.99194335938),
            new SpectrumPeak(565.761339342656, 4823.13818359375),
            new SpectrumPeak(572.386583483281, 3508.73608398438),
            new SpectrumPeak(578.903795397344, 2089.9072265625),
            new SpectrumPeak(586.779832995, 1887.47265625),
            new SpectrumPeak(602.331651842656, 1753.16296386719),
            new SpectrumPeak(606.275743639531, 2763.12548828125),
            new SpectrumPeak(617.319505846562, 3740.82470703125),
            new SpectrumPeak(618.312791979375, 4203.15087890625),
            new SpectrumPeak(619.269518053594, 5435.81396484375),
            new SpectrumPeak(620.337511217656, 3778.05078125),
            new SpectrumPeak(622.307237780156, 6229.90625),
            new SpectrumPeak(628.290697252812, 11510.7490234375),
            new SpectrumPeak(637.294603502812, 18673.736328125),
            new SpectrumPeak(664.318102037969, 2803.0126953125),
            new SpectrumPeak(666.852525866094, 10181.146484375),
            new SpectrumPeak(672.832750475469, 3355.21704101563),
            new SpectrumPeak(677.82329002625, 6123.15673828125),
            new SpectrumPeak(679.846300280156, 4045.50024414063),
            new SpectrumPeak(686.351854479375, 1983.88598632813),
            new SpectrumPeak(693.8374501825, 10529.3583984375),
            new SpectrumPeak(695.364061510625, 8855.1181640625),
            new SpectrumPeak(709.434190905156, 2648.53466796875),
            new SpectrumPeak(734.351793444219, 3702.2119140625),
            new SpectrumPeak(735.386705553594, 6051.64453125),
            new SpectrumPeak(736.851671373906, 1870.32824707031),
            new SpectrumPeak(741.862962877812, 2347.93627929688),
            new SpectrumPeak(743.869127428594, 6433.10107421875),
            new SpectrumPeak(750.384020006719, 2508.337890625),
            new SpectrumPeak(751.898790514531, 6425.45361328125),
            new SpectrumPeak(776.343858873906, 2091.82885742188),
            new SpectrumPeak(782.389146959844, 2800.0634765625),
            new SpectrumPeak(794.346117174687, 3134.03491210938),
            new SpectrumPeak(807.419054186406, 7499.3916015625),
            new SpectrumPeak(814.405504381719, 2218.85083007813),
            new SpectrumPeak(816.421739733281, 11138.12109375),
            new SpectrumPeak(823.351305162969, 4459.0205078125),
            new SpectrumPeak(838.371935045781, 2234.54418945313),
            new SpectrumPeak(907.444505846562, 1852.02319335938),
            new SpectrumPeak(911.420213854375, 2362.48388671875),
            new SpectrumPeak(915.992967760625, 1808.74389648438),
            new SpectrumPeak(919.37211815125, 2496.04614257813),
            new SpectrumPeak(953.512315905156, 4192.06982421875),
            new SpectrumPeak(961.599413073125, 2409.576171875),
            new SpectrumPeak(991.430833971562, 5899.87939453125),
            new SpectrumPeak(1014.43193260438, 1942.21203613281),
            new SpectrumPeak(1014.45193893922, 7565.21875),
            new SpectrumPeak(1025.44999901063, 6058.72998046875),
            new SpectrumPeak(1032.45720115906, 5034.3212890625),
            new SpectrumPeak(1032.45883591188, 11302.94140625),
            new SpectrumPeak(1081.60905662781, 3352.92602539063),
            new SpectrumPeak(1125.00029197938, 1525.560546875),
            new SpectrumPeak(1125.46640427125, 4263.609375),
            new SpectrumPeak(1131.52267868531, 4823.13818359375),
            new SpectrumPeak(1131.54106346375, 3161.83911132813),
            new SpectrumPeak(1143.49008591188, 14417.537109375),
            new SpectrumPeak(1145.53886619813, 2548.10571289063),
            new SpectrumPeak(1145.55307419313, 4361.20263671875),
            new SpectrumPeak(1161.50290329469, 12808.53125),
            new SpectrumPeak(1161.50334373719, 1988.13659667969),
            new SpectrumPeak(1196.63762108094, 5710.68505859375),
            new SpectrumPeak(1210.71891990906, 1890.66882324219),
            new SpectrumPeak(1228.57199509156, 4198.84619140625),
            new SpectrumPeak(1230.16862694031, 1532.05541992188),
            new SpectrumPeak(1244.61418358094, 2420.38842773438),
            new SpectrumPeak(1244.61447556031, 6229.90625),
            new SpectrumPeak(1246.60007126344, 7518.71826171875),
            new SpectrumPeak(1256.58139450563, 11510.7490234375),
            new SpectrumPeak(1266.60576072938, 1600.06958007813),
            new SpectrumPeak(1274.58920700563, 18673.736328125),
            new SpectrumPeak(1274.60234276063, 2532.78857421875),
            new SpectrumPeak(1274.89506737, 2794.80786132813),
            new SpectrumPeak(1333.70505173219, 10181.146484375),
            new SpectrumPeak(1351.79142967469, 1800.90002441406),
            new SpectrumPeak(1355.6465800525, 6123.15673828125),
            new SpectrumPeak(1359.69260056031, 4045.50024414063),
            new SpectrumPeak(1369.67111618531, 5537.71484375),
            new SpectrumPeak(1373.65231735719, 8860.5078125),
            new SpectrumPeak(1387.674900365, 10529.3583984375),
            new SpectrumPeak(1390.72812302125, 8855.1181640625),
            new SpectrumPeak(1470.77341110719, 6051.64453125),
            new SpectrumPeak(1486.74155075563, 5411.5556640625),
            new SpectrumPeak(1503.79758102906, 6425.45361328125),
            new SpectrumPeak(1590.25566307313, 3635.97338867188),
            new SpectrumPeak(1614.83810837281, 7499.3916015625),
            new SpectrumPeak(1632.84347946656, 11138.12109375),
            new SpectrumPeak(1672.29338279969, 2429.712890625),
            new SpectrumPeak(1745.92355759156, 8530.388671875),
    };

    public static final int MORPHEUS_MATCHING_PRODUCTS = 1;
    public static final double MORPHEUS_MATCHING_PRODUCTS_FRACTION = 0.033333;

    public static final String PEPTIDE_SEQUENCE = "PVPYYKNNGMPYSLSK";
    public static final FastaAminoAcid BEFORE_PEPTIDE = FastaAminoAcid.R;
    public static final FastaAminoAcid AFTER_PEPTIDE = FastaAminoAcid.V;
    public static final String PARENT_PROTEIN = "MSLKEDDFGKDNSRNIESYTGRIFDVYIQKDSYSQSALDDMFPEAVVSTAACVKNEAEDNINLIDTHPQFELVNTGLGAKSDDLKSPSAKATFTDKQRKNEVPNISVSNYFPGQSSETSSTTESWTIGCDKWSEKVEEAFLEALRLIMKNGTTKIKIRNANFGRNELISLYIKHKTNEFRTKKQISSHIQVWKKTIQNKIKDSLTLSSKEKELLHLIEHGAEQTTENSNLFYDIFEEIIDSLPSVSDSGSLTPKNLYVSNNSSGLSVHSKLLTPITASNEKKIENFIKTNAASQAKTPLIYAKHIYENIDGYKCVPSKRPLEQLSPTELHQGDRPNKASFSNKKAILESAKKIEIEQRKIINKYQRISRIQEHESNPEFSSNSNSGSEYESEEEVVPRSATVTQLQSRPVPYYKNNGMPYSLSKVRGRPMYPRPAEDAYNANYIQGLPQYQTSYFSQLLLSSPQHYEHSPHQRNFTPSNQSHGNFY";

    public static final int DEFAULT_CHARGE = 1;


    @Test
    public void testCondition() throws Exception {
          MorpheusScoringAlgorithm psx = new MorpheusScoringAlgorithm();

        double MassTolerance = psx.getMassTolerance();

        int precursorCharge = 1;
        double precursorMass = 100;
        RawPeptideScan raw = new RawPeptideScan("1", "");
        raw.setPeaks(EXPERIMENTAL_PEAKS);
        IScoredScan scored = new ScoredScan(raw);

        ITandemScoringAlgorithm ps = psx;
        IMeasuredSpectrum ms2 = ps.conditionSpectrum(scored, null);
        IMeasuredSpectrum ms1 = new MutableMeasuredSpectrum(precursorCharge, precursorMass, raw, THEORETICAL_PEAKS);


        ISpectrumPeak[] tps = ms1.getPeaks();
        ISpectrumPeak[] peaks  = ms2.getPeaks();
        int t = 0;
        int e = 0;
        int MatchingProducts = 0;
        int TotalProducts = peaks.length;
        double MatchingIntensity = 0.0;
        while (t < tps.length && e < peaks.length) {
             double mass_difference = peaks[e].getMassChargeRatio() - tps[t].getMassChargeRatio();
            if (Math.abs(mass_difference) <= MassTolerance) {
                MatchingProducts++;
                MatchingIntensity += peaks[e].getPeak();
                t++;
            }
            else if (mass_difference < 0) {
                e++;
            }
            else if (mass_difference > 0) {
                t++;
            }
        }
        double MatchingProductsFraction = (double) MatchingProducts / TotalProducts;

        Assert.assertEquals(MORPHEUS_MATCHING_PRODUCTS, MatchingProducts);
        Assert.assertEquals(MORPHEUS_MATCHING_PRODUCTS_FRACTION, MatchingProductsFraction,0.001);

    }


    // 9/13/2012 fixed with brute force
    public static final double SEQUENCE_SCORE =  1.0339340765660969;
     //  public static final double SEQUENCE_SCORE = 20.11979;

    public ITheoreticalPeak[] buildSpectra(double[] bIons, double[] yIons) {
        List<ITheoreticalPeak> holder = new ArrayList<ITheoreticalPeak>();
        for (int i = 0; i < bIons.length; i++) {
            double bIon = bIons[i];
            holder.add(new TheoreticalPeak(bIon, 1, null, IonType.B));
        }
        for (int i = 0; i < yIons.length; i++) {
            double bIon = yIons[i];
            holder.add(new TheoreticalPeak(bIon, 1, null, IonType.Y));
        }
        ITheoreticalPeak[] ret = new ITheoreticalPeak[holder.size()];
        holder.toArray(ret);
        return ret;
    }

    @Test
    public void testScoring() throws Exception {
        double[] scores = new double[4];
        XTandemMain main = new XTandemMain(XTandemUtilities.getResourceStream("input3.xml"),"input2.xml");
        Scorer scoreRunner = main.getScoreRunner();
        MorpheusScoringAlgorithm psx = new MorpheusScoringAlgorithm();
        ITandemScoringAlgorithm ps = psx;

        IProtein protein = Protein.getProtein("1","",PARENT_PROTEIN,"");
        IPolypeptide pp = Polypeptide.fromString(PEPTIDE_SEQUENCE);



        int precursorCharge = 1;
        double precursorMass = 100;
        RawPeptideScan raw = new RawPeptideScan("1", "");
        raw.setPeaks(EXPERIMENTAL_PEAKS);
        IScoredScan scored = new ScoredScan(raw);
        IMeasuredSpectrum ms2 = ps.conditionSpectrum(scored, null);

        IMeasuredSpectrum ms1 = new MutableMeasuredSpectrum(precursorCharge, precursorMass, raw, THEORETICAL_PEAKS);

        ITheoreticalSpectrumSet set = new TheoreticalSpectrumSet(precursorCharge, precursorMass, pp);
        for (int charge = 1; charge <= precursorCharge; charge++) {
            ITheoreticalSpectrum spectrum = scoreRunner.generateTheoreticalSpectra(set, charge);
        }

        double proton = MassCalculator.getDefaultCalculator().calcMass("H");
        ITheoreticalPeak[] tps = set.getSpectrum(precursorCharge).getTheoreticalPeaks();

         IMeasuredSpectrum fromMorpheus = new MutableMeasuredSpectrum(precursorCharge, precursorMass, raw, THEORETICAL_PEAKS);
        ISpectrumPeak[] peaks = fromMorpheus.getPeaks();
        for (int i = 0; i < tps.length; i++) {
            ITheoreticalPeak tp = tps[i];
            ISpectrumPeak pk = peaks[i];

            double expectedMass = tp.getMassChargeRatio() - precursorCharge * MorpheusScoringAlgorithm.PROTON_MASS;
            Assert.assertEquals(expectedMass, pk.getMassChargeRatio() ,MorpheusScoringAlgorithm.DEFAULT_MASS_TOLERANCE);

        }
        ITheoreticalSpectrum theory = new ScoringSpectrum(precursorCharge, set, tps);
        double score = ps.scoreSpectrum(ms2, theory, scores);

        Assert.assertEquals(SEQUENCE_SCORE, score,MorpheusScoringAlgorithm.DEFAULT_MASS_TOLERANCE);
        //   ScoringSpectrum spectrum = new ScoringSpectrum()


    }


}
