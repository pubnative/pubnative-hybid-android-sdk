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

import android.app.Activity;
import android.content.Context;
import android.os.Build;
import android.text.TextUtils;
import android.webkit.WebView;

import androidx.multidex.MultiDex;
import androidx.multidex.MultiDexApplication;

import com.applovin.sdk.AppLovinSdk;
import com.fyber.FairBid;
import com.google.android.gms.ads.MobileAds;
import com.ogury.sdk.Ogury;
import com.ogury.sdk.OguryConfiguration;

import net.pubnative.lite.demo.managers.AnalyticsSubscriber;
import net.pubnative.lite.demo.managers.SettingsManager;
import net.pubnative.lite.demo.models.SettingsModel;
import net.pubnative.lite.sdk.HyBid;
import net.pubnative.lite.sdk.InterstitialActionBehaviour;
import net.pubnative.lite.sdk.api.ApiManager;
import net.pubnative.lite.sdk.utils.Logger;
import net.pubnative.lite.sdk.vpaid.enums.AudioState;

import java.util.ArrayList;

import ai.numbereight.audiences.Audiences;
import ai.numbereight.sdk.ConsentOptions;
import ai.numbereight.sdk.NumberEight;
import ai.numbereight.sdk.common.Log;

/**
 * Created by erosgarciaponte on 08.01.18.
 */

