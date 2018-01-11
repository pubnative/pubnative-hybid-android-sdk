package net.pubnative.tarantula.sdk.interstitial.presenter;

import android.app.Activity;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import net.pubnative.tarantula.sdk.Tarantula;
import net.pubnative.tarantula.sdk.models.Ad;
import net.pubnative.tarantula.sdk.models.api.PNAPIAsset;
import net.pubnative.tarantula.sdk.models.api.PNAPIV3AdModel;
import net.pubnative.tarantula.sdk.mraid.MRAIDInterstitial;
import net.pubnative.tarantula.sdk.mraid.MRAIDNativeFeatureListener;
import net.pubnative.tarantula.sdk.mraid.MRAIDView;
import net.pubnative.tarantula.sdk.mraid.MRAIDViewListener;
import net.pubnative.tarantula.sdk.utils.CheckUtils;
import net.pubnative.tarantula.sdk.utils.UrlHandler;

/**
 * Created by erosgarciaponte on 09.01.18.
 */

public class MraidInterstitialPresenter implements InterstitialPresenter, MRAIDViewListener, MRAIDNativeFeatureListener {
    @NonNull private final Activity mActivity;
    @NonNull private final PNAPIV3AdModel mAd;
    @NonNull private final UrlHandler mUrlHandlerDelegate;
    @NonNull private final String[] mSupportedNativeFeatures;

    @Nullable private InterstitialPresenter.Listener mListener;
    @Nullable private MRAIDInterstitial mMRAIDInterstitial;
    private boolean mIsDestroyed;

    public MraidInterstitialPresenter(@NonNull Activity activity, @NonNull PNAPIV3AdModel ad) {
        mActivity = activity;
        mAd = ad;
        mUrlHandlerDelegate = new UrlHandler(activity);
        mSupportedNativeFeatures = new String[]{};
    }

    @Override
    public void setListener(@Nullable Listener listener) {
        mListener = listener;
    }

    @NonNull
    @Override
    public PNAPIV3AdModel getAd() {
        return mAd;
    }

    @Override
    public void load() {
        if (!CheckUtils.NoThrow.checkArgument(!mIsDestroyed, "MraidInterstitialPresenter is destroyed")) {
            return;
        }

        if (mAd.getAssetUrl(PNAPIAsset.HTML_BANNER) != null) {
            mMRAIDInterstitial = new MRAIDInterstitial(mActivity, mAd.getAssetUrl(PNAPIAsset.HTML_BANNER), "",
                    mSupportedNativeFeatures, this, this);
        } else if (mAd.getAssetHtml(PNAPIAsset.HTML_BANNER) != null) {
            mMRAIDInterstitial = new MRAIDInterstitial(mActivity, "", mAd.getAssetHtml(PNAPIAsset.HTML_BANNER),
                    mSupportedNativeFeatures, this, this);
        }
    }

    @Override
    public void show() {
        if (!CheckUtils.NoThrow.checkArgument(!mIsDestroyed, "MraidInterstitialPresenter is destroyed")) {
            return;
        }

        if (mMRAIDInterstitial != null) {
            mMRAIDInterstitial.show(mActivity);
        }
    }

    @Override
    public void destroy() {
        if (mMRAIDInterstitial != null) {
            mMRAIDInterstitial.destroy();
        }
        mListener = null;
        mIsDestroyed = true;
    }

    @Override
    public void mraidViewLoaded(MRAIDView mraidView) {
        if (mIsDestroyed) {
            return;
        }

        if (mListener != null) {
            mListener.onInterstitialLoaded(this);
        }
    }

    @Override
    public void mraidViewExpand(MRAIDView mraidView) {
        if (mIsDestroyed) {
            return;
        }

        if (mListener != null) {
            mListener.onInterstitialShown(this);
        }
    }

    @Override
    public void mraidViewClose(MRAIDView mraidView) {
        if (mIsDestroyed) {
            return;
        }

        if (mListener != null) {
            mListener.onInterstitialDismissed(this);
        }
    }

    @Override
    public boolean mraidViewResize(MRAIDView mraidView, int width, int height, int offsetX, int offsetY) {
        return true;
    }

    @Override
    public void mraidNativeFeatureCallTel(String url) {

    }

    @Override
    public void mraidNativeFeatureCreateCalendarEvent(String eventJSON) {

    }

    @Override
    public void mraidNativeFeaturePlayVideo(String url) {

    }

    @Override
    public void mraidNativeFeatureOpenBrowser(String url) {
        if (mIsDestroyed) {
            return;
        }

        mUrlHandlerDelegate.handleUrl(url);
        // TODO will this always count as a click? Are there other cases that should be considered a click?
        if (mListener != null) {
            mListener.onInterstitialClicked(this);
        }
    }

    @Override
    public void mraidNativeFeatureStorePicture(String url) {

    }

    @Override
    public void mraidNativeFeatureSendSms(String url) {

    }
}
