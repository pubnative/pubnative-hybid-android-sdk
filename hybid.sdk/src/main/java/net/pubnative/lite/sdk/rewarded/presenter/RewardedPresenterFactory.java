// HyBid SDK License
//
// https://github.com/pubnative/pubnative-hybid-android-sdk/blob/main/LICENSE
//
package net.pubnative.lite.sdk.rewarded.presenter;

import android.content.Context;
import android.text.TextUtils;

import net.pubnative.lite.sdk.HyBid;
import net.pubnative.lite.sdk.models.Ad;
import net.pubnative.lite.sdk.models.ApiAssetGroupType;
import net.pubnative.lite.sdk.models.IntegrationType;
import net.pubnative.lite.sdk.utils.AdTracker;
import net.pubnative.lite.sdk.utils.Logger;

public class RewardedPresenterFactory {
    private static final String TAG = RewardedPresenterFactory.class.getSimpleName();
    private final Context mContext;
    private final String mZoneId;

    public RewardedPresenterFactory(Context context, String zoneId) {
        mContext = context;
        mZoneId = zoneId;
    }

    public RewardedPresenter createRewardedPresenter(
            Ad ad,
            RewardedPresenter.Listener rewardedPresenterListener, IntegrationType integrationType) {

        final RewardedPresenter rewardedPresenter = fromCreativeType(ad.assetgroupid, ad, integrationType);
        if (rewardedPresenter == null) {
            return null;
        }

        final RewardedPresenterDecorator rewardedPresenterDecorator =
                new RewardedPresenterDecorator(
                        rewardedPresenter,
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
                        rewardedPresenterListener,
                        integrationType);
        rewardedPresenter.setListener(rewardedPresenterDecorator);
        rewardedPresenter.setCustomEndCardListener(rewardedPresenterDecorator);
        return rewardedPresenterDecorator;
    }

    RewardedPresenter fromCreativeType(int assetGroupId, Ad ad, IntegrationType integrationType) {
        switch (assetGroupId) {
            case ApiAssetGroupType.MRAID_300x600:
            case ApiAssetGroupType.MRAID_320x480:
            case ApiAssetGroupType.MRAID_480x320:
            case ApiAssetGroupType.MRAID_1024x768:
            case ApiAssetGroupType.MRAID_768x1024: {
                return new MraidRewardedPresenter(mContext, ad, mZoneId);
            }
            case ApiAssetGroupType.VAST_INTERSTITIAL: {
                return new VastRewardedPresenter(mContext, ad, mZoneId, integrationType);
            }
            default: {
                Logger.e(TAG, "Incompatible asset group type: " + assetGroupId + ", for rewarded ad format.");
                return null;
            }
        }

    }
}
