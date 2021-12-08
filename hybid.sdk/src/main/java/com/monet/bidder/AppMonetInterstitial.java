package com.monet.bidder;

import android.content.Context;
import android.webkit.ValueCallback;

import net.pubnative.lite.sdk.interstitial.HyBidInterstitialAd;

public class AppMonetInterstitial extends HyBidInterstitialAd {

    public interface InterstitialAdListener {
        void onInterstitialLoaded(AppMonetInterstitial interstitial);

        void onInterstitialFailed(AppMonetInterstitial interstitial, AppMonetErrorCode errorCode);

        void onInterstitialShown(AppMonetInterstitial interstitial);

        void onInterstitialClicked(AppMonetInterstitial interstitial);

        void onInterstitialDismissed(AppMonetInterstitial interstitial);
    }

    private InterstitialAdListener mInterstitialAdListener;


    public AppMonetInterstitial(final Context context, final String adUnitId) {
        super(context, adUnitId, null);
    }


    public void requestAds(ValueCallback<MonetBid> callback) {

    }

    public void load() {
        super.load();
    }

    public void load(MonetBid bid) {

    }

    public boolean show(){
        return super.show();
    }

    public void setInterstitialAdListener(final InterstitialAdListener listener) {
        this.mInterstitialAdListener = listener;
    }

    public InterstitialAdListener getInterstitialAdListener(){
        return mInterstitialAdListener;
    }

    private final Listener mListener = new Listener() {
        @Override
        public void onInterstitialLoaded() {
            if (mInterstitialAdListener != null){
                mInterstitialAdListener.onInterstitialLoaded(AppMonetInterstitial.this);
            }
        }

        @Override
        public void onInterstitialLoadFailed(Throwable error) {
            if (mInterstitialAdListener != null) {
                    mInterstitialAdListener.onInterstitialFailed(AppMonetInterstitial.this,
                            AppMonetErrorCode.parseHyBidException(error));
            }
        }

        @Override
        public void onInterstitialImpression() {
            if (mInterstitialAdListener != null){
                mInterstitialAdListener.onInterstitialShown(AppMonetInterstitial.this);
            }
        }

        @Override
        public void onInterstitialDismissed() {
            if (mInterstitialAdListener != null){
                mInterstitialAdListener.onInterstitialDismissed(AppMonetInterstitial.this);
            }
        }

        @Override
        public void onInterstitialClick() {
            if (mInterstitialAdListener != null){
                mInterstitialAdListener.onInterstitialClicked(AppMonetInterstitial.this);
            }
        }
    };
}
