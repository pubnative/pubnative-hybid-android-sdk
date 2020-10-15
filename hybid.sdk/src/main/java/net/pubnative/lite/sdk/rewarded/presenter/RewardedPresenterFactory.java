package net.pubnative.lite.sdk.rewarded.presenter;

import android.content.Context;

import net.pubnative.lite.sdk.models.Ad;
import net.pubnative.lite.sdk.models.ApiAssetGroupType;
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
            RewardedPresenter.Listener rewardedPresenterListener) {
        return createRewardedPresenter(ad, 0, rewardedPresenterListener);
    }

    public RewardedPresenter createRewardedPresenter(
            Ad ad,
            int skipOffset,
            RewardedPresenter.Listener rewardedPresenterListener) {

        final RewardedPresenter rewardedPresenter = fromCreativeType(ad.assetgroupid, ad);
        if (rewardedPresenter == null) {
            return null;
        }

        final RewardedPresenterDecorator rewardedPresenterDecorator =
                new RewardedPresenterDecorator(rewardedPresenter, new AdTracker(
                        ad.getBeacons(Ad.Beacon.IMPRESSION),
                        ad.getBeacons(Ad.Beacon.CLICK)), rewardedPresenterListener);
        rewardedPresenter.setListener(rewardedPresenterDecorator);
        return rewardedPresenterDecorator;
    }

    RewardedPresenter fromCreativeType(int assetGroupId, Ad ad) {
        switch (assetGroupId) {
            case ApiAssetGroupType.VAST_INTERSTITIAL: {
                return new VastRewardedPresenter(mContext, ad, mZoneId);
            }
            default: {
                Logger.e(TAG, "Incompatible asset group type: " + assetGroupId + ", for rewarded ad format.");
                return null;
            }
        }
    }
}
