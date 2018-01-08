package net.pubnative.tarantula.sdk.utils;

import android.support.annotation.NonNull;
import android.support.annotation.VisibleForTesting;

import net.pubnative.tarantula.sdk.Tarantula;
import net.pubnative.tarantula.sdk.api.ApiClient;

import java.util.List;

/**
 * Created by erosgarciaponte on 08.01.18.
 */

public class AdTracker {
    private enum Type {
        SELECTED("selected"),
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
    private final List<String> mSelectedUrls;
    @NonNull
    private final List<String> mImpressionUrls;
    @NonNull
    private final List<String> mClickUrls;
    private boolean mSelectedTracked;
    private boolean mImpressionTracked;
    private boolean mClickTracked;

    public AdTracker(@NonNull List<String> selectedUrls,
                     @NonNull List<String> impressionUrls,
                     @NonNull List<String> clickUrls) {
        this(Tarantula.getApiClient(), selectedUrls, impressionUrls, clickUrls);
    }

    @VisibleForTesting
    AdTracker(@NonNull ApiClient apiClient,
              @NonNull List<String> selectedUrls,
              @NonNull List<String> impressionUrls,
              @NonNull List<String> clickUrls) {
        mApiClient = apiClient;
        mSelectedUrls = selectedUrls;
        mImpressionUrls = impressionUrls;
        mClickUrls = clickUrls;
    }

    public void trackSelected() {
        if (mSelectedTracked) {
            return;
        }

        trackUrls(mSelectedUrls, Type.SELECTED);
        mSelectedTracked = true;
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

    private void trackUrls(@NonNull List<String> urls, @NonNull Type type) {
        for (final String url : urls) {
            Logger.d(TAG, "Tracking " + type.toString() + " url: " + url);
            mApiClient.trackUrl(url);
        }
    }
}
