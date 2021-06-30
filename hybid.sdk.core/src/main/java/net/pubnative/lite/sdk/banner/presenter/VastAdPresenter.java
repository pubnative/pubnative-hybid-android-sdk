// The MIT License (MIT)
//
// Copyright (c) 2018 PubNative GmbH
//
// Permission is hereby granted, free of charge, to any person obtaining a copy
// of this software and associated documentation files (the "Software"), to deal
// in the Software without restriction, including without limitation the rights
// to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
// copies of the Software, and to permit persons to whom the Software is
// furnished to do so, subject to the following conditions:
//
// The above copyright notice and this permission notice shall be included in all
// copies or substantial portions of the Software.
//
// THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
// IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
// FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
// AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
// LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
// OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
// SOFTWARE.
//
package net.pubnative.lite.sdk.banner.presenter;

import android.content.Context;
import android.graphics.Color;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import net.pubnative.lite.sdk.HyBid;
import net.pubnative.lite.sdk.models.Ad;
import net.pubnative.lite.sdk.presenter.AdPresenter;
import net.pubnative.lite.sdk.utils.CheckUtils;
import net.pubnative.lite.sdk.vpaid.PlayerInfo;
import net.pubnative.lite.sdk.vpaid.VideoAd;
import net.pubnative.lite.sdk.vpaid.VideoAdCacheItem;
import net.pubnative.lite.sdk.vpaid.VideoAdListener;
import net.pubnative.lite.sdk.vpaid.VideoAdView;

public class VastAdPresenter implements AdPresenter {
    private final Context mContext;
    private final Ad mAd;

    private Listener mListener;
    private ImpressionListener mImpressionListener;
    private boolean mIsDestroyed;
    private boolean mLoaded = false;

    private VideoAdView mVideoPlayer;
    private VideoAd mVideoAd;

    public VastAdPresenter(Context context, Ad ad) {
        mContext = context;
        mAd = ad;
    }

    @Override
    public void setListener(Listener listener) {
        mListener = listener;
    }

    @Override
    public void setImpressionListener(ImpressionListener listener) {
        mImpressionListener = listener;
    }

    @Override
    public Ad getAd() {
        return mAd;
    }

    @Override
    public void load() {
        if (!CheckUtils.NoThrow.checkArgument(!mIsDestroyed, "VastMRectPresenter is destroyed")) {
            return;
        }

        mVideoAd = new VideoAd(mContext, mAd.getVast(), false, false);
        mVideoPlayer = new VideoAdView(mContext);
        mVideoAd.bindView(mVideoPlayer);
        mVideoAd.setAdListener(mVideoAdListener);

        if (!TextUtils.isEmpty(getAd().getZoneId())) {
            VideoAdCacheItem adCacheItem = HyBid.getVideoAdCache().remove(getAd().getZoneId());
            if (adCacheItem != null) {
                mVideoAd.setVideoCacheItem(adCacheItem);
            }
        }

        mVideoAd.load();
    }

    @Override
    public void destroy() {
        if (mVideoAd != null) {
            mVideoAd.destroy();
        }
        mListener = null;
        mIsDestroyed = true;
    }

    @Override
    public void startTracking() {
        mVideoAd.show();
    }

    @Override
    public void stopTracking() {
        mVideoAd.dismiss();
    }

    private View buildView() {
        RelativeLayout container = new RelativeLayout(mContext);
        RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        layoutParams.addRule(RelativeLayout.CENTER_IN_PARENT, RelativeLayout.TRUE);

        container.setBackgroundColor(Color.BLACK);

        container.addView(mVideoPlayer, layoutParams);

        View contentInfo = getAd().getContentInfoContainer(mContext);
        if (contentInfo != null) {

            container.addView(contentInfo);
        }

        return container;
    }

    private final VideoAdListener mVideoAdListener = new VideoAdListener() {
        @Override
        public void onAdLoadSuccess() {
            if (mIsDestroyed) {
                return;
            }

            if (!mLoaded) {
                mLoaded = true;
                if (mListener != null) {
                    mListener.onAdLoaded(VastAdPresenter.this, buildView());
                }
            }
        }

        @Override
        public void onAdLoadFail(PlayerInfo info) {
            if (mListener != null) {
                mListener.onAdError(VastAdPresenter.this);
            }
        }

        @Override
        public void onAdClicked() {
            if (mIsDestroyed) {
                return;
            }

            if (mListener != null) {
                mListener.onAdClicked(VastAdPresenter.this);
            }
        }

        @Override
        public void onAdDidReachEnd() {

        }

        @Override
        public void onAdDismissed() {

        }

        @Override
        public void onAdExpired() {

        }

        @Override
        public void onAdStarted() {
            mImpressionListener.onImpression();
        }
    };
}
