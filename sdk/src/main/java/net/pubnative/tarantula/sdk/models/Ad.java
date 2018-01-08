package net.pubnative.tarantula.sdk.models;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.VisibleForTesting;

import java.util.Collections;
import java.util.List;

/**
 * Created by erosgarciaponte on 08.01.18.
 */

public class Ad {
    @NonNull
    private final String mAdUnitId;
    @Nullable
    private final String mCreative;
    @NonNull
    private final String mPrebidKeywords;
    @NonNull
    private final Integer mRefreshTimeSeconds;
    @NonNull
    private final List<String> mImpressionUrls;
    @NonNull
    private final List<String> mClickUrls;
    @NonNull
    private final List<String> mSelectedUrls;
    @NonNull
    private final List<String> mErrorUrls;
    @NonNull
    private final Winner mWinner;

    public static Ad from(@NonNull String adUnitId, @NonNull AdResponse adResponse) {
        return new Ad(adUnitId, adResponse.creative, adResponse.prebidKeywords, adResponse.refresh,
                adResponse.impressionUrls, adResponse.clickUrls, adResponse.selectedUrls, adResponse.errorUrls,
                Winner.from(adResponse.winnerResponse));
    }

    @VisibleForTesting
    public Ad(@NonNull String adUnitId, @Nullable String creative, @NonNull String prebidKeywords,
              @NonNull Integer refreshTimeSeconds, @Nullable List<String> impressionUrls, @Nullable List<String> clickUrls,
              @Nullable List<String> selectedUrls, @Nullable List<String> errorUrls, @NonNull Winner winner) {

        mAdUnitId = adUnitId;
        mCreative = creative;
        mPrebidKeywords = prebidKeywords;
        mRefreshTimeSeconds = refreshTimeSeconds;
        mImpressionUrls = impressionUrls == null ? Collections.<String>emptyList() : impressionUrls;
        mClickUrls = clickUrls == null ? Collections.<String>emptyList() : clickUrls;
        mSelectedUrls = selectedUrls == null ? Collections.<String>emptyList() : selectedUrls;
        mErrorUrls = errorUrls == null ? Collections.<String>emptyList() : errorUrls;
        mWinner = winner;
    }

    @NonNull
    public String getAdUnitId() {
        return mAdUnitId;
    }

    @Nullable
    public String getCreative() {
        return mCreative;
    }

    @NonNull
    public String getPrebidKeywords() {
        return mPrebidKeywords;
    }

    @NonNull
    public Integer getRefreshTimeSeconds() {
        return mRefreshTimeSeconds;
    }

    @NonNull
    public List<String> getImpressionUrls() {
        return mImpressionUrls;
    }

    @NonNull
    public List<String> getClickUrls() {
        return mClickUrls;
    }

    @NonNull
    public List<String> getSelectedUrls() {
        return mSelectedUrls;
    }

    @NonNull
    public List<String> getErrorUrls() {
        return mErrorUrls;
    }

    @NonNull
    public Winner getWinner() {
        return mWinner;
    }
}
