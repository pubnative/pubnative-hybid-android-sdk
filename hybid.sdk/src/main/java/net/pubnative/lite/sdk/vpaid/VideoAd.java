// HyBid SDK License
//
// https://github.com/pubnative/pubnative-hybid-android-sdk/blob/main/LICENSE
//
package net.pubnative.lite.sdk.vpaid;

import static net.pubnative.lite.sdk.vpaid.utils.Utils.isPhoneMuted;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.view.View;

import net.pubnative.lite.sdk.models.Ad;
import net.pubnative.lite.sdk.presenter.AdPresenter;
import net.pubnative.lite.sdk.utils.AdAudioStateManager;
import net.pubnative.lite.sdk.utils.Logger;
import net.pubnative.lite.sdk.viewability.HyBidViewabilityFriendlyObstruction;
import net.pubnative.lite.sdk.vpaid.enums.AdFormat;
import net.pubnative.lite.sdk.vpaid.enums.AdState;
import net.pubnative.lite.sdk.vpaid.enums.AudioState;
import net.pubnative.lite.sdk.vpaid.models.vpaid.AdSpotDimensions;

public class VideoAd extends BaseVideoAd {

    private static final String LOG_TAG = VideoAd.class.getSimpleName();

    private volatile VideoAdView mBannerView;

    private boolean mIsAdStarted = false;

    public VideoAd(Context context, Ad ad, boolean isInterstitial, boolean isFullscreen,
                   AdPresenter.ImpressionListener impressionListener) throws Exception {
        super(context, ad, isInterstitial, isFullscreen, impressionListener, null);
    }

    public VideoAd(Context context, Ad ad, boolean isInterstitial, boolean isFullscreen,
                   AdPresenter.ImpressionListener impressionListener,
                   AdCloseButtonListener adCloseButtonListener) throws Exception {
        super(context, ad, isInterstitial, isFullscreen, impressionListener, adCloseButtonListener);
    }

    @Override
    int getAdFormat() {
        return AdFormat.BANNER;
    }

    @Override
    AdSpotDimensions getAdSpotDimensions() {
        if (mBannerView != null) {
            return new AdSpotDimensions(mBannerView.getWidth(), mBannerView.getHeight());
        }
        return null;
    }

    public void bindView(VideoAdView bannerView) {
        if (bannerView != null) {
            Logger.d(LOG_TAG, "Bind view (visibility: " + bannerView.getVisibility() + ")");
            mBannerView = bannerView;
        } else {
            Logger.e(LOG_TAG, "Bind view is null");
        }
    }

    public void show() {

        Handler handler = new Handler(Looper.getMainLooper());

        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        showAd();
                    }

                    private void showAd() {
                        Logger.d(LOG_TAG, "Banner did start showing ad");
                        if (getAdState() == AdState.SHOWING) {
                            Logger.d(LOG_TAG, "Banner already displays on screen");
                            return;
                        }
                        if (isReady() && mBannerView != null) {
                            setAdState(AdState.SHOWING);
                            stopExpirationTimer();

                            if (getAdController() != null) {
                                synchronized (this) {
                                    if (getAdController() != null && getAdController().getAdParams() != null) {
                                        getViewabilityAdSession().initAdSession(mBannerView, getAdController().getAdParams().getVerificationScriptResources());

                                        getAdController().buildVideoAdView(mBannerView);

                                        for (HyBidViewabilityFriendlyObstruction obstruction : getAdController().getViewabilityFriendlyObstructions()) {
                                            getViewabilityAdSession().addFriendlyObstruction(
                                                    obstruction.getView(),
                                                    obstruction.getPurpose(),
                                                    obstruction.getReason());
                                        }

                                        getViewabilityAdSession().fireLoaded();
                                        getAdController().playAd();

                                        validateAudioState();
                                    }
                                }
                            } else {
                                Logger.e(LOG_TAG, "getAdController() is null and can not set attributes to banner view ");
                                if (getAdListener() != null) {
                                    PlayerInfo info = new PlayerInfo("getAdController() is null and can not set attributes to banner view ");
                                    getAdListener().onAdLoadFail(info);
                                }
                            }
                        } else {
                            Logger.e(LOG_TAG, "Banner is not ready");
                        }

                        mIsAdStarted = true;
                    }
                });
            }
        }, 500);
    }

    private void validateAudioState() {
        boolean isMuted = false;
        AudioState audioState = AdAudioStateManager.getAudioState(getAd(), isFullscreen);

        switch (audioState) {
            case DEFAULT:
            case MUTED:
                isMuted = true;
                break;
            case ON:
                if (isPhoneMuted(getContext())) {
                    isMuted = true;
                }
                break;
        }

        if (isMuted) {
            getAdController().toggleMute();
        }
    }

    /**
     * Triggered when the banner ad disappears on the screen
     */
    private void onBannerHide() {
        Logger.d(LOG_TAG, "Ad disappeared from screen");
        setReady();
        setAdState(AdState.NONE);
        if (getAdListener() != null) {
            getAdListener().onAdDismissed(getAdController().getProgress());
        }
    }

    /**
     * Dismisses a banner ad
     * This method dismisses a banner ad and only if it is currently presented.
     * NOTE: should be called from UI thread
     * <p>
     * After it banner ad requires "loading process" to be ready for displaying
     * <p>
     * As a result you'll receive onAdDismissed() notification
     * <p>
     * NOTE: should be triggered from UI thread
     */
    @Override
    public void dismiss() {
        runOnUiThread(() -> {
            Logger.d(LOG_TAG, "Video will be dismissed");
            if (getAdState() == AdState.SHOWING) {
                if (mBannerView != null) {
                    mBannerView.setVisibility(View.GONE);
                    mBannerView.removeAllViews();
                }
                if (getAdController() != null) {
                    getAdController().dismiss();
                }
                onBannerHide();
            } else {
                Logger.e(LOG_TAG, "Can't dismiss ad, it's not displaying");
            }
        });
    }

    public void resume() {
        if (getAdController() != null && isReady()) {
            getAdController().resume();
        }
    }

    public void resumeEndCardCloseButtonTimer() {
        Logger.d(LOG_TAG, "resume End Card Timer");
        if (getAdController() != null) {
            getAdController().resumeEndCardCloseButtonTimer();
        }
    }

    public void pauseEndCardCloseButtonTimer() {
        Logger.d(LOG_TAG, "pause End Card Timer");
        if (getAdController() != null) {
            getAdController().pauseEndCardCloseButtonTimer();
        }
    }

    /**
     * Pauses video ad
     * Needs to be triggered on appropriate Activity life-cycle method "onPause()".
     */
    public void pause() {
        if (getAdController() != null) {
            getAdController().pause();
        }
    }

    public boolean isAdStarted() {
        return mIsAdStarted;
    }

    public void skip() {
        getAdController().skipVideo();
    }

    public void closeVideo() {
        getAdController().closeSelf();
    }

    public void onVolumeChanged() {
        if (getAdController() != null) {
            getAdController().onVolumeChanged();
        }
    }
}
