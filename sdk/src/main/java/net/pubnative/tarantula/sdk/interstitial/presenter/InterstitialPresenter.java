package net.pubnative.tarantula.sdk.interstitial.presenter;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import net.pubnative.tarantula.sdk.models.Ad;
import net.pubnative.tarantula.sdk.models.api.PNAPIV3AdModel;

/**
 * Created by erosgarciaponte on 09.01.18.
 */

public interface InterstitialPresenter {
    interface Listener {
        void onInterstitialLoaded(@NonNull InterstitialPresenter interstitialPresenter);
        void onInterstitialShown(@NonNull InterstitialPresenter interstitialPresenter);
        void onInterstitialClicked(@NonNull InterstitialPresenter interstitialPresenter);
        void onInterstitialDismissed(@NonNull InterstitialPresenter interstitialPresenter);
        void onInterstitialError(@NonNull InterstitialPresenter interstitialPresenter);
    }

    void setListener(@Nullable Listener listener);
    @NonNull
    PNAPIV3AdModel getAd();
    void load();
    void show();
    void destroy();
}
