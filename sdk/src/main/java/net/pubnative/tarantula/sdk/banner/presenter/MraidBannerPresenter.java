package net.pubnative.tarantula.sdk.banner.presenter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import net.pubnative.tarantula.sdk.Tarantula;
import net.pubnative.tarantula.sdk.models.Ad;
import net.pubnative.tarantula.sdk.mraid.MRAIDBanner;
import net.pubnative.tarantula.sdk.mraid.MRAIDNativeFeatureListener;
import net.pubnative.tarantula.sdk.mraid.MRAIDView;
import net.pubnative.tarantula.sdk.mraid.MRAIDViewListener;
import net.pubnative.tarantula.sdk.utils.CheckUtils;
import net.pubnative.tarantula.sdk.utils.UrlHandler;

/**
 * Created by erosgarciaponte on 08.01.18.
 */

public class MraidBannerPresenter implements BannerPresenter, MRAIDViewListener, MRAIDNativeFeatureListener {
    @NonNull private final Context mContext;
    @NonNull private final Ad mAd;
    @NonNull private final UrlHandler mUrlHandlerDelegate;
    @NonNull private final String[] mSupportedNativeFeatures;

    @Nullable private BannerPresenter.Listener mListener;
    @Nullable private MRAIDBanner mMRAIDBanner;
    private boolean mIsDestroyed;

    public MraidBannerPresenter(@NonNull Context context, @NonNull Ad ad) {
        mContext = context;
        mAd = ad;
        mUrlHandlerDelegate = new UrlHandler(context);
        mSupportedNativeFeatures = new String[]{};
    }

    @Override
    public void setListener(@Nullable Listener listener) {
        mListener = listener;
    }

    @NonNull
    @Override
    public Ad getAd() {
        return mAd;
    }

    @Override
    public void load() {
        if (!CheckUtils.NoThrow.checkArgument(!mIsDestroyed, "MraidBannerPresenter is destroyed")) {
            return;
        }

        mMRAIDBanner = new MRAIDBanner(mContext, "http://" + Tarantula.HOST + "/", mAd.getCreative(), mSupportedNativeFeatures,
                this, this);
    }

    @Override
    public void destroy() {
        if (mMRAIDBanner != null) {
            mMRAIDBanner.destroy();
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
            mListener.onBannerLoaded(this, mraidView);
        }
    }

    @Override
    public void mraidViewExpand(MRAIDView mraidView) {
        if (mIsDestroyed) {
            return;
        }

        if (mListener != null) {
            mListener.onBannerClicked(this);
        }
    }

    @Override
    public void mraidViewClose(MRAIDView mraidView) {

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
        // TODO (steffan): will this always count as a click? Are there other cases that should be considered a click?
        if (mListener != null) {
            mListener.onBannerClicked(this);
        }
    }

    @Override
    public void mraidNativeFeatureStorePicture(String url) {

    }

    @Override
    public void mraidNativeFeatureSendSms(String url) {

    }
}
