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

import android.content.Context;
import android.os.Build;
import android.text.TextUtils;
import android.webkit.WebView;

import androidx.multidex.MultiDex;
import androidx.multidex.MultiDexApplication;

import com.google.android.gms.ads.MobileAds;

import net.pubnative.lite.demo.managers.MoPubManager;
import net.pubnative.lite.demo.managers.SettingsManager;
import net.pubnative.lite.demo.models.SettingsModel;
import net.pubnative.lite.sdk.HyBid;
import net.pubnative.lite.sdk.api.ApiManager;
import net.pubnative.lite.sdk.utils.Logger;

import java.util.ArrayList;

/**
 * Created by erosgarciaponte on 08.01.18.
 */

public class HyBidDemoApplication extends MultiDexApplication {
    @Override
    public void onCreate() {
        super.onCreate();

        initSettings();
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

        HyBid.initialize(settings.getAppToken(), this, new HyBid.InitialisationListener() {
            @Override
            public void onInitialisationFinished(boolean success) {
                // HyBid SDK has been initialised
            }
        });

        HyBid.setLogLevel(Logger.Level.debug);

        HyBid.setTestMode(settings.getTestMode());
        HyBid.setCoppaEnabled(settings.getCoppa());
        HyBid.setAge(settings.getAge());
        HyBid.setGender(settings.getGender());

        HyBid.getBrowserManager().addBrowser("org.mozilla.firefox");
        HyBid.getBrowserManager().addBrowser("com.opera.mini.native");

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
        if (!TextUtils.isEmpty(settings.getApiUrl())) {
            ApiManager.INSTANCE.setApiUrl(settings.getApiUrl());
        }

        MoPubManager.initMoPubSdk(this, settings.getMopubBannerAdUnitId());

        MobileAds.initialize(this, initializationStatus -> { });
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
                    HyBid.BASE_URL,
                    "",
                    "",
                    new ArrayList<>(),
                    false,
                    true,
                    Constants.MOPUB_MRAID_BANNER_AD_UNIT,
                    Constants.MOPUB_MRAID_MEDIUM_AD_UNIT,
                    Constants.MOPUB_MRAID_LEADERBOARD_AD_UNIT,
                    Constants.MOPUB_MRAID_INTERSTITIAL_AD_UNIT,
                    Constants.MOPUB_MEDIATION_BANNER_AD_UNIT,
                    Constants.MOPUB_MEDIATION_MEDIUM_AD_UNIT,
                    Constants.MOPUB_MEDIATION_LEADERBOARD_AD_UNIT,
                    Constants.MOPUB_MEDIATION_INTERSTITIAL_AD_UNIT,
                    Constants.MOPUB_MEDIATION_NATIVE_AD_UNIT,
                    Constants.DFP_MRAID_BANNER_AD_UNIT,
                    Constants.DFP_MRAID_MEDIUM_AD_UNIT,
                    Constants.DFP_MRAID_LEADERBOARD_AD_UNIT,
                    Constants.DFP_MRAID_INTERSTITIAL_AD_UNIT,
                    Constants.ADMOB_APP_ID,
                    Constants.ADMOB_BANNER_AD_UNIT,
                    Constants.ADMOB_MEDIUM_AD_UNIT,
                    Constants.ADMOB_LEADERBOARD_AD_UNIT,
                    Constants.ADMOB_INTERSTITIAL_AD_UNIT);
            manager.setSettings(model, true);
        }

        return model;
    }
}
