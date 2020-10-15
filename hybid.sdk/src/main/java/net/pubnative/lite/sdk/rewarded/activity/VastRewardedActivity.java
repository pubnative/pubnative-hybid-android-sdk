package net.pubnative.lite.sdk.rewarded.activity;

import android.os.Bundle;
import android.view.View;

import net.pubnative.lite.sdk.HyBid;
import net.pubnative.lite.sdk.rewarded.HyBidRewardedBroadcastReceiver;
import net.pubnative.lite.sdk.vpaid.PlayerInfo;
import net.pubnative.lite.sdk.vpaid.VideoAd;
import net.pubnative.lite.sdk.vpaid.VideoAdCacheItem;
import net.pubnative.lite.sdk.vpaid.VideoAdListener;
import net.pubnative.lite.sdk.vpaid.VideoAdView;

public class VastRewardedActivity extends HyBidRewardedActivity {
    private boolean mReady = false;

    private VideoAdView mVideoPlayer;
    private VideoAd mVideoAd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getAd() != null) {
            mVideoAd = new VideoAd(this, getAd().getVast());
            mVideoAd.setRewarded(true);
            mVideoAd.bindView(mVideoPlayer);
            mVideoAd.setAdListener(mVideoAdListener);
            setProgressBarVisible();

            VideoAdCacheItem adCacheItem = HyBid.getVideoAdCache().remove(getZoneId());
            if (adCacheItem != null) {
                mVideoAd.setVideoCacheItem(adCacheItem);
            }

            mVideoPlayer.postDelayed(new Runnable() {
                @Override
                public void run() {
                    mVideoAd.load();
                }
            }, 1000);
        }
    }

    @Override
    public View getAdView() {
        if (getAd() != null) {
            mVideoPlayer = new VideoAdView(this);
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

    private final VideoAdListener mVideoAdListener = new VideoAdListener() {
        @Override
        public void onAdLoadSuccess() {
            if (!mReady) {
                mReady = true;

                setProgressBarInvisible();
                mVideoAd.show();
                getBroadcastSender().sendBroadcast(HyBidRewardedBroadcastReceiver.Action.OPEN);
            }
        }

        @Override
        public void onAdLoadFail(PlayerInfo info) {
            setProgressBarInvisible();
            getBroadcastSender().sendBroadcast(HyBidRewardedBroadcastReceiver.Action.ERROR);
            finish();
        }

        @Override
        public void onAdClicked() {
            getBroadcastSender().sendBroadcast(HyBidRewardedBroadcastReceiver.Action.CLICK);
        }

        @Override
        public void onAdDidReachEnd() {
            mReady = false;
            showRewardedCloseButton();
            getBroadcastSender().sendBroadcast(HyBidRewardedBroadcastReceiver.Action.FINISH);
        }

        @Override
        public void onAdDismissed() {
            getBroadcastSender().sendBroadcast(HyBidRewardedBroadcastReceiver.Action.CLOSE);
            finish();
        }

        @Override
        public void onAdExpired() {

        }

        @Override
        public void onAdStarted() {

        }
    };
}
