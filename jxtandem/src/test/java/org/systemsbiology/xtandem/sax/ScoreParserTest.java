package org.systemsbiology.xtandem.sax;

import org.junit.*;
import org.systemsbiology.xtandem.*;
import org.systemsbiology.xtandem.hadoop.*;
import org.systemsbiology.xtandem.scoring.*;

/**
 * org.systemsbiology.xtandem.sax.ScoreParserTest
 * User: steven
 * Date: 3/28/11
 */
public class ScoreParserTest {
    public static final ScoreParserTest[] EMPTY_ARRAY = {};

    public static final String SCORE_XML =
            "<score id=\"7858\"   expectedValue=\"205.515179402281\"  >\n" +
                    "<scan num=\"7858\"\n" +
                    " mslevel=\"2\"\n" +
                    " peaksCount=\"345\"\n" +
                    " polarity=\"+\"\n" +
                    " scantype=\"Full\"\n" +
                    " filterLine=\"null\"\n" +
                    " retentionTime=\"PT2897.2S\"\n" +
                    " lowMz=\"324.828\"\n" +
                    " highMz=\"1998.61\"\n" +
                    " basePeakMz=\"1231.62\"\n" +
                    " basePeakIntensity=\"143.081\"\n" +
                    " totIonCurrent=\"3862.91\"\n" +
                    " collisionEnergy=\"0.0\"\n" +
                    ">\n" +
                    "<precursorMz precursorIntensity=\"69490.9\"  activationMethod=\"CID\" >\n" +
                    "1096.63</precursorMz>\n" +
                    "<peaks precision=\"32\"\n" +
                    "     byteOrder=\"network\"\n" +
                    "     contentType=\"m/z-int\"\n" +
                    "     compressionType=\"none\"\n" +
                    "     compressedLen=\"0\" >Q6Jp7ECM7AtDqyP9QDD4u0OtJFdAQwAtQ68QOEDleVJDtRitQKD0KEO2vWJAzmQjQ7cU8UDFXFxD\n" +
                    "u7kTQT6NXEO8uMNA12zDQ78fOECPathDyZ0WQR2S5UPNOD5AzjYtQ86e4kEyp0ZD0TMuQSLjmEPS\n" +
                    "JkRAoPJxQ9MQvkB40/dD1G+MQDFlPkPdpulAl+vSQ+KgokCWqsZD6UPKQGdRXEPqIahAVZhAQ+y2\n" +
                    "0UIafSVD8JuVQDCN5UPxXutBJROOQ/YjAkD2rMxD9sMsQXd6nkP3bpJBF4nJQ/j0gEEcbIREAYDY\n" +
                    "QLDJq0QCkRpA1RgJRALYJkDKQ2JEA6jiQRxE7UQFH9ZAjwMTRAdXtUBzWlVEB6SKQHme+UQITndA\n" +
                    "jcxhRA/mDkEKC99EETE0QHno8UQR/cBAMSA8RBLQhECD8PNEExHGQLwd30QWF5ZAVQ4nRBert0C7\n" +
                    "MN1EGNnYQHmZdUQZzuJBFkV9RBwKFkBnjRhEHk54QOXFBkQekJJAnxhdRB7sGEGXkj5EIEyPQYXq\n" +
                    "QUQhIsRA7y3jRCLBVkFDAcBEIxv1QP5WnEQjTeVBDgVeRCTVKEGTMbFEJ9wRQB8oRkQo17FBbUpc\n" +
                    "RCkXOkC53t1EKfAQQIYz9EQqHZJBGgkZRCreXECvYDNEK6GsQIXtkUQsJSdBBp3KRCxruECMpRlE\n" +
                    "Let3QKfY9EQuZ6VAMOL7RC7S/kCm3BZELwy9QSDZF0QviMpAoALDRDAd/0D3fwREMweYQRf5lEQ2\n" +
                    "BdBBNudqRDdZi0ChE+dEN6bEQazbA0Q35mpBIKqdRDiRUkCeDUlEOOrSQaeFnEQ5dfxBHMhjRDmd\n" +
                    "mkEODU9EO9hKQTyKVkQ8LH9AsJmyRDyPQkEOLK1EPSeFQhLV8UQ9XERBL9+OREGGeUC51IxEQa1G\n" +
                    "QHk9U0RC3jxAQe2WREOcekBVYk5ERY3bQItOkERGDIJAuSceREZ6AEGKuY5ER0SjQTYEMkRHoCFA\n" +
                    "Q7KHREgq/UANO1JESHMsQFSmYkRJZFpCTwL8REnd9EHIJhpESmvkQJ4iRERK4oxAziXqREwlQEBV\n" +
                    "enRETGnGQHmk6UROJGRAnVz+RE5hxkDds0tET04sQEPvzERP3ABAwwN4RFBqmEB5zAtEUKdKQGdI\n" +
                    "sERQ1ZBBG9lARFXoVEGf3+JEVyKYQWGGmERXYehAlRS3RFiiqkBnPsxEWOS4QQELUURZbOxBYQKH\n" +
                    "RFmkTEC3fmdEW94IQYK9o0Rc4zxAr22cRF2bpEEFWWxEXhPOQFTsNkRh3JBAwi37RGNtdEIKHDlE\n" +
                    "Y7xyQDFU00Rkdso/9kj9RGWgPkGsaiBEZehCQI7TdERmt8ZAoNxERGbhukDB6sVEZ/bWQa9FdERo\n" +
                    "RCZA/riuRGiAVkD4tvhEaQzkQYwPP0RraDZAQuhYRGvRQEGvnU1EbCGcQIYv5kRtdPpBU1S8RG4n\n" +
                    "qkDXTmVEbqdaQA0G5ERwFvxC0b4/RHB8EkC5/udEcK9IQWnaAURxWvZAVYTwRHGc8kBnO+5EdSne\n" +
                    "QoxsFER1brhB8Vi6RHWZ2kE0jH5EdhuEQRpaJER4iyxAxX89RHlXtEBzGgtEfI42QReAYkR8455A\n" +
                    "H6SRRH08lEG1YlpEfmDgQU4XgESANYpAhgxnRIBN70B56JpEgK4DQlCLq0SAxbBBW7mjRIEPGECo\n" +
                    "sexEgUMYQKFAJESBY8xBvQNgRIHGDkFQgAxEgs6WQbdsUkSC7ElC1eDhRIMIZEEtBy9EgyZyQIXS\n" +
                    "gkSDbT5BpccTRIOTZ0B56QxEg7DaQiFSbUSEAXBBmJfmRIQcD0GVouREhDbgQWaJpESEVBhBsHjj\n" +
                    "RISFPEGE16lEhLdBQdZU4USFQnpBVTPmRIVv9kGluPREhkauQJfb50SGrvZCtjV4RIbOHkK4HHJE\n" +
                    "hvN4Qa2xTESHFEZA78w4RIfm0kErCStEiB/pQOJxjESNkqpCAJwTRI21VEKgLIBEjdfGQiU6/kSP\n" +
                    "F/xC/pk/RI84IEHj3DhEkDdwQTmHUUSRcp5AlNqVRJGmKkC4ELdElW/NQSx+0USZe55AMSJsRJnz\n" +
                    "5EMPFLREmhphQVe6aESaNUBAOiO1RJsxdEDdQ/pEm7kLQEMPt0SdrGlBnc8lRJ3LiUH8qSZEnfZU\n" +
                    "QoHCPESeEY5BZdADRJ70OECMiVJEnyyYQMqLf0SgFdFAZ5C4RKBh0EBDGtNEoTqMQFjZrkSjuSBB\n" +
                    "KRw+RKeQhEDOiktEqHsYQO/qLESp0sVBCvF4RKqqE0AxDxZEqxJGQTjgg0Sr1PBADbA3RKwH/0Fu\n" +
                    "UhNErBxNQYcXlkStT7pBURLKRK1tQkFQGD5EsT9oQYZrKkSxZVlADRvqRLM76UGHdw9Es1T9QEHl\n" +
                    "5ESzudFBJMDVRLSVZ0BF5q9EthMdQOdf30S2NGlBFyNRRLe/hUDu8ddEuiG4QSNzqkS7e5tAqgNh\n" +
                    "RL+ga0Fo5QFEwTUzQXwKaETCMeBAuHvjRMMZ50Dl3W9ExJxZQFSBfkTE5I9AqhmyRMYDR0CYFsZE\n" +
                    "xkwXQX8kLUTHzSVAvF6gRMgBi0AelUhEyFeDQPkWKETIvJ9Ajut7RMoXCUE771lEylCtQKENVUTP\n" +
                    "HZZA8IkzRNDVEUBDSstE0SMVQLh4EkTRN89AqkLGRNINU0ECZN5E0oZqQEMDq0TUQJ5Agt/eRNRj\n" +
                    "dkHZRUhE1NHHQAzoFkTVVaFAeWI6RNZwjUAgHOhE1t4VQIKvI0TW+K9AnaxnRNe6gkBDU6dE1/O1\n" +
                    "QLw+m0TYRAZAeS3ARNovbUCwsu1E2wMWQObUFUTbUkJAoSaWRN2l50ERZJVE3lcxQEMHL0Tede9A\n" +
                    "VaisRN6s+kC6eKRE3w0KQENTHkTgSKNADU4wROBij0CNR6pE4LoRQHmtZETgzo5Aheb9RODxckGg\n" +
                    "JLZE4xB2QVi9NkTjQB1AjvYfROOUgUB5sRlE5A5nQHmpEkTlAO9AYImuROVr90CX5e9E5cNxQItu\n" +
                    "gkTmCidAen/rROZpgUFfgZVE6DkOQHov7UToWNVBDkbQROjx00AfkbZE6TsjQSu9akTqMLo/9cEi\n" +
                    "ROsbHkD4YctE6+8ZQKoOdkTsJnpAr9m7ROxa1UCYLSZE7LSrQLHIF0TtKR9AQwkvRO1KM0A1tRxE\n" +
                    "7Wr9QDE/2ETtk4lAzGdoRO6BDUFHE9NE7sayQJfUm0TvhbZAvAEeRO+310E2ztlE8B8dQSm3tkTw\n" +
                    "dhdAMZqnRPCc50CWiFRE8TLnQDEutUTxRlU/9j40RPHnUkBgnOpE8gQeQFT3yETyYjlAyDD/RPKT\n" +
                    "MkDvaRJE8q8+QJgIgETy4OFAZ/YSRPL9vUDMVA9E8xqqQDFI40TzOHZAec2GRPOTb0Cd3RtE86gv\n" +
                    "QIXsz0Tz29ZAYJAnRPP5ukEz941E9BnVP/yFxET0SI9AMT6lRPSIokAM66dE9KmdQMUs8UT1N2dA\n" +
                    "a5g5RPVZ/UD4md1E9Y/dQcVXakT1uj1AhElDRPXjaUENPvVE9oelQDFGTET237dA7/FwRPcIkUEa\n" +
                    "h0pE905DQVH3ekT3dAlAViJrRPeKZ0FgwcRE97oTQS42AUT34M5Bui+6RPgzNkE7PA9E+FvaQYsm\n" +
                    "TkT4fNdBLehQRPictkGPaXxE+MTVQTtbRET5BWtAheoqRPkfvUA1DWVE+Uy9QDIyPET5cRpBksCj\n" +
                    "RPmQVUDSy0ZE+bmyQGdQiUT5045AZ3QK</peaks>\n" +
                    "</scan>\n" +
                    "<ConditionedScan>\n" +
                    "<MeasuredSpectrum PrecursorCharge=\"0\" PrecursorMass=\"2192.253\" >\n" +
                    "<Peak massChargeRatio=\"324.828\" peak=\"0.03287\" />\n" +
                    "<Peak massChargeRatio=\"342.281\" peak=\"0.02224\" />\n" +
                    "<Peak massChargeRatio=\"346.284\" peak=\"0.02374\" />\n" +
                    "<Peak massChargeRatio=\"350.127\" peak=\"0.04067\" />\n" +
                    "<Peak massChargeRatio=\"362.193\" peak=\"0.03086\" />\n" +
                    "<Peak massChargeRatio=\"365.48\" peak=\"0.03511\" />\n" +
                    "<Peak massChargeRatio=\"366.164\" peak=\"0.03409\" />\n" +
                    "<Peak massChargeRatio=\"375.446\" peak=\"0.04938\" />\n" +
                    "<Peak massChargeRatio=\"377.443\" peak=\"0.03383\" />\n" +
                    "<Peak massChargeRatio=\"382.244\" peak=\"0.02516\" />\n" +
                    "<Peak massChargeRatio=\"403.227\" peak=\"0.04475\" />\n" +
                    "<Peak massChargeRatio=\"410.439\" peak=\"0.03347\" />\n" +
                    "<Peak massChargeRatio=\"413.241\" peak=\"0.0488\" />\n" +
                    "<Peak massChargeRatio=\"418.4\" peak=\"0.04656\" />\n" +
                    "<Peak massChargeRatio=\"420.299\" peak=\"0.02935\" />\n" +
                    "<Peak massChargeRatio=\"422.131\" peak=\"0.02443\" />\n" +
                    "<Peak massChargeRatio=\"424.871\" peak=\"0.01669\" />\n" +
                    "<Peak massChargeRatio=\"443.304\" peak=\"0.02562\" />\n" +
                    "<Peak massChargeRatio=\"453.255\" peak=\"0.02325\" />\n" +
                    "<Peak massChargeRatio=\"466.53\" peak=\"0.02085\" />\n" +
                    "<Peak massChargeRatio=\"468.263\" peak=\"0.01874\" />\n" +
                    "<Peak massChargeRatio=\"473.428\" peak=\"0.09922\" />\n" +
                    "<Peak massChargeRatio=\"481.215\" peak=\"0.016\" />\n" +
                    "<Peak massChargeRatio=\"482.742\" peak=\"0.04347\" />\n" +
                    "<Peak massChargeRatio=\"492.273\" peak=\"0.03466\" />\n" +
                    "<Peak massChargeRatio=\"493.525\" peak=\"0.05569\" />\n" +
                    "<Peak massChargeRatio=\"494.864\" peak=\"0.04048\" />\n" +
                    "<Peak massChargeRatio=\"497.91\" peak=\"0.04068\" />\n" +
                    "<Peak massChargeRatio=\"518.013\" peak=\"0.02789\" />\n" +
                    "<Peak massChargeRatio=\"522.267\" peak=\"0.03264\" />\n" +
                    "<Peak massChargeRatio=\"523.377\" peak=\"0.03144\" />\n" +
                    "<Peak massChargeRatio=\"526.639\" peak=\"0.04367\" />\n" +
                    "<Peak massChargeRatio=\"532.497\" peak=\"0.02516\" />\n" +
                    "<Peak massChargeRatio=\"541.37\" peak=\"0.02119\" />\n" +
                    "<Peak massChargeRatio=\"542.571\" peak=\"0.02214\" />\n" +
                    "<Peak massChargeRatio=\"545.226\" peak=\"0.02728\" />\n" +
                    "<Peak massChargeRatio=\"575.595\" peak=\"0.04354\" />\n" +
                    "<Peak massChargeRatio=\"580.769\" peak=\"0.02669\" />\n" +
                    "<Peak massChargeRatio=\"583.965\" peak=\"0.02068\" />\n" +
                    "<Peak massChargeRatio=\"587.258\" peak=\"0.0253\" />\n" +
                    "<Peak massChargeRatio=\"588.278\" peak=\"0.03264\" />\n" +
                    "<Peak massChargeRatio=\"600.369\" peak=\"0.0206\" />\n" +
                    "<Peak massChargeRatio=\"606.683\" peak=\"0.03005\" />\n" +
                    "<Peak massChargeRatio=\"611.404\" peak=\"0.02032\" />\n" +
                    "<Peak massChargeRatio=\"615.233\" peak=\"0.04062\" />\n" +
                    "<Peak massChargeRatio=\"624.158\" peak=\"0.01851\" />\n" +
                    "<Peak massChargeRatio=\"633.226\" peak=\"0.03065\" />\n" +
                    "<Peak massChargeRatio=\"634.259\" peak=\"0.0224\" />\n" +
                    "<Peak massChargeRatio=\"635.689\" peak=\"0.06194\" />\n" +
                    "<Peak massChargeRatio=\"641.196\" peak=\"0.05638\" />\n" +
                    "<Peak massChargeRatio=\"644.543\" peak=\"0.03109\" />\n" +
                    "<Peak massChargeRatio=\"651.021\" peak=\"0.04331\" />\n" +
                    "<Peak massChargeRatio=\"652.437\" peak=\"0.03336\" />\n" +
                    "<Peak massChargeRatio=\"659.331\" peak=\"0.05769\" />\n" +
                    "<Peak massChargeRatio=\"671.439\" peak=\"0.00786\" />\n" +
                    "<Peak massChargeRatio=\"675.37\" peak=\"0.05082\" />\n" +
                    "<Peak massChargeRatio=\"676.363\" peak=\"0.02398\" />\n" +
                    "<Peak massChargeRatio=\"679.751\" peak=\"0.01613\" />\n" +
                    "<Peak massChargeRatio=\"680.462\" peak=\"0.03578\" />\n" +
                    "<Peak massChargeRatio=\"683.474\" peak=\"0.0214\" />\n" +
                    "<Peak massChargeRatio=\"686.526\" peak=\"0.0159\" />\n" +
                    "<Peak massChargeRatio=\"688.581\" peak=\"0.03113\" />\n" +
                    "<Peak massChargeRatio=\"689.683\" peak=\"0.01459\" />\n" +
                    "<Peak massChargeRatio=\"695.679\" peak=\"0.01851\" />\n" +
                    "<Peak massChargeRatio=\"697.619\" peak=\"0.00681\" />\n" +
                    "<Peak massChargeRatio=\"699.297\" peak=\"0.0349\" />\n" +
                    "<Peak massChargeRatio=\"702.137\" peak=\"0.01754\" />\n" +
                    "<Peak massChargeRatio=\"704.469\" peak=\"0.02697\" />\n" +
                    "<Peak massChargeRatio=\"716.119\" peak=\"0.03086\" />\n" +
                    "<Peak massChargeRatio=\"728.091\" peak=\"0.03765\" />\n" +
                    "<Peak massChargeRatio=\"733.399\" peak=\"0.0171\" />\n" +
                    "<Peak massChargeRatio=\"734.606\" peak=\"0.06263\" />\n" +
                    "<Peak massChargeRatio=\"735.6\" peak=\"0.03507\" />\n" +
                    "<Peak massChargeRatio=\"738.271\" peak=\"0.01807\" />\n" +
                    "<Peak massChargeRatio=\"739.669\" peak=\"0.0626\" />\n" +
                    "<Peak massChargeRatio=\"741.844\" peak=\"0.0356\" />\n" +
                    "<Peak massChargeRatio=\"742.463\" peak=\"0.03235\" />\n" +
                    "<Peak massChargeRatio=\"751.38\" peak=\"0.03927\" />\n" +
                    "<Peak massChargeRatio=\"752.695\" peak=\"0.01944\" />\n" +
                    "<Peak massChargeRatio=\"754.238\" peak=\"0.03121\" />\n" +
                    "<Peak massChargeRatio=\"756.617\" peak=\"0.08796\" />\n" +
                    "<Peak massChargeRatio=\"757.442\" peak=\"0.0364\" />\n" +
                    "<Peak massChargeRatio=\"774.101\" peak=\"0.01848\" />\n" +
                    "<Peak massChargeRatio=\"774.707\" peak=\"0.00997\" />\n" +
                    "<Peak massChargeRatio=\"779.472\" peak=\"0.00598\" />\n" +
                    "<Peak massChargeRatio=\"782.445\" peak=\"0.00713\" />\n" +
                    "<Peak massChargeRatio=\"790.216\" peak=\"0.0159\" />\n" +
                    "<Peak massChargeRatio=\"792.195\" peak=\"0.02313\" />\n" +
                    "<Peak massChargeRatio=\"793.906\" peak=\"0.05731\" />\n" +
                    "<Peak massChargeRatio=\"797.072\" peak=\"0.04257\" />\n" +
                    "<Peak massChargeRatio=\"798.502\" peak=\"0.01232\" />\n" +
                    "<Peak massChargeRatio=\"800.672\" peak=\"0.00741\" />\n" +
                    "<Peak massChargeRatio=\"801.8\" peak=\"0.01433\" />\n" +
                    "<Peak massChargeRatio=\"805.568\" peak=\"0.06142\" />\n" +
                    "<Peak massChargeRatio=\"807.468\" peak=\"0.03948\" />\n" +
                    "<Peak massChargeRatio=\"809.686\" peak=\"0.00933\" />\n" +
                    "<Peak massChargeRatio=\"811.54\" peak=\"0.01165\" />\n" +
                    "<Peak massChargeRatio=\"816.582\" peak=\"0.00345\" />\n" +
                    "<Peak massChargeRatio=\"817.653\" peak=\"0.00454\" />\n" +
                    "<Peak massChargeRatio=\"824.569\" peak=\"0.00675\" />\n" +
                    "<Peak massChargeRatio=\"825.528\" peak=\"0.01189\" />\n" +
                    "<Peak massChargeRatio=\"829.221\" peak=\"0.00217\" />\n" +
                    "<Peak massChargeRatio=\"831.438\" peak=\"0.00978\" />\n" +
                    "<Peak massChargeRatio=\"833.666\" peak=\"0.00443\" />\n" +
                    "<Peak massChargeRatio=\"834.614\" peak=\"0.01706\" />\n" +
                    "<Peak massChargeRatio=\"855.63\" peak=\"0.03686\" />\n" +
                    "<Peak massChargeRatio=\"860.541\" peak=\"0.02978\" />\n" +
                    "<Peak massChargeRatio=\"861.53\" peak=\"0.01201\" />\n" +
                    "<Peak massChargeRatio=\"866.542\" peak=\"0.00942\" />\n" +
                    "<Peak massChargeRatio=\"867.574\" peak=\"0.01967\" />\n" +
                    "<Peak massChargeRatio=\"869.702\" peak=\"0.02916\" />\n" +
                    "<Peak massChargeRatio=\"870.567\" peak=\"0.01422\" />\n" +
                    "<Peak massChargeRatio=\"879.469\" peak=\"0.03101\" />\n" +
                    "<Peak massChargeRatio=\"883.551\" peak=\"0.01159\" />\n" +
                    "<Peak massChargeRatio=\"886.432\" peak=\"0.01851\" />\n" +
                    "<Peak massChargeRatio=\"888.309\" peak=\"0.0068\" />\n" +
                    "<Peak massChargeRatio=\"903.446\" peak=\"0.01114\" />\n" +
                    "<Peak massChargeRatio=\"909.71\" peak=\"0.04841\" />\n" +
                    "<Peak massChargeRatio=\"910.944\" peak=\"0.00129\" />\n" +
                    "<Peak massChargeRatio=\"918.504\" peak=\"0.03373\" />\n" +
                    "<Peak massChargeRatio=\"919.629\" peak=\"0.0061\" />\n" +
                    "<Peak massChargeRatio=\"922.871\" peak=\"0.00873\" />\n" +
                    "<Peak massChargeRatio=\"923.527\" peak=\"0.01115\" />\n" +
                    "<Peak massChargeRatio=\"927.857\" peak=\"0.0356\" />\n" +
                    "<Peak massChargeRatio=\"929.065\" peak=\"0.01511\" />\n" +
                    "<Peak massChargeRatio=\"930.005\" peak=\"0.01518\" />\n" +
                    "<Peak massChargeRatio=\"932.201\" peak=\"0.0281\" />\n" +
                    "<Peak massChargeRatio=\"941.628\" peak=\"0.00195\" />\n" +
                    "<Peak massChargeRatio=\"943.27\" peak=\"0.03435\" />\n" +
                    "<Peak massChargeRatio=\"944.525\" peak=\"0.00484\" />\n" +
                    "<Peak massChargeRatio=\"949.828\" peak=\"0.02199\" />\n" +
                    "<Peak massChargeRatio=\"952.62\" peak=\"0.01053\" />\n" +
                    "<Peak massChargeRatio=\"960.359\" peak=\"0.09535\" />\n" +
                    "<Peak massChargeRatio=\"961.939\" peak=\"0.00966\" />\n" +
                    "<Peak massChargeRatio=\"962.739\" peak=\"0.02483\" />\n" +
                    "<Peak massChargeRatio=\"965.421\" peak=\"0.00251\" />\n" +
                    "<Peak massChargeRatio=\"966.452\" peak=\"0.00333\" />\n" +
                    "<Peak massChargeRatio=\"980.654\" peak=\"0.07574\" />\n" +
                    "<Peak massChargeRatio=\"981.73\" peak=\"0.04382\" />\n" +
                    "<Peak massChargeRatio=\"984.43\" peak=\"0.01773\" />\n" +
                    "<Peak massChargeRatio=\"994.175\" peak=\"0.0105\" />\n" +
                    "<Peak massChargeRatio=\"997.37\" peak=\"0.00336\" />\n" +
                    "<Peak massChargeRatio=\"1010.222\" peak=\"0.01074\" />\n" +
                    "<Peak massChargeRatio=\"1012.947\" peak=\"0.03145\" />\n" +
                    "<Peak massChargeRatio=\"1017.514\" peak=\"0.01817\" />\n" +
                    "<Peak massChargeRatio=\"1025.673\" peak=\"0.00089\" />\n" +
                    "<Peak massChargeRatio=\"1029.438\" peak=\"0.05424\" />\n" +
                    "<Peak massChargeRatio=\"1030.178\" peak=\"0.01492\" />\n" +
                    "<Peak massChargeRatio=\"1032.472\" peak=\"0.00146\" />\n" +
                    "<Peak massChargeRatio=\"1034.097\" peak=\"0.0014\" />\n" +
                    "<Peak massChargeRatio=\"1035.119\" peak=\"0.03081\" />\n" +
                    "<Peak massChargeRatio=\"1038.189\" peak=\"0.01653\" />\n" +
                    "<Peak massChargeRatio=\"1046.456\" peak=\"0.02198\" />\n" +
                    "<Peak massChargeRatio=\"1047.384\" peak=\"0.07458\" />\n" +
                    "<Peak massChargeRatio=\"1048.262\" peak=\"0.00816\" />\n" +
                    "<Peak massChargeRatio=\"1051.414\" peak=\"0.02008\" />\n" +
                    "<Peak massChargeRatio=\"1053.527\" peak=\"0.03705\" />\n" +
                    "<Peak massChargeRatio=\"1056.045\" peak=\"0.01834\" />\n" +
                    "<Peak massChargeRatio=\"1056.877\" peak=\"0.01794\" />\n" +
                    "<Peak massChargeRatio=\"1057.715\" peak=\"0.02144\" />\n" +
                    "<Peak massChargeRatio=\"1060.164\" peak=\"0.01558\" />\n" +
                    "<Peak massChargeRatio=\"1061.727\" peak=\"0.02672\" />\n" +
                    "<Peak massChargeRatio=\"1066.077\" peak=\"0.01325\" />\n" +
                    "<Peak massChargeRatio=\"1067.499\" peak=\"0.02175\" />\n" +
                    "<Peak massChargeRatio=\"1074.209\" peak=\"0.00002\" />\n" +
                    "<Peak massChargeRatio=\"1077.468\" peak=\"0.06984\" />\n" +
                    "<Peak massChargeRatio=\"1078.441\" peak=\"0.07031\" />\n" +
                    "<Peak massChargeRatio=\"1079.608\" peak=\"0.02454\" />\n" +
                    "<Peak massChargeRatio=\"1080.634\" peak=\"0.00745\" />\n" +
                    "<Peak massChargeRatio=\"1087.213\" peak=\"0.01058\" />\n" +
                    "<Peak massChargeRatio=\"1088.997\" peak=\"0.00549\" />\n" +
                    "<Peak massChargeRatio=\"1132.583\" peak=\"0.04446\" />\n" +
                    "<Peak massChargeRatio=\"1133.667\" peak=\"0.07539\" />\n" +
                    "<Peak massChargeRatio=\"1134.743\" peak=\"0.0516\" />\n" +
                    "<Peak massChargeRatio=\"1144.75\" peak=\"0.09829\" />\n" +
                    "<Peak massChargeRatio=\"1145.754\" peak=\"0.04162\" />\n" +
                    "<Peak massChargeRatio=\"1153.732\" peak=\"0.02339\" />\n" +
                    "<Peak massChargeRatio=\"1163.582\" peak=\"0.01161\" />\n" +
                    "<Peak massChargeRatio=\"1165.193\" peak=\"0.01389\" />\n" +
                    "<Peak massChargeRatio=\"1195.494\" peak=\"0.02421\" />\n" +
                    "<Peak massChargeRatio=\"1227.863\" peak=\"0.00649\" />\n" +
                    "<Peak massChargeRatio=\"1231.622\" peak=\"0.10335\" />\n" +
                    "<Peak massChargeRatio=\"1232.824\" peak=\"0.02514\" />\n" +
                    "<Peak massChargeRatio=\"1233.664\" peak=\"0.00631\" />\n" +
                    "<Peak massChargeRatio=\"1241.545\" peak=\"0.01408\" />\n" +
                    "<Peak massChargeRatio=\"1245.783\" peak=\"0.00605\" />\n" +
                    "<Peak massChargeRatio=\"1261.388\" peak=\"0.03005\" />\n" +
                    "<Peak massChargeRatio=\"1262.36\" peak=\"0.04117\" />\n" +
                    "<Peak massChargeRatio=\"1263.698\" peak=\"0.06414\" />\n" +
                    "<Peak massChargeRatio=\"1264.549\" peak=\"0.0239\" />\n" +
                    "<Peak massChargeRatio=\"1271.632\" peak=\"0.00792\" />\n" +
                    "<Peak massChargeRatio=\"1273.394\" peak=\"0.01188\" />\n" +
                    "<Peak massChargeRatio=\"1280.682\" peak=\"0.00635\" />\n" +
                    "<Peak massChargeRatio=\"1283.057\" peak=\"0.0077\" />\n" +
                    "<Peak massChargeRatio=\"1289.83\" peak=\"0.04197\" />\n" +
                    "<Peak massChargeRatio=\"1309.785\" peak=\"0.07713\" />\n" +
                    "<Peak massChargeRatio=\"1340.516\" peak=\"0.05456\" />\n" +
                    "<Peak massChargeRatio=\"1347.847\" peak=\"0.05997\" />\n" +
                    "<Peak massChargeRatio=\"1358.587\" peak=\"0.0657\" />\n" +
                    "<Peak massChargeRatio=\"1365.315\" peak=\"0.0322\" />\n" +
                    "<Peak massChargeRatio=\"1368.571\" peak=\"0.07768\" />\n" +
                    "<Peak massChargeRatio=\"1374.654\" peak=\"0.02455\" />\n" +
                    "<Peak massChargeRatio=\"1376.25\" peak=\"0.09644\" />\n" +
                    "<Peak massChargeRatio=\"1386.491\" peak=\"0.07988\" />\n" +
                    "<Peak massChargeRatio=\"1387.414\" peak=\"0.07884\" />\n" +
                    "<Peak massChargeRatio=\"1417.981\" peak=\"0.0927\" />\n" +
                    "<Peak massChargeRatio=\"1419.167\" peak=\"0.02209\" />\n" +
                    "<Peak massChargeRatio=\"1433.872\" peak=\"0.09635\" />\n" +
                    "<Peak massChargeRatio=\"1434.656\" peak=\"0.03123\" />\n" +
                    "<Peak massChargeRatio=\"1437.807\" peak=\"0.07434\" />\n" +
                    "<Peak massChargeRatio=\"1444.669\" peak=\"0.03376\" />\n" +
                    "<Peak massChargeRatio=\"1456.597\" peak=\"0.05808\" />\n" +
                    "<Peak massChargeRatio=\"1457.638\" peak=\"0.06863\" />\n" +
                    "<Peak massChargeRatio=\"1469.985\" peak=\"0.0618\" />\n" +
                    "<Peak massChargeRatio=\"1489.054\" peak=\"0.07792\" />\n" +
                    "<Peak massChargeRatio=\"1499.863\" peak=\"0.05272\" />\n" +
                    "<Peak massChargeRatio=\"1533.013\" peak=\"0.07285\" />\n" +
                    "<Peak massChargeRatio=\"1545.662\" peak=\"0.07536\" />\n" +
                    "<Peak massChargeRatio=\"1553.559\" peak=\"0.03988\" />\n" +
                    "<Peak massChargeRatio=\"1560.809\" peak=\"0.04508\" />\n" +
                    "<Peak massChargeRatio=\"1572.886\" peak=\"0.02418\" />\n" +
                    "<Peak massChargeRatio=\"1575.142\" peak=\"0.03465\" />\n" +
                    "<Peak massChargeRatio=\"1584.102\" peak=\"0.03352\" />\n" +
                    "<Peak massChargeRatio=\"1586.378\" peak=\"0.07278\" />\n" +
                    "<Peak massChargeRatio=\"1598.411\" peak=\"0.0405\" />\n" +
                    "<Peak massChargeRatio=\"1600.048\" peak=\"0.02204\" />\n" +
                    "<Peak massChargeRatio=\"1602.735\" peak=\"0.04837\" />\n" +
                    "<Peak massChargeRatio=\"1605.894\" peak=\"0.03469\" />\n" +
                    "<Peak massChargeRatio=\"1616.72\" peak=\"0.06311\" />\n" +
                    "<Peak massChargeRatio=\"1618.521\" peak=\"0.03747\" />\n" +
                    "<Peak massChargeRatio=\"1656.925\" peak=\"0.04731\" />\n" +
                    "<Peak massChargeRatio=\"1670.658\" peak=\"0.02585\" />\n" +
                    "<Peak massChargeRatio=\"1673.096\" peak=\"0.04001\" />\n" +
                    "<Peak massChargeRatio=\"1673.744\" peak=\"0.03797\" />\n" +
                    "<Peak massChargeRatio=\"1680.416\" peak=\"0.04745\" />\n" +
                    "<Peak massChargeRatio=\"1684.2\" peak=\"0.02343\" />\n" +
                    "<Peak massChargeRatio=\"1698.019\" peak=\"0.02852\" />\n" +
                    "<Peak massChargeRatio=\"1699.108\" peak=\"0.09757\" />\n" +
                    "<Peak massChargeRatio=\"1702.556\" peak=\"0.0158\" />\n" +
                    "<Peak massChargeRatio=\"1706.676\" peak=\"0.02554\" />\n" +
                    "<Peak massChargeRatio=\"1715.517\" peak=\"0.01816\" />\n" +
                    "<Peak massChargeRatio=\"1718.94\" peak=\"0.02767\" />\n" +
                    "<Peak massChargeRatio=\"1719.771\" peak=\"0.03197\" />\n" +
                    "<Peak massChargeRatio=\"1725.828\" peak=\"0.02305\" />\n" +
                    "<Peak massChargeRatio=\"1727.616\" peak=\"0.03774\" />\n" +
                    "<Peak massChargeRatio=\"1730.126\" peak=\"0.02651\" />\n" +
                    "<Peak massChargeRatio=\"1745.482\" peak=\"0.03339\" />\n" +
                    "<Peak massChargeRatio=\"1752.096\" peak=\"0.04088\" />\n" +
                    "<Peak massChargeRatio=\"1754.571\" peak=\"0.03187\" />\n" +
                    "<Peak massChargeRatio=\"1773.184\" peak=\"0.05161\" />\n" +
                    "<Peak massChargeRatio=\"1778.725\" peak=\"0.02363\" />\n" +
                    "<Peak massChargeRatio=\"1779.685\" peak=\"0.02548\" />\n" +
                    "<Peak massChargeRatio=\"1781.406\" peak=\"0.03957\" />\n" +
                    "<Peak massChargeRatio=\"1784.407\" peak=\"0.02368\" />\n" +
                    "<Peak massChargeRatio=\"1794.27\" peak=\"0.0137\" />\n" +
                    "<Peak massChargeRatio=\"1795.08\" peak=\"0.02768\" />\n" +
                    "<Peak massChargeRatio=\"1797.815\" peak=\"0.02733\" />\n" +
                    "<Peak massChargeRatio=\"1799.545\" peak=\"0.08251\" />\n" +
                    "<Peak massChargeRatio=\"1816.514\" peak=\"0.06249\" />\n" +
                    "<Peak massChargeRatio=\"1818.004\" peak=\"0.02688\" />\n" +
                    "<Peak massChargeRatio=\"1820.641\" peak=\"0.02374\" />\n" +
                    "<Peak massChargeRatio=\"1824.45\" peak=\"0.02451\" />\n" +
                    "<Peak massChargeRatio=\"1832.029\" peak=\"0.02351\" />\n" +
                    "<Peak massChargeRatio=\"1835.374\" peak=\"0.03116\" />\n" +
                    "<Peak massChargeRatio=\"1838.108\" peak=\"0.02819\" />\n" +
                    "<Peak massChargeRatio=\"1840.317\" peak=\"0.0248\" />\n" +
                    "<Peak massChargeRatio=\"1843.297\" peak=\"0.06393\" />\n" +
                    "<Peak massChargeRatio=\"1857.783\" peak=\"0.02376\" />\n" +
                    "<Peak massChargeRatio=\"1858.776\" peak=\"0.0458\" />\n" +
                    "<Peak massChargeRatio=\"1863.557\" peak=\"0.01313\" />\n" +
                    "<Peak massChargeRatio=\"1865.848\" peak=\"0.05135\" />\n" +
                    "<Peak massChargeRatio=\"1873.523\" peak=\"0.00842\" />\n" +
                    "<Peak massChargeRatio=\"1880.847\" peak=\"0.03879\" />\n" +
                    "<Peak massChargeRatio=\"1887.472\" peak=\"0.02834\" />\n" +
                    "<Peak massChargeRatio=\"1889.202\" peak=\"0.02921\" />\n" +
                    "<Peak massChargeRatio=\"1890.839\" peak=\"0.02512\" />\n" +
                    "<Peak massChargeRatio=\"1893.646\" peak=\"0.02934\" />\n" +
                    "<Peak massChargeRatio=\"1897.285\" peak=\"0.01383\" />\n" +
                    "<Peak massChargeRatio=\"1898.319\" peak=\"0.01224\" />\n" +
                    "<Peak massChargeRatio=\"1899.343\" peak=\"0.01065\" />\n" +
                    "<Peak massChargeRatio=\"1900.61\" peak=\"0.03013\" />\n" +
                    "<Peak massChargeRatio=\"1908.033\" peak=\"0.04964\" />\n" +
                    "<Peak massChargeRatio=\"1910.209\" peak=\"0.02053\" />\n" +
                    "<Peak massChargeRatio=\"1916.178\" peak=\"0.02327\" />\n" +
                    "<Peak massChargeRatio=\"1917.745\" peak=\"0.04454\" />\n" +
                    "<Peak massChargeRatio=\"1920.972\" peak=\"0.04174\" />\n" +
                    "<Peak massChargeRatio=\"1923.69\" peak=\"0.00559\" />\n" +
                    "<Peak massChargeRatio=\"1924.903\" peak=\"0.01665\" />\n" +
                    "<Peak massChargeRatio=\"1929.591\" peak=\"0.00185\" />\n" +
                    "<Peak massChargeRatio=\"1935.229\" peak=\"0.00216\" />\n" +
                    "<Peak massChargeRatio=\"1936.129\" peak=\"0.00027\" />\n" +
                    "<Peak massChargeRatio=\"1939.069\" peak=\"0.01198\" />\n" +
                    "<Peak massChargeRatio=\"1940.6\" peak=\"0.0169\" />\n" +
                    "<Peak massChargeRatio=\"1941.476\" peak=\"0.00477\" />\n" +
                    "<Peak massChargeRatio=\"1943.929\" peak=\"0.01212\" />\n" +
                    "<Peak massChargeRatio=\"1948.607\" peak=\"0.00284\" />\n" +
                    "<Peak massChargeRatio=\"1951.804\" peak=\"0.03034\" />\n" +
                    "<Peak massChargeRatio=\"1957.3\" peak=\"0.01054\" />\n" +
                    "<Peak massChargeRatio=\"1962.812\" peak=\"0.01966\" />\n" +
                    "<Peak massChargeRatio=\"1964.496\" peak=\"0.06918\" />\n" +
                    "<Peak massChargeRatio=\"1965.82\" peak=\"0.00253\" />\n" +
                    "<Peak massChargeRatio=\"1967.107\" peak=\"0.02473\" />\n" +
                    "<Peak massChargeRatio=\"1974.991\" peak=\"0.02304\" />\n" +
                    "<Peak massChargeRatio=\"1976.268\" peak=\"0.03172\" />\n" +
                    "<Peak massChargeRatio=\"1978.446\" peak=\"0.04341\" />\n" +
                    "<Peak massChargeRatio=\"1979.626\" peak=\"0.00304\" />\n" +
                    "<Peak massChargeRatio=\"1980.325\" peak=\"0.04709\" />\n" +
                    "<Peak massChargeRatio=\"1981.815\" peak=\"0.03699\" />\n" +
                    "<Peak massChargeRatio=\"1983.025\" peak=\"0.07163\" />\n" +
                    "<Peak massChargeRatio=\"1985.6\" peak=\"0.04016\" />\n" +
                    "<Peak massChargeRatio=\"1986.87\" peak=\"0.05793\" />\n" +
                    "<Peak massChargeRatio=\"1987.901\" peak=\"0.03807\" />\n" +
                    "<Peak massChargeRatio=\"1988.897\" peak=\"0.05937\" />\n" +
                    "<Peak massChargeRatio=\"1990.151\" peak=\"0.04176\" />\n" +
                    "<Peak massChargeRatio=\"1992.169\" peak=\"0.01214\" />\n" +
                    "<Peak massChargeRatio=\"1992.992\" peak=\"0.00387\" />\n" +
                    "<Peak massChargeRatio=\"1994.398\" peak=\"0.00471\" />\n" +
                    "<Peak massChargeRatio=\"1995.534\" peak=\"0.06477\" />\n" +
                    "<Peak massChargeRatio=\"1996.51\" peak=\"0.0263\" />\n" +
                    "<Peak massChargeRatio=\"1997.803\" peak=\"0.01134\" />\n" +
                    "</MeasuredSpectrum>\n" +
                    "</ConditionedScan>\n" +
                    "<NormalizedRawScan>\n" +
                    "<MeasuredSpectrum PrecursorCharge=\"0\" PrecursorMass=\"2192.253\" >\n" +
                    "<Peak massChargeRatio=\"324.828\" peak=\"0.03287\" />\n" +
                    "<Peak massChargeRatio=\"342.281\" peak=\"0.02224\" />\n" +
                    "<Peak massChargeRatio=\"346.284\" peak=\"0.02374\" />\n" +
                    "<Peak massChargeRatio=\"350.127\" peak=\"0.04067\" />\n" +
                    "<Peak massChargeRatio=\"362.193\" peak=\"0.03086\" />\n" +
                    "<Peak massChargeRatio=\"365.48\" peak=\"0.03511\" />\n" +
                    "<Peak massChargeRatio=\"366.164\" peak=\"0.03409\" />\n" +
                    "<Peak massChargeRatio=\"375.446\" peak=\"0.04938\" />\n" +
                    "<Peak massChargeRatio=\"377.443\" peak=\"0.03383\" />\n" +
                    "<Peak massChargeRatio=\"382.244\" peak=\"0.02516\" />\n" +
                    "<Peak massChargeRatio=\"403.227\" peak=\"0.04475\" />\n" +
                    "<Peak massChargeRatio=\"410.439\" peak=\"0.03347\" />\n" +
                    "<Peak massChargeRatio=\"413.241\" peak=\"0.0488\" />\n" +
                    "<Peak massChargeRatio=\"418.4\" peak=\"0.04656\" />\n" +
                    "<Peak massChargeRatio=\"420.299\" peak=\"0.02935\" />\n" +
                    "<Peak massChargeRatio=\"422.131\" peak=\"0.02443\" />\n" +
                    "<Peak massChargeRatio=\"424.871\" peak=\"0.01669\" />\n" +
                    "<Peak massChargeRatio=\"443.304\" peak=\"0.02562\" />\n" +
                    "<Peak massChargeRatio=\"453.255\" peak=\"0.02325\" />\n" +
                    "<Peak massChargeRatio=\"466.53\" peak=\"0.02085\" />\n" +
                    "<Peak massChargeRatio=\"468.263\" peak=\"0.01874\" />\n" +
                    "<Peak massChargeRatio=\"473.428\" peak=\"0.09922\" />\n" +
                    "<Peak massChargeRatio=\"481.215\" peak=\"0.016\" />\n" +
                    "<Peak massChargeRatio=\"482.742\" peak=\"0.04347\" />\n" +
                    "<Peak massChargeRatio=\"492.273\" peak=\"0.03466\" />\n" +
                    "<Peak massChargeRatio=\"493.525\" peak=\"0.05569\" />\n" +
                    "<Peak massChargeRatio=\"494.864\" peak=\"0.04048\" />\n" +
                    "<Peak massChargeRatio=\"497.91\" peak=\"0.04068\" />\n" +
                    "<Peak massChargeRatio=\"518.013\" peak=\"0.02789\" />\n" +
                    "<Peak massChargeRatio=\"522.267\" peak=\"0.03264\" />\n" +
                    "<Peak massChargeRatio=\"523.377\" peak=\"0.03144\" />\n" +
                    "<Peak massChargeRatio=\"526.639\" peak=\"0.04367\" />\n" +
                    "<Peak massChargeRatio=\"532.497\" peak=\"0.02516\" />\n" +
                    "<Peak massChargeRatio=\"541.37\" peak=\"0.02119\" />\n" +
                    "<Peak massChargeRatio=\"542.571\" peak=\"0.02214\" />\n" +
                    "<Peak massChargeRatio=\"545.226\" peak=\"0.02728\" />\n" +
                    "<Peak massChargeRatio=\"575.595\" peak=\"0.04354\" />\n" +
                    "<Peak massChargeRatio=\"580.769\" peak=\"0.02669\" />\n" +
                    "<Peak massChargeRatio=\"583.965\" peak=\"0.02068\" />\n" +
                    "<Peak massChargeRatio=\"587.258\" peak=\"0.0253\" />\n" +
                    "<Peak massChargeRatio=\"588.278\" peak=\"0.03264\" />\n" +
                    "<Peak massChargeRatio=\"600.369\" peak=\"0.0206\" />\n" +
                    "<Peak massChargeRatio=\"606.683\" peak=\"0.03005\" />\n" +
                    "<Peak massChargeRatio=\"611.404\" peak=\"0.02032\" />\n" +
                    "<Peak massChargeRatio=\"615.233\" peak=\"0.04062\" />\n" +
                    "<Peak massChargeRatio=\"624.158\" peak=\"0.01851\" />\n" +
                    "<Peak massChargeRatio=\"633.226\" peak=\"0.03065\" />\n" +
                    "<Peak massChargeRatio=\"634.259\" peak=\"0.0224\" />\n" +
                    "<Peak massChargeRatio=\"635.689\" peak=\"0.06194\" />\n" +
                    "<Peak massChargeRatio=\"641.196\" peak=\"0.05638\" />\n" +
                    "<Peak massChargeRatio=\"644.543\" peak=\"0.03109\" />\n" +
                    "<Peak massChargeRatio=\"651.021\" peak=\"0.04331\" />\n" +
                    "<Peak massChargeRatio=\"652.437\" peak=\"0.03336\" />\n" +
                    "<Peak massChargeRatio=\"659.331\" peak=\"0.05769\" />\n" +
                    "<Peak massChargeRatio=\"671.439\" peak=\"0.00786\" />\n" +
                    "<Peak massChargeRatio=\"675.37\" peak=\"0.05082\" />\n" +
                    "<Peak massChargeRatio=\"676.363\" peak=\"0.02398\" />\n" +
                    "<Peak massChargeRatio=\"679.751\" peak=\"0.01613\" />\n" +
                    "<Peak massChargeRatio=\"680.462\" peak=\"0.03578\" />\n" +
                    "<Peak massChargeRatio=\"683.474\" peak=\"0.0214\" />\n" +
                    "<Peak massChargeRatio=\"686.526\" peak=\"0.0159\" />\n" +
                    "<Peak massChargeRatio=\"688.581\" peak=\"0.03113\" />\n" +
                    "<Peak massChargeRatio=\"689.683\" peak=\"0.01459\" />\n" +
                    "<Peak massChargeRatio=\"695.679\" peak=\"0.01851\" />\n" +
                    "<Peak massChargeRatio=\"697.619\" peak=\"0.00681\" />\n" +
                    "<Peak massChargeRatio=\"699.297\" peak=\"0.0349\" />\n" +
                    "<Peak massChargeRatio=\"702.137\" peak=\"0.01754\" />\n" +
                    "<Peak massChargeRatio=\"704.469\" peak=\"0.02697\" />\n" +
                    "<Peak massChargeRatio=\"716.119\" peak=\"0.03086\" />\n" +
                    "<Peak massChargeRatio=\"728.091\" peak=\"0.03765\" />\n" +
                    "<Peak massChargeRatio=\"733.399\" peak=\"0.0171\" />\n" +
                    "<Peak massChargeRatio=\"734.606\" peak=\"0.06263\" />\n" +
                    "<Peak massChargeRatio=\"735.6\" peak=\"0.03507\" />\n" +
                    "<Peak massChargeRatio=\"738.271\" peak=\"0.01807\" />\n" +
                    "<Peak massChargeRatio=\"739.669\" peak=\"0.0626\" />\n" +
                    "<Peak massChargeRatio=\"741.844\" peak=\"0.0356\" />\n" +
                    "<Peak massChargeRatio=\"742.463\" peak=\"0.03235\" />\n" +
                    "<Peak massChargeRatio=\"751.38\" peak=\"0.03927\" />\n" +
                    "<Peak massChargeRatio=\"752.695\" peak=\"0.01944\" />\n" +
                    "<Peak massChargeRatio=\"754.238\" peak=\"0.03121\" />\n" +
                    "<Peak massChargeRatio=\"756.617\" peak=\"0.08796\" />\n" +
                    "<Peak massChargeRatio=\"757.442\" peak=\"0.0364\" />\n" +
                    "<Peak massChargeRatio=\"774.101\" peak=\"0.01848\" />\n" +
                    "<Peak massChargeRatio=\"774.707\" peak=\"0.00997\" />\n" +
                    "<Peak massChargeRatio=\"779.472\" peak=\"0.00598\" />\n" +
                    "<Peak massChargeRatio=\"782.445\" peak=\"0.00713\" />\n" +
                    "<Peak massChargeRatio=\"790.216\" peak=\"0.0159\" />\n" +
                    "<Peak massChargeRatio=\"792.195\" peak=\"0.02313\" />\n" +
                    "<Peak massChargeRatio=\"793.906\" peak=\"0.05731\" />\n" +
                    "<Peak massChargeRatio=\"797.072\" peak=\"0.04257\" />\n" +
                    "<Peak massChargeRatio=\"798.502\" peak=\"0.01232\" />\n" +
                    "<Peak massChargeRatio=\"800.672\" peak=\"0.00741\" />\n" +
                    "<Peak massChargeRatio=\"801.8\" peak=\"0.01433\" />\n" +
                    "<Peak massChargeRatio=\"805.568\" peak=\"0.06142\" />\n" +
                    "<Peak massChargeRatio=\"807.468\" peak=\"0.03948\" />\n" +
                    "<Peak massChargeRatio=\"809.686\" peak=\"0.00933\" />\n" +
                    "<Peak massChargeRatio=\"811.54\" peak=\"0.01165\" />\n" +
                    "<Peak massChargeRatio=\"816.582\" peak=\"0.00345\" />\n" +
                    "<Peak massChargeRatio=\"817.653\" peak=\"0.00454\" />\n" +
                    "<Peak massChargeRatio=\"824.569\" peak=\"0.00675\" />\n" +
                    "<Peak massChargeRatio=\"825.528\" peak=\"0.01189\" />\n" +
                    "<Peak massChargeRatio=\"829.221\" peak=\"0.00217\" />\n" +
                    "<Peak massChargeRatio=\"831.438\" peak=\"0.00978\" />\n" +
                    "<Peak massChargeRatio=\"833.666\" peak=\"0.00443\" />\n" +
                    "<Peak massChargeRatio=\"834.614\" peak=\"0.01706\" />\n" +
                    "<Peak massChargeRatio=\"855.63\" peak=\"0.03686\" />\n" +
                    "<Peak massChargeRatio=\"860.541\" peak=\"0.02978\" />\n" +
                    "<Peak massChargeRatio=\"861.53\" peak=\"0.01201\" />\n" +
                    "<Peak massChargeRatio=\"866.542\" peak=\"0.00942\" />\n" +
                    "<Peak massChargeRatio=\"867.574\" peak=\"0.01967\" />\n" +
                    "<Peak massChargeRatio=\"869.702\" peak=\"0.02916\" />\n" +
                    "<Peak massChargeRatio=\"870.567\" peak=\"0.01422\" />\n" +
                    "<Peak massChargeRatio=\"879.469\" peak=\"0.03101\" />\n" +
                    "<Peak massChargeRatio=\"883.551\" peak=\"0.01159\" />\n" +
                    "<Peak massChargeRatio=\"886.432\" peak=\"0.01851\" />\n" +
                    "<Peak massChargeRatio=\"888.309\" peak=\"0.0068\" />\n" +
                    "<Peak massChargeRatio=\"903.446\" peak=\"0.01114\" />\n" +
                    "<Peak massChargeRatio=\"909.71\" peak=\"0.04841\" />\n" +
                    "<Peak massChargeRatio=\"910.944\" peak=\"0.00129\" />\n" +
                    "<Peak massChargeRatio=\"918.504\" peak=\"0.03373\" />\n" +
                    "<Peak massChargeRatio=\"919.629\" peak=\"0.0061\" />\n" +
                    "<Peak massChargeRatio=\"922.871\" peak=\"0.00873\" />\n" +
                    "<Peak massChargeRatio=\"923.527\" peak=\"0.01115\" />\n" +
                    "<Peak massChargeRatio=\"927.857\" peak=\"0.0356\" />\n" +
                    "<Peak massChargeRatio=\"929.065\" peak=\"0.01511\" />\n" +
                    "<Peak massChargeRatio=\"930.005\" peak=\"0.01518\" />\n" +
                    "<Peak massChargeRatio=\"932.201\" peak=\"0.0281\" />\n" +
                    "<Peak massChargeRatio=\"941.628\" peak=\"0.00195\" />\n" +
                    "<Peak massChargeRatio=\"943.27\" peak=\"0.03435\" />\n" +
                    "<Peak massChargeRatio=\"944.525\" peak=\"0.00484\" />\n" +
                    "<Peak massChargeRatio=\"949.828\" peak=\"0.02199\" />\n" +
                    "<Peak massChargeRatio=\"952.62\" peak=\"0.01053\" />\n" +
                    "<Peak massChargeRatio=\"960.359\" peak=\"0.09535\" />\n" +
                    "<Peak massChargeRatio=\"961.939\" peak=\"0.00966\" />\n" +
                    "<Peak massChargeRatio=\"962.739\" peak=\"0.02483\" />\n" +
                    "<Peak massChargeRatio=\"965.421\" peak=\"0.00251\" />\n" +
                    "<Peak massChargeRatio=\"966.452\" peak=\"0.00333\" />\n" +
                    "<Peak massChargeRatio=\"980.654\" peak=\"0.07574\" />\n" +
                    "<Peak massChargeRatio=\"981.73\" peak=\"0.04382\" />\n" +
                    "<Peak massChargeRatio=\"984.43\" peak=\"0.01773\" />\n" +
                    "<Peak massChargeRatio=\"994.175\" peak=\"0.0105\" />\n" +
                    "<Peak massChargeRatio=\"997.37\" peak=\"0.00336\" />\n" +
                    "<Peak massChargeRatio=\"1010.222\" peak=\"0.01074\" />\n" +
                    "<Peak massChargeRatio=\"1012.947\" peak=\"0.03145\" />\n" +
                    "<Peak massChargeRatio=\"1017.514\" peak=\"0.01817\" />\n" +
                    "<Peak massChargeRatio=\"1025.673\" peak=\"0.00089\" />\n" +
                    "<Peak massChargeRatio=\"1029.438\" peak=\"0.05424\" />\n" +
                    "<Peak massChargeRatio=\"1030.178\" peak=\"0.01492\" />\n" +
                    "<Peak massChargeRatio=\"1032.472\" peak=\"0.00146\" />\n" +
                    "<Peak massChargeRatio=\"1034.097\" peak=\"0.0014\" />\n" +
                    "<Peak massChargeRatio=\"1035.119\" peak=\"0.03081\" />\n" +
                    "<Peak massChargeRatio=\"1038.189\" peak=\"0.01653\" />\n" +
                    "<Peak massChargeRatio=\"1046.456\" peak=\"0.02198\" />\n" +
                    "<Peak massChargeRatio=\"1047.384\" peak=\"0.07458\" />\n" +
                    "<Peak massChargeRatio=\"1048.262\" peak=\"0.00816\" />\n" +
                    "<Peak massChargeRatio=\"1051.414\" peak=\"0.02008\" />\n" +
                    "<Peak massChargeRatio=\"1053.527\" peak=\"0.03705\" />\n" +
                    "<Peak massChargeRatio=\"1056.045\" peak=\"0.01834\" />\n" +
                    "<Peak massChargeRatio=\"1056.877\" peak=\"0.01794\" />\n" +
                    "<Peak massChargeRatio=\"1057.715\" peak=\"0.02144\" />\n" +
                    "<Peak massChargeRatio=\"1060.164\" peak=\"0.01558\" />\n" +
                    "<Peak massChargeRatio=\"1061.727\" peak=\"0.02672\" />\n" +
                    "<Peak massChargeRatio=\"1066.077\" peak=\"0.01325\" />\n" +
                    "<Peak massChargeRatio=\"1067.499\" peak=\"0.02175\" />\n" +
                    "<Peak massChargeRatio=\"1074.209\" peak=\"0.00002\" />\n" +
                    "<Peak massChargeRatio=\"1077.468\" peak=\"0.06984\" />\n" +
                    "<Peak massChargeRatio=\"1078.441\" peak=\"0.07031\" />\n" +
                    "<Peak massChargeRatio=\"1079.608\" peak=\"0.02454\" />\n" +
                    "<Peak massChargeRatio=\"1080.634\" peak=\"0.00745\" />\n" +
                    "<Peak massChargeRatio=\"1087.213\" peak=\"0.01058\" />\n" +
                    "<Peak massChargeRatio=\"1088.997\" peak=\"0.00549\" />\n" +
                    "<Peak massChargeRatio=\"1132.583\" peak=\"0.04446\" />\n" +
                    "<Peak massChargeRatio=\"1133.667\" peak=\"0.07539\" />\n" +
                    "<Peak massChargeRatio=\"1134.743\" peak=\"0.0516\" />\n" +
                    "<Peak massChargeRatio=\"1144.75\" peak=\"0.09829\" />\n" +
                    "<Peak massChargeRatio=\"1145.754\" peak=\"0.04162\" />\n" +
                    "<Peak massChargeRatio=\"1153.732\" peak=\"0.02339\" />\n" +
                    "<Peak massChargeRatio=\"1163.582\" peak=\"0.01161\" />\n" +
                    "<Peak massChargeRatio=\"1165.193\" peak=\"0.01389\" />\n" +
                    "<Peak massChargeRatio=\"1195.494\" peak=\"0.02421\" />\n" +
                    "<Peak massChargeRatio=\"1227.863\" peak=\"0.00649\" />\n" +
                    "<Peak massChargeRatio=\"1231.622\" peak=\"0.10335\" />\n" +
                    "<Peak massChargeRatio=\"1232.824\" peak=\"0.02514\" />\n" +
                    "<Peak massChargeRatio=\"1233.664\" peak=\"0.00631\" />\n" +
                    "<Peak massChargeRatio=\"1241.545\" peak=\"0.01408\" />\n" +
                    "<Peak massChargeRatio=\"1245.783\" peak=\"0.00605\" />\n" +
                    "<Peak massChargeRatio=\"1261.388\" peak=\"0.03005\" />\n" +
                    "<Peak massChargeRatio=\"1262.36\" peak=\"0.04117\" />\n" +
                    "<Peak massChargeRatio=\"1263.698\" peak=\"0.06414\" />\n" +
                    "<Peak massChargeRatio=\"1264.549\" peak=\"0.0239\" />\n" +
                    "<Peak massChargeRatio=\"1271.632\" peak=\"0.00792\" />\n" +
                    "<Peak massChargeRatio=\"1273.394\" peak=\"0.01188\" />\n" +
                    "<Peak massChargeRatio=\"1280.682\" peak=\"0.00635\" />\n" +
                    "<Peak massChargeRatio=\"1283.057\" peak=\"0.0077\" />\n" +
                    "<Peak massChargeRatio=\"1289.83\" peak=\"0.04197\" />\n" +
                    "<Peak massChargeRatio=\"1309.785\" peak=\"0.07713\" />\n" +
                    "<Peak massChargeRatio=\"1340.516\" peak=\"0.05456\" />\n" +
                    "<Peak massChargeRatio=\"1347.847\" peak=\"0.05997\" />\n" +
                    "<Peak massChargeRatio=\"1358.587\" peak=\"0.0657\" />\n" +
                    "<Peak massChargeRatio=\"1365.315\" peak=\"0.0322\" />\n" +
                    "<Peak massChargeRatio=\"1368.571\" peak=\"0.07768\" />\n" +
                    "<Peak massChargeRatio=\"1374.654\" peak=\"0.02455\" />\n" +
                    "<Peak massChargeRatio=\"1376.25\" peak=\"0.09644\" />\n" +
                    "<Peak massChargeRatio=\"1386.491\" peak=\"0.07988\" />\n" +
                    "<Peak massChargeRatio=\"1387.414\" peak=\"0.07884\" />\n" +
                    "<Peak massChargeRatio=\"1417.981\" peak=\"0.0927\" />\n" +
                    "<Peak massChargeRatio=\"1419.167\" peak=\"0.02209\" />\n" +
                    "<Peak massChargeRatio=\"1433.872\" peak=\"0.09635\" />\n" +
                    "<Peak massChargeRatio=\"1434.656\" peak=\"0.03123\" />\n" +
                    "<Peak massChargeRatio=\"1437.807\" peak=\"0.07434\" />\n" +
                    "<Peak massChargeRatio=\"1444.669\" peak=\"0.03376\" />\n" +
                    "<Peak massChargeRatio=\"1456.597\" peak=\"0.05808\" />\n" +
                    "<Peak massChargeRatio=\"1457.638\" peak=\"0.06863\" />\n" +
                    "<Peak massChargeRatio=\"1469.985\" peak=\"0.0618\" />\n" +
                    "<Peak massChargeRatio=\"1489.054\" peak=\"0.07792\" />\n" +
                    "<Peak massChargeRatio=\"1499.863\" peak=\"0.05272\" />\n" +
                    "<Peak massChargeRatio=\"1533.013\" peak=\"0.07285\" />\n" +
                    "<Peak massChargeRatio=\"1545.662\" peak=\"0.07536\" />\n" +
                    "<Peak massChargeRatio=\"1553.559\" peak=\"0.03988\" />\n" +
                    "<Peak massChargeRatio=\"1560.809\" peak=\"0.04508\" />\n" +
                    "<Peak massChargeRatio=\"1572.886\" peak=\"0.02418\" />\n" +
                    "<Peak massChargeRatio=\"1575.142\" peak=\"0.03465\" />\n" +
                    "<Peak massChargeRatio=\"1584.102\" peak=\"0.03352\" />\n" +
                    "<Peak massChargeRatio=\"1586.378\" peak=\"0.07278\" />\n" +
                    "<Peak massChargeRatio=\"1598.411\" peak=\"0.0405\" />\n" +
                    "<Peak massChargeRatio=\"1600.048\" peak=\"0.02204\" />\n" +
                    "<Peak massChargeRatio=\"1602.735\" peak=\"0.04837\" />\n" +
                    "<Peak massChargeRatio=\"1605.894\" peak=\"0.03469\" />\n" +
                    "<Peak massChargeRatio=\"1616.72\" peak=\"0.06311\" />\n" +
                    "<Peak massChargeRatio=\"1618.521\" peak=\"0.03747\" />\n" +
                    "<Peak massChargeRatio=\"1656.925\" peak=\"0.04731\" />\n" +
                    "<Peak massChargeRatio=\"1670.658\" peak=\"0.02585\" />\n" +
                    "<Peak massChargeRatio=\"1673.096\" peak=\"0.04001\" />\n" +
                    "<Peak massChargeRatio=\"1673.744\" peak=\"0.03797\" />\n" +
                    "<Peak massChargeRatio=\"1680.416\" peak=\"0.04745\" />\n" +
                    "<Peak massChargeRatio=\"1684.2\" peak=\"0.02343\" />\n" +
                    "<Peak massChargeRatio=\"1698.019\" peak=\"0.02852\" />\n" +
                    "<Peak massChargeRatio=\"1699.108\" peak=\"0.09757\" />\n" +
                    "<Peak massChargeRatio=\"1702.556\" peak=\"0.0158\" />\n" +
                    "<Peak massChargeRatio=\"1706.676\" peak=\"0.02554\" />\n" +
                    "<Peak massChargeRatio=\"1715.517\" peak=\"0.01816\" />\n" +
                    "<Peak massChargeRatio=\"1718.94\" peak=\"0.02767\" />\n" +
                    "<Peak massChargeRatio=\"1719.771\" peak=\"0.03197\" />\n" +
                    "<Peak massChargeRatio=\"1725.828\" peak=\"0.02305\" />\n" +
                    "<Peak massChargeRatio=\"1727.616\" peak=\"0.03774\" />\n" +
                    "<Peak massChargeRatio=\"1730.126\" peak=\"0.02651\" />\n" +
                    "<Peak massChargeRatio=\"1745.482\" peak=\"0.03339\" />\n" +
                    "<Peak massChargeRatio=\"1752.096\" peak=\"0.04088\" />\n" +
                    "<Peak massChargeRatio=\"1754.571\" peak=\"0.03187\" />\n" +
                    "<Peak massChargeRatio=\"1773.184\" peak=\"0.05161\" />\n" +
                    "<Peak massChargeRatio=\"1778.725\" peak=\"0.02363\" />\n" +
                    "<Peak massChargeRatio=\"1779.685\" peak=\"0.02548\" />\n" +
                    "<Peak massChargeRatio=\"1781.406\" peak=\"0.03957\" />\n" +
                    "<Peak massChargeRatio=\"1784.407\" peak=\"0.02368\" />\n" +
                    "<Peak massChargeRatio=\"1794.27\" peak=\"0.0137\" />\n" +
                    "<Peak massChargeRatio=\"1795.08\" peak=\"0.02768\" />\n" +
                    "<Peak massChargeRatio=\"1797.815\" peak=\"0.02733\" />\n" +
                    "<Peak massChargeRatio=\"1799.545\" peak=\"0.08251\" />\n" +
                    "<Peak massChargeRatio=\"1816.514\" peak=\"0.06249\" />\n" +
                    "<Peak massChargeRatio=\"1818.004\" peak=\"0.02688\" />\n" +
                    "<Peak massChargeRatio=\"1820.641\" peak=\"0.02374\" />\n" +
                    "<Peak massChargeRatio=\"1824.45\" peak=\"0.02451\" />\n" +
                    "<Peak massChargeRatio=\"1832.029\" peak=\"0.02351\" />\n" +
                    "<Peak massChargeRatio=\"1835.374\" peak=\"0.03116\" />\n" +
                    "<Peak massChargeRatio=\"1838.108\" peak=\"0.02819\" />\n" +
                    "<Peak massChargeRatio=\"1840.317\" peak=\"0.0248\" />\n" +
                    "<Peak massChargeRatio=\"1843.297\" peak=\"0.06393\" />\n" +
                    "<Peak massChargeRatio=\"1857.783\" peak=\"0.02376\" />\n" +
                    "<Peak massChargeRatio=\"1858.776\" peak=\"0.0458\" />\n" +
                    "<Peak massChargeRatio=\"1863.557\" peak=\"0.01313\" />\n" +
                    "<Peak massChargeRatio=\"1865.848\" peak=\"0.05135\" />\n" +
                    "<Peak massChargeRatio=\"1873.523\" peak=\"0.00842\" />\n" +
                    "<Peak massChargeRatio=\"1880.847\" peak=\"0.03879\" />\n" +
                    "<Peak massChargeRatio=\"1887.472\" peak=\"0.02834\" />\n" +
                    "<Peak massChargeRatio=\"1889.202\" peak=\"0.02921\" />\n" +
                    "<Peak massChargeRatio=\"1890.839\" peak=\"0.02512\" />\n" +
                    "<Peak massChargeRatio=\"1893.646\" peak=\"0.02934\" />\n" +
                    "<Peak massChargeRatio=\"1897.285\" peak=\"0.01383\" />\n" +
                    "<Peak massChargeRatio=\"1898.319\" peak=\"0.01224\" />\n" +
                    "<Peak massChargeRatio=\"1899.343\" peak=\"0.01065\" />\n" +
                    "<Peak massChargeRatio=\"1900.61\" peak=\"0.03013\" />\n" +
                    "<Peak massChargeRatio=\"1908.033\" peak=\"0.04964\" />\n" +
                    "<Peak massChargeRatio=\"1910.209\" peak=\"0.02053\" />\n" +
                    "<Peak massChargeRatio=\"1916.178\" peak=\"0.02327\" />\n" +
                    "<Peak massChargeRatio=\"1917.745\" peak=\"0.04454\" />\n" +
                    "<Peak massChargeRatio=\"1920.972\" peak=\"0.04174\" />\n" +
                    "<Peak massChargeRatio=\"1923.69\" peak=\"0.00559\" />\n" +
                    "<Peak massChargeRatio=\"1924.903\" peak=\"0.01665\" />\n" +
                    "<Peak massChargeRatio=\"1929.591\" peak=\"0.00185\" />\n" +
                    "<Peak massChargeRatio=\"1935.229\" peak=\"0.00216\" />\n" +
                    "<Peak massChargeRatio=\"1936.129\" peak=\"0.00027\" />\n" +
                    "<Peak massChargeRatio=\"1939.069\" peak=\"0.01198\" />\n" +
                    "<Peak massChargeRatio=\"1940.6\" peak=\"0.0169\" />\n" +
                    "<Peak massChargeRatio=\"1941.476\" peak=\"0.00477\" />\n" +
                    "<Peak massChargeRatio=\"1943.929\" peak=\"0.01212\" />\n" +
                    "<Peak massChargeRatio=\"1948.607\" peak=\"0.00284\" />\n" +
                    "<Peak massChargeRatio=\"1951.804\" peak=\"0.03034\" />\n" +
                    "<Peak massChargeRatio=\"1957.3\" peak=\"0.01054\" />\n" +
                    "<Peak massChargeRatio=\"1962.812\" peak=\"0.01966\" />\n" +
                    "<Peak massChargeRatio=\"1964.496\" peak=\"0.06918\" />\n" +
                    "<Peak massChargeRatio=\"1965.82\" peak=\"0.00253\" />\n" +
                    "<Peak massChargeRatio=\"1967.107\" peak=\"0.02473\" />\n" +
                    "<Peak massChargeRatio=\"1974.991\" peak=\"0.02304\" />\n" +
                    "<Peak massChargeRatio=\"1976.268\" peak=\"0.03172\" />\n" +
                    "<Peak massChargeRatio=\"1978.446\" peak=\"0.04341\" />\n" +
                    "<Peak massChargeRatio=\"1979.626\" peak=\"0.00304\" />\n" +
                    "<Peak massChargeRatio=\"1980.325\" peak=\"0.04709\" />\n" +
                    "<Peak massChargeRatio=\"1981.815\" peak=\"0.03699\" />\n" +
                    "<Peak massChargeRatio=\"1983.025\" peak=\"0.07163\" />\n" +
                    "<Peak massChargeRatio=\"1985.6\" peak=\"0.04016\" />\n" +
                    "<Peak massChargeRatio=\"1986.87\" peak=\"0.05793\" />\n" +
                    "<Peak massChargeRatio=\"1987.901\" peak=\"0.03807\" />\n" +
                    "<Peak massChargeRatio=\"1988.897\" peak=\"0.05937\" />\n" +
                    "<Peak massChargeRatio=\"1990.151\" peak=\"0.04176\" />\n" +
                    "<Peak massChargeRatio=\"1992.169\" peak=\"0.01214\" />\n" +
                    "<Peak massChargeRatio=\"1992.992\" peak=\"0.00387\" />\n" +
                    "<Peak massChargeRatio=\"1994.398\" peak=\"0.00471\" />\n" +
                    "<Peak massChargeRatio=\"1995.534\" peak=\"0.06477\" />\n" +
                    "<Peak massChargeRatio=\"1996.51\" peak=\"0.0263\" />\n" +
                    "<Peak massChargeRatio=\"1997.803\" peak=\"0.01134\" />\n" +
                    "</MeasuredSpectrum>\n" +
                    "</NormalizedRawScan>\n" +
                    "<match  peak=\"VNG0675C VNG0675C:25(19)\" score=\"131.90623349866172\" hyperscore=\"131.90623349866172\">\n" +
                    "<IonScore  A_count=\"0\" A_score=\"0.0\" B_count=\"5\" B_score=\"0.18512288806959987\" C_count=\"0\" C_score=\"0.0\" Y_count=\"5\" Y_score=\"0.40069255512207747\" X_count=\"0\" X_score=\"0.0\" Z_count=\"0\" Z_score=\"0.0\" />\n" +
                    "</match>\n" +
                    "</score>\n"


