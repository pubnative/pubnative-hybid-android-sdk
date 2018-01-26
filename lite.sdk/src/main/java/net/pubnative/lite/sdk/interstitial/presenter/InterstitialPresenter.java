package net.pubnative.lite.sdk.interstitial.presenter;

import net.pubnative.lite.sdk.models.Ad;

/**
 * Created by erosgarciaponte on 09.01.18.
 */

public interface InterstitialPresenter {
    interface Listener {
        void onInterstitialLoaded(InterstitialPresenter interstitialPresenter);

        void onInterstitialShown(InterstitialPresenter interstitialPresenter);

        void onInterstitialClicked(InterstitialPresenter interstitialPresenter);

        void onInterstitialDismissed(InterstitialPresenter interstitialPresenter);

        void onInterstitialError(InterstitialPresenter interstitialPresenter);
    }

    void setListener(Listener listener);

    Ad getAd();

    void load();

    void show();

    void destroy();
}
