package net.pubnative.lite.sdk.interstitial.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.ProgressBar;

import net.pubnative.lite.sdk.HyBid;
import net.pubnative.lite.sdk.analytics.Reporting;
import net.pubnative.lite.sdk.contentinfo.AdFeedbackFormHelper;
import net.pubnative.lite.sdk.contentinfo.listeners.AdFeedbackLoadListener;
import net.pubnative.lite.sdk.interstitial.HyBidInterstitialBroadcastReceiver;
import net.pubnative.lite.sdk.interstitial.HyBidInterstitialBroadcastSender;
import net.pubnative.lite.sdk.models.Ad;
import net.pubnative.lite.sdk.models.ContentInfo;
import net.pubnative.lite.sdk.models.ContentInfoIconXPosition;
import net.pubnative.lite.sdk.models.ContentInfoIconYPosition;
import net.pubnative.lite.sdk.models.IntegrationType;
import net.pubnative.lite.sdk.models.PositionX;
import net.pubnative.lite.sdk.models.PositionY;
import net.pubnative.lite.sdk.utils.Logger;
import net.pubnative.lite.sdk.utils.SkipOffsetManager;
import net.pubnative.lite.sdk.utils.URLValidator;
import net.pubnative.lite.sdk.utils.UrlHandler;
import net.pubnative.lite.sdk.views.CloseableContainer;
import net.pubnative.lite.sdk.views.PNAPIContentInfoView;
import net.pubnative.lite.sdk.vpaid.helpers.EventTracker;
import net.pubnative.lite.sdk.vpaid.helpers.SimpleTimer;
import net.pubnative.lite.sdk.vpaid.models.vast.Icon;
import net.pubnative.lite.sdk.vpaid.utils.Utils;


public abstract class HyBidInterstitialActivity extends Activity implements PNAPIContentInfoView.ContentInfoListener {
    private static final String TAG = HyBidInterstitialActivity.class.getSimpleName();
    public static final String EXTRA_ZONE_ID = "extra_pn_zone_id";
    public static final String EXTRA_BROADCAST_ID = "extra_pn_broadcast_id";
    public static final String EXTRA_SKIP_OFFSET = "extra_pn_skip_offset";

    protected Integer backButtonDelay = -1;

    private CloseableContainer mCloseableContainer;
    private UrlHandler mUrlHandlerDelegate;
    private Ad mAd;
    private String mZoneId;
    private HyBidInterstitialBroadcastSender mBroadcastSender;
    private ProgressBar mProgressBar;
    private boolean mIsVast = false;
    protected boolean mIsFeedbackFormOpen = false;
    private boolean mIsFeedbackFormLoading = false;

    protected Boolean mIsSkippable = false;

    public abstract View getAdView();

    protected abstract boolean shouldShowContentInfo();

    protected AdFeedbackFormHelper adFeedbackFormHelper;

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

            if (getAd() != null) {
                backButtonDelay = SkipOffsetManager.getBackButtonDelay(getAd().getBackButtonDelay());
            }

            if (adView != null) {

                mCloseableContainer = new CloseableContainer(this);

                FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.MATCH_PARENT);
                params.gravity = Gravity.CENTER;

                mProgressBar = new ProgressBar(this);
                setProgressBarInvisible();

