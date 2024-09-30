package net.pubnative.lite.sdk.interstitial.activity;

import static net.pubnative.lite.sdk.analytics.Reporting.Key.CLICK_SOURCE_TYPE_END_CARD;

import android.annotation.SuppressLint;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;

import net.pubnative.lite.sdk.HyBid;
import net.pubnative.lite.sdk.analytics.Reporting;
import net.pubnative.lite.sdk.analytics.ReportingEvent;
import net.pubnative.lite.sdk.interstitial.HyBidInterstitialBroadcastReceiver;
import net.pubnative.lite.sdk.models.Ad;
import net.pubnative.lite.sdk.models.IntegrationType;
import net.pubnative.lite.sdk.presenter.AdPresenter;
import net.pubnative.lite.sdk.utils.AdEndCardManager;
import net.pubnative.lite.sdk.utils.AdTracker;
import net.pubnative.lite.sdk.utils.Logger;
import net.pubnative.lite.sdk.views.CloseableContainer;
import net.pubnative.lite.sdk.vpaid.AdCloseButtonListener;
import net.pubnative.lite.sdk.vpaid.CloseButtonListener;
import net.pubnative.lite.sdk.vpaid.PlayerInfo;
import net.pubnative.lite.sdk.vpaid.VastActivityInteractor;
import net.pubnative.lite.sdk.vpaid.VideoAd;
import net.pubnative.lite.sdk.vpaid.VideoAdCacheItem;
import net.pubnative.lite.sdk.vpaid.VideoAdListener;
import net.pubnative.lite.sdk.vpaid.VideoAdView;

public class VastInterstitialActivity extends HyBidInterstitialActivity implements AdPresenter.ImpressionListener, AdCloseButtonListener {
    private static final String TAG = VastInterstitialActivity.class.getSimpleName();
    private boolean mReady = false;

    private VideoAdView mVideoPlayer;

    private boolean mHasEndCard = false;

    VastActivityInteractor vastActivityInteractor;
    private AdTracker mCustomCTATracker;
    private AdTracker mCustomCTAEndcardTracker;

