package net.pubnative.tarantula.demo;

import android.app.Application;

import net.pubnative.tarantula.sdk.Tarantula;

/**
 * Created by erosgarciaponte on 08.01.18.
 */

public class TarantulaDemoApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();

        Tarantula.initialize(this);
    }
}
