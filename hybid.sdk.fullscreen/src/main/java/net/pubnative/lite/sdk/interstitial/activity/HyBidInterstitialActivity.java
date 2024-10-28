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
import net.pubnative.lite.sdk.analytics.ReportingController;
import net.pubnative.lite.sdk.analytics.ReportingEvent;
import net.pubnative.lite.sdk.contentinfo.AdFeedbackFormHelper;
import net.pubnative.lite.sdk.contentinfo.listeners.AdFeedbackLoadListener;
import net.pubnative.lite.sdk.interstitial.HyBidInterstitialBroadcastReceiver;
import net.pubnative.lite.sdk.interstitial.HyBidInterstitialBroadcastSender;
import net.pubnative.lite.sdk.models.Ad;
import net.pubnative.lite.sdk.models.AdExperience;
import net.pubnative.lite.sdk.models.ContentInfo;
import net.pubnative.lite.sdk.models.ContentInfoIconXPosition;
import net.pubnative.lite.sdk.models.ContentInfoIconYPosition;
import net.pubnative.lite.sdk.models.IntegrationType;
import net.pubnative.lite.sdk.models.PositionX;
import net.pubnative.lite.sdk.models.PositionY;
import net.pubnative.lite.sdk.models.RemoteConfig;
import net.pubnative.lite.sdk.utils.Logger;
import net.pubnative.lite.sdk.utils.URLValidator;
import net.pubnative.lite.sdk.utils.UrlHandler;
import net.pubnative.lite.sdk.views.CloseableContainer;
import net.pubnative.lite.sdk.views.PNAPIContentInfoView;
import net.pubnative.lite.sdk.vpaid.VideoAd;
import net.pubnative.lite.sdk.vpaid.helpers.EventTracker;
import net.pubnative.lite.sdk.vpaid.models.vast.Icon;
import net.pubnative.lite.sdk.vpaid.utils.Utils;

import java.util.ArrayList;
import java.util.List;


public abstract class HyBidInterstitialActivity extends Activity implements PNAPIContentInfoView.ContentInfoListener {
    private static final String TAG = HyBidInterstitialActivity.class.getSimpleName();
    private static final int REDUCED_CLOSE_BUTTON_SIZE = 20;
    public static final String EXTRA_ZONE_ID = "extra_pn_zone_id";
    public static final String EXTRA_BROADCAST_ID = "extra_pn_broadcast_id";
    public static final String EXTRA_SKIP_OFFSET = "extra_pn_skip_offset";
    public static final String INTEGRATION_TYPE = "integration_type";

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
    protected Boolean mIsBackEnabled = false;

    protected Boolean mDefaultEndCardClickTracked = false;
    protected Boolean mCustomEndCardClickTracked = false;
    protected List<String> mCustomCTAClickTrackedEvents = new ArrayList<>();

    protected Boolean mDefaultEndCardImpressionTracked = false;
    protected Boolean mCustomEndCardImpressionTracked = false;
    protected Boolean mLoadDefaultEndCardTracked = false;
    protected Boolean mLoadCustomEndCardTracked = false;
    protected Boolean mLoadEndCardFailTracked = false;
    protected Boolean mCustomCTAImpressionTracked = false;
    protected Boolean mDefaultEndCardSkipTracked = false;
    protected Boolean mCustomEndCardSkipTracked = false;
    protected Boolean mCustomEndCardCloseTracked = false;
    protected Boolean mDefaultEndCardCloseTracked = false;
    protected IntegrationType mIntegrationType;
    protected View mContentInfoView = null;
    protected boolean mIsFinishing = false;

    protected boolean mIsVideoFinished = false;
    protected boolean mIsContentInfoIconClickTracked = false;

    protected VideoAd mVideoAd;

    public abstract View getAdView();

    protected abstract boolean shouldShowContentInfo();

    protected AdFeedbackFormHelper adFeedbackFormHelper;

