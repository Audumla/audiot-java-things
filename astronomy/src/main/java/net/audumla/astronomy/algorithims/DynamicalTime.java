package net.audumla.astronomy.algorithims;

/*
 * *********************************************************************
 *  ORGANIZATION : audumla.net
 *  More information about this project can be found at the following locations:
 *  http://www.audumla.net/
 *  http://audumla.googlecode.com/
 * *********************************************************************
 *  Copyright (C) 2012 - 2013 Audumla.net
 *  Licensed under the Creative Commons Attribution-NonCommercial-NoDerivs 3.0 Unported License.
 *  You may not use this file except in compliance with the License located at http://creativecommons.org/licenses/by-nc-nd/3.0/
 *
 *  Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an
 *  "AS IS BASIS", WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and limitations under the License.
 */


public class DynamicalTime {

    public static DeltaTValue[] g_DeltaTValues = {new DeltaTValue(2441714.5, 43.4724), new DeltaTValue(2441742.5, 43.5648), new DeltaTValue(2441773.5, 43.6737), new DeltaTValue(2441803.5, 43.7782), new DeltaTValue(2441834.5, 43.8763), new DeltaTValue(2441864.5, 43.9562), new DeltaTValue(2441895.5, 44.0315), new DeltaTValue(2441926.5, 44.1132), new DeltaTValue(2441956.5, 44.1982), new DeltaTValue(2441987.5, 44.2952), new DeltaTValue(2442017.5, 44.3936), new DeltaTValue(2442048.5, 44.4841), new DeltaTValue(2442079.5, 44.5646), new DeltaTValue(2442107.5, 44.6425), new DeltaTValue(2442138.5, 44.7386), new DeltaTValue(2442168.5, 44.8370), new DeltaTValue(2442199.5, 44.9302), new DeltaTValue(2442229.5, 44.9986), new DeltaTValue(2442260.5, 45.0584), new DeltaTValue(2442291.5, 45.1284), new DeltaTValue(2442321.5, 45.2064), new DeltaTValue(2442352.5, 45.2980), new DeltaTValue(2442382.5, 45.3897), new DeltaTValue(2442413.5, 45.4761), new DeltaTValue(2442444.5, 45.5633), new DeltaTValue(2442472.5, 45.6450), new DeltaTValue(2442503.5, 45.7375), new DeltaTValue(2442533.5, 45.8284), new DeltaTValue(2442564.5, 45.9133), new DeltaTValue(2442594.5, 45.9820), new DeltaTValue(2442625.5, 46.0408), new DeltaTValue(2442656.5, 46.1067), new DeltaTValue(2442686.5, 46.1825), new DeltaTValue(2442717.5, 46.2789), new DeltaTValue(2442747.5, 46.3713), new DeltaTValue(2442778.5, 46.4567), new DeltaTValue(2442809.5, 46.5445), new DeltaTValue(2442838.5, 46.6311), new DeltaTValue(2442869.5, 46.7302), new DeltaTValue(2442899.5, 46.8284), new DeltaTValue(2442930.5, 46.9247), new DeltaTValue(2442960.5, 46.9970), new DeltaTValue(2442991.5, 47.0709), new DeltaTValue(2443022.5, 47.1451), new DeltaTValue(2443052.5, 47.2362), new DeltaTValue(2443083.5, 47.3413), new DeltaTValue(2443113.5, 47.4319), new DeltaTValue(2443144.5, 47.5214), new DeltaTValue(2443175.5, 47.6049), new DeltaTValue(2443203.5, 47.6837), new DeltaTValue(2443234.5, 47.7781), new DeltaTValue(2443264.5, 47.8771), new DeltaTValue(2443295.5, 47.9687), new DeltaTValue(2443325.5, 48.0348), new DeltaTValue(2443356.5, 48.0942), new DeltaTValue(2443387.5, 48.1608), new DeltaTValue(2443417.5, 48.2460), new DeltaTValue(2443448.5, 48.3439), new DeltaTValue(2443478.5, 48.4355), new DeltaTValue(2443509.5, 48.5344), new DeltaTValue(2443540.5, 48.6325), new DeltaTValue(2443568.5, 48.7294), new DeltaTValue(2443599.5, 48.8365), new DeltaTValue(2443629.5, 48.9353), new DeltaTValue(2443660.5, 49.0319), new DeltaTValue(2443690.5, 49.1013), new DeltaTValue(2443721.5, 49.1591), new DeltaTValue(2443752.5, 49.2286), new DeltaTValue(2443782.5, 49.3070), new DeltaTValue(2443813.5, 49.4018), new DeltaTValue(2443843.5, 49.4945), new DeltaTValue(2443874.5, 49.5862), new DeltaTValue(2443905.5, 49.6805), new DeltaTValue(2443933.5, 49.7602), new DeltaTValue(2443964.5, 49.8556), new DeltaTValue(2443994.5, 49.9489), new DeltaTValue(2444025.5, 50.0347), new DeltaTValue(2444055.5, 50.1019), new DeltaTValue(2444086.5, 50.1622), new DeltaTValue(2444117.5, 50.2260), new DeltaTValue(2444147.5, 50.2968), new DeltaTValue(2444178.5, 50.3831), new DeltaTValue(2444208.5, 50.4599), new DeltaTValue(2444239.5, 50.5387), new DeltaTValue(2444270.5, 50.6161), new DeltaTValue(2444299.5, 50.6866), new DeltaTValue(2444330.5, 50.7658), new DeltaTValue(2444360.5, 50.8454), new DeltaTValue(2444391.5, 50.9187), new DeltaTValue(2444421.5, 50.9761), new DeltaTValue(2444452.5, 51.0278), new DeltaTValue(2444483.5, 51.0843), new DeltaTValue(2444513.5, 51.1538), new DeltaTValue(2444544.5, 51.2319), new DeltaTValue(2444574.5, 51.3063), new DeltaTValue(2444605.5, 51.3808), new DeltaTValue(2444636.5, 51.4526), new DeltaTValue(2444664.5, 51.5160), new DeltaTValue(2444695.5, 51.5985), new DeltaTValue(2444725.5, 51.6809), new DeltaTValue(2444756.5, 51.7573), new DeltaTValue(2444786.5, 51.8133), new DeltaTValue(2444817.5, 51.8532), new DeltaTValue(2444848.5, 51.9014), new DeltaTValue(2444878.5, 51.9603), new DeltaTValue(2444909.5, 52.0328), new DeltaTValue(2444939.5, 52.0985), new DeltaTValue(2444970.5, 52.1668), new DeltaTValue(2445001.5, 52.2316), new DeltaTValue(2445029.5, 52.2938), new DeltaTValue(2445060.5, 52.3680), new DeltaTValue(2445090.5, 52.4465), new DeltaTValue(2445121.5, 52.5180), new DeltaTValue(2445151.5, 52.5752), new DeltaTValue(2445182.5, 52.6178), new DeltaTValue(2445213.5, 52.6668), new DeltaTValue(2445243.5, 52.7340), new DeltaTValue(2445274.5, 52.8056), new DeltaTValue(2445304.5, 52.8792), new DeltaTValue(2445335.5, 52.9565), new DeltaTValue(2445366.5, 53.0445), new DeltaTValue(2445394.5, 53.1268), new DeltaTValue(2445425.5, 53.2197), new DeltaTValue(2445455.5, 53.3024), new DeltaTValue(2445486.5, 53.3747), new DeltaTValue(2445516.5, 53.4335), new DeltaTValue(2445547.5, 53.4778), new DeltaTValue(2445578.5, 53.5300), new DeltaTValue(2445608.5, 53.5845), new DeltaTValue(2445639.5, 53.6523), new DeltaTValue(2445669.5, 53.7256), new DeltaTValue(2445700.5, 53.7882), new DeltaTValue(2445731.5, 53.8367), new DeltaTValue(2445760.5, 53.8830), new DeltaTValue(2445791.5, 53.9443), new DeltaTValue(2445821.5, 54.0042), new DeltaTValue(2445852.5, 54.0536), new DeltaTValue(2445882.5, 54.0856), new DeltaTValue(2445913.5, 54.1084), new DeltaTValue(2445944.5, 54.1463), new DeltaTValue(2445974.5, 54.1914), new DeltaTValue(2446005.5, 54.2452), new DeltaTValue(2446035.5, 54.2958), new DeltaTValue(2446066.5, 54.3427), new DeltaTValue(2446097.5, 54.3911), new DeltaTValue(2446125.5, 54.4320), new DeltaTValue(2446156.5, 54.4898), new DeltaTValue(2446186.5, 54.5456), new DeltaTValue(2446217.5, 54.5977), new DeltaTValue(2446247.5, 54.6355), new DeltaTValue(2446278.5, 54.6532), new DeltaTValue(2446309.5, 54.6776), new DeltaTValue(2446339.5, 54.7174), new DeltaTValue(2446370.5, 54.7741), new DeltaTValue(2446400.5, 54.8253), new DeltaTValue(2446431.5, 54.8713), new DeltaTValue(2446462.5, 54.9161), new DeltaTValue(2446490.5, 54.9581), new DeltaTValue(2446521.5, 54.9997), new DeltaTValue(2446551.5, 55.0476), new DeltaTValue(2446582.5, 55.0912), new DeltaTValue(2446612.5, 55.1132), new DeltaTValue(2446643.5, 55.1328), new DeltaTValue(2446674.5, 55.1532), new DeltaTValue(2446704.5, 55.1898), new DeltaTValue(2446735.5, 55.2416), new DeltaTValue(2446765.5, 55.2838), new DeltaTValue(2446796.5, 55.3222), new DeltaTValue(2446827.5, 55.3613), new DeltaTValue(2446855.5, 55.4063), new DeltaTValue(2446886.5, 55.4629), new DeltaTValue(2446916.5, 55.5111), new DeltaTValue(2446947.5, 55.5524), new DeltaTValue(2446977.5, 55.5812), new DeltaTValue(2447008.5, 55.6004), new DeltaTValue(2447039.5, 55.6262), new DeltaTValue(2447069.5, 55.6656), new DeltaTValue(2447100.5, 55.7168), new DeltaTValue(2447130.5, 55.7698), new DeltaTValue(2447161.5, 55.8197), new DeltaTValue(2447192.5, 55.8615), new DeltaTValue(2447221.5, 55.9130), new DeltaTValue(2447252.5, 55.9663), new DeltaTValue(2447282.5, 56.0220), new DeltaTValue(2447313.5, 56.0700), new DeltaTValue(2447343.5, 56.0939), new DeltaTValue(2447374.5, 56.1105), new DeltaTValue(2447405.5, 56.1314), new DeltaTValue(2447435.5, 56.1611), new DeltaTValue(2447466.5, 56.2068), new DeltaTValue(2447496.5, 56.2583), new DeltaTValue(2447527.5, 56.3000), new DeltaTValue(2447558.5, 56.3399), new DeltaTValue(2447586.5, 56.3790), new DeltaTValue(2447617.5, 56.4283), new DeltaTValue(2447647.5, 56.4804), new DeltaTValue(2447678.5, 56.5352), new DeltaTValue(2447708.5, 56.5697), new DeltaTValue(2447739.5, 56.5983), new DeltaTValue(2447770.5, 56.6328), new DeltaTValue(2447800.5, 56.6739), new DeltaTValue(2447831.5, 56.7332), new DeltaTValue(2447861.5, 56.7972), new DeltaTValue(2447892.5, 56.8553), new DeltaTValue(2447923.5, 56.9111), new DeltaTValue(2447951.5, 56.9755), new DeltaTValue(2447982.5, 57.0471), new DeltaTValue(2448012.5, 57.1136), new DeltaTValue(2448043.5, 57.1738), new DeltaTValue(2448073.5, 57.2226), new DeltaTValue(2448104.5, 57.2597), new DeltaTValue(2448135.5, 57.3073), new DeltaTValue(2448165.5, 57.3643), new DeltaTValue(2448196.5, 57.4334), new DeltaTValue(2448226.5, 57.5016), new DeltaTValue(2448257.5, 57.5653), new DeltaTValue(2448288.5, 57.6333), new DeltaTValue(2448316.5, 57.6973), new DeltaTValue(2448347.5, 57.7711), new DeltaTValue(2448377.5, 57.8407), new DeltaTValue(2448408.5, 57.9058), new DeltaTValue(2448438.5, 57.9576), new DeltaTValue(2448469.5, 57.9975), new DeltaTValue(2448500.5, 58.0426), new DeltaTValue(2448530.5, 58.1043), new DeltaTValue(2448561.5, 58.1679), new DeltaTValue(2448591.5, 58.2389), new DeltaTValue(2448622.5, 58.3092), new DeltaTValue(2448653.5, 58.3833), new DeltaTValue(2448682.5, 58.4537), new DeltaTValue(2448713.5, 58.5401), new DeltaTValue(2448743.5, 58.6228), new DeltaTValue(2448774.5, 58.6917), new DeltaTValue(2448804.5, 58.7410), new DeltaTValue(2448835.5, 58.7836), new DeltaTValue(2448866.5, 58.8406), new DeltaTValue(2448896.5, 58.8986), new DeltaTValue(2448927.5, 58.9714), new DeltaTValue(2448957.5, 59.0438), new DeltaTValue(2448988.5, 59.1218), new DeltaTValue(2449019.5, 59.2003), new DeltaTValue(2449047.5, 59.2747), new DeltaTValue(2449078.5, 59.3574), new DeltaTValue(2449108.5, 59.4434), new DeltaTValue(2449139.5, 59.5242), new DeltaTValue(2449169.5, 59.5850), new DeltaTValue(2449200.5, 59.6344), new DeltaTValue(2449231.5, 59.6928), new DeltaTValue(2449261.5, 59.7588), new DeltaTValue(2449292.5, 59.8386), new DeltaTValue(2449322.5, 59.9111), new DeltaTValue(2449353.5, 59.9845), new DeltaTValue(2449384.5, 60.0564), new DeltaTValue(2449412.5, 60.1231), new DeltaTValue(2449443.5, 60.2042), new DeltaTValue(2449473.5, 60.2804), new DeltaTValue(2449504.5, 60.3530), new DeltaTValue(2449534.5, 60.4012), new DeltaTValue(2449565.5, 60.4440), new DeltaTValue(2449596.5, 60.4900), new DeltaTValue(2449626.5, 60.5578), new DeltaTValue(2449657.5, 60.6324), new DeltaTValue(2449687.5, 60.7059), new DeltaTValue(2449718.5, 60.7853), new DeltaTValue(2449749.5, 60.8664), new DeltaTValue(2449777.5, 60.9387), new DeltaTValue(2449808.5, 61.0277), new DeltaTValue(2449838.5, 61.1103), new DeltaTValue(2449869.5, 61.1870), new DeltaTValue(2449899.5, 61.2454), new DeltaTValue(2449930.5, 61.2881), new DeltaTValue(2449961.5, 61.3378), new DeltaTValue(2449991.5, 61.4036), new DeltaTValue(2450022.5, 61.4760), new DeltaTValue(2450052.5, 61.5525), new DeltaTValue(2450083.5, 61.6287), new DeltaTValue(2450114.5, 61.6846), new DeltaTValue(2450143.5, 61.7433), new DeltaTValue(2450174.5, 61.8132), new DeltaTValue(2450204.5, 61.8823), new DeltaTValue(2450235.5, 61.9497), new DeltaTValue(2450265.5, 61.9969), new DeltaTValue(2450296.5, 62.0343), new DeltaTValue(2450327.5, 62.0714), new DeltaTValue(2450357.5, 62.1202), new DeltaTValue(2450388.5, 62.1810), new DeltaTValue(2450418.5, 62.2382), new DeltaTValue(2450449.5, 62.2950), new DeltaTValue(2450480.5, 62.3506), new DeltaTValue(2450508.5, 62.3995), new DeltaTValue(2450539.5, 62.4754), new DeltaTValue(2450569.5, 62.5463), new DeltaTValue(2450600.5, 62.6136), new DeltaTValue(2450630.5, 62.6571), new DeltaTValue(2450661.5, 62.6942), new DeltaTValue(2450692.5, 62.7383), new DeltaTValue(2450722.5, 62.7926), new DeltaTValue(2450753.5, 62.8567), new DeltaTValue(2450783.5, 62.9146), new DeltaTValue(2450814.5, 62.9659), new DeltaTValue(2450845.5, 63.0217), new DeltaTValue(2450873.5, 63.0807), new DeltaTValue(2450904.5, 63.1462), new DeltaTValue(2450934.5, 63.2053), new DeltaTValue(2450965.5, 63.2599), new DeltaTValue(2450995.5, 63.2844), new DeltaTValue(2451026.5, 63.2961), new DeltaTValue(2451057.5, 63.3126), new DeltaTValue(2451087.5, 63.3422), new DeltaTValue(2451118.5, 63.3871), new DeltaTValue(2451148.5, 63.4339), new DeltaTValue(2451179.5, 63.4673), new DeltaTValue(2451210.5, 63.4979), new DeltaTValue(2451238.5, 63.5319), new DeltaTValue(2451269.5, 63.5679), new DeltaTValue(2451299.5, 63.6104), new DeltaTValue(2451330.5, 63.6444), new DeltaTValue(2451360.5, 63.6642), new DeltaTValue(2451391.5, 63.6739), new DeltaTValue(2451422.5, 63.6926), new DeltaTValue(2451452.5, 63.7147), new DeltaTValue(2451483.5, 63.7518), new DeltaTValue(2451513.5, 63.7927), new DeltaTValue(2451544.5, 63.8285), new DeltaTValue(2451575.5, 63.8557), new DeltaTValue(2451604.5, 63.8804), new DeltaTValue(2451635.5, 63.9075), new DeltaTValue(2451665.5, 63.9393), new DeltaTValue(2451696.5, 63.9691), new DeltaTValue(2451726.5, 63.9799), new DeltaTValue(2451757.5, 63.9833), new DeltaTValue(2451788.5, 63.9938), new DeltaTValue(2451818.5, 64.0093), new DeltaTValue(2451849.5, 64.0400), new DeltaTValue(2451879.5, 64.0670), new DeltaTValue(2451910.5, 64.0908), new DeltaTValue(2451941.5, 64.1068), new DeltaTValue(2451969.5, 64.1282), new DeltaTValue(2452000.5, 64.1584), new DeltaTValue(2452030.5, 64.1833), new DeltaTValue(2452061.5, 64.2094), new DeltaTValue(2452091.5, 64.2117), new DeltaTValue(2452122.5, 64.2073), new DeltaTValue(2452153.5, 64.2116), new DeltaTValue(2452183.5, 64.2223), new DeltaTValue(2452214.5, 64.2500), new DeltaTValue(2452244.5, 64.2761), new DeltaTValue(2452275.5, 64.2998), new DeltaTValue(2452306.5, 64.3192), new DeltaTValue(2452334.5, 64.3450), new DeltaTValue(2452365.5, 64.3735), new DeltaTValue(2452395.5, 64.3943), new DeltaTValue(2452426.5, 64.4151), new DeltaTValue(2452456.5, 64.4132), new DeltaTValue(2452487.5, 64.4118), new DeltaTValue(2452518.5, 64.4097), new DeltaTValue(2452548.5, 64.4168), new DeltaTValue(2452579.5, 64.4329), new DeltaTValue(2452609.5, 64.4511), new DeltaTValue(2452640.5, 64.4734), new DeltaTValue(2452671.5, 64.4893), new DeltaTValue(2452699.5, 64.5053), new DeltaTValue(2452730.5, 64.5269), new DeltaTValue(2452760.5, 64.5471), new DeltaTValue(2452791.5, 64.5597), new DeltaTValue(2452821.5, 64.5512), new DeltaTValue(2452852.5, 64.5371), new DeltaTValue(2452883.5, 64.5359), new DeltaTValue(2452913.5, 64.5415), new DeltaTValue(2452944.5, 64.5544), new DeltaTValue(2452974.5, 64.5654), new DeltaTValue(2453005.5, 64.5736), new DeltaTValue(2453036.5, 64.5891), new DeltaTValue(2453065.5, 64.6015), new DeltaTValue(2453096.5, 64.6176), new DeltaTValue(2453126.5, 64.6374), new DeltaTValue(2453157.5, 64.6549), new DeltaTValue(2453187.5, 64.6530), new DeltaTValue(2453218.5, 64.6379), new DeltaTValue(2453249.5, 64.6372), new DeltaTValue(2453279.5, 64.6400), new DeltaTValue(2453310.5, 64.6543), new DeltaTValue(2453340.5, 64.6723), new DeltaTValue(2453371.5, 64.6876), new DeltaTValue(2453402.5, 64.7052), new DeltaTValue(2453430.5, 64.7313), new DeltaTValue(2453461.5, 64.7575), new DeltaTValue(2453491.5, 64.7811), new DeltaTValue(2453522.5, 64.8001), new DeltaTValue(2453552.5, 64.7995), new DeltaTValue(2453583.5, 64.7876), new DeltaTValue(2453614.5, 64.7831), new DeltaTValue(2453644.5, 64.7921), new DeltaTValue(2453675.5, 64.8096), new DeltaTValue(2453705.5, 64.8311), new DeltaTValue(2453736.5, 64.8452), new DeltaTValue(2453767.5, 64.8597), new DeltaTValue(2453795.5, 64.8850), new DeltaTValue(2453826.5, 64.9175), new DeltaTValue(2453856.5, 64.9480), new DeltaTValue(2453887.5, 64.9794), new DeltaTValue(2453917.5, 64.9895), new DeltaTValue(2453948.5, 65.0028), new DeltaTValue(2453979.5, 65.0138), new DeltaTValue(2454009.5, 65.0371), new DeltaTValue(2454040.5, 65.0773), new DeltaTValue(2454070.5, 65.1122), new DeltaTValue(2454101.5, 65.1464), new DeltaTValue(2454132.5, 65.1833), new DeltaTValue(2454160.5, 65.2145), new DeltaTValue(2454191.5, 65.2494), new DeltaTValue(2454221.5, 65.2921), new DeltaTValue(2454252.5, 65.3279), new DeltaTValue(2454282.5, 65.3413), new DeltaTValue(2454313.5, 65.3452), new DeltaTValue(2454344.5, 65.3496), new DeltaTValue(2454374.5, 65.3711), new DeltaTValue(2454405.5, 65.3972), new DeltaTValue(2454435.5, 65.4296), new DeltaTValue(2454466.5, 65.4573), new DeltaTValue(2454497.5, 65.4868), new DeltaTValue(2454526.5, 65.5152), new DeltaTValue(2454557.5, 65.5450), new DeltaTValue(2454587.5, 65.5781), new DeltaTValue(2454618.5, 65.6127), new DeltaTValue(2454648.5, 65.6288), new DeltaTValue(2454679.5, 65.6370), new DeltaTValue(2454710.5, 65.6493), new DeltaTValue(2454740.5, 65.6760), new DeltaTValue(2454771.5, 65.7097), new DeltaTValue(2454801.5, 65.7461), new DeltaTValue(2454832.5, 65.7768), new DeltaTValue(2454863.5, 65.8025), new DeltaTValue(2454891.5, 65.8237), new DeltaTValue(2454922.5, 65.8595), new DeltaTValue(2454952.5, 65.8973), new DeltaTValue(2454983.5, 65.9323), new DeltaTValue(2455013.5, 65.9509), new DeltaTValue(2455044.5, 65.9534), new DeltaTValue(2455075.5, 65.9628), new DeltaTValue(2455105.5, 65.9839), new DeltaTValue(2455136.5, 66.0147), new DeltaTValue(2455166.5, 66.0420), new DeltaTValue(2455197.5, 66.0699), new DeltaTValue(2455228.5, 66.0961), new DeltaTValue(2455256.5, 66.1310), new DeltaTValue(2455287.5, 66.1683), new DeltaTValue(2455317.5, 66.2072), new DeltaTValue(2455348.5, 66.2356), new DeltaTValue(2455378.5, 66.2409), new DeltaTValue(2455409.5, 66.2335), new DeltaTValue(2455440.5, 66.2349), new DeltaTValue(2455470.5, 66.2441), new DeltaTValue(2455501.5, 66.2751), new DeltaTValue(2455531.5, 66.3054), new DeltaTValue(2455562.5, 66.3246), new DeltaTValue(2455593.5, 66.3406), new DeltaTValue(2455621.5, 66.3624), new DeltaTValue(2455652.5, 66.3957), new DeltaTValue(2455682.5, 66.4289), new DeltaTValue(2455713.5, 66.4619), new DeltaTValue(2455743.5, 66.4749), new DeltaTValue(2455774.5, 66.4751), new DeltaTValue(2455805.5, 66.4829), new DeltaTValue(2455835.5, 66.5056), new DeltaTValue(2455866.5, 66.5383), new DeltaTValue(2455896.5, 66.5706), new DeltaTValue(2455927.5, 66.6030), new DeltaTValue(2455958.5, 66.6340), new DeltaTValue(2455987.5, 66.6569), new DeltaTValue(2456018.5, 66.6925), new DeltaTValue(2456048.5, 66.7289), new DeltaTValue(2456079.5, 66.7579), new DeltaTValue(2456109.5, 66.7708), new DeltaTValue(2456140.5, 66.7740), new DeltaTValue(2456171.5, 66.7846), new DeltaTValue(2456201.5, 66.8103), new DeltaTValue(2456232.5, 66.8400), new DeltaTValue(2456262.5, 66.8779), new DeltaTValue(2456293.5, 66.9069), new DeltaTValue(2456324.5, 66.9443), new DeltaTValue(2456352.5, 66.9763), new DeltaTValue(2456383.5, 67.0258), new DeltaTValue(2456476.0, 67.121), new DeltaTValue(2456567.25, 67.158), new DeltaTValue(2456658.5, 67.267), new DeltaTValue(2456749.75, 67.379), new DeltaTValue(2456841.0, 67.7), new DeltaTValue(2456932.25, 67.8), new DeltaTValue(2457023.5, 67.9), new DeltaTValue(2457114.75, 68.0), new DeltaTValue(2457206.00, 68.1), new DeltaTValue(2457297.25, 68.3), new DeltaTValue(2457388.50, 68.4), new DeltaTValue(2457480.00, 68.5), new DeltaTValue(2457571.50, 69), new DeltaTValue(2457663.00, 69), new DeltaTValue(2457754.50, 69), new DeltaTValue(2457845.75, 69), new DeltaTValue(2457937.00, 69), new DeltaTValue(2458028.25, 69), new DeltaTValue(2458119.50, 69), new DeltaTValue(2458210.75, 69), new DeltaTValue(2458302.00, 70), new DeltaTValue(2458393.25, 70), new DeltaTValue(2458484.50, 70), new DeltaTValue(2458575.75, 70), new DeltaTValue(2458667.00, 70), new DeltaTValue(2458758.25, 70), new DeltaTValue(2458849.50, 70), new DeltaTValue(2458941.00, 70), new DeltaTValue(2459032.50, 71), new DeltaTValue(2459124.00, 71), new DeltaTValue(2459215.50, 71), new DeltaTValue(2459306.75, 71), new DeltaTValue(2459398.00, 71), new DeltaTValue(2459489.25, 71), new DeltaTValue(2459580.50, 71), new DeltaTValue(2459671.75, 71), new DeltaTValue(2459763.00, 71), new DeltaTValue(2459854.25, 72), new DeltaTValue(2459945.50, 72)};
    //Note as currently coded there is a single discontinuity of c. 1.28 seconds on 1 January 2023. At this point http://maia.usno.navy.mil/ser7/deltat.preds indicates an error value for DeltaT of about 5 seconds anyway.
    public static LeapSecondCoefficient[] g_LeapSecondCoefficients = {new LeapSecondCoefficient(2437300.5, 1.4228180, 37300, 0.001296),new LeapSecondCoefficient(2437512.5, 1.3728180, 37300, 0.001296),new LeapSecondCoefficient(2437665.5, 1.8458580, 37665, 0.0011232),new LeapSecondCoefficient(2438334.5, 1.9458580, 37665, 0.0011232),new LeapSecondCoefficient(2438395.5, 3.2401300, 38761, 0.001296),new LeapSecondCoefficient(2438486.5, 3.3401300, 38761, 0.001296),new LeapSecondCoefficient(2438639.5, 3.4401300, 38761, 0.001296),new LeapSecondCoefficient(2438761.5, 3.5401300, 38761, 0.001296),new LeapSecondCoefficient(2438820.5, 3.6401300, 38761, 0.001296),new LeapSecondCoefficient(2438942.5, 3.7401300, 38761, 0.001296),new LeapSecondCoefficient(2439004.5, 3.8401300, 38761, 0.001296),new LeapSecondCoefficient(2439126.5, 4.3131700, 39126, 0.002592),new LeapSecondCoefficient(2439887.5, 4.2131700, 39126, 0.002592),new LeapSecondCoefficient(2441317.5, 10.0, 41317, 0.0),new LeapSecondCoefficient(2441499.5, 11.0, 41317, 0.0),new LeapSecondCoefficient(2441683.5, 12.0, 41317, 0.0),new LeapSecondCoefficient(2442048.5, 13.0, 41317, 0.0),new LeapSecondCoefficient(2442413.5, 14.0, 41317, 0.0),new LeapSecondCoefficient(2442778.5, 15.0, 41317, 0.0),new LeapSecondCoefficient(2443144.5, 16.0, 41317, 0.0),new LeapSecondCoefficient(2443509.5, 17.0, 41317, 0.0),new LeapSecondCoefficient(2443874.5, 18.0, 41317, 0.0),new LeapSecondCoefficient(2444239.5, 19.0, 41317, 0.0),new LeapSecondCoefficient(2444786.5, 20.0, 41317, 0.0),new LeapSecondCoefficient(2445151.5, 21.0, 41317, 0.0),new LeapSecondCoefficient(2445516.5, 22.0, 41317, 0.0),new LeapSecondCoefficient(2446247.5, 23.0, 41317, 0.0),new LeapSecondCoefficient(2447161.5, 24.0, 41317, 0.0),new LeapSecondCoefficient(2447892.5, 25.0, 41317, 0.0),new LeapSecondCoefficient(2448257.5, 26.0, 41317, 0.0),new LeapSecondCoefficient(2448804.5, 27.0, 41317, 0.0),new LeapSecondCoefficient(2449169.5, 28.0, 41317, 0.0),new LeapSecondCoefficient(2449534.5, 29.0, 41317, 0.0),new LeapSecondCoefficient(2450083.5, 30.0, 41317, 0.0),new LeapSecondCoefficient(2450630.5, 31.0, 41317, 0.0),new LeapSecondCoefficient(2451179.5, 32.0, 41317, 0.0),new LeapSecondCoefficient(2453736.5, 33.0, 41317, 0.0),new LeapSecondCoefficient(2454832.5, 34.0, 41317, 0.0),new LeapSecondCoefficient(2456109.5, 35.0, 41317, 0.0)};

