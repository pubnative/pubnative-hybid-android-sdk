package net.pubnative.lite.sdk.vpaid.vast;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.SurfaceTexture;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Surface;
import android.view.TextureView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.iab.omid.library.pubnativenet.adsession.FriendlyObstructionPurpose;

import net.pubnative.lite.sdk.CountdownStyle;
import net.pubnative.lite.sdk.HyBid;
import net.pubnative.lite.sdk.InterstitialActionBehaviour;
import net.pubnative.lite.sdk.analytics.Reporting;
import net.pubnative.lite.sdk.analytics.ReportingEvent;
import net.pubnative.lite.sdk.core.R;
import net.pubnative.lite.sdk.models.SkipOffset;
import net.pubnative.lite.sdk.mraid.MRAIDBanner;
import net.pubnative.lite.sdk.mraid.MRAIDNativeFeatureListener;
import net.pubnative.lite.sdk.mraid.MRAIDView;
import net.pubnative.lite.sdk.mraid.MRAIDViewListener;
import net.pubnative.lite.sdk.utils.Logger;
import net.pubnative.lite.sdk.views.endcard.HyBidEndCardView;
import net.pubnative.lite.sdk.vpaid.AdCloseButtonListener;
import net.pubnative.lite.sdk.vpaid.CloseButtonListener;
import net.pubnative.lite.sdk.vpaid.VastActivityInteractor;
import net.pubnative.lite.sdk.vpaid.VideoAdController;
import net.pubnative.lite.sdk.vpaid.VideoAdView;
import net.pubnative.lite.sdk.vpaid.VideoVisibilityManager;
import net.pubnative.lite.sdk.vpaid.helpers.BitmapHelper;
import net.pubnative.lite.sdk.vpaid.helpers.SimpleTimer;
import net.pubnative.lite.sdk.vpaid.models.CloseCardData;
import net.pubnative.lite.sdk.models.EndCardData;
import net.pubnative.lite.sdk.vpaid.response.AdParams;
import net.pubnative.lite.sdk.vpaid.utils.ImageUtils;
import net.pubnative.lite.sdk.vpaid.utils.Utils;
import net.pubnative.lite.sdk.vpaid.widget.CountDownView;
import net.pubnative.lite.sdk.vpaid.widget.CountDownViewFactory;
import net.pubnative.lite.sdk.vpaid.widget.LinearCountDownView;

public class ViewControllerVast implements View.OnClickListener {
    private static final String LOG_TAG = ViewControllerVast.class.getSimpleName();

    private final VideoAdController mAdController;

    private VideoAdView mBannerView;
    private CountDownView mSkipCountdownView;
    private LinearCountDownView mLinearCountdownView;
    private FrameLayout mVideoPlayerLayout;
    private TextureView mVideoPlayerLayoutTexture;
    private View mControlsLayout;
    private HyBidEndCardView mEndCardView;
    private boolean mMuteState;
    private final boolean mIsFullscreen;
    private Surface mSurface;
    private View mSkipView;
    private ImageView mMuteView;
    private final Integer mRemoteEndCardCloseDelay;

    VideoVisibilityManager videoVisibilityManager;
    VastActivityInteractor interactor;

    private InterstitialActionBehaviour remoteConfigInterstitialClickBehaviour = null;

    public ViewControllerVast(VideoAdController adController, boolean isFullscreen, Integer endCardCloseDelay, Integer nativeCloseButtonDelay, Boolean fullScreenClickability, AdCloseButtonListener adCloseButtonListener) {
        mAdController = adController;
        mIsFullscreen = isFullscreen;
        videoVisibilityManager = VideoVisibilityManager.getInstance();
        mRemoteEndCardCloseDelay = endCardCloseDelay;
        if (fullScreenClickability != null) {
            if (fullScreenClickability)
                remoteConfigInterstitialClickBehaviour = InterstitialActionBehaviour.HB_CREATIVE;
            else
                remoteConfigInterstitialClickBehaviour = InterstitialActionBehaviour.HB_ACTION_BUTTON;
        }
        interactor = VastActivityInteractor.getInstance();
    }

