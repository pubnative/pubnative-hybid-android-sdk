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
import android.view.View;
import android.widget.RelativeLayout;

import net.pubnative.lite.sdk.models.APIAsset;
import net.pubnative.lite.sdk.models.Ad;
import net.pubnative.lite.sdk.mraid.MRAIDBanner;
import net.pubnative.lite.sdk.mraid.MRAIDNativeFeatureListener;
import net.pubnative.lite.sdk.mraid.MRAIDView;
import net.pubnative.lite.sdk.mraid.MRAIDViewListener;
import net.pubnative.lite.sdk.utils.CheckUtils;
import net.pubnative.lite.sdk.utils.UrlHandler;

/**
 * Created by erosgarciaponte on 08.01.18.
 */

public class MraidBannerPresenter implements BannerPresenter, MRAIDViewListener, MRAIDNativeFeatureListener {
    private final Context mContext;
    private final Ad mAd;
    private final UrlHandler mUrlHandlerDelegate;
    private final String[] mSupportedNativeFeatures;

    private BannerPresenter.Listener mListener;
    private MRAIDBanner mMRAIDBanner;
    private boolean mIsDestroyed;

    public MraidBannerPresenter(Context context, Ad ad) {
        mContext = context;
        mAd = ad;
        mUrlHandlerDelegate = new UrlHandler(context);
        mSupportedNativeFeatures = new String[]{};
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
        if (!CheckUtils.NoThrow.checkArgument(!mIsDestroyed, "MraidBannerPresenter is destroyed")) {
            return;
        }

        if (mAd.getAssetUrl(APIAsset.HTML_BANNER) != null) {
            mMRAIDBanner = new MRAIDBanner(mContext, mAd.getAssetUrl(APIAsset.HTML_BANNER), "", mSupportedNativeFeatures,
                    this, this, mAd.getContentInfoContainer(mContext));
        } else if (mAd.getAssetHtml(APIAsset.HTML_BANNER) != null) {
            mMRAIDBanner = new MRAIDBanner(mContext, "", mAd.getAssetHtml(APIAsset.HTML_BANNER), mSupportedNativeFeatures,
                    this, this, mAd.getContentInfoContainer(mContext));
        }

    }

    @Override
    public void destroy() {
        if (mMRAIDBanner != null) {
            mMRAIDBanner.destroy();
        }
        mListener = null;
        mIsDestroyed = true;
    }

    @Override
    public void startTracking() {

    }

    @Override
    public void stopTracking() {

    }

    @Override
    public void mraidViewLoaded(MRAIDView mraidView) {
        if (mIsDestroyed) {
            return;
        }

        if (mListener != null) {
            mListener.onBannerLoaded(this, mraidView);
        }
    }

    @Override
    public void mraidViewExpand(MRAIDView mraidView) {
        if (mIsDestroyed) {
            return;
        }

        if (mListener != null) {
            mListener.onBannerClicked(this);
        }
    }

    @Override
    public void mraidViewClose(MRAIDView mraidView) {

    }

    @Override
    public boolean mraidViewResize(MRAIDView mraidView, int width, int height, int offsetX, int offsetY) {
        return true;
    }

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
        if (mIsDestroyed) {
            return;
        }

        mUrlHandlerDelegate.handleUrl(url);
        // TODO will this always count as a click? Are there other cases that should be considered a click?
        if (mListener != null) {
            mListener.onBannerClicked(this);
        }
    }

    @Override
    public void mraidNativeFeatureStorePicture(String url) {

    }

    @Override
    public void mraidNativeFeatureSendSms(String url) {

    }
}