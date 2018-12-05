package net.pubnative.lite.sdk.interstitial.activity;

import android.os.Bundle;
import android.view.View;

import net.pubnative.lite.sdk.interstitial.HyBidInterstitialBroadcastReceiver;
import net.pubnative.lite.sdk.interstitial.activity.HyBidInterstitialActivity;
import net.pubnative.lite.sdk.models.APIAsset;
import net.pubnative.lite.sdk.mraid.MRAIDBanner;
import net.pubnative.lite.sdk.mraid.MRAIDNativeFeature;
import net.pubnative.lite.sdk.mraid.MRAIDNativeFeatureListener;
import net.pubnative.lite.sdk.mraid.MRAIDView;
import net.pubnative.lite.sdk.mraid.MRAIDViewListener;

public class MraidInterstitialActivity extends HyBidInterstitialActivity implements MRAIDViewListener, MRAIDNativeFeatureListener {
    private String[] mSupportedNativeFeatures = new String[]{
            MRAIDNativeFeature.CALENDAR,
            MRAIDNativeFeature.INLINE_VIDEO,
            MRAIDNativeFeature.SMS,
            MRAIDNativeFeature.STORE_PICTURE,
            MRAIDNativeFeature.TEL
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View getAdView() {
        if (getAd() != null) {
            if (getAd().getAssetUrl(APIAsset.HTML_BANNER) != null) {
                return new MRAIDBanner(this, getAd().getAssetUrl(APIAsset.HTML_BANNER), "", mSupportedNativeFeatures,
                        this, this, getAd().getContentInfoContainer(this));
            } else if (getAd().getAssetHtml(APIAsset.HTML_BANNER) != null) {
                return new MRAIDBanner(this, "", getAd().getAssetHtml(APIAsset.HTML_BANNER), mSupportedNativeFeatures,
                        this, this, getAd().getContentInfoContainer(this));
            }
        }
        return null;
    }

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
}
