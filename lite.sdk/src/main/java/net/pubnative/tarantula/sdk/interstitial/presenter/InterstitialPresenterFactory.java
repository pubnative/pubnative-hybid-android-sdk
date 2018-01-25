package net.pubnative.tarantula.sdk.interstitial.presenter;

import android.app.Activity;

import net.pubnative.tarantula.sdk.models.Ad;
import net.pubnative.tarantula.sdk.models.ApiAssetGroupType;
import net.pubnative.tarantula.sdk.utils.AdTracker;
import net.pubnative.tarantula.sdk.utils.Logger;

/**
 * Created by erosgarciaponte on 09.01.18.
 */

public class InterstitialPresenterFactory {
    private static final String TAG = InterstitialPresenterFactory.class.getSimpleName();
    private final Activity mActivity;

    public InterstitialPresenterFactory(Activity activity) {
        mActivity = activity;
    }

    public InterstitialPresenter createInterstitialPresenter(
            Ad ad,
            InterstitialPresenter.Listener interstitialPresenterListener) {

        final InterstitialPresenter interstitialPresenter = fromCreativeType(ad.assetgroupid, ad);
        if (interstitialPresenter == null) {
            return null;
        }

        final InterstitialPresenterDecorator interstitialPresenterDecorator =
                new InterstitialPresenterDecorator(interstitialPresenter, new AdTracker(
                        ad.getBeacons(Ad.Beacon.IMPRESSION),
                        ad.getBeacons(Ad.Beacon.CLICK)), interstitialPresenterListener);
        interstitialPresenter.setListener(interstitialPresenterDecorator);
        return interstitialPresenterDecorator;
    }

    InterstitialPresenter fromCreativeType(int assetGroupId, Ad ad) {
        switch (assetGroupId) {
            case ApiAssetGroupType.MRAID_INTERSTITIAL: {
                return new MraidInterstitialPresenter(mActivity, ad);
            }
            case ApiAssetGroupType.VAST_INTERSTITIAL_1:
            case ApiAssetGroupType.VAST_INTERSTITIAL_2:
            case ApiAssetGroupType.VAST_INTERSTITIAL_3:
            case ApiAssetGroupType.VAST_INTERSTITIAL_4: {
                return new VastInterstitialPresenter(mActivity, ad);
            }
            default: {
                Logger.e(TAG, "Incompatible asset group type: " + assetGroupId + ", for interstitial ad format.");
                return null;
            }
        }
    }
}