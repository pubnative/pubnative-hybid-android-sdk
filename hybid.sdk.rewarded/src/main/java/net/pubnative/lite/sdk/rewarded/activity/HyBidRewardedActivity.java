// The MIT License (MIT)
//
// Copyright (c) 2020 PubNative GmbH
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
package net.pubnative.lite.sdk.rewarded.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.ProgressBar;

import net.pubnative.lite.sdk.HyBid;
import net.pubnative.lite.sdk.models.Ad;
import net.pubnative.lite.sdk.models.ContentInfo;
import net.pubnative.lite.sdk.models.PositionX;
import net.pubnative.lite.sdk.models.PositionY;
import net.pubnative.lite.sdk.rewarded.HyBidRewardedBroadcastReceiver;
import net.pubnative.lite.sdk.rewarded.HyBidRewardedBroadcastSender;
import net.pubnative.lite.sdk.utils.UrlHandler;
import net.pubnative.lite.sdk.views.CloseableContainer;
import net.pubnative.lite.sdk.vpaid.helpers.EventTracker;
import net.pubnative.lite.sdk.vpaid.models.vast.Icon;
import net.pubnative.lite.sdk.vpaid.utils.Utils;

public abstract class HyBidRewardedActivity extends Activity {
    public static final String EXTRA_ZONE_ID = "extra_pn_zone_id";
    public static final String EXTRA_BROADCAST_ID = "extra_pn_broadcast_id";

    private CloseableContainer mCloseableContainer;
    private UrlHandler mUrlHandlerDelegate;
    private Ad mAd;
    private String mZoneId;
    private HyBidRewardedBroadcastSender mBroadcastSender;
    private ProgressBar mProgressBar;

    public abstract View getAdView();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Intent intent = getIntent();

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);

        mUrlHandlerDelegate = new UrlHandler(this);
        mZoneId = intent.getStringExtra(EXTRA_ZONE_ID);
        long broadcastId = intent.getLongExtra(EXTRA_BROADCAST_ID, -1);

        if (!TextUtils.isEmpty(mZoneId) && broadcastId != -1) {
            mBroadcastSender = new HyBidRewardedBroadcastSender(this, broadcastId);

            View adView = getAdView();

            if (adView != null) {

                mCloseableContainer = new CloseableContainer(this);
                hideRewardedCloseButton();

                FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.MATCH_PARENT);
                params.gravity = Gravity.CENTER;

                mProgressBar = new ProgressBar(this);
                setProgressBarInvisible();

                FrameLayout.LayoutParams pBarParams = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.WRAP_CONTENT, FrameLayout.LayoutParams.WRAP_CONTENT);
                pBarParams.gravity = Gravity.CENTER;

                mCloseableContainer.addView(mProgressBar, pBarParams);

                mCloseableContainer.addView(adView, params);
                mCloseableContainer.setBackgroundColor(Color.WHITE);

                setContentView(mCloseableContainer);
            } else {
                finish();
            }
        } else {
            finish();
        }
    }

    protected void setupContentInfo() {
        setupContentInfo(null);
    }

    protected void setupContentInfo(Icon icon) {
        if (getAd() != null && mCloseableContainer != null) {
            ContentInfo contentInfo = Utils.parseContentInfo(icon);
            View contentInfoView = getContentInfo(this, getAd(), contentInfo);
            if (contentInfoView != null) {
                if (contentInfo != null) {
                    int xGravity = Gravity.START;
                    int yGravity = Gravity.TOP;

                    if (contentInfo.getPositionX() == PositionX.RIGHT) {
                        xGravity = Gravity.END;
                    }

                    if (contentInfo.getPositionY() == PositionY.BOTTOM) {
                        yGravity = Gravity.BOTTOM;
                    }
                    FrameLayout.LayoutParams layoutParams = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                    layoutParams.gravity = xGravity | yGravity;
                    mCloseableContainer.addView(contentInfoView, layoutParams);
                } else {
                    mCloseableContainer.addView(contentInfoView);
                }
                if (contentInfo != null && contentInfo.getViewTrackers() != null && !contentInfo.getViewTrackers().isEmpty()) {
                    for (String tracker : contentInfo.getViewTrackers()) {
                        EventTracker.post(this, tracker, null, true);
                    }
                }
            }
        }
    }

    private View getContentInfo(Context context, Ad ad, ContentInfo contentInfo) {
        return contentInfo == null ? ad.getContentInfoContainer(context) : ad.getContentInfoContainer(context, contentInfo);
    }

    private final CloseableContainer.OnCloseListener mCloseListener = this::dismiss;

    protected void dismiss() {
        getBroadcastSender().sendBroadcast(HyBidRewardedBroadcastReceiver.Action.CLOSE);
        finish();
    }

    protected String getZoneId() {
        return mZoneId;
    }

    @Override
    protected void onDestroy() {
        if (mCloseableContainer != null) {
            mCloseableContainer.removeAllViews();
        }
        super.onDestroy();
    }

    @Override
    public void onBackPressed() {
        dismiss();
    }

    protected CloseableContainer getCloseableContainer() {
        return mCloseableContainer;
    }

    protected void showRewardedCloseButton() {
        if (mCloseableContainer != null) {
            mCloseableContainer.setCloseVisible(true);
            mCloseableContainer.setOnCloseListener(mCloseListener);
        }
    }

    protected void hideRewardedCloseButton() {
        if (mCloseableContainer != null) {
            mCloseableContainer.setCloseVisible(false);
            mCloseableContainer.setOnCloseListener(null);
        }
    }

    protected void setClosePosition(CloseableContainer.ClosePosition closePosition) {
        mCloseableContainer.setClosePosition(closePosition);
    }

    protected UrlHandler getUrlHandler() {
        return mUrlHandlerDelegate;
    }

    protected Ad getAd() {
        if (mAd == null && HyBid.getAdCache() != null) {
            mAd = HyBid.getAdCache().remove(mZoneId);
        }
        return mAd;
    }

    protected HyBidRewardedBroadcastSender getBroadcastSender() {
        return mBroadcastSender;
    }

    protected void setProgressBarVisible() {
        mProgressBar.setVisibility(View.VISIBLE);
    }

    protected void setProgressBarInvisible() {
        mProgressBar.setVisibility(View.INVISIBLE);
    }
}
