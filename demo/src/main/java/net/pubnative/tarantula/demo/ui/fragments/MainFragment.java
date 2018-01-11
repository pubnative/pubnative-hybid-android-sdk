package net.pubnative.tarantula.demo.ui.fragments;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.mopub.mobileads.MoPubErrorCode;
import com.mopub.mobileads.MoPubInterstitial;
import com.mopub.mobileads.MoPubView;

import net.pubnative.tarantula.demo.Constants;
import net.pubnative.tarantula.demo.R;
import net.pubnative.tarantula.sdk.api.BannerRequestManager;
import net.pubnative.tarantula.sdk.api.InterstitialRequestManager;
import net.pubnative.tarantula.sdk.api.RequestManager;
import net.pubnative.tarantula.sdk.models.api.PNAPIV3AdModel;

/**
 * A placeholder fragment containing a simple view.
 */
public class MainFragment extends Fragment implements MoPubView.BannerAdListener, MoPubInterstitial.InterstitialAdListener {

    private static final int REFRESH_SECONDS = 60;

    private MoPubView mMopubView;
    private MoPubInterstitial mMraidInterstitial;
    private MoPubInterstitial mVideoInterstitial;

    private PNAPIV3AdModel mBannerAd;
    private RequestManager mBannerRequestManager;
    private RequestManager mMraidInterstitialRequestManager;
    private RequestManager mVideoInterstitialRequestManager;

    public MainFragment() {
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBannerRequestManager = new BannerRequestManager();
        mMraidInterstitialRequestManager = new InterstitialRequestManager();
        mVideoInterstitialRequestManager = new InterstitialRequestManager();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_main, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mMopubView = view.findViewById(R.id.mopub_ad);
        mMopubView.setBannerAdListener(this);
        mMopubView.setAutorefreshEnabled(false);

        mMraidInterstitial = new MoPubInterstitial(getActivity(), "");
        mMraidInterstitial.setInterstitialAdListener(this);

        mVideoInterstitial = new MoPubInterstitial(getActivity(), "");
        mVideoInterstitial.setInterstitialAdListener(this);

        view.findViewById(R.id.button_banner_mraid).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadMraidBanner();
            }
        });

        view.findViewById(R.id.button_interstitial_mraid).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        view.findViewById(R.id.button_interstitial_video).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mMopubView.destroy();
    }

    private void loadMraidBanner() {
        mBannerRequestManager.setZoneId(Constants.BANNER_MRAID_ZONE_ID);
        mBannerRequestManager.setRequestListener(new RequestManager.RequestListener() {
            @Override
            public void onRequestSuccess(@NonNull PNAPIV3AdModel ad) {
                mBannerAd = ad;
                mMopubView.setAdUnitId("");
                mMopubView.setKeywords("");
                mMopubView.loadAd();
            }

            @Override
            public void onRequestFail(@NonNull Throwable throwable) {
                Log.e("Eros", "onRequestFail");
                mBannerRequestManager.startRefreshTimer(RequestManager.DEFAULT_REFRESH_TIME_SECONDS);
            }
        });

        mBannerRequestManager.requestAd();
    }

    private void loadMraidInterstitial() {
        mMraidInterstitialRequestManager.setZoneId(Constants.INTERSTITIAL_MRAID_ZONE_ID);
        mMraidInterstitialRequestManager.setRequestListener(new RequestManager.RequestListener() {
            @Override
            public void onRequestSuccess(@NonNull PNAPIV3AdModel ad) {
                mMraidInterstitial.setKeywords("");
                mMraidInterstitial.load();
            }

            @Override
            public void onRequestFail(@NonNull Throwable throwable) {
                mMraidInterstitialRequestManager.startRefreshTimer(RequestManager.DEFAULT_REFRESH_TIME_SECONDS);
            }
        });

        mMraidInterstitialRequestManager.requestAd();
    }

    private void loadVideoInterstitial() {
        mVideoInterstitialRequestManager.setZoneId(Constants.INTERSTITIAL_VIDEO_ZONE_ID);
        mVideoInterstitialRequestManager.setRequestListener(new RequestManager.RequestListener() {
            @Override
            public void onRequestSuccess(@NonNull PNAPIV3AdModel ad) {
                mVideoInterstitial.setKeywords("");
                mVideoInterstitial.load();
            }

            @Override
            public void onRequestFail(@NonNull Throwable throwable) {
                mVideoInterstitialRequestManager.startRefreshTimer(RequestManager.DEFAULT_REFRESH_TIME_SECONDS);
            }
        });

        mVideoInterstitialRequestManager.requestAd();
    }

    // MoPubView.BannerAdListener
    @Override
    public void onBannerLoaded(MoPubView banner) {
        mBannerRequestManager.startRefreshTimer(REFRESH_SECONDS);
    }

    @Override
    public void onBannerFailed(MoPubView banner, MoPubErrorCode errorCode) {
        mBannerRequestManager.startRefreshTimer(REFRESH_SECONDS);
    }

    @Override
    public void onBannerClicked(MoPubView banner) {

    }

    @Override
    public void onBannerExpanded(MoPubView banner) {

    }

    @Override
    public void onBannerCollapsed(MoPubView banner) {

    }

    // MoPubInterstitial.InterstitialAdListener
    @Override
    public void onInterstitialLoaded(MoPubInterstitial interstitial) {
        //mMopubInterstitial.show();
    }

    @Override
    public void onInterstitialFailed(MoPubInterstitial interstitial, MoPubErrorCode errorCode) {

    }

    @Override
    public void onInterstitialShown(MoPubInterstitial interstitial) {

    }

    @Override
    public void onInterstitialClicked(MoPubInterstitial interstitial) {

    }

    @Override
    public void onInterstitialDismissed(MoPubInterstitial interstitial) {

    }
}