    public void buildVideoAdView(VideoAdView bannerView) {
        if (interactor.isActivityVisible() || !mIsFullscreen) {
            Context context = bannerView.getContext();
            mBannerView = bannerView;
            bannerView.setVisibilityListener(mCreateVisibilityListener);
            bannerView.removeAllViews();

            mControlsLayout = LayoutInflater.from(context).inflate(R.layout.controls, bannerView, false);

            InterstitialActionBehaviour interstitialClickBehaviour = HyBid.getInterstitialClickBehaviour();
            if (remoteConfigInterstitialClickBehaviour != null)
                interstitialClickBehaviour = remoteConfigInterstitialClickBehaviour;

            TextView openView = mControlsLayout.findViewById(R.id.openURL);
            if (interstitialClickBehaviour == InterstitialActionBehaviour.HB_CREATIVE) {
                mControlsLayout.setOnClickListener(v -> validateOpenURLClicked());
                if (openView != null) openView.setVisibility(View.GONE);
            } else {
                if (openView != null) openView.setVisibility(View.VISIBLE);
            }

            mVideoPlayerLayout = mControlsLayout.findViewById(R.id.videoPlayerLayout);

            if (hasCTAExtension(mAdController.getAdParams())) {
                RelativeLayout mMediaLayout = new RelativeLayout(mVideoPlayerLayout.getContext());
                mVideoPlayerLayoutTexture = new TextureView(mMediaLayout.getContext());
                mVideoPlayerLayoutTexture.setId(R.id.textureView);
                mMediaLayout.addView(mVideoPlayerLayoutTexture, new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.MATCH_PARENT));
                mVideoPlayerLayout.addView(mMediaLayout, 0, new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.MATCH_PARENT));
            } else {
                mVideoPlayerLayoutTexture = new TextureView(mVideoPlayerLayout.getContext());
                mVideoPlayerLayoutTexture.setId(R.id.textureView);
                mVideoPlayerLayout.addView(mVideoPlayerLayoutTexture, 0, new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.MATCH_PARENT));
            }

            mEndCardView = new HyBidEndCardView(context);
            mEndCardView.setLayoutParams(new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
            mEndCardView.setVisibility(View.GONE);

            mControlsLayout.findViewById(R.id.openURL).setOnClickListener(this);

            mSkipCountdownView = new CountDownViewFactory().createCountdownView(context, HyBid.getCountdownStyle(), mVideoPlayerLayout);
            mVideoPlayerLayout.addView(mSkipCountdownView);
            mLinearCountdownView = mControlsLayout.findViewById(R.id.linear_count_down);
            if (mVideoPlayerLayoutTexture != null) {
                mVideoPlayerLayoutTexture.setSurfaceTextureListener(mCreateTextureListener);
            }

            mMuteView = mControlsLayout.findViewById(R.id.muteView);
            mMuteView.setOnClickListener(this);

            if (HyBid.getCountdownStyle() == CountdownStyle.PROGRESS) {
                mSkipView = mControlsLayout.findViewById(R.id.progressSkipView);
            } else {
                mSkipView = mControlsLayout.findViewById(R.id.skipView);
                Bitmap skipBitmap = BitmapHelper.toBitmap(mSkipView.getContext(), HyBid.getSkipXmlResource(), R.mipmap.skip);
                if (skipBitmap != null) ((ImageView) mSkipView).setImageBitmap(skipBitmap);
                else
                    ((ImageView) mSkipView).setImageBitmap(BitmapHelper.decodeResource(mSkipView.getContext(), R.mipmap.skip));
            }

            mSkipView.setOnClickListener(this);

            mAdController.addViewabilityFriendlyObstruction(mControlsLayout, FriendlyObstructionPurpose.VIDEO_CONTROLS, "Video controls");

            bannerView.addView(mControlsLayout);
            bannerView.addView(mEndCardView);
        }
    }

    private boolean hasCTAExtension(AdParams adParams) {
        return adParams != null && !TextUtils.isEmpty(adParams.getCtaExtensionHtml());
    }

    private void validateOpenURLClicked() {
        mAdController.getViewabilityAdSession().fireClick();
        mAdController.openUrl(null);
    }

    private final VideoAdView.VisibilityListener mCreateVisibilityListener = new VideoAdView.VisibilityListener() {
        @Override
        public void onVisibilityChanged(int visibility) {
            try {
                if (visibility == View.VISIBLE) {
                    mAdController.setVideoVisible(true);
                    videoVisibilityManager.reportChange(VideoVisibilityManager.VideoAdStatus.RESUMED);
                } else {
                    mAdController.setVideoVisible(false);
                    videoVisibilityManager.reportChange(VideoVisibilityManager.VideoAdStatus.PAUSED);
                }

            } catch (Exception e) {
                HyBid.reportException(e);
                Logger.e(LOG_TAG, "ViewControllerVast.createVisibilityListener: Log: " + Log.getStackTraceString(e));
            }
        }
    };

    private final TextureView.SurfaceTextureListener mCreateTextureListener = new TextureView.SurfaceTextureListener() {
        @Override
        public void onSurfaceTextureAvailable(SurfaceTexture surface, int width, int height) {
            mSurface = new Surface(surface);
        }

        @Override
        public void onSurfaceTextureSizeChanged(SurfaceTexture surface, int width, int height) {
        }

        @Override
        public boolean onSurfaceTextureDestroyed(SurfaceTexture surface) {
            return false;
        }

        @Override

        public void onSurfaceTextureUpdated(SurfaceTexture surface) {
        }
    };

    public void adjustLayoutParams(int width, int height) {
        if (mControlsLayout == null) {
            Logger.e(LOG_TAG, "ViewControllerVast.adjustLayoutParams: Log: mControlsLayout is null");
            return;
        }
        FrameLayout.LayoutParams oldParams = (FrameLayout.LayoutParams) mControlsLayout.getLayoutParams();
        ViewGroup.LayoutParams newParams = Utils.calculateNewLayoutParams(oldParams, width, height, mBannerView.getWidth(), mBannerView.getHeight(), Utils.StretchOption.NO_STRETCH);
        mControlsLayout.setLayoutParams(newParams);
    }

    public void postDelayed(Runnable action, long delayMillis) {
        if (mBannerView != null) {
            mBannerView.postDelayed(action, delayMillis);
        }
    }

    public Surface getSurface() {
        return mSurface;
    }

    public void setProgress(int progress, int total) {
        if (mLinearCountdownView != null) {
            mLinearCountdownView.setProgress(total - progress, total);
        }
    }

    public void setSkipProgress(int millisUntilFinished, int mSkipTimeMillis) {
        if (mSkipCountdownView != null) {
            mSkipCountdownView.setProgress(mSkipTimeMillis - millisUntilFinished, mSkipTimeMillis);
        }
    }

    public void endSkip() {
        if (mSkipCountdownView != null) {
            mSkipCountdownView.setVisibility(View.GONE);
            showSkipButton();
        }
    }

    public void resetProgress() {
        if (mLinearCountdownView != null) {
            mLinearCountdownView.reset();
        }
    }

    public boolean isMute() {
        return mMuteState;
    }

    public void showEndCard(EndCardData endCardData, String imageUri, Boolean isLastEndCard, CloseButtonListener closeButtonListener) {
        if (mEndCardView != null) {
            mEndCardView.setEndCardViewListener(new HyBidEndCardView.EndCardViewListener() {
                @Override
                public void onClick(Boolean isCustomEndCard) {
                    validateOpenURLClicked();
                    if(isCustomEndCard){
                        mAdController.onCustomEndCardClick();
                    }
                }

                @Override
                public void onSkip() {
                    skipEndCard();
                }

                @Override
                public void onClose() {
                    closeSelf();
                }

                @Override
                public void onShow(Boolean isCustomEndCard) {
                    if(isCustomEndCard)
                        mAdController.onCustomEndCardShow();
                }
            });
            SkipOffset endCardCloseDelay = getEndCardCloseDelay();
            mEndCardView.setSkipOffset(endCardCloseDelay);
            mEndCardView.show(endCardData, imageUri);
            if (mIsFullscreen) {
                if (isLastEndCard) {
                    mEndCardView.showCloseButton();
                } else {
                    mEndCardView.showSkipButton();
                }
            }
            ReportingEvent event = new ReportingEvent();
            event.setEventType(Reporting.EventType.COMPANION_VIEW_END_CARD);
            event.setCreativeType(Reporting.CreativeType.VIDEO);
            event.setTimestamp(System.currentTimeMillis());
            if (HyBid.getReportingController() != null) {
                HyBid.getReportingController().reportEvent(event);
            }
        }
    }

    private final MRAIDViewListener mraidViewListener = new MRAIDViewListener() {
        @Override
        public void mraidViewLoaded(MRAIDView mraidView) {

        }

        @Override
        public void mraidViewError(MRAIDView mraidView) {

        }

        @Override
        public void mraidViewExpand(MRAIDView mraidView) {

        }

        @Override
        public void mraidViewClose(MRAIDView mraidView) {

        }

        @Override
        public boolean mraidViewResize(MRAIDView mraidView, int width, int height, int offsetX, int offsetY) {
            return false;
        }

        @Override
        public void mraidShowCloseButton() {

        }

        @Override
        public void onExpandedAdClosed() {
        }
    };

    private final MRAIDNativeFeatureListener mraidNativeFeatureListener = new MRAIDNativeFeatureListener() {
        @Override
        public void mraidNativeFeatureCallTel(String url) {
        }

        @Override
        public void mraidNativeFeatureCreateCalendarEvent(String eventJSON) {
        }

        @Override
        public void mraidNativeFeaturePlayVideo(String url) {
        }

        @Override
        public void mraidNativeFeatureOpenBrowser(String url) {
            validateOpenURLClicked();
        }

        @Override
        public void mraidNativeFeatureStorePicture(String url) {
        }

        @Override
        public void mraidNativeFeatureSendSms(String url) {
        }
    };

    public void showSkipButton() {
        if (mSkipView != null) {
            mSkipView.setVisibility(View.VISIBLE);
            mSkipView.setClickable(true);
        }
    }

    public VideoAdView getVideoView() {
        return mBannerView;
    }

    public void hideSkipButton() {
        if (mSkipView != null) {
            mSkipView.setVisibility(View.GONE);
        }
    }

    public void hideTimerAndMuteButton() {
        if (mLinearCountdownView != null) {
            mLinearCountdownView.setVisibility(View.GONE);
        }
        if (mMuteView != null) {
            mMuteView.setVisibility(View.GONE);
        }
    }

    public boolean isEndCard() {
        return mEndCardView != null && mEndCardView.getVisibility() != View.VISIBLE;
    }

    public void dismiss() {
        if (mBannerView != null) {
            mBannerView.removeAllViews();
        }
    }

    public void destroy() {
        if (mEndCardView != null) {
            mEndCardView.destroy();
        }
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.close_view) {
            closeSelf();
        } else if (v.getId() == R.id.skipView || v.getId() == R.id.progressSkipView) {
            skipVideo();
        } else if (v.getId() == R.id.muteView) {
            muteVideo();
        } else if (v.getId() == R.id.openURL) {
            validateOpenURLClicked();
        }
    }

    private void skipVideo() {
        mAdController.skipVideo();
    }

    private void skipEndCard() {
        mAdController.skipEndCard();
    }

    private void closeSelf() {
        mAdController.closeSelf();
    }

    private void replayVideo() {
        mEndCardView.hide();
        mVideoPlayerLayout.setVisibility(View.VISIBLE);
        mAdController.playAd();
    }

    public void pauseEndCardCloseButtonTimer() {
        if (mEndCardView != null) mEndCardView.pause();
    }

    public void resumeEndCardCloseButtonTimer() {
        if (mEndCardView != null) mEndCardView.resume();
    }

    public void muteVideo() {
        mMuteState = !mMuteState;
        mAdController.setVolume(mMuteState);
        if (mMuteView != null) {
            if (mMuteState) {
                mMuteView.setImageResource(R.mipmap.mute);
                mMuteView.setContentDescription("muteButton");
            } else {
                mMuteView.setImageResource(R.mipmap.unmute);
                mMuteView.setContentDescription("unmuteButton");
            }
        }
    }

    public TextureView getTexture() {
        return mVideoPlayerLayoutTexture;
    }

    private SkipOffset getEndCardCloseDelay() {
        if (mRemoteEndCardCloseDelay != null) {
            return new SkipOffset(mRemoteEndCardCloseDelay, true);
        } else {
            return HyBid.getEndCardCloseButtonDelay();
        }
    }
}
