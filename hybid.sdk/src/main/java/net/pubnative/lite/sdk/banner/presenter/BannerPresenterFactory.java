package net.pubnative.lite.sdk.banner.presenter;

import android.content.Context;

import net.pubnative.lite.sdk.models.Ad;
import net.pubnative.lite.sdk.models.ApiAssetGroupType;
import net.pubnative.lite.sdk.utils.AdTracker;
import net.pubnative.lite.sdk.utils.Logger;

/**
 * Created by erosgarciaponte on 08.01.18.
 */

public class BannerPresenterFactory {
    private static final String TAG = BannerPresenterFactory.class.getSimpleName();
    private final Context mContext;

    public BannerPresenterFactory(Context context) {
        mContext = context;
    }

    public BannerPresenter createBannerPresenter(Ad ad,
                                                 BannerPresenter.Listener bannerPresenterListener) {
        final BannerPresenter bannerPresenter = fromCreativeType(ad.assetgroupid, ad);
        if (bannerPresenter == null) {
            return null;
        }

        final BannerPresenterDecorator bannerPresenterDecorator = new BannerPresenterDecorator(bannerPresenter,
                new AdTracker(ad.getBeacons(Ad.Beacon.IMPRESSION), ad.getBeacons(Ad.Beacon.CLICK)), bannerPresenterListener);
        bannerPresenter.setListener(bannerPresenterDecorator);
        return bannerPresenterDecorator;
    }

    BannerPresenter fromCreativeType(int assetGroupId, Ad ad) {
        switch (assetGroupId) {
            case ApiAssetGroupType.MRAID_BANNER_1:
            case ApiAssetGroupType.MRAID_BANNER_2: {
                return new MraidBannerPresenter(mContext, ad);
            }
            default: {
                Logger.e(TAG, "Incompatible asset group type: " + assetGroupId + ", for banner ad format.");
                return null;
            }
        }
    }
}
