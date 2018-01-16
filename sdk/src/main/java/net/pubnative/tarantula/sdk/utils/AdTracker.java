package net.pubnative.tarantula.sdk.utils;

import android.support.annotation.NonNull;
import android.support.annotation.VisibleForTesting;

import net.pubnative.tarantula.sdk.Tarantula;
import net.pubnative.tarantula.sdk.api.ApiClient;
import net.pubnative.tarantula.sdk.models.AdData;

import java.util.List;

/**
 * Created by erosgarciaponte on 08.01.18.
 */

public class AdTracker {
    private enum Type {
        IMPRESSION("impression"),
        CLICK("click");

        @NonNull
        private final String mType;

        Type(@NonNull String type) {
            mType = type;
        }


        @Override
        public String toString() {
            return mType;
        }
    }

    @NonNull
    private static final String TAG = AdTracker.class.getSimpleName();
    @NonNull
    private final ApiClient mApiClient;
    @NonNull
    private final List<AdData> mImpressionUrls;
    @NonNull
    private final List<AdData> mClickUrls;
    private boolean mImpressionTracked;
    private boolean mClickTracked;

    public AdTracker(@NonNull List<AdData> impressionUrls,
                     @NonNull List<AdData> clickUrls) {
        this(Tarantula.getApiClient(), impressionUrls, clickUrls);
    }

    @VisibleForTesting
    AdTracker(@NonNull ApiClient apiClient,
              @NonNull List<AdData> impressionUrls,
              @NonNull List<AdData> clickUrls) {
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

    private void trackUrls(@NonNull List<AdData> urls, @NonNull Type type) {
        for (final AdData url : urls) {
            Logger.d(TAG, "Tracking " + type.toString() + " url: " + url);
            mApiClient.trackUrl(url.getURL(), new ApiClient.TrackUrlListener() {
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
