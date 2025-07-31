// HyBid SDK License
//
// https://github.com/pubnative/pubnative-hybid-android-sdk/blob/main/LICENSE
//
package net.pubnative.lite.sdk.interstitial.viewModel;

import android.content.Context;
import android.view.View;

import net.pubnative.lite.sdk.interstitial.HyBidInterstitialBroadcastReceiver;
import net.pubnative.lite.sdk.interstitial.InterstitialActivityInteractor;
import net.pubnative.lite.sdk.models.APIAsset;
import net.pubnative.lite.sdk.mraid.MRAIDBanner;
import net.pubnative.lite.sdk.mraid.MRAIDNativeFeature;
import net.pubnative.lite.sdk.mraid.MRAIDNativeFeatureListener;
import net.pubnative.lite.sdk.mraid.MRAIDView;
import net.pubnative.lite.sdk.mraid.MRAIDViewCloseLayoutListener;
import net.pubnative.lite.sdk.mraid.MRAIDViewListener;
import net.pubnative.lite.sdk.utils.SkipOffsetManager;

public class MraidInterstitialViewModel extends InterstitialViewModel implements MRAIDViewListener, MRAIDNativeFeatureListener, MRAIDViewCloseLayoutListener {

    private final String[] mSupportedNativeFeatures = new String[]{MRAIDNativeFeature.CALENDAR, MRAIDNativeFeature.INLINE_VIDEO, MRAIDNativeFeature.SMS, MRAIDNativeFeature.STORE_PICTURE, MRAIDNativeFeature.TEL, MRAIDNativeFeature.LOCATION};

    private MRAIDBanner mView;

    public MraidInterstitialViewModel(Context context, String zoneId, String integrationType, int skipOffset, long broadcastId, InterstitialActivityInteractor listener) {
        super(context, zoneId, integrationType, skipOffset, broadcastId, listener);
        processInterstitialAd();
        listener.setContentLayout();
    }

    @Override
    public boolean shouldShowContentInfo() {
        return false;
    }

    @Override
    public void closeButtonClicked() {
        sendBroadcast(HyBidInterstitialBroadcastReceiver.Action.DISMISS);
        mListener.finishActivity();
    }

    @Override
    public View getAdView() {
        MRAIDBanner adView = null;
        if (mAd != null) {
            if (mAd.getAssetUrl(APIAsset.HTML_BANNER) != null) {
                adView = new MRAIDBanner(mContext, mAd.getAssetUrl(APIAsset.HTML_BANNER), "", true, false, mSupportedNativeFeatures, this, this, getContentInfoContainer());
            } else if (mAd.getAssetHtml(APIAsset.HTML_BANNER) != null) {
                adView = new MRAIDBanner(mContext, "", mAd.getAssetHtml(APIAsset.HTML_BANNER), true, false, mSupportedNativeFeatures, this, this, getContentInfoContainer());
            }
            if (adView != null) {
                Integer htmlSkipOffset = SkipOffsetManager.getHTMLSkipOffset(mAd.getHtmlSkipOffset(), true);
                Integer nativeCloseButtonDelay = SkipOffsetManager.getNativeCloseButtonDelay(mAd.getNativeCloseButtonDelay());
                Integer playableSkipOffset = SkipOffsetManager.getPlayableSkipOffset(mAd.getPlayableSkipOffset());
                adView.setCloseLayoutListener(this);
                mIsSkippable = htmlSkipOffset != null && htmlSkipOffset == 0;
                adView.setSkipOffset(htmlSkipOffset);
                adView.setNativeCloseButtonDelay(nativeCloseButtonDelay);
                adView.setIsAdPlayable(mAd.isAdPlayable());
                adView.setPlayableSkipOffset(playableSkipOffset);
                if (mAd.isLandingPage() != null) {
                    adView.setIsLandingPageEnabled(mAd.isLandingPage());
                }
            }
        }
        mView = adView;
        return adView;
    }

    // ----------------------------------- MRAIDViewListener ---------------------------------------

    @Override
    public void mraidViewLoaded(MRAIDView mraidView) {
        sendBroadcast(HyBidInterstitialBroadcastReceiver.Action.SHOW);
    }

    @Override
    public void mraidViewError(MRAIDView mraidView) {
        sendBroadcast(HyBidInterstitialBroadcastReceiver.Action.ERROR);
        dismiss();
    }

    @Override
    public void mraidViewExpand(MRAIDView mraidView) {

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
        mIsSkippable = true;
        mListener.showInterstitialCloseButton(mCloseListener);
    }

    @Override
    public void onExpandedAdClosed() {

    }

    @Override
    public void onReplayClicked() {

    }

    // ------------------------------- MRAIDNativeFeatureListener ----------------------------------

    @Override
    public void mraidNativeFeatureCallTel(String url) {
        sendBroadcast(HyBidInterstitialBroadcastReceiver.Action.CLICK);
    }

    @Override
    public void mraidNativeFeatureCreateCalendarEvent(String eventJSON) {

    }

    @Override
    public void mraidNativeFeaturePlayVideo(String url) {

    }

    @Override
    public void mraidNativeFeatureOpenBrowser(String url) {
        sendBroadcast(HyBidInterstitialBroadcastReceiver.Action.CLICK);
        handleURL(url);
    }

    @Override
    public void mraidNativeFeatureStorePicture(String url) {

    }

    @Override
    public void mraidNativeFeatureSendSms(String url) {
        sendBroadcast(HyBidInterstitialBroadcastReceiver.Action.CLICK);
    }

    // ------------------------------ MRAIDViewCloseLayoutListener ---------------------------------

    @Override
    public void onShowCloseLayout() {
        mIsSkippable = true;
        mListener.showInterstitialCloseButton(mCloseListener);
    }

    @Override
    public void onRemoveCloseLayout() {
        mListener.hideInterstitialCloseButton();
    }

    @Override
    public void onClose() {
        dismiss();
    }

    @Override
    public void pauseAd() {
        if (mView != null) {
            mView.pause();
        }
    }

    @Override
    public void resumeAd() {
        if (!isFeedbackFormOpen() && mView != null) {
            mView.resume();
        }
    }

    @Override
    public void destroyAd() {
        if (mView != null) {
            mView.stopAdSession();
            mView.destroy();
        }
    }
}