                FrameLayout.LayoutParams pBarParams = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.WRAP_CONTENT, FrameLayout.LayoutParams.WRAP_CONTENT);
                pBarParams.gravity = Gravity.CENTER;

                mCloseableContainer.addView(mProgressBar, pBarParams);

                mCloseableContainer.addView(adView, params);
                mCloseableContainer.setBackgroundColor(Color.WHITE);
                showInterstitialCloseButton();
                if (!mIsVast && shouldShowContentInfo() && getAd() != null) {
                    View contentInfo = getAd().getContentInfoContainer(this, this);
                    if (contentInfo != null) {
                        mCloseableContainer.addView(contentInfo);
                    }
                }
                setContentView(mCloseableContainer);
            } else {
                finish();
            }
        } else {
            finish();
        }
    }

    SimpleTimer backButtonTimer;

    protected void handleBackClickability() {
        int delay = backButtonDelay * 1000;

        backButtonTimer = new SimpleTimer(delay, new SimpleTimer.Listener() {

            @Override
            public void onFinish() {
                mIsSkippable = true;
            }

            @Override
            public void onTick(long millisUntilFinished) {
                mIsSkippable = false;
            }
        }, 1000);

        backButtonTimer.start();
    }

    protected void setupContentInfo() {
        setupContentInfo(null);
    }

    protected void setupContentInfo(Icon icon) {
        if (getAd() != null && mCloseableContainer != null) {
            ContentInfo contentInfo = Utils.parseContentInfo(icon);
            View contentInfoView = getContentInfo(this, getAd(), contentInfo);
            if (contentInfoView != null) {
                if (contentInfo != null) {
                    int xGravity = Gravity.START;
                    int yGravity = Gravity.TOP;

                    if (getAd().getContentInfoIconXPosition() != null) {
                        ContentInfoIconXPosition remoteIconXPosition = getAd().getContentInfoIconXPosition();
                        if (remoteIconXPosition == ContentInfoIconXPosition.RIGHT) {
                            xGravity = Gravity.END;
                        }
                    } else {
                        if (contentInfo.getPositionX() == PositionX.RIGHT) {
                            xGravity = Gravity.END;
                        }
                    }

                    if (getAd().getContentInfoIconYPosition() != null) {
                        ContentInfoIconYPosition remoteIconYPosition = getAd().getContentInfoIconYPosition();
                        if (remoteIconYPosition == ContentInfoIconYPosition.BOTTOM) {
                            yGravity = Gravity.BOTTOM;
                        }
                    } else {
                        if (contentInfo.getPositionY() == PositionY.BOTTOM) {
                            yGravity = Gravity.BOTTOM;
                        }
                    }

                    if (yGravity == Gravity.TOP && xGravity == Gravity.END) {
                        mCloseableContainer.setClosePosition(CloseableContainer.ClosePosition.TOP_LEFT);
                    }

                    FrameLayout.LayoutParams layoutParams = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                    layoutParams.gravity = xGravity | yGravity;
                    mCloseableContainer.addView(contentInfoView, layoutParams);
                } else {
                    mCloseableContainer.addView(contentInfoView);
                    if (getAd().getContentInfoIconYPosition() == ContentInfoIconYPosition.TOP && getAd().getContentInfoIconXPosition() == ContentInfoIconXPosition.RIGHT) {
                        mCloseableContainer.setClosePosition(CloseableContainer.ClosePosition.TOP_LEFT);
                    }
                }
                if (contentInfo != null && contentInfo.getViewTrackers() != null && !contentInfo.getViewTrackers().isEmpty()) {
                    for (String tracker : contentInfo.getViewTrackers()) {
                        EventTracker.post(this, tracker, null, true);
                    }
                }
            }
        }
    }

    private View getContentInfo(Context context, Ad ad, ContentInfo contentInfo) {
        return contentInfo == null ? ad.getContentInfoContainer(context, this) : ad.getContentInfoContainer(context, contentInfo, this);
    }

    private final CloseableContainer.OnCloseListener mCloseListener = this::dismiss;

    protected void dismiss() {
        if (getBroadcastSender() != null) {
            getBroadcastSender().sendBroadcast(HyBidInterstitialBroadcastReceiver.Action.DISMISS);
        }
        finish();
    }

    protected String getZoneId() {
        return mZoneId;
    }

    @Override
    protected void onDestroy() {
        if (mCloseableContainer != null) {
            mCloseableContainer.removeAllViews();
        }
        super.onDestroy();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if ((keyCode == KeyEvent.KEYCODE_BACK)) {
            if (mIsSkippable)
                dismiss();
        } else {
            return super.onKeyDown(keyCode, event);
        }
        return false;
    }

    protected CloseableContainer getCloseableContainer() {
        return mCloseableContainer;
    }

    protected void showInterstitialCloseButton() {
        if (mCloseableContainer != null && !isFinishing()) {
            mCloseableContainer.setCloseVisible(true);
            mCloseableContainer.setOnCloseListener(mCloseListener);
        }
    }

    protected void hideInterstitialCloseButton() {
        if (mCloseableContainer != null) {
            mCloseableContainer.setCloseVisible(false);
            mCloseableContainer.setOnCloseListener(null);
        }
    }

    protected void setClosePosition(CloseableContainer.ClosePosition closePosition) {
        mCloseableContainer.setClosePosition(closePosition);
    }

    protected UrlHandler getUrlHandler() {
        return mUrlHandlerDelegate;
    }

    protected Ad getAd() {
        if (mAd == null) {
            synchronized (this) {
                if (HyBid.getAdCache() != null) {
                    mAd = HyBid.getAdCache().remove(mZoneId);
                }
            }
        }
        return mAd;
    }

    // Content info listener
    @Override
    public void onIconClicked() {
        //TODO report content info icon clicked
    }

    String processedURL = "";

    @Override
    public synchronized void onLinkClicked(String url) {
        if (!isLinkClickRunning) {
            isLinkClickRunning = true;
            if (!mIsFeedbackFormOpen && !mIsFeedbackFormLoading) {
                adFeedbackFormHelper = new AdFeedbackFormHelper();
                URLValidator.isValidURL(url, isValid -> {
                    if (isValid) {
                        adFeedbackFormHelper.showFeedbackForm(HyBidInterstitialActivity.this, url, mAd, Reporting.AdFormat.REWARDED, IntegrationType.STANDALONE, new AdFeedbackLoadListener() {
                            @Override
                            public void onLoad(String url1) {
                                mIsFeedbackFormLoading = true;
                            }

                            @Override
                            public void onLoadFinished() {
                                isLinkClickRunning = false;
                                mIsFeedbackFormLoading = false;
                                mIsFeedbackFormOpen = true;
                            }

                            @Override
                            public void onLoadFailed(Throwable error) {
                                isLinkClickRunning = false;
                                mIsFeedbackFormLoading = false;
                                if (mIsFeedbackFormOpen) {
                                    mIsFeedbackFormOpen = false;
                                }
                                Logger.e(TAG, error.getMessage());
                            }

                            @Override
                            public void onFormClosed() {
                                isLinkClickRunning = false;
                                mIsFeedbackFormOpen = false;
                                mIsFeedbackFormLoading = false;
                            }
                        });
                    } else {
                        isLinkClickRunning = false;
                        mIsFeedbackFormOpen = false;
                        mIsFeedbackFormLoading = false;
                        Logger.e(TAG, "Content Info URL is invalid");
                    }
                });
            }
        }
    }

    public boolean isLinkClickRunning = false;

    protected HyBidInterstitialBroadcastSender getBroadcastSender() {
        return mBroadcastSender;
    }

    protected abstract void resumeAd();

    protected abstract void pauseAd();

    protected void setProgressBarVisible() {
        mProgressBar.setVisibility(View.VISIBLE);
    }

    protected void setProgressBarInvisible() {
        mProgressBar.setVisibility(View.INVISIBLE);
    }

    protected void setIsVast(Boolean isVast) {
        this.mIsVast = isVast;
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (backButtonTimer != null) {
            backButtonTimer.pauseTimer();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (backButtonTimer != null) {
            backButtonTimer.resumeTimer();
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (backButtonTimer != null) {
            backButtonTimer.onFinish();
            backButtonTimer = null;
        }
    }
}
