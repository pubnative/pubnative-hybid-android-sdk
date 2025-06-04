// HyBid SDK License
//
// https://github.com/pubnative/pubnative-hybid-android-sdk/blob/main/LICENSE
//
package net.pubnative.lite.sdk.rewarded.viewModel;

import static net.pubnative.lite.sdk.analytics.Reporting.Key.CLICK_SOURCE_TYPE_END_CARD;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;
import android.view.View;

import net.pubnative.lite.sdk.HyBid;
import net.pubnative.lite.sdk.analytics.Reporting;
import net.pubnative.lite.sdk.models.Ad;
import net.pubnative.lite.sdk.models.AuxiliaryAdEventType;
import net.pubnative.lite.sdk.presenter.AdPresenter;
import net.pubnative.lite.sdk.rewarded.HyBidRewardedBroadcastReceiver;
import net.pubnative.lite.sdk.rewarded.RewardedActivityInteractor;
import net.pubnative.lite.sdk.utils.AdEndCardManager;
import net.pubnative.lite.sdk.utils.AdTracker;
import net.pubnative.lite.sdk.utils.Logger;
import net.pubnative.lite.sdk.vpaid.AdCloseButtonListener;
import net.pubnative.lite.sdk.vpaid.CloseButtonListener;
import net.pubnative.lite.sdk.vpaid.PlayerInfo;
import net.pubnative.lite.sdk.vpaid.VideoAd;
import net.pubnative.lite.sdk.vpaid.VideoAdCacheItem;
import net.pubnative.lite.sdk.vpaid.VideoAdListener;
import net.pubnative.lite.sdk.vpaid.VideoAdView;

import java.util.ArrayList;
import java.util.List;

public class VastRewardedViewModel extends RewardedViewModel implements AdPresenter.ImpressionListener, AdCloseButtonListener {

    private static final String TAG = VastRewardedViewModel.class.getSimpleName();

    private boolean mReady = false;
    private boolean mHasEndCard = false;
    private boolean mIsVideoFinished = false;
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

    private VideoAdView mVideoPlayer;
    private AdTracker mAdEventTracker;
    private AdTracker mCustomCTATracker;
    private AdTracker mCustomCTAEndCardTracker;
    private VideoAdCacheItem mAdCacheItem;
    private VideoAd mVideoAd;

    /*
     * This flag is used for some edge case :when user put app in background directly when VastRewardedActivity is visible for the user ,It causes
     * app to view black screen instead of ad because surface is lost when app is in background.
     * */
    private boolean mIsAdPausedBeforeRender = false;

    public VastRewardedViewModel(Context context, String zoneId, String integrationType, int skipOffset, long broadcastId, RewardedActivityInteractor listener) {
        super(context, zoneId, integrationType, skipOffset, broadcastId, listener);
        initiateCustomCTAAdTrackers();
        initiateEventTrackers();
        processRewardedAd();
        listener.setContentLayout();
    }

    private void initiateCustomCTAAdTrackers() {
        if (mAd != null) {
            mCustomCTATracker = new AdTracker(mAd.getBeacons(Ad.Beacon.CUSTOM_CTA_SHOW), mAd.getBeacons(Ad.Beacon.CUSTOM_CTA_CLICK), false);
            mCustomCTAEndCardTracker = new AdTracker(null, mAd.getBeacons(Ad.Beacon.CUSTOM_CTA_ENDCARD_CLICK), false);
        }
    }

    private void initiateEventTrackers() {
        if (mAd != null) {
            mAdEventTracker = new AdTracker(null, null, null, mAd.getBeacons(Ad.Beacon.COMPANION_AD_EVENT), mAd.getBeacons(Ad.Beacon.CUSTOM_ENDCARD_EVENT));
        }
    }

