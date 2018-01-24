package net.pubnative.tarantula.sdk.api;

import net.pubnative.tarantula.sdk.AdCache;
import net.pubnative.tarantula.sdk.Tarantula;
import net.pubnative.tarantula.sdk.models.AdRequestFactory;
import net.pubnative.tarantula.sdk.models.AdRequest;
import net.pubnative.tarantula.sdk.models.Ad;
import net.pubnative.tarantula.sdk.utils.CheckUtils;
import net.pubnative.tarantula.sdk.utils.Logger;
import net.pubnative.tarantula.sdk.utils.TarantulaInitializationHelper;

/**
 * Created by erosgarciaponte on 08.01.18.
 */

public abstract class RequestManager {
    public interface RequestListener {
        void onRequestSuccess(Ad ad);

        void onRequestFail(Throwable throwable);
    }

    private static final String TAG = RequestManager.class.getSimpleName();
    private final TarantulaApiClient mApiClient;
    private final AdCache mAdCache;
    private final AdRequestFactory mAdRequestFactory;
    private final TarantulaInitializationHelper mInitializationHelper;
    private String mZoneId;
    private RequestListener mRequestListener;
    private boolean mIsDestroyed;

    public RequestManager() {
        this(Tarantula.getApiClient(), Tarantula.getAdCache(), new AdRequestFactory(), new TarantulaInitializationHelper());
    }

    RequestManager(TarantulaApiClient apiClient,
                   AdCache adCache,
                   AdRequestFactory adRequestFactory,
                   TarantulaInitializationHelper initializationHelper) {
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
        if (!CheckUtils.NoThrow.checkArgument(mInitializationHelper.isInitialized(), "Tarantula SDK has not been initialized. " +
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
        mApiClient.getAd(adRequest, new TarantulaApiClient.AdRequestListener() {
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
