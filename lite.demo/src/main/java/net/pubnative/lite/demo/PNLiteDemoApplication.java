package net.pubnative.lite.demo;

import android.content.Context;
import android.support.multidex.MultiDex;
import android.support.multidex.MultiDexApplication;
import android.text.TextUtils;

import com.crashlytics.android.Crashlytics;
import com.mopub.common.MoPub;

import io.fabric.sdk.android.Fabric;

import net.pubnative.lite.demo.managers.MoPubManager;
import net.pubnative.lite.demo.managers.SettingsManager;
import net.pubnative.lite.demo.models.SettingsModel;
import net.pubnative.lite.sdk.PNLite;

import java.util.ArrayList;

/**
 * Created by erosgarciaponte on 08.01.18.
 */

public class PNLiteDemoApplication extends MultiDexApplication {
    @Override
    public void onCreate() {
        super.onCreate();
        Fabric.with(this, new Crashlytics());

        initSettings();
    }

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        MultiDex.install(this);
    }

    private void initSettings() {
        SettingsModel settings = fetchSettings();

        PNLite.initialize(settings.getAppToken(), this, new PNLite.InitialisationListener() {
            @Override
            public void onInitialisationFinished(boolean success) {
                // PNLite SDK has been initialised
            }
        });
        PNLite.setTestMode(settings.getTestMode());
        PNLite.setCoppaEnabled(settings.getCoppa());
        PNLite.setAge(settings.getAge());
        PNLite.setGender(settings.getGender());

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

        PNLite.setKeywords(keywordString);

        MoPubManager.initMoPubSdk(this, settings.getMopubBannerAdUnitId());
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
                    "",
                    "",
                    new ArrayList<String>(),
                    false,
                    true,
                    Constants.MOPUB_MRAID_BANNER_AD_UNIT,
                    Constants.MOPUB_MRAID_MEDIUM_AD_UNIT,
                    Constants.MOPUB_MRAID_INTERSTITIAL_AD_UNIT,
                    Constants.DFP_MRAID_BANNER_AD_UNIT,
                    Constants.DFP_MRAID_MEDIUM_AD_UNIT,
                    Constants.DFP_MRAID_INTERSTITIAL_AD_UNIT);
            manager.setSettings(model, true);
        }

        return model;
    }
}
