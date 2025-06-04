// HyBid SDK License
//
// https://github.com/pubnative/pubnative-hybid-android-sdk/blob/main/LICENSE
//
package net.pubnative.lite.sdk.banner.presenter;

import android.content.Context;
import android.graphics.Color;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import net.pubnative.lite.sdk.HyBid;
import net.pubnative.lite.sdk.VideoListener;
import net.pubnative.lite.sdk.analytics.Reporting;
import net.pubnative.lite.sdk.analytics.ReportingController;
import net.pubnative.lite.sdk.analytics.ReportingEvent;
import net.pubnative.lite.sdk.contentinfo.AdFeedbackFormHelper;
import net.pubnative.lite.sdk.contentinfo.listeners.AdFeedbackLoadListener;
import net.pubnative.lite.sdk.models.Ad;
import net.pubnative.lite.sdk.models.AdSize;
import net.pubnative.lite.sdk.models.AuxiliaryAdEventType;
import net.pubnative.lite.sdk.models.ContentInfo;
import net.pubnative.lite.sdk.models.ContentInfoIconXPosition;
import net.pubnative.lite.sdk.models.ContentInfoIconYPosition;
import net.pubnative.lite.sdk.models.ImpressionTrackingMethod;
import net.pubnative.lite.sdk.models.IntegrationType;
import net.pubnative.lite.sdk.models.PositionX;
import net.pubnative.lite.sdk.models.PositionY;
import net.pubnative.lite.sdk.mraid.MRAIDViewListener;
import net.pubnative.lite.sdk.presenter.AdPresenter;
import net.pubnative.lite.sdk.utils.AdTracker;
import net.pubnative.lite.sdk.utils.CheckUtils;
import net.pubnative.lite.sdk.utils.HybidConsumer;
import net.pubnative.lite.sdk.utils.Logger;
import net.pubnative.lite.sdk.utils.URLValidator;
import net.pubnative.lite.sdk.views.PNAPIContentInfoView;
import net.pubnative.lite.sdk.visibility.ImpressionManager;
import net.pubnative.lite.sdk.visibility.ImpressionTracker;
import net.pubnative.lite.sdk.vpaid.CloseButtonListener;
import net.pubnative.lite.sdk.vpaid.PlayerInfo;
import net.pubnative.lite.sdk.vpaid.VideoAd;
import net.pubnative.lite.sdk.vpaid.VideoAdCacheItem;
import net.pubnative.lite.sdk.vpaid.VideoAdListener;
import net.pubnative.lite.sdk.vpaid.VideoAdView;
import net.pubnative.lite.sdk.vpaid.VideoVisibilityListener;
import net.pubnative.lite.sdk.vpaid.VideoVisibilityManager;
import net.pubnative.lite.sdk.vpaid.helpers.EventTracker;
import net.pubnative.lite.sdk.vpaid.models.vast.Icon;
import net.pubnative.lite.sdk.vpaid.utils.Utils;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class VastAdPresenter implements AdPresenter, ImpressionTracker.Listener, PNAPIContentInfoView.ContentInfoListener, VideoVisibilityListener {
    private static final String TAG = VastAdPresenter.class.getSimpleName();
    private final Context mContext;
    private final Ad mAd;
    private final ImpressionTrackingMethod mTrackingMethod;
    private VideoVisibilityManager videoVisibilityManager;

    private Listener mListener;
    private ImpressionListener mImpressionListener;
    private VideoListener mVideoListener;
    private Icon mVastIcon;
    private boolean mIsDestroyed;
    private boolean mLoaded = false;
    private boolean wasClicked = false;
    private AdSize mAdSize;

    private VideoAdView mVideoPlayer;
    private VideoAd mVideoAd;
    private View mContentInfo;

    private boolean isFeedbackFormVisible = false;

    private Boolean mDefaultEndCardClickTracked = false;
    private Boolean mCustomEndCardClickTracked = false;
    private List<String> mCustomCTAClickTrackedEvents = new ArrayList<>();
    private Boolean mDefaultEndCardImpressionTracked = false;
    private Boolean mCustomEndCardImpressionTracked = false;
    private Boolean mLoadDefaultEndCardTracked = false;
    private Boolean mLoadCustomEndCardTracked = false;
    private Boolean mCustomCTAImpressionTracked = false;
    private Boolean mDefaultEndCardSkipTracked = false;
    private Boolean mCustomEndCardSkipTracked = false;
    private Boolean mDefaultEndCardCloseTracked = false;
    private Boolean mCustomEndCardCloseTracked = false;
    private IntegrationType mIntegrationType;

    private AdTracker mAdTracker;
    private AdTracker mCustomEndcardTracker;
    private AdTracker mAdEventTracker;
    private AdTracker mCustomCTATracker;
    private AdTracker mCustomCTAEndcardTracker;
    private ReportingController mReportingController;

    public VastAdPresenter(Context context, Ad ad, AdSize adSize, ImpressionTrackingMethod trackingMethod, IntegrationType integrationType) {
        mContext = context;
        mAdSize = adSize;
        mAd = ad;

        mReportingController = HyBid.getReportingController();

        ImpressionTrackingMethod remoteConfigTrackingMethod = null;
        if (ad != null && ad.getImpressionTrackingMethod() != null && ImpressionTrackingMethod.fromString(ad.getImpressionTrackingMethod()) != null) {
            remoteConfigTrackingMethod = ImpressionTrackingMethod.fromString(ad.getImpressionTrackingMethod());
        }

        if (remoteConfigTrackingMethod != null) {
            mTrackingMethod = remoteConfigTrackingMethod;
        } else if (trackingMethod != null) {
            mTrackingMethod = trackingMethod;
        } else {
            mTrackingMethod = ImpressionTrackingMethod.AD_VIEWABLE;
        }

        videoVisibilityManager = VideoVisibilityManager.getInstance();
        videoVisibilityManager.addCallback(this);
        mIntegrationType = integrationType;
        initiateAdTrackers();
        initiateCustomCTAAdTrackers();
        initiateEventTrackers();
    }

    @Override
    public void setListener(Listener listener) {
        mListener = listener;
    }

    @Override
    public void setImpressionListener(ImpressionListener listener) {
        mImpressionListener = listener;
    }

    @Override
    public void setVideoListener(VideoListener listener) {
        mVideoListener = listener;
    }

    @Override
    public void setMRaidListener(MRAIDViewListener listener) {

    }

    @Override
    public Ad getAd() {
        return mAd;
    }

    @Override
    public void load() {
        if (!CheckUtils.NoThrow.checkArgument(!mIsDestroyed, "VastMRectPresenter is destroyed")) {
            return;
        }

        try {
            if (mAd != null) {
                mVideoAd = new VideoAd(mContext, mAd, false, false, mVideoImpressionListener);
                mVideoPlayer = new VideoAdView(mContext);
                mVideoAd.bindView(mVideoPlayer);
                mVideoAd.setAdListener(mVideoAdListener);
                mVideoAd.setAdCloseButtonListener(mAdCloseButtonListener);
                mDefaultEndCardClickTracked = false;
                mCustomEndCardClickTracked = false;
                mDefaultEndCardImpressionTracked = false;
                mCustomEndCardImpressionTracked = false;
                mLoadDefaultEndCardTracked = false;
                mLoadCustomEndCardTracked = false;
                if (!TextUtils.isEmpty(getAd().getZoneId())) {
                    VideoAdCacheItem adCacheItem = HyBid.getVideoAdCache().remove(getAd().getZoneId());
                    if (adCacheItem != null) {
                        mVideoAd.setVideoCacheItem(adCacheItem);
                        if (adCacheItem.getAdParams() != null && adCacheItem.getAdParams().getAdIcon() != null) {
                            mVastIcon = adCacheItem.getAdParams().getAdIcon();
                        }
                    }
                }
                mVideoAd.load(mIntegrationType);
            }
        } catch (Exception exception) {
            Logger.e(TAG, exception.getMessage());
            if (mListener != null) {
                mListener.onAdError(this);
            }

            if (mVideoListener != null) {
                mVideoListener.onVideoError(0);
            }
        }
    }

    @Override
    public void destroy() {
        if (mVideoAd != null) {
            mVideoAd.destroy();
        }
        videoVisibilityManager.removeCallback(this);
        mListener = null;
        mIsDestroyed = true;
    }

    @Override
    public void startTracking() {
        startTracking(null);
    }

    @Override
    public void startTracking(HybidConsumer<Double> percentConsumer) {
        if (mTrackingMethod == ImpressionTrackingMethod.AD_VIEWABLE) {
            ImpressionManager.startTrackingView(mVideoPlayer, mAdSize, mAd.getImpressionMinVisibleTime(), mAd.getImpressionVisiblePercent()
                    , mNativeTrackerListener, percentConsumer);
        } else {
            if (mVideoAd != null) {
                mVideoAd.show();
            }
        }
    }

    @Override
    public void stopTracking() {
        if (mTrackingMethod == ImpressionTrackingMethod.AD_VIEWABLE) {
            ImpressionManager.stopTrackingView(mVideoPlayer);
        }
        mVideoAd.dismiss();
    }

    @Override
    public void pauseAd() {
        if (mVideoAd != null && mVideoAd.isShowing()) {
            mVideoAd.pause();
        }
    }

    @Override
    public void resumeAd() {
        if (mVideoAd != null && mVideoAd.isShowing() && !isFeedbackFormVisible) {
            mVideoAd.resume();
        }
    }

    @Override
    public JSONObject getPlacementParams() {
        return null;
    }

    private View buildView() {
        FrameLayout container = new FrameLayout(mContext);
        FrameLayout.LayoutParams layoutParams = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        layoutParams.gravity = Gravity.CENTER;

        container.setBackgroundColor(Color.BLACK);

        container.addView(mVideoPlayer, layoutParams);

        setupContentInfo(container);

        return container;
    }

    private void setupContentInfo(ViewGroup container) {
        if (getAd() != null && container != null) {
            ContentInfo contentInfo = Utils.parseContentInfo(mVastIcon);
            mContentInfo = getContentInfo(container.getContext(), getAd(), contentInfo);
            if (mContentInfo != null) {
                if (contentInfo != null) {
                    FrameLayout.LayoutParams layoutParams = (FrameLayout.LayoutParams) mContentInfo.getLayoutParams();
                    int horizontalAlign = Gravity.START;
                    int verticalAlign = Gravity.TOP;

                    if (getAd().getContentInfoIconXPosition() != null) {
                        ContentInfoIconXPosition remoteIconXPosition = getAd().getContentInfoIconXPosition();
                        if (remoteIconXPosition == ContentInfoIconXPosition.RIGHT) {
                            horizontalAlign = Gravity.END;
                        }
                    } else {
                        if (contentInfo.getPositionX() == PositionX.RIGHT) {
                            horizontalAlign = Gravity.END;
                        }
                    }

                    if (getAd().getContentInfoIconYPosition() != null) {
                        ContentInfoIconYPosition remoteIconYPosition = getAd().getContentInfoIconYPosition();
                        if (remoteIconYPosition == ContentInfoIconYPosition.BOTTOM) {
                            verticalAlign = Gravity.BOTTOM;
                        }
                    } else {
                        if (contentInfo.getPositionY() == PositionY.BOTTOM) {
                            verticalAlign = Gravity.BOTTOM;
                        }
                    }

                    layoutParams.gravity = horizontalAlign | verticalAlign;
                    container.addView(mContentInfo, layoutParams);
                } else {
                    container.addView(mContentInfo);
                }
                if (contentInfo != null && contentInfo.getViewTrackers() != null && !contentInfo.getViewTrackers().isEmpty()) {
                    for (String tracker : contentInfo.getViewTrackers()) {
                        EventTracker.post(container.getContext(), tracker, null, true);
                    }
                }
            }
        }
    }

    private View getContentInfo(Context context, Ad ad, ContentInfo contentInfo) {
        return contentInfo == null ? ad.getContentInfoContainer(context, this) : ad.getContentInfoContainer(context, contentInfo, this);
    }

    @Override
    public void onImpression(View visibleView) {

    }

    private final ImpressionTracker.Listener mNativeTrackerListener = new ImpressionTracker.Listener() {
        @Override
        public void onImpression(View visibleView) {
            if (mVideoAd != null) {
                mVideoAd.show();
            }
        }
    };

    private final ImpressionListener mVideoImpressionListener = new ImpressionListener() {
        @Override
        public void onImpression() {
            if (mImpressionListener != null) {
                mImpressionListener.onImpression();
            }
        }
    };

    private final CloseButtonListener mAdCloseButtonListener = new CloseButtonListener() {

        @Override
        public void onCloseButtonVisible() {

        }
    };

    private final VideoAdListener mVideoAdListener = new VideoAdListener() {
        @Override
        public void onAdLoadSuccess() {
            if (mIsDestroyed) {
                return;
            }

            if (!mLoaded) {
                mLoaded = true;
                if (mListener != null) {
                    mListener.onAdLoaded(VastAdPresenter.this, buildView());
                }
            }
        }

        @Override
        public void onAdLoadFail(PlayerInfo info) {
            if (mListener != null) {
                mListener.onAdError(VastAdPresenter.this);
            }
        }

        @Override
        public void onAdClicked() {
            if (mIsDestroyed) {
                return;
            }

            if (mListener != null) {
                mListener.onAdClicked(VastAdPresenter.this);
            }
            wasClicked = true;
        }

        @Override
        public void onAdDidReachEnd() {
            if (mVideoListener != null) {
                mVideoListener.onVideoFinished();
            }
        }

        @Override
        public void onAdSkipped() {
            if (mVideoListener != null) {
                mVideoListener.onVideoSkipped();
            }
        }

        @Override
        public void onAdCustomEndCardFound() {

        }

        @Override
        public void onAdDismissed() {
            onAdDismissed(-1);
        }

        @Override
        public void onAdDismissed(int progressPercentage) {
            hideContentInfo();
            if (mVideoListener != null) {
                mVideoListener.onVideoDismissed(progressPercentage);
            }
        }

        @Override
        public void onAdStarted() {
            if (mVideoListener != null) {
                mVideoListener.onVideoStarted();
            }
        }

        @Override
        public synchronized void onEndCardLoadSuccess(Boolean isCustomEndCard) {
            if (isCustomEndCard && mLoadCustomEndCardTracked) return;

            if (!isCustomEndCard && mLoadDefaultEndCardTracked) return;

            if (!isCustomEndCard) {
                mLoadDefaultEndCardTracked = true;
            } else {
                mLoadCustomEndCardTracked = true;
            }

            if (HyBid.getReportingController() != null && HyBid.isReportingEnabled()) {
                ReportingEvent reportingEvent = new ReportingEvent();
                reportingEvent.setTimestamp(System.currentTimeMillis());
                reportingEvent.setAdFormat(Reporting.AdFormat.BANNER);
                reportingEvent.setPlatform(Reporting.Platform.ANDROID);
                reportingEvent.setSdkVersion(HyBid.getSDKVersionInfo(mIntegrationType));
                if (mAd != null) {
                    reportingEvent.setImpId(mAd.getSessionId());
                    reportingEvent.setCampaignId(mAd.getCampaignId());
                    reportingEvent.setConfigId(mAd.getConfigId());
                }
                if (mLoadDefaultEndCardTracked) {
                    reportingEvent.setEventType(Reporting.EventType.DEFAULT_END_CARD_LOAD_SUCCESS);
                    reportingEvent.setCustomString(Reporting.Key.END_CARD_TYPE, Reporting.Key.END_CARD_TYPE_DEFAULT);
                } else {
                    reportingEvent.setEventType(Reporting.EventType.CUSTOM_END_CARD_LOAD_SUCCESS);
                    reportingEvent.setCustomString(Reporting.Key.END_CARD_TYPE, Reporting.Key.END_CARD_TYPE_CUSTOM);
                }

                HyBid.getReportingController().reportEvent(reportingEvent);
            }
        }

        @Override
        public void onEndCardLoadFail(Boolean isCustomEndCard) {
            if (HyBid.getReportingController() != null && HyBid.isReportingEnabled()) {
                ReportingEvent reportingEvent = new ReportingEvent();
                reportingEvent.setTimestamp(System.currentTimeMillis());
                reportingEvent.setAdFormat(Reporting.AdFormat.BANNER);
                reportingEvent.setPlatform(Reporting.Platform.ANDROID);
                reportingEvent.setSdkVersion(HyBid.getSDKVersionInfo(mIntegrationType));
                if (mAd != null) {
                    reportingEvent.setImpId(mAd.getSessionId());
                    reportingEvent.setCampaignId(mAd.getCampaignId());
                    reportingEvent.setConfigId(mAd.getConfigId());
                }
                if (!isCustomEndCard) {
                    reportingEvent.setEventType(Reporting.EventType.DEFAULT_END_CARD_LOAD_FAILURE);
                    reportingEvent.setCustomString(Reporting.Key.END_CARD_TYPE, Reporting.Key.END_CARD_TYPE_DEFAULT);
                } else {
                    reportingEvent.setEventType(Reporting.EventType.CUSTOM_END_CARD_LOAD_FAILURE);
                    reportingEvent.setCustomString(Reporting.Key.END_CARD_TYPE, Reporting.Key.END_CARD_TYPE_CUSTOM);
                }

                HyBid.getReportingController().reportEvent(reportingEvent);
            }
        }

        @Override
        public void onDefaultEndCardShow(String endCardType) {
            if (!mDefaultEndCardImpressionTracked) {
                reportCompanionView();
                if (HyBid.getReportingController() != null && HyBid.isReportingEnabled()) {
                    ReportingEvent reportingEvent = new ReportingEvent();
                    reportingEvent.setTimestamp(System.currentTimeMillis());
                    reportingEvent.setAdFormat(Reporting.AdFormat.BANNER);
                    reportingEvent.setPlatform(Reporting.Platform.ANDROID);
                    reportingEvent.setSdkVersion(HyBid.getSDKVersionInfo(mIntegrationType));
                    if (mAd != null) {
                        reportingEvent.setImpId(mAd.getSessionId());
                        reportingEvent.setCampaignId(mAd.getCampaignId());
                        reportingEvent.setConfigId(mAd.getConfigId());
                    }
                    reportingEvent.setEventType(Reporting.EventType.DEFAULT_ENDCARD_IMPRESSION);
                    reportingEvent.setCustomString(Reporting.Key.END_CARD_TYPE, endCardType);

                    HyBid.getReportingController().reportEvent(reportingEvent);
                }
                mAdEventTracker.trackCompanionAdEvent(AuxiliaryAdEventType.IMPRESSION, null);
                mDefaultEndCardImpressionTracked = true;
            }
        }

        @Override
        public void onCustomEndCardShow(String endCardType) {
            if (!mCustomEndCardImpressionTracked) {
                hideContentInfo();

                if (HyBid.getReportingController() != null && HyBid.isReportingEnabled()) {
                    ReportingEvent reportingEvent = new ReportingEvent();
                    reportingEvent.setTimestamp(System.currentTimeMillis());
                    reportingEvent.setAdFormat(Reporting.AdFormat.BANNER);
                    reportingEvent.setPlatform(Reporting.Platform.ANDROID);
                    reportingEvent.setSdkVersion(HyBid.getSDKVersionInfo(mIntegrationType));
                    if (mAd != null) {
                        reportingEvent.setImpId(mAd.getSessionId());
                        reportingEvent.setCampaignId(mAd.getCampaignId());
                        reportingEvent.setConfigId(mAd.getConfigId());
                    }
                    reportingEvent.setEventType(Reporting.EventType.CUSTOM_ENDCARD_IMPRESSION);
                    reportingEvent.setCustomString(Reporting.Key.END_CARD_TYPE, endCardType);

                    HyBid.getReportingController().reportEvent(reportingEvent);
                }
                mCustomEndcardTracker.trackImpression();
                mAdEventTracker.trackCustomEndcardEvent(AuxiliaryAdEventType.IMPRESSION, null);
                mCustomEndCardImpressionTracked = true;
            }
        }

        @Override
        public void onDefaultEndCardClick(String endCardType) {
            if (!mDefaultEndCardClickTracked) {

                if (HyBid.getReportingController() != null && HyBid.isReportingEnabled()) {
                    ReportingEvent reportingEvent = new ReportingEvent();
                    reportingEvent.setEventType(Reporting.EventType.DEFAULT_ENDCARD_CLICK);
                    reportingEvent.setTimestamp(System.currentTimeMillis());
                    reportingEvent.setAdFormat(Reporting.AdFormat.BANNER);
                    reportingEvent.setPlatform(Reporting.Platform.ANDROID);
                    reportingEvent.setSdkVersion(HyBid.getSDKVersionInfo(mIntegrationType));
                    if (mAd != null) {
                        reportingEvent.setImpId(mAd.getSessionId());
                        reportingEvent.setCampaignId(mAd.getCampaignId());
                        reportingEvent.setConfigId(mAd.getConfigId());
                    }
                    reportingEvent.setCustomString(Reporting.Key.END_CARD_TYPE, Reporting.Key.END_CARD_TYPE_DEFAULT);

                    HyBid.getReportingController().reportEvent(reportingEvent);
                }
                mAdEventTracker.trackCompanionAdEvent(AuxiliaryAdEventType.CLICK, null);
                mDefaultEndCardClickTracked = true;
            }
        }

        @Override
        public void onCustomEndCardClick(String endCardType) {
            if (!mCustomEndCardClickTracked) {
                if (HyBid.getReportingController() != null && HyBid.isReportingEnabled()) {
                    ReportingEvent reportingEvent = new ReportingEvent();
                    reportingEvent.setEventType(Reporting.EventType.CUSTOM_ENDCARD_CLICK);
                    reportingEvent.setTimestamp(System.currentTimeMillis());
                    reportingEvent.setAdFormat(Reporting.AdFormat.BANNER);
                    reportingEvent.setPlatform(Reporting.Platform.ANDROID);
                    reportingEvent.setSdkVersion(HyBid.getSDKVersionInfo(mIntegrationType));
                    if (mAd != null) {
                        reportingEvent.setImpId(mAd.getSessionId());
                        reportingEvent.setCampaignId(mAd.getCampaignId());
                        reportingEvent.setConfigId(mAd.getConfigId());
                    }
                    reportingEvent.setCustomString(Reporting.Key.END_CARD_TYPE, Reporting.Key.END_CARD_TYPE_CUSTOM);

                    HyBid.getReportingController().reportEvent(reportingEvent);
                }
                if (!wasClicked) {
                    mAdTracker.trackClick();
                }
                mCustomEndcardTracker.trackClick();
                mAdEventTracker.trackCustomEndcardEvent(AuxiliaryAdEventType.CLICK, null);
                mCustomEndCardClickTracked = true;
            }
        }

        @Override
        public void onCustomCTACLick(boolean isEndcardVisible) {
            String eventType = (isEndcardVisible) ? Reporting.EventType.CUSTOM_CTA_ENDCARD_CLICK : Reporting.EventType.CUSTOM_CTA_CLICK;
            if (mCustomCTAClickTrackedEvents.contains(eventType)) return;

            if (HyBid.getReportingController() != null && HyBid.isReportingEnabled()) {
                ReportingEvent reportingEvent = new ReportingEvent();
                reportingEvent.setEventType(eventType);
                reportingEvent.setPlatform(Reporting.Platform.ANDROID);
                reportingEvent.setSdkVersion(HyBid.getSDKVersionInfo(mIntegrationType));
                reportingEvent.setTimestamp(System.currentTimeMillis());
                if (mAd != null) {
                    reportingEvent.setImpId(mAd.getSessionId());
                    reportingEvent.setCampaignId(mAd.getCampaignId());
                    reportingEvent.setConfigId(mAd.getConfigId());
                }

                HyBid.getReportingController().reportEvent(reportingEvent);
            }
            if (eventType.equals(Reporting.EventType.CUSTOM_CTA_ENDCARD_CLICK)) {
                if (mCustomCTAEndcardTracker != null) {
                    mCustomCTAEndcardTracker.trackClick();
                }
            } else {
                if (mCustomCTATracker != null) {
                    mCustomCTATracker.trackImpression();
                }
            }
            mCustomCTAClickTrackedEvents.add(eventType);
        }

        @Override
        public void onCustomCTAShow() {
            if (mCustomCTAImpressionTracked) return;

            if (HyBid.getReportingController() != null && HyBid.isReportingEnabled()) {
                ReportingEvent reportingEvent = new ReportingEvent();
                reportingEvent.setEventType(Reporting.EventType.CUSTOM_CTA_SHOW);
                reportingEvent.setPlatform(Reporting.Platform.ANDROID);
                reportingEvent.setSdkVersion(HyBid.getSDKVersionInfo(mIntegrationType));
                reportingEvent.setTimestamp(System.currentTimeMillis());
                if (mAd != null) {
                    reportingEvent.setImpId(mAd.getSessionId());
                    reportingEvent.setCampaignId(mAd.getCampaignId());
                    reportingEvent.setConfigId(mAd.getConfigId());
                }

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
        public void onEndCardSkipped(Boolean isCustom) {
            if (isCustom && mCustomEndCardSkipTracked)
                return;
            if (!isCustom && mDefaultEndCardSkipTracked)
                return;

            if (!isCustom) {
                mDefaultEndCardSkipTracked = true;
                mAdEventTracker.trackCompanionAdEvent(AuxiliaryAdEventType.SKIP, null);
            } else {
                mAdEventTracker.trackCustomEndcardEvent(AuxiliaryAdEventType.SKIP, null);
            }

            if (HyBid.getReportingController() != null && HyBid.isReportingEnabled()) {
                ReportingEvent reportingEvent = new ReportingEvent();
                reportingEvent.setTimestamp(System.currentTimeMillis());
                if (mDefaultEndCardSkipTracked) {
                    reportingEvent.setEventType(Reporting.EventType.DEFAULT_ENDCARD_SKIP);
                    reportingEvent.setCustomString(Reporting.Key.END_CARD_TYPE, Reporting.Key.END_CARD_TYPE_DEFAULT);
                }

                HyBid.getReportingController().reportEvent(reportingEvent);
            }
        }

        @Override
        public void onEndCardClosed(Boolean isCustom) {
            if (isCustom && mCustomEndCardCloseTracked)
                return;
            if (!isCustom && mDefaultEndCardCloseTracked)
                return;

            if (!isCustom) {
                mDefaultEndCardCloseTracked = true;
                mAdEventTracker.trackCompanionAdEvent(AuxiliaryAdEventType.CLOSE, null);
            } else {
                mCustomEndCardCloseTracked = true;
                mAdEventTracker.trackCustomEndcardEvent(AuxiliaryAdEventType.CLOSE, null);
            }

            if (HyBid.getReportingController() != null && HyBid.isReportingEnabled()) {
                ReportingEvent reportingEvent = new ReportingEvent();
                reportingEvent.setTimestamp(System.currentTimeMillis());
                if (!mDefaultEndCardCloseTracked) {
                    reportingEvent.setEventType(Reporting.EventType.DEFAULT_ENDCARD_CLOSE);
                    reportingEvent.setCustomString(Reporting.Key.END_CARD_TYPE, Reporting.Key.END_CARD_TYPE_DEFAULT);
                } else {
                    reportingEvent.setEventType(Reporting.EventType.CUSTOM_ENDCARD_CLOSE);
                    reportingEvent.setCustomString(Reporting.Key.END_CARD_TYPE, Reporting.Key.END_CARD_TYPE_CUSTOM);
                }

                HyBid.getReportingController().reportEvent(reportingEvent);
            }
        }
    };

    // Content info listener
    @Override
    public void onIconClicked(List<String> clickTrackers) {
        if (clickTrackers != null && !clickTrackers.isEmpty()) {
            for (int i = 0; i < clickTrackers.size(); i++) {
                EventTracker.post(mContext, clickTrackers.get(i), null, false);
            }
        }
        invokeOnContentInfoClick();
    }

    private void invokeOnContentInfoClick() {
        if (mReportingController != null && HyBid.isReportingEnabled()) {
            ReportingEvent reportingEvent = new ReportingEvent();
            reportingEvent.setEventType(Reporting.EventType.CONTENT_INFO_CLICK);
            reportingEvent.setTimestamp(System.currentTimeMillis());
            reportingEvent.setPlatform(Reporting.Platform.ANDROID);
            if (mAd != null) {
                reportingEvent.setImpId(mAd.getSessionId());
                reportingEvent.setCampaignId(mAd.getCampaignId());
                reportingEvent.setConfigId(mAd.getConfigId());
            }
            mReportingController.reportEvent(reportingEvent);
        }
    }

    @Override
    public synchronized void onLinkClicked(String url) {
        if (!isLinkClickRunning) {
            isLinkClickRunning = true;
            AdFeedbackFormHelper adFeedbackFormHelper = new AdFeedbackFormHelper();
            if (URLValidator.isValidURL(url) && mAd != null) {
                adFeedbackFormHelper.showFeedbackForm(mContext, url, mAd, Reporting.AdFormat.BANNER, IntegrationType.STANDALONE, new AdFeedbackLoadListener() {

                    @Override
                    public void onLoad(String url1) {
                    }

                    @Override
                    public void onLoadFinished() {
                        isFeedbackFormVisible = true;
                        isLinkClickRunning = false;
                    }

                    @Override
                    public void onLoadFailed(Throwable error) {
                        Logger.e(TAG, error.getMessage());
                        isLinkClickRunning = false;
                    }

                    @Override
                    public void onFormClosed() {
                        isFeedbackFormVisible = false;
                        isLinkClickRunning = false;
                    }
                });
            } else {
                Logger.e(TAG, "URL is invalid");
                isLinkClickRunning = false;
            }
        }
    }

    private void reportCompanionView() {
        if (HyBid.getReportingController() != null && HyBid.isReportingEnabled()) {
            ReportingEvent reportingEvent = new ReportingEvent();
            reportingEvent.setEventType(Reporting.EventType.COMPANION_VIEW);
            reportingEvent.setAdFormat(Reporting.AdFormat.BANNER);
            reportingEvent.setCreativeType(Reporting.CreativeType.VIDEO);
            reportingEvent.setPlatform(Reporting.Platform.ANDROID);
            reportingEvent.setSdkVersion(HyBid.getSDKVersionInfo(IntegrationType.STANDALONE));
            if (mAd != null) {
                reportingEvent.setImpId(mAd.getSessionId());
                reportingEvent.setCampaignId(mAd.getCampaignId());
                reportingEvent.setConfigId(mAd.getConfigId());
            }
            reportingEvent.setTimestamp(System.currentTimeMillis());

            HyBid.getReportingController().reportEvent(reportingEvent);
        }
    }

    private void initiateAdTrackers() {
        if (mAd != null) {
            mAdTracker = new AdTracker(mAd.getBeacons(Ad.Beacon.IMPRESSION), mAd.getBeacons(Ad.Beacon.CLICK));
            mCustomEndcardTracker = new AdTracker(mAd.getBeacons(Ad.Beacon.CUSTOM_END_CARD_IMPRESSION), mAd.getBeacons(Ad.Beacon.CUSTOM_END_CARD_CLICK));
        }
    }

    private void initiateCustomCTAAdTrackers() {
        if (mAd != null) {
            mCustomCTATracker = new AdTracker(mAd.getBeacons(Ad.Beacon.CUSTOM_CTA_SHOW), mAd.getBeacons(Ad.Beacon.CUSTOM_CTA_CLICK), false);
            mCustomCTAEndcardTracker = new AdTracker(null, mAd.getBeacons(Ad.Beacon.CUSTOM_CTA_ENDCARD_CLICK), false);
        }
    }

    private void initiateEventTrackers() {
        if (mAd != null) {
            mAdEventTracker = new AdTracker(null, null, null, mAd.getBeacons(Ad.Beacon.COMPANION_AD_EVENT), mAd.getBeacons(Ad.Beacon.CUSTOM_ENDCARD_EVENT));
        }
    }

    private void hideContentInfo() {
        if (mContentInfo != null) {
            mContentInfo.setVisibility(View.GONE);
        }
    }

    public boolean isLinkClickRunning = false;
}
