package net.pubnative.lite.sdk.tracking;

import android.os.StrictMode;

/**
 * Created by erosgarciaponte on 13.02.18.
 */

class StrictModeWrapper {

    static void setUp() throws Exception {
        StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder()
                .detectDiskReads()
                .penaltyDeath()
                .build());
    }

    static void tearDown() throws Exception {
        StrictMode.setThreadPolicy(StrictMode.ThreadPolicy.LAX);
    }
}
