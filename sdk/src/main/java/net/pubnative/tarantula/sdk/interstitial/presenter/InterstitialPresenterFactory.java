package net.pubnative.tarantula.sdk.interstitial.presenter;

import android.app.Activity;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.VisibleForTesting;

import net.pubnative.tarantula.sdk.models.Ad;
import net.pubnative.tarantula.sdk.models.Winner;
import net.pubnative.tarantula.sdk.models.api.PNAPIV3AdModel;
import net.pubnative.tarantula.sdk.utils.AdTracker;
import net.pubnative.tarantula.sdk.utils.Logger;

/**
 * Created by erosgarciaponte on 09.01.18.
 */

public class InterstitialPresenterFactory {
    @NonNull
    private static final String TAG = InterstitialPresenterFactory.class.getSimpleName();
    @NonNull
    private final Activity mActivity;

    public InterstitialPresenterFactory(@NonNull Activity activity) {
        mActivity = activity;
    }

    @Nullable
    public InterstitialPresenter createInterstitialPresenter(
            @NonNull PNAPIV3AdModel ad,
            @NonNull InterstitialPresenter.Listener interstitialPresenterListener) {

        final InterstitialPresenter interstitialPresenter = fromCreativeType(ad.assetgroupid, ad);
        if (interstitialPresenter == null) {
            return null;
        }

        final InterstitialPresenterDecorator interstitialPresenterDecorator =
                new InterstitialPresenterDecorator(interstitialPresenter, new AdTracker(
                        ad.getBeacons(PNAPIV3AdModel.Beacon.IMPRESSION),
                        ad.getBeacons(PNAPIV3AdModel.Beacon.CLICK)), interstitialPresenterListener);
        interstitialPresenter.setListener(interstitialPresenterDecorator);
        return interstitialPresenterDecorator;
    }

    @Nullable
    @VisibleForTesting
    InterstitialPresenter fromCreativeType(int assetGroupId, @NonNull PNAPIV3AdModel ad) {
        switch (assetGroupId) {
            case 21: {
                return new MraidInterstitialPresenter(mActivity, ad);
            }
            case 15:
            case 18:
            case 19:
            case 20: {
                return new VastInterstitialPresenter(mActivity, ad);
            }
            default: {
                Logger.e(TAG, "Incompatible asset group type: " + assetGroupId + ", for interstitial ad format.");
                return null;
            }
        }
    }
}
