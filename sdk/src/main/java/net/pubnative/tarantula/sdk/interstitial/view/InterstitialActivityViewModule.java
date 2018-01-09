package net.pubnative.tarantula.sdk.interstitial.view;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

/**
 * Created by erosgarciaponte on 09.01.18.
 */

public interface InterstitialActivityViewModule {
    interface Listener {
        void onInterstitialClicked();
        void onDismissClicked();
    }

    void setListener(@Nullable Listener listener);
    void show(@NonNull String html);
    void destroy();
}
