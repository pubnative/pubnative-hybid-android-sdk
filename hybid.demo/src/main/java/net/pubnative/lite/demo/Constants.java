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

import net.pubnative.lite.sdk.CountdownStyle;

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

    public static final Boolean COPPA_DEFAULT = false;
    public static final Boolean TEST_MODE_DEFAULT = true;
    public static final Boolean TOPICS_API_DEFAULT = false;
    public static final Boolean LOCATION_TRACKING_DEFAULT = true;
    public static final Boolean LOCATION_UPDATES_DEFAULT = false;
    public static final int INITIAL_AUDIO_STATE_DEFAULT = 1;
    public static final Boolean MRAID_EXPANDED_DEFAULT = true;
    public static final Boolean CLOSE_VIDEO_AFTER_FINISH_DEFAULT = false;
    public static final Boolean CLOSE_VIDEO_AFTER_FINISH_DEFAULT_FOR_REWARDED = false;
    public static final Boolean ENABLE_ENDCARD_DEFAULT = true;
    public static final int SKIP_OFFSET_DEFAULT = 3;
    public static final int VIDEO_SKIP_OFFSET_DEFAULT = 8;
    public static final int ENDCARD_CLOSE_BUTTON_DELAY_DEFAULT = 5;
    public static final Boolean VIDEO_CLICK_BEHAVIOUR_DEFAULT = true;
    public static final int MRAID_CUSTOM_CLOSE_CLOSE_BUTTON_DELAY_DEFAULT = 15;

    public static final String DFP_MEDIATION_BANNER_AD_UNIT = "";
    public static final String DFP_MEDIATION_INTERSTITIAL_AD_UNIT = "";
    public static final String DFP_MEDIATION_MEDIUM_AD_UNIT = "";
    public static final String DFP_MEDIATION_LEADERBOARD_AD_UNIT = "";
    public static final String DFP_MEDIATION_REWARDED_AD_UNIT = "";

    public static final String ADMOB_APP_ID = "ca-app-pub-8741261465579918~1379849443";
    public static final String ADMOB_BANNER_AD_UNIT = "ca-app-pub-8741261465579918/6124360926";
    public static final String ADMOB_INTERSTITIAL_AD_UNIT = "ca-app-pub-8741261465579918/7945828611";
    public static final String ADMOB_INTERSTITIAL_VIDEO_AD_UNIT = "ca-app-pub-8741261465579918/9849937224";
    public static final String ADMOB_MEDIUM_AD_UNIT = "ca-app-pub-8741261465579918/9021987007";
    public static final String ADMOB_MEDIUM_VIDEO_AD_UNIT = "ca-app-pub-8741261465579918/9550554321";
    public static final String ADMOB_LEADERBOARD_AD_UNIT = "ca-app-pub-8741261465579918/5172325610";
    public static final String ADMOB_REWARDED_AD_UNIT = "ca-app-pub-8741261465579918/1951265901";

    public static final String ADMOB_REWARDED_HTML_AD_UNIT = "ca-app-pub-8741261465579918/1097349235";
    public static final String ADMOB_NATIVE_AD_UNIT = "ca-app-pub-8741261465579918/3727382007";

    public static final String IRONSOURCE_APP_KEY = "120ac03ad";
    public static final String IRONSOURCE_BANNER_AD_UNIT = "HyBid_Banner";
    public static final String IRONSOURCE_INTERSTITIAL_AD_UNIT = "HyBid_Interstitial";
    public static final String IRONSOURCE_REWARDED_AD_UNIT = "HyBid_Rewarded";

    public static final String MAXADS_SDK_KEY = "sMRyqsHzbW5B55p5RLfJTNaXBH1rFzvkU5_LGa_Kerigolzf62Jl6iwzLtMIqn2XRt0tDol1bAc8g0N7C7c51N";
    public static final String MAXADS_BANNER_AD_UNIT = "a6eea664dbc57ba2";
    public static final String MAXADS_MRECT_AD_UNIT = "b530984eac9af2db";
    public static final String MAXADS_MRECT_VIDEO_AD_UNIT = "95319bf36f0060eb";
