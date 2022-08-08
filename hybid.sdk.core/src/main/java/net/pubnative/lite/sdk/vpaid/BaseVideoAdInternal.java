package net.pubnative.lite.sdk.vpaid;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;

import net.pubnative.lite.sdk.HyBid;
import net.pubnative.lite.sdk.HyBidError;
import net.pubnative.lite.sdk.HyBidErrorCode;
import net.pubnative.lite.sdk.models.Ad;
import net.pubnative.lite.sdk.presenter.AdPresenter;
import net.pubnative.lite.sdk.utils.Logger;
import net.pubnative.lite.sdk.viewability.HyBidViewabilityNativeVideoAdSession;
import net.pubnative.lite.sdk.vpaid.enums.AdState;
import net.pubnative.lite.sdk.vpaid.enums.VastError;
import net.pubnative.lite.sdk.vpaid.helpers.AssetsLoader;
import net.pubnative.lite.sdk.vpaid.helpers.ErrorLog;
import net.pubnative.lite.sdk.vpaid.helpers.SimpleTimer;
import net.pubnative.lite.sdk.vpaid.models.EndCardData;
import net.pubnative.lite.sdk.vpaid.models.vpaid.AdSpotDimensions;
import net.pubnative.lite.sdk.vpaid.response.AdParams;
import net.pubnative.lite.sdk.vpaid.response.VastProcessor;

abstract class BaseVideoAdInternal {

    private static final String LOG_TAG = BaseVideoAdInternal.class.getSimpleName();

    private final Context mContext;
    private final AssetsLoader mAssetsLoader;
    private final Handler mHandler = new Handler(Looper.getMainLooper());

    private int mAdState;
    private boolean mIsReady;
    private boolean mIsRewarded = false;
    private VideoAdListener mVideoAdListener;
    private long mAdLoadingStartTime;
    private SimpleTimer mExpirationTimer;
    private VideoAdController mAdController;
    private SimpleTimer mFetcherTimer;
    private SimpleTimer mPrepareTimer;
    private final String mVastData;
    private Ad mAd;

    private final boolean isInterstitial;
    private final boolean isFullscreen;

    private VideoAdCacheItem mCacheItem;

    private final HyBidViewabilityNativeVideoAdSession mViewabilityAdSession;

    AdPresenter.ImpressionListener mImpressionListener;

    BaseVideoAdInternal(Context context, Ad ad, boolean isInterstitial, boolean isFullscreen, AdPresenter.ImpressionListener impressionListener) throws Exception {
        String data = ad.getVast();
        if (context == null || TextUtils.isEmpty(data)) {
            throw new HyBidError(HyBidErrorCode.VAST_PLAYER_ERROR);
        }
        mAd = ad;
        mAdState = AdState.NONE;
        mContext = context;
        mVastData = data;
        mAssetsLoader = new AssetsLoader();
        this.isInterstitial = isInterstitial;
        this.isFullscreen = isFullscreen;

        mViewabilityAdSession = new HyBidViewabilityNativeVideoAdSession(HyBid.getViewabilityManager());
        this.mImpressionListener = impressionListener;
    }

    abstract void dismiss();

    abstract AdSpotDimensions getAdSpotDimensions();

    abstract int getAdFormat();

    Ad getAd() {
        return mAd;
    }

    Context getContext() {
        return mContext;
    }

    VideoAdListener getAdListener() {
        return mVideoAdListener;
    }

    VideoAdController getAdController() {
        return mAdController;
    }

    int getAdState() {
        return mAdState;
    }

    void setAdState(int adState) {
        this.mAdState = adState;
    }

    boolean isReady() {
        return mIsReady;
    }

    boolean isRewarded() {
        return mIsRewarded;
    }

    public Boolean isInterstitial() {
        return isInterstitial;
    }

    void setRewarded(boolean isRewarded) {
        this.mIsRewarded = isRewarded;
    }

    void setAdListener(VideoAdListener videoAdListener) {
        mVideoAdListener = videoAdListener;
    }

    public void setVideoCacheItem(VideoAdCacheItem adCacheItem) {
        this.mCacheItem = adCacheItem;
    }

