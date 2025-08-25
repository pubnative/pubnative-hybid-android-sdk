// HyBid SDK License
//
// https://github.com/pubnative/pubnative-hybid-android-sdk/blob/main/LICENSE
//
package net.pubnative.lite.sdk.rewarded.viewModel;

import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import net.pubnative.lite.sdk.BaseViewModel;
import net.pubnative.lite.sdk.HyBid;
import net.pubnative.lite.sdk.analytics.Reporting;
import net.pubnative.lite.sdk.contentinfo.AdFeedbackFormHelper;
import net.pubnative.lite.sdk.contentinfo.listeners.AdFeedbackLoadListener;
import net.pubnative.lite.sdk.models.Ad;
import net.pubnative.lite.sdk.models.AdExperience;
import net.pubnative.lite.sdk.models.ContentInfo;
import net.pubnative.lite.sdk.models.ContentInfoIconXPosition;
import net.pubnative.lite.sdk.models.ContentInfoIconYPosition;
import net.pubnative.lite.sdk.models.IntegrationType;
import net.pubnative.lite.sdk.models.PositionX;
import net.pubnative.lite.sdk.models.PositionY;
import net.pubnative.lite.sdk.rewarded.HyBidRewardedBroadcastReceiver;
import net.pubnative.lite.sdk.rewarded.HyBidRewardedBroadcastSender;
import net.pubnative.lite.sdk.rewarded.RewardedActivityInteractor;
import net.pubnative.lite.sdk.utils.Logger;
import net.pubnative.lite.sdk.utils.URLValidator;
import net.pubnative.lite.sdk.utils.UrlHandler;
import net.pubnative.lite.sdk.views.CloseableContainer;
import net.pubnative.lite.sdk.views.PNAPIContentInfoView;
import net.pubnative.lite.sdk.vpaid.helpers.EventTracker;
import net.pubnative.lite.sdk.vpaid.models.vast.Icon;
import net.pubnative.lite.sdk.vpaid.utils.Utils;

import java.util.List;

public abstract class RewardedViewModel extends BaseViewModel implements PNAPIContentInfoView.ContentInfoListener {
    private static final String TAG = RewardedViewModel.class.getSimpleName();

    protected final Context mContext;
    protected final String mZoneId;

    protected final RewardedActivityInteractor mListener;
    protected IntegrationType mIntegrationType;
    protected final int mSkipOffset;
    protected Ad mAd;

    private final HyBidRewardedBroadcastSender mBroadcastSender;
    private final UrlHandler mUrlHandlerDelegate;

    public boolean isLinkClickRunning = false;
    protected boolean mIsFeedbackFormOpen = false;
    private boolean mIsFeedbackFormLoading = false;
    private final AdFeedbackFormHelper adFeedbackFormHelper;

    private View mContentInfoView;
    private static final int REDUCED_CLOSE_BUTTON_SIZE = 20;

    protected boolean mIsSkippable = false;

