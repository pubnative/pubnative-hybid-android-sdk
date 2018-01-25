package net.pubnative.tarantula.sdk.mrect.presenter;

import android.content.Context;

import net.pubnative.tarantula.sdk.models.Ad;
import net.pubnative.tarantula.sdk.models.ApiAssetGroupType;
import net.pubnative.tarantula.sdk.utils.AdTracker;
import net.pubnative.tarantula.sdk.utils.Logger;

/**
 * Created by erosgarciaponte on 12.01.18.
 */

public class MRectPresenterFactory {
    private static final String TAG = MRectPresenterFactory.class.getSimpleName();
    private final Context mContext;

    public MRectPresenterFactory(Context context) {
        mContext = context;
    }

    public MRectPresenter createMRectPresenter(Ad ad,
                                                 MRectPresenter.Listener mRectPresenterListener) {
        final MRectPresenter mRectPresenter = fromCreativeType(ad.assetgroupid, ad);
        if (mRectPresenter == null) {
            return null;
        }

        final MRectPresenterDecorator mRectPresenterDecorator = new MRectPresenterDecorator(mRectPresenter,
                new AdTracker(ad.getBeacons(Ad.Beacon.IMPRESSION), ad.getBeacons(Ad.Beacon.CLICK)), mRectPresenterListener);
        mRectPresenter.setListener(mRectPresenterDecorator);
        return mRectPresenterDecorator;
    }

    MRectPresenter fromCreativeType(int assetGroupId, Ad ad) {
        switch (assetGroupId) {
            case ApiAssetGroupType.MRAID_MRECT: {
                return new MraidMRectPresenter(mContext, ad);
            }
            default: {
                Logger.e(TAG, "Incompatible asset group type: " + assetGroupId + ", for MRect ad format.");
                return null;
            }
        }
    }
}
