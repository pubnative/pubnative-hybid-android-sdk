// HyBid SDK License
//
// https://github.com/pubnative/pubnative-hybid-android-sdk/blob/main/LICENSE
//
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
import net.pubnative.lite.sdk.vpaid.CloseButtonListener;
import net.pubnative.lite.sdk.vpaid.InvalidCTAUrlListener;
import net.pubnative.lite.sdk.vpaid.HyBidActivityInteractor;
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
    private FrameLayout mControlsLayout;
    private View mOpenUrlLayout;
    private View mUxLayout;
    private HyBidEndCardView mEndCardView;
    private HyBidEndCardView mLastCustomEndCardView;
    private HyBidCTAView ctaView;
    private boolean mMuteState;
    private final boolean mIsBrandAd;
    private final boolean mHasHiddenUx;
    private final boolean mIsFullscreen;
    private Surface mSurface;
    private View mSkipView;
    private ImageView mMuteView;
    private final Integer mRemoteEndCardCloseDelay;

    VideoVisibilityManager videoVisibilityManager;
    HyBidActivityInteractor interactor;
    AdCloseButtonListener mcloseButtonListener;

    private InterstitialActionBehaviour remoteConfigInterstitialClickBehaviour = null;
    private boolean mHasReducedCloseButton = false;
    private CustomCTAData mCustomCTAData = null;
    private Integer mCustomCTADelay = 0;

    public ViewControllerVast(VideoAdController adController,
                              boolean isFullscreen,
                              Integer endCardCloseDelay,
                              Boolean fullScreenClickability,
                              boolean reducedCloseButton,
                              AdCloseButtonListener adCloseButtonListener,
                              CustomCTAData customCTAData,
                              Integer customCTADelay,
                              boolean isBrandAd,
                              boolean hasHidenUx) {
        mAdController = adController;
        mIsFullscreen = isFullscreen;
        mcloseButtonListener = adCloseButtonListener;
        mCustomCTAData = customCTAData;
        mCustomCTADelay = customCTADelay;
        mIsBrandAd = isBrandAd;
        mHasHiddenUx = hasHidenUx;
        videoVisibilityManager = VideoVisibilityManager.getInstance();
        mRemoteEndCardCloseDelay = endCardCloseDelay;
        if (fullScreenClickability != null) {
            if (fullScreenClickability) {
                remoteConfigInterstitialClickBehaviour = InterstitialActionBehaviour.HB_CREATIVE;
            } else {
                remoteConfigInterstitialClickBehaviour = InterstitialActionBehaviour.HB_ACTION_BUTTON;
            }
        }
        mHasReducedCloseButton = reducedCloseButton;
        interactor = HyBidActivityInteractor.getInstance();
    }

    public void buildVideoAdView(VideoAdView bannerView) {
        Context context = bannerView.getContext();
        mBannerView = bannerView;
        bannerView.setVisibilityListener(mCreateVisibilityListener);
        bannerView.removeAllViews();

        mControlsLayout = (FrameLayout) LayoutInflater.from(context).inflate(R.layout.controls, bannerView, false);
        mOpenUrlLayout = LayoutInflater.from(context).inflate(R.layout.open_url, bannerView, false);
        mUxLayout = mControlsLayout.findViewById(R.id.uxLayout);

        initCustomCta(context);

        if (mIsFullscreen && mIsBrandAd) {
            if (mOpenUrlLayout != null) {
                mOpenUrlLayout.setVisibility(View.VISIBLE);
            }
            if (mHasHiddenUx) {
                mBannerView.setOnClickListener(v -> changeUxVisibility());
                mUxLayout.setVisibility(View.INVISIBLE);
            }
        } else {
            InterstitialActionBehaviour interstitialClickBehaviour;

            if (remoteConfigInterstitialClickBehaviour != null) {
                interstitialClickBehaviour = remoteConfigInterstitialClickBehaviour;
            } else {
                interstitialClickBehaviour = INTERSTITIAL_CLICK_BEHAVIOUR_DEFAULT;
            }

            if (mCustomCTAData != null && mIsFullscreen) {
                if (mOpenUrlLayout != null) {
                    mOpenUrlLayout.setVisibility(View.GONE);
                }
                mBannerView.setOnClickListener(v -> validateOpenURLClicked(false));
                showCTAButton(mCustomCTAData, mCustomCTADelay,
                        () -> showHideLearnMore(interstitialClickBehaviour));
            } else {
                showHideLearnMore(interstitialClickBehaviour);
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

        mEndCardView = new HyBidEndCardView(context, mHasReducedCloseButton);
        mEndCardView.setLayoutParams(new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        mEndCardView.setVisibility(View.GONE);

        mLastCustomEndCardView = new HyBidEndCardView(context, mHasReducedCloseButton);
        mLastCustomEndCardView.setLayoutParams(new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        mLastCustomEndCardView.setVisibility(View.GONE);

        mOpenUrlLayout.findViewById(R.id.openURL).setOnClickListener(this);

        mSkipCountdownView = new CountDownViewFactory().createCountdownView(context, COUNTDOWN_STYLE_DEFAULT, mControlsLayout);

        mControlsLayout.addView(mSkipCountdownView);
        mLinearCountdownView = mControlsLayout.findViewById(R.id.linear_count_down);
        if (mVideoPlayerLayoutTexture != null) {
            mVideoPlayerLayoutTexture.setSurfaceTextureListener(mCreateTextureListener);
        }

        mMuteView = mControlsLayout.findViewById(R.id.muteView);
        mMuteView.setOnClickListener(this);

        if (mIsBrandAd && mHasHiddenUx) {
            if (mMuteView != null) {
                mMuteView.setVisibility(View.INVISIBLE);
            }
            hideCountdown(true);
            hideMute(true);
        }

        mSkipView = mControlsLayout.findViewById(R.id.skipView);
        if (mHasReducedCloseButton) {
            int reducedSkipSize = (int) ViewUtils.convertDpToPixel(20f, context);
            int margins = (int) ViewUtils.convertDpToPixel(8f, context);
            FrameLayout.LayoutParams layoutParams = new FrameLayout.LayoutParams(reducedSkipSize, reducedSkipSize);
            layoutParams.setMargins(margins, margins, 0, 0);
            mSkipView.setId(R.id.skipView_small);
            mSkipView.setLayoutParams(layoutParams);
            mSkipView.setPadding(0, 0, 0, 0);
            mSkipView.requestLayout();
        }
        Bitmap skipBitmap = BitmapHelper.toBitmap(mSkipView.getContext(), HyBid.getSkipXmlResource(), R.mipmap.skip);
        if (skipBitmap != null) {
            ((ImageView) mSkipView).setImageBitmap(skipBitmap);
        } else {
            ((ImageView) mSkipView).setImageBitmap(BitmapHelper.decodeResource(mSkipView.getContext(), R.mipmap.skip));
        }

        mSkipView.setOnClickListener(this);

        mAdController.addViewabilityFriendlyObstruction(mControlsLayout, FriendlyObstructionPurpose.VIDEO_CONTROLS, "Video controls");

        bannerView.addView(mControlsLayout);
        bannerView.addView(mEndCardView);
        bannerView.addView(mLastCustomEndCardView);
        bannerView.addView(ctaView);
        bannerView.addView(mOpenUrlLayout);
    }

    private void showHideLearnMore(InterstitialActionBehaviour interstitialClickBehaviour) {
        if (interstitialClickBehaviour == InterstitialActionBehaviour.HB_CREATIVE) {
            mBannerView.setOnClickListener(v -> validateOpenURLClicked(false));
            if (mOpenUrlLayout != null) {
                mOpenUrlLayout.setVisibility(View.GONE);
            }
        } else {
            if (mOpenUrlLayout != null) {
                mOpenUrlLayout.setVisibility(View.VISIBLE);
            }
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

    private void validateEndCardOpenURLClicked(String url) {
        mAdController.openUrl(url, false, false);
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
        FrameLayout.LayoutParams oldParams = (FrameLayout.LayoutParams) mVideoPlayerLayout.getLayoutParams();
        ViewGroup.LayoutParams newParams = Utils.calculateNewLayoutParams(oldParams, width, height, mBannerView.getWidth(), mBannerView.getHeight(), Utils.StretchOption.NO_STRETCH);
        mVideoPlayerLayout.setLayoutParams(newParams);
        mVideoPlayerLayout.addOnLayoutChangeListener((v, left, top, right, bottom, oldLeft, oldTop, oldRight, oldBottom) -> {
            mVideoPlayerLayout.post(() -> {
                FrameLayout.LayoutParams oldParams1 = (FrameLayout.LayoutParams) mVideoPlayerLayout.getLayoutParams();
                ViewGroup.LayoutParams newParams1 = Utils.calculateNewLayoutParams(oldParams1, width, height, mBannerView.getWidth(), mBannerView.getHeight(), Utils.StretchOption.NO_STRETCH);
                mVideoPlayerLayout.setLayoutParams(newParams1);
            });
        });
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

    public void showEndCard(EndCardData endCardData,
                            String imageUri,
                            Boolean isLastEndCard,
                            CloseButtonListener closeButtonListener) {
        if (mEndCardView != null) {
            mEndCardView.setEndCardViewListener(new HyBidEndCardView.EndCardViewListener() {
                @Override
                public void onClick(String url, Boolean isCustomEndCard, String endCardType) {
                    if (isCustomEndCard) {
                        validateCustomEndCardOpenURLClicked();
                        mAdController.onCustomEndCardClick(endCardType);
                    } else {
                        validateEndCardOpenURLClicked(url);
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
                    if (mOpenUrlLayout != null) {
                        mOpenUrlLayout.setVisibility(View.GONE);
                    }
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
            mEndCardView.setSkipOffset(endCardCloseDelay);
            mEndCardView.show(endCardData, imageUri);

            mEndCardView.addOnLayoutChangeListener((v, left, top, right, bottom, oldLeft, oldTop, oldRight, oldBottom) -> {
                mEndCardView.post(() -> mEndCardView.setLayoutParams(new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)));
            });

            if (mIsFullscreen) {
                if (isLastEndCard) {
                    mEndCardView.showCloseButton(closeButtonListener);
                } else {
                    mEndCardView.showSkipButton();
                }
            }
        }
    }

    public void showLastCustomEndCard(EndCardData endCardData,
                                      String imageUri,
                                      CloseButtonListener closeButtonListener) {
        if (mLastCustomEndCardView != null) {
            mLastCustomEndCardView.setEndCardViewListener(new HyBidEndCardView.EndCardViewListener() {
                @Override
                public void onClick(String url, Boolean isCustomEndCard, String endCardType) {
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
                    if (mOpenUrlLayout != null) {
                        mOpenUrlLayout.setVisibility(View.GONE);
                    }
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
            mLastCustomEndCardView.setSkipOffset(endCardCloseDelay);
            mEndCardView.hideSkipButton();
            mLastCustomEndCardView.show(endCardData, imageUri);
            mLastCustomEndCardView.addOnLayoutChangeListener((v, left, top, right, bottom, oldLeft, oldTop, oldRight, oldBottom) -> {
                mLastCustomEndCardView.post(() -> mLastCustomEndCardView.setLayoutParams(new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)));
            });
            if (mIsFullscreen) {
                mLastCustomEndCardView.showCloseButton(closeButtonListener);
            }
        }
    }

    public void showCTAButton(CustomCTAData data, Integer delay, InvalidCTAUrlListener listener) {

        if (ctaView == null || TextUtils.isEmpty(data.getIconURL())) {
            if (mAdController != null) {
                mAdController.onCustomCTALoadFail();
            }
            return;
        }
        ctaView.setListener(new HyBidCTAView.CTAViewListener() {
            @Override
            public void onClick() {
                if (mAdController != null) {
                    mAdController.onCustomCTAClick(isEndCard());
                }
                validateOpenURLClicked(true);
            }

            @Override
            public void onShow() {
                if (mAdController != null) {
                    mAdController.onCustomCTAShow();
                }
            }

            @Override
            public void onFail() {
                if (mAdController != null) {
                    mAdController.onCustomCTALoadFail();
                }
            }

            @Override
            public void onInvalidCTAIconUrl() {
                if (mAdController != null) {
                    mAdController.onCustomCTALoadFail();
                }
                if (listener != null) {
                    listener.invalidCTAUrl();
                }
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
            if (mIsBrandAd) {
                if (mUxLayout.getVisibility() == View.VISIBLE) {
                    mSkipView.setVisibility(View.VISIBLE);
                    mSkipView.setClickable(true);
                } else {
                    mSkipView.setVisibility(View.INVISIBLE);
                    mSkipView.setClickable(false);
                }
            } else {
                mSkipView.setVisibility(View.VISIBLE);
                mSkipView.setClickable(true);
            }
        }
    }

    private void hideMute(boolean hide) {
        if (mMuteView != null) {
            if (mMuteView.getVisibility() == View.GONE) {
                return;
            }
            if (hide) {
                mMuteView.setVisibility(View.INVISIBLE);
            } else {
                mMuteView.setVisibility(View.VISIBLE);
            }
        }
    }

    private void hideSkip(boolean hide) {
        if (mSkipView != null) {
            if (mSkipView.getVisibility() == View.GONE) {
                return;
            }
            if (hide) {
                mSkipView.setVisibility(View.INVISIBLE);
                mSkipView.setClickable(false);
            } else {
                mSkipView.setVisibility(View.VISIBLE);
                mSkipView.setClickable(true);
            }
        }
    }

    public void showCloseButton() {
        if (mcloseButtonListener != null) {
            mcloseButtonListener.showButton();
        }
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
        } else if (v.getId() == R.id.skipView || v.getId() == R.id.progressSkipView ||
                v.getId() == R.id.skipView_small) {
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

//    private void replayVideo() {
//        mEndCardView.hide();
//        mVideoPlayerLayout.setVisibility(View.VISIBLE);
//        mAdController.playAd();
//    }

    public void pauseEndCardCloseButtonTimer() {
        if (mEndCardView != null) {
            mEndCardView.pause();
        }
        if (mLastCustomEndCardView != null) {
            mLastCustomEndCardView.pause();
        }
    }

    public void pause() {
        if (ctaView != null) {
            ctaView.pause();
        }
    }

    public void resume() {
        if (ctaView != null) {
            ctaView.resume();
        }
    }

    public void resumeEndCardCloseButtonTimer() {
        if (mEndCardView != null) {
            mEndCardView.resume();
        }
        if (mLastCustomEndCardView != null) {
            mLastCustomEndCardView.resume();
        }
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

    private void hideCountdown(boolean hide) {
        if (mSkipCountdownView != null) {
            if (mSkipCountdownView.getVisibility() == View.GONE) {
                return;
            }
            if (hide) {
                mSkipCountdownView.setVisibility(View.INVISIBLE);
            } else {
                mSkipCountdownView.setVisibility(View.VISIBLE);
            }
        }
    }

    private void changeUxVisibility() {
        if (mUxLayout != null) {
            if (mUxLayout.getVisibility() == View.VISIBLE) {
                mUxLayout.setVisibility(View.INVISIBLE);
                hideCountdown(true);
                hideMute(true);
                hideSkip(true);
            } else {
                mUxLayout.setVisibility(View.VISIBLE);
                hideCountdown(false);
                hideMute(false);
                hideSkip(false);
            }
        }
    }
}