    public RewardedViewModel(Context context, String zoneId, String integrationType, int skipOffset, long broadcastId, RewardedActivityInteractor listener) {
        this.mContext = context;
        this.mZoneId = zoneId;
        this.mSkipOffset = skipOffset;
        this.mListener = listener;
        validateIntegrationType(integrationType);
        mBroadcastSender = new HyBidRewardedBroadcastSender(context, broadcastId);
        mUrlHandlerDelegate = new UrlHandler(context);
        adFeedbackFormHelper = new AdFeedbackFormHelper();
        fetchAd();
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

    public void processRewardedAd() {
        if (isValidAdToRender()) {
            View view = getAdView();
            mListener.hideRewardedCloseButton();
            if (view != null) {
                if (hasReducedCloseSize()) {
                    mListener.setCloseSize(REDUCED_CLOSE_BUTTON_SIZE);
                }
                FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.MATCH_PARENT);
                params.gravity = Gravity.CENTER;
                mListener.hideProgressBar();
                FrameLayout.LayoutParams pBarParams = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.WRAP_CONTENT, FrameLayout.LayoutParams.WRAP_CONTENT);
                pBarParams.gravity = Gravity.CENTER;
                mListener.addProgressBarView(pBarParams);
                mListener.addAdView(view, params);
                if (!(this instanceof VastRewardedViewModel) && shouldShowContentInfo() && mAd != null) {
                    View contentInfo = mAd.getContentInfoContainer(mContext, this);
                    if (contentInfo != null) {
                        mListener.addContentInfoView(contentInfo, null);
                    }
                }
            } else {
                mListener.finishActivity();
            }
        } else {
            mListener.finishActivity();
        }
    }

    private void fetchAd() {
        if (mAd == null) {
            synchronized (this) {
                if (HyBid.getAdCache() != null) {
                    mAd = HyBid.getAdCache().remove(mZoneId);
                }
            }
        }
    }

    protected void dismiss() {
        sendBroadcast(HyBidRewardedBroadcastReceiver.Action.CLOSE);
        mListener.finishActivity();
    }

    public void setupContentInfo(Icon icon) {
        if (mAd != null) {
            ContentInfo contentInfo = Utils.parseContentInfo(icon);
            mContentInfoView = getContentInfo(mContext, contentInfo);
            if (mContentInfoView != null) {
                if (contentInfo != null) {
                    int xGravity = Gravity.START;
                    int yGravity = Gravity.TOP;
                    if (mAd.getContentInfoIconXPosition() != null) {
                        ContentInfoIconXPosition remoteIconXPosition = mAd.getContentInfoIconXPosition();
                        if (remoteIconXPosition == ContentInfoIconXPosition.RIGHT) {
                            xGravity = Gravity.END;
                        }
                    } else {
                        if (contentInfo.getPositionX() == PositionX.RIGHT) {
                            xGravity = Gravity.END;
                        }
                    }

                    if (mAd.getContentInfoIconYPosition() != null) {
                        ContentInfoIconYPosition remoteIconYPosition = mAd.getContentInfoIconYPosition();
                        if (remoteIconYPosition == ContentInfoIconYPosition.BOTTOM) {
                            yGravity = Gravity.BOTTOM;
                        }
                    } else {
                        if (contentInfo.getPositionY() == PositionY.BOTTOM) {
                            yGravity = Gravity.BOTTOM;
                        }
                    }
                    FrameLayout.LayoutParams layoutParams = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                    layoutParams.gravity = xGravity | yGravity;
                    mListener.addContentInfoView(mContentInfoView, layoutParams);
                } else {
                    mListener.addContentInfoView(mContentInfoView, null);
                }
                if (contentInfo != null)
                    postTrackerEvents(mContext, contentInfo.getViewTrackers());
            }
        }
    }

    private View getContentInfo(Context context, ContentInfo contentInfo) {
        return contentInfo == null ? mAd.getContentInfoContainer(context, this) : mAd.getContentInfoContainer(context, contentInfo, this);
    }

    public ViewGroup getContentInfoContainer() {
        if (mAd != null)
            return mAd.getContentInfoContainer(mContext, this);
        return null;
    }

    public void hideContentInfo() {
        if (mContentInfoView != null) {
            mListener.removeContentInfoView(mContentInfoView);
        }
    }

    public boolean isAdSkippable() {
        return mIsSkippable;
    }

    protected final CloseableContainer.OnCloseListener mCloseListener = this::closeButtonClicked;

    @Override
    public void onIconClicked(List<String> clickTrackers) {
        if (clickTrackers != null && !clickTrackers.isEmpty()) {
            for (int i = 0; i < clickTrackers.size(); i++) {
                EventTracker.post(mContext, clickTrackers.get(i), null, false);
            }
        }
        invokeOnContentInfoClick(mIntegrationType, mAd, Reporting.AdFormat.REWARDED);
    }

    @Override
    public void onLinkClicked(String url) {
        if (!isLinkClickRunning) {
            isLinkClickRunning = true;
            if (!mIsFeedbackFormOpen && !mIsFeedbackFormLoading) {
                if (URLValidator.isValidURL(url)) {
                    adFeedbackFormHelper.showFeedbackForm(mContext, url, mAd, Reporting.AdFormat.REWARDED, IntegrationType.STANDALONE, new AdFeedbackLoadListener() {
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

    public void handleURL(String url) {
        if (mAd != null) {
            mUrlHandlerDelegate.handleUrl(url, mAd.getNavigationMode());
        }
    }

    public boolean hasReducedCloseSize() {
        if (mAd != null) {
            Boolean hasReducedIconSize = mAd.isIconSizeReduced();
            String adExperience = mAd.getAdExperience();
            return adExperience.equalsIgnoreCase(AdExperience.PERFORMANCE) &&
                    hasReducedIconSize != null && hasReducedIconSize;
        }
        return false;
    }

    public boolean isValidAdToRender() {
        return !TextUtils.isEmpty(mZoneId) && mBroadcastSender.getBroadcastId() != -1;
    }

    public void sendBroadcast(HyBidRewardedBroadcastReceiver.Action action) {
        if (mBroadcastSender != null)
            mBroadcastSender.sendBroadcast(action);
    }

    public void sendBroadcast(HyBidRewardedBroadcastReceiver.Action action, Bundle extras) {
        if (mBroadcastSender != null)
            mBroadcastSender.sendBroadcast(action, extras);
    }

    public boolean isFeedbackFormOpen() {
        return mIsFeedbackFormOpen;
    }

    public abstract boolean shouldShowContentInfo();

    public abstract void resumeAd();

    public abstract void pauseAd();

    public abstract void closeButtonClicked();

    public abstract View getAdView();

    public abstract void destroyAd();

    public abstract void resetVolumeChangeTracker();
}