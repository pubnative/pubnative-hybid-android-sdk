package net.pubnative.lite.sdk.interstitial;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.FrameLayout;

import net.pubnative.lite.sdk.AdCache;
import net.pubnative.lite.sdk.HyBid;
import net.pubnative.lite.sdk.models.Ad;
import net.pubnative.lite.sdk.utils.UrlHandler;
import net.pubnative.lite.sdk.views.CloseableContainer;

public abstract class HyBidInterstitialActivity extends Activity {
    private static final String TAG = HyBidInterstitialActivity.class.getSimpleName();
    public static final String EXTRA_ZONE_ID = "extra_pn_zone_id";

    private CloseableContainer mCloseableContainer;
    private UrlHandler mUrlHandlerDelegate;
    private Ad mAd;

    public abstract View getAdView();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Intent intent = getIntent();

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);

        mUrlHandlerDelegate = new UrlHandler(this);
        String zoneId = intent.getStringExtra(EXTRA_ZONE_ID);

        if (!TextUtils.isEmpty(zoneId)) {
            mAd = HyBid.getAdCache().remove(zoneId);

            View adView = getAdView();

            if (adView != null) {

                mCloseableContainer = new CloseableContainer(this);
                mCloseableContainer.setOnCloseListener(new CloseableContainer.OnCloseListener() {
                    @Override
                    public void onClose() {
                        finish();
                    }
                });
                mCloseableContainer.addView(adView,
                        new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.MATCH_PARENT));
                setContentView(mCloseableContainer);
            } else {
                finish();
            }
        } else {
            finish();
        }
    }

    @Override
    protected void onDestroy() {
        if (mCloseableContainer != null) {
            mCloseableContainer.removeAllViews();
        }
        super.onDestroy();
    }

    protected CloseableContainer getCloseableContainer() {
        return mCloseableContainer;
    }

    protected void showInterstitialCloseButton() {
        if (mCloseableContainer != null) {
            mCloseableContainer.setCloseVisible(true);
        }
    }

    protected void hideInterstitialCloseButton() {
        if (mCloseableContainer != null) {
            mCloseableContainer.setCloseVisible(false);
        }
    }

    protected UrlHandler getUrlHandler() {
        return mUrlHandlerDelegate;
    }

    protected Ad getAd() {
        return mAd;
    }
}
