package net.pubnative.lite.sdk.api;

import net.pubnative.lite.sdk.AdCache;
import net.pubnative.lite.sdk.PNLite;
import net.pubnative.lite.sdk.models.AdRequestFactory;
import net.pubnative.lite.sdk.models.AdRequest;
import net.pubnative.lite.sdk.models.Ad;
import net.pubnative.lite.sdk.utils.CheckUtils;
import net.pubnative.lite.sdk.utils.Logger;
import net.pubnative.lite.sdk.utils.PNInitializationHelper;

/**
 * Created by erosgarciaponte on 08.01.18.
 */

public abstract class RequestManager {
    public interface RequestListener {
        void onRequestSuccess(Ad ad);

        void onRequestFail(Throwable throwable);
    }

    private static final String TAG = RequestManager.class.getSimpleName();
    private final PNApiClient mApiClient;
    private final AdCache mAdCache;
    private final AdRequestFactory mAdRequestFactory;
    private final PNInitializationHelper mInitializationHelper;
    private String mZoneId;
    private RequestListener mRequestListener;
    private boolean mIsDestroyed;

    public RequestManager() {
        this(PNLite.getApiClient(), PNLite.getAdCache(), new AdRequestFactory(), new PNInitializationHelper());
    }

    RequestManager(PNApiClient apiClient,
                   AdCache adCache,
                   AdRequestFactory adRequestFactory,
                   PNInitializationHelper initializationHelper) {
        mApiClient = apiClient;
        mAdCache = adCache;
        mAdRequestFactory = adRequestFactory;
        mInitializationHelper = initializationHelper;
    }

    public void setRequestListener(RequestListener requestListener) {
        mRequestListener = requestListener;
    }

    public void setZoneId(String zoneId) {
        mZoneId = zoneId;
    }

    public void requestAd() {
        if (!CheckUtils.NoThrow.checkArgument(mInitializationHelper.isInitialized(), "PNLite SDK has not been initialized. " +
                "Please call PNLite#initialize in your application's onCreate method.")) {
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
        mApiClient.getAd(adRequest, new PNApiClient.AdRequestListener() {
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

                Logger.w(TAG, throwable.getMessage());
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
