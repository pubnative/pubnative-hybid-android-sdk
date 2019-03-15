package net.pubnative.lite.sdk.interstitial.activity;

import android.os.Bundle;
import android.view.View;

import net.pubnative.lite.sdk.interstitial.HyBidInterstitialBroadcastReceiver;
import net.pubnative.lite.sdk.vpaid.AdBanner;
import net.pubnative.lite.sdk.vpaid.AdListener;
import net.pubnative.lite.sdk.vpaid.PlayerInfo;
import net.pubnative.lite.sdk.vpaid.VideoBannerView;

public class VastInterstitialActivity extends HyBidInterstitialActivity {
    private boolean mReady = false;

    private VideoBannerView mVideoPlayer;
    private AdBanner mVideoAd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getAd() != null) {
            mVideoAd = new AdBanner(this, getAd().getVast());
            mVideoAd.bindView(mVideoPlayer);
            mVideoAd.setAdListener(mAdListener);
            mVideoAd.load();
        }
    }

    @Override
    public View getAdView() {
        if (getAd() != null) {
            mVideoPlayer = new VideoBannerView(this);
            return mVideoPlayer;
        }

        return null;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mVideoAd != null) {
            mVideoAd.destroy();
            mReady = false;
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (mReady) {
            mVideoAd.resume();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (mReady) {
            mVideoAd.pause();
        }
    }

    private final AdListener mAdListener = new AdListener() {
        @Override
        public void onAdLoadSuccess() {
            if (!mReady) {
                mReady = true;

                mVideoAd.show();
                getBroadcastSender().sendBroadcast(HyBidInterstitialBroadcastReceiver.Action.SHOW);
            }
        }

        @Override
        public void onAdLoadFail(PlayerInfo info) {
            getBroadcastSender().sendBroadcast(HyBidInterstitialBroadcastReceiver.Action.ERROR);
            getBroadcastSender().sendBroadcast(HyBidInterstitialBroadcastReceiver.Action.DISMISS);
            finish();
        }

        @Override
        public void onAdClicked() {
            getBroadcastSender().sendBroadcast(HyBidInterstitialBroadcastReceiver.Action.CLICK);
        }

        @Override
        public void onAdDidReachEnd() {
            mReady = false;
        }

        @Override
        public void onAdDismissed() {

        }

        @Override
        public void onAdExpired() {

        }

        @Override
        public void onAdStarted() {

        }
    };
}