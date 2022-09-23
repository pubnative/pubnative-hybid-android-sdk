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

import net.pubnative.lite.sdk.HyBid;
import net.pubnative.lite.sdk.analytics.Reporting;
import net.pubnative.lite.sdk.contentinfo.AdFeedbackView;
import net.pubnative.lite.sdk.models.APIAsset;
import net.pubnative.lite.sdk.models.Ad;
import net.pubnative.lite.sdk.models.AdSize;
import net.pubnative.lite.sdk.models.ImpressionTrackingMethod;
import net.pubnative.lite.sdk.models.IntegrationType;
import net.pubnative.lite.sdk.mraid.MRAIDBanner;
import net.pubnative.lite.sdk.mraid.MRAIDNativeFeature;
import net.pubnative.lite.sdk.mraid.MRAIDNativeFeatureListener;
import net.pubnative.lite.sdk.mraid.MRAIDView;
import net.pubnative.lite.sdk.mraid.MRAIDViewListener;
import net.pubnative.lite.sdk.presenter.AdPresenter;
import net.pubnative.lite.sdk.VideoListener;
import net.pubnative.lite.sdk.utils.CheckUtils;
import net.pubnative.lite.sdk.utils.Logger;
import net.pubnative.lite.sdk.utils.UrlHandler;
import net.pubnative.lite.sdk.views.PNAPIContentInfoView;
import net.pubnative.lite.sdk.visibility.ImpressionManager;
import net.pubnative.lite.sdk.visibility.ImpressionTracker;

import org.json.JSONObject;

/**
 * Created by erosgarciaponte on 08.01.18.
 */

public class MraidAdPresenter implements AdPresenter, MRAIDViewListener, MRAIDNativeFeatureListener, ImpressionTracker.Listener, PNAPIContentInfoView.ContentInfoListener {
    private static final String TAG = MraidAdPresenter.class.getSimpleName();
    private final Context mContext;
    private final Ad mAd;
    private final ImpressionTrackingMethod mTrackingMethod;
    private final UrlHandler mUrlHandlerDelegate;
    private final String[] mSupportedNativeFeatures;
    private AdSize mAdSize;

    private AdPresenter.Listener mListener;
    private ImpressionListener mImpressionListener;
    private MRAIDBanner mMRAIDBanner;
    private boolean mIsDestroyed = false;

    public MraidAdPresenter(Context context, Ad ad, AdSize adSize, ImpressionTrackingMethod trackingMethod) {
        mContext = context;
        mAdSize = adSize;
        mAd = ad;
        if (trackingMethod != null) {
            mTrackingMethod = trackingMethod;
        } else {
            mTrackingMethod = ImpressionTrackingMethod.AD_RENDERED;
        }
        mUrlHandlerDelegate = new UrlHandler(context);
        mSupportedNativeFeatures = new String[]{
                MRAIDNativeFeature.CALENDAR,
                MRAIDNativeFeature.INLINE_VIDEO,
                MRAIDNativeFeature.SMS,
                MRAIDNativeFeature.STORE_PICTURE,
                MRAIDNativeFeature.TEL,
                MRAIDNativeFeature.LOCATION
        };
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
        //Do nothing. No need for video listener in the MRAID presenter
    }

    @Override
    public Ad getAd() {
        return mAd;
    }

    @Override
    public void load() {
        if (!CheckUtils.NoThrow.checkArgument(!mIsDestroyed, "MraidAdPresenter is destroyed")) {
            return;
        }

        if (mAd.getAssetUrl(APIAsset.HTML_BANNER) != null) {
            mMRAIDBanner = new MRAIDBanner(mContext, mAd.getAssetUrl(APIAsset.HTML_BANNER), "", mSupportedNativeFeatures,
                    this, this, mAd.getContentInfoContainer(mContext, HyBid.isAdFeedbackEnabled(), this));
        } else if (mAd.getAssetHtml(APIAsset.HTML_BANNER) != null) {
            mMRAIDBanner = new MRAIDBanner(mContext, "", mAd.getAssetHtml(APIAsset.HTML_BANNER), mSupportedNativeFeatures,
                    this, this, mAd.getContentInfoContainer(mContext, HyBid.isAdFeedbackEnabled(), this));
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
        if (mMRAIDBanner != null && mTrackingMethod == ImpressionTrackingMethod.AD_VIEWABLE) {
            ImpressionManager.startTrackingView(mMRAIDBanner, mAdSize, this);
        }
    }

    @Override
    public void stopTracking() {
        if (mMRAIDBanner != null) {
            mMRAIDBanner.stopAdSession();
            if (mTrackingMethod == ImpressionTrackingMethod.AD_VIEWABLE) {
                ImpressionManager.stopTrackingView(mMRAIDBanner);
            }
        }
    }

    @Override
    public JSONObject getPlacementParams() {
        return null;
    }

    @Override
    public void mraidViewLoaded(MRAIDView mraidView) {
        if (mIsDestroyed) {
            return;
        }

        if (mListener != null) {
            mListener.onAdLoaded(this, mMRAIDBanner);
            if (mTrackingMethod == ImpressionTrackingMethod.AD_RENDERED
                    && mImpressionListener != null) {
                mImpressionListener.onImpression();
            }
        }
    }

    @Override
    public void mraidViewError(MRAIDView mraidView) {
        if (mIsDestroyed) {
            return;
        }

        if (mListener != null) {
            mListener.onAdError(this);
        }
    }

    @Override
    public void mraidViewExpand(MRAIDView mraidView) {
        if (mIsDestroyed) {
            return;
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
    public void mraidShowCloseButton() {
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
        if (mListener != null) {
            mListener.onAdClicked(this);
        }
    }

    @Override
    public void mraidNativeFeatureStorePicture(String url) {

    }

    @Override
    public void mraidNativeFeatureSendSms(String url) {

    }

    @Override
    public void onImpression(View visibleView) {
        if (mImpressionListener != null) {
            mImpressionListener.onImpression();
        }
    }

    // Content info listener
    @Override
    public void onIconClicked() {
        //TODO report content info icon clicked
    }

    @Override
    public void onLinkClicked(String url) {
        AdFeedbackView adFeedbackView = new AdFeedbackView();
        adFeedbackView.prepare(mContext, url, mAd, Reporting.AdFormat.BANNER,
                IntegrationType.STANDALONE, new AdFeedbackView.AdFeedbackLoadListener() {
                    @Override
                    public void onLoadFinished() {
                        if (mMRAIDBanner != null) {
                            mMRAIDBanner.pause();
                        }
                        adFeedbackView.showFeedbackForm(mContext);
                    }

                    @Override
                    public void onLoadFailed(Throwable error) {
                        Logger.e(TAG, error.getMessage());
                    }

                    @Override
                    public void onFormClosed() {
                        if (mMRAIDBanner != null) {
                            mMRAIDBanner.resume();
                        }
                    }
                });
    }
}