    public static double deltaT(double JD) {
        //What will be the return value from the method
        double Delta = 0;

        //Determine if we can use the lookup table
        int nLookupElements = g_DeltaTValues.length;
        if ((JD >= g_DeltaTValues[0].JD) && (JD < g_DeltaTValues[nLookupElements - 1].JD)) {
            //Find the index in the lookup table which contains the JD value closest to the JD input parameter
            boolean bFound = false;
            int nFoundIndex = 0;
            while (!bFound) {
                assert nFoundIndex < nLookupElements;
                bFound = (g_DeltaTValues[nFoundIndex].JD > JD);

                //Prepare for the next loop
                if (!bFound) {
                    ++nFoundIndex;
                } else {
                    //Now do a simple linear interpolation of the DeltaT values from the lookup table
                    Delta = (JD - g_DeltaTValues[nFoundIndex - 1].JD) / (g_DeltaTValues[nFoundIndex].JD - g_DeltaTValues[nFoundIndex - 1].JD) * (g_DeltaTValues[nFoundIndex].DeltaT - g_DeltaTValues[nFoundIndex - 1].DeltaT) + g_DeltaTValues[nFoundIndex - 1].DeltaT;
                }
            }
        } else {
            JulianDate date = new JulianDate(JD, JulianDate.afterPapalReform(JD));
            double y = date.fractionalYear();

            //Use the polynomial expressions from Espenak & Meeus 2006. References: http://eclipse.gsfc.nasa.gov/SEcat5/deltatpoly.html and
            //http://www.staff.science.uu.nl/~gent0113/deltat/deltat_old.htm (Espenak & Meeus 2006 section)
            if (y < -500) {
                double u = (y - 1820) / 100.0;
                double u2 = u * u;
                Delta = -20 + (32 * u2);
            } else if (y < 500) {
                double u = y / 100.0;
                double u2 = u * u;
                double u3 = u2 * u;
                double u4 = u3 * u;
                double u5 = u4 * u;
                double u6 = u5 * u;
                Delta = 10583.6 + (-1014.41 * u) + (33.78311 * u2) + (-5.952053 * u3) + (-0.1798452 * u4) + (0.022174192 * u5) + (0.0090316521 * u6);
            } else if (y < 1600) {
                double u = (y - 1000) / 100.0;
                double u2 = u * u;
                double u3 = u2 * u;
                double u4 = u3 * u;
                double u5 = u4 * u;
                double u6 = u5 * u;
                Delta = 1574.2 + (-556.01 * u) + (71.23472 * u2) + (0.319781 * u3) + (-0.8503463 * u4) + (-0.005050998 * u5) + (0.0083572073 * u6);
            } else if (y < 1700) {
                double u = (y - 1600) / 100.0;
                double u2 = u * u;
                double u3 = u2 * u;
                Delta = 120 + (-98.08 * u) + (-153.2 * u2) + (u3 / 0.007129);
            } else if (y < 1800) {
                double u = (y - 1700) / 100.0;
                double u2 = u * u;
                double u3 = u2 * u;
                double u4 = u3 * u;
                Delta = 8.83 + (16.03 * u) + (-59.285 * u2) + (133.36 * u3) + (-u4 / 0.01174);
            } else if (y < 1860) {
                double u = (y - 1800) / 100.0;
                double u2 = u * u;
                double u3 = u2 * u;
                double u4 = u3 * u;
                double u5 = u4 * u;
                double u6 = u5 * u;
                double u7 = u6 * u;
                Delta = 13.72 + (-33.2447 * u) + (68.612 * u2) + (4111.6 * u3) + (-37436 * u4) + (121272 * u5) + (-169900 * u6) + (87500 * u7);
            } else if (y < 1900) {
                double u = (y - 1860) / 100.0;
                double u2 = u * u;
                double u3 = u2 * u;
                double u4 = u3 * u;
                double u5 = u4 * u;
                Delta = 7.62 + (57.37 * u) + (-2517.54 * u2) + (16806.68 * u3) + (-44736.24 * u4) + (u5 / 0.0000233174);
            } else if (y < 1920) {
                double u = (y - 1900) / 100.0;
                double u2 = u * u;
                double u3 = u2 * u;
                double u4 = u3 * u;
                Delta = -2.79 + (149.4119 * u) + (-598.939 * u2) + (6196.6 * u3) + (-19700 * u4);
            } else if (y < 1941) {
                double u = (y - 1920) / 100.0;
                double u2 = u * u;
                double u3 = u2 * u;
                Delta = 21.20 + (84.493 * u) + (-761.00 * u2) + (2093.6 * u3);
            } else if (y < 1961) {
                double u = (y - 1950) / 100.0;
                double u2 = u * u;
                double u3 = u2 * u;
                Delta = 29.07 + (40.7 * u) + (-u2 / 0.0233) + (u3 / 0.002547);
            } else if (y < 1986) {
                double u = (y - 1975) / 100.0;
                double u2 = u * u;
                double u3 = u2 * u;
                Delta = 45.45 + 106.7 * u - u2 / 0.026 - u3 / 0.000718;
            } else if (y < 2005) {
                double u = (y - 2000) / 100.0;
                double u2 = u * u;
                double u3 = u2 * u;
                double u4 = u3 * u;
                double u5 = u4 * u;
                Delta = 63.86 + (33.45 * u) + (-603.74 * u2) + (1727.5 * u3) + (65181.4 * u4) + (237359.9 * u5);
            } else if (y < 2050) {
                double u = (y - 2000) / 100.0;
                double u2 = u * u;
                Delta = 62.92 + (32.217 * u) + (55.89 * u2);
            } else if (y < 2150) {
                double u = (y - 1820) / 100.0;
                double u2 = u * u;
                Delta = -205.72 + (56.28 * u) + (32 * u2);
            } else {
                double u = (y - 1820) / 100.0;
                double u2 = u * u;
                Delta = -20 + (32 * u2);
            }
        }

        return Delta;
    }