    void initAdLoadingStartTime() {
        mAdLoadingStartTime = System.currentTimeMillis();
    }

    void setReady() {
        this.mIsReady = false;
    }

    protected HyBidViewabilityNativeVideoAdSession getViewabilityAdSession() {
        return mViewabilityAdSession;
    }

    protected VideoAdCacheItem getCacheItem() {
        return mCacheItem;
    }

    void releaseAdController() {
        Logger.d(LOG_TAG, "Release ViewControllerVast");

        if (mAdController != null) {
            mAdController.destroy();
            mAdController = null;
        }
    }

    void runOnUiThread(Runnable r) {
        mHandler.post(r);
    }

    private void startExpirationTimer() {
        if (mExpirationTimer != null) {
            return;
        }
        mExpirationTimer = new SimpleTimer(VpaidConstants.DEFAULT_EXPIRED_TIME, this::onAdExpired);
        mExpirationTimer.start();
        Logger.d(LOG_TAG, "Start schedule expiration");
    }

    void stopExpirationTimer() {
        if (mExpirationTimer != null) {
            Logger.d(LOG_TAG, "Stop schedule expiration");
            mExpirationTimer.cancel();
            mExpirationTimer = null;
        }
    }

    private void startPrepareTimer() {
        if (mPrepareTimer != null) {
            return;
        }
        mPrepareTimer = new SimpleTimer(VpaidConstants.PREPARE_PLAYER_TIMEOUT, () -> {
            mPrepareTimer = null;
            if (mAdController != null && mAdController instanceof VideoAdControllerVpaid) {
                ErrorLog.postError(getContext(), VastError.FILE_NOT_FOUND);
                onAdLoadFail(new PlayerInfo("Problem with js file"));
            }
            cancelFetcher();
        });
        mPrepareTimer.start();
        Logger.d(LOG_TAG, "Start prepare timer");
    }

    private void stopPrepareTimer() {
        Logger.d(LOG_TAG, "Stop prepare timer");
        if (mPrepareTimer != null) {
            mPrepareTimer.cancel();
            mPrepareTimer = null;
        }
    }


    void proceedLoad() {
        if (mCacheItem != null) {
            prepare(mCacheItem.getAdParams(), mVastData);
        } else {
            fetchAd();
        }
    }

    void startFetcherTimer() {
        if (mFetcherTimer != null) {
            return;
        }
        mFetcherTimer = new SimpleTimer(VpaidConstants.FETCH_TIMEOUT, () -> {
            cancelFetcher();
            ErrorLog.postError(getContext(), VastError.TIMEOUT);
            onAdLoadFail(new PlayerInfo("Ad processing timeout"));
        });
        mFetcherTimer.start();
        Logger.d(LOG_TAG, "Start fetcher timer");
    }

    void stopFetcherTimer() {
        Logger.d(LOG_TAG, "Stop fetcher timer");
        if (mFetcherTimer != null) {
            mFetcherTimer.cancel();
            mFetcherTimer = null;
        }
    }

    void cancelFetcher() {
        Logger.d(LOG_TAG, "Cancel ad fetcher");
        mAssetsLoader.breakLoading();

        mHandler.removeCallbacksAndMessages(null);
    }

    private void fetchAd() {
        VastProcessor processor = new VastProcessor(getContext(), getAdSpotDimensions());
        processor.parseResponse(mVastData, new VastProcessor.Listener() {
            @Override
            public void onParseSuccess(AdParams adParams, String vastFileContent) {
                prepare(adParams, vastFileContent);
            }

            @Override
            public void onParseError(PlayerInfo message) {
                onAdLoadFailInternal(message);
            }
        });
    }

    private void prepare(AdParams adParams, String vastFileContent) {
        if (adParams.isVpaid()) {
            mAdController = new VideoAdControllerVpaid(this, adParams, getAdSpotDimensions(), vastFileContent, getViewabilityAdSession());
        } else {
            mAdController = new VideoAdControllerVast(this, adParams, getViewabilityAdSession(), isFullscreen, this.mImpressionListener);
        }
        if (mCacheItem != null) {
            prepareAdController(mCacheItem.getVideoFilePath(), mCacheItem.getEndCardData(), mCacheItem.getEndCardFilePath());
        } else {
            mAssetsLoader.load(adParams, mContext, createAssetsLoadListener());
        }
    }

