package net.pubnative.tarantula.sdk.mrect.presenter;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.View;

import net.pubnative.tarantula.sdk.models.Ad;
import net.pubnative.tarantula.sdk.utils.AdTracker;
import net.pubnative.tarantula.sdk.utils.CheckUtils;
import net.pubnative.tarantula.sdk.utils.Logger;

/**
 * Created by erosgarciaponte on 12.01.18.
 */

public class MRectPresenterDecorator implements MRectPresenter, MRectPresenter.Listener {
    @NonNull
    private static final String TAG = MRectPresenterDecorator.class.getSimpleName();
    @NonNull
    private final MRectPresenter mMRectPresenter;
    @NonNull
    private final AdTracker mAdTrackingDelegate;
    @NonNull
    private final MRectPresenter.Listener mListener;
    private boolean mIsDestroyed;

    public MRectPresenterDecorator(@NonNull MRectPresenter mRectPresenter,
                                    @NonNull AdTracker adTrackingDelegate,
                                    @NonNull MRectPresenter.Listener listener) {
        mMRectPresenter = mRectPresenter;
        mAdTrackingDelegate = adTrackingDelegate;
        mListener = listener;
    }

    @Override
    public void setListener(@Nullable Listener listener) {
        // We set the listener in the constructor instead
    }

    @NonNull
    @Override
    public Ad getAd() {
        return mMRectPresenter.getAd();
    }

    @Override
    public void load() {
        if (!CheckUtils.NoThrow.checkArgument(!mIsDestroyed, "MRectPresenterDecorator is destroyed")) {
            return;
        }

        //Logger.d(TAG, "Loading MRect presenter for zone id: " + getAd().getAdUnitId());
        mMRectPresenter.load();
    }

    @Override
    public void destroy() {
        //Logger.d(TAG, "Destroying MRect presenter for ad unit id: " + getAd().getAdUnitId());
        mMRectPresenter.destroy();
        mIsDestroyed = true;
    }

    @Override
    public void onMRectLoaded(@NonNull MRectPresenter mRectPresenter, @NonNull View mRect) {
        if (mIsDestroyed) {
            return;
        }

        mAdTrackingDelegate.trackImpression();
        mListener.onMRectLoaded(mRectPresenter, mRect);
    }

    @Override
    public void onMRectClicked(@NonNull MRectPresenter mRectPresenter) {
        if (mIsDestroyed) {
            return;
        }

        mAdTrackingDelegate.trackClick();
        mListener.onMRectClicked(mRectPresenter);
    }

    @Override
    public void onMRectError(@NonNull MRectPresenter mRectPresenter) {
        if (mIsDestroyed) {
            return;
        }

        String errorMessage = "MRect error for zone id: ";
        Logger.d(TAG, errorMessage);
        //mAdTrackingDelegate.trackError(errorMessage);
        mListener.onMRectError(mRectPresenter);
    }
}
