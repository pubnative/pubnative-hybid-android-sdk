package net.pubnative.lite.sdk.interstitial.presenter;

import android.app.Activity;
import android.view.View;
import android.widget.RelativeLayout;

import net.pubnative.lite.sdk.models.APIAsset;
import net.pubnative.lite.sdk.models.Ad;
import net.pubnative.lite.sdk.mraid.MRAIDInterstitial;
import net.pubnative.lite.sdk.mraid.MRAIDNativeFeatureListener;
import net.pubnative.lite.sdk.mraid.MRAIDView;
import net.pubnative.lite.sdk.mraid.MRAIDViewListener;
import net.pubnative.lite.sdk.utils.CheckUtils;
import net.pubnative.lite.sdk.utils.UrlHandler;

/**
 * Created by erosgarciaponte on 09.01.18.
 */

public class MraidInterstitialPresenter implements InterstitialPresenter, MRAIDViewListener, MRAIDNativeFeatureListener {
    private final Activity mActivity;
    private final Ad mAd;
    private final UrlHandler mUrlHandlerDelegate;
    private final String[] mSupportedNativeFeatures;

    private InterstitialPresenter.Listener mListener;
    private MRAIDInterstitial mMRAIDInterstitial;
    private boolean mIsDestroyed;

    public MraidInterstitialPresenter(Activity activity, Ad ad) {
        mActivity = activity;
        mAd = ad;
        mUrlHandlerDelegate = new UrlHandler(activity);
        mSupportedNativeFeatures = new String[]{};
    }

    @Override
    public void setListener(Listener listener) {
        mListener = listener;
    }

    @Override
    public Ad getAd() {
        return mAd;
    }

    @Override
    public void load() {
        if (!CheckUtils.NoThrow.checkArgument(!mIsDestroyed, "MraidInterstitialPresenter is destroyed")) {
            return;
        }

        if (mAd.getAssetUrl(APIAsset.HTML_BANNER) != null) {
            mMRAIDInterstitial = new MRAIDInterstitial(mActivity, mAd.getAssetUrl(APIAsset.HTML_BANNER), "",
                    mSupportedNativeFeatures, this, this, mAd.getContentInfoContainer(mActivity));
        } else if (mAd.getAssetHtml(APIAsset.HTML_BANNER) != null) {
            mMRAIDInterstitial = new MRAIDInterstitial(mActivity, "", mAd.getAssetHtml(APIAsset.HTML_BANNER),
                    mSupportedNativeFeatures, this, this, mAd.getContentInfoContainer(mActivity));
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
