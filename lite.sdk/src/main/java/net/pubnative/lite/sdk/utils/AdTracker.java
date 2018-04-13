package net.pubnative.lite.sdk.utils;

import net.pubnative.lite.sdk.PNLite;
import net.pubnative.lite.sdk.api.PNApiClient;
import net.pubnative.lite.sdk.models.AdData;

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
    private final PNApiClient mApiClient;
    private final List<AdData> mImpressionUrls;
    private final List<AdData> mClickUrls;
    private boolean mImpressionTracked;
    private boolean mClickTracked;

    private PNApiClient.TrackUrlListener mTrackUrlListener;

    public AdTracker(List<AdData> impressionUrls,
                     List<AdData> clickUrls) {
        this(PNLite.getApiClient(), impressionUrls, clickUrls);
    }

    AdTracker(PNApiClient apiClient,
              List<AdData> impressionUrls,
              List<AdData> clickUrls) {
        mApiClient = apiClient;
        mImpressionUrls = impressionUrls;
        mClickUrls = clickUrls;

        mTrackUrlListener = new PNApiClient.TrackUrlListener() {
            @Override
            public void onSuccess() {

            }

            @Override
            public void onFailure(Throwable throwable) {

            }
        };
    }

    public void setTrackUrlListener(PNApiClient.TrackUrlListener listener) {
        this.mTrackUrlListener = listener;
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
        if (urls != null) {
            for (final AdData url : urls) {
                Logger.d(TAG, "Tracking " + type.toString() + " url: " + url);
                mApiClient.trackUrl(url.getURL(), mTrackUrlListener);
            }
        }
    }
}
