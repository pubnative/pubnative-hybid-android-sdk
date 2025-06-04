// HyBid SDK License
//
// https://github.com/pubnative/pubnative-hybid-android-sdk/blob/main/LICENSE
//
package net.pubnative.lite.sdk.presenter;

import android.content.Context;

import net.pubnative.lite.sdk.HyBid;
import net.pubnative.lite.sdk.models.Ad;
import net.pubnative.lite.sdk.models.AdSize;
import net.pubnative.lite.sdk.models.ImpressionTrackingMethod;
import net.pubnative.lite.sdk.models.IntegrationType;
import net.pubnative.lite.sdk.utils.AdTracker;

public abstract class PresenterFactory {

    private final Context mContext;
    protected final IntegrationType mIntegrationType;

    public PresenterFactory(Context context, IntegrationType integrationType) {
        mContext = context;
        mIntegrationType = integrationType;
    }

    public AdPresenter createPresenter(Ad ad, AdSize adSize,
                                       AdPresenter.Listener bannerPresenterListener) {
        return createPresenter(ad, adSize, bannerPresenterListener, null);
    }

    public AdPresenter createPresenter(Ad ad, AdSize adSize,
                                       AdPresenter.Listener bannerPresenterListener, AdPresenter.ImpressionListener impressionListener) {
        return createPresenter(ad, null, adSize, ImpressionTrackingMethod.AD_VIEWABLE, bannerPresenterListener, impressionListener);
    }

    public AdPresenter createPresenter(Ad ad, AdTracker adTracker, AdSize adSize, ImpressionTrackingMethod trackingMethod,
                                       AdPresenter.Listener bannerPresenterListener, AdPresenter.ImpressionListener impressionListener) {
        if (ad == null) {
            return null;
        }

        final AdPresenter adPresenter = fromCreativeType(ad.assetgroupid, ad, adSize, trackingMethod);
        if (adPresenter == null) {
            return null;
        }

        final AdTracker tracker;
        if (adTracker != null) {
            tracker = adTracker;
        } else {
            tracker = new AdTracker(
                    ad.getBeacons(Ad.Beacon.IMPRESSION),
                    ad.getBeacons(Ad.Beacon.CLICK),
                    ad.getBeacons(Ad.Beacon.SDK_EVENT),
                    ad.getBeacons(Ad.Beacon.COMPANION_AD_EVENT),
                    ad.getBeacons(Ad.Beacon.CUSTOM_ENDCARD_EVENT));
        }

        final AdPresenterDecorator bannerPresenterDecorator = new AdPresenterDecorator(
                adPresenter,
                tracker,
                HyBid.getReportingController(),
                bannerPresenterListener,
                impressionListener,
                mIntegrationType);
        adPresenter.setListener(bannerPresenterDecorator);
        adPresenter.setImpressionListener(bannerPresenterDecorator);
        adPresenter.setVideoListener(bannerPresenterDecorator);
        adPresenter.setMRaidListener(bannerPresenterDecorator);
        return bannerPresenterDecorator;
    }

    protected Context getContext() {
        return mContext;
    }

    protected abstract AdPresenter fromCreativeType(int assetGroupId, Ad ad, AdSize adSize);

    protected abstract AdPresenter fromCreativeType(int assetGroupId, Ad ad, AdSize adSize, ImpressionTrackingMethod trackingMethod);
}
