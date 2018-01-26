package net.pubnative.lite.demo;

import android.content.Context;
import android.support.multidex.MultiDex;
import android.support.multidex.MultiDexApplication;

import com.crashlytics.android.Crashlytics;

import io.fabric.sdk.android.Fabric;

import net.pubnative.lite.sdk.PNLite;

/**
 * Created by erosgarciaponte on 08.01.18.
 */

public class PNLiteDemoApplication extends MultiDexApplication {
    @Override
    public void onCreate() {
        super.onCreate();
        Fabric.with(this, new Crashlytics());

        PNLite.initialize(Constants.APP_TOKEN, this);
        PNLite.setTestMode(true);
        PNLite.setCoppaEnabled(false);
        PNLite.setAge("27");
        PNLite.setGender("male");
    }

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        MultiDex.install(this);
    }
}
