package net.pubnative.lite.sdk.vpaid.vast;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Point;
import android.graphics.SurfaceTexture;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Surface;
import android.view.TextureView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.iab.omid.library.pubnativenet.adsession.FriendlyObstructionPurpose;

import net.pubnative.lite.sdk.CountdownStyle;
import net.pubnative.lite.sdk.HyBid;
import net.pubnative.lite.sdk.InterstitialActionBehaviour;
import net.pubnative.lite.sdk.core.R;
import net.pubnative.lite.sdk.models.CustomCTAData;
import net.pubnative.lite.sdk.models.EndCardData;
import net.pubnative.lite.sdk.models.SkipOffset;
import net.pubnative.lite.sdk.utils.Logger;
import net.pubnative.lite.sdk.utils.ScreenDimensionsUtils;
import net.pubnative.lite.sdk.utils.SkipOffsetManager;
import net.pubnative.lite.sdk.utils.ViewUtils;
import net.pubnative.lite.sdk.views.cta.HyBidCTAView;
import net.pubnative.lite.sdk.views.endcard.HyBidEndCardView;
import net.pubnative.lite.sdk.vpaid.AdCloseButtonListener;
import net.pubnative.lite.sdk.vpaid.BackButtonClickabilityListener;
import net.pubnative.lite.sdk.vpaid.CloseButtonListener;
import net.pubnative.lite.sdk.vpaid.VastActivityInteractor;
import net.pubnative.lite.sdk.vpaid.VideoAdController;
import net.pubnative.lite.sdk.vpaid.VideoAdView;
import net.pubnative.lite.sdk.vpaid.VideoVisibilityManager;
import net.pubnative.lite.sdk.vpaid.helpers.BitmapHelper;
import net.pubnative.lite.sdk.vpaid.response.AdParams;
import net.pubnative.lite.sdk.vpaid.utils.Utils;
import net.pubnative.lite.sdk.vpaid.widget.CountDownView;
import net.pubnative.lite.sdk.vpaid.widget.CountDownViewFactory;
import net.pubnative.lite.sdk.vpaid.widget.LinearCountDownView;

public class ViewControllerVast implements View.OnClickListener {
    private static final String LOG_TAG = ViewControllerVast.class.getSimpleName();
    private static final CountdownStyle COUNTDOWN_STYLE_DEFAULT = CountdownStyle.PIE_CHART;
    private static final InterstitialActionBehaviour INTERSTITIAL_CLICK_BEHAVIOUR_DEFAULT = InterstitialActionBehaviour.HB_CREATIVE;

    private final VideoAdController mAdController;

    private VideoAdView mBannerView;
    private CountDownView mSkipCountdownView;
    private LinearCountDownView mLinearCountdownView;
    private FrameLayout mVideoPlayerLayout;
    private TextureView mVideoPlayerLayoutTexture;
    private View mControlsLayout;
    private View mOpenUrlLayout;
    private HyBidEndCardView mEndCardView;
    private HyBidEndCardView mLastCustomEndCardView;
    private HyBidCTAView ctaView;
    private boolean mMuteState;
    private final boolean mIsFullscreen;
    private Surface mSurface;
    private View mSkipView;
    private ImageView mMuteView;
    private final Integer mRemoteEndCardCloseDelay;
    private final Integer mRemoteBackButtonDelay;

    VideoVisibilityManager videoVisibilityManager;
    VastActivityInteractor interactor;
    AdCloseButtonListener mcloseButtonListener;

    private InterstitialActionBehaviour remoteConfigInterstitialClickBehaviour = null;
    private CustomCTAData mCustomCTAData = null;
    private Integer mCustomCTADelay = 0;

