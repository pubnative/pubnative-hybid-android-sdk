package net.pubnative.tarantula.demo;

import android.app.Application;
import android.content.Context;
import android.support.multidex.MultiDex;
import android.support.multidex.MultiDexApplication;

import net.pubnative.tarantula.sdk.Tarantula;

/**
 * Created by erosgarciaponte on 08.01.18.
 */

public class TarantulaDemoApplication extends MultiDexApplication {
    @Override
    public void onCreate() {
        super.onCreate();

        Tarantula.initialize(Constants.APP_TOKEN, this);
    }

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        MultiDex.install(this);
    }
}