    private AssetsLoader.OnAssetsLoaded createAssetsLoadListener() {
        return new AssetsLoader.OnAssetsLoaded() {
            @Override
            public void onAssetsLoaded(String videoFilePath, EndCardData endCardData, String endCardFilePath) {
                prepareAdController(videoFilePath, endCardData, endCardFilePath);
            }

            @Override
            public void onError(PlayerInfo info) {
                onAdLoadFailInternal(info);
            }
        };
    }

    private void prepareAdController(String videoFilePath, EndCardData endCardData, String endCardFilePath) {
        if (mAdController == null) {
            onAdLoadFailInternal(new PlayerInfo("Error during video loading"));
            ErrorLog.postError(getContext(), VastError.UNDEFINED);
            Logger.d(LOG_TAG, "VideoAdController == null, after onAssetsLoaded success");
            return;
        }
        mAdController.setVideoFilePath(videoFilePath);
        mAdController.setEndCardData(endCardData);
        mAdController.setEndCardFilePath(endCardFilePath);
        runOnUiThread(() -> {
            startPrepareTimer();
            mAdController.prepare(createOnPrepareListener());
        });
    }

    private VideoAdController.OnPreparedListener createOnPrepareListener() {
        return () -> {
            if (getAdState() == AdState.SHOWING) {
                Logger.d(LOG_TAG, "Creative call unexpected AdLoaded");
                return;
            }
            stopPrepareTimer();
            onAdLoadSuccessInternal();
        };
    }

    void onAdLoadFailInternal(final PlayerInfo issue) {
        runOnUiThread(() -> onAdLoadFail(issue));
    }

    void onAdLoadSuccessInternal() {
        runOnUiThread(() -> onAdLoadSuccess());
    }

    private void onAdExpired() {
        Logger.d(LOG_TAG, "Ad content is expired");
        mExpirationTimer = null;
        mIsReady = false;
        mAdState = AdState.NONE;
        mAssetsLoader.breakLoading();
        if (mVideoAdListener != null) {
            mVideoAdListener.onAdExpired();
        }
    }

    private void onAdLoadFail(PlayerInfo issue) {
        Logger.d(LOG_TAG, "Ad fails to load: " + issue.getMessage());
        mAdState = AdState.NONE;
        mIsReady = false;
        stopFetcherTimer();
        if (mVideoAdListener != null) {
            mVideoAdListener.onAdLoadFail(issue);
        } else {
            Logger.w(LOG_TAG, "Warning: empty listener");
        }
    }

    private void onAdLoadSuccess() {
        startExpirationTimer();

        long currentTime = System.currentTimeMillis();
        long loadingTime = currentTime - mAdLoadingStartTime;

        Logger.d(LOG_TAG, "Ad successfully loaded (" + loadingTime + "ms)");
        mIsReady = true;
        mAdState = AdState.NONE;
        stopFetcherTimer();
        if (mVideoAdListener != null) {
            mVideoAdListener.onAdLoadSuccess();
        } else {
            Logger.w(LOG_TAG, "Warning: empty listener");
        }
    }

    void onAdDidReachEnd() {
        Logger.d(LOG_TAG, "Video reach end");
        if (mVideoAdListener != null) {
            mVideoAdListener.onAdDidReachEnd();
        }
    }

    void onAdLeaveApp() {
        Logger.d(LOG_TAG, "adLeaveApp");
        if (mVideoAdListener != null) {
            mVideoAdListener.onLeaveApp();
        }
    }

    void onAdClicked() {
        Logger.d(LOG_TAG, "Ad received click event");
        if (mVideoAdListener != null) {
            mVideoAdListener.onAdClicked();
        }
    }

    void onAdSkipped() {
        Logger.d(LOG_TAG, "onAdSkipped");
        if (mVideoAdListener != null) {
            mVideoAdListener.onAdSkipped();
        }
    }
}