                     ;


    public static final String SCORE_XML2 =
            "<score id=\"7868\"   expectedValue=\"1008.1612963554293\"  >\n" +
                    "<scan num=\"7868\"\n" +
                    " mslevel=\"2\"\n" +
                    " peaksCount=\"393\"\n" +
                    " polarity=\"+\"\n" +
                    " scantype=\"Full\"\n" +
                    " filterLine=\"null\"\n" +
                    " retentionTime=\"PT2900.28S\"\n" +
                    " lowMz=\"213.22\"\n" +
                    " highMz=\"1435.13\"\n" +
                    " basePeakMz=\"631.589\"\n" +
                    " basePeakIntensity=\"977.866\"\n" +
                    " totIonCurrent=\"11607.6\"\n" +
                    " collisionEnergy=\"0.0\"\n" +
                    ">\n" +
                    "<precursorMz precursorIntensity=\"102324\"  activationMethod=\"CID\" >\n" +
                    "730.62</precursorMz>\n" +
                    "<peaks precision=\"32\"\n" +
                    "     byteOrder=\"network\"\n" +
                    "     contentType=\"m/z-int\"\n" +
                    "     compressionType=\"none\"\n" +
                    "     compressedLen=\"0\" >Q1U4U0AeDwJDV10aQDCkE0NxNEw/0HA6Q3YvpEBDGMlDeyDhQMF6n0OBLoNAZyorQ4IB2kChLDpD\n" +
                    "g4riQK/VqkOFfz5AQxJqQ4YaNEAebidDhqA4QGgwpEOHnDpAxf4wQ4genUBnZkNDiJz+QHlxQkOJ\n" +
                    "owBBCv5fQ44jVkF1pydDkSiUQYmXTEOUrLBAxZ+uQ5UiGkCqQydDmq/+QAxlMUObFVZAhBpkQ5xh\n" +
                    "dUCw1+lDn6daQWrrAEOsG7JAeJUsQ623fEDwRSlDsG32QOnSHEOxGIhBIkWUQ7GUmkCF4QtDsgoh\n" +
                    "QMUeb0OzRWtAeRosQ7QVZEB13cxDtYtWQGfFb0O2jS5AjsrCQ7e1HEDBnZ1DuOa4QRENWUO6j7ZA\n" +
                    "QrY9Q7xHlEBVJURDvWyIQbLIBEO+xK5AMINqQ7+Q9EBUhBFDwCXCQT8tUEPApoRA3bGgQ8OqakMf\n" +
                    "DqtDxD10QQr6z0PFt7ZAjxRTQ8Y8FkAwIWhDxxMkQAvVREPIR3pBSTSiQ8qKUEBoBZlDzKS8QRco\n" +
                    "p0POPmpAwbTcQ87OIkEUqyNDz5sAQP4pkUPRQwhCFpyiQ9VDTkEX0g5D1kNSQTOwG0PXmtJC6b/E\n" +
                    "Q9oXZkB1XfZD26wiQGfQREPdA+xAqiejQ923jEEYhZdD4Do+QI6SGkPhtqxAjLPAQ+K8gEC4ZZ5D\n" +
                    "5DPOQY+zhEPkkyxBMJGLQ+kgpkDedZJD6qlqQGduuEPrFG5BYB3NQ+uqhEEV6BVD6/8gQIXlIEPt\n" +
                    "SW5AoR6YQ+2w3kGG6Q9D77NmQIXZUkPzy1JBEv0cQ/TM7EB5zRtD9jM4Q8ZdlkP2p8BBjkWwQ/cV\n" +
                    "KkEu7OZD+8TAQKJvkEP8p0RBrjzMQ/6yskEOLpVD/4oqQduuWUQAAwlBUlRCRAGK2EH6ZmZEAn3U\n" +
                    "QGaAA0QEIKhB7V/2RARZF0Gcv3dEBIKSQQlgtEQE5C9Bj8/iRAUc70IIaspEBVy5QTl9LEQFlDtA\n" +
                    "Z17rRAZhM0C6RvNEB6C8QVpzWEQIGvZApzygRAhbpUCduW1ECLu5QZuMIEQJJ8dCQVT5RAlWzUGe\n" +
                    "TYlECZnwQSriakQK1idBa/jIRAssckGO0hhEC4l1QhSBKkQMEk1A5hnURAzUvkEhAJ9EDQrMQMrN\n" +
                    "GkQNTj5AQaiJRA3AW0RLrSJEDgB/P/8q5kQPSgVBXEFqRBBrR0G1fadEEJu6QGepqUQRaP9BaP5e\n" +
                    "RBGSxUF+GxhEEdTfQY6SgUQSBo5BkT3ERBKRwUCFpCNEEsx0QMkNeUQTE81BzXjARBVxpUFIedJE\n" +
                    "FgKdQKndG0QWN29BgFHIRBa3F0EKN+tEFy5GQd+GTkQXXotDGrOlRBehYUHA3wpEF8tKQROoLEQY\n" +
                    "xltBYRZFRBlXBUIeRVJEGYF7QMIslUQZ5NdAsKwGRBpLRkAfsE1EG1YTQQhCA0QbpLlDESABRBvt\n" +
                    "H0E06O1EHFB1QPiDbEQcnd5AMFFbRBzdHUFJlWtEHW7pQf2aS0Qdt/lDGAtGRB3lskR0d3JEHiqF\n" +
                    "Qa9/dEQemq1BUmMFRB7azkE14blEHxAUQDC8zEQf0gtCC6UPRCAUEUD5Gr1EIFfzQgzTkkQhcB5A\n" +
                    "qfRARCGo40DKjlREIiMDQYqtV0QjNxVAi5tCRCOeBEHA/GBEI9prQhDpIkQkKRBBNUBqRCRgD0Ge\n" +
                    "o6JEJJcNQSVTkUQk29JA3gjdRCUtHUE8KLhEJZpSQfEOiUQl6M9BWm5oRCZHIUDu8uxEJpjoQPjL\n" +
                    "X0QmyU9AWDUBRCbx0UEcCmVEJ4iNQOksl0Qn7qNA3Mh/RCgayUCVZSNEKGvGQZWNNEQoxoZAVXxn\n" +
                    "RCksLEE+4wxEKWqHQcW/ckQpvAtBld13RCnndkE7zbZEKhdFQGekL0QqWPlBD0KwRCtQnEGpL8BE\n" +
                    "K6BRQjp6/EQsB1VCzFz8RCxhhUIQD45ELL6tQZLsbkQtnqRCgy0SRC3YokEWkWJELmf/QcCDgkQu\n" +
                    "3x9AqhE0RC8SV0DOMKVEL1rAQkrfkkQvmo5B/D4tRC/o10IIpABEMQqHQbkTBkQxORdBMkS9RDFg\n" +
                    "Z0Ed9+NEMhIWQ0BpbkQyOZtCKP+MRDJmoEIB64xEMsoiQmyxDkQzE35CMkd3RDN2A0Q8s6hEM8he\n" +
                    "QnPEJ0Qz9XRBGRazRDRBPUIk4WxENPCvQFWKxUQ3WZ9AMEXhRDeQwECGDAhEN/cJQN3DMUQ4fI9A\n" +
                    "mA51RDkEAEBrXFNEOSqHQHmKsEQ5l2lBzupyRDnsyUB5jMBEOh6PQhTBfkQ67StA+GsbRDwZpUGN\n" +
                    "7QdEPFJNQRKXDEQ8sRFAr823RDzgb0GXJN5EPSSTQ4Unh0Q9XAdBVVi+RD2MpUGSPzNEPlzHQJgM\n" +
                    "MUQ+6DFAxS6GRD89Z0E0QglEP6FuQA0JjURASoFBgDEmRECZ50ISm+JEQMaVQRtihERA87FAsF4e\n" +
                    "REGIo0DggYtEQca7QYXxQERCSTFAa/4pREJ7P0FyIqpEQqjVQJaD2ERDSm5BDvIlREPfoEGrX+dE\n" +
                    "RAcPQYRXNkRET1RBk3a0REVhX0D4x4JERhgcQz5cLERGRzNBMh7tREbCXEIhlH5ERxRbQcrgfkRH\n" +
                    "VdRBfaiGREeMEEDwxQ5ER7LUQAxoLkRIDcFAMHNFREhoXUFr/O5ESJhdQQHgyURK4+FBSIE1REuU\n" +
                    "gUL2zSRES8XzQOH2JkRM+UlBVzOiRE7aTEF//51ETx0ZQkU4HkRPUc9CHxxcRFB7/kAxaWlEUTKB\n" +
                    "QjIiAERRcZxBLibvRFH8JEHu8SVEUlTTQQOOMERSo8tAVoPARFMje0FZsjFEU27LQJUiSERUMcBD\n" +
                    "d8rSRFReo0K+pZBEVJlPQDCdX0RUy5pBDnoaRFT0pkDzB+BEVTi+Qa1A50RVbgtBaUGnRFWw/0AN\n" +
                    "Yc5EVfcVQYjm50RW4YpBXBErRFdaSkIrTtpEV6KOQOCKDkRYCQdA75zcRFlUUUBWlF1EWdtmQiO9\n" +
                    "ZkRaF1xAlFwQRFq0fUAe2HtEWw13QPfEvkRbgEtCDxGaRFvNqUFaF0BEXECoQZzo0kRc1ARBxC0p\n" +
                    "RF0cSUGEoHxEXVlAQTbxskRdlilBZ//mRF2/T0EQtuVEXuo9QI8JDERfzCdBHIRCRGDn60OCy9FE\n" +
                    "YSt/QA4X4ERiJctAmC1fRGJRaEC4zYtEYpP8QMI6a0RjW1lCySpkRGOeFUHmlKhEY+WoQKEs00Rk\n" +
                    "0vhBG+gkRGWI70EFahREZyFXQUkLj0Rn2PNERAYnRGgYs0M+68BEaL37QVPGwERpB1dB43fHRGmM\n" +
                    "YUGCQ4BEaeDhQbxzsERrPllCtPwrRGtvhEAp6hBEa7KkQVJnaURr4z1BKE/yRGwR+0GA6YlEbSY9\n" +
                    "QeEBnURtc1RByeuSRG2gL0HWaDFEbqIxQRL5gkRvVt9BvtO9RHD1B0DY5ddEcVidQNdpAERxkytA\n" +
                    "12HHRHKMdUCCEdZEcsovQXcjPkRzLmlBpYKvRHOpFUFmXfZEdPxQQgT05kR1kPxAHsEARHXrWUBD\n" +
                    "GR1EdiF1QMHHlER24thAxGxKRHcqHEJf5n5EeC2dQLMM80R4af1AdedDRHi2y0EzLvlEeQIfQGdL\n" +
                    "MUR5VVxA6VytRHuyjECOw1tEfbOrQY3aokR/76hBRCu8RIAddkAw7ztEgFtRQRdah0SAdRhAndcH\n" +
                    "RICZIUEH3X5EgLlHQB6MhkSBM3BBFxjIRIHM3kIasbxEge8VQSvvI0SCqlJAgpq0RIPA5kAepi9E\n" +
                    "hA6LQ75rqESEMG1CswlhRIT7ikESroREhlNCQXggFUSGcFhBHXGQRIaOc0DBxGxEhz6OQQ4vJESH\n" +
                    "rgJBAoq/RIfukEBC0WBEiRAkQIXdpESJNShBizZDRIldw0EvlS1EinMAQLxqX0SK2FJAZ3xtRItM\n" +
                    "HkCXn3xEjIbFQUSG9USM9nxBYAqYRI2QPEKoQs1Ejba0QY9HCUSOlk5AsyNKRI8ueUDdVbxEkDV5\n" +
                    "QB7ZF0SReY5BNt42RJIZhUDODjREki4rQFUZekSS3QdAoAHJRJMW1EE7ebREk8VIQPLBGkSVG55A\n" +
                    "xVZtRJVcJz/8zppElhAJQIYGgESZDtxBUiL+RJsaN0EwahxEnbNHQDDpMESe0btA+Jp7RJ758kCv\n" +
                    "VQJEpSEJP/S7ekSl9WxA3tfyRKYd8UCWKdhEp3drQQN/x0SqES9BDehJRK7zNUBi8tBEszjLQnhg\n" +
                    "LkSzZBxAHkb3</peaks>\n" +
                    "</scan>\n" +
                    "<ConditionedScan>\n" +
                    "<MeasuredSpectrum PrecursorCharge=\"0\" PrecursorMass=\"1460.233\" >\n" +
                    "<Peak massChargeRatio=\"213.22\" peak=\"0.0238\" />\n" +
                    "<Peak massChargeRatio=\"215.364\" peak=\"0.02545\" />\n" +
                    "<Peak massChargeRatio=\"241.204\" peak=\"0.01095\" />\n" +
                    "<Peak massChargeRatio=\"246.186\" peak=\"0.01957\" />\n" +
                    "<Peak massChargeRatio=\"251.128\" peak=\"0.03107\" />\n" +
                    "<Peak massChargeRatio=\"258.363\" peak=\"0.02084\" />\n" +
                    "<Peak massChargeRatio=\"260.014\" peak=\"0.02644\" />\n" +
                    "<Peak massChargeRatio=\"263.085\" peak=\"0.02715\" />\n" +
                    "<Peak massChargeRatio=\"266.994\" peak=\"0.01731\" />\n" +
                    "<Peak massChargeRatio=\"268.205\" peak=\"0.01414\" />\n" +
                    "<Peak massChargeRatio=\"269.252\" peak=\"0.01963\" />\n" +
                    "<Peak massChargeRatio=\"271.221\" peak=\"0.02961\" />\n" +
                    "<Peak massChargeRatio=\"272.239\" peak=\"0.01887\" />\n" +
                    "<Peak massChargeRatio=\"273.227\" peak=\"0.0202\" />\n" +
                    "<Peak massChargeRatio=\"275.273\" peak=\"0.03804\" />\n" +
                    "<Peak massChargeRatio=\"284.276\" peak=\"0.05584\" />\n" +
                    "<Peak massChargeRatio=\"290.317\" peak=\"0.06004\" />\n" +
                    "<Peak massChargeRatio=\"297.349\" peak=\"0.02959\" />\n" +
                    "<Peak massChargeRatio=\"298.266\" peak=\"0.02583\" />\n" +
                    "<Peak massChargeRatio=\"309.375\" peak=\"0.00836\" />\n" +
                    "<Peak massChargeRatio=\"310.167\" peak=\"0.01865\" />\n" +
                    "<Peak massChargeRatio=\"312.761\" peak=\"0.02447\" />\n" +
                    "<Peak massChargeRatio=\"319.307\" peak=\"0.05149\" />\n" +
                    "<Peak massChargeRatio=\"344.216\" peak=\"0.01224\" />\n" +
                    "<Peak massChargeRatio=\"347.433\" peak=\"0.02577\" />\n" +
                    "<Peak massChargeRatio=\"352.859\" peak=\"0.02472\" />\n" +
                    "<Peak massChargeRatio=\"354.192\" peak=\"0.03354\" />\n" +
                    "<Peak massChargeRatio=\"355.161\" peak=\"0.01213\" />\n" +
                    "<Peak massChargeRatio=\"356.079\" peak=\"0.02013\" />\n" +
                    "<Peak massChargeRatio=\"358.542\" peak=\"0.0108\" />\n" +
                    "<Peak massChargeRatio=\"360.167\" peak=\"0.01041\" />\n" +
                    "<Peak massChargeRatio=\"363.089\" peak=\"0.00924\" />\n" +
                    "<Peak massChargeRatio=\"365.103\" peak=\"0.01216\" />\n" +
                    "<Peak massChargeRatio=\"367.415\" peak=\"0.01802\" />\n" +
                    "<Peak massChargeRatio=\"369.802\" peak=\"0.02797\" />\n" +
                    "<Peak massChargeRatio=\"373.123\" peak=\"0.00535\" />\n" +
                    "<Peak massChargeRatio=\"376.559\" peak=\"0.00648\" />\n" +
                    "<Peak massChargeRatio=\"378.848\" peak=\"0.05929\" />\n" +
                    "<Peak massChargeRatio=\"381.537\" peak=\"0.00148\" />\n" +
                    "<Peak massChargeRatio=\"383.132\" peak=\"0.00444\" />\n" +
                    "<Peak massChargeRatio=\"384.295\" peak=\"0.0344\" />\n" +
                    "<Peak massChargeRatio=\"385.301\" peak=\"0.01929\" />\n" +
                    "<Peak massChargeRatio=\"391.331\" peak=\"0.20185\" />\n" +
                    "<Peak massChargeRatio=\"392.48\" peak=\"0.02442\" />\n" +
                    "<Peak massChargeRatio=\"395.435\" peak=\"0.00935\" />\n" +
                    "<Peak massChargeRatio=\"396.469\" peak=\"0.001\" />\n" +
                    "<Peak massChargeRatio=\"400.558\" peak=\"0.03622\" />\n" +
                    "<Peak massChargeRatio=\"405.081\" peak=\"0.00745\" />\n" +
                    "<Peak massChargeRatio=\"409.287\" peak=\"0.02981\" />\n" +
                    "<Peak massChargeRatio=\"412.488\" peak=\"0.01913\" />\n" +
                    "<Peak massChargeRatio=\"413.61\" peak=\"0.03025\" />\n" +
                    "<Peak massChargeRatio=\"415.211\" peak=\"0.02661\" />\n" +
                    "<Peak massChargeRatio=\"418.524\" peak=\"0.02529\" />\n" +
                    "<Peak massChargeRatio=\"428.526\" peak=\"0.00269\" />\n" +
                    "<Peak massChargeRatio=\"431.21\" peak=\"0.06443\" />\n" +
                    "<Peak massChargeRatio=\"442.031\" peak=\"0.00063\" />\n" +
                    "<Peak massChargeRatio=\"443.434\" peak=\"0.00568\" />\n" +
                    "<Peak massChargeRatio=\"453.473\" peak=\"0.00212\" />\n" +
                    "<Peak massChargeRatio=\"456.405\" peak=\"0.01671\" />\n" +
                    "<Peak massChargeRatio=\"457.15\" peak=\"0.00929\" />\n" +
                    "<Peak massChargeRatio=\"466.255\" peak=\"0.00618\" />\n" +
                    "<Peak massChargeRatio=\"469.324\" peak=\"0.00031\" />\n" +
                    "<Peak massChargeRatio=\"470.16\" peak=\"0.01523\" />\n" +
                    "<Peak massChargeRatio=\"471.332\" peak=\"0.00969\" />\n" +
                    "<Peak massChargeRatio=\"474.574\" peak=\"0.00292\" />\n" +
                    "<Peak massChargeRatio=\"475.382\" peak=\"0.01801\" />\n" +
                    "<Peak massChargeRatio=\"479.402\" peak=\"0.00075\" />\n" +
                    "<Peak massChargeRatio=\"487.588\" peak=\"0.0084\" />\n" +
                    "<Peak massChargeRatio=\"489.601\" peak=\"0\" />\n" +
                    "<Peak massChargeRatio=\"492.4\" peak=\"0.14539\" />\n" +
                    "<Peak massChargeRatio=\"493.311\" peak=\"0.018\" />\n" +
                    "<Peak massChargeRatio=\"494.165\" peak=\"0.01095\" />\n" +
                    "<Peak massChargeRatio=\"503.537\" peak=\"0.00013\" />\n" +
                    "<Peak massChargeRatio=\"505.307\" peak=\"0.01961\" />\n" +
                    "<Peak massChargeRatio=\"509.396\" peak=\"0.0053\" />\n" +
                    "<Peak massChargeRatio=\"511.079\" peak=\"0.02332\" />\n" +
                    "<Peak massChargeRatio=\"518.169\" peak=\"0.02125\" />\n" +
                    "<Peak massChargeRatio=\"528.51\" peak=\"0.02084\" />\n" +
                    "<Peak massChargeRatio=\"529.392\" peak=\"0.01257\" />\n" +
                    "<Peak massChargeRatio=\"531.565\" peak=\"0.01119\" />\n" +
                    "<Peak massChargeRatio=\"532.452\" peak=\"0.0234\" />\n" +
                    "<Peak massChargeRatio=\"533.449\" peak=\"0.00319\" />\n" +
                    "<Peak massChargeRatio=\"542.511\" peak=\"0.00677\" />\n" +
                    "<Peak massChargeRatio=\"546.933\" peak=\"0.01507\" />\n" +
                    "<Peak massChargeRatio=\"548.622\" peak=\"0.0353\" />\n" +
                    "<Peak massChargeRatio=\"549.356\" peak=\"0.01502\" />\n" +
                    "<Peak massChargeRatio=\"550.405\" peak=\"0.00514\" />\n" +
                    "<Peak massChargeRatio=\"555.346\" peak=\"0.00836\" />\n" +
                    "<Peak massChargeRatio=\"556.694\" peak=\"0.01015\" />\n" +
                    "<Peak massChargeRatio=\"558.148\" peak=\"0.025\" />\n" +
                    "<Peak massChargeRatio=\"563.324\" peak=\"0.00172\" />\n" +
                    "<Peak massChargeRatio=\"567.006\" peak=\"0.20653\" />\n" +
                    "<Peak massChargeRatio=\"573.157\" peak=\"0.0041\" />\n" +
                    "<Peak massChargeRatio=\"577.676\" peak=\"0.01172\" />\n" +
                    "<Peak massChargeRatio=\"582.293\" peak=\"0.00051\" />\n" +
                    "<Peak massChargeRatio=\"583.326\" peak=\"0.00319\" />\n" +
                    "<Peak massChargeRatio=\"588.309\" peak=\"0.00976\" />\n" +
                    "<Peak massChargeRatio=\"600.866\" peak=\"0.00219\" />\n" +
                    "<Peak massChargeRatio=\"604.723\" peak=\"0.01204\" />\n" +
                    "<Peak massChargeRatio=\"605.477\" peak=\"0.06962\" />\n" +
                    "<Peak massChargeRatio=\"606.522\" peak=\"0.00878\" />\n" +
                    "<Peak massChargeRatio=\"613.36\" peak=\"0.01977\" />\n" +
                    "<Peak massChargeRatio=\"622.574\" peak=\"0.06997\" />\n" +
                    "<Peak massChargeRatio=\"629.733\" peak=\"0.01329\" />\n" +
                    "<Peak massChargeRatio=\"630.875\" peak=\"0.06269\" />\n" +
                    "<Peak massChargeRatio=\"631.589\" peak=\"0.2025\" />\n" +
                    "<Peak massChargeRatio=\"632.664\" peak=\"0.00667\" />\n" +
                    "<Peak massChargeRatio=\"639.282\" peak=\"0.01454\" />\n" +
                    "<Peak massChargeRatio=\"641.374\" peak=\"0.01386\" />\n" +
                    "<Peak massChargeRatio=\"654.469\" peak=\"0.00338\" />\n" +
                    "<Peak massChargeRatio=\"655.413\" peak=\"0.01222\" />\n" +
                    "<Peak massChargeRatio=\"657.501\" peak=\"0.00312\" />\n" +
                    "<Peak massChargeRatio=\"662.411\" peak=\"0.00824\" />\n" +
                    "<Peak massChargeRatio=\"685.26\" peak=\"0.00435\" />\n" +
                    "<Peak massChargeRatio=\"686.505\" peak=\"0.02106\" />\n" +
                    "<Peak massChargeRatio=\"688.115\" peak=\"0.04516\" />\n" +
                    "<Peak massChargeRatio=\"689.524\" peak=\"0.01522\" />\n" +
                    "<Peak massChargeRatio=\"690.979\" peak=\"0.00269\" />\n" +
                    "<Peak massChargeRatio=\"694.479\" peak=\"0.03013\" />\n" +
                    "<Peak massChargeRatio=\"697.625\" peak=\"0.00686\" />\n" +
                    "<Peak massChargeRatio=\"701.418\" peak=\"0.02343\" />\n" +
                    "<Peak massChargeRatio=\"702.415\" peak=\"0.01187\" />\n" +
                    "<Peak massChargeRatio=\"703.638\" peak=\"0.01372\" />\n" +
                    "<Peak massChargeRatio=\"708.164\" peak=\"0.00454\" />\n" +
                    "<Peak massChargeRatio=\"712.283\" peak=\"0.07224\" />\n" +
                    "<Peak massChargeRatio=\"712.9\" peak=\"0.01814\" />\n" +
                    "<Peak massChargeRatio=\"713.604\" peak=\"0.01238\" />\n" +
                    "<Peak massChargeRatio=\"715.158\" peak=\"0.02712\" />\n" +
                    "<Peak massChargeRatio=\"716.305\" peak=\"0.01961\" />\n" +
                    "<Peak massChargeRatio=\"717.844\" peak=\"0.17369\" />\n" +
                    "<Peak massChargeRatio=\"719.131\" peak=\"0.02812\" />\n" +
                    "<Peak massChargeRatio=\"721.019\" peak=\"0.01702\" />\n" +
                    "<Peak massChargeRatio=\"742.366\" peak=\"0.00916\" />\n" +
                    "<Peak massChargeRatio=\"744.477\" peak=\"0.0153\" />\n" +
                    "<Peak massChargeRatio=\"752.401\" peak=\"0.00188\" />\n" +
                    "<Peak massChargeRatio=\"755.507\" peak=\"0.00414\" />\n" +
                    "<Peak massChargeRatio=\"756.571\" peak=\"0.09265\" />\n" +
                    "<Peak massChargeRatio=\"757.438\" peak=\"0.00361\" />\n" +
                    "<Peak massChargeRatio=\"769.164\" peak=\"0.00987\" />\n" +
                    "<Peak massChargeRatio=\"770.405\" peak=\"0.02528\" />\n" +
                    "<Peak massChargeRatio=\"774.135\" peak=\"0.00097\" />\n" +
                    "<Peak massChargeRatio=\"775.105\" peak=\"0.01164\" />\n" +
                    "<Peak massChargeRatio=\"777.926\" peak=\"0.00972\" />\n" +
                    "<Peak massChargeRatio=\"781.163\" peak=\"0.00206\" />\n" +
                    "<Peak massChargeRatio=\"783.494\" peak=\"0.01433\" />\n" +
                    "<Peak massChargeRatio=\"785.24\" peak=\"0.01172\" />\n" +
                    "<Peak massChargeRatio=\"792.377\" peak=\"0.08065\" />\n" +
                    "<Peak massChargeRatio=\"795.037\" peak=\"0.02582\" />\n" +
                    "<Peak massChargeRatio=\"796.318\" peak=\"0.01612\" />\n" +
                    "<Peak massChargeRatio=\"797.341\" peak=\"0.00832\" />\n" +
                    "<Peak massChargeRatio=\"801.631\" peak=\"0.00321\" />\n" +
                    "<Peak massChargeRatio=\"811.561\" peak=\"0.00157\" />\n" +
                    "<Peak massChargeRatio=\"814.32\" peak=\"0.05717\" />\n" +
                    "<Peak massChargeRatio=\"819.895\" peak=\"0.00293\" />\n" +
                    "<Peak massChargeRatio=\"827.411\" peak=\"0.00546\" />\n" +
                    "<Peak massChargeRatio=\"828.455\" peak=\"0.0337\" />\n" +
                    "<Peak massChargeRatio=\"836.789\" peak=\"0.02963\" />\n" +
                    "<Peak massChargeRatio=\"837.775\" peak=\"0.00152\" />\n" +
                    "<Peak massChargeRatio=\"839.94\" peak=\"0.01961\" />\n" +
                    "<Peak massChargeRatio=\"844.554\" peak=\"0.00666\" />\n" +
                    "<Peak massChargeRatio=\"848.777\" peak=\"0.10803\" />\n" +
                    "<Peak massChargeRatio=\"849.479\" peak=\"0.05764\" />\n" +
                    "<Peak massChargeRatio=\"851.181\" peak=\"0.00035\" />\n" +
                    "<Peak massChargeRatio=\"852.887\" peak=\"0.01453\" />\n" +
                    "<Peak massChargeRatio=\"853.719\" peak=\"0.00764\" />\n" +
                    "<Peak massChargeRatio=\"855.861\" peak=\"0.01002\" />\n" +
                    "<Peak massChargeRatio=\"859.524\" peak=\"0.00455\" />\n" +
                    "<Peak massChargeRatio=\"861.411\" peak=\"0.02752\" />\n" +
                    "<Peak massChargeRatio=\"871.428\" peak=\"0.02791\" />\n" +
                    "<Peak massChargeRatio=\"878.005\" peak=\"0.01897\" />\n" +
                    "<Peak massChargeRatio=\"881.01\" peak=\"0.00574\" />\n" +
                    "<Peak massChargeRatio=\"883.313\" peak=\"0.00935\" />\n" +
                    "<Peak massChargeRatio=\"884.442\" peak=\"0.00171\" />\n" +
                    "<Peak massChargeRatio=\"899.624\" peak=\"0.10392\" />\n" +
                    "<Peak massChargeRatio=\"909.427\" peak=\"0.05477\" />\n" +
                    "<Peak massChargeRatio=\"910.47\" peak=\"0.01659\" />\n" +
                    "<Peak massChargeRatio=\"924.521\" peak=\"0.00196\" />\n" +
                    "<Peak massChargeRatio=\"927.39\" peak=\"0.204\" />\n" +
                    "<Peak massChargeRatio=\"928.386\" peak=\"0.08737\" />\n" +
                    "<Peak massChargeRatio=\"930.968\" peak=\"0.00321\" />\n" +
                    "<Peak massChargeRatio=\"932.115\" peak=\"0.0177\" />\n" +
                    "<Peak massChargeRatio=\"934.193\" peak=\"0.00718\" />\n" +
                    "<Peak massChargeRatio=\"935.514\" peak=\"0.01468\" />\n" +
                    "<Peak massChargeRatio=\"940.974\" peak=\"0.05251\" />\n" +
                    "<Peak massChargeRatio=\"942.791\" peak=\"0.00379\" />\n" +
                    "<Peak massChargeRatio=\"943.551\" peak=\"0.00692\" />\n" +
                    "<Peak massChargeRatio=\"948.597\" peak=\"0.0167\" />\n" +
                    "<Peak massChargeRatio=\"949.802\" peak=\"0.01698\" />\n" +
                    "<Peak massChargeRatio=\"954.534\" peak=\"0.00052\" />\n" +
                    "<Peak massChargeRatio=\"957.357\" peak=\"0.01603\" />\n" +
                    "<Peak massChargeRatio=\"971.159\" peak=\"0.01061\" />\n" +
                    "<Peak massChargeRatio=\"972.725\" peak=\"0.01571\" />\n" +
                    "<Peak massChargeRatio=\"974.642\" peak=\"0.00927\" />\n" +
                    "<Peak massChargeRatio=\"979.942\" peak=\"0.03088\" />\n" +
                    "<Peak massChargeRatio=\"983.677\" peak=\"0.00412\" />\n" +
                    "<Peak massChargeRatio=\"987.544\" peak=\"0.0054\" />\n" +
                    "<Peak massChargeRatio=\"988.658\" peak=\"0.04508\" />\n" +
                    "<Peak massChargeRatio=\"992.713\" peak=\"0.00399\" />\n" +
                    "<Peak massChargeRatio=\"993.656\" peak=\"0.00141\" />\n" +
                    "<Peak massChargeRatio=\"994.856\" peak=\"0.01288\" />\n" +
                    "<Peak massChargeRatio=\"996.033\" peak=\"0.00071\" />\n" +
                    "<Peak massChargeRatio=\"997.334\" peak=\"0.00731\" />\n" +
                    "<Peak massChargeRatio=\"1014.807\" peak=\"0.01502\" />\n" +
                    "<Peak massChargeRatio=\"1023.745\" peak=\"0.01072\" />\n" +
                    "<Peak massChargeRatio=\"1026.854\" peak=\"0.00613\" />\n" +
                    "<Peak massChargeRatio=\"1028.785\" peak=\"0.0048\" />\n" +
                    "<Peak massChargeRatio=\"1033.607\" peak=\"0.01754\" />\n" +
                    "<Peak massChargeRatio=\"1038.402\" peak=\"0.05457\" />\n" +
                    "<Peak massChargeRatio=\"1039.471\" peak=\"0.02034\" />\n" +
                    "<Peak massChargeRatio=\"1045.323\" peak=\"0.00675\" />\n" +
                    "<Peak massChargeRatio=\"1054.028\" peak=\"0.0003\" />\n" +
                    "<Peak massChargeRatio=\"1056.454\" peak=\"0.21284\" />\n" +
                    "<Peak massChargeRatio=\"1057.513\" peak=\"0.0937\" />\n" +
                    "<Peak massChargeRatio=\"1063.861\" peak=\"0.01671\" />\n" +
                    "<Peak massChargeRatio=\"1074.602\" peak=\"0.02735\" />\n" +
                    "<Peak massChargeRatio=\"1075.511\" peak=\"0.01818\" />\n" +
                    "<Peak massChargeRatio=\"1076.452\" peak=\"0.0102\" />\n" +
                    "<Peak massChargeRatio=\"1081.955\" peak=\"0.01572\" />\n" +
                    "<Peak massChargeRatio=\"1085.438\" peak=\"0.01286\" />\n" +
                    "<Peak massChargeRatio=\"1096.504\" peak=\"0.00461\" />\n" +
                    "<Peak massChargeRatio=\"1097.661\" peak=\"0.02979\" />\n" +
                    "<Peak massChargeRatio=\"1098.93\" peak=\"0.01962\" />\n" +
                    "<Peak massChargeRatio=\"1107.594\" peak=\"0.01466\" />\n" +
                    "<Peak massChargeRatio=\"1110.76\" peak=\"0.00946\" />\n" +
                    "<Peak massChargeRatio=\"1114.379\" peak=\"0.01233\" />\n" +
                    "<Peak massChargeRatio=\"1124.212\" peak=\"0.02777\" />\n" +
                    "<Peak massChargeRatio=\"1127.703\" peak=\"0.03128\" />\n" +
                    "<Peak massChargeRatio=\"1132.507\" peak=\"0.09537\" />\n" +
                    "<Peak massChargeRatio=\"1133.709\" peak=\"0.03709\" />\n" +
                    "<Peak massChargeRatio=\"1140.697\" peak=\"0.01583\" />\n" +
                    "<Peak massChargeRatio=\"1145.452\" peak=\"0.01813\" />\n" +
                    "<Peak massChargeRatio=\"1153.671\" peak=\"0.00711\" />\n" +
                    "<Peak massChargeRatio=\"1163.799\" peak=\"0.02937\" />\n" +
                    "<Peak massChargeRatio=\"1168.797\" peak=\"0.01981\" />\n" +
                    "<Peak massChargeRatio=\"1174.907\" peak=\"0.01619\" />\n" +
                    "<Peak massChargeRatio=\"1176.713\" peak=\"0.03025\" />\n" +
                    "<Peak massChargeRatio=\"1182.165\" peak=\"0.02313\" />\n" +
                    "<Peak massChargeRatio=\"1192.863\" peak=\"0.02152\" />\n" +
                    "<Peak massChargeRatio=\"1194.88\" peak=\"0.00875\" />\n" +
                    "<Peak massChargeRatio=\"1200.501\" peak=\"0.01689\" />\n" +
                    "<Peak massChargeRatio=\"1224.464\" peak=\"0.03332\" />\n" +
                    "<Peak massChargeRatio=\"1240.819\" peak=\"0.08962\" />\n" +
                    "<Peak massChargeRatio=\"1261.602\" peak=\"0.04225\" />\n" +
                    "<Peak massChargeRatio=\"1270.554\" peak=\"0.07525\" />\n" +
                    "<Peak massChargeRatio=\"1271.811\" peak=\"0.06138\" />\n" +
                    "<Peak massChargeRatio=\"1321.032\" peak=\"0.03244\" />\n" +
                    "<Peak massChargeRatio=\"1327.669\" peak=\"0.07062\" />\n" +
                    "<Peak massChargeRatio=\"1328.936\" peak=\"0.05675\" />\n" +
                    "<Peak massChargeRatio=\"1339.732\" peak=\"0.07731\" />\n" +
                    "<Peak massChargeRatio=\"1360.537\" peak=\"0.07952\" />\n" +
                    "<Peak massChargeRatio=\"1399.6\" peak=\"0.04708\" />\n" +
                    "<Peak massChargeRatio=\"1433.775\" peak=\"0.22471\" />\n" +
                    "<Peak massChargeRatio=\"1435.128\" peak=\"0.03966\" />\n" +
                    "</MeasuredSpectrum>\n" +
                    "</ConditionedScan>\n" +
                    "<NormalizedRawScan>\n" +
                    "<MeasuredSpectrum PrecursorCharge=\"0\" PrecursorMass=\"1460.233\" >\n" +
                    "<Peak massChargeRatio=\"213.22\" peak=\"0.0238\" />\n" +
                    "<Peak massChargeRatio=\"215.364\" peak=\"0.02545\" />\n" +
                    "<Peak massChargeRatio=\"241.204\" peak=\"0.01095\" />\n" +
                    "<Peak massChargeRatio=\"246.186\" peak=\"0.01957\" />\n" +
                    "<Peak massChargeRatio=\"251.128\" peak=\"0.03107\" />\n" +
                    "<Peak massChargeRatio=\"258.363\" peak=\"0.02084\" />\n" +
                    "<Peak massChargeRatio=\"260.014\" peak=\"0.02644\" />\n" +
                    "<Peak massChargeRatio=\"263.085\" peak=\"0.02715\" />\n" +
                    "<Peak massChargeRatio=\"266.994\" peak=\"0.01731\" />\n" +
                    "<Peak massChargeRatio=\"268.205\" peak=\"0.01414\" />\n" +
                    "<Peak massChargeRatio=\"269.252\" peak=\"0.01963\" />\n" +
                    "<Peak massChargeRatio=\"271.221\" peak=\"0.02961\" />\n" +
                    "<Peak massChargeRatio=\"272.239\" peak=\"0.01887\" />\n" +
                    "<Peak massChargeRatio=\"273.227\" peak=\"0.0202\" />\n" +
                    "<Peak massChargeRatio=\"275.273\" peak=\"0.03804\" />\n" +
                    "<Peak massChargeRatio=\"284.276\" peak=\"0.05584\" />\n" +
                    "<Peak massChargeRatio=\"290.317\" peak=\"0.06004\" />\n" +
                    "<Peak massChargeRatio=\"297.349\" peak=\"0.02959\" />\n" +
                    "<Peak massChargeRatio=\"298.266\" peak=\"0.02583\" />\n" +
                    "<Peak massChargeRatio=\"309.375\" peak=\"0.00836\" />\n" +
                    "<Peak massChargeRatio=\"310.167\" peak=\"0.01865\" />\n" +
                    "<Peak massChargeRatio=\"312.761\" peak=\"0.02447\" />\n" +
                    "<Peak massChargeRatio=\"319.307\" peak=\"0.05149\" />\n" +
                    "<Peak massChargeRatio=\"344.216\" peak=\"0.01224\" />\n" +
                    "<Peak massChargeRatio=\"347.433\" peak=\"0.02577\" />\n" +
                    "<Peak massChargeRatio=\"352.859\" peak=\"0.02472\" />\n" +
                    "<Peak massChargeRatio=\"354.192\" peak=\"0.03354\" />\n" +
                    "<Peak massChargeRatio=\"355.161\" peak=\"0.01213\" />\n" +
                    "<Peak massChargeRatio=\"356.079\" peak=\"0.02013\" />\n" +
                    "<Peak massChargeRatio=\"358.542\" peak=\"0.0108\" />\n" +
                    "<Peak massChargeRatio=\"360.167\" peak=\"0.01041\" />\n" +
                    "<Peak massChargeRatio=\"363.089\" peak=\"0.00924\" />\n" +
                    "<Peak massChargeRatio=\"365.103\" peak=\"0.01216\" />\n" +
                    "<Peak massChargeRatio=\"367.415\" peak=\"0.01802\" />\n" +
                    "<Peak massChargeRatio=\"369.802\" peak=\"0.02797\" />\n" +
                    "<Peak massChargeRatio=\"373.123\" peak=\"0.00535\" />\n" +
                    "<Peak massChargeRatio=\"376.559\" peak=\"0.00648\" />\n" +
                    "<Peak massChargeRatio=\"378.848\" peak=\"0.05929\" />\n" +
                    "<Peak massChargeRatio=\"381.537\" peak=\"0.00148\" />\n" +
                    "<Peak massChargeRatio=\"383.132\" peak=\"0.00444\" />\n" +
                    "<Peak massChargeRatio=\"384.295\" peak=\"0.0344\" />\n" +
                    "<Peak massChargeRatio=\"385.301\" peak=\"0.01929\" />\n" +
                    "<Peak massChargeRatio=\"391.331\" peak=\"0.20185\" />\n" +
                    "<Peak massChargeRatio=\"392.48\" peak=\"0.02442\" />\n" +
                    "<Peak massChargeRatio=\"395.435\" peak=\"0.00935\" />\n" +
                    "<Peak massChargeRatio=\"396.469\" peak=\"0.001\" />\n" +
                    "<Peak massChargeRatio=\"400.558\" peak=\"0.03622\" />\n" +
                    "<Peak massChargeRatio=\"405.081\" peak=\"0.00745\" />\n" +
                    "<Peak massChargeRatio=\"409.287\" peak=\"0.02981\" />\n" +
                    "<Peak massChargeRatio=\"412.488\" peak=\"0.01913\" />\n" +
                    "<Peak massChargeRatio=\"413.61\" peak=\"0.03025\" />\n" +
                    "<Peak massChargeRatio=\"415.211\" peak=\"0.02661\" />\n" +
                    "<Peak massChargeRatio=\"418.524\" peak=\"0.02529\" />\n" +
                    "<Peak massChargeRatio=\"428.526\" peak=\"0.00269\" />\n" +
                    "<Peak massChargeRatio=\"431.21\" peak=\"0.06443\" />\n" +
                    "<Peak massChargeRatio=\"442.031\" peak=\"0.00063\" />\n" +
                    "<Peak massChargeRatio=\"443.434\" peak=\"0.00568\" />\n" +
                    "<Peak massChargeRatio=\"453.473\" peak=\"0.00212\" />\n" +
                    "<Peak massChargeRatio=\"456.405\" peak=\"0.01671\" />\n" +
                    "<Peak massChargeRatio=\"457.15\" peak=\"0.00929\" />\n" +
                    "<Peak massChargeRatio=\"466.255\" peak=\"0.00618\" />\n" +
                    "<Peak massChargeRatio=\"469.324\" peak=\"0.00031\" />\n" +
                    "<Peak massChargeRatio=\"470.16\" peak=\"0.01523\" />\n" +
                    "<Peak massChargeRatio=\"471.332\" peak=\"0.00969\" />\n" +
                    "<Peak massChargeRatio=\"474.574\" peak=\"0.00292\" />\n" +
                    "<Peak massChargeRatio=\"475.382\" peak=\"0.01801\" />\n" +
                    "<Peak massChargeRatio=\"479.402\" peak=\"0.00075\" />\n" +
                    "<Peak massChargeRatio=\"487.588\" peak=\"0.0084\" />\n" +
                    "<Peak massChargeRatio=\"489.601\" peak=\"0\" />\n" +
                    "<Peak massChargeRatio=\"492.4\" peak=\"0.14539\" />\n" +
                    "<Peak massChargeRatio=\"493.311\" peak=\"0.018\" />\n" +
                    "<Peak massChargeRatio=\"494.165\" peak=\"0.01095\" />\n" +
                    "<Peak massChargeRatio=\"503.537\" peak=\"0.00013\" />\n" +
                    "<Peak massChargeRatio=\"505.307\" peak=\"0.01961\" />\n" +
                    "<Peak massChargeRatio=\"509.396\" peak=\"0.0053\" />\n" +
                    "<Peak massChargeRatio=\"511.079\" peak=\"0.02332\" />\n" +
                    "<Peak massChargeRatio=\"518.169\" peak=\"0.02125\" />\n" +
                    "<Peak massChargeRatio=\"528.51\" peak=\"0.02084\" />\n" +
                    "<Peak massChargeRatio=\"529.392\" peak=\"0.01257\" />\n" +
                    "<Peak massChargeRatio=\"531.565\" peak=\"0.01119\" />\n" +
                    "<Peak massChargeRatio=\"532.452\" peak=\"0.0234\" />\n" +
                    "<Peak massChargeRatio=\"533.449\" peak=\"0.00319\" />\n" +
                    "<Peak massChargeRatio=\"542.511\" peak=\"0.00677\" />\n" +
                    "<Peak massChargeRatio=\"546.933\" peak=\"0.01507\" />\n" +
                    "<Peak massChargeRatio=\"548.622\" peak=\"0.0353\" />\n" +
                    "<Peak massChargeRatio=\"549.356\" peak=\"0.01502\" />\n" +
                    "<Peak massChargeRatio=\"550.405\" peak=\"0.00514\" />\n" +
                    "<Peak massChargeRatio=\"555.346\" peak=\"0.00836\" />\n" +
                    "<Peak massChargeRatio=\"556.694\" peak=\"0.01015\" />\n" +
                    "<Peak massChargeRatio=\"558.148\" peak=\"0.025\" />\n" +
                    "<Peak massChargeRatio=\"563.324\" peak=\"0.00172\" />\n" +
                    "<Peak massChargeRatio=\"567.006\" peak=\"0.20653\" />\n" +
                    "<Peak massChargeRatio=\"573.157\" peak=\"0.0041\" />\n" +
                    "<Peak massChargeRatio=\"577.676\" peak=\"0.01172\" />\n" +
                    "<Peak massChargeRatio=\"582.293\" peak=\"0.00051\" />\n" +
                    "<Peak massChargeRatio=\"583.326\" peak=\"0.00319\" />\n" +
                    "<Peak massChargeRatio=\"588.309\" peak=\"0.00976\" />\n" +
                    "<Peak massChargeRatio=\"600.866\" peak=\"0.00219\" />\n" +
                    "<Peak massChargeRatio=\"604.723\" peak=\"0.01204\" />\n" +
                    "<Peak massChargeRatio=\"605.477\" peak=\"0.06962\" />\n" +
                    "<Peak massChargeRatio=\"606.522\" peak=\"0.00878\" />\n" +
                    "<Peak massChargeRatio=\"613.36\" peak=\"0.01977\" />\n" +
                    "<Peak massChargeRatio=\"622.574\" peak=\"0.06997\" />\n" +
                    "<Peak massChargeRatio=\"629.733\" peak=\"0.01329\" />\n" +
                    "<Peak massChargeRatio=\"630.875\" peak=\"0.06269\" />\n" +
                    "<Peak massChargeRatio=\"631.589\" peak=\"0.2025\" />\n" +
                    "<Peak massChargeRatio=\"632.664\" peak=\"0.00667\" />\n" +
                    "<Peak massChargeRatio=\"639.282\" peak=\"0.01454\" />\n" +
                    "<Peak massChargeRatio=\"641.374\" peak=\"0.01386\" />\n" +
                    "<Peak massChargeRatio=\"654.469\" peak=\"0.00338\" />\n" +
                    "<Peak massChargeRatio=\"655.413\" peak=\"0.01222\" />\n" +
                    "<Peak massChargeRatio=\"657.501\" peak=\"0.00312\" />\n" +
                    "<Peak massChargeRatio=\"662.411\" peak=\"0.00824\" />\n" +
                    "<Peak massChargeRatio=\"685.26\" peak=\"0.00435\" />\n" +
                    "<Peak massChargeRatio=\"686.505\" peak=\"0.02106\" />\n" +
                    "<Peak massChargeRatio=\"688.115\" peak=\"0.04516\" />\n" +
                    "<Peak massChargeRatio=\"689.524\" peak=\"0.01522\" />\n" +
                    "<Peak massChargeRatio=\"690.979\" peak=\"0.00269\" />\n" +
                    "<Peak massChargeRatio=\"694.479\" peak=\"0.03013\" />\n" +
                    "<Peak massChargeRatio=\"697.625\" peak=\"0.00686\" />\n" +
                    "<Peak massChargeRatio=\"701.418\" peak=\"0.02343\" />\n" +
                    "<Peak massChargeRatio=\"702.415\" peak=\"0.01187\" />\n" +
                    "<Peak massChargeRatio=\"703.638\" peak=\"0.01372\" />\n" +
                    "<Peak massChargeRatio=\"708.164\" peak=\"0.00454\" />\n" +
                    "<Peak massChargeRatio=\"712.283\" peak=\"0.07224\" />\n" +
                    "<Peak massChargeRatio=\"712.9\" peak=\"0.01814\" />\n" +
                    "<Peak massChargeRatio=\"713.604\" peak=\"0.01238\" />\n" +
                    "<Peak massChargeRatio=\"715.158\" peak=\"0.02712\" />\n" +
                    "<Peak massChargeRatio=\"716.305\" peak=\"0.01961\" />\n" +
                    "<Peak massChargeRatio=\"717.844\" peak=\"0.17369\" />\n" +
                    "<Peak massChargeRatio=\"719.131\" peak=\"0.02812\" />\n" +
                    "<Peak massChargeRatio=\"721.019\" peak=\"0.01702\" />\n" +
                    "<Peak massChargeRatio=\"742.366\" peak=\"0.00916\" />\n" +
                    "<Peak massChargeRatio=\"744.477\" peak=\"0.0153\" />\n" +
                    "<Peak massChargeRatio=\"752.401\" peak=\"0.00188\" />\n" +
                    "<Peak massChargeRatio=\"755.507\" peak=\"0.00414\" />\n" +
                    "<Peak massChargeRatio=\"756.571\" peak=\"0.09265\" />\n" +
                    "<Peak massChargeRatio=\"757.438\" peak=\"0.00361\" />\n" +
                    "<Peak massChargeRatio=\"769.164\" peak=\"0.00987\" />\n" +
                    "<Peak massChargeRatio=\"770.405\" peak=\"0.02528\" />\n" +
                    "<Peak massChargeRatio=\"774.135\" peak=\"0.00097\" />\n" +
                    "<Peak massChargeRatio=\"775.105\" peak=\"0.01164\" />\n" +
                    "<Peak massChargeRatio=\"777.926\" peak=\"0.00972\" />\n" +
                    "<Peak massChargeRatio=\"781.163\" peak=\"0.00206\" />\n" +
                    "<Peak massChargeRatio=\"783.494\" peak=\"0.01433\" />\n" +
                    "<Peak massChargeRatio=\"785.24\" peak=\"0.01172\" />\n" +
                    "<Peak massChargeRatio=\"792.377\" peak=\"0.08065\" />\n" +
                    "<Peak massChargeRatio=\"795.037\" peak=\"0.02582\" />\n" +
                    "<Peak massChargeRatio=\"796.318\" peak=\"0.01612\" />\n" +
                    "<Peak massChargeRatio=\"797.341\" peak=\"0.00832\" />\n" +
                    "<Peak massChargeRatio=\"801.631\" peak=\"0.00321\" />\n" +
                    "<Peak massChargeRatio=\"811.561\" peak=\"0.00157\" />\n" +
                    "<Peak massChargeRatio=\"814.32\" peak=\"0.05717\" />\n" +
                    "<Peak massChargeRatio=\"819.895\" peak=\"0.00293\" />\n" +
                    "<Peak massChargeRatio=\"827.411\" peak=\"0.00546\" />\n" +
                    "<Peak massChargeRatio=\"828.455\" peak=\"0.0337\" />\n" +
                    "<Peak massChargeRatio=\"836.789\" peak=\"0.02963\" />\n" +
                    "<Peak massChargeRatio=\"837.775\" peak=\"0.00152\" />\n" +
                    "<Peak massChargeRatio=\"839.94\" peak=\"0.01961\" />\n" +
                    "<Peak massChargeRatio=\"844.554\" peak=\"0.00666\" />\n" +
                    "<Peak massChargeRatio=\"848.777\" peak=\"0.10803\" />\n" +
                    "<Peak massChargeRatio=\"849.479\" peak=\"0.05764\" />\n" +
                    "<Peak massChargeRatio=\"851.181\" peak=\"0.00035\" />\n" +
                    "<Peak massChargeRatio=\"852.887\" peak=\"0.01453\" />\n" +
                    "<Peak massChargeRatio=\"853.719\" peak=\"0.00764\" />\n" +
                    "<Peak massChargeRatio=\"855.861\" peak=\"0.01002\" />\n" +
                    "<Peak massChargeRatio=\"859.524\" peak=\"0.00455\" />\n" +
                    "<Peak massChargeRatio=\"861.411\" peak=\"0.02752\" />\n" +
                    "<Peak massChargeRatio=\"871.428\" peak=\"0.02791\" />\n" +
                    "<Peak massChargeRatio=\"878.005\" peak=\"0.01897\" />\n" +
                    "<Peak massChargeRatio=\"881.01\" peak=\"0.00574\" />\n" +
                    "<Peak massChargeRatio=\"883.313\" peak=\"0.00935\" />\n" +
                    "<Peak massChargeRatio=\"884.442\" peak=\"0.00171\" />\n" +
                    "<Peak massChargeRatio=\"899.624\" peak=\"0.10392\" />\n" +
                    "<Peak massChargeRatio=\"909.427\" peak=\"0.05477\" />\n" +
                    "<Peak massChargeRatio=\"910.47\" peak=\"0.01659\" />\n" +
                    "<Peak massChargeRatio=\"924.521\" peak=\"0.00196\" />\n" +
                    "<Peak massChargeRatio=\"927.39\" peak=\"0.204\" />\n" +
                    "<Peak massChargeRatio=\"928.386\" peak=\"0.08737\" />\n" +
                    "<Peak massChargeRatio=\"930.968\" peak=\"0.00321\" />\n" +
                    "<Peak massChargeRatio=\"932.115\" peak=\"0.0177\" />\n" +
                    "<Peak massChargeRatio=\"934.193\" peak=\"0.00718\" />\n" +
                    "<Peak massChargeRatio=\"935.514\" peak=\"0.01468\" />\n" +
                    "<Peak massChargeRatio=\"940.974\" peak=\"0.05251\" />\n" +
                    "<Peak massChargeRatio=\"942.791\" peak=\"0.00379\" />\n" +
                    "<Peak massChargeRatio=\"943.551\" peak=\"0.00692\" />\n" +
                    "<Peak massChargeRatio=\"948.597\" peak=\"0.0167\" />\n" +
                    "<Peak massChargeRatio=\"949.802\" peak=\"0.01698\" />\n" +
                    "<Peak massChargeRatio=\"954.534\" peak=\"0.00052\" />\n" +
                    "<Peak massChargeRatio=\"957.357\" peak=\"0.01603\" />\n" +
                    "<Peak massChargeRatio=\"971.159\" peak=\"0.01061\" />\n" +
                    "<Peak massChargeRatio=\"972.725\" peak=\"0.01571\" />\n" +
                    "<Peak massChargeRatio=\"974.642\" peak=\"0.00927\" />\n" +
                    "<Peak massChargeRatio=\"979.942\" peak=\"0.03088\" />\n" +
                    "<Peak massChargeRatio=\"983.677\" peak=\"0.00412\" />\n" +
                    "<Peak massChargeRatio=\"987.544\" peak=\"0.0054\" />\n" +
                    "<Peak massChargeRatio=\"988.658\" peak=\"0.04508\" />\n" +
                    "<Peak massChargeRatio=\"992.713\" peak=\"0.00399\" />\n" +
                    "<Peak massChargeRatio=\"993.656\" peak=\"0.00141\" />\n" +
                    "<Peak massChargeRatio=\"994.856\" peak=\"0.01288\" />\n" +
                    "<Peak massChargeRatio=\"996.033\" peak=\"0.00071\" />\n" +
                    "<Peak massChargeRatio=\"997.334\" peak=\"0.00731\" />\n" +
                    "<Peak massChargeRatio=\"1014.807\" peak=\"0.01502\" />\n" +
                    "<Peak massChargeRatio=\"1023.745\" peak=\"0.01072\" />\n" +
                    "<Peak massChargeRatio=\"1026.854\" peak=\"0.00613\" />\n" +
                    "<Peak massChargeRatio=\"1028.785\" peak=\"0.0048\" />\n" +
                    "<Peak massChargeRatio=\"1033.607\" peak=\"0.01754\" />\n" +
                    "<Peak massChargeRatio=\"1038.402\" peak=\"0.05457\" />\n" +
                    "<Peak massChargeRatio=\"1039.471\" peak=\"0.02034\" />\n" +
                    "<Peak massChargeRatio=\"1045.323\" peak=\"0.00675\" />\n" +
                    "<Peak massChargeRatio=\"1054.028\" peak=\"0.0003\" />\n" +
                    "<Peak massChargeRatio=\"1056.454\" peak=\"0.21284\" />\n" +
                    "<Peak massChargeRatio=\"1057.513\" peak=\"0.0937\" />\n" +
                    "<Peak massChargeRatio=\"1063.861\" peak=\"0.01671\" />\n" +
                    "<Peak massChargeRatio=\"1074.602\" peak=\"0.02735\" />\n" +
                    "<Peak massChargeRatio=\"1075.511\" peak=\"0.01818\" />\n" +
                    "<Peak massChargeRatio=\"1076.452\" peak=\"0.0102\" />\n" +
                    "<Peak massChargeRatio=\"1081.955\" peak=\"0.01572\" />\n" +
                    "<Peak massChargeRatio=\"1085.438\" peak=\"0.01286\" />\n" +
                    "<Peak massChargeRatio=\"1096.504\" peak=\"0.00461\" />\n" +
                    "<Peak massChargeRatio=\"1097.661\" peak=\"0.02979\" />\n" +
                    "<Peak massChargeRatio=\"1098.93\" peak=\"0.01962\" />\n" +
                    "<Peak massChargeRatio=\"1107.594\" peak=\"0.01466\" />\n" +
                    "<Peak massChargeRatio=\"1110.76\" peak=\"0.00946\" />\n" +
                    "<Peak massChargeRatio=\"1114.379\" peak=\"0.01233\" />\n" +
                    "<Peak massChargeRatio=\"1124.212\" peak=\"0.02777\" />\n" +
                    "<Peak massChargeRatio=\"1127.703\" peak=\"0.03128\" />\n" +
                    "<Peak massChargeRatio=\"1132.507\" peak=\"0.09537\" />\n" +
                    "<Peak massChargeRatio=\"1133.709\" peak=\"0.03709\" />\n" +
                    "<Peak massChargeRatio=\"1140.697\" peak=\"0.01583\" />\n" +
                    "<Peak massChargeRatio=\"1145.452\" peak=\"0.01813\" />\n" +
                    "<Peak massChargeRatio=\"1153.671\" peak=\"0.00711\" />\n" +
                    "<Peak massChargeRatio=\"1163.799\" peak=\"0.02937\" />\n" +
                    "<Peak massChargeRatio=\"1168.797\" peak=\"0.01981\" />\n" +
                    "<Peak massChargeRatio=\"1174.907\" peak=\"0.01619\" />\n" +
                    "<Peak massChargeRatio=\"1176.713\" peak=\"0.03025\" />\n" +
                    "<Peak massChargeRatio=\"1182.165\" peak=\"0.02313\" />\n" +
                    "<Peak massChargeRatio=\"1192.863\" peak=\"0.02152\" />\n" +
                    "<Peak massChargeRatio=\"1194.88\" peak=\"0.00875\" />\n" +
                    "<Peak massChargeRatio=\"1200.501\" peak=\"0.01689\" />\n" +
                    "<Peak massChargeRatio=\"1224.464\" peak=\"0.03332\" />\n" +
                    "<Peak massChargeRatio=\"1240.819\" peak=\"0.08962\" />\n" +
                    "<Peak massChargeRatio=\"1261.602\" peak=\"0.04225\" />\n" +
                    "<Peak massChargeRatio=\"1270.554\" peak=\"0.07525\" />\n" +
                    "<Peak massChargeRatio=\"1271.811\" peak=\"0.06138\" />\n" +
                    "<Peak massChargeRatio=\"1321.032\" peak=\"0.03244\" />\n" +
                    "<Peak massChargeRatio=\"1327.669\" peak=\"0.07062\" />\n" +
                    "<Peak massChargeRatio=\"1328.936\" peak=\"0.05675\" />\n" +
                    "<Peak massChargeRatio=\"1339.732\" peak=\"0.07731\" />\n" +
                    "<Peak massChargeRatio=\"1360.537\" peak=\"0.07952\" />\n" +
                    "<Peak massChargeRatio=\"1399.6\" peak=\"0.04708\" />\n" +
                    "<Peak massChargeRatio=\"1433.775\" peak=\"0.22471\" />\n" +
                    "<Peak massChargeRatio=\"1435.128\" peak=\"0.03966\" />\n" +
                    "</MeasuredSpectrum>\n" +
                    "</NormalizedRawScan>\n" +
                    "<match  peak=\"VNG0675C VNG0675C:196(19)\" score=\"55.16332907002614\" hyperscore=\"55.16332907002614\">\n" +
                    "<IonScore  A_count=\"0\" A_score=\"0.0\" B_count=\"1\" B_score=\"0.04299108777195215\" C_count=\"0\" C_score=\"0.0\" Y_count=\"4\" Y_score=\"0.2019975621951744\" X_count=\"0\" X_score=\"0.0\" Z_count=\"0\" Z_score=\"0.0\" />\n" +
                    "</match>\n" +
                    "</score>\n"
            ;

