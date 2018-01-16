package net.pubnative.tarantula.sdk.api;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.VisibleForTesting;

import net.pubnative.tarantula.sdk.AdCache;
import net.pubnative.tarantula.sdk.Tarantula;
import net.pubnative.tarantula.sdk.models.AdRequestFactory;
import net.pubnative.tarantula.sdk.models.AdRequest;
import net.pubnative.tarantula.sdk.models.Ad;
import net.pubnative.tarantula.sdk.utils.CheckUtils;
import net.pubnative.tarantula.sdk.utils.Logger;
import net.pubnative.tarantula.sdk.utils.RefreshTimer;

import io.reactivex.functions.Consumer;

/**
 * Created by erosgarciaponte on 08.01.18.
 */

public abstract class RequestManager {
    public interface RequestListener {
        void onRequestSuccess(@NonNull Ad ad);

        void onRequestFail(@NonNull Throwable throwable);
    }

    @NonNull
    private static final String TAG = RequestManager.class.getSimpleName();
    public static final int DEFAULT_REFRESH_TIME_SECONDS = 60;
    @NonNull
    private final ApiClient mApiClient;
    @NonNull
    private final AdCache mAdCache;
    @NonNull
    private final AdRequestFactory mAdRequestFactory;
    @NonNull
    private final RefreshTimer mRefreshTimer;
    @Nullable
    private String mZoneId;
    @Nullable
    private RequestListener mRequestListener;
    private boolean mIsDestroyed;

    public RequestManager() {
        this(Tarantula.getApiClient(), Tarantula.getAdCache(), new AdRequestFactory(), new RefreshTimer());
    }

    @VisibleForTesting
    RequestManager(@NonNull ApiClient apiClient,
                   @NonNull AdCache adCache,
                   @NonNull AdRequestFactory adRequestFactory,
                   @NonNull RefreshTimer refreshTimer) {
        mApiClient = apiClient;
        mAdCache = adCache;
        mAdRequestFactory = adRequestFactory;
        mRefreshTimer = refreshTimer;
    }

    public void setRequestListener(@Nullable RequestListener requestListener) {
        mRequestListener = requestListener;
    }

    public void setZoneId(@Nullable String zoneId) {
        mZoneId = zoneId;
    }

    public void requestAd() {
        if (!CheckUtils.NoThrow.checkArgument(Tarantula.isInitialized(), "Tarantula SDK has not been initialized. " +
                "Please call Tarantula#initialize in your application's onCreate method.")) {
            return;
        }

        if (!CheckUtils.NoThrow.checkNotNull(mZoneId, "zone id cannot be null")) {
            return;
        }

        if (!CheckUtils.NoThrow.checkArgument(!mIsDestroyed, "RequestManager has been destroyed")) {
            return;
        }

        AdRequest adRequest = mAdRequestFactory.createAdRequest(mZoneId, getAdSize());
        requestAdFromApi(adRequest);
    }

    @VisibleForTesting
    void requestAdFromApi(@NonNull final AdRequest adRequest) {
        Logger.d(TAG, "Requesting ad for zone id: " + adRequest.zoneid);
        mApiClient.getAd(adRequest, new ApiClient.AdRequestListener() {
            @Override
            public void onSuccess(Ad ad) {
                if (mIsDestroyed) {
                    return;
                }

                Logger.d(TAG, "Received ad response for zone id: " + adRequest.zoneid);
                mAdCache.put(adRequest.zoneid, ad);
                if (mRequestListener != null) {
                    mRequestListener.onRequestSuccess(ad);
                }
            }

            @Override
            public void onFailure(Throwable throwable) {
                if (mIsDestroyed) {
                    return;
                }

                Logger.w(TAG, "Failed to receive ad response for zone id: " + adRequest.zoneid, throwable);
                if (mRequestListener != null) {
                    mRequestListener.onRequestFail(throwable);
                }
            }
        });
    }

    public void startRefreshTimer(long delaySeconds) {
        if (!CheckUtils.NoThrow.checkArgument(!mIsDestroyed, "RequestManager has been destroyed")) {
            return;
        }

        delaySeconds = delaySeconds > 0 ? delaySeconds : DEFAULT_REFRESH_TIME_SECONDS;
        mRefreshTimer.start(delaySeconds, new Consumer<Long>() {
            @Override
            public void accept(Long aLong) throws Exception {
                requestAd();
            }
        });
    }

    public void stopRefreshTimer() {
        mRefreshTimer.stop();
    }

    public void destroy() {
        mRefreshTimer.stop();
        mRequestListener = null;
        mIsDestroyed = true;
    }

    protected abstract String getAdSize();
}