    @SuppressLint("SourceLockedOrientationActivity")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        }
        if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        }
        setIsVast(true);

        super.onCreate(savedInstanceState);

        vastActivityInteractor = VastActivityInteractor.getInstance();
        vastActivityInteractor.activityStarted();
        initiateCustomCTAAdTrackers();

        try {
            hideInterstitialCloseButton();

            if (getAd() != null) {
                int mSkipOffset = getIntent().getIntExtra(EXTRA_SKIP_OFFSET, -1);
                mIsSkippable = mSkipOffset == 0;
                mVideoAd = new VideoAd(this, getAd(), true, true, this, this);
                mVideoAd.useMobileNetworkForCaching(true);
                mVideoAd.bindView(mVideoPlayer);
                mVideoAd.setAdListener(mVideoAdListener);
                mVideoAd.setAdCloseButtonListener(mCloseButtonListener);
                setProgressBarVisible();

                VideoAdCacheItem adCacheItem = HyBid.getVideoAdCache().remove(getZoneId());
                if (adCacheItem != null) {
                    if (adCacheItem.getAdParams() != null) {
                        adCacheItem.getAdParams().setPublisherSkipSeconds(mSkipOffset);
                        if (adCacheItem.getEndCardData() != null && !TextUtils.isEmpty(adCacheItem.getEndCardData().getContent())) {
                            mHasEndCard = AdEndCardManager.isEndCardEnabled(getAd());
                        } else if (getAd().isEndCardEnabled() != null && getAd().isEndCardEnabled() && getAd().isCustomEndCardEnabled() != null && getAd().isCustomEndCardEnabled() && getAd().hasCustomEndCard()) {
                            mHasEndCard = true;
                        }

                        if (adCacheItem.getAdParams().getAdIcon() != null) {
                            setupContentInfo(adCacheItem.getAdParams().getAdIcon());
                        } else {
                            setupContentInfo();
                        }
                    }
                    mVideoAd.setVideoCacheItem(adCacheItem);
                } else {
                    setupContentInfo();
                }

                mVideoPlayer.postDelayed(() -> mVideoAd.load(mIntegrationType), 1000);
            } else {
                if (getBroadcastSender() != null) {
                    Bundle extras = new Bundle();
                    extras.putInt(HyBidInterstitialBroadcastReceiver.VIDEO_PROGRESS, 0);
                    getBroadcastSender().sendBroadcast(HyBidInterstitialBroadcastReceiver.Action.ERROR);
                    getBroadcastSender().sendBroadcast(HyBidInterstitialBroadcastReceiver.Action.VIDEO_ERROR, extras);
                    getBroadcastSender().sendBroadcast(HyBidInterstitialBroadcastReceiver.Action.DISMISS);
                }
                mIsFinishing = true;
                finish();
            }
        } catch (Exception exception) {
            Logger.e(TAG, exception.getMessage());
            if (getBroadcastSender() != null) {
                Bundle extras = new Bundle();
                extras.putInt(HyBidInterstitialBroadcastReceiver.VIDEO_PROGRESS, 0);
                getBroadcastSender().sendBroadcast(HyBidInterstitialBroadcastReceiver.Action.ERROR);
                getBroadcastSender().sendBroadcast(HyBidInterstitialBroadcastReceiver.Action.VIDEO_ERROR, extras);
                getBroadcastSender().sendBroadcast(HyBidInterstitialBroadcastReceiver.Action.DISMISS);
            }
            mIsFinishing = true;
            finish();
        }
    }

    @Override
    public View getAdView() {
        if (getAd() != null) {
            mVideoPlayer = new VideoAdView(this);
            return mVideoPlayer;
        }
        return null;
    }

    @Override
    protected void onDestroy() {
        vastActivityInteractor.activityDestroyed();
        if (mVideoAd != null) {
            mVideoAd.destroy();
            mReady = false;
        }
        super.onDestroy();
    }

    @Override
    protected void onResume() {
        vastActivityInteractor.activityResumed();
        super.onResume();
        resumeAd();
    }

    @Override
    protected void onPause() {
        if (!mIsFinishing) {
            vastActivityInteractor.activityPaused();
            pauseAd();
        }
        super.onPause();
    }

    @Override
    protected boolean shouldShowContentInfo() {
        return true;
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if ((keyCode == KeyEvent.KEYCODE_BACK)) {
            if (mIsBackEnabled) {
                dismiss();
                return true;
            }
        } else {
            return super.onKeyDown(keyCode, event);
        }
        return false;
    }

    @Override
    protected void resumeAd() {
        if (!mIsFeedbackFormOpen) {
            if (mReady) {
                if (mVideoAd.isAdStarted()) {
                    mVideoAd.resume();
                } else {
                    setProgressBarInvisible();
                    mVideoAd.show();
                }
            }

            if (mIsVideoFinished) {
                mVideoAd.resumeEndCardCloseButtonTimer();
            }
        }
    }

    @Override
    protected void pauseAd() {
        if (mReady && mVideoAd.isAdStarted()) {
            mVideoAd.pause();
        }

        if (mIsVideoFinished) {
            mVideoAd.pauseEndCardCloseButtonTimer();
        }
    }

    private final VideoAdListener mVideoAdListener = new VideoAdListener() {
        @Override
        public void onAdLoadSuccess() {
            if (!mReady) {
                mReady = true;
                setProgressBarInvisible();
                mVideoAd.show();
            }
        }

        @Override
        public void onAdLoadFail(PlayerInfo info) {
            setProgressBarInvisible();
            if (getBroadcastSender() != null) {
                Bundle extras = new Bundle();
                extras.putInt(HyBidInterstitialBroadcastReceiver.VIDEO_PROGRESS, 0);
                getBroadcastSender().sendBroadcast(HyBidInterstitialBroadcastReceiver.Action.ERROR);
                getBroadcastSender().sendBroadcast(HyBidInterstitialBroadcastReceiver.Action.VIDEO_ERROR, extras);
                getBroadcastSender().sendBroadcast(HyBidInterstitialBroadcastReceiver.Action.DISMISS);
            }
            mIsFinishing = true;
            finish();
        }

        @Override
        public void onAdClicked() {
            getBroadcastSender().sendBroadcast(HyBidInterstitialBroadcastReceiver.Action.CLICK);
        }

        @Override
        public void onAdDidReachEnd() {
            mReady = false;
            if (!mHasEndCard) {
                new Handler(Looper.getMainLooper()).postDelayed(() -> showInterstitialCloseButton(), 100);

                mIsSkippable = true;
            }
            mIsVideoFinished = true;
            if (getBroadcastSender() != null) {
                getBroadcastSender().sendBroadcast(HyBidInterstitialBroadcastReceiver.Action.VIDEO_FINISH);
            }
        }

        @Override
        public synchronized void onAdSkipped() {
            mIsVideoFinished = true;
            if (getBroadcastSender() != null) {
                getBroadcastSender().sendBroadcast(HyBidInterstitialBroadcastReceiver.Action.VIDEO_SKIP);
            }
        }

        @Override
        public void onAdCustomEndCardFound() {
            mHasEndCard = true;
        }

        @Override
        public void onCustomEndCardShow(String endCardType) {
            if (!mCustomEndCardImpressionTracked) {
                if (getBroadcastSender() != null) {
                    Bundle extras = new Bundle();
                    extras.putString(Reporting.Key.END_CARD_TYPE, endCardType);
                    extras.putString(Reporting.Key.CLICK_SOURCE_TYPE, CLICK_SOURCE_TYPE_END_CARD);
                    getBroadcastSender().sendBroadcast(HyBidInterstitialBroadcastReceiver.Action.CUSTOM_END_CARD_SHOW, extras);
                }
                mCustomEndCardImpressionTracked = true;
            }
        }

        @Override
        public void onCustomEndCardClick(String endCardType) {
            if (!mCustomEndCardClickTracked) {
                if (getBroadcastSender() != null) {
                    Bundle extras = new Bundle();
                    extras.putString(Reporting.Key.END_CARD_TYPE, endCardType);
                    extras.putString(Reporting.EventType.CLICK, endCardType);
                    extras.putString(Reporting.Key.CLICK_SOURCE_TYPE, CLICK_SOURCE_TYPE_END_CARD);
                    getBroadcastSender().sendBroadcast(HyBidInterstitialBroadcastReceiver.Action.CUSTOM_END_CARD_CLICK, extras);
                }
                mCustomEndCardClickTracked = true;
            }
        }

        @Override
        public void onDefaultEndCardShow(String endCardType) {
            if (!mDefaultEndCardImpressionTracked) {
                if (getBroadcastSender() != null) {
                    Bundle extras = new Bundle();
                    extras.putString(Reporting.Key.END_CARD_TYPE, endCardType);
                    extras.putString(Reporting.Key.CLICK_SOURCE_TYPE, CLICK_SOURCE_TYPE_END_CARD);
                    getBroadcastSender().sendBroadcast(HyBidInterstitialBroadcastReceiver.Action.DEFAULT_END_CARD_SHOW, extras);
                }
                mDefaultEndCardImpressionTracked = true;
            }
        }

        @Override
        public void onDefaultEndCardClick(String endCardType) {
            if (!mDefaultEndCardClickTracked) {
                if (getBroadcastSender() != null) {
                    Bundle extras = new Bundle();
                    extras.putString(Reporting.Key.END_CARD_TYPE, endCardType);
                    extras.putString(Reporting.Key.CLICK_SOURCE_TYPE, CLICK_SOURCE_TYPE_END_CARD);
                    getBroadcastSender().sendBroadcast(HyBidInterstitialBroadcastReceiver.Action.DEFAULT_END_CARD_CLICK, extras);
                }
                mDefaultEndCardClickTracked = true;
            }

        }

        @Override
        public synchronized void onEndCardLoadSuccess(Boolean isCustomEndCard) {
            if (isCustomEndCard && mLoadCustomEndCardTracked) return;
            if (!isCustomEndCard && mLoadDefaultEndCardTracked) return;
            if (getBroadcastSender() != null) {
                if (isCustomEndCard) {
                    hideContentInfo();
                    mLoadCustomEndCardTracked = true;
                } else mLoadDefaultEndCardTracked = true;
                Bundle extras = new Bundle();
                extras.putBoolean(Reporting.Key.IS_CUSTOM_END_CARD, isCustomEndCard);
                getBroadcastSender().sendBroadcast(HyBidInterstitialBroadcastReceiver.Action.END_CARD_LOAD_SUCCESS, extras);
            }
        }

        @Override
        public void onEndCardLoadFail(Boolean isCustomEndCard) {
            if (!mLoadEndCardFailTracked) {
                if (getBroadcastSender() != null) {
                    Bundle extras = new Bundle();
                    extras.putBoolean(Reporting.Key.IS_CUSTOM_END_CARD, isCustomEndCard);
                    getBroadcastSender().sendBroadcast(HyBidInterstitialBroadcastReceiver.Action.END_CARD_LOAD_FAILURE, extras);
                }
                mLoadEndCardFailTracked = true;
            }
        }

        @Override
        public void onCustomCTACLick(boolean isEndcardVisible) {
            String eventType = (isEndcardVisible) ? Reporting.EventType.CUSTOM_CTA_ENDCARD_CLICK : Reporting.EventType.CUSTOM_CTA_CLICK;
            if (mCustomCTAClickTrackedEvents.contains(eventType)) return;

            ReportingEvent reportingEvent = new ReportingEvent();
            reportingEvent.setEventType(eventType);
            reportingEvent.setAdFormat(Reporting.AdFormat.FULLSCREEN);
            reportingEvent.setCreativeType(Reporting.CreativeType.VIDEO);
            reportingEvent.setPlatform(Reporting.Platform.ANDROID);
            reportingEvent.setSdkVersion(HyBid.getSDKVersionInfo(IntegrationType.STANDALONE));
            if (getAd() != null) {
                reportingEvent.setImpId(getAd().getSessionId());
                reportingEvent.setCampaignId(getAd().getCampaignId());
                reportingEvent.setConfigId(getAd().getConfigId());
            }
            reportingEvent.setTimestamp(System.currentTimeMillis());
            if (HyBid.getReportingController() != null) {
                HyBid.getReportingController().reportEvent(reportingEvent);
            }
            if (eventType.equals(Reporting.EventType.CUSTOM_CTA_ENDCARD_CLICK)) {
                if (mCustomCTAEndcardTracker != null) {
                    mCustomCTAEndcardTracker.trackClick();
                }
            } else {
                if (mCustomCTATracker != null) {
                    mCustomCTATracker.trackClick();
                }
            }
            mCustomCTAClickTrackedEvents.add(eventType);
        }

        @Override
        public void onCustomCTAShow() {
            if (mCustomCTAImpressionTracked) return;
            ReportingEvent reportingEvent = new ReportingEvent();
            reportingEvent.setEventType(Reporting.EventType.CUSTOM_CTA_SHOW);
            reportingEvent.setAdFormat(Reporting.AdFormat.FULLSCREEN);
            reportingEvent.setCreativeType(Reporting.CreativeType.VIDEO);
            reportingEvent.setPlatform(Reporting.Platform.ANDROID);
            reportingEvent.setSdkVersion(HyBid.getSDKVersionInfo(IntegrationType.STANDALONE));
            if (getAd() != null) {
                reportingEvent.setImpId(getAd().getSessionId());
                reportingEvent.setCampaignId(getAd().getCampaignId());
                reportingEvent.setConfigId(getAd().getConfigId());
            }
            reportingEvent.setTimestamp(System.currentTimeMillis());
            if (HyBid.getReportingController() != null) {
                HyBid.getReportingController().reportEvent(reportingEvent);
            }
            if (mCustomCTATracker != null) {
                mCustomCTATracker.trackImpression();
            }
            mCustomCTAImpressionTracked = true;
        }

        @Override
        public void onCustomCTALoadFail() {
            Logger.e("onCustomCTALoadFail", "CTA Failed to load");
        }

        @Override
        public void onAdDismissed() {
            onAdDismissed(-1);
        }

        @Override
        public void onAdDismissed(int progressPercentage) {
            dismissVideo(mIsSkippable ? 100 : progressPercentage);
            dismiss();
        }

        @Override
        public void onAdStarted() {
            if (getBroadcastSender() != null) {
                getBroadcastSender().sendBroadcast(HyBidInterstitialBroadcastReceiver.Action.VIDEO_START);
            }
        }

        @Override
        public void onEndCardSkipped(Boolean isCustom) {
            if (isCustom && mCustomEndCardSkipTracked) return;
            if (!isCustom && mDefaultEndCardSkipTracked) return;

            ReportingEvent reportingEvent = new ReportingEvent();
            reportingEvent.setTimestamp(System.currentTimeMillis());
            if (!isCustom) {
                reportingEvent.setEventType(Reporting.EventType.DEFAULT_ENDCARD_SKIP);
                reportingEvent.setCustomString(Reporting.Key.END_CARD_TYPE, Reporting.Key.END_CARD_TYPE_DEFAULT);
                mDefaultEndCardSkipTracked = true;
            }

//            else {
//                reportingEvent.setEventType(Reporting.EventType.CUSTOM_END_CARD_SKIP);
//                reportingEvent.setCustomString(Reporting.Key.END_CARD_TYPE,  Reporting.Key.END_CARD_TYPE_CUSTOM);
//                mCustomEndCardSkipTracked = true;
//            }
            if (HyBid.getReportingController() != null) {
                HyBid.getReportingController().reportEvent(reportingEvent);
            }
        }

        @Override
        public void onEndCardClosed(Boolean isCustom) {
            if (isCustom && mCustomEndCardCloseTracked) return;
            if (!isCustom && mDefaultEndCardCloseTracked) return;

            ReportingEvent reportingEvent = new ReportingEvent();
            reportingEvent.setTimestamp(System.currentTimeMillis());
            if (!isCustom) {
                reportingEvent.setEventType(Reporting.EventType.DEFAULT_ENDCARD_CLOSE);
                reportingEvent.setCustomString(Reporting.Key.END_CARD_TYPE, Reporting.Key.END_CARD_TYPE_DEFAULT);
                mDefaultEndCardCloseTracked = true;
            } else {
                reportingEvent.setEventType(Reporting.EventType.CUSTOM_ENDCARD_CLOSE);
                reportingEvent.setCustomString(Reporting.Key.END_CARD_TYPE, Reporting.Key.END_CARD_TYPE_CUSTOM);
                mCustomEndCardCloseTracked = true;
            }
            if (HyBid.getReportingController() != null) {
                HyBid.getReportingController().reportEvent(reportingEvent);
            }
        }
    };

    private final CloseButtonListener mCloseButtonListener = () -> {
        mIsVideoFinished = true;
        mIsSkippable = true;
        mIsBackEnabled = true;
    };

    private void dismissVideo(int progressPercentage) {
        if (getBroadcastSender() != null) {
            Bundle extras = new Bundle();
            extras.putInt(HyBidInterstitialBroadcastReceiver.VIDEO_PROGRESS, progressPercentage);
            getBroadcastSender().sendBroadcast(HyBidInterstitialBroadcastReceiver.Action.VIDEO_DISMISS, extras);
        }
    }

    @Override
    public void onImpression() {
        if (getBroadcastSender() != null) {
            getBroadcastSender().sendBroadcast(HyBidInterstitialBroadcastReceiver.Action.SHOW);
        }
    }

    @Override
    public void showButton() {
        showInterstitialCloseButton();
    }

    @Override
    public void hideButton() {
        CloseableContainer closeableContainer = getCloseableContainer();
        if (closeableContainer != null) {
            closeableContainer.setCloseVisible(true);
        }
    }

    private void initiateCustomCTAAdTrackers() {
        if (getAd() != null) {
            mCustomCTATracker = new AdTracker(getAd().getBeacons(Ad.Beacon.CUSTOM_CTA_SHOW), getAd().getBeacons(Ad.Beacon.CUSTOM_CTA_CLICK), false);
            mCustomCTAEndcardTracker = new AdTracker(null, getAd().getBeacons(Ad.Beacon.CUSTOM_CTA_ENDCARD_CLICK), false);
        }
    }
}