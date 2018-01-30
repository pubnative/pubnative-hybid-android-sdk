package net.pubnative.lite.demo.ui.fragments;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.crashlytics.android.answers.Answers;
import com.crashlytics.android.answers.CustomEvent;
import com.mopub.mobileads.MoPubErrorCode;
import com.mopub.mobileads.MoPubInterstitial;
import com.mopub.mobileads.MoPubView;

import net.pubnative.lite.demo.Constants;
import net.pubnative.lite.demo.R;
import net.pubnative.lite.demo.ui.activities.InterstitialActivity;
import net.pubnative.lite.sdk.api.BannerRequestManager;
import net.pubnative.lite.sdk.api.InterstitialRequestManager;
import net.pubnative.lite.sdk.api.MRectRequestManager;
import net.pubnative.lite.sdk.api.RequestManager;
import net.pubnative.lite.sdk.models.Ad;
import net.pubnative.lite.sdk.utils.PrebidUtils;

/**
 * A placeholder fragment containing a simple view.
 */
public class MainFragment extends Fragment {

    private MoPubView mMopubBanner;
    private MoPubView mMopubMRect;

    private RequestManager mBannerRequestManager;
    private RequestManager mMRectRequestManager;

    public MainFragment() {
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBannerRequestManager = new BannerRequestManager();
        mMRectRequestManager = new MRectRequestManager();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_main, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mMopubBanner = view.findViewById(R.id.mopub_banner);
        mMopubBanner.setBannerAdListener(mBannerListener);
        mMopubBanner.setAutorefreshEnabled(false);

        mMopubMRect = view.findViewById(R.id.mopub_mrect);
        mMopubMRect.setBannerAdListener(mMRectListener);
        mMopubMRect.setAutorefreshEnabled(false);

        view.findViewById(R.id.button_banner_mraid).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadMraidBanner();
                Answers.getInstance().logCustom(new CustomEvent("request_mraid_banner"));
            }
        });

        view.findViewById(R.id.button_medium_mraid).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadMraidMedium();
                Answers.getInstance().logCustom(new CustomEvent("request_mraid_mrect"));
            }
        });

        view.findViewById(R.id.button_interstitial_mraid).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), InterstitialActivity.class);
                intent.putExtra("zone_id", Constants.INTERSTITIAL_MRAID_ZONE_ID);
                startActivity(intent);
            }
        });
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mMopubBanner.destroy();
        mMopubMRect.destroy();
    }

    private void loadMraidBanner() {
        mBannerRequestManager.setZoneId(Constants.BANNER_MRAID_ZONE_ID);
        mBannerRequestManager.setRequestListener(new RequestManager.RequestListener() {
            @Override
            public void onRequestSuccess(@NonNull Ad ad) {
                mMopubBanner.setAdUnitId(Constants.MOPUB_MRAID_BANNER_AD_UNIT);
                mMopubBanner.setKeywords(PrebidUtils.getPrebidKeywords(ad, Constants.BANNER_MRAID_ZONE_ID));
                mMopubBanner.loadAd();
            }

            @Override
            public void onRequestFail(@NonNull Throwable throwable) {

            }
        });

        mBannerRequestManager.requestAd();
    }

    private void loadMraidMedium() {
        mMRectRequestManager.setZoneId(Constants.MEDIUM_MRAID_ZONE_ID);
        mMRectRequestManager.setRequestListener(new RequestManager.RequestListener() {
            @Override
            public void onRequestSuccess(@NonNull Ad ad) {
                mMopubMRect.setAdUnitId(Constants.MOPUB_MRAID_MEDIUM_AD_UNIT);
                mMopubMRect.setKeywords(PrebidUtils.getPrebidKeywords(ad, Constants.MEDIUM_MRAID_ZONE_ID));
                mMopubMRect.loadAd();
            }

            @Override
            public void onRequestFail(@NonNull Throwable throwable) {

            }
        });

        mMRectRequestManager.requestAd();
    }

    private final MoPubView.BannerAdListener mBannerListener = new MoPubView.BannerAdListener() {
        @Override
        public void onBannerLoaded(MoPubView banner) {

        }

        @Override
        public void onBannerFailed(MoPubView banner, MoPubErrorCode errorCode) {

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
    };

    private final MoPubView.BannerAdListener mMRectListener = new MoPubView.BannerAdListener() {
        @Override
        public void onBannerLoaded(MoPubView banner) {

        }

        @Override
        public void onBannerFailed(MoPubView banner, MoPubErrorCode errorCode) {

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
    };
}
