// HyBid SDK License
//
// https://github.com/pubnative/pubnative-hybid-android-sdk/blob/main/LICENSE
//
package net.pubnative.lite.sdk.presenter;

import android.view.View;

import net.pubnative.lite.sdk.VideoListener;
import net.pubnative.lite.sdk.models.Ad;
import net.pubnative.lite.sdk.mraid.MRAIDViewListener;
import net.pubnative.lite.sdk.utils.HybidConsumer;

import org.json.JSONObject;

/**
 * Created by erosgarciaponte on 08.01.18.
 */

public interface AdPresenter {
    interface Listener {
        void onAdLoaded(AdPresenter adPresenter, View adView);

        void onAdClicked(AdPresenter adPresenter);

        void onAdError(AdPresenter adPresenter);
    }

    interface ImpressionListener {
        void onImpression();
    }

    void setListener(Listener listener);

    void setImpressionListener(ImpressionListener listener);

    void setVideoListener(VideoListener listener);

    void setMRaidListener(MRAIDViewListener listener);

    Ad getAd();

    void load();

    void destroy();

    void startTracking();

    void startTracking(HybidConsumer<Double> percentageVisible);

    void stopTracking();

    JSONObject getPlacementParams();
}
