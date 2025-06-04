// HyBid SDK License
//
// https://github.com/pubnative/pubnative-hybid-android-sdk/blob/main/LICENSE
//
package net.pubnative.lite.sdk.rewarded.viewModel;

import android.content.Context;
import android.view.View;

import net.pubnative.lite.sdk.models.APIAsset;
import net.pubnative.lite.sdk.mraid.MRAIDBanner;
import net.pubnative.lite.sdk.mraid.MRAIDNativeFeature;
import net.pubnative.lite.sdk.mraid.MRAIDNativeFeatureListener;
import net.pubnative.lite.sdk.mraid.MRAIDView;
import net.pubnative.lite.sdk.mraid.MRAIDViewCloseLayoutListener;
import net.pubnative.lite.sdk.mraid.MRAIDViewListener;
import net.pubnative.lite.sdk.rewarded.HyBidRewardedBroadcastReceiver;
import net.pubnative.lite.sdk.rewarded.RewardedActivityInteractor;
import net.pubnative.lite.sdk.utils.SkipOffsetManager;

public class MraidRewardedViewModel extends RewardedViewModel implements MRAIDViewListener, MRAIDNativeFeatureListener, MRAIDViewCloseLayoutListener {

    private final String[] mSupportedNativeFeatures = new String[]{MRAIDNativeFeature.CALENDAR, MRAIDNativeFeature.INLINE_VIDEO, MRAIDNativeFeature.SMS, MRAIDNativeFeature.STORE_PICTURE, MRAIDNativeFeature.TEL, MRAIDNativeFeature.LOCATION};

    private MRAIDBanner mView;

    public MraidRewardedViewModel(Context context, String zoneId, String integrationType, int skipOffset, long broadcastId, RewardedActivityInteractor listener) {
        super(context, zoneId, integrationType, skipOffset, broadcastId, listener);
        processRewardedAd();
        listener.setContentLayout();
    }

    @Override
    public boolean shouldShowContentInfo() {
        return false;
    }

    @Override
    public void closeButtonClicked() {
        sendBroadcast(HyBidRewardedBroadcastReceiver.Action.CLOSE);
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
                Integer mSkipOffset = SkipOffsetManager.getHTMLSkipOffset(mAd.getMraidRewardedSkipOffset(), false);
                Integer nativeCloseButtonDelay = SkipOffsetManager.getNativeCloseButtonDelay(mAd.getNativeCloseButtonDelay());
                adView.setCloseLayoutListener(this);
                mIsSkippable = mSkipOffset != null && mSkipOffset == 0;
                adView.setSkipOffset(mSkipOffset);
                adView.setNativeCloseButtonDelay(nativeCloseButtonDelay);
                if (mAd.isLandingPage() != null) {
                    adView.setIsLandingPageEnabled(mAd.isLandingPage());
                }
            }
        }
        mView = adView;
        return adView;
    }

    @Override
    public void mraidViewLoaded(MRAIDView mraidView) {
        sendBroadcast(HyBidRewardedBroadcastReceiver.Action.OPEN);
    }

    @Override
    public void mraidViewError(MRAIDView mraidView) {
        sendBroadcast(HyBidRewardedBroadcastReceiver.Action.ERROR);
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
        mListener.showRewardedCloseButton(mCloseListener);
    }

    @Override
    public void onExpandedAdClosed() {
    }

    // ------------------------------- MRAIDNativeFeatureListener ----------------------------------

    @Override
    public void mraidNativeFeatureCallTel(String url) {
        sendBroadcast(HyBidRewardedBroadcastReceiver.Action.CLICK);
    }

    @Override
    public void mraidNativeFeatureCreateCalendarEvent(String eventJSON) {

    }

    @Override
    public void mraidNativeFeaturePlayVideo(String url) {

    }

    @Override
    public void mraidNativeFeatureOpenBrowser(String url) {
        sendBroadcast(HyBidRewardedBroadcastReceiver.Action.CLICK);
        handleURL(url);
    }

    @Override
    public void mraidNativeFeatureStorePicture(String url) {

    }

    @Override
    public void mraidNativeFeatureSendSms(String url) {
        sendBroadcast(HyBidRewardedBroadcastReceiver.Action.CLICK);
    }

    // ------------------------------ MRAIDViewCloseLayoutListener ---------------------------------

    @Override
    public void onShowCloseLayout() {
        mIsSkippable = true;
        mListener.showRewardedCloseButton(mCloseListener);
    }

    @Override
    public void onRemoveCloseLayout() {
        mListener.hideRewardedCloseButton();
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