public class HyBidDemoApplication extends MultiDexApplication {
    @Override
    public void onCreate() {
        super.onCreate();

        try {
            initSettings();
        } catch (Exception exception) {
            Log.d("Exception", exception.toString());
        }
    }

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        MultiDex.install(this);
    }

    private void initSettings() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            WebView.setWebContentsDebuggingEnabled(true);
        }
        SettingsModel settings = fetchSettings();

        String appToken = settings.getAppToken();

        HyBid.initialize(appToken, this, new HyBid.InitialisationListener() {
            @Override
            public void onInitialisationFinished(boolean success) {
                // HyBid SDK has been initialised
                HyBid.addReportingCallback(AnalyticsSubscriber.INSTANCE.getEventCallback());
            }
        });

        HyBid.setLogLevel(Logger.Level.debug);

        HyBid.setTestMode(settings.getTestMode());
        HyBid.setCoppaEnabled(settings.getCoppa());
        HyBid.setAge(settings.getAge());
        HyBid.setGender(settings.getGender());
        HyBid.setVideoAudioStatus(getAudioStateFromSettings(settings.getInitialAudioState()));
        HyBid.setLocationTrackingEnabled(settings.getLocationTracking());
        HyBid.setLocationUpdatesEnabled(settings.getLocationUpdates());
        HyBid.setMraidExpandEnabled(settings.getMraidExpanded());

        HyBid.setCloseVideoAfterFinish(settings.getCloseVideoAfterFinish());

        HyBid.setHtmlInterstitialSkipOffset(settings.getSkipOffset());
        HyBid.setVideoInterstitialSkipOffset(settings.getVideoSkipOffset());
        HyBid.setEndCardCloseButtonDelay(settings.getEndCardCloseButtonDelay());

        HyBid.setInterstitialClickBehaviour(getInterstitialActionBehaviourFromSettings(settings.getVideoClickBehaviour()));

        StringBuilder keywordsBuilder = new StringBuilder();
        String separator = ",";
        for (String keyword : settings.getKeywords()) {
            keywordsBuilder.append(keyword);
            keywordsBuilder.append(separator);
        }
        String keywordString = keywordsBuilder.toString();

        if (!TextUtils.isEmpty(keywordString)) {
            keywordString = keywordString.substring(0, keywordString.length() - separator.length());
        }

        HyBid.setKeywords(keywordString);

        if (HyBid.getViewabilityManager() != null) {
            HyBid.getViewabilityManager().setViewabilityMeasurementEnabled(true);
        }

        if (HyBid.getBrowserManager() != null && !settings.getBrowserPriorities().isEmpty()) {
            for (String packageName : settings.getBrowserPriorities()) {
                HyBid.getBrowserManager().addBrowser(packageName);
            }
        }

        if (!TextUtils.isEmpty(settings.getApiUrl())) {
            ApiManager.INSTANCE.setApiUrl(settings.getApiUrl());
        }

        MobileAds.initialize(this, initializationStatus -> {
        });

        AppLovinSdk.getInstance(this).setMediationProvider("max");
        AppLovinSdk.initializeSdk(this, config -> {

        });

        OguryConfiguration.Builder oguryConfigBuilder = new OguryConfiguration.Builder(this, Constants.OGURY_KEY);
        Ogury.start(oguryConfigBuilder.build());

        // NumberEight SDK crashes below API level 26.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NumberEight.APIToken apiToken = NumberEight.start(Constants.NUMBEREIGHT_API_TOKEN, this, ConsentOptions.withDefault());
            Audiences.startRecording(apiToken);
        }
    }

    private SettingsModel fetchSettings() {
        SettingsModel model;
        SettingsManager manager = SettingsManager.Companion.getInstance(this);

        if (manager.isInitialised()) {
            model = manager.getSettings();
        } else {
            model = new SettingsModel(
                    Constants.APP_TOKEN,
                    Constants.ZONE_ID_LIST,
                    BuildConfig.BASE_URL,
                    "",
                    "",
                    new ArrayList<>(),
                    new ArrayList<>(),
                    Constants.COPPA_DEFAULT,
                    Constants.TEST_MODE_DEFAULT,
                    Constants.LOCATION_TRACKING_DEFAULT,
                    Constants.LOCATION_UPDATES_DEFAULT,
                    Constants.INITIAL_AUDIO_STATE_DEFAULT,
                    Constants.MRAID_EXPANDED_DEFAULT,
                    Constants.CLOSE_VIDEO_AFTER_FINISH_DEFAULT,
                    Constants.ENABLE_ENDCARD_DEFAULT,
                    Constants.SKIP_OFFSET_DEFAULT,
                    Constants.VIDEO_SKIP_OFFSET_DEFAULT,
                    Constants.ENDCARD_CLOSE_BUTTON_DELAY_DEFAULT,
                    Constants.VIDEO_CLICK_BEHAVIOUR_DEFAULT,
                    Constants.DFP_MRAID_BANNER_AD_UNIT,
                    Constants.DFP_MRAID_MEDIUM_AD_UNIT,
                    Constants.DFP_MRAID_LEADERBOARD_AD_UNIT,
                    Constants.DFP_MRAID_INTERSTITIAL_AD_UNIT,
                    Constants.DFP_MEDIATION_BANNER_AD_UNIT,
                    Constants.DFP_MEDIATION_MEDIUM_AD_UNIT,
                    Constants.DFP_MEDIATION_LEADERBOARD_AD_UNIT,
                    Constants.DFP_MEDIATION_INTERSTITIAL_AD_UNIT,
                    Constants.DFP_MEDIATION_REWARDED_AD_UNIT,
                    Constants.ADMOB_APP_ID,
                    Constants.ADMOB_BANNER_AD_UNIT,
                    Constants.ADMOB_MEDIUM_AD_UNIT,
                    Constants.ADMOB_MEDIUM_VIDEO_AD_UNIT,
                    Constants.ADMOB_LEADERBOARD_AD_UNIT,
                    Constants.ADMOB_REWARDED_AD_UNIT,
                    Constants.ADMOB_INTERSTITIAL_AD_UNIT,
                    Constants.ADMOB_INTERSTITIAL_VIDEO_AD_UNIT,
                    Constants.ADMOB_NATIVE_AD_UNIT,
                    Constants.IRONSOURCE_APP_KEY,
                    Constants.IRONSOURCE_BANNER_AD_UNIT,
                    Constants.IRONSOURCE_INTERSTITIAL_AD_UNIT,
                    Constants.IRONSOURCE_REWARDED_AD_UNIT,
                    Constants.MAXADS_SDK_KEY,
                    Constants.MAXADS_BANNER_AD_UNIT,
                    Constants.MAXADS_MRECT_AD_UNIT,
                    Constants.MAXADS_INTERSTITIAL_AD_UNIT,
                    Constants.MAXADS_REWARDED_AD_UNIT,
                    Constants.MAXADS_NATIVE_AD_UNIT,
                    Constants.FAIRBID_APP_ID,
                    Constants.FAIRBID_MEDIATION_BANNER_AD_UNIT,
                    Constants.FAIRBID_MEDIATION_INTERSTITIAL_AD_UNIT,
                    Constants.FAIRBID_MEDIATION_REWARDED_AD_UNIT,
                    Constants.FAIRBID_BANNER_AD_UNIT,
                    Constants.FAIRBID_INTERSTITIAL_AD_UNIT,
                    Constants.FAIRBID_REWARDED_AD_UNIT);
            manager.setSettings(model, true);
        }

        return model;
    }

    private AudioState getAudioStateFromSettings(int settingsAudioState) {
        switch (settingsAudioState) {
            case 1:
                return AudioState.ON;
            case 2:
                return AudioState.MUTED;
            default:
                return AudioState.DEFAULT;
        }
    }

    private InterstitialActionBehaviour getInterstitialActionBehaviourFromSettings(boolean settingsActionBehaviour) {
        if (settingsActionBehaviour) {
            return InterstitialActionBehaviour.HB_CREATIVE;
        } else {
            return InterstitialActionBehaviour.HB_ACTION_BUTTON;
        }
    }

}