    public static double cumulativeLeapSeconds(double JD) {
        //What will be the return value from the method
        double LeapSeconds = 0;

        int nLookupElements = g_LeapSecondCoefficients.length;
        if (JD >= g_LeapSecondCoefficients[0].JD) {
            //Find the index in the lookup table which contains the JD value closest to the JD input parameter
            boolean bContinue = true;
            int nIndex = 1;
            while (bContinue) {
                if (nIndex >= nLookupElements) {
                    LeapSeconds = g_LeapSecondCoefficients[nLookupElements - 1].LeapSeconds + (JD - 2400000.5 - g_LeapSecondCoefficients[nLookupElements - 1].BaseMJD) * g_LeapSecondCoefficients[nLookupElements - 1].Coefficient;
                    bContinue = false;
                } else if (JD < g_LeapSecondCoefficients[nIndex].JD) {
                    LeapSeconds = g_LeapSecondCoefficients[nIndex - 1].LeapSeconds + (JD - 2400000.5 - g_LeapSecondCoefficients[nIndex - 1].BaseMJD) * g_LeapSecondCoefficients[nIndex - 1].Coefficient;
                    bContinue = false;
                }

                //Prepare for the next loop
                if (bContinue) {
                    ++nIndex;
                }
            }
        }

        return LeapSeconds;
    }

    public static class DeltaTValue {
        public DeltaTValue(double JD, double deltaT) {
            this.JD = JD;
            DeltaT = deltaT;
        }

        public double JD;
        public double DeltaT;
    }

    public static class LeapSecondCoefficient {
        public LeapSecondCoefficient(double JD, double leapSeconds, double baseMJD, double coefficient) {
            this.JD = JD;
            LeapSeconds = leapSeconds;
            BaseMJD = baseMJD;
            Coefficient = coefficient;
        }

        public double JD;
        public double LeapSeconds;
        public double BaseMJD;
        public double Coefficient;
    }
}