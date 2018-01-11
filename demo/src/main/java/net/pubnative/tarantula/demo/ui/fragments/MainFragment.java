package net.pubnative.tarantula.demo.ui.fragments;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

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
public class MainFragment extends Fragment {

    private MoPubView mMopubView;

    private RequestManager mBannerRequestManager;
    private RequestManager mInterstitialRequestManager;

    public MainFragment() {
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBannerRequestManager = new BannerRequestManager();
        mInterstitialRequestManager = new InterstitialRequestManager();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_main, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
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

    private void loadMraidBanner() {
        mBannerRequestManager.setZoneId(String.valueOf(Constants.BANNER_MRAID_ZONE_ID));
        mBannerRequestManager.setRequestListener(new RequestManager.RequestListener() {
            @Override
            public void onRequestSuccess(@NonNull PNAPIV3AdModel ad) {
                Log.e("Eros", "onRequestSuccess");
            }

            @Override
            public void onRequestFail(@NonNull Throwable throwable) {
                Log.e("Eros", "onRequestFail");
                mBannerRequestManager.startRefreshTimer(RequestManager.DEFAULT_REFRESH_TIME_SECONDS);
            }
        });

        mBannerRequestManager.requestAd();
    }
}
