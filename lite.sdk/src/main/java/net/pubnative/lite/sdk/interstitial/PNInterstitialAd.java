package net.pubnative.lite.sdk.interstitial;

import android.app.Activity;

import net.pubnative.lite.sdk.api.InterstitialRequestManager;
import net.pubnative.lite.sdk.api.RequestManager;
import net.pubnative.lite.sdk.interstitial.presenter.InterstitialPresenter;
import net.pubnative.lite.sdk.models.Ad;

public class PNInterstitialAd implements RequestManager.RequestListener, InterstitialPresenter.Listener {
    private static final String TAG = PNInterstitialAd.class.getSimpleName();

    public interface Listener {
        void onInterstitialLoaded();
        void onInterstitialLoadFailed(Throwable error);
        void onInterstitialImpression();
        void onInterstitialClick();
    }

    private RequestManager mRequestManager;
    private Listener mListener;
    private Activity mActivity;
    private String mZoneId;

    public PNInterstitialAd(Activity activity, String zoneId, Listener listener) {
        mRequestManager = new InterstitialRequestManager();
        mActivity = activity;
        mZoneId = zoneId;
        mListener = listener;
    }

    public void load() {

    }

    public void show() {

    }

    public void hide() {

    }

    public boolean isReady() {
        return false;
    }

    public boolean isDestroyed() {
        return false;
    }

    //------------------------------ RequestManager Callbacks --------------------------------------
    @Override
    public void onRequestSuccess(Ad ad) {

    }

    @Override
    public void onRequestFail(Throwable throwable) {

    }

    //------------------------- IntersititialPresenter Callbacks -----------------------------------
    @Override
    public void onInterstitialLoaded(InterstitialPresenter interstitialPresenter) {

    }

    @Override
    public void onInterstitialError(InterstitialPresenter interstitialPresenter) {

    }

    @Override
    public void onInterstitialShown(InterstitialPresenter interstitialPresenter) {

    }

    @Override
    public void onInterstitialClicked(InterstitialPresenter interstitialPresenter) {

    }

    @Override
    public void onInterstitialDismissed(InterstitialPresenter interstitialPresenter) {

    }
}