    public void renderVastAd() {
        try {
            if (mAd != null) {
                mIsSkippable = mSkipOffset == 0;
                if (mVideoAd == null) {
                    mVideoAd = new VideoAd(mContext, mAd, true, true, this, this);
                    mVideoAd.useMobileNetworkForCaching(true);
                }
                mVideoAd.bindView(mVideoPlayer);
                mVideoAd.setRewarded(true);
                mVideoAd.setAdListener(mVideoAdListener);
                mVideoAd.setAdCloseButtonListener(mCloseButtonListener);
                mListener.showProgressBar();
                mAdCacheItem = HyBid.getVideoAdCache().remove(mZoneId);
                if (mAdCacheItem != null) {
                    if (mAdCacheItem.getAdParams() != null) {
                        mAdCacheItem.getAdParams().setPublisherSkipSeconds(mSkipOffset);
                        if (mAdCacheItem.getEndCardData() != null && !TextUtils.isEmpty(mAdCacheItem.getEndCardData().getContent())) {
                            mHasEndCard = AdEndCardManager.isEndCardEnabled(mAd);
                        } else if (mAd.isEndCardEnabled() != null && mAd.isEndCardEnabled() && mAd.isCustomEndCardEnabled() != null && mAd.isCustomEndCardEnabled() && mAd.hasCustomEndCard()) {
                            mHasEndCard = true;
                        }

                        if (mAdCacheItem.getAdParams().getAdIcon() != null) {
                            setupContentInfo(mAdCacheItem.getAdParams().getAdIcon());
                        } else {
                            setupContentInfo(null);
                        }
                    }
                    mVideoAd.setVideoCacheItem(mAdCacheItem);
                } else {
                    setupContentInfo(null);
                }
                mVideoPlayer.postDelayed(() -> mVideoAd.load(mIntegrationType), 1000);
            } else {
                Bundle extras = new Bundle();
                extras.putInt(HyBidRewardedBroadcastReceiver.VIDEO_PROGRESS, 0);
                sendBroadcast(HyBidRewardedBroadcastReceiver.Action.ERROR);
                sendBroadcast(HyBidRewardedBroadcastReceiver.Action.VIDEO_ERROR, extras);
                sendBroadcast(HyBidRewardedBroadcastReceiver.Action.CLOSE);
                mListener.finishActivity();
            }
        } catch (Exception exception) {
            Logger.e(TAG, exception.getMessage());
            Bundle extras = new Bundle();
            extras.putInt(HyBidRewardedBroadcastReceiver.VIDEO_PROGRESS, 0);
            sendBroadcast(HyBidRewardedBroadcastReceiver.Action.ERROR);
            sendBroadcast(HyBidRewardedBroadcastReceiver.Action.VIDEO_ERROR, extras);
            sendBroadcast(HyBidRewardedBroadcastReceiver.Action.CLOSE);
            mListener.finishActivity();
        }
    }

