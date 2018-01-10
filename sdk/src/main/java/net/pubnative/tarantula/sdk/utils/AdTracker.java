package net.pubnative.tarantula.sdk.utils;

import android.support.annotation.NonNull;
import android.support.annotation.VisibleForTesting;

import net.pubnative.tarantula.sdk.Tarantula;
import net.pubnative.tarantula.sdk.api.ApiClient;
import net.pubnative.tarantula.sdk.models.api.PNAPIV3DataModel;

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
    private final List<PNAPIV3DataModel> mImpressionUrls;
    @NonNull
    private final List<PNAPIV3DataModel> mClickUrls;
    private boolean mImpressionTracked;
    private boolean mClickTracked;

    public AdTracker(@NonNull List<PNAPIV3DataModel> impressionUrls,
                     @NonNull List<PNAPIV3DataModel> clickUrls) {
        this(Tarantula.getApiClient(), impressionUrls, clickUrls);
    }

    @VisibleForTesting
    AdTracker(@NonNull ApiClient apiClient,
              @NonNull List<PNAPIV3DataModel> impressionUrls,
              @NonNull List<PNAPIV3DataModel> clickUrls) {
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

    public void trackError(@NonNull String errorMessage) {
        mApiClient.trackError(errorMessage);
    }

    private void trackUrls(@NonNull List<PNAPIV3DataModel> urls, @NonNull Type type) {
        for (final PNAPIV3DataModel url : urls) {
            Logger.d(TAG, "Tracking " + type.toString() + " url: " + url);
            mApiClient.trackUrl(url.getURL());
        }
    }
}
