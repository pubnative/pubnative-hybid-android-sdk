package net.pubnative.lite.sdk.interstitial.activity;

import android.graphics.Color;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.FrameLayout;

import net.pubnative.lite.sdk.interstitial.HyBidInterstitialBroadcastReceiver;
import net.pubnative.lite.sdk.models.APIAsset;
import net.pubnative.lite.sdk.mraid.MRAIDBanner;
import net.pubnative.lite.sdk.mraid.MRAIDNativeFeature;
import net.pubnative.lite.sdk.mraid.MRAIDNativeFeatureListener;
import net.pubnative.lite.sdk.mraid.MRAIDView;
import net.pubnative.lite.sdk.mraid.MRAIDViewCloseLayoutListener;
import net.pubnative.lite.sdk.mraid.MRAIDViewListener;
import net.pubnative.lite.sdk.utils.ViewUtils;

public class CenteredMraidInterstitialActivity extends HyBidInterstitialActivity implements MRAIDViewListener, MRAIDNativeFeatureListener, MRAIDViewCloseLayoutListener {
    private String[] mSupportedNativeFeatures = new String[]{
            MRAIDNativeFeature.CALENDAR,
            MRAIDNativeFeature.INLINE_VIDEO,
            MRAIDNativeFeature.SMS,
            MRAIDNativeFeature.STORE_PICTURE,
            MRAIDNativeFeature.TEL
    };
    private MRAIDBanner mMRAIDView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View getAdView() {
        mMRAIDView = null;
        FrameLayout container = new FrameLayout(this);
        container.setBackgroundColor(Color.WHITE);
        if (getAd() != null) {

            if (getAd().getAssetUrl(APIAsset.HTML_BANNER) != null) {
                mMRAIDView = new MRAIDBanner(this, getAd().getAssetUrl(APIAsset.HTML_BANNER), "", mSupportedNativeFeatures,
                        this, this, getAd().getContentInfoContainer(this));
            } else if (getAd().getAssetHtml(APIAsset.HTML_BANNER) != null) {
                mMRAIDView = new MRAIDBanner(this, "", getAd().getAssetHtml(APIAsset.HTML_BANNER), mSupportedNativeFeatures,
                        this, this, getAd().getContentInfoContainer(this));
            }

            int width = getAd().getAssetWidth(APIAsset.HTML_BANNER);
            if (width == -1) {
                width = 320;
            }

            int height = getAd().getAssetHeight(APIAsset.HTML_BANNER);
            if (height == -1) {
                height = 480;
            }

            if (mMRAIDView != null) {
                mMRAIDView.setCloseLayoutListener(this);
            }

            FrameLayout.LayoutParams layoutParams = new FrameLayout.LayoutParams(
                    (int) ViewUtils.convertDpToPixel(width, this),
                    (int) ViewUtils.convertDpToPixel(height, this));
            layoutParams.gravity = Gravity.CENTER;

            container.addView(mMRAIDView, layoutParams);
        }
        return container;
    }

    @Override
    protected boolean shouldShowContentInfo() {
        return false;
    }

    @Override
    protected void onDestroy() {
        if (mMRAIDView != null) {
            mMRAIDView.stopAdSession();
            mMRAIDView.destroy();
        }

        super.onDestroy();
    }

// ----------------------------------- MRAIDViewListener ---------------------------------------

    @Override
    public void mraidViewLoaded(MRAIDView mraidView) {
        getBroadcastSender().sendBroadcast(HyBidInterstitialBroadcastReceiver.Action.SHOW);
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
        getBroadcastSender().sendBroadcast(HyBidInterstitialBroadcastReceiver.Action.CLICK);
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
}
