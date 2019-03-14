package net.pubnative.lite.sdk.vpaid;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;

import net.pubnative.lite.sdk.utils.Logger;
import net.pubnative.lite.sdk.vpaid.enums.AdState;
import net.pubnative.lite.sdk.vpaid.enums.VastError;
import net.pubnative.lite.sdk.vpaid.helpers.AssetsLoader;
import net.pubnative.lite.sdk.vpaid.helpers.ErrorLog;
import net.pubnative.lite.sdk.vpaid.helpers.SimpleTimer;
import net.pubnative.lite.sdk.vpaid.models.AdSpotDimensions;
import net.pubnative.lite.sdk.vpaid.response.AdParams;
import net.pubnative.lite.sdk.vpaid.response.VastParser;
import net.pubnative.lite.sdk.vpaid.utils.Utils;

import java.util.concurrent.Future;

abstract class BaseAdInternal {

    private static final String LOG_TAG = BaseAdInternal.class.getSimpleName();

    private final Context mContext;
    private final AssetsLoader mAssetsLoader;
    private final Handler mHandler = new Handler(Looper.getMainLooper());

    private int mAdState;
    private boolean mIsReady;
    private AdListener mAdListener;
    private long mAdLoadingStartTime;
    private SimpleTimer mExpirationTimer;
    private AdController mAdController;
    private SimpleTimer mFetcherTimer;
    private SimpleTimer mPrepareTimer;
    private String mVastData;
    private Future mFuture;

    BaseAdInternal(Context context, String data) {
        if (context == null || TextUtils.isEmpty(data)) {
            throw new IllegalArgumentException("Wrong parameters");
        }
        mAdState = AdState.NONE;
        mContext = context;
        mVastData = data;
        mAssetsLoader = new AssetsLoader();
        Utils.init(context);
    }

    abstract void dismiss();

    abstract AdSpotDimensions getAdSpotDimensions();

    abstract int getAdFormat();

    Context getContext() {
        return mContext;
    }

    AdListener getAdListener() {
        return mAdListener;
    }

    AdController getAdController() {
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

    void setAdListener(AdListener adListener) {
        mAdListener = adListener;
    }

    void initAdLoadingStartTime() {
        mAdLoadingStartTime = System.currentTimeMillis();
    }

    void setReady() {
        this.mIsReady = false;
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
        mExpirationTimer = new SimpleTimer(VpaidConstants.DEFAULT_EXPIRED_TIME, new SimpleTimer.Listener() {
            @Override
            public void onFinish() {
                onAdExpired();
            }
        });
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
        mPrepareTimer = new SimpleTimer(VpaidConstants.PREPARE_PLAYER_TIMEOUT, new SimpleTimer.Listener() {
            @Override
            public void onFinish() {
                mPrepareTimer = null;
                if (mAdController != null && mAdController instanceof AdControllerVpaid) {
                    ErrorLog.postError(VastError.FILE_NOT_FOUND);
                    onAdLoadFail(new PlayerInfo("Problem with js file"));
                }
                cancelFetcher();
            }
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
        fetchAd();
    }

    void startFetcherTimer() {
        if (mFetcherTimer != null) {
            return;
        }
        mFetcherTimer = new SimpleTimer(VpaidConstants.FETCH_TIMEOUT, new SimpleTimer.Listener() {
            @Override
            public void onFinish() {
                cancelFetcher();
                ErrorLog.postError(VastError.TIMEOUT);
                onAdLoadFail(new PlayerInfo("Ad processing timeout"));
            }
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
        if (mFuture != null) {
            mFuture.cancel(true);
        }
        if (mHandler != null) {
            mHandler.removeCallbacksAndMessages(null);
        }
    }

    private void fetchAd() {
        VastParser.parseResponse(mVastData, getAdSpotDimensions(), new VastParser.Listener() {
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
            mAdController = new AdControllerVpaid(this, adParams, getAdSpotDimensions(), vastFileContent);
        } else {
            mAdController = new AdControllerVast(this, adParams);
        }
        mAssetsLoader.load(adParams, mContext, createAssetsLoadListener());
    }

    private AssetsLoader.OnAssetsLoaded createAssetsLoadListener() {
        return new AssetsLoader.OnAssetsLoaded() {
            @Override
            public void onAssetsLoaded(String videoFilePath, String endCardFilePath) {
                if (mAdController == null) {
                    onAdLoadFailInternal(new PlayerInfo("Error during video loading"));
                    ErrorLog.postError(VastError.UNDEFINED);
                    Logger.d(LOG_TAG, "AdController == null, after onAssetsLoaded success");
                    return;
                }
                mAdController.setVideoFilePath(videoFilePath);
                mAdController.setEndCardFilePath(endCardFilePath);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        startPrepareTimer();
                        mAdController.prepare(createOnPrepareListener());
                    }
                });
            }

            @Override
            public void onError(PlayerInfo info) {
                onAdLoadFailInternal(info);
            }
        };
    }

    private AdController.OnPreparedListener createOnPrepareListener() {
        return new AdController.OnPreparedListener() {
            @Override
            public void onPrepared() {
                if (getAdState() == AdState.SHOWING) {
                    Logger.d(LOG_TAG, "Creative call unexpected AdLoaded");
                    return;
                }
                stopPrepareTimer();
                onAdLoadSuccessInternal();
            }
        };
    }

    void onAdLoadFailInternal(final PlayerInfo issue) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                onAdLoadFail(issue);
            }
        });
    }

    void onAdLoadSuccessInternal() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                onAdLoadSuccess();
            }
        });
    }

    private void onAdExpired() {
        Logger.d(LOG_TAG, "Ad content is expired");
        mExpirationTimer = null;
        mIsReady = false;
        mAdState = AdState.NONE;
        mAssetsLoader.breakLoading();
        if (mAdListener != null) {
            mAdListener.onAdExpired();
        }
    }

    private void onAdLoadFail(PlayerInfo issue) {
        Logger.d(LOG_TAG, "Ad fails to load: " + issue.getMessage());
        mAdState = AdState.NONE;
        mIsReady = false;
        stopFetcherTimer();
        if (mAdListener != null) {
            mAdListener.onAdLoadFail(issue);
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
        if (mAdListener != null) {
            mAdListener.onAdLoadSuccess();
        } else {
            Logger.w(LOG_TAG, "Warning: empty listener");
        }
    }

    void onAdDidReachEnd() {
        Logger.d(LOG_TAG, "Video reach end");
        if (mAdListener != null) {
            mAdListener.onAdDidReachEnd();
        }
    }

    void onAdLeaveApp() {
        Logger.d(LOG_TAG, "adLeaveApp");
        if (mAdListener != null) {
            mAdListener.onLeaveApp();
        }
    }

    void onAdClicked() {
        Logger.d(LOG_TAG, "Ad received click event");
        if (mAdListener != null) {
            mAdListener.onAdClicked();
        }
    }

}
