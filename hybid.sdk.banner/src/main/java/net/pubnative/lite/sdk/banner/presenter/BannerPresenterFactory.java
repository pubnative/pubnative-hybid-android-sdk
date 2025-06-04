// HyBid SDK License
//
// https://github.com/pubnative/pubnative-hybid-android-sdk/blob/main/LICENSE
//
package net.pubnative.lite.sdk.banner.presenter;

import android.content.Context;

import net.pubnative.lite.sdk.models.Ad;
import net.pubnative.lite.sdk.models.AdSize;
import net.pubnative.lite.sdk.models.ApiAssetGroupType;
import net.pubnative.lite.sdk.models.ImpressionTrackingMethod;
import net.pubnative.lite.sdk.models.IntegrationType;
import net.pubnative.lite.sdk.presenter.AdPresenter;
import net.pubnative.lite.sdk.presenter.PresenterFactory;
import net.pubnative.lite.sdk.utils.Logger;

/**
 * Created by erosgarciaponte on 08.01.18.
 */

public class BannerPresenterFactory extends PresenterFactory {
    private static final String TAG = BannerPresenterFactory.class.getSimpleName();

    public BannerPresenterFactory(Context context, IntegrationType integrationType) {
        super(context, integrationType);
    }

    @Override
    public AdPresenter fromCreativeType(int assetGroupId, Ad ad, AdSize adSize) {
        return fromCreativeType(assetGroupId, ad, adSize, ImpressionTrackingMethod.AD_VIEWABLE);
    }

    @Override
    protected AdPresenter fromCreativeType(int assetGroupId, Ad ad, AdSize adSize, ImpressionTrackingMethod trackingMethod) {


        ImpressionTrackingMethod trackingMethodFinal = trackingMethod;
        if (ad != null && ad.getImpressionTrackingMethod() != null &&
                ImpressionTrackingMethod.fromString(ad.getImpressionTrackingMethod()) != null) {
            trackingMethodFinal = ImpressionTrackingMethod.fromString(ad.getImpressionTrackingMethod());
        }

        switch (assetGroupId) {
            case ApiAssetGroupType.MRAID_160x600:
            case ApiAssetGroupType.MRAID_250x250:
            case ApiAssetGroupType.MRAID_300x50:
            case ApiAssetGroupType.MRAID_300x250:
            case ApiAssetGroupType.MRAID_300x600:
            case ApiAssetGroupType.MRAID_320x50:
            case ApiAssetGroupType.MRAID_320x100:
            case ApiAssetGroupType.MRAID_320x480:
            case ApiAssetGroupType.MRAID_480x320:
            case ApiAssetGroupType.MRAID_728x90:
            case ApiAssetGroupType.MRAID_768x1024:
            case ApiAssetGroupType.MRAID_1024x768: {
                return new MraidAdPresenter(getContext(), ad, adSize, trackingMethodFinal);
            }
            case ApiAssetGroupType.VAST_MRECT: {
                return new VastAdPresenter(getContext(), ad, adSize, trackingMethodFinal, mIntegrationType);
            }
            default: {
                Logger.e(TAG, "Incompatible asset group type: " + assetGroupId + ", for banner ad format.");
                return null;
            }
        }
    }
}
