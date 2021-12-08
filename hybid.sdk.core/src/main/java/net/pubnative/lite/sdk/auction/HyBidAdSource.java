package net.pubnative.lite.sdk.auction;

import android.content.Context;
import android.text.TextUtils;

import net.pubnative.lite.sdk.api.BannerRequestManager;
import net.pubnative.lite.sdk.api.InterstitialRequestManager;
import net.pubnative.lite.sdk.api.RequestManager;
import net.pubnative.lite.sdk.models.Ad;
import net.pubnative.lite.sdk.models.AdSize;
import net.pubnative.lite.sdk.models.IntegrationType;

public class HyBidAdSource implements AdSource, RequestManager.RequestListener {
    private final AdSourceConfig mConfig;
    private final AdSize mAdSize;
    private Listener mListener;

    public HyBidAdSource(Context context, AdSourceConfig config, AdSize adSize) {
        this.mConfig = config;
        this.mAdSize = adSize;
    }

    @Override
    public void fetchAd(Listener listener) {
        if (mConfig != null && !TextUtils.isEmpty(mConfig.getZoneId())) {
            mListener = listener;

            RequestManager requestManager;
            if (mAdSize == AdSize.SIZE_INTERSTITIAL) {
                requestManager = new InterstitialRequestManager();
            } else {
                requestManager = new BannerRequestManager();
                requestManager.setAdSize(mAdSize);
            }

            requestManager.setZoneId(mConfig.getZoneId());
            requestManager.setIntegrationType(IntegrationType.IN_APP_BIDDING);
            requestManager.setRequestListener(this);
            requestManager.requestAd();
        } else {
            if (listener != null) {
                listener.onError(new AuctionError(mConfig.getName(), new Exception("HyBid ad fetch failed. Invalid config")));
            }
        }
    }

    @Override
    public AdSize getAdSize(){
        return mAdSize;
    }

    @Override
    public String getName(){
        String name = mConfig.getName();
        return !TextUtils.isEmpty(name) ? mConfig.getName() : "hybid_api";
    }

    @Override
    public double getECPM(){
        return mConfig.getECPM();
    }

    @Override
    public void onRequestSuccess(Ad ad) {
        if (mListener != null) {
            ad.setAdSourceName(mConfig.getName());
            mListener.onAdFetched(ad);
        }
    }

    @Override
    public void onRequestFail(Throwable throwable) {
        if (mListener != null) {
            mListener.onError(new AuctionError(mConfig.getName(), throwable));
        }
    }
}
