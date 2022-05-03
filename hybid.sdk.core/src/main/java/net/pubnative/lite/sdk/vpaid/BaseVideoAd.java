package net.pubnative.lite.sdk.vpaid;

import android.content.Context;

import net.pubnative.lite.sdk.models.Ad;
import net.pubnative.lite.sdk.presenter.AdPresenter;
import net.pubnative.lite.sdk.utils.Logger;
import net.pubnative.lite.sdk.vpaid.enums.AdState;
import net.pubnative.lite.sdk.vpaid.helpers.EventTracker;
import net.pubnative.lite.sdk.vpaid.helpers.FileLoader;
import net.pubnative.lite.sdk.vpaid.utils.FileUtils;
import net.pubnative.lite.sdk.vpaid.utils.Utils;

@SuppressWarnings("unused")
abstract class BaseVideoAd extends BaseVideoAdInternal {

    private static final String LOG_TAG = BaseVideoAd.class.getSimpleName();

    BaseVideoAd(Context context, Ad ad, boolean isInterstitial, boolean isFullscreen, AdPresenter.ImpressionListener impressionListener) throws Exception {
        super(context, ad, isInterstitial, isFullscreen, impressionListener);
    }

    public boolean isRewarded() {
        return super.isRewarded();
    }

    public void setRewarded(boolean isRewarded) {
        super.setRewarded(isRewarded);
    }

    /**
     * Stop ad processing and remove AdSpot from screen.
     */
    public abstract void dismiss();

    /**
     * Indicates whether ad content was loaded successfully and ready to be displayed.
     * After you initialized a `AdInterstitial`/`VideoAd` object and triggered the `load` method,
     * this property will be set to TRUE on it's successful completion.
     * It is set to FALSE when loaded ad content has expired or already was presented,
     * in this case it requires next `load` method triggering
     */
    public boolean isReady() {
        return super.isReady();
    }

    /**
     * Indicates whether `AdInterstitial`/`VideoAd` currently presented on screen.
     * Ad status will be set to `AdState.SHOWING` after trigger `show` method
     *
     * @return true - if ad presented on screen
     * false - if ad absent on screen
     */
    public boolean isShowing() {
        return getAdState() == AdState.SHOWING;
    }

    /**
     * Indicates whether `AdInterstitial`/`VideoAd` in "loading ad content" process.
     * Ad status will be set to `AdState.LOADING` after trigger `load` method
     *
     * @return true - if ad is loading now
     * false - if ad is not loading now
     */
    public boolean isLoading() {
        return getAdState() == AdState.LOADING;
    }

    /**
     * @param videoAdListener provides call back methods for AdSpot lifecycle
     */
    public void setAdListener(VideoAdListener videoAdListener) {
        super.setAdListener(videoAdListener);
    }

    /**
     * Defines, should use mobile network for caching video or not.
     * By default, video will not cache on mobile network (only on wi-fi)
     *
     * @param useMobile - true if need to cache video on mobile network,
     *                  false if need to cache video only on wi-fi network.
     */
    public void useMobileNetworkForCaching(boolean useMobile) {
        FileLoader.setUseMobileNetworkForCaching(useMobile);
    }

    /**
     * Use it for figure out any problems during integration process.
     * We recommend to set it "false" after full integration and testing.
     * <p>
     * If true - all debug logs will be in Logcat.
     * If false - only main info logs will be in Logcat.
     */
    public void setDebugMode(boolean mode) {
        Utils.setDebugMode(mode);
    }

    /**
     * Removes all video files from cache.
     */
    public void clearCache() {
        FileUtils.clearCache(getContext());
    }

    /**
     * Starts loading ad content process.
     * It is recommended triggering it in advance to have interstitial/banner ad ready and to be able to display instantly in your
     * application.
     * After its execution, the interstitial/banner notifies whether the loading of the ad content failed or succeeded.
     */
    public void load() {
        runOnUiThread(() -> {
            Logger.d(LOG_TAG, "Start loading ad");
            if (getAdState() == AdState.LOADING || getAdState() == AdState.SHOWING) {
                Logger.d(LOG_TAG, "Ad already loading or showing");
                return;
            }

            EventTracker.clear();

            setAdState(AdState.LOADING);

            initAdLoadingStartTime();
            startFetcherTimer();

            FileUtils.deleteExpiredFiles(getContext());

            if (isReady()) {
                Logger.d(LOG_TAG, "Ad already loaded");
                onAdLoadSuccessInternal();
                return;
            }

            if (Utils.isOnline(getContext())) {
                proceedLoad();
            } else {
                onAdLoadFailInternal(new PlayerInfo("No connection"));
            }
        });
    }

    /**
     * Destroy to clean-up resources if Ad no longer needed
     * If the Ad is in "loading ad" phase, destroy causes it's interrupting and cleaning-up all related resources
     * If the Ad is in "displaying ad" phase, destroy causes "closing ad" and cleaning-up all related resources
     */
    public void destroy() {
        Logger.d(LOG_TAG, "Ad will be destroyed");
        setReady();
        stopExpirationTimer();
        stopFetcherTimer();
        setAdState(AdState.NONE);
        cancelFetcher();
        releaseAdController();
        getViewabilityAdSession().stopAdSession();
    }
}
