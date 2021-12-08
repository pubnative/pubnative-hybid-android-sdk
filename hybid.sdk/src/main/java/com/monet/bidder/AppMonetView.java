package com.monet.bidder;

import android.content.Context;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.webkit.ValueCallback;

import net.pubnative.lite.sdk.HyBid;
import net.pubnative.lite.sdk.api.RequestManager;
import net.pubnative.lite.sdk.models.Ad;
import net.pubnative.lite.sdk.models.AdSize;
import net.pubnative.lite.sdk.utils.Logger;
import net.pubnative.lite.sdk.utils.PrebidUtils;
import net.pubnative.lite.sdk.views.HyBidAdView;

public class AppMonetView extends HyBidAdView {
    private static final String TAG = AppMonetView.class.getSimpleName();

    public interface BannerAdListener {
        void onBannerLoaded(AppMonetView banner);

        void onBannerFailed(AppMonetView banner, AppMonetErrorCode errorCode);

        void onBannerClicked(AppMonetView banner);
    }

    private String mAdUnitId;
    private BannerAdListener mBannerAdListener;
    private AdSize mAdSize = AdSize.SIZE_300x250;

    public AppMonetView(Context context) {
        super(context);
    }

    public AppMonetView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public AppMonetView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public AppMonetView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    public String getAdUnitId() {
        return mAdUnitId;
    }

    public BannerAdListener getBannerAdListener() {
        return mBannerAdListener;
    }

    public void loadAd() {
        super.load(mAdUnitId, mListener);
    }

    public void requestAds(final ValueCallback<MonetBid> callback) {
        final RequestManager requestManager = new RequestManager();
        requestManager.setAdSize(mAdSize);
        requestManager.setZoneId(mAdUnitId);
        requestManager.setRequestListener(new RequestManager.RequestListener() {
            @Override
            public void onRequestSuccess(Ad ad) {
                if (callback != null) {
                    MonetBid monetBid = new MonetBid(PrebidUtils.getBidFromPoints(
                                    ad.getECPM(), PrebidUtils.KeywordMode.TWO_DECIMALS), mAdUnitId);
                    callback.onReceiveValue(monetBid);
                }
                requestManager.destroy();
            }

            @Override
            public void onRequestFail(Throwable throwable) {
                if (callback != null) {
                    callback.onReceiveValue(null);
                }
                requestManager.destroy();
            }
        });
        requestManager.requestAd();
    }

    public void render(MonetBid bid) {
        if (bid != null && !TextUtils.isEmpty(bid.getId())) {
            Ad ad = HyBid.getAdCache().remove(bid.getId());
            renderAd(ad, mListener);
        } else {
            Logger.e(TAG, "The provided bid is invalid.");
        }
    }

    public void setAdUnitId(String adUnitId) {
        this.mAdUnitId = adUnitId;
    }

    public void setAdSize(AppMonetAdSize adSize) {
        mAdSize = mapAdSize(adSize);
        super.setAdSize(mAdSize);
    }

    public void setMonetBid(MonetBid bid) {
    }

    public void setBannerAdListener(BannerAdListener bannerAdListener) {
        this.mBannerAdListener = bannerAdListener;
    }

    private AdSize mapAdSize(AppMonetAdSize appMonetAdSize) {
        if (appMonetAdSize.width == AdSize.SIZE_300x250.getWidth()
                && appMonetAdSize.height == AdSize.SIZE_300x250.getHeight()) {
            return AdSize.SIZE_300x250;
        } else if (appMonetAdSize.width == AdSize.SIZE_480x320.getWidth()
                && appMonetAdSize.height == AdSize.SIZE_480x320.getHeight()) {
            return AdSize.SIZE_480x320;
        } else if (appMonetAdSize.width == AdSize.SIZE_300x50.getWidth()
                && appMonetAdSize.height == AdSize.SIZE_300x50.getHeight()) {
            return AdSize.SIZE_300x50;
        } else if (appMonetAdSize.width == AdSize.SIZE_320x480.getWidth()
                && appMonetAdSize.height == AdSize.SIZE_320x480.getHeight()) {
            return AdSize.SIZE_320x480;
        } else if (appMonetAdSize.width == AdSize.SIZE_1024x768.getWidth()
                && appMonetAdSize.height == AdSize.SIZE_1024x768.getHeight()) {
            return AdSize.SIZE_1024x768;
        } else if (appMonetAdSize.width == AdSize.SIZE_768x1024.getWidth()
                && appMonetAdSize.height == AdSize.SIZE_768x1024.getHeight()) {
            return AdSize.SIZE_768x1024;
        } else if (appMonetAdSize.width == AdSize.SIZE_728x90.getWidth()
                && appMonetAdSize.height == AdSize.SIZE_728x90.getHeight()) {
            return AdSize.SIZE_728x90;
        } else if (appMonetAdSize.width == AdSize.SIZE_160x600.getWidth()
                && appMonetAdSize.height == AdSize.SIZE_160x600.getHeight()) {
            return AdSize.SIZE_160x600;
        } else if (appMonetAdSize.width == AdSize.SIZE_250x250.getWidth()
                && appMonetAdSize.height == AdSize.SIZE_250x250.getHeight()) {
            return AdSize.SIZE_250x250;
        } else if (appMonetAdSize.width == AdSize.SIZE_300x600.getWidth()
                && appMonetAdSize.height == AdSize.SIZE_300x600.getHeight()) {
            return AdSize.SIZE_300x600;
        } else if (appMonetAdSize.width == AdSize.SIZE_320x100.getWidth()
                && appMonetAdSize.height == AdSize.SIZE_320x100.getHeight()) {
            return AdSize.SIZE_320x100;
        } else {
            return AdSize.SIZE_320x50;
        }
    }

    private final Listener mListener = new Listener() {
        @Override
        public void onAdLoaded() {
            if (mBannerAdListener != null) {
                mBannerAdListener.onBannerLoaded(AppMonetView.this);
            }
        }

        @Override
        public void onAdLoadFailed(Throwable error) {
            if (mBannerAdListener != null) {
                mBannerAdListener.onBannerFailed(AppMonetView.this, AppMonetErrorCode.parseHyBidException(error));
            }
        }

        @Override
        public void onAdImpression() {

        }

        @Override
        public void onAdClick() {
            if (mBannerAdListener != null) {
                mBannerAdListener.onBannerClicked(AppMonetView.this);
            }
        }
    };
}
