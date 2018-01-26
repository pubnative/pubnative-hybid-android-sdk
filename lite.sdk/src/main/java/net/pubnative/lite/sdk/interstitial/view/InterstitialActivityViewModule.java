package net.pubnative.lite.sdk.interstitial.view;

/**
 * Created by erosgarciaponte on 09.01.18.
 */

public interface InterstitialActivityViewModule {
    interface Listener {
        void onInterstitialClicked();

        void onDismissClicked();
    }

    void setListener(Listener listener);

    void show(String html);

    void destroy();
}
