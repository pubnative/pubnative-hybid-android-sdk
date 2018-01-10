package net.pubnative.tarantula.sdk.banner.presenter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.VisibleForTesting;

import net.pubnative.tarantula.sdk.models.api.PNAPIV3AdModel;
import net.pubnative.tarantula.sdk.utils.AdTracker;
import net.pubnative.tarantula.sdk.utils.Logger;

/**
 * Created by erosgarciaponte on 08.01.18.
 */

public class BannerPresenterFactory {
    @NonNull private static final String TAG = BannerPresenterFactory.class.getSimpleName();
    @NonNull private final Context mContext;

    public BannerPresenterFactory(@NonNull Context context) {
        mContext = context;
    }

    @Nullable
    public BannerPresenter createBannerPresenter(@NonNull PNAPIV3AdModel ad,
                                                 @NonNull BannerPresenter.Listener bannerPresenterListener) {
        final BannerPresenter bannerPresenter = fromCreativeType(ad.assetgroupid, ad);
        if (bannerPresenter == null) {
            return null;
        }

        final BannerPresenterDecorator bannerPresenterDecorator = new BannerPresenterDecorator(bannerPresenter,
                new AdTracker(ad.getBeacons(PNAPIV3AdModel.Beacon.IMPRESSION), ad.getBeacons(PNAPIV3AdModel.Beacon.CLICK)), bannerPresenterListener);
        bannerPresenter.setListener(bannerPresenterDecorator);
        return bannerPresenterDecorator;
    }

    @Nullable
    @VisibleForTesting
    BannerPresenter fromCreativeType(int assetGroupId, @NonNull PNAPIV3AdModel ad) {
        switch (assetGroupId) {
            case 10:
            case 12: {
                return new MraidBannerPresenter(mContext, ad);
            }
            default: {
                Logger.e(TAG, "Incompatible asset group type: " + assetGroupId + ", for banner ad format.");
                return null;
            }
        }
    }
}
