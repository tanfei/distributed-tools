package org.systemsbiology.xtandem.probid;

import org.junit.*;
import org.systemsbiology.xtandem.*;
import org.systemsbiology.xtandem.ionization.*;
import org.systemsbiology.xtandem.peptide.*;
import org.systemsbiology.xtandem.probidx.*;
import org.systemsbiology.xtandem.scoring.*;

import java.util.*;

/**
 * org.systemsbiology.xtandem.probid.ProbIdTests
 * User: Steve
 * Date: 1/13/12
 */
public class ProbIdTests {
    public static final ProbIdTests[] EMPTY_ARRAY = {};
    public static SpectrumPeak[] READ_PEAKS = {
            new SpectrumPeak(336.2644, 2554.0F),
            new SpectrumPeak(345.3678, 2115.0F),
            new SpectrumPeak(351.2167, 7045.0F),
            new SpectrumPeak(354.9368, 1066.0F),
            new SpectrumPeak(364.0204, 475.0F),
            new SpectrumPeak(376.8004, 2556.0F),
            new SpectrumPeak(382.3109, 2295.0F),
            new SpectrumPeak(387.6006, 1082.0F),
            new SpectrumPeak(393.9623, 3859.0F),
            new SpectrumPeak(395.9375, 1532.0F),
            new SpectrumPeak(403.2794, 761.0F),
            new SpectrumPeak(407.9617, 5045.0F),
            new SpectrumPeak(411.5009, 3863.0F),
            new SpectrumPeak(412.1258, 4674.0F),
            new SpectrumPeak(414.0627, 1092.0F),
            new SpectrumPeak(416.4247, 5442.0F),
            new SpectrumPeak(433.4421, 2891.0F),
            new SpectrumPeak(444.3599, 1919.0F),
            new SpectrumPeak(446.0516, 4381.0F),
            new SpectrumPeak(447.01, 2888.0F),
            new SpectrumPeak(448.963, 2292.0F),
            new SpectrumPeak(452.1877, 3281.0F),
            new SpectrumPeak(455.2191, 454.0F),
            new SpectrumPeak(458.9249, 3412.0F),
            new SpectrumPeak(466.1902, 1567.0F),
            new SpectrumPeak(467.9503, 2773.0F),
            new SpectrumPeak(470.9819, 5300.0F),
            new SpectrumPeak(471.9626, 1401.0F),
            new SpectrumPeak(474.1969, 942.0F),
            new SpectrumPeak(476.8678, 1530.0F),
            new SpectrumPeak(482.947, 3322.0F),
            new SpectrumPeak(485.4312, 2494.0F),
            new SpectrumPeak(486.2681, 2874.0F),
            new SpectrumPeak(494.3732, 2809.0F),
            new SpectrumPeak(495.1359, 764.0F),
            new SpectrumPeak(498.9677, 3534.0F),
            new SpectrumPeak(503.0357, 567.0F),
            new SpectrumPeak(505.2313, 736.0F),
            new SpectrumPeak(509.3862, 878.0F),
            new SpectrumPeak(516.2823, 2954.0F),
            new SpectrumPeak(517.2297, 3357.0F),
            new SpectrumPeak(518.1254, 8717.0F),
            new SpectrumPeak(519.2278, 1961.0F),
            new SpectrumPeak(522.314, 2101.0F),
            new SpectrumPeak(525.7562, 3160.0F),
            new SpectrumPeak(528.557, 3034.0F),
            new SpectrumPeak(531.2273, 5521.0F),
            new SpectrumPeak(533.1893, 2268.0F),
            new SpectrumPeak(539.1262, 3530.0F),
            new SpectrumPeak(541.3054, 4248.0F),
            new SpectrumPeak(544.2971, 1308.0F),
            new SpectrumPeak(553.907, 2317.0F),
            new SpectrumPeak(555.0356, 1597.0F),
            new SpectrumPeak(556.2595, 1121.0F),
            new SpectrumPeak(559.7981, 958.0F),
            new SpectrumPeak(561.0056, 2803.0F),
            new SpectrumPeak(566.7576, 799.0F),
            new SpectrumPeak(568.1464, 3123.0F),
            new SpectrumPeak(576.4458, 2029.0F),
            new SpectrumPeak(577.1821, 2367.0F),
            new SpectrumPeak(581.0853, 1663.0F),
            new SpectrumPeak(583.3608, 2957.0F),
            new SpectrumPeak(584.0121, 2673.0F),
            new SpectrumPeak(587.407, 1015.0F),
            new SpectrumPeak(588.9713, 1792.0F),
            new SpectrumPeak(591.2351, 716.0F),
            new SpectrumPeak(602.2467, 7134.0F),
            new SpectrumPeak(605.2889, 2717.0F),
            new SpectrumPeak(606.0466, 2361.0F),
            new SpectrumPeak(608.1812, 2352.0F),
            new SpectrumPeak(613.9929, 2814.0F),
            new SpectrumPeak(619.2219, 2275.0F),
            new SpectrumPeak(620.0518, 1264.0F),
            new SpectrumPeak(624.1138, 713.0F),
            new SpectrumPeak(624.8235, 5033.0F),
            new SpectrumPeak(628.0312, 857.0F),
            new SpectrumPeak(629.4904, 3659.0F),
            new SpectrumPeak(631.8932, 2353.0F),
            new SpectrumPeak(633.9775, 3582.0F),
            new SpectrumPeak(635.3362, 2933.0F),
            new SpectrumPeak(637.9763, 4011.0F),
            new SpectrumPeak(641.175, 2597.0F),
            new SpectrumPeak(648.3551, 1309.0F),
            new SpectrumPeak(653.0151, 2290.0F),
            new SpectrumPeak(654.0669, 992.0F),
            new SpectrumPeak(655.6899, 8571.0F),
            new SpectrumPeak(660.3271, 1262.0F),
            new SpectrumPeak(662.3918, 5489.0F),
            new SpectrumPeak(663.2959, 2219.0F),
            new SpectrumPeak(664.2356, 1962.0F),
            new SpectrumPeak(665.8987, 9639.0F),
            new SpectrumPeak(667.3484, 1874.0F),
            new SpectrumPeak(669.1055, 3606.0F),
            new SpectrumPeak(670.8657, 3816.0F),
            new SpectrumPeak(673.7659, 1455.0F),
            new SpectrumPeak(679.1101, 5843.0F),
            new SpectrumPeak(681.408, 2492.0F),
            new SpectrumPeak(682.7891, 5215.0F),
            new SpectrumPeak(685.9768, 4163.0F),
            new SpectrumPeak(690.1472, 2573.0F),
            new SpectrumPeak(692.1763, 2221.0F),
            new SpectrumPeak(693.2578, 2989.0F),
            new SpectrumPeak(694.2021, 4415.0F),
            new SpectrumPeak(700.3607, 381.0F),
            new SpectrumPeak(701.0049, 1527.0F),
            new SpectrumPeak(702.2969, 2327.0F),
            new SpectrumPeak(707.2964, 1050.0F),
            new SpectrumPeak(708.3513, 11271.0F),
            new SpectrumPeak(710.97, 1046.0F),
            new SpectrumPeak(717.0947, 1232.0F),
            new SpectrumPeak(719.3335, 4262.0F),
            new SpectrumPeak(722.7017, 1212.0F),
            new SpectrumPeak(723.3467, 3439.0F),
            new SpectrumPeak(727.1108, 2642.0F),
            new SpectrumPeak(729.1442, 3848.0F),
            new SpectrumPeak(730.0, 1.0F),
            new SpectrumPeak(730.8506, 2647.0F),
            new SpectrumPeak(734.6965, 11920.0F),
            new SpectrumPeak(736.0737, 821.0F),
            new SpectrumPeak(737.3162, 1554.0F),
            new SpectrumPeak(738.5017, 3679.0F),
            new SpectrumPeak(739.866, 7139.0F),
            new SpectrumPeak(741.535, 6009.0F),
            new SpectrumPeak(743.1333, 997.0F),
            new SpectrumPeak(743.8533, 2954.0F),
            new SpectrumPeak(744.9518, 5195.0F),
            new SpectrumPeak(745.8105, 923.0F),
            new SpectrumPeak(747.4136, 2239.0F),
            new SpectrumPeak(748.3748, 2495.0F),
            new SpectrumPeak(752.7791, 8668.0F),
            new SpectrumPeak(755.5278, 1546.0F),
            new SpectrumPeak(756.5969, 2325.0F),
            new SpectrumPeak(759.4819, 1849.0F),
            new SpectrumPeak(761.4822, 7326.0F),
            new SpectrumPeak(770.2544, 3793.0F),
            new SpectrumPeak(771.6035, 808.0F),
            new SpectrumPeak(772.3529, 4345.0F),
            new SpectrumPeak(779.444, 7788.0F),
            new SpectrumPeak(782.3143, 6284.0F),
            new SpectrumPeak(783.1079, 1681.0F),
            new SpectrumPeak(786.0706, 2821.0F),
            new SpectrumPeak(788.2771, 1979.0F),
            new SpectrumPeak(789.0116, 2079.0F),
            new SpectrumPeak(792.0585, 4659.0F),
            new SpectrumPeak(794.4421, 3379.0F),
            new SpectrumPeak(795.7151, 2936.0F),
            new SpectrumPeak(799.0059, 3655.0F),
            new SpectrumPeak(801.0989, 2171.0F),
            new SpectrumPeak(804.3716, 4111.0F),
            new SpectrumPeak(805.0667, 2.0F),
            new SpectrumPeak(806.2958, 3180.0F),
            new SpectrumPeak(807.3398, 1329.0F),
            new SpectrumPeak(808.1942, 1877.0F),
            new SpectrumPeak(809.1591, 2552.0F),
            new SpectrumPeak(809.9823, 1147.0F),
            new SpectrumPeak(813.1606, 2229.0F),
            new SpectrumPeak(814.2388, 2075.0F),
            new SpectrumPeak(815.214, 5431.0F),
            new SpectrumPeak(817.1279, 3294.0F),
            new SpectrumPeak(821.3528, 1111.0F),
            new SpectrumPeak(822.8214, 1683.0F),
            new SpectrumPeak(823.86, 5124.0F),
            new SpectrumPeak(824.7881, 4282.0F),
            new SpectrumPeak(826.2004, 5238.0F),
            new SpectrumPeak(829.2683, 6686.0F),
            new SpectrumPeak(831.3717, 5687.0F),
            new SpectrumPeak(832.3546, 3110.0F),
            new SpectrumPeak(834.8599, 3202.0F),
            new SpectrumPeak(837.0939, 2340.0F),
            new SpectrumPeak(838.4556, 10224.0F),
            new SpectrumPeak(839.5442, 6024.0F),
            new SpectrumPeak(844.0359, 2498.0F),
            new SpectrumPeak(845.5089, 450.0F),
            new SpectrumPeak(850.137, 4385.0F),
            new SpectrumPeak(850.8575, 19936.0F),
            new SpectrumPeak(851.9543, 8505.0F),
            new SpectrumPeak(852.995, 5489.0F),
            new SpectrumPeak(854.7871, 1187.0F),
            new SpectrumPeak(856.7865, 2095.0F),
            new SpectrumPeak(857.5453, 1473.0F),
            new SpectrumPeak(858.3224, 1256.0F),
            new SpectrumPeak(860.4209, 5217.0F),
            new SpectrumPeak(862.2198, 1541.0F),
            new SpectrumPeak(863.0605, 4711.0F),
            new SpectrumPeak(863.8584, 4718.0F),
            new SpectrumPeak(866.4028, 13235.0F),
            new SpectrumPeak(869.6449, 9911.0F),
            new SpectrumPeak(871.6331, 19983.0F),
            new SpectrumPeak(873.8247, 3893.0F),
            new SpectrumPeak(877.786, 3399.0F),
            new SpectrumPeak(878.8047, 8398.0F),
            new SpectrumPeak(879.8859, 10513.0F),
            new SpectrumPeak(882.0709, 9130.0F),
            new SpectrumPeak(882.7333, 1.0F),
            new SpectrumPeak(884.1075, 1819.0F),
            new SpectrumPeak(884.7618, 1807.0F),
            new SpectrumPeak(886.1326, 8719.0F),
            new SpectrumPeak(892.3354, 4656.0F),
            new SpectrumPeak(893.3838, 5418.0F),
            new SpectrumPeak(894.24, 6600.0F),
            new SpectrumPeak(895.0856, 8005.0F),
            new SpectrumPeak(896.3416, 1161.0F),
            new SpectrumPeak(899.1799, 9505.0F),
            new SpectrumPeak(902.3301, 2660.0F),
            new SpectrumPeak(903.3351, 6041.0F),
            new SpectrumPeak(904.729, 3300.0F),
            new SpectrumPeak(907.3762, 6458.0F),
            new SpectrumPeak(908.3171, 4607.0F),
            new SpectrumPeak(909.2587, 3585.0F),
            new SpectrumPeak(910.5575, 7373.0F),
            new SpectrumPeak(911.5909, 3831.0F),
            new SpectrumPeak(912.9348, 2262.0),
            new SpectrumPeak(913.9973, 8209.0),
            new SpectrumPeak(915.3447, 2401.0),
            new SpectrumPeak(916.3129, 2512.0),
            new SpectrumPeak(917.9213, 5604.0),
            new SpectrumPeak(919.2258, 4400.0),
            new SpectrumPeak(920.5024, 10031.0),
            new SpectrumPeak(921.3987, 7574.0),
            new SpectrumPeak(922.2472, 803.0),
            new SpectrumPeak(922.8628, 7536.0),
            new SpectrumPeak(924.7751, 1479.0),
            new SpectrumPeak(925.7761, 3440.0),
            new SpectrumPeak(928.9702, 9313.0),
            new SpectrumPeak(930.3137, 859.0),
            new SpectrumPeak(930.949, 5863.0),
            new SpectrumPeak(933.3461, 7575.0),
            new SpectrumPeak(934.5137, 5594.0),
            new SpectrumPeak(935.3733, 381.0),
            new SpectrumPeak(937.1284, 9441.0),
            new SpectrumPeak(938.2957, 5448.0),
            new SpectrumPeak(939.2015, 4406.0),
            new SpectrumPeak(940.0922, 5088.0),
            new SpectrumPeak(941.3561, 2054.0),
            new SpectrumPeak(942.2181, 2895.0),
            new SpectrumPeak(943.2462, 12053.0),
            new SpectrumPeak(944.0126, 4364.0),
            new SpectrumPeak(944.7964, 9279.0),
            new SpectrumPeak(945.5121, 3131.0),
            new SpectrumPeak(946.2262, 7316.0),
            new SpectrumPeak(948.6376, 1755.0),
            new SpectrumPeak(949.8546, 10217.0),
            new SpectrumPeak(950.8325, 8561.0),
            new SpectrumPeak(951.7534, 6365.0),
            new SpectrumPeak(953.2289, 3372.0),
            new SpectrumPeak(954.5916, 4384.0),
            new SpectrumPeak(955.624, 1362.0),
            new SpectrumPeak(956.2485, 2465.0),
            new SpectrumPeak(958.3146, 4113.0),
            new SpectrumPeak(959.4722, 3413.0),
            new SpectrumPeak(960.1, 2.0),
            new SpectrumPeak(960.8817, 2694.0),
            new SpectrumPeak(961.6709, 12020.0),
            new SpectrumPeak(963.3282, 34457.0),
            new SpectrumPeak(964.2786, 21060.0),
            new SpectrumPeak(965.3951, 12497.0),
            new SpectrumPeak(966.1272, 8370.0),
            new SpectrumPeak(968.3282, 4137.0),
            new SpectrumPeak(971.0232, 1974.0),
            new SpectrumPeak(972.3156, 2867.0),
            new SpectrumPeak(974.348, 1810.0),
            new SpectrumPeak(977.4111, 1060.0),
            new SpectrumPeak(980.0345, 3685.0),
            new SpectrumPeak(981.248, 9422.0),
            new SpectrumPeak(982.4283, 4548.0),
            new SpectrumPeak(983.4778, 1183.0),
            new SpectrumPeak(988.9604, 1545.0),
            new SpectrumPeak(992.0695, 7052.0),
            new SpectrumPeak(992.7554, 3.0),
            new SpectrumPeak(994.302, 5558.0),
            new SpectrumPeak(997.4508, 3459.0),
            new SpectrumPeak(998.6493, 3966.0),
            new SpectrumPeak(999.5873, 15172.0),
            new SpectrumPeak(1000.4142, 2837.0),
            new SpectrumPeak(1004.4847, 2128.0),
            new SpectrumPeak(1011.4132, 7325.0),
            new SpectrumPeak(1012.0667, 1.0),
            new SpectrumPeak(1015.7343, 1052.0),
            new SpectrumPeak(1017.0682, 4253.0),
            new SpectrumPeak(1019.7913, 6844.0),
            new SpectrumPeak(1021.4779, 1169.0),
            new SpectrumPeak(1023.0459, 2088.0),
            new SpectrumPeak(1027.9294, 1840.0),
            new SpectrumPeak(1031.6592, 5788.0),
            new SpectrumPeak(1032.4182, 1414.0),
            new SpectrumPeak(1034.3184, 1607.0),
            new SpectrumPeak(1035.5518, 2306.0),
            new SpectrumPeak(1037.873, 3213.0),
            new SpectrumPeak(1040.4697, 9857.0),
            new SpectrumPeak(1054.1428, 1182.0),
            new SpectrumPeak(1061.3777, 3731.0),
            new SpectrumPeak(1063.9531, 911.0),
            new SpectrumPeak(1071.1904, 3869.0),
            new SpectrumPeak(1071.9067, 1123.0),
            new SpectrumPeak(1073.3408, 3035.0),
            new SpectrumPeak(1075.1401, 3722.0),
            new SpectrumPeak(1081.6199, 1257.0),
            new SpectrumPeak(1082.2856, 1925.0),
            new SpectrumPeak(1084.9639, 8727.0),
            new SpectrumPeak(1085.6001, 1.0),
            new SpectrumPeak(1087.8347, 505.0),
            new SpectrumPeak(1089.4299, 762.0),
            new SpectrumPeak(1091.5015, 1412.0),
            new SpectrumPeak(1092.5215, 4759.0),
            new SpectrumPeak(1094.7805, 4869.0),
            new SpectrumPeak(1096.4087, 2252.0),
            new SpectrumPeak(1104.813, 1953.0),
            new SpectrumPeak(1108.4629, 3205.0),
            new SpectrumPeak(1112.0088, 1421.0),
            new SpectrumPeak(1118.1055, 1232.0),
            new SpectrumPeak(1119.6448, 752.0),
            new SpectrumPeak(1126.2881, 3687.0),
            new SpectrumPeak(1132.0735, 819.0),
            new SpectrumPeak(1132.9929, 4407.0),
            new SpectrumPeak(1134.0928, 2353.0),
            new SpectrumPeak(1135.2893, 1732.0),
            new SpectrumPeak(1137.4595, 6391.0),
            new SpectrumPeak(1143.3369, 4132.0),
            new SpectrumPeak(1147.8301, 3061.0),
            new SpectrumPeak(1150.1541, 2992.0),
            new SpectrumPeak(1156.1602, 2320.0),
            new SpectrumPeak(1159.9919, 4974.0),
            new SpectrumPeak(1166.1738, 7447.0),
            new SpectrumPeak(1179.7134, 1597.0),
            new SpectrumPeak(1184.5164, 7896.0),
            new SpectrumPeak(1185.4446, 3029.0),
            new SpectrumPeak(1186.8752, 3361.0),
            new SpectrumPeak(1189.3591, 4668.0),
            new SpectrumPeak(1191.1943, 2874.0),
            new SpectrumPeak(1194.071, 6286.0),
            new SpectrumPeak(1196.8323, 3013.0),
            new SpectrumPeak(1198.3933, 2199.0),
            new SpectrumPeak(1199.8298, 10589.0),
            new SpectrumPeak(1202.6768, 15417.0),
            new SpectrumPeak(1209.0093, 6493.0),
            new SpectrumPeak(1209.9001, 3817.0),
            new SpectrumPeak(1211.1096, 3120.0),
            new SpectrumPeak(1213.606, 2047.0),
            new SpectrumPeak(1217.1812, 13812.0),
            new SpectrumPeak(1222.7544, 4342.0),
            new SpectrumPeak(1224.9849, 1579.0),
            new SpectrumPeak(1234.8093, 5878.0),
            new SpectrumPeak(1236.3093, 6541.0),
            new SpectrumPeak(1238.2437, 4831.0),
            new SpectrumPeak(1242.4526, 3969.0),
            new SpectrumPeak(1245.1213, 922.0),
            new SpectrumPeak(1251.1394, 1496.0),
            new SpectrumPeak(1252.5884, 4137.0),
            new SpectrumPeak(1253.8027, 556.0),
            new SpectrumPeak(1261.3096, 2335.0),
            new SpectrumPeak(1262.2388, 4649.0),
            new SpectrumPeak(1264.9255, 4978.0),
            new SpectrumPeak(1271.9458, 2887.0),
            new SpectrumPeak(1274.5713, 3374.0),
            new SpectrumPeak(1275.3926, 2091.0),
            new SpectrumPeak(1285.4131, 1718.0),
            new SpectrumPeak(1287.2861, 2250.0),
            new SpectrumPeak(1289.9385, 3042.0),
            new SpectrumPeak(1294.3545, 1869.0),
            new SpectrumPeak(1295.9343, 10770.0),
            new SpectrumPeak(1298.8933, 3196.0),
            new SpectrumPeak(1303.6365, 981.0),
            new SpectrumPeak(1315.3521, 539.0),
            new SpectrumPeak(1318.2214, 3199.0),
            new SpectrumPeak(1327.6763, 4369.0),
            new SpectrumPeak(1359.0886, 2696.0),
            new SpectrumPeak(1362.1279, 10155.0),
            new SpectrumPeak(1390.5898, 2649.0),
            new SpectrumPeak(1395.417, 4824.0),
            new SpectrumPeak(1401.4377, 3212.0),
            new SpectrumPeak(1409.3948, 1635.0),
            new SpectrumPeak(1415.0564, 2268.0),
            new SpectrumPeak(1416.2751, 3530.0),
            new SpectrumPeak(1420.938, 2843.0),
            new SpectrumPeak(1447.0088, 1603.0),
            new SpectrumPeak(1453.2917, 2047.0),
            new SpectrumPeak(1457.4629, 3631.0),
            new SpectrumPeak(1458.6763, 858.0),
            new SpectrumPeak(1465.5728, 1781.0),
            new SpectrumPeak(1477.0522, 1591.0),
            new SpectrumPeak(1488.8845, 2971.0),
            new SpectrumPeak(1502.6162, 6244.0),
            new SpectrumPeak(1509.2966, 7218.0),
            new SpectrumPeak(1524.2642, 2730.0),
            new SpectrumPeak(1532.374, 1044.0),
            new SpectrumPeak(1557.6191, 3910.0),
            new SpectrumPeak(1650.1294, 3997.0),
            new SpectrumPeak(1655.5686, 1439.0),
            new SpectrumPeak(1676.6235, 1824.0),
            new SpectrumPeak(1707.6895, 992.0),
            new SpectrumPeak(1727.5225, 1820.0),
            new SpectrumPeak(1760.0198, 1324.0),
            new SpectrumPeak(1766.5647, 1577.0),
            new SpectrumPeak(1792.8562, 2433.0),
            new SpectrumPeak(1812.6958, 1919.0),
            new SpectrumPeak(1816.2524, 2269.0),
            new SpectrumPeak(1821.5369, 1449.0),
            new SpectrumPeak(1854.3564, 1052.0),
            new SpectrumPeak(1880.8625, 989.0),
    };
    // notmslized contents of
    public static SpectrumPeak[] NORMALIZED_PEAKS = {
            new SpectrumPeak(336.2644, 58.59814156246414F),
            new SpectrumPeak(351.2167, 161.6381782723414F),
            new SpectrumPeak(376.8004, 58.644028909028336F),
            new SpectrumPeak(393.9623, 88.53963519559481F),
            new SpectrumPeak(407.9617, 115.75083170815648F),
            new SpectrumPeak(411.5009, 88.63140988872318F),
            new SpectrumPeak(412.1258, 107.23872892050017F),
            new SpectrumPeak(416.4247, 124.85947000114719F),
            new SpectrumPeak(433.4421, 66.33015945852931F),
            new SpectrumPeak(446.0516, 100.51623264884708F),
            new SpectrumPeak(447.01, 66.26132843868304F),
            new SpectrumPeak(452.1877, 75.27819203854537F),
            new SpectrumPeak(458.9249, 78.28381323849949F),
            new SpectrumPeak(467.9503, 63.6228060112424F),
            new SpectrumPeak(470.9819, 121.60146839509005F),
            new SpectrumPeak(482.947, 76.21888264311116F),
            new SpectrumPeak(485.4312, 57.221521165538604F),
            new SpectrumPeak(486.2681, 65.94011701273374F),
            new SpectrumPeak(494.3732, 64.44877824939773F),
            new SpectrumPeak(498.9677, 81.08294137891477F),
            new SpectrumPeak(516.2823, 67.77561087530114F),
            new SpectrumPeak(517.2297, 77.02191120798439F),
            new SpectrumPeak(518.1254, 200.0F),
            new SpectrumPeak(525.7562, 72.50200757141218F),
            new SpectrumPeak(528.557, 69.61110473786853F),
            new SpectrumPeak(531.2273, 126.67202019043249F),
            new SpectrumPeak(539.1262, 80.9911666857864F),
            new SpectrumPeak(541.3054, 97.46472410232879F),
            new SpectrumPeak(561.0056, 64.31111620970518F),
            new SpectrumPeak(568.1464, 71.65309165997476F),
            new SpectrumPeak(577.1821, 54.307674658712855F),
            new SpectrumPeak(583.3608, 67.84444189514741F),
            new SpectrumPeak(584.0121, 61.32843868303315F),
            new SpectrumPeak(602.2467, 163.68016519444762F),
            new SpectrumPeak(605.2889, 62.337960307445215F),
            new SpectrumPeak(606.0466, 54.170012619020305F),
            new SpectrumPeak(608.1812, 53.963519559481476F),
            new SpectrumPeak(613.9929, 64.56349661580819F),
            new SpectrumPeak(624.8235, 115.47550762877137F),
            new SpectrumPeak(629.4904, 83.95090053917632F),
            new SpectrumPeak(631.8932, 53.98646323276357F),
            new SpectrumPeak(633.9775, 82.1842376964552F),
            new SpectrumPeak(635.3362, 67.2937937363772F),
            new SpectrumPeak(637.9763, 92.02707353447288F),
            new SpectrumPeak(641.175, 59.58471951359413F),
            new SpectrumPeak(655.6899, 196.6502237008145F),
            new SpectrumPeak(662.3918, 92.09731543624162F),
            new SpectrumPeak(665.8987, 161.7281879194631F),
            new SpectrumPeak(669.1055, 60.503355704697995F),
            new SpectrumPeak(670.8657, 64.02684563758389F),
            new SpectrumPeak(679.1101, 98.03691275167785F),
            new SpectrumPeak(681.408, 41.81208053691275F),
            new SpectrumPeak(682.7891, 87.5F),
            new SpectrumPeak(685.9768, 69.8489932885906F),
            new SpectrumPeak(690.1472, 43.171140939597315F),
            new SpectrumPeak(693.2578, 50.151006711409394F),
            new SpectrumPeak(694.2021, 74.07718120805369F),
            new SpectrumPeak(702.2969, 39.04362416107382F),
            new SpectrumPeak(708.3513, 189.11073825503354F),
            new SpectrumPeak(719.3335, 71.51006711409396F),
            new SpectrumPeak(723.3467, 57.7013422818792F),
            new SpectrumPeak(727.1108, 44.328859060402685F),
            new SpectrumPeak(729.1442, 64.56375838926175F),
            new SpectrumPeak(730.8506, 44.41275167785235F),
            new SpectrumPeak(734.6965, 200.0F),
            new SpectrumPeak(738.5017, 61.72818791946308F),
            new SpectrumPeak(739.866, 119.78187919463087F),
            new SpectrumPeak(741.535, 100.82214765100672F),
            new SpectrumPeak(743.8533, 49.56375838926175F),
            new SpectrumPeak(744.9518, 87.16442953020133F),
            new SpectrumPeak(748.3748, 41.86241610738255F),
            new SpectrumPeak(752.7791, 145.43624161073825F),
            new SpectrumPeak(756.5969, 39.01006711409396F),
            new SpectrumPeak(761.4822, 122.91946308724833F),
            new SpectrumPeak(770.2544, 63.64093959731544F),
            new SpectrumPeak(772.3529, 72.90268456375838F),
            new SpectrumPeak(779.444, 130.6711409395973F),
            new SpectrumPeak(782.3143, 105.43624161073825F),
            new SpectrumPeak(786.0706, 47.33221476510067F),
            new SpectrumPeak(792.0585, 78.17114093959732F),
            new SpectrumPeak(794.4421, 56.69463087248322F),
            new SpectrumPeak(795.7151, 49.261744966442954F),
            new SpectrumPeak(799.0059, 61.325503355704704F),
            new SpectrumPeak(804.3716, 68.97651006711409F),
            new SpectrumPeak(806.2958, 53.355704697986575F),
            new SpectrumPeak(809.1591, 42.81879194630873F),
            new SpectrumPeak(815.214, 91.1241610738255F),
            new SpectrumPeak(817.1279, 55.26845637583892F),
            new SpectrumPeak(823.86, 85.97315436241611F),
            new SpectrumPeak(824.7881, 71.84563758389262F),
            new SpectrumPeak(826.2004, 87.88590604026845F),
            new SpectrumPeak(829.2683, 112.18120805369128F),
            new SpectrumPeak(831.3717, 56.91838062353F),
            new SpectrumPeak(832.3546, 31.126457488865533F),
            new SpectrumPeak(834.8599, 32.04724015413101F),
            new SpectrumPeak(837.0939, 23.41990692088275F),
            new SpectrumPeak(838.4556, 102.32697793124154F),
            new SpectrumPeak(839.5442, 60.29124756042636F),
            new SpectrumPeak(844.0359, 25.001251063403895F),
            new SpectrumPeak(850.137, 43.88730420857729F),
            new SpectrumPeak(850.8575, 199.5296001601361F),
            new SpectrumPeak(851.9543, 85.12235400090077F),
            new SpectrumPeak(852.995, 54.936696191763005F),
            new SpectrumPeak(860.4209, 52.21438222489115F),
            new SpectrumPeak(863.0605, 47.15007756593104F),
            new SpectrumPeak(863.8584, 47.22013711654907F),
            new SpectrumPeak(866.4028, 132.4625932042236F),
            new SpectrumPeak(869.6449, 99.19431516789271F),
            new SpectrumPeak(871.6331, 200.0F),
            new SpectrumPeak(873.8247, 38.963118650853225F),
            new SpectrumPeak(878.8047, 84.05144372716809F),
            new SpectrumPeak(879.8859, 105.21943652104288F),
            new SpectrumPeak(882.0709, 91.37767102036732F),
            new SpectrumPeak(886.1326, 87.26417454836611F),
            new SpectrumPeak(892.3354, 46.59960966821799F),
            new SpectrumPeak(893.3838, 54.22609217835161F),
            new SpectrumPeak(894.24, 66.05614772556673F),
            new SpectrumPeak(895.0856, 80.11810038532752F),
            new SpectrumPeak(899.1799, 95.13086123204724F),
            new SpectrumPeak(903.3351, 60.461392183355855F),
            new SpectrumPeak(907.3762, 64.63493969874393F),
            new SpectrumPeak(908.3171, 46.10919281389181F),
            new SpectrumPeak(909.2587, 35.880498423660114F),
            new SpectrumPeak(910.5575, 73.79272381524295F),
            new SpectrumPeak(911.5909, 38.342591202522144F),
            new SpectrumPeak(913.9973, 82.15983586048141F),
            new SpectrumPeak(917.9213, 56.087674523344845F),
            new SpectrumPeak(919.2258, 44.037431817044485F),
            new SpectrumPeak(920.5024, 100.3953360356303F),
            new SpectrumPeak(921.3987, 75.80443376870339F),
            new SpectrumPeak(922.8628, 75.42411049391983F),
            new SpectrumPeak(925.7761, 34.42926487514387F),
            new SpectrumPeak(928.9702, 93.20922784366712F),
            new SpectrumPeak(930.949, 58.679877896211785F),
            new SpectrumPeak(933.3461, 75.81444227593454F),
            new SpectrumPeak(934.5137, 55.98758945103338F),
            new SpectrumPeak(937.1284, 94.49031676925387F),
            new SpectrumPeak(938.2957, 54.52634739528599F),
            new SpectrumPeak(939.2015, 25.573903706068435F),
            new SpectrumPeak(940.0922, 29.53246074817889F),
            new SpectrumPeak(943.2462, 69.95965986591985F),
            new SpectrumPeak(944.0126, 25.330121600835824F),
            new SpectrumPeak(944.7964, 53.85843224888992F),
            new SpectrumPeak(946.2262, 42.4645209971849F),
            new SpectrumPeak(949.8546, 59.30289926575152F),
            new SpectrumPeak(950.8325, 49.69091911658008F),
            new SpectrumPeak(951.7534, 36.944597614417965F),
            new SpectrumPeak(954.5916, 25.446208317613255F),
            new SpectrumPeak(958.3146, 23.873233305279044F),
            new SpectrumPeak(959.4722, 19.810198218068898F),
            new SpectrumPeak(961.6709, 69.76811678323708F),
            new SpectrumPeak(963.3282, 200.0F),
            new SpectrumPeak(964.2786, 122.23931276663669F),
            new SpectrumPeak(965.3951, 72.53678497837885F),
            new SpectrumPeak(966.1272, 48.582290971355604F),
            new SpectrumPeak(968.3282, 24.012537365411962F),
            new SpectrumPeak(980.0345, 21.38897756624198F),
            new SpectrumPeak(981.248, 54.68845227384856F),
            new SpectrumPeak(982.4283, 26.398119395188207F),
            new SpectrumPeak(992.0695, 40.93217633572279F),
            new SpectrumPeak(994.302, 32.26049859244856F),
            new SpectrumPeak(997.4508, 20.077197666656993F),
            new SpectrumPeak(998.6493, 23.019995936964914F),
            new SpectrumPeak(999.5873, 88.06338334736049F),
            new SpectrumPeak(1011.4132, 42.51676001973474F),
            new SpectrumPeak(1017.0682, 24.685840322721074F),
            new SpectrumPeak(1019.7913, 39.724874481237485F),
            new SpectrumPeak(1031.6592, 33.59549583538903F),
            new SpectrumPeak(1040.4697, 57.21333836375773F),
            new SpectrumPeak(1061.3777, 21.65597701483008F),
            new SpectrumPeak(1071.1904, 22.456975360594363F),
            new SpectrumPeak(1075.1401, 21.603737992280234F),
            new SpectrumPeak(1084.9639, 50.65443886583277F),
            new SpectrumPeak(1092.5215, 27.62283425719012F),
            new SpectrumPeak(1094.7805, 28.261311199466F),
            new SpectrumPeak(1126.2881, 21.400586237919725F),
            new SpectrumPeak(1132.9929, 25.579708041907306F),
            new SpectrumPeak(1137.4595, 37.095510346228636F),
            new SpectrumPeak(1143.3369, 23.983515686217604F),
            new SpectrumPeak(1159.9919, 28.870766462547525F),
            new SpectrumPeak(1166.1738, 43.224888992077084F),
            new SpectrumPeak(1184.5164, 45.83103578373044F),
            new SpectrumPeak(1189.3591, 27.094639695852802F),
            new SpectrumPeak(1194.071, 36.486055083147114F),
            new SpectrumPeak(1199.8298, 137.36784069533633F),
            new SpectrumPeak(1202.6768, 200.0F),
            new SpectrumPeak(1209.0093, 84.23169228773432F),
            new SpectrumPeak(1209.9001, 49.51676720503341F),
            new SpectrumPeak(1217.1812, 179.17882856586886F),
            new SpectrumPeak(1222.7544, 56.32743075825387F),
            new SpectrumPeak(1234.8093, 76.25348641110462F),
            new SpectrumPeak(1236.3093, 84.8543815268859F),
            new SpectrumPeak(1238.2437, 62.67107738211066F),
            new SpectrumPeak(1242.4526, 51.48861646234676F),
            new SpectrumPeak(1252.5884, 53.66802879937731F),
            new SpectrumPeak(1262.2388, 60.31004735032756F),
            new SpectrumPeak(1264.9255, 64.5780631770124F),
            new SpectrumPeak(1295.9343, 139.7158980346371F),
            new SpectrumPeak(1327.6763, 56.677693455276646F),
            new SpectrumPeak(1362.1279, 131.7376921580074F),
            new SpectrumPeak(1390.5898, 34.364662385678145F),
            new SpectrumPeak(1395.417, 62.58026853473439F),
            new SpectrumPeak(1401.4377, 41.66828825322696F),
            new SpectrumPeak(1409.3948, 21.210352208600895F),
            new SpectrumPeak(1415.0564, 29.422066549912433F),
            new SpectrumPeak(1416.2751, 45.79360446260621F),
            new SpectrumPeak(1420.938, 36.881364727249135F),
            new SpectrumPeak(1447.0088, 20.795226049166505F),
            new SpectrumPeak(1453.2917, 26.555101511318675F),
            new SpectrumPeak(1457.4629, 47.10384640332101F),
            new SpectrumPeak(1458.6763, 11.130570149834599F),
            new SpectrumPeak(1465.5728, 23.104365311020302F),
            new SpectrumPeak(1477.0522, 20.63955373937861F),
            new SpectrumPeak(1488.8845, 38.5418693649867F),
            new SpectrumPeak(1502.6162, 81.00149185963546F),
            new SpectrumPeak(1509.2966, 93.63689433741973F),
            new SpectrumPeak(1524.2642, 35.41545047674644F),
            new SpectrumPeak(1532.374, 13.543490951546994F),
            new SpectrumPeak(1557.6191, 50.7232276058896F),
            new SpectrumPeak(1650.1294, 51.85185185185185F),
            new SpectrumPeak(1655.5686, 18.667704482065254F),
            new SpectrumPeak(1676.6235, 23.662191087760263F),
            new SpectrumPeak(1707.6895, 12.868910942466108F),
            new SpectrumPeak(1727.5225, 23.610300317830966F),
            new SpectrumPeak(1760.0198, 17.17584484659791F),
            new SpectrumPeak(1766.5647, 20.457936044626063F),
            new SpectrumPeak(1792.8562, 31.56256080949601F),
            new SpectrumPeak(1812.6958, 24.89459687358111F),
            new SpectrumPeak(1816.2524, 29.43503924239476F),
            new SpectrumPeak(1821.5369, 18.7974314068885F),
            new SpectrumPeak(1854.3564, 200.0F),
            new SpectrumPeak(1880.8625, 188.02281368821292F),
    };

