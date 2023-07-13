package net.pubnative.lite.sdk.interstitial.activity;

import android.annotation.SuppressLint;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.os.Bundle;
import android.view.View;

import net.pubnative.lite.sdk.HyBid;
import net.pubnative.lite.sdk.interstitial.HyBidInterstitialBroadcastReceiver;
import net.pubnative.lite.sdk.models.APIAsset;
import net.pubnative.lite.sdk.models.ContentInfoIconXPosition;
import net.pubnative.lite.sdk.models.ContentInfoIconYPosition;
import net.pubnative.lite.sdk.mraid.MRAIDBanner;
import net.pubnative.lite.sdk.mraid.MRAIDNativeFeature;
import net.pubnative.lite.sdk.mraid.MRAIDNativeFeatureListener;
import net.pubnative.lite.sdk.mraid.MRAIDView;
import net.pubnative.lite.sdk.mraid.MRAIDViewCloseLayoutListener;
import net.pubnative.lite.sdk.mraid.MRAIDViewListener;
import net.pubnative.lite.sdk.utils.SkipOffsetManager;
import net.pubnative.lite.sdk.views.CloseableContainer;
import net.pubnative.lite.sdk.vpaid.models.CloseCardData;
import net.pubnative.lite.sdk.vpaid.utils.CloseCardUtil;

public class MraidInterstitialActivity extends HyBidInterstitialActivity implements MRAIDViewListener, MRAIDNativeFeatureListener, MRAIDViewCloseLayoutListener {
    private final String[] mSupportedNativeFeatures = new String[]{MRAIDNativeFeature.CALENDAR, MRAIDNativeFeature.INLINE_VIDEO, MRAIDNativeFeature.SMS, MRAIDNativeFeature.STORE_PICTURE, MRAIDNativeFeature.TEL, MRAIDNativeFeature.LOCATION};

    private MRAIDBanner mView;

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
        hideInterstitialCloseButton();
    }

    @Override
    public View getAdView() {
        MRAIDBanner adView = null;
        if (getAd() != null) {
            if (getAd().getAssetUrl(APIAsset.HTML_BANNER) != null) {
                adView = new MRAIDBanner(this, getAd().getAssetUrl(APIAsset.HTML_BANNER), "", true, false, mSupportedNativeFeatures, this, this, getAd().getContentInfoContainer(this, this));
            } else if (getAd().getAssetHtml(APIAsset.HTML_BANNER) != null) {
                adView = new MRAIDBanner(this, "", getAd().getAssetHtml(APIAsset.HTML_BANNER), true, false, mSupportedNativeFeatures, this, this, getAd().getContentInfoContainer(this, this));
            }
            if (adView != null) {
                Integer renderingSkipOffset = HyBid.getHtmlInterstitialSkipOffset() != null ? HyBid.getHtmlInterstitialSkipOffset().getOffset() : null;
                Integer mSkipOffset = SkipOffsetManager.getInterstitialHTMLSkipOffset(getAd().getHtmlSkipOffset(), renderingSkipOffset);
                Integer nativeCloseButtonDelay = SkipOffsetManager.getNativeCloseButtonDelay(getAd().getNativeCloseButtonDelay());
                Integer backButtonDelay = SkipOffsetManager.getBackButtonDelay(getAd().getBackButtonDelay());
                adView.setCloseLayoutListener(this);
                mIsSkippable = mSkipOffset != null && mSkipOffset == 0;
                adView.setSkipOffset(mSkipOffset);
                adView.setNativeCloseButtonDelay(nativeCloseButtonDelay);
                adView.setBackButtonDelay(backButtonDelay);
            }
        }
        mView = adView;
        fetchCloseCard();
        defineBackButtonClickableityhandler();
        return adView;
    }

    private void fetchCloseCard() {
        if (mView != null && getAd() != null) {
            CloseCardData data = new CloseCardData();
            new CloseCardUtil().fetchCloseCardData(getAd(), data);
            mView.setCloseCardData(data);
        }
    }

    private void defineBackButtonClickableityhandler() {
        if (mView != null)
            mView.setBackButtonClickableityhandler(this::handleBackClickability);
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

    // ----------------------------------- MRAIDViewListener ---------------------------------------

    @Override
    public void mraidViewLoaded(MRAIDView mraidView) {
        if (getBroadcastSender() != null) {
            getBroadcastSender().sendBroadcast(HyBidInterstitialBroadcastReceiver.Action.SHOW);
        }
    }

    @Override
    public void mraidViewError(MRAIDView mraidView) {
        if (getBroadcastSender() != null) {
            getBroadcastSender().sendBroadcast(HyBidInterstitialBroadcastReceiver.Action.ERROR);
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
    public boolean mraidViewResize(MRAIDView mraidView, int width, int height, int offsetX, int offsetY) {
        return true;
    }

    @Override
    public void mraidShowCloseButton() {
        mIsSkippable = true;
        showInterstitialCloseButton();
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
            getBroadcastSender().sendBroadcast(HyBidInterstitialBroadcastReceiver.Action.CLICK);
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
        showInterstitialCloseButton();
    }

    @Override
    public void onRemoveCloseLayout() {
        hideInterstitialCloseButton();
    }

    @Override
    public void onClose() {
        dismiss();
    }

    @Override
    protected void dismiss() {
        if (mView != null && mView.hasValidCloseCard() && !mView.isCloseCardShown()) {
            mView.showCloseCard(getAd().link);
        } else {
            super.dismiss();
        }
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
