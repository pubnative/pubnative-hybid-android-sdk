package net.pubnative.lite.sdk.mrect.presenter;

import android.view.View;

import net.pubnative.lite.sdk.models.Ad;
import net.pubnative.lite.sdk.utils.AdTracker;
import net.pubnative.lite.sdk.utils.CheckUtils;
import net.pubnative.lite.sdk.utils.Logger;

/**
 * Created by erosgarciaponte on 12.01.18.
 */

public class MRectPresenterDecorator implements MRectPresenter, MRectPresenter.Listener {
    private static final String TAG = MRectPresenterDecorator.class.getSimpleName();
    private final MRectPresenter mMRectPresenter;
    private final AdTracker mAdTrackingDelegate;
    private final MRectPresenter.Listener mListener;
    private boolean mIsDestroyed;

    public MRectPresenterDecorator(MRectPresenter mRectPresenter,
                                    AdTracker adTrackingDelegate,
                                    MRectPresenter.Listener listener) {
        mMRectPresenter = mRectPresenter;
        mAdTrackingDelegate = adTrackingDelegate;
        mListener = listener;
    }

    @Override
    public void setListener(Listener listener) {
        // We set the listener in the constructor instead
    }

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
    public void onMRectLoaded(MRectPresenter mRectPresenter, View mRect) {
        if (mIsDestroyed) {
            return;
        }

        mAdTrackingDelegate.trackImpression();
        mListener.onMRectLoaded(mRectPresenter, mRect);
    }

    @Override
    public void onMRectClicked(MRectPresenter mRectPresenter) {
        if (mIsDestroyed) {
            return;
        }

        mAdTrackingDelegate.trackClick();
        mListener.onMRectClicked(mRectPresenter);
    }

    @Override
    public void onMRectError(MRectPresenter mRectPresenter) {
        if (mIsDestroyed) {
            return;
        }

        String errorMessage = "MRect error for zone id: ";
        Logger.d(TAG, errorMessage);
        mListener.onMRectError(mRectPresenter);
    }
}