    ReportingController mReportingController;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Intent intent = getIntent();

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);

        mUrlHandlerDelegate = new UrlHandler(this);
        mZoneId = intent.getStringExtra(EXTRA_ZONE_ID);

        validateIntegrationType(intent.getStringExtra(INTEGRATION_TYPE));

        mReportingController = HyBid.getReportingController();

        long broadcastId = intent.getLongExtra(EXTRA_BROADCAST_ID, -1);

        if (!TextUtils.isEmpty(mZoneId) && broadcastId != -1) {
            mBroadcastSender = new HyBidInterstitialBroadcastSender(this, broadcastId);

            View adView = getAdView();

            if (adView != null) {

                mCloseableContainer = new CloseableContainer(this);
                if (hasReducedCloseSize()) {
                    mCloseableContainer.setCloseSize(REDUCED_CLOSE_BUTTON_SIZE);
                }

                FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.MATCH_PARENT);
                params.gravity = Gravity.CENTER;

                mProgressBar = new ProgressBar(this);
                setProgressBarInvisible();

                FrameLayout.LayoutParams pBarParams = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.WRAP_CONTENT, FrameLayout.LayoutParams.WRAP_CONTENT);
                pBarParams.gravity = Gravity.CENTER;

                mCloseableContainer.addView(mProgressBar, pBarParams);

                mCloseableContainer.addView(adView, params);
                mCloseableContainer.setBackgroundColor(Color.BLACK);
                showInterstitialCloseButton();
                if (!mIsVast && shouldShowContentInfo() && getAd() != null) {
                    View contentInfo = getAd().getContentInfoContainer(this, this);
                    if (contentInfo != null) {
                        mCloseableContainer.addView(contentInfo);
                    }
                }
                setContentView(mCloseableContainer);
            } else {
                mIsFinishing = true;
                finish();
            }
        } else {
            mIsFinishing = true;
            finish();
        }
    }

    private void validateIntegrationType(String integration_type) {
        if (integration_type == null) {
            mIntegrationType = IntegrationType.IN_APP_BIDDING;
            return;
        }

        if (integration_type.equals(IntegrationType.HEADER_BIDDING.getCode())) {
            mIntegrationType = IntegrationType.HEADER_BIDDING;
        } else if (integration_type.equals(IntegrationType.MEDIATION.getCode())) {
            mIntegrationType = IntegrationType.MEDIATION;
        } else if (integration_type.equals(IntegrationType.STANDALONE.getCode())) {
            mIntegrationType = IntegrationType.STANDALONE;
        } else {
            mIntegrationType = IntegrationType.IN_APP_BIDDING;
        }
    }

    protected void setupContentInfo() {
        setupContentInfo(null);
    }

    protected void setupContentInfo(Icon icon) {
        if (getAd() != null && mCloseableContainer != null) {
            ContentInfo contentInfo = Utils.parseContentInfo(icon);
            mContentInfoView = getContentInfo(this, getAd(), contentInfo);
            if (mContentInfoView != null) {
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
                    mCloseableContainer.addView(mContentInfoView, layoutParams);
                } else {
                    mCloseableContainer.addView(mContentInfoView);
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

    public void hideContentInfo() {
        if (mContentInfoView != null && mCloseableContainer != null) {
            mCloseableContainer.removeView(mContentInfoView);
        }
    }

    private final CloseableContainer.OnCloseListener mCloseListener = this::closeButtonClicked;

    protected void closeButtonClicked() {
        if (getBroadcastSender() != null) {
            if (mIsVast && !mIsVideoFinished) {
                mVideoAd.skip();
            } else {
                if (mIsVast) mVideoAd.closeVideo();
                getBroadcastSender().sendBroadcast(HyBidInterstitialBroadcastReceiver.Action.DISMISS);
                mIsFinishing = true;
                finish();
            }
        }
    }

    protected void dismiss() {
        if (getBroadcastSender() != null) {
            getBroadcastSender().sendBroadcast(HyBidInterstitialBroadcastReceiver.Action.DISMISS);
        }
        mIsFinishing = true;
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
            if (mIsBackEnabled)
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
            mIsBackEnabled = true;
        }
    }

    protected void hideInterstitialCloseButton() {
        if (mCloseableContainer != null) {
            mCloseableContainer.setCloseVisible(false);
            mCloseableContainer.setOnCloseListener(null);
            mIsBackEnabled = false;
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
    public void onIconClicked(List<String> clickTrackers) {
        if (clickTrackers != null && !clickTrackers.isEmpty()) {
            for (int i = 0; i < clickTrackers.size(); i++) {
                EventTracker.post(this, clickTrackers.get(i), null, false);
            }
        }
        invokeOnContentInfoClick();
    }

    private void invokeOnContentInfoClick() {
        if (mReportingController != null && HyBid.isReportingEnabled()) {
            ReportingEvent reportingEvent = new ReportingEvent();
            reportingEvent.setEventType(Reporting.EventType.CONTENT_INFO_CLICK);
            reportingEvent.setTimestamp(System.currentTimeMillis());
            reportingEvent.setAdFormat(Reporting.AdFormat.FULLSCREEN);
            reportingEvent.setPlatform(Reporting.Platform.ANDROID);
            reportingEvent.setSdkVersion(HyBid.getSDKVersionInfo(mIntegrationType));
            Ad ad = getAd();
            if (ad != null) {
                reportingEvent.setImpId(ad.getSessionId());
                reportingEvent.setCampaignId(ad.getCampaignId());
                reportingEvent.setConfigId(ad.getConfigId());
            }
            mReportingController.reportEvent(reportingEvent);
        }
    }

    String processedURL = "";

    @Override
    public synchronized void onLinkClicked(String url) {
        if (!isLinkClickRunning) {
            isLinkClickRunning = true;
            if (!mIsFeedbackFormOpen && !mIsFeedbackFormLoading) {
                adFeedbackFormHelper = new AdFeedbackFormHelper();
                if (URLValidator.isValidURL(url)) {
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

    private boolean hasReducedCloseSize() {
        if (mAd != null) {
            Boolean hasReducedIconSize = mAd.isIconSizeReduced();
            String adExperience = mAd.getAdExperience();
            return adExperience.equalsIgnoreCase(AdExperience.PERFORMANCE) &&
                    hasReducedIconSize != null && hasReducedIconSize;
        }
        return false;
    }
}
