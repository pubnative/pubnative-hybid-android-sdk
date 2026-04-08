// HyBid SDK License
//
// https://github.com/pubnative/pubnative-hybid-android-sdk/blob/main/LICENSE
//
package net.pubnative.lite.sdk.interstitial.presenter;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;

import net.pubnative.lite.sdk.HyBid;
import net.pubnative.lite.sdk.models.Ad;
import net.pubnative.lite.sdk.models.ApiAssetGroupType;
import net.pubnative.lite.sdk.models.IntegrationType;
import net.pubnative.lite.sdk.models.SkipOffset;
import net.pubnative.lite.sdk.utils.AdEndCardManager;
import net.pubnative.lite.sdk.utils.AdTracker;
import net.pubnative.lite.sdk.utils.Logger;
import net.pubnative.lite.sdk.utils.SkipOffsetManager;

/**
 * Created by erosgarciaponte on 09.01.18.
 */

public class InterstitialPresenterFactory {
    private static final String TAG = InterstitialPresenterFactory.class.getSimpleName();
    private final Context mContext;
    private final String mZoneId;

    public InterstitialPresenterFactory(Context context, String zoneId) {
        mContext = context;
        mZoneId = zoneId;
    }

    public InterstitialPresenter createInterstitialPresenter(
            Ad ad,
            InterstitialPresenter.Listener interstitialPresenterListener, IntegrationType integrationType) {
        return createInterstitialPresenter(ad, interstitialPresenterListener, integrationType, null);
    }

    public InterstitialPresenter createInterstitialPresenter(
            Ad ad,
            InterstitialPresenter.Listener interstitialPresenterListener,
            IntegrationType integrationType,
            String watermarkData) {
        return createInterstitialPresenter(ad, new SkipOffset(SkipOffsetManager.getDefaultHtmlInterstitialSkipOffset(), false),
                new SkipOffset(ad.hasEndCard() ? SkipOffsetManager.getDefaultVideoWithEndCardSkipOffset() :
                        SkipOffsetManager.getDefaultVideoWithoutEndCardSkipOffset(), false),
                interstitialPresenterListener, integrationType, watermarkData);
    }

    public InterstitialPresenter createInterstitialPresenter(
            Ad ad,
            SkipOffset htmlSkipOffset,
            SkipOffset videoSkipOffset,
            InterstitialPresenter.Listener interstitialPresenterListener, IntegrationType integrationType) {
        return createInterstitialPresenter(ad, htmlSkipOffset, videoSkipOffset, interstitialPresenterListener, integrationType, null);
    }

    public InterstitialPresenter createInterstitialPresenter(
            Ad ad,
            SkipOffset htmlSkipOffset,
            SkipOffset videoSkipOffset,
            InterstitialPresenter.Listener interstitialPresenterListener,
            IntegrationType integrationType,
            String watermarkData) {

        final InterstitialPresenter interstitialPresenter = fromCreativeType(ad.assetgroupid, ad, htmlSkipOffset, videoSkipOffset, integrationType, watermarkData);
        if (interstitialPresenter == null) {
            return null;
        }

        final InterstitialPresenterDecorator interstitialPresenterDecorator =
                new InterstitialPresenterDecorator(
                        interstitialPresenter,
                        new AdTracker(
                                ad.getBeacons(Ad.Beacon.IMPRESSION),
                                ad.getBeacons(Ad.Beacon.CLICK),
                                ad.getBeacons(Ad.Beacon.SDK_EVENT),
                                ad.getBeacons(Ad.Beacon.COMPANION_AD_EVENT),
                                ad.getBeacons(Ad.Beacon.CUSTOM_ENDCARD_EVENT)),
                        new AdTracker(
                                ad.getBeacons(Ad.Beacon.CUSTOM_END_CARD_IMPRESSION),
                                ad.getBeacons(Ad.Beacon.CUSTOM_END_CARD_CLICK)),
                        HyBid.getReportingController(),
                        interstitialPresenterListener,
                        integrationType);
        interstitialPresenter.setListener(interstitialPresenterDecorator);
        interstitialPresenter.setVideoListener(interstitialPresenterDecorator);
        interstitialPresenter.setCustomEndCardListener(interstitialPresenterDecorator);
        return interstitialPresenterDecorator;
    }

    InterstitialPresenter fromCreativeType(int assetGroupId, Ad ad, SkipOffset htmlSkipOffset, SkipOffset videoSkipOffset, IntegrationType integrationType, String watermarkData) {
        switch (assetGroupId) {
            case ApiAssetGroupType.MRAID_300x600:
            case ApiAssetGroupType.MRAID_320x480:
            case ApiAssetGroupType.MRAID_480x320:
            case ApiAssetGroupType.MRAID_1024x768:
            case ApiAssetGroupType.MRAID_768x1024: {
                return new MraidInterstitialPresenter(mContext, ad, mZoneId, htmlSkipOffset.getOffset(), watermarkData);
            }
            case ApiAssetGroupType.VAST_INTERSTITIAL: {
                int videoOffset = videoSkipOffset.getOffset();
                if (!videoSkipOffset.isCustom()) {
                    Boolean hasEndCard = AdEndCardManager.isEndCardEnabled(ad);
                    if (ad.hasEndCard() && hasEndCard) {
                        videoOffset = SkipOffsetManager.getDefaultVideoWithEndCardSkipOffset();
                    } else {
                        videoOffset = SkipOffsetManager.getDefaultVideoWithoutEndCardSkipOffset();
                    }
                }
                return new VastInterstitialPresenter(mContext, ad, mZoneId, videoOffset, integrationType, watermarkData);
            }
            default: {
                Logger.e(TAG, "Incompatible asset group type: " + assetGroupId + ", for interstitial ad format.");
                return null;
            }
        }

    }
}
