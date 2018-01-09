package net.pubnative.tarantula.sdk.interstitial.presenter;

import android.app.Activity;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.VisibleForTesting;

import net.pubnative.tarantula.sdk.models.Ad;
import net.pubnative.tarantula.sdk.models.Winner;
import net.pubnative.tarantula.sdk.utils.AdTracker;
import net.pubnative.tarantula.sdk.utils.Logger;

/**
 * Created by erosgarciaponte on 09.01.18.
 */

public class InterstitialPresenterFactory {
    @NonNull private static final String TAG = InterstitialPresenterFactory.class.getSimpleName();
    @NonNull private final Activity mActivity;

    public InterstitialPresenterFactory(@NonNull Activity activity) {
        mActivity = activity;
    }

    @Nullable
    public InterstitialPresenter createInterstitialPresenter(
            @NonNull Ad ad,
            @NonNull InterstitialPresenter.Listener interstitialPresenterListener) {

        final InterstitialPresenter interstitialPresenter = fromCreativeType(ad.getWinner().getCreativeType(), ad);
        if (interstitialPresenter == null) {
            return null;
        }

        final InterstitialPresenterDecorator interstitialPresenterDecorator =
                new InterstitialPresenterDecorator(interstitialPresenter, new AdTracker(ad.getSelectedUrls(),
                        ad.getImpressionUrls(), ad.getClickUrls()), interstitialPresenterListener);
        interstitialPresenter.setListener(interstitialPresenterDecorator);
        return interstitialPresenterDecorator;
    }

    @Nullable
    @VisibleForTesting
    InterstitialPresenter fromCreativeType(@NonNull Winner.CreativeType creativeType, @NonNull Ad ad) {
        switch (creativeType) {
            case HTML: {
                return new MraidInterstitialPresenter(mActivity, ad);
            }
            case VAST3: {
                return new VastInterstitialPresenter(mActivity, ad);
            }
            case EMPTY: {
                Logger.d(TAG, "Interstitial creative type is empty");
                return null;
            }
            default: {
                Logger.e(TAG, "Incompatible creative type: " + creativeType + ", for interstitial ad format.");
                return null;
            }
        }
    }
}
