package net.pubnative.tarantula.sdk.api;

import net.pubnative.tarantula.sdk.AdCache;
import net.pubnative.tarantula.sdk.Tarantula;
import net.pubnative.tarantula.sdk.models.AdRequestFactory;
import net.pubnative.tarantula.sdk.models.AdRequest;
import net.pubnative.tarantula.sdk.models.Ad;
import net.pubnative.tarantula.sdk.utils.CheckUtils;
import net.pubnative.tarantula.sdk.utils.Logger;

/**
 * Created by erosgarciaponte on 08.01.18.
 */

public abstract class RequestManager {
    public interface RequestListener {
        void onRequestSuccess(Ad ad);

        void onRequestFail(Throwable throwable);
    }

    private static final String TAG = RequestManager.class.getSimpleName();
    private final ApiClient mApiClient;
    private final AdCache mAdCache;
    private final AdRequestFactory mAdRequestFactory;
    private String mZoneId;
    private RequestListener mRequestListener;
    private boolean mIsDestroyed;

    public RequestManager() {
        this(Tarantula.getApiClient(), Tarantula.getAdCache(), new AdRequestFactory());
    }

    RequestManager(ApiClient apiClient,
                   AdCache adCache,
                   AdRequestFactory adRequestFactory) {
        mApiClient = apiClient;
        mAdCache = adCache;
        mAdRequestFactory = adRequestFactory;
    }

    public void setRequestListener(RequestListener requestListener) {
        mRequestListener = requestListener;
    }

    public void setZoneId(String zoneId) {
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

    void requestAdFromApi(final AdRequest adRequest) {
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

    public void destroy() {
        mRequestListener = null;
        mIsDestroyed = true;
    }

    protected abstract String getAdSize();
}