    @Test
    public void testCondition() throws Exception {
        ProbIdScoringAlgorithm psx = new ProbIdScoringAlgorithm();
         ITandemScoringAlgorithm ps = psx;
         int precursorCharge = 1;
        double precursorMass = 100;
        RawPeptideScan raw = new RawPeptideScan("1", "");
        raw.setPeaks(READ_PEAKS);
        IScoredScan scored = new ScoredScan(raw);
        IMeasuredSpectrum ms2 = ps.conditionSpectrum(scored, null);
        IMeasuredSpectrum ms1 = new MutableMeasuredSpectrum(precursorCharge, precursorMass, raw, NORMALIZED_PEAKS);
        ISpectrumPeak[] peaks1 = ms2.getPeaks();
        ISpectrumPeak[] peaks2 = ms1.getPeaks();
        int numberUnmatched = 0;
        for (int i = 0; i < peaks2.length; i++) {
            ISpectrumPeak p2 = peaks2[i];
            if (i >= peaks1.length) {
                numberUnmatched++;
                continue;
            }
            ISpectrumPeak p1 = peaks1[i];
            double m2 = p2.getMassChargeRatio();
            double m1 = p1.getMassChargeRatio();
            double pk2 = p2.getPeak();
            double pk1 = p1.getPeak();
            if (Math.abs(m2 - m1) > 0.1) {


            }
            else {
                Assert.assertEquals(pk2, pk1, 0.1);
                Assert.assertEquals(m2, m1, 0.01);
            }


        }
        Assert.assertTrue(numberUnmatched < 4);


    }

