package net.pubnative.tarantula.demo;

import android.content.Context;
import android.support.multidex.MultiDex;
import android.support.multidex.MultiDexApplication;

import com.crashlytics.android.Crashlytics;
import io.fabric.sdk.android.Fabric;
import net.pubnative.tarantula.sdk.Tarantula;

/**
 * Created by erosgarciaponte on 08.01.18.
 */

public class TarantulaDemoApplication extends MultiDexApplication {
    @Override
    public void onCreate() {
        super.onCreate();
        Fabric.with(this, new Crashlytics());

        Tarantula.initialize(Constants.APP_TOKEN, this);
        Tarantula.setTestMode(true);
        Tarantula.setCoppaEnabled(false);
        Tarantula.setAge("27");
        Tarantula.setGender("male");
    }

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        MultiDex.install(this);
    }
}
