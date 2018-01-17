package net.pubnative.tarantula.sdk.utils;

import net.pubnative.tarantula.sdk.Tarantula;
import net.pubnative.tarantula.sdk.api.TarantulaApiClient;
import net.pubnative.tarantula.sdk.models.AdData;

import java.util.List;

/**
 * Created by erosgarciaponte on 08.01.18.
 */

public class AdTracker {
    private enum Type {
        IMPRESSION("impression"),
        CLICK("click");

        private final String mType;

        Type(String type) {
            mType = type;
        }


        @Override
        public String toString() {
            return mType;
        }
    }

    private static final String TAG = AdTracker.class.getSimpleName();
    private final TarantulaApiClient mApiClient;
    private final List<AdData> mImpressionUrls;
    private final List<AdData> mClickUrls;
    private boolean mImpressionTracked;
    private boolean mClickTracked;

    public AdTracker(List<AdData> impressionUrls,
                     List<AdData> clickUrls) {
        this(Tarantula.getApiClient(), impressionUrls, clickUrls);
    }

    AdTracker(TarantulaApiClient apiClient,
              List<AdData> impressionUrls,
              List<AdData> clickUrls) {
        mApiClient = apiClient;
        mImpressionUrls = impressionUrls;
        mClickUrls = clickUrls;
    }

    public void trackImpression() {
        if (mImpressionTracked) {
            return;
        }

        trackUrls(mImpressionUrls, Type.IMPRESSION);
        mImpressionTracked = true;
    }

    public void trackClick() {
        if (mClickTracked) {
            return;
        }

        trackUrls(mClickUrls, Type.CLICK);
        mClickTracked = true;
    }

    private void trackUrls(List<AdData> urls, Type type) {
        for (final AdData url : urls) {
            Logger.d(TAG, "Tracking " + type.toString() + " url: " + url);
            mApiClient.trackUrl(url.getURL(), new TarantulaApiClient.TrackUrlListener() {
                @Override
                public void onSuccess() {

                }

                @Override
                public void onFailure(Throwable throwable) {

                }
            });
        }
    }
}
