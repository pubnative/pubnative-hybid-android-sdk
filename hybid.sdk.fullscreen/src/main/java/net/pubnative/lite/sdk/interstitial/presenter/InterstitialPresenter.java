// HyBid SDK License
//
// https://github.com/pubnative/pubnative-hybid-android-sdk/blob/main/LICENSE
//
package net.pubnative.lite.sdk.interstitial.presenter;

import net.pubnative.lite.sdk.CustomEndCardListener;
import net.pubnative.lite.sdk.VideoListener;
import net.pubnative.lite.sdk.models.Ad;

import org.json.JSONObject;

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

    void setVideoListener(VideoListener listener);

    void setCustomEndCardListener(CustomEndCardListener listener);

    Ad getAd();

    void load();

    void show();

    boolean isReady();

    void destroy();

    JSONObject getPlacementParams();
}
