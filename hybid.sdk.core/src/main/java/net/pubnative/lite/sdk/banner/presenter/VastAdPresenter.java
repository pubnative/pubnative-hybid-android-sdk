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
import net.pubnative.lite.sdk.models.ContentInfo;
import net.pubnative.lite.sdk.models.ImpressionTrackingMethod;
import net.pubnative.lite.sdk.presenter.AdPresenter;
import net.pubnative.lite.sdk.VideoListener;
import net.pubnative.lite.sdk.utils.CheckUtils;
import net.pubnative.lite.sdk.utils.Logger;
import net.pubnative.lite.sdk.visibility.ImpressionManager;
import net.pubnative.lite.sdk.visibility.ImpressionTracker;
import net.pubnative.lite.sdk.vpaid.PlayerInfo;
import net.pubnative.lite.sdk.vpaid.VideoAd;
import net.pubnative.lite.sdk.vpaid.VideoAdCacheItem;
import net.pubnative.lite.sdk.vpaid.VideoAdListener;
import net.pubnative.lite.sdk.vpaid.VideoAdView;
import net.pubnative.lite.sdk.vpaid.helpers.EventTracker;
import net.pubnative.lite.sdk.vpaid.models.vast.Icon;
import net.pubnative.lite.sdk.vpaid.utils.Utils;

import org.json.JSONObject;

public class VastAdPresenter implements AdPresenter, ImpressionTracker.Listener {
    private static final String TAG = VastAdPresenter.class.getSimpleName();
    private final Context mContext;
    private final Ad mAd;
    private final ImpressionTrackingMethod mTrackingMethod;

    private Listener mListener;
    private ImpressionListener mImpressionListener;
    private VideoListener mVideoListener;
    private Icon mVastIcon;
    private boolean mIsDestroyed;
    private boolean mLoaded = false;

    private VideoAdView mVideoPlayer;
    private VideoAd mVideoAd;
    private View mContentInfo;

    public VastAdPresenter(Context context, Ad ad, ImpressionTrackingMethod trackingMethod) {
        mContext = context;
        mAd = ad;
        if (trackingMethod != null) {
            mTrackingMethod = trackingMethod;
        } else {
            mTrackingMethod = ImpressionTrackingMethod.AD_RENDERED;
        }
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
    public void setVideoListener(VideoListener listener) {
        mVideoListener = listener;
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

        try {
            mVideoAd = new VideoAd(mContext, mAd, false, false, mVideoImpressionListener);
            mVideoPlayer = new VideoAdView(mContext);
            mVideoAd.bindView(mVideoPlayer);
            mVideoAd.setAdListener(mVideoAdListener);

            if (!TextUtils.isEmpty(getAd().getZoneId())) {
                VideoAdCacheItem adCacheItem = HyBid.getVideoAdCache().remove(getAd().getZoneId());
                if (adCacheItem != null) {
                    mVideoAd.setVideoCacheItem(adCacheItem);
                    if (adCacheItem.getAdParams() != null && adCacheItem.getAdParams().getAdIcon() != null) {
                        mVastIcon = adCacheItem.getAdParams().getAdIcon();
                    }
                }
            }

            mVideoAd.load();
        } catch (Exception exception) {
            Logger.e(TAG, exception.getMessage());
            if (mListener != null) {
                mListener.onAdError(this);
            }

            if (mVideoListener != null) {
                mVideoListener.onVideoError(0);
            }
        }
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
        if (mTrackingMethod == ImpressionTrackingMethod.AD_VIEWABLE) {
            ImpressionManager.startTrackingView(mVideoPlayer, mNativeTrackerListener);
        } else {
            if (mVideoAd != null) {
                mVideoAd.show();
            }
        }
    }

    @Override
    public void stopTracking() {
        if (mTrackingMethod == ImpressionTrackingMethod.AD_VIEWABLE) {
            ImpressionManager.stopTrackingView(mVideoPlayer);
        }
        mVideoAd.dismiss();
    }

    @Override
    public JSONObject getPlacementParams() {
        return null;
    }

    private View buildView() {
        RelativeLayout container = new RelativeLayout(mContext);
        RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        layoutParams.addRule(RelativeLayout.CENTER_IN_PARENT, RelativeLayout.TRUE);

        container.setBackgroundColor(Color.BLACK);

        container.addView(mVideoPlayer, layoutParams);

        setupContentInfo(container);

        return container;
    }

    private void setupContentInfo(ViewGroup container) {
        if (getAd() != null && container != null) {
            ContentInfo contentInfo = Utils.parseContentInfo(mVastIcon);
            mContentInfo = getContentInfo(container.getContext(), getAd(), contentInfo);
            if (mContentInfo != null) {
                container.addView(mContentInfo);
                if (contentInfo != null && contentInfo.getViewTrackers() != null && !contentInfo.getViewTrackers().isEmpty()) {
                    for (String tracker : contentInfo.getViewTrackers()) {
                        EventTracker.post(container.getContext(), tracker, null, true);
                    }
                }
            }
        }
    }

    private View getContentInfo(Context context, Ad ad, ContentInfo contentInfo) {
        return contentInfo == null ? ad.getContentInfoContainer(context) : ad.getContentInfoContainer(context, contentInfo);
    }

    @Override
    public void onImpression(View visibleView) {

    }

    private final ImpressionTracker.Listener mNativeTrackerListener = new ImpressionTracker.Listener() {
        @Override
        public void onImpression(View visibleView) {
            if (mVideoAd != null) {
                mVideoAd.show();
            }
        }
    };

    private final ImpressionListener mVideoImpressionListener = new ImpressionListener() {
        @Override
        public void onImpression() {
            if (mImpressionListener != null) {
                mImpressionListener.onImpression();
            }
        }
    };

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
            if (mVideoListener != null) {
                mVideoListener.onVideoFinished();
            }
        }

        @Override
        public void onAdDismissed() {
            onAdDismissed(-1);
        }

        @Override
        public void onAdDismissed(int progressPercentage) {
            if (mContentInfo != null) {
                mContentInfo.setVisibility(View.GONE);
            }
            if (mVideoListener != null) {
                mVideoListener.onVideoDismissed(progressPercentage);
            }
        }

        @Override
        public void onAdStarted() {
            if (mVideoListener != null) {
                mVideoListener.onVideoStarted();
            }
        }
    };
}
