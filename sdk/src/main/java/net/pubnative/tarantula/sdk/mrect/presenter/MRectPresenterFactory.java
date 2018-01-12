package net.pubnative.tarantula.sdk.mrect.presenter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.VisibleForTesting;

import net.pubnative.tarantula.sdk.models.Ad;
import net.pubnative.tarantula.sdk.utils.AdTracker;
import net.pubnative.tarantula.sdk.utils.Logger;

/**
 * Created by erosgarciaponte on 12.01.18.
 */

public class MRectPresenterFactory {
    @NonNull
    private static final String TAG = MRectPresenterFactory.class.getSimpleName();
    @NonNull
    private final Context mContext;

    public MRectPresenterFactory(@NonNull Context context) {
        mContext = context;
    }

    @Nullable
    public MRectPresenter createMRectPresenter(@NonNull Ad ad,
                                                 @NonNull MRectPresenter.Listener mRectPresenterListener) {
        final MRectPresenter mRectPresenter = fromCreativeType(ad.assetgroupid, ad);
        if (mRectPresenter == null) {
            return null;
        }

        final MRectPresenterDecorator mRectPresenterDecorator = new MRectPresenterDecorator(mRectPresenter,
                new AdTracker(ad.getBeacons(Ad.Beacon.IMPRESSION), ad.getBeacons(Ad.Beacon.CLICK)), mRectPresenterListener);
        mRectPresenter.setListener(mRectPresenterDecorator);
        return mRectPresenterDecorator;
    }

    @Nullable
    @VisibleForTesting
    MRectPresenter fromCreativeType(int assetGroupId, @NonNull Ad ad) {
        switch (assetGroupId) {
            case 8: {
                return new MraidMRectPresenter(mContext, ad);
            }
            default: {
                Logger.e(TAG, "Incompatible asset group type: " + assetGroupId + ", for MRect ad format.");
                return null;
            }
        }
    }
}
