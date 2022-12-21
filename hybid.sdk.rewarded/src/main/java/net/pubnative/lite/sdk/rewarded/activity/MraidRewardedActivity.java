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

import android.annotation.SuppressLint;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;

import net.pubnative.lite.sdk.HyBid;
import net.pubnative.lite.sdk.models.APIAsset;
import net.pubnative.lite.sdk.mraid.MRAIDBanner;
import net.pubnative.lite.sdk.mraid.MRAIDNativeFeature;
import net.pubnative.lite.sdk.mraid.MRAIDNativeFeatureListener;
import net.pubnative.lite.sdk.mraid.MRAIDView;
import net.pubnative.lite.sdk.mraid.MRAIDViewCloseLayoutListener;
import net.pubnative.lite.sdk.mraid.MRAIDViewListener;
import net.pubnative.lite.sdk.rewarded.HyBidRewardedBroadcastReceiver;

public class MraidRewardedActivity extends HyBidRewardedActivity implements MRAIDViewListener, MRAIDNativeFeatureListener, MRAIDViewCloseLayoutListener {
    private final String[] mSupportedNativeFeatures = new String[]{
            MRAIDNativeFeature.CALENDAR,
            MRAIDNativeFeature.INLINE_VIDEO,
            MRAIDNativeFeature.SMS,
            MRAIDNativeFeature.STORE_PICTURE,
            MRAIDNativeFeature.TEL,
            MRAIDNativeFeature.LOCATION
    };

    private MRAIDBanner mView;
    private boolean mIsSkippable = true;

    @SuppressLint("SourceLockedOrientationActivity")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        }
        if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        }
        super.onCreate(savedInstanceState);
        hideRewardedCloseButton();
    }

    @Override
    public View getAdView() {
        MRAIDBanner adView = null;
        if (getAd() != null) {
            int mSkipOffset = getIntent().getIntExtra(EXTRA_SKIP_OFFSET, 0);
            if (mSkipOffset > 0) {
                mIsSkippable = false;
            }

            if (getAd().getAssetUrl(APIAsset.HTML_BANNER) != null) {
                adView = new MRAIDBanner(this, getAd().getAssetUrl(APIAsset.HTML_BANNER), "", false, mSupportedNativeFeatures,
                        this, this, getAd().getContentInfoContainer(this, HyBid.isAdFeedbackEnabled(), this));
            } else if (getAd().getAssetHtml(APIAsset.HTML_BANNER) != null) {
                adView = new MRAIDBanner(this, "", getAd().getAssetHtml(APIAsset.HTML_BANNER), false, mSupportedNativeFeatures,
                        this, this, getAd().getContentInfoContainer(this, HyBid.isAdFeedbackEnabled(), this));
            }

            if (adView != null) {
                adView.setCloseLayoutListener(this);
            }

            if (mSkipOffset > 0 && adView != null) {
                adView.setSkipOffset(mSkipOffset);
            }
        }
        mView = adView;
        return adView;
    }

    @Override
    protected boolean shouldShowContentInfo() {
        return false;
    }

    @Override
    protected void onDestroy() {
        if (mView != null) {
            mView.stopAdSession();
            mView.destroy();
        }

        super.onDestroy();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if ((keyCode == KeyEvent.KEYCODE_BACK)) {
            if (mIsSkippable) {
                dismiss();
                return true;
            }
        } else {
            return super.onKeyDown(keyCode, event);
        }
        return false;
    }

    // ----------------------------------- MRAIDViewListener ---------------------------------------

    @Override
    public void mraidViewLoaded(MRAIDView mraidView) {
        if (getBroadcastSender() != null) {
            getBroadcastSender().sendBroadcast(HyBidRewardedBroadcastReceiver.Action.OPEN);
            getBroadcastSender().sendBroadcast(HyBidRewardedBroadcastReceiver.Action.FINISH);
        }
    }

    @Override
    public void mraidViewError(MRAIDView mraidView) {
        if (getBroadcastSender() != null) {
            getBroadcastSender().sendBroadcast(HyBidRewardedBroadcastReceiver.Action.ERROR);
        }
        dismiss();
    }

    @Override
    public void mraidViewExpand(MRAIDView mraidView) {

    }

    @Override
    public void mraidViewClose(MRAIDView mraidView) {

    }

    @Override
    public boolean mraidViewResize(MRAIDView mraidView, int width, int height, int offsetX,
                                   int offsetY) {
        return true;
    }

    @Override
    public void mraidShowCloseButton() {
        mIsSkippable = true;
        showRewardedCloseButton();
    }

    @Override
    public void onExpandedAdClosed() {

    }

    // ------------------------------- MRAIDNativeFeatureListener ----------------------------------

    @Override
    public void mraidNativeFeatureCallTel(String url) {

    }

    @Override
    public void mraidNativeFeatureCreateCalendarEvent(String eventJSON) {

    }

    @Override
    public void mraidNativeFeaturePlayVideo(String url) {

    }

    @Override
    public void mraidNativeFeatureOpenBrowser(String url) {
        if (getBroadcastSender() != null) {
            getBroadcastSender().sendBroadcast(HyBidRewardedBroadcastReceiver.Action.CLICK);
        }
        getUrlHandler().handleUrl(url);
    }

    @Override
    public void mraidNativeFeatureStorePicture(String url) {

    }

    @Override
    public void mraidNativeFeatureSendSms(String url) {

    }

    // ------------------------------ MRAIDViewCloseLayoutListener ---------------------------------

    @Override
    public void onShowCloseLayout() {
        mIsSkippable = true;
        showRewardedCloseButton();
    }

    @Override
    public void onRemoveCloseLayout() {
        hideRewardedCloseButton();
    }

    @Override
    public void onClose() {
        dismiss();
    }

    @Override
    protected void onPause() {
        super.onPause();
        pauseAd();
    }

    @Override
    protected void onResume() {
        resumeAd();
        super.onResume();
    }

    @Override
    protected void pauseAd() {
        mView.pause();
    }

    @Override
    protected void resumeAd() {
        if (!mIsFeedbackFormOpen) {
            mView.resume();
        }
    }
}