    public static double[] B_Masses = {
            72.0435136,
            132.0980636,
            185.1275772,
            189.1195272,
            256.1646908,
            302.2035908,
            313.1861544,
            373.24070439999997,
            428.2130972,
            488.26764719999994,
            499.2502108,
            602.3105744,
            614.2771536,
            685.3142672,
            703.3582525999999,
            756.3513808,
            804.4059307999999,
            875.4430443999998,
            885.3939736,
            972.4260018,
            988.5271079999999,
            1075.5591361999998,
            1085.5100654,
            1156.5471790000001,
            1204.6017289999998,
            1257.5948572000002,
            1275.6388425999999,
            1346.6759561999997,
            1358.6425354000003,
            1461.7028989999997,
            1472.6854626000002,
            1532.7400125999998,
            1587.7124054,
            1647.7669553999997,
            1658.749519,
            1704.7884189999997,
            1771.8335826,
            1775.8255325999999,
            1828.8550462,
            1888.9095962,
            1941.9391098,
            1959.9467098,
    };
    public static double[] Y_Masses = {
    };
    public static final String SEQUENCE = "AIAGDADAAESIATTNDALGI";

    // 9/13/2012 fixed with brute force
    public static final double SEQUENCE_SCORE = 2171.62; // 43.14564548953248;
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

