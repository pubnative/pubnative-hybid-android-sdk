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
    public static final String BANNER_MRAID_ZONE_ID = "2";
    public static final String INTERSTITIAL_MRAID_ZONE_ID = "3";
    public static final String MEDIUM_MRAID_ZONE_ID = "5";
    public static final String INTERSTITIAL_VIDEO_ZONE_ID = "4";
    public static final String MEDIUM_VIDEO_ZONE_ID = "6";

    public static final String MOPUB_MEDIATION_BANNER_AD_UNIT = "38864d35877d4684af11319530993074";
    public static final String MOPUB_MEDIATION_INTERSTITIAL_AD_UNIT = "2bb6db4d69fa407a87b0c96c55d7c2b4";
    public static final String MOPUB_MEDIATION_MEDIUM_AD_UNIT = "0af490cac3d34256b0010fac17f26759";
    public static final String MOPUB_MEDIATION_NATIVE_AD_UNIT = "2dfcd13fb5dc4e9d9bc6ab3e382ffeb7";

    public static final String MOPUB_MRAID_BANNER_AD_UNIT = "b8b82260e1b84a9ba361e03c21ce4caf";
    public static final String MOPUB_MRAID_INTERSTITIAL_AD_UNIT = "0bd7ea20185547f2bd29a9574bfce917";
    public static final String MOPUB_MRAID_MEDIUM_AD_UNIT = "1fafef6b872a4e10ba9fc573ca347e55";

    public static final String DFP_MRAID_BANNER_AD_UNIT = "/219576711/pnlite_dfp_banner";
    public static final String DFP_MRAID_INTERSTITIAL_AD_UNIT = "/219576711/pnlite_dfp_interstitial";
    public static final String DFP_MRAID_MEDIUM_AD_UNIT = "/219576711/pnlite_dfp_mrect";

    public static final String APP_TOKEN = "dde3c298b47648459f8ada4a982fa92d";
    public static final List<String> ZONE_ID_LIST = Arrays.asList(NATIVE_ZONE_ID, BANNER_MRAID_ZONE_ID,
            MEDIUM_MRAID_ZONE_ID, INTERSTITIAL_MRAID_ZONE_ID, MEDIUM_VIDEO_ZONE_ID,
            INTERSTITIAL_VIDEO_ZONE_ID);

    public final class IntentParams {
        public static final String ZONE_ID = "zone_id";
    }
}