    public ViewControllerVast(VideoAdController adController, boolean isFullscreen, Integer endCardCloseDelay, Integer backButtonDelay, Boolean fullScreenClickability, AdCloseButtonListener adCloseButtonListener, CustomCTAData customCTAData, Integer customCTADelay) {
        mAdController = adController;
        mIsFullscreen = isFullscreen;
        mcloseButtonListener = adCloseButtonListener;
        mCustomCTAData = customCTAData;
        mCustomCTADelay = customCTADelay;
        videoVisibilityManager = VideoVisibilityManager.getInstance();
        mRemoteEndCardCloseDelay = endCardCloseDelay;
        mRemoteBackButtonDelay = backButtonDelay;
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
            mOpenUrlLayout = LayoutInflater.from(context).inflate(R.layout.open_url, bannerView, false);

            initCustomCta(context);

            InterstitialActionBehaviour interstitialClickBehaviour = INTERSTITIAL_CLICK_BEHAVIOUR_DEFAULT;
            if (remoteConfigInterstitialClickBehaviour != null)
                interstitialClickBehaviour = remoteConfigInterstitialClickBehaviour;

            if (mCustomCTAData != null && mIsFullscreen) {
                mBannerView.setOnClickListener(v -> validateOpenURLClicked(false));
                if (mOpenUrlLayout != null) mOpenUrlLayout.setVisibility(View.GONE);
                showCTAButton(mCustomCTAData, mCustomCTADelay);
            } else {
                if (interstitialClickBehaviour == InterstitialActionBehaviour.HB_CREATIVE) {
                    mBannerView.setOnClickListener(v -> validateOpenURLClicked(false));
                    if (mOpenUrlLayout != null) mOpenUrlLayout.setVisibility(View.GONE);
                } else {
                    if (mOpenUrlLayout != null) mOpenUrlLayout.setVisibility(View.VISIBLE);
                }
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

            mLastCustomEndCardView = new HyBidEndCardView(context);
            mLastCustomEndCardView.setLayoutParams(new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
            mLastCustomEndCardView.setVisibility(View.GONE);

            mOpenUrlLayout.findViewById(R.id.openURL).setOnClickListener(this);

            mSkipCountdownView = new CountDownViewFactory().createCountdownView(context, COUNTDOWN_STYLE_DEFAULT, mVideoPlayerLayout);
            mVideoPlayerLayout.addView(mSkipCountdownView);
            mLinearCountdownView = mControlsLayout.findViewById(R.id.linear_count_down);
            if (mVideoPlayerLayoutTexture != null) {
                mVideoPlayerLayoutTexture.setSurfaceTextureListener(mCreateTextureListener);
            }

            mMuteView = mControlsLayout.findViewById(R.id.muteView);
            mMuteView.setOnClickListener(this);

            mSkipView = mControlsLayout.findViewById(R.id.skipView);
            Bitmap skipBitmap = BitmapHelper.toBitmap(mSkipView.getContext(), HyBid.getSkipXmlResource(), R.mipmap.skip);
            if (skipBitmap != null) ((ImageView) mSkipView).setImageBitmap(skipBitmap);
            else
                ((ImageView) mSkipView).setImageBitmap(BitmapHelper.decodeResource(mSkipView.getContext(), R.mipmap.skip));

            mSkipView.setOnClickListener(this);

            mAdController.addViewabilityFriendlyObstruction(mControlsLayout, FriendlyObstructionPurpose.VIDEO_CONTROLS, "Video controls");

            bannerView.addView(mControlsLayout);
            bannerView.addView(mEndCardView);
            bannerView.addView(mLastCustomEndCardView);
            bannerView.addView(ctaView);
            bannerView.addView(mOpenUrlLayout);
        }
    }

    private void initCustomCta(Context context) {
        ctaView = new HyBidCTAView(context);
        FrameLayout.LayoutParams ctaLp = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        int hMargin = ViewUtils.asIntPixels(6, context);
        ScreenDimensionsUtils screenDimensionsUtils = new ScreenDimensionsUtils();
        Point point = screenDimensionsUtils.getScreenDimensionsToPoint(context);
        int vMargin = point.y * 10 / 100;
        ctaLp.setMargins(hMargin, vMargin, hMargin, vMargin);
        ctaLp.gravity = Gravity.BOTTOM | Gravity.RIGHT;
        ctaView.setLayoutParams(ctaLp);
        ctaView.setContentDescription("ctaView");
    }

    private boolean hasCTAExtension(AdParams adParams) {
        return adParams != null && !TextUtils.isEmpty(adParams.getCtaExtensionHtml());
    }

    private void validateOpenURLClicked(Boolean isCTAClick) {
        mAdController.getViewabilityAdSession().fireClick();
        mAdController.openUrl(null, false, isCTAClick);
    }

    private void validateEndCardOpenURLClicked() {
        mAdController.openUrl(null, false, false);
    }

    private void validateCustomEndCardOpenURLClicked() {
        mAdController.openUrl(null, true, false);
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

    public void endSkip(Boolean isAutoClose, Boolean hasEndcard) {
        if (mSkipCountdownView != null) {
            mSkipCountdownView.setVisibility(View.GONE);
            if (isAutoClose) {
                showCloseButton();
            } else if (!hasEndcard) {
                showCloseButton();
            } else {
                showSkipButton();
            }
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

    public void showEndCard(EndCardData endCardData, String imageUri, Boolean isLastEndCard, CloseButtonListener closeButtonListener, BackButtonClickabilityListener backButtonClickabilityListener) {
        if (mEndCardView != null) {
            mEndCardView.setEndCardViewListener(new HyBidEndCardView.EndCardViewListener() {
                @Override
                public void onClick(Boolean isCustomEndCard, String endCardType) {
                    if (isCustomEndCard) {
                        validateCustomEndCardOpenURLClicked();
                        mAdController.onCustomEndCardClick(endCardType);
                    } else {
                        validateEndCardOpenURLClicked();
                        mAdController.onDefaultEndCardClick(endCardType);
                    }
                }

                @Override
                public void onSkip() {
                    skipEndCard();
                }

                @Override
                public void onClose(Boolean isCustomEndCard) {
                    if (mAdController != null) {
                        mAdController.onEndCardClosed(isCustomEndCard);
                    }
                    closeSelf();
                }

                @Override
                public void onShow(Boolean isCustomEndCard, String endCardType) {
                    if (mOpenUrlLayout != null) mOpenUrlLayout.setVisibility(View.GONE);
                    if (isCustomEndCard) {
                        mAdController.onCustomEndCardShow(endCardType);
                        mEndCardView.bringToFront();
                        if (ctaView != null) {
                            ctaView.hide();
                        }
                    } else {
                        mAdController.onDefaultEndCardShow(endCardType);
                        if (ctaView != null) {
                            ctaView.show();
                        }
                    }
                }

                @Override
                public void onLoadSuccess(Boolean isCustomEndCard) {
                    if (mAdController != null) {
                        mAdController.onEndCardLoadSuccess(isCustomEndCard);
                    }
                }

                @Override
                public void onLoadFail(Boolean isCustomEndCard) {
                    if (mAdController != null) {
                        mAdController.onEndCardLoadFail(isCustomEndCard);
                    }
                }
            });
            SkipOffset endCardCloseDelay = getEndCardCloseDelay();
            SkipOffset backButtonDelay = getBackButtonDelay();
            mEndCardView.setSkipOffset(endCardCloseDelay);
            mEndCardView.setBackButtonSkipOffset(backButtonDelay);
            mEndCardView.show(endCardData, imageUri);
            if (mIsFullscreen) {
                if (isLastEndCard) {
                    mEndCardView.showCloseButton(closeButtonListener, backButtonClickabilityListener);
                } else {
                    mEndCardView.showSkipButton();
                }
            }
        }
    }

    public void showLastCustomEndCard(EndCardData endCardData, String imageUri, CloseButtonListener closeButtonListener, BackButtonClickabilityListener backButtonClickabilityListener) {
        if (mLastCustomEndCardView != null) {
            mLastCustomEndCardView.setEndCardViewListener(new HyBidEndCardView.EndCardViewListener() {
                @Override
                public void onClick(Boolean isCustomEndCard, String endCardType) {
                    validateCustomEndCardOpenURLClicked();
                    if (isCustomEndCard) {
                        mAdController.onCustomEndCardClick(endCardType);
                    } else {
                        mAdController.onDefaultEndCardClick(endCardType);
                    }
                }

                @Override
                public void onSkip() {
                    skipEndCard();
                }

                @Override
                public void onClose(Boolean isCustomEndCard) {
                    if (mAdController != null) {
                        mAdController.onEndCardClosed(isCustomEndCard);
                    }
                    closeSelf();
                }

                @Override
                public void onShow(Boolean isCustomEndCard, String endCardType) {
                    if (mOpenUrlLayout != null) mOpenUrlLayout.setVisibility(View.GONE);
                    if (isCustomEndCard) {
                        mAdController.onCustomEndCardShow(endCardType);
                        mLastCustomEndCardView.bringToFront();
                        if (ctaView != null) {
                            ctaView.hide();
                        }
                    } else {
                        mAdController.onDefaultEndCardShow(endCardType);
                        if (ctaView != null) {
                            ctaView.show();
                        }
                    }
                }

                @Override
                public void onLoadSuccess(Boolean isCustomEndCard) {
                    if (mAdController != null) {
                        mAdController.onEndCardLoadSuccess(isCustomEndCard);
                    }
                }

                @Override
                public void onLoadFail(Boolean isCustomEndCard) {
                    if (mAdController != null) {
                        mAdController.onEndCardLoadFail(isCustomEndCard);
                    }
                }
            });
            SkipOffset endCardCloseDelay = getEndCardCloseDelay();
            SkipOffset backButtonDelay = getBackButtonDelay();
            mLastCustomEndCardView.setSkipOffset(endCardCloseDelay);
            mLastCustomEndCardView.setBackButtonSkipOffset(backButtonDelay);
            mEndCardView.hideSkipButton();
            mLastCustomEndCardView.show(endCardData, imageUri);
            if (mIsFullscreen) {
                mLastCustomEndCardView.showCloseButton(closeButtonListener, backButtonClickabilityListener);
            }
        }
    }

    public void showCTAButton(CustomCTAData data, Integer delay) {

        if (ctaView == null || TextUtils.isEmpty(data.getIconURL())) {
            if (mAdController != null) mAdController.onCustomCTALoadFail();
            return;
        }
        ctaView.setListener(new HyBidCTAView.CTAViewListener() {
            @Override
            public void onClick() {
                if (mAdController != null)
                    mAdController.onCustomCTAClick(isEndCard());
                validateOpenURLClicked(true);
            }

            @Override
            public void onShow() {
                if (mAdController != null) mAdController.onCustomCTAShow();
            }

            @Override
            public void onFail() {
                if (mAdController != null) mAdController.onCustomCTALoadFail();
            }
        });

        if (data.getBitmap() != null) {
            ctaView.show(data.getBitmap(), data.getLabel(), delay);
        } else {
            ctaView.show(data.getIconURL(), data.getLabel(), delay);
        }
    }

    public void showSkipButton() {
        if (mSkipView != null) {
            mSkipView.setVisibility(View.VISIBLE);
            mSkipView.setClickable(true);
        }
    }

    public void showCloseButton() {
        if (mcloseButtonListener != null) {
            mcloseButtonListener.showButton();
        }
    }

    public void hideCloseButton() {
        if (mcloseButtonListener != null) {
            mcloseButtonListener.hideButton();
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
        return mEndCardView != null && mEndCardView.getVisibility() != View.GONE;
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
        if (ctaView != null) {
            ctaView.destroy();
        }
        if (mLastCustomEndCardView != null) {
            mLastCustomEndCardView.destroy();
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
            validateOpenURLClicked(false);
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
        if (mEndCardView != null)
            mEndCardView.pause();
        if (mLastCustomEndCardView != null)
            mLastCustomEndCardView.pause();
    }

    public void pause() {
        if (ctaView != null)
            ctaView.pause();
    }

    public void resume() {
        if (ctaView != null)
            ctaView.resume();
    }

    public void resumeEndCardCloseButtonTimer() {
        if (mEndCardView != null)
            mEndCardView.resume();
        if (mLastCustomEndCardView != null)
            mLastCustomEndCardView.resume();
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
            if (mRemoteEndCardCloseDelay > SkipOffsetManager.getMaximumEndcardCloseDelay()) {
                return new SkipOffset(SkipOffsetManager.getMaximumEndcardCloseDelay(), true);
            } else {
                return new SkipOffset(mRemoteEndCardCloseDelay, true);
            }
        } else {
            return new SkipOffset(SkipOffsetManager.getDefaultEndcardSkipOffset(), false);
        }
    }

    private SkipOffset getBackButtonDelay() {
        if (mRemoteBackButtonDelay != null) {
            if (mRemoteBackButtonDelay > SkipOffsetManager.getMaximumBackButtonDelay()) {
                return new SkipOffset(SkipOffsetManager.getMaximumBackButtonDelay(), true);
            } else {
                return new SkipOffset(mRemoteBackButtonDelay, true);
            }
        } else {
            return new SkipOffset(SkipOffsetManager.getDefaultBackButtonDelay(), false);
        }
    }
}