   // @Test  todo fix
    public void testScoreParse() {
        XTandemMain main = new XTandemMain(
                XTandemUtilities.getResourceStream("largeSample/tandem.params"),
                "largeSample/tandem.params");
        main.loadScoringTest();
        main.loadSpectra();
        IScoredScan scan1 = XTandemHadoopUtilities.readScoredScanX(SCORE_XML2, main);
        String s = scan1.getId();
        Assert.assertEquals("7868", s);
        Assert.assertEquals(393, scan1.getRaw().getPeaks().length);
        IMeasuredSpectrum cs = scan1.getConditionedScan();
        Assert.assertNotNull(cs);

        IMeasuredSpectrum rs = scan1.getNormalizedRawScan();
        Assert.assertNotNull(rs);

        Assert.assertEquals(0, cs.getPrecursorCharge());
        Assert.assertEquals(1460.233, cs.getPrecursorMass(), 0.02);


        ISpectrumPeak[] peaks = cs.getPeaks();
        Assert.assertEquals(253, peaks.length);
        Assert.assertEquals(215.36, peaks[1].getMassChargeRatio(), 0.02);
        Assert.assertEquals(0.02545, peaks[1].getPeak(), 0.0002);


        IScoredScan scan2 = XTandemHadoopUtilities.readScoredScanX(SCORE_XML, main);
        Assert.assertEquals(7858, scan2.getId());
        Assert.assertEquals(345, scan2.getRaw().getPeaks().length);

        cs = scan2.getConditionedScan();
        Assert.assertNotNull(cs);

        rs = scan2.getNormalizedRawScan();
        Assert.assertNotNull(rs);

        Assert.assertEquals(0, cs.getPrecursorCharge());
        Assert.assertEquals(2192.253, cs.getPrecursorMass(), 0.02);


        peaks = cs.getPeaks();
        Assert.assertEquals(320, peaks.length);
        Assert.assertEquals(342.281, peaks[1].getMassChargeRatio(), 0.02);
        Assert.assertEquals(0.02224, peaks[1].getPeak(), 0.0002);


    }


    // @Test  todo fix
    public void testLargeMXZMLParse() {
        XTandemMain main = new XTandemMain(
                XTandemUtilities.getResourceStream("largeSample/tandem_large.params"),
                "largeSample/tandem_large.params");
        main.loadScoringTest();
          main.loadSpectra();
    }

}