//    public static final String MAXADS_NATIVE_AD_UNIT = "9052d6e7f6f0841a";
    public static final String MAXADS_NATIVE_AD_UNIT = "cea692bbbc299612";
    public static final String MAXADS_INTERSTITIAL_HTML_AD_UNIT = "d9c7ea61191f825a";
    public static final String MAXADS_INTERSTITIAL_VIDEO_AD_UNIT = "19c5ca80db9d56f0";
    public static final String MAXADS_REWARDED_VIDEO_AD_UNIT = "8750fa87e3aaaea9";
    public static final String MAXADS_REWARDED_HTML_AD_UNIT = "a8127cee3c1da24d";

    public static final String FAIRBID_APP_ID = "133232";
    public static final String FAIRBID_MEDIATION_BANNER_AD_UNIT = "681242";
    public static final String FAIRBID_MEDIATION_INTERSTITIAL_AD_UNIT = "681244";
    public static final String FAIRBID_MEDIATION_REWARDED_AD_UNIT = "681245";

    public static final String CONTENT_INFO_URL = "https://pubnative.net/content-info";
    public static final String CONTENT_INFO_ICON_URL = "https://cdn.pubnative.net/static/adserver/contentinfo.png";

    public static final String CHARTBOOST_APP_ID = "64f5c7fb7ddfa7bc4080417b";
    public static final String CHARTBOOST_APP_SIGNATURE = "72d9f2abbacdf32a49494390d9e8a0f89190f474";
    public static final String CHARTBOOST_MEDIATION_BANNER_AD_UNIT = "hybid-android-banner";
    /*public static final String CHARTBOOST_MEDIATION_MRECT_AD_UNIT = "";
    public static final String CHARTBOOST_MEDIATION_MRECT_VIDEO_AD_UNIT = "";
    public static final String CHARTBOOST_MEDIATION_LEADERBOARD_AD_UNIT = "";*/
    public static final String CHARTBOOST_MEDIATION_INTERSTITIAL_AD_UNIT = "hybid-android-interstitial-html";
    public static final String CHARTBOOST_MEDIATION_INTERSTITIAL_VIDEO_AD_UNIT = "hybid-android-interstitial-video";
    public static final String CHARTBOOST_MEDIATION_REWARDED_VIDEO_AD_UNIT = "hybid-android-rewarded-video";
    public static final String CHARTBOOST_MEDIATION_REWARDED_HTML_AD_UNIT = "hybid-android-rewarded-html";

    public static final String APP_TOKEN = "dde3c298b47648459f8ada4a982fa92d";

    public static final List<String> ZONE_ID_LIST = Arrays.asList(NATIVE_ZONE_ID, MRAID_320x50_ZONE_ID, MRAID_300x250_ZONE_ID, INTERSTITIAL_MRAID_ZONE_ID, MEDIUM_VIDEO_ZONE_ID, INTERSTITIAL_VIDEO_ZONE_ID, MRAID_728x90_ZONE_ID, MRAID_160x600_ZONE_ID, MRAID_250x250_ZONE_ID, MRAID_300x600_ZONE_ID, MRAID_320x100_ZONE_ID, MRAID_480x320_ZONE_ID, MRAID_300x50_ZONE_ID);

    public static final String COUNTDOWN_STYLE_DEFAULT = CountdownStyle.PIE_CHART.getId();

    public static final class IntentParams {
        public static final String ZONE_ID = "zone_id";
    }

    public static final class AdmType {
        public static final String MARKUP = "markup";
        public static final String API_V3 = "apiv3";
        public static final String ORTB = "ortb";
    }

    public static final class Format {
        public static final String HTML = "html";
        public static final String VIDEO = "video";
    }
}