   // @Test
    public void testScoring() throws Exception {
        double[] scores = new double[4];
        ProbIdScoringAlgorithm psx = new ProbIdScoringAlgorithm();
        ITandemScoringAlgorithm ps = psx;
       int precursorCharge = 1;
        double precursorMass = 100;
        RawPeptideScan raw = new RawPeptideScan("1", "");
        raw.setPeaks(READ_PEAKS);
        IScoredScan scored = new ScoredScan(raw);
        IPolypeptide pp = Polypeptide.fromString(SEQUENCE);
        IMeasuredSpectrum ms2 = ps.conditionSpectrum(scored, null);

        ITheoreticalPeak[] peaks = buildSpectra(B_Masses, Y_Masses);
        ITheoreticalSpectrumSet set = new TheoreticalSpectrumSet(precursorCharge, precursorMass, pp);
        ITheoreticalSpectrum theory = new ScoringSpectrum(precursorCharge, set, peaks);
        double score = ps.scoreSpectrum(ms2, theory, scores);

        double dot = scores[0];
        double logMissScore = scores[1];
        double numTotalIons = scores[2];
        double numMatchedIons = scores[3];

        // 9/13/2012 fixed with brute force
        double logHitScoreAnswer = 2171.62; // 43.14564548953248;
        double logMissScoreAnswer = -198.24; // -234.96060883335915;
        Assert.assertEquals(SEQUENCE_SCORE, score);
        //   ScoringSpectrum spectrum = new ScoringSpectrum()


    }

    double score = 20.119794559592016;


}
