// The MIT License (MIT)
//
// Copyright (c) 2018 PubNative GmbH
//
// Permission is hereby granted, free of charge, to any person obtaining a copy
// of this software and associated documentation files (the "Software"), to deal
// in the Software without restriction, including without limitation the rights
// to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
// copies of the Software, and to permit persons to whom the Software is
// furnished to do so, subject to the following conditions:
//
// The above copyright notice and this permission notice shall be included in all
// copies or substantial portions of the Software.
//
// THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
// IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
// FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
// AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
// LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
// OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
// SOFTWARE.
//
package net.pubnative.lite.demo;

import java.util.Arrays;
import java.util.List;

/**
 * Created by erosgarciaponte on 11.01.18.
 */

public class Constants {
    public static final String NATIVE_ZONE_ID = "7";
    public static final String INTERSTITIAL_MRAID_ZONE_ID = "3";
    public static final String INTERSTITIAL_VIDEO_ZONE_ID = "4";
    public static final String MEDIUM_VIDEO_ZONE_ID = "6";
    public static final String MRAID_320x50_ZONE_ID = "2";
    public static final String MRAID_300x250_ZONE_ID = "5";
    public static final String MRAID_728x90_ZONE_ID = "8";
    public static final String MRAID_300x50_ZONE_ID = "12";
    public static final String MRAID_160x600_ZONE_ID = "25";
    public static final String MRAID_250x250_ZONE_ID = "26";
    public static final String MRAID_300x600_ZONE_ID = "27";
    public static final String MRAID_320x100_ZONE_ID = "28";
    public static final String MRAID_480x320_ZONE_ID = "29";

    public static final String MOPUB_MEDIATION_BANNER_AD_UNIT = "13cb0efc7b8b4b169424bccb0a4dd349";
    public static final String MOPUB_MEDIATION_INTERSTITIAL_AD_UNIT = "ccdec09fb29846f98cf57cea8287a606";
    public static final String MOPUB_MEDIATION_INTERSTITIAL_VIDEO_AD_UNIT = "0a39b233f7774d0e9538b7b832bc35e8";
    public static final String MOPUB_MEDIATION_REWARDED_AD_UNIT = "a45aa28eb92042349d83bcc61d3b768d";
    public static final String MOPUB_MEDIATION_MEDIUM_AD_UNIT = "5ffde0dbca0240079b4dd5f2b181fe05";
    public static final String MOPUB_MEDIATION_MEDIUM_VIDEO_AD_UNIT = "59f8d32ddcbd47baa325ca7ebb0687c3";
    public static final String MOPUB_MEDIATION_LEADERBOARD_AD_UNIT = "a3908796fb804430a78270e147a08fcd";
    public static final String MOPUB_MEDIATION_NATIVE_AD_UNIT = "0a7d2b88d636448a937ee65084e38a3e";

    public static final String MOPUB_MRAID_BANNER_AD_UNIT = "94ef2036a5f4453b8eb096627359cffe";
    public static final String MOPUB_MRAID_INTERSTITIAL_AD_UNIT = "af448ecf8c1546ecbcf329992fb7e868";
    public static final String MOPUB_MRAID_MEDIUM_AD_UNIT = "91736fc87a9a45ad908053899a8e28db";
    public static final String MOPUB_MRAID_LEADERBOARD_AD_UNIT = "4966aa487537480babe4929a8d5570d9";
    public static final String MOPUB_VAST_INTERSTITIAL_AD_UNIT = "ab174d4c566e45d1ba86e7b8f61b22dc";
    public static final String MOPUB_VAST_REWARDED_AD_UNIT = "64f6df919cd149a68776ce0dc2136225";
    public static final String MOPUB_VAST_MEDIUM_AD_UNIT = "2711179d4dec4ef9a9750fbd0884ffd1";

    public static final String DFP_MRAID_BANNER_AD_UNIT = "/219576711/pnlite_dfp_banner";
    public static final String DFP_MRAID_INTERSTITIAL_AD_UNIT = "/219576711/pnlite_dfp_interstitial";
    public static final String DFP_MRAID_MEDIUM_AD_UNIT = "/219576711/pnlite_dfp_mrect";
    public static final String DFP_MRAID_LEADERBOARD_AD_UNIT = "/219576711/pnlite_dfp_leaderboard";

    public static final String DFP_MEDIATION_BANNER_AD_UNIT = "";
    public static final String DFP_MEDIATION_INTERSTITIAL_AD_UNIT = "";
    public static final String DFP_MEDIATION_MEDIUM_AD_UNIT = "";
    public static final String DFP_MEDIATION_LEADERBOARD_AD_UNIT = "";
    public static final String DFP_MEDIATION_REWARDED_AD_UNIT = "";

    public static final String ADMOB_APP_ID = "ca-app-pub-9763601123242224~7761163696";
    public static final String ADMOB_BANNER_AD_UNIT = "ca-app-pub-9763601123242224/5027189140";
    public static final String ADMOB_INTERSTITIAL_AD_UNIT = "ca-app-pub-9763601123242224/6994876935";
    public static final String ADMOB_INTERSTITIAL_VIDEO_AD_UNIT = "ca-app-pub-9763601123242224/5439389087";
    public static final String ADMOB_MEDIUM_AD_UNIT = "ca-app-pub-9763601123242224/9033705159";
    public static final String ADMOB_MEDIUM_VIDEO_AD_UNIT = "ca-app-pub-9763601123242224/4028543995";
    public static final String ADMOB_LEADERBOARD_AD_UNIT = "ca-app-pub-9763601123242224/4902888458";
    public static final String ADMOB_REWARDED_AD_UNIT = "ca-app-pub-9763601123242224/1139038306";
    public static final String ADMOB_NATIVE_AD_UNIT = "ca-app-pub-9763601123242224/2966644429";

    public static final String IRONSOURCE_APP_KEY = "120ac03ad";
    public static final String IRONSOURCE_BANNER_AD_UNIT = "HyBid_Banner";
    public static final String IRONSOURCE_INTERSTITIAL_AD_UNIT = "HyBid_Interstitial";
    public static final String IRONSOURCE_REWARDED_AD_UNIT = "HyBid_Rewarded";

    public static final String APP_TOKEN = "dde3c298b47648459f8ada4a982fa92d";
    public static final List<String> ZONE_ID_LIST = Arrays.asList(NATIVE_ZONE_ID, MRAID_320x50_ZONE_ID,
            MRAID_300x250_ZONE_ID, INTERSTITIAL_MRAID_ZONE_ID, MEDIUM_VIDEO_ZONE_ID,
            INTERSTITIAL_VIDEO_ZONE_ID, MRAID_728x90_ZONE_ID, MRAID_160x600_ZONE_ID,
            MRAID_250x250_ZONE_ID, MRAID_300x600_ZONE_ID, MRAID_320x100_ZONE_ID,
            MRAID_480x320_ZONE_ID, MRAID_300x50_ZONE_ID);

    public static final String OGURY_KEY = "OGY-7B028F43E33F";

    public static final String NUMBEREIGHT_API_TOKEN = "T954C5VJTIAXAGMUVPDU0TZMGEV2";

    public static final class IntentParams {
        public static final String ZONE_ID = "zone_id";
    }
}