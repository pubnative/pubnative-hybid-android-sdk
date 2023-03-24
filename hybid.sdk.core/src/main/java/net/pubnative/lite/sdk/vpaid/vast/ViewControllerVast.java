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
import net.pubnative.lite.sdk.vpaid.CloseButtonListener;
import net.pubnative.lite.sdk.vpaid.VastActivityInteractor;
import net.pubnative.lite.sdk.vpaid.VideoAdController;
import net.pubnative.lite.sdk.vpaid.VideoAdView;
import net.pubnative.lite.sdk.vpaid.VideoVisibilityManager;
import net.pubnative.lite.sdk.vpaid.helpers.BitmapHelper;
import net.pubnative.lite.sdk.vpaid.helpers.SimpleTimer;
import net.pubnative.lite.sdk.vpaid.models.CloseCardData;
import net.pubnative.lite.sdk.vpaid.models.EndCardData;
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
    private ImageView mStaticEndCardView;
    private FrameLayout mHtmlEndCardContainer;
    private MRAIDBanner mHtmlEndCardView;
    private View mControlsLayout;
    private View mEndCardLayout;
    private ImageView mEndCardCloseView;
    private View mCloseCardLayout;
    private FrameLayout mHtmlCloseCardContainer;
    private ImageView mCloseCardCloseView;
    private TextView mCloseCardTitleView;
    private ImageView mCloseCardIconView;
    private RatingBar mCloseCardRatingView;
    private ImageView mStaticCloseCardView;
    private View closeCardVotesLayout;
    private TextView mCloseCardVoteView;
    private View mCloseCardActionView;
    private MRAIDBanner mHtmlCloseCardView;
    private boolean mMuteState;
    private boolean mIsFullscreen;
    private RelativeLayout mMediaLayout;
    private Surface mSurface;
    private View mSkipView;
    private ImageView mMuteView;
    private SimpleTimer mEndcardTimer;
    private Integer mRemoteEndCardCloseDelay;

    VideoVisibilityManager videoVisibilityManager;
    VastActivityInteractor interactor;

    private InterstitialActionBehaviour interstitialClickBehaviour;
    private InterstitialActionBehaviour remoteConfigInterstitialClickBehaviour = null;

    public ViewControllerVast(VideoAdController adController, boolean isFullscreen, Integer endCardCloseDelay, Boolean fullScreenClickability) {
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

            interstitialClickBehaviour = HyBid.getInterstitialClickBehaviour();
            if (remoteConfigInterstitialClickBehaviour != null)
                interstitialClickBehaviour = remoteConfigInterstitialClickBehaviour;

            if (interstitialClickBehaviour == InterstitialActionBehaviour.HB_CREATIVE) {
                mControlsLayout.setOnClickListener(v -> validateOpenURLClicked());
                mControlsLayout.findViewById(R.id.openURL).setVisibility(View.GONE);
            } else {
                mControlsLayout.findViewById(R.id.openURL).setVisibility(View.VISIBLE);
            }

            mVideoPlayerLayout = mControlsLayout.findViewById(R.id.videoPlayerLayout);

            if (hasCTAExtension(mAdController.getAdParams())) {
                mMediaLayout = new RelativeLayout(mVideoPlayerLayout.getContext());
                mVideoPlayerLayoutTexture = new TextureView(mMediaLayout.getContext());
                mVideoPlayerLayoutTexture.setId(R.id.textureView);
                mMediaLayout.addView(mVideoPlayerLayoutTexture, new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.MATCH_PARENT));
                mVideoPlayerLayout.addView(mMediaLayout, 0, new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.MATCH_PARENT));
            } else {
                mVideoPlayerLayoutTexture = new TextureView(mVideoPlayerLayout.getContext());
                mVideoPlayerLayoutTexture.setId(R.id.textureView);
                mVideoPlayerLayout.addView(mVideoPlayerLayoutTexture, 0, new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.MATCH_PARENT));
            }

            mEndCardLayout = mControlsLayout.findViewById(R.id.endCardLayout);
            mEndCardLayout.setVisibility(View.GONE);
            mStaticEndCardView = mControlsLayout.findViewById(R.id.staticEndCardView);
            mHtmlEndCardContainer = mControlsLayout.findViewById(R.id.htmlEndCardContainer);

            mEndCardCloseView = mControlsLayout.findViewById(R.id.closeEndCardView);
            mEndCardCloseView.setOnClickListener(this);
            mEndCardCloseView.setVisibility(View.GONE);

            Bitmap closeBitmap = BitmapHelper.toBitmap(mEndCardCloseView.getContext(), HyBid.getNormalCloseXmlResource(), R.mipmap.close);
            if (closeBitmap != null) ((ImageView) mEndCardCloseView).setImageBitmap(closeBitmap);
            else
                ((ImageView) mEndCardCloseView).setImageBitmap(BitmapHelper.decodeResource(mEndCardCloseView.getContext(), R.mipmap.close));

            mCloseCardLayout = mControlsLayout.findViewById(R.id.closeCardLayout);
            mCloseCardLayout.setVisibility(View.GONE);
            mCloseCardCloseView = mCloseCardLayout.findViewById(R.id.closeView);
            mStaticCloseCardView = mCloseCardLayout.findViewById(R.id.staticCloseCardView);
            mHtmlCloseCardContainer = mCloseCardLayout.findViewById(R.id.htmlCloseCardContainer);
            mCloseCardTitleView = mCloseCardLayout.findViewById(R.id.closeCardTitle);
            mCloseCardRatingView = mCloseCardLayout.findViewById(R.id.closeCardRaiting);
            mCloseCardRatingView.setIsIndicator(true);
            mCloseCardVoteView = mCloseCardLayout.findViewById(R.id.closeCardVoteCount);
            mCloseCardActionView = mCloseCardLayout.findViewById(R.id.closeCardActionButton);
            mCloseCardIconView = mCloseCardLayout.findViewById(R.id.closeCardIconImageView);
            closeCardVotesLayout = mCloseCardLayout.findViewById(R.id.closeCardVotesLayout);
            mCloseCardCloseView.setOnClickListener(this);

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

            if (mAdController.isRewarded()) {
                mSkipCountdownView.setVisibility(View.GONE);
            }
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
        if(mControlsLayout == null){
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
        mLinearCountdownView.setProgress(total - progress, total);
    }

    public void setSkipProgress(int millisUntilFinished, int mSkipTimeMillis) {
        mSkipCountdownView.setProgress(mSkipTimeMillis - millisUntilFinished, mSkipTimeMillis);
    }

    public void endSkip() {
        mSkipCountdownView.setVisibility(View.GONE);
        showSkipButton();
    }

    public void resetProgress() {
        mLinearCountdownView.reset();
    }

    public boolean isMute() {
        return mMuteState;
    }

    public void showEndCard(EndCardData endCardData, String imageUri, CloseButtonListener closeButtonListener) {
        mEndCardLayout.setVisibility(View.VISIBLE);
        mEndCardLayout.setOnClickListener(v -> validateOpenURLClicked());
        SkipOffset endCardCloseDelay = getEndCardCloseDelay();
        if (endCardData != null) {
            mControlsLayout.setLayoutParams(new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.MATCH_PARENT));
            if (endCardData.getType() == EndCardData.Type.STATIC_RESOURCE) {
                mHtmlEndCardContainer.setVisibility(View.GONE);
                mStaticEndCardView.setVisibility(View.VISIBLE);
                mVideoPlayerLayout.setVisibility(View.GONE);
                ImageUtils.setScaledImage(mStaticEndCardView, imageUri);
            } else if (!TextUtils.isEmpty(endCardData.getContent())) {
                mStaticEndCardView.setVisibility(View.GONE);
                mHtmlEndCardContainer.setVisibility(View.VISIBLE);
                mVideoPlayerLayout.setVisibility(View.GONE);
                if (endCardData.getType() == EndCardData.Type.IFRAME_RESOURCE) {
                    mHtmlEndCardView = new MRAIDBanner(mEndCardLayout.getContext(), endCardData.getContent(), "", false, new String[]{}, mraidViewListener, mraidNativeFeatureListener, null);
                } else {
                    mHtmlEndCardView = new MRAIDBanner(mEndCardLayout.getContext(), "", endCardData.getContent(), false, new String[]{}, mraidViewListener, mraidNativeFeatureListener, null);
                }
                mHtmlEndCardView.setSkipOffset(endCardCloseDelay.getOffset());
                mHtmlEndCardContainer.addView(mHtmlEndCardView);
            }
        }

        if (mIsFullscreen) {
            showEndCardCloseButton(endCardCloseDelay.getOffset(), closeButtonListener);
        }

        ReportingEvent event = new ReportingEvent();
        event.setEventType(Reporting.EventType.COMPANION_VIEW_END_CARD);
        event.setCreativeType(Reporting.CreativeType.VIDEO);
        event.setTimestamp(System.currentTimeMillis());
        if (HyBid.getReportingController() != null) {
            HyBid.getReportingController().reportEvent(event);
        }
    }

    public void showCloseCard(CloseCardData closeCardData) {

        mCloseCardLayout.setVisibility(View.VISIBLE);
        mCloseCardLayout.setOnClickListener(null);
        mControlsLayout.setLayoutParams(new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.MATCH_PARENT));

        mCloseCardTitleView.setText(closeCardData.getTitle());
        mCloseCardRatingView.setRating((float) closeCardData.getRating());

        if (closeCardData.getVotes() > 0) {
            closeCardVotesLayout.setVisibility(View.VISIBLE);
            mCloseCardVoteView.setText(mCloseCardLayout.getContext().getString(R.string.close_card_votes, closeCardData.getVotes()));
        } else {
            closeCardVotesLayout.setVisibility(View.GONE);
        }

        if (closeCardData.getIcon() != null) {
            mCloseCardIconView.setImageBitmap(closeCardData.getIcon());
        }

        mCloseCardActionView.setOnClickListener(v -> validateOpenURLClicked());

        if (closeCardData.getBanner() != null) {
            // add Check if is static banner or MRAIDBanne in the feature
            if (true) {
                if (closeCardData.getBannerImage() != null) {
                    ImageUtils.setScaledImage(mStaticCloseCardView, closeCardData.getBannerImage());
                    mStaticCloseCardView.setVisibility(View.VISIBLE);
                }
            } else {
                mStaticCloseCardView.setVisibility(View.GONE);
                mHtmlCloseCardView = new MRAIDBanner(mEndCardLayout.getContext(), closeCardData.getBanner(), "", false, new String[]{}, mraidViewListener, mraidNativeFeatureListener, null);
                mHtmlCloseCardContainer.addView(mHtmlCloseCardView);
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
        if (!mAdController.isRewarded()) {
            if (mSkipView != null) {
                mSkipView.setVisibility(View.VISIBLE);
                mSkipView.setClickable(true);
            }
        }
    }

    public VideoAdView getVideoView() {
        return mBannerView;
    }

    public void hideSkipButton() {
        mSkipView.setVisibility(View.GONE);
    }

    public void hideTimerAndMuteButton() {
        mLinearCountdownView.setVisibility(View.GONE);
        mMuteView.setVisibility(View.GONE);
    }

    public boolean isEndCard() {
        return mEndCardLayout != null && mEndCardLayout.getVisibility() != View.VISIBLE;
    }

    public void dismiss() {
        if (mBannerView != null) {
            mBannerView.removeAllViews();
        }
    }

    public void destroy() {
        if (mStaticEndCardView != null) {
            mStaticEndCardView.setImageDrawable(null);
        }
        if (mHtmlEndCardView != null) {
            mHtmlEndCardView.destroy();
        }
        if (mEndcardTimer != null) {
            mEndcardTimer.cancel();
        }
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.closeView) {
            closeSelf();
        } else if (v.getId() == R.id.closeEndCardView) {
            closeEndCard();
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

    private void closeSelf() {
        mAdController.closeSelf();
    }

    private void closeEndCard() {
        mAdController.closeEndCard();
    }

    private void replayVideo() {
        mEndCardLayout.setVisibility(View.GONE);
        mVideoPlayerLayout.setVisibility(View.VISIBLE);
        mAdController.playAd();
    }

    private void showEndCardCloseButton(int endCardDelay, CloseButtonListener closeButtonListener) {
        if (endCardDelay >= 0) {
            int endCardDelayInMillis = endCardDelay * 1000;

            mEndcardTimer = new SimpleTimer(endCardDelayInMillis, new SimpleTimer.Listener() {
                @Override
                public void onFinish() {
                    mEndCardCloseView.setVisibility(View.VISIBLE);
                    closeButtonListener.onCloseButtonVisible();
                }

                @Override
                public void onTick(long millisUntilFinished) {
                }
            });
            mEndcardTimer.start();
        } else {
            mEndCardCloseView.setVisibility(View.VISIBLE);
        }
    }

    public void pauseEndCardCloseButtonTimer() {
        if (mEndcardTimer != null)
            mEndcardTimer.pause();
        if (mHtmlEndCardView != null)
            mHtmlEndCardView.pause();
    }

    public void resumeEndCardCloseButtonTimer() {
        if (mEndcardTimer != null)
            mEndcardTimer.resume();
        if (mHtmlEndCardView != null)
            mHtmlEndCardView.resume();
    }

    public void muteVideo() {
        mMuteState = !mMuteState;
        mAdController.setVolume(mMuteState);
        if (mMuteView != null) {
            if (mMuteState) {
                mMuteView.setImageResource(R.mipmap.mute);
            } else {
                mMuteView.setImageResource(R.mipmap.unmute);
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
