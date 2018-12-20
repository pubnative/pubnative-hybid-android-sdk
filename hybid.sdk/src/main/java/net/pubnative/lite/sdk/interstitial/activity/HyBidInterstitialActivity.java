package net.pubnative.lite.sdk.interstitial.activity;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.FrameLayout;

import net.pubnative.lite.sdk.HyBid;
import net.pubnative.lite.sdk.interstitial.HyBidInterstitialBroadcastReceiver;
import net.pubnative.lite.sdk.interstitial.HyBidInterstitialBroadcastSender;
import net.pubnative.lite.sdk.models.Ad;
import net.pubnative.lite.sdk.utils.UrlHandler;
import net.pubnative.lite.sdk.views.CloseableContainer;

public abstract class HyBidInterstitialActivity extends Activity {
    private static final String TAG = HyBidInterstitialActivity.class.getSimpleName();
    public static final String EXTRA_ZONE_ID = "extra_pn_zone_id";
    public static final String EXTRA_BROADCAST_ID = "extra_pn_broadcast_id";

    private CloseableContainer mCloseableContainer;
    private UrlHandler mUrlHandlerDelegate;
    private Ad mAd;
    private String mZoneId;
    private HyBidInterstitialBroadcastSender mBroadcastSender;

    public abstract View getAdView();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Intent intent = getIntent();

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);

        mUrlHandlerDelegate = new UrlHandler(this);
        mZoneId = intent.getStringExtra(EXTRA_ZONE_ID);
        long broadcastId = intent.getLongExtra(EXTRA_BROADCAST_ID, -1);

        if (!TextUtils.isEmpty(mZoneId) && broadcastId != -1) {
            mBroadcastSender = new HyBidInterstitialBroadcastSender(this, broadcastId);

            View adView = getAdView();

            if (adView != null) {

                mCloseableContainer = new CloseableContainer(this);
                mCloseableContainer.setOnCloseListener(new CloseableContainer.OnCloseListener() {
                    @Override
                    public void onClose() {
                        dismiss();
                    }
                });

                FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.MATCH_PARENT);
                params.gravity = Gravity.CENTER;

                mCloseableContainer.addView(adView, params);
                mCloseableContainer.setBackgroundColor(Color.BLACK);
                setContentView(mCloseableContainer);
            } else {
                finish();
            }
        } else {
            finish();
        }
    }

    protected void dismiss() {
        getBroadcastSender().sendBroadcast(HyBidInterstitialBroadcastReceiver.Action.DISMISS);
        finish();
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
        if (mAd == null) {
            mAd = HyBid.getAdCache().remove(mZoneId);
        }
        return mAd;
    }

    protected HyBidInterstitialBroadcastSender getBroadcastSender() {
        return mBroadcastSender;
    }
}
