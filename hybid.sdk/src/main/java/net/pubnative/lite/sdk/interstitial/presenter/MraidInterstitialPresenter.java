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
package net.pubnative.lite.sdk.interstitial.presenter;

import android.app.Activity;
import android.content.Intent;
import android.view.View;
import android.widget.RelativeLayout;

import net.pubnative.lite.sdk.interstitial.HyBidInterstitialActivity;
import net.pubnative.lite.sdk.interstitial.HyBidInterstitialBroadcastReceiver;
import net.pubnative.lite.sdk.interstitial.MraidInterstitialActivity;
import net.pubnative.lite.sdk.models.APIAsset;
import net.pubnative.lite.sdk.models.Ad;
import net.pubnative.lite.sdk.mraid.MRAIDInterstitial;
import net.pubnative.lite.sdk.mraid.MRAIDNativeFeature;
import net.pubnative.lite.sdk.mraid.MRAIDNativeFeatureListener;
import net.pubnative.lite.sdk.mraid.MRAIDView;
import net.pubnative.lite.sdk.mraid.MRAIDViewListener;
import net.pubnative.lite.sdk.utils.CheckUtils;
import net.pubnative.lite.sdk.utils.UrlHandler;

/**
 * Created by erosgarciaponte on 09.01.18.
 */

public class MraidInterstitialPresenter implements InterstitialPresenter, HyBidInterstitialBroadcastReceiver.Listener {
    private final Activity mActivity;
    private final Ad mAd;
    private final String mZoneId;
    private final HyBidInterstitialBroadcastReceiver mBroadcastReceiver;

    private InterstitialPresenter.Listener mListener;
    private boolean mIsDestroyed;
    private boolean mReady = false;

    public MraidInterstitialPresenter(Activity activity, Ad ad, String zoneId) {
        mActivity = activity;
        mBroadcastReceiver = new HyBidInterstitialBroadcastReceiver(mActivity);
        mBroadcastReceiver.setListener(this);
        mAd = ad;
        mZoneId = zoneId;
    }

    @Override
    public void setListener(Listener listener) {
        mListener = listener;
    }

    @Override
    public Ad getAd() {
        return mAd;
    }

    @Override
    public void load() {
        if (!CheckUtils.NoThrow.checkArgument(!mIsDestroyed, "MraidInterstitialPresenter is destroyed")) {
            return;
        }

        mReady = true;
        if (mListener != null) {
            mListener.onInterstitialLoaded(this);
        }
    }

    @Override
    public boolean isReady() {
        return mReady;
    }

    @Override
    public void show() {
        if (!CheckUtils.NoThrow.checkArgument(!mIsDestroyed, "MraidInterstitialPresenter is destroyed")) {
            return;
        }

        mBroadcastReceiver.register();

        Intent intent = new Intent(mActivity, MraidInterstitialActivity.class);
        intent.putExtra(HyBidInterstitialActivity.EXTRA_BROADCAST_ID, mBroadcastReceiver.getBroadcastId());
        intent.putExtra(HyBidInterstitialActivity.EXTRA_ZONE_ID, mZoneId);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        mActivity.startActivity(intent);
    }

    @Override
    public void hide() {
        if (!CheckUtils.NoThrow.checkArgument(!mIsDestroyed, "MraidInterstitialPresenter is destroyed")) {
            return;
        }

        //TODO Implement hide method
    }

    @Override
    public void destroy() {
        mBroadcastReceiver.destroy();
        mListener = null;
        mIsDestroyed = true;
    }

    //----------------------- Interstitial Broadcast Receiver Callbacks ----------------------------
    @Override
    public void onReceivedAction(HyBidInterstitialBroadcastReceiver.Action action) {
        mBroadcastReceiver.handleAction(action, this, mListener);
    }
}