    private final VideoAdListener mVideoAdListener = new VideoAdListener() {
        @Override
        public void onAdLoadSuccess() {
            if (!mReady) {
                mReady = true;
                mListener.hideProgressBar();
                if (!mIsAdPausedBeforeRender)
                    mVideoAd.show();
            }
        }

        @Override
        public void onAdLoadFail(PlayerInfo info) {
            mListener.hideProgressBar();
            Bundle extras = new Bundle();
            extras.putInt(HyBidRewardedBroadcastReceiver.VIDEO_PROGRESS, 0);
            sendBroadcast(HyBidRewardedBroadcastReceiver.Action.ERROR);
            sendBroadcast(HyBidRewardedBroadcastReceiver.Action.VIDEO_ERROR, extras);
            sendBroadcast(HyBidRewardedBroadcastReceiver.Action.CLOSE);
            mListener.finishActivity();
        }

        @Override
        public void onAdClicked() {
            sendBroadcast(HyBidRewardedBroadcastReceiver.Action.CLICK);
        }

        @Override
        public void onAdDidReachEnd() {
            mReady = false;
            mIsVideoFinished = true;
            if (!mHasEndCard) {
                new Handler(Looper.getMainLooper()).postDelayed(() -> {
                    mIsSkippable = true;
                    mListener.showRewardedCloseButton(mCloseListener);
                }, 100);
            }
            sendBroadcast(HyBidRewardedBroadcastReceiver.Action.VIDEO_FINISH);
        }

        @Override
        public synchronized void onAdSkipped() {
            mIsVideoFinished = true;
            sendBroadcast(HyBidRewardedBroadcastReceiver.Action.VIDEO_SKIP);
        }

        @Override
        public void onAdCustomEndCardFound() {
            mHasEndCard = true;
        }

        @Override
        public void onCustomEndCardShow(String endCardType) {
            if (!mCustomEndCardImpressionTracked) {
                Bundle extras = new Bundle();
                extras.putString(Reporting.Key.END_CARD_TYPE, endCardType);
                extras.putString(Reporting.Key.CLICK_SOURCE_TYPE, CLICK_SOURCE_TYPE_END_CARD);
                sendBroadcast(HyBidRewardedBroadcastReceiver.Action.CUSTOM_END_CARD_SHOW, extras);
                mCustomEndCardImpressionTracked = true;
            }
        }

        @Override
        public void onCustomEndCardClick(String endCardType) {
            if (!mCustomEndCardClickTracked) {
                Bundle extras = new Bundle();
                extras.putString(Reporting.Key.END_CARD_TYPE, endCardType);
                extras.putString(Reporting.EventType.CLICK, endCardType);
                extras.putString(Reporting.Key.CLICK_SOURCE_TYPE, CLICK_SOURCE_TYPE_END_CARD);
                sendBroadcast(HyBidRewardedBroadcastReceiver.Action.CUSTOM_END_CARD_CLICK, extras);
                mCustomEndCardClickTracked = true;
            }
        }

        @Override
        public void onDefaultEndCardShow(String endCardType) {
            if (!mDefaultEndCardImpressionTracked) {
                Bundle extras = new Bundle();
                extras.putString(Reporting.Key.END_CARD_TYPE, endCardType);
                extras.putString(Reporting.Key.CLICK_SOURCE_TYPE, CLICK_SOURCE_TYPE_END_CARD);
                sendBroadcast(HyBidRewardedBroadcastReceiver.Action.DEFAULT_END_CARD_SHOW, extras);
                mDefaultEndCardImpressionTracked = true;
            }
        }

        @Override
        public void onDefaultEndCardClick(String endCardType) {
            if (!mDefaultEndCardClickTracked) {
                Bundle extras = new Bundle();
                extras.putString(Reporting.Key.END_CARD_TYPE, endCardType);
                extras.putString(Reporting.Key.CLICK_SOURCE_TYPE, CLICK_SOURCE_TYPE_END_CARD);
                sendBroadcast(HyBidRewardedBroadcastReceiver.Action.DEFAULT_END_CARD_CLICK, extras);
                mDefaultEndCardClickTracked = true;
            }
        }

        @Override
        public synchronized void onEndCardLoadSuccess(Boolean isCustomEndCard) {
            if (isCustomEndCard && mLoadCustomEndCardTracked) return;
            if (!isCustomEndCard && mLoadDefaultEndCardTracked) return;
            if (isCustomEndCard) {
                hideContentInfo();
                mLoadCustomEndCardTracked = true;
            } else {
                mLoadDefaultEndCardTracked = true;
            }
            Bundle extras = new Bundle();
            extras.putBoolean(Reporting.Key.IS_CUSTOM_END_CARD, isCustomEndCard);
            sendBroadcast(HyBidRewardedBroadcastReceiver.Action.END_CARD_LOAD_SUCCESS, extras);
        }

        @Override
        public void onEndCardLoadFail(Boolean isCustomEndCard) {
            if (!mLoadEndCardFailTracked) {
                Bundle extras = new Bundle();
                extras.putBoolean(Reporting.Key.IS_CUSTOM_END_CARD, isCustomEndCard);
                sendBroadcast(HyBidRewardedBroadcastReceiver.Action.END_CARD_LOAD_FAILURE, extras);
                mLoadEndCardFailTracked = true;
            }
        }

        @Override
        public void onCustomCTACLick(boolean isEndcardVisible) {
            String eventType = (isEndcardVisible) ? Reporting.EventType.CUSTOM_CTA_ENDCARD_CLICK : Reporting.EventType.CUSTOM_CTA_CLICK;
            if (mCustomCTAClickTrackedEvents.contains(eventType)) return;
            invokeOnCustomCTAClick(eventType, mAd, Reporting.AdFormat.FULLSCREEN);
            if (eventType.equals(Reporting.EventType.CUSTOM_CTA_ENDCARD_CLICK)) {
                if (mCustomCTAEndCardTracker != null) {
                    mCustomCTAEndCardTracker.trackClick();
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
            invokeOnCustomCTAShow(mAd, Reporting.AdFormat.FULLSCREEN);
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
            sendBroadcast(HyBidRewardedBroadcastReceiver.Action.VIDEO_START);
        }

        @Override
        public void onEndCardSkipped(Boolean isCustom) {
            if (isCustom && mCustomEndCardSkipTracked) return;
            if (!isCustom && mDefaultEndCardSkipTracked) return;
            if (!isCustom) {
                mDefaultEndCardSkipTracked = true;
                mAdEventTracker.trackCompanionAdEvent(AuxiliaryAdEventType.SKIP, null);
            } else {
                mAdEventTracker.trackCustomEndcardEvent(AuxiliaryAdEventType.SKIP, null);
            }
            invokeOnEndCardSkipped(mDefaultEndCardSkipTracked);
        }

        @Override
        public void onEndCardClosed(Boolean isCustom) {
            if (isCustom && mCustomEndCardCloseTracked) return;
            if (!isCustom && mDefaultEndCardCloseTracked) return;

            if (!isCustom) {
                mDefaultEndCardCloseTracked = true;
                mAdEventTracker.trackCompanionAdEvent(AuxiliaryAdEventType.CLOSE, null);
            } else {
                mCustomEndCardCloseTracked = true;
                mAdEventTracker.trackCustomEndcardEvent(AuxiliaryAdEventType.CLOSE, null);
            }

            invokeOnEndCardClosed(mDefaultEndCardCloseTracked);
        }
    };

    private void dismissVideo(int progressPercentage) {
        Bundle extras = new Bundle();
        extras.putInt(HyBidRewardedBroadcastReceiver.VIDEO_PROGRESS, progressPercentage);
        sendBroadcast(HyBidRewardedBroadcastReceiver.Action.VIDEO_DISMISS, extras);
    }

    private final CloseButtonListener mCloseButtonListener = () -> {
        mIsVideoFinished = true;
        mIsSkippable = true;
    };

    @Override
    public void onImpression() {
        sendBroadcast(HyBidRewardedBroadcastReceiver.Action.OPEN);
    }

    @Override
    public void showButton() {
        if (!mHasEndCard)
            mIsSkippable = true;
        mListener.showRewardedCloseButton(mCloseListener);
    }

    @Override
    public boolean shouldShowContentInfo() {
        return true;
    }

    @Override
    public void closeButtonClicked() {
        if (mVideoAd == null) return;
        if (!mIsVideoFinished) {
            mVideoAd.skip();
        } else {
            mVideoAd.closeVideo();
            sendBroadcast(HyBidRewardedBroadcastReceiver.Action.CLOSE);
            mListener.finishActivity();
        }
    }

    @Override
    public View getAdView() {
        if (mAd != null) {
            mVideoPlayer = new VideoAdView(mContext);
            return mVideoPlayer;
        }
        return null;
    }

    @Override
    public void pauseAd() {
        if (mVideoAd != null) {
            if (!mReady) {
                mIsAdPausedBeforeRender = true;
            }
            if (mReady && mVideoAd.isAdStarted()) {
                mVideoAd.pause();
            }
            if (mIsVideoFinished) {
                mVideoAd.pauseEndCardCloseButtonTimer();
            }
        }
    }

    @Override
    public void resumeAd() {
        if (!isFeedbackFormOpen() && mVideoAd != null) {
            if (mReady) {
                if (mVideoAd.isAdStarted()) {
                    mVideoAd.resume();
                } else {
                    mListener.hideProgressBar();
                    mIsAdPausedBeforeRender = false;
                    mVideoAd.show();
                }
            }
            if (mIsVideoFinished) {
                mVideoAd.resumeEndCardCloseButtonTimer();
            }
        }
    }

    @Override
    public void destroyAd() {
        if (mVideoAd != null) {
            mVideoAd.destroy();
            mIsAdPausedBeforeRender = false;
            mReady = false;
        }
    }
}