package net.pubnative.tarantula.sdk.interstitial.presenter;

import android.app.Activity;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import net.pubnative.tarantula.sdk.interstitial.vast.VASTPlayer;
import net.pubnative.tarantula.sdk.models.Ad;
import net.pubnative.tarantula.sdk.models.api.PNAPIV3AdModel;
import net.pubnative.tarantula.sdk.utils.CheckUtils;

/**
 * Created by erosgarciaponte on 09.01.18.
 */

public class VastInterstitialPresenter implements InterstitialPresenter, VASTPlayer.VASTPlayerListener {
    @NonNull private final Activity mActivity;
    @NonNull private final PNAPIV3AdModel mAd;

    @Nullable private VASTPlayer mVastPlayer;
    @Nullable private InterstitialPresenter.Listener mListener;
    private boolean mIsDestroyed;

    public VastInterstitialPresenter(@NonNull Activity activity, @NonNull PNAPIV3AdModel ad) {
        mActivity = activity;
        mAd = ad;
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
        if (!CheckUtils.NoThrow.checkArgument(!mIsDestroyed, "VastInterstitialPresenter is destroyed")) {
            return;
        }

        mVastPlayer = new VASTPlayer(mActivity, this);
        mVastPlayer.loadVideoWithData(mAd.getVast());
    }

    @Override
    public void show() {
        if (!CheckUtils.NoThrow.checkArgument(!mIsDestroyed, "VastInterstitialPresenter is destroyed")) {
            return;
        }

        if (mVastPlayer != null) {
            mVastPlayer.play();

            // TODO (steffan): ideally we fire the impression url after we confirm the video successfully plays
            if (mListener != null) {
                mListener.onInterstitialShown(this);
            }
        }
    }

    @Override
    public void destroy() {
        mListener = null;
        mIsDestroyed = true;
    }

    // VASTPlayerListener

    @Override
    public void vastReady() {
        if (mIsDestroyed) {
            return;
        }

        if (mListener != null) {
            mListener.onInterstitialLoaded(this);
        }
    }

    @Override
    public void vastError(int error) {
        if (mIsDestroyed) {
            return;
        }

        if (mListener != null) {
            mListener.onInterstitialError(this);
        }
    }

    @Override
    public void vastClick() {
        if (mIsDestroyed) {
            return;
        }

        if (mListener != null) {
            mListener.onInterstitialClicked(this);
        }
    }

    @Override
    public void vastComplete() {
    }

    @Override
    public void vastDismiss() {
        if (mIsDestroyed) {
            return;
        }

        if (mListener != null) {
            mListener.onInterstitialDismissed(this);
        }
    }
}
