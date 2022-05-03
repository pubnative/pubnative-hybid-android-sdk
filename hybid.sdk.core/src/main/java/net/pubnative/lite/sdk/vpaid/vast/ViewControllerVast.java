package net.pubnative.lite.sdk.vpaid.vast;

import android.content.Context;
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
import android.widget.RelativeLayout;

import com.iab.omid.library.pubnativenet.adsession.FriendlyObstructionPurpose;

import net.pubnative.lite.sdk.HyBid;
import net.pubnative.lite.sdk.InterstitialActionBehaviour;
import net.pubnative.lite.sdk.core.R;
import net.pubnative.lite.sdk.mraid.MRAIDBanner;
import net.pubnative.lite.sdk.mraid.MRAIDNativeFeatureListener;
import net.pubnative.lite.sdk.mraid.MRAIDView;
import net.pubnative.lite.sdk.mraid.MRAIDViewListener;
import net.pubnative.lite.sdk.utils.Logger;
import net.pubnative.lite.sdk.vpaid.VideoAdController;
import net.pubnative.lite.sdk.vpaid.VideoAdView;
import net.pubnative.lite.sdk.vpaid.helpers.SimpleTimer;
import net.pubnative.lite.sdk.vpaid.models.EndCardData;
import net.pubnative.lite.sdk.vpaid.response.AdParams;
import net.pubnative.lite.sdk.vpaid.utils.ImageUtils;
import net.pubnative.lite.sdk.vpaid.utils.Utils;
import net.pubnative.lite.sdk.vpaid.widget.CountDownView;
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
    private boolean mMuteState;
    private boolean mIsFullscreen;
    private RelativeLayout mMediaLayout;
    private Surface mSurface;
    private View mSkipView;
    private ImageView mMuteView;
    private SimpleTimer mEndcardTimer;

    private InterstitialActionBehaviour interstitialClickBehaviour;

    public ViewControllerVast(VideoAdController adController, boolean isFullscreen) {
        mAdController = adController;
        mIsFullscreen = isFullscreen;
    }

    public void buildVideoAdView(VideoAdView bannerView) {
        Context context = bannerView.getContext();
        mBannerView = bannerView;
        bannerView.setVisibilityListener(mCreateVisibilityListener);
        bannerView.removeAllViews();

        mControlsLayout = LayoutInflater.from(context).inflate(R.layout.controls, bannerView, false);

        interstitialClickBehaviour = HyBid.getInterstitialClickBehaviour();

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
            mMediaLayout.addView(mVideoPlayerLayoutTexture,
                    new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.MATCH_PARENT));
            mVideoPlayerLayout.addView(mMediaLayout, 0,
                    new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.MATCH_PARENT));
        } else {
            mVideoPlayerLayoutTexture = new TextureView(mVideoPlayerLayout.getContext());
            mVideoPlayerLayoutTexture.setId(R.id.textureView);
            mVideoPlayerLayout.addView(mVideoPlayerLayoutTexture, 0,
                    new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.MATCH_PARENT));
        }

        mEndCardLayout = mControlsLayout.findViewById(R.id.endCardLayout);
        mEndCardLayout.setVisibility(View.GONE);
        mStaticEndCardView = mControlsLayout.findViewById(R.id.staticEndCardView);
        mHtmlEndCardContainer = mControlsLayout.findViewById(R.id.htmlEndCardContainer);

        mEndCardCloseView = mControlsLayout.findViewById(R.id.closeView);
        mEndCardCloseView.setOnClickListener(this);
        mEndCardCloseView.setVisibility(View.GONE);

        mControlsLayout.findViewById(R.id.openURL).setOnClickListener(this);

        mSkipCountdownView = mControlsLayout.findViewById(R.id.count_down);
        mLinearCountdownView = mControlsLayout.findViewById(R.id.linear_count_down);
        if (mVideoPlayerLayoutTexture != null) {
            mVideoPlayerLayoutTexture.setSurfaceTextureListener(mCreateTextureListener);
        }

        mMuteView = mControlsLayout.findViewById(R.id.muteView);
        mMuteView.setOnClickListener(this);

        mSkipView = mControlsLayout.findViewById(R.id.skipView);
        mSkipView.setOnClickListener(this);

        mAdController.addViewabilityFriendlyObstruction(mControlsLayout, FriendlyObstructionPurpose.VIDEO_CONTROLS, "Video controls");

        bannerView.addView(mControlsLayout);

        if (mAdController.isRewarded()) {
            mSkipCountdownView.setVisibility(View.GONE);
        }
    }

    private boolean hasCTAExtension(AdParams adParams) {
        return adParams != null && !TextUtils.isEmpty(adParams.getCtaExtensionHtml());
    }

    private void validateOpenURLClicked() {
        if (mAdController.isRewarded() && !mAdController.adFinishedPlaying() && interstitialClickBehaviour == InterstitialActionBehaviour.HB_CREATIVE) {
            // Define pause/resume behaviour
        } else {
            mAdController.getViewabilityAdSession().fireClick();
            mAdController.openUrl(null);
        }
    }

    private final VideoAdView.VisibilityListener mCreateVisibilityListener = new VideoAdView.VisibilityListener() {
        @Override
        public void onVisibilityChanged(int visibility) {
            try {
                if (!mAdController.adFinishedPlaying()) {
                    if (visibility == View.VISIBLE) {
                        mAdController.setVideoVisible(true);
                        mAdController.resume();
                    } else {
                        mAdController.setVideoVisible(false);
                        mAdController.pause();
                    }
                }
            } catch (Exception e) {
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
        FrameLayout.LayoutParams oldParams = (FrameLayout.LayoutParams) mControlsLayout.getLayoutParams();
        ViewGroup.LayoutParams newParams = Utils.calculateNewLayoutParams(
                oldParams,
                width,
                height,
                mBannerView.getWidth(),
                mBannerView.getHeight(),
                Utils.StretchOption.NO_STRETCH
        );
        mControlsLayout.setLayoutParams(newParams);
    }

    public void postDelayed(Runnable action, long delayMillis) {
        mBannerView.postDelayed(action, delayMillis);
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

    public void showEndCard(EndCardData endCardData, String imageUri) {
        mEndCardLayout.setVisibility(View.VISIBLE);
        int endCardCloseDelay = HyBid.getEndCardCloseButtonDelay();
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
                    mHtmlEndCardView = new MRAIDBanner(mEndCardLayout.getContext(), endCardData.getContent(), "",
                            new String[]{}, mraidViewListener, mraidNativeFeatureListener, null);
                } else {
                    mHtmlEndCardView = new MRAIDBanner(mEndCardLayout.getContext(), "", endCardData.getContent(),
                            new String[]{}, mraidViewListener, mraidNativeFeatureListener, null);
                }
                mHtmlEndCardContainer.addView(mHtmlEndCardView);
            }
        }

        if (mIsFullscreen) {
            showEndCardCloseButton(endCardCloseDelay);
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
        } else if (v.getId() == R.id.skipView) {
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

    private void replayVideo() {
        mEndCardLayout.setVisibility(View.GONE);
        mVideoPlayerLayout.setVisibility(View.VISIBLE);
        mAdController.playAd();
    }

    private void showEndCardCloseButton(int endCardDelay) {
        if (endCardDelay >= 0) {
            int endCardDelayInMillis = endCardDelay * 1000;

            mEndcardTimer = new SimpleTimer(endCardDelayInMillis, () -> mEndCardCloseView.setVisibility(View.VISIBLE));
            mEndcardTimer.start();
        } else {
            mEndCardCloseView.setVisibility(View.VISIBLE);
        }
    }

    public void muteVideo() {
        mMuteState = !mMuteState;
        mAdController.setVolume(mMuteState);
        if (mMuteState) {
            mMuteView.setImageResource(R.drawable.mute);
        } else {
            mMuteView.setImageResource(R.drawable.unmute);
        }
    }

    public TextureView getTexture() {
        return mVideoPlayerLayoutTexture;
    }
}
