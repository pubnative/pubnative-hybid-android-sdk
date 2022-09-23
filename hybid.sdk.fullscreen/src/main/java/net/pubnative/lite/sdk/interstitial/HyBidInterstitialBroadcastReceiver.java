package net.pubnative.lite.sdk.interstitial;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;

import net.pubnative.lite.sdk.VideoListener;
import net.pubnative.lite.sdk.interstitial.presenter.InterstitialPresenter;
import net.pubnative.lite.sdk.utils.PNLocalBroadcastManager;

import java.util.Random;

public class HyBidInterstitialBroadcastReceiver extends BroadcastReceiver {
    public interface Listener {
        void onReceivedAction(Action action, Bundle extras);
    }

    public enum Action {
        SHOW("net.pubnative.hybid.interstitial.show"),
        CLICK("net.pubnative.hybid.interstitial.click"),
        DISMISS("net.pubnative.hybid.interstitial.dismiss"),
        ERROR("net.pubnative.hybid.interstitial.error"),
        VIDEO_ERROR("net.pubnative.hybid.interstitial.video_error"),
        VIDEO_START("net.pubnative.hybid.interstitial.video_start"),
        VIDEO_SKIP("net.pubnative.hybid.interstitial.video_skip"),
        VIDEO_DISMISS("net.pubnative.hybid.interstitial.video_dismiss"),
        VIDEO_FINISH("net.pubnative.hybid.interstitial.video_finish"),
        NONE("none");

        public static Action from(String action) {
            if (SHOW.getId().equals(action)) {
                return SHOW;
            } else if (CLICK.getId().equals(action)) {
                return CLICK;
            } else if (DISMISS.getId().equals(action)) {
                return DISMISS;
            } else if (ERROR.getId().equals(action)) {
                return ERROR;
            } else if (VIDEO_ERROR.getId().equals(action)) {
                return VIDEO_ERROR;
            } else if (VIDEO_START.getId().equals(action)) {
                return VIDEO_START;
            } else if (VIDEO_SKIP.getId().equals(action)) {
                return VIDEO_SKIP;
            } else if (VIDEO_DISMISS.getId().equals(action)) {
                return VIDEO_DISMISS;
            } else if (VIDEO_FINISH.getId().equals(action)) {
                return VIDEO_FINISH;
            }

            return NONE;
        }

        private final String mId;

        Action(String id) {
            mId = id;
        }

        public String getId() {
            return mId;
        }
    }

    public static final String BROADCAST_ID = "pn_broadcastId";
    public static final String VIDEO_PROGRESS = "pn_video_progress";

    private final long mBroadcastId;
    private final PNLocalBroadcastManager mLocalBroadcastManager;
    private final IntentFilter mIntentFilter;
    private boolean mDestroyed;

    private Listener mListener;

    public HyBidInterstitialBroadcastReceiver(Context context) {
        this(new Random().nextLong(), PNLocalBroadcastManager.getInstance(context), new IntentFilter());
    }

    HyBidInterstitialBroadcastReceiver(long broadcastId,
                                       PNLocalBroadcastManager localBroadcastManager,
                                       IntentFilter intentFilter) {
        mBroadcastId = broadcastId;
        mLocalBroadcastManager = localBroadcastManager;

        mIntentFilter = intentFilter;
        mIntentFilter.addAction(Action.SHOW.getId());
        mIntentFilter.addAction(Action.CLICK.getId());
        mIntentFilter.addAction(Action.DISMISS.getId());
        mIntentFilter.addAction(Action.VIDEO_ERROR.getId());
        mIntentFilter.addAction(Action.VIDEO_START.getId());
        mIntentFilter.addAction(Action.VIDEO_SKIP.getId());
        mIntentFilter.addAction(Action.VIDEO_DISMISS.getId());
        mIntentFilter.addAction(Action.VIDEO_FINISH.getId());
        mIntentFilter.addAction(Action.ERROR.getId());
    }

    public void setListener(Listener listener) {
        mListener = listener;
    }

    public long getBroadcastId() {
        return mBroadcastId;
    }

    public void register() {
        if (mDestroyed) {
            return;
        }

        mLocalBroadcastManager.registerReceiver(this, mIntentFilter);
    }

    public void destroy() {
        mLocalBroadcastManager.unregisterReceiver(this);
        mDestroyed = true;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        if (mDestroyed || mListener == null) {
            return;
        }

        final long receivedId = intent.getLongExtra(BROADCAST_ID, -1);
        if (mBroadcastId != receivedId) {
            return;
        }

        mListener.onReceivedAction(Action.from(intent.getAction()), intent.getExtras());
    }

    public void handleAction(Action action,
                             Bundle extras,
                             InterstitialPresenter presenter,
                             InterstitialPresenter.Listener listener) {
        handleAction(action, extras, presenter, listener, null);
    }

    public void handleAction(Action action,
                             Bundle extras,
                             InterstitialPresenter presenter,
                             InterstitialPresenter.Listener listener,
                             VideoListener videoListener) {
        if (listener == null) {
            return;
        }

        switch (action) {
            case SHOW:
                listener.onInterstitialShown(presenter);
                break;
            case CLICK:
                listener.onInterstitialClicked(presenter);
                break;
            case DISMISS:
                listener.onInterstitialDismissed(presenter);
                break;
            case ERROR:
                listener.onInterstitialError(presenter);
                break;
            case VIDEO_ERROR:
                if (videoListener != null) {
                    if (extras != null) {
                        videoListener.onVideoError(extras.getInt(VIDEO_PROGRESS, -1));
                    } else {
                        videoListener.onVideoError(-1);
                    }
                }
                break;
            case VIDEO_START:
                if (videoListener != null) {
                    videoListener.onVideoStarted();
                }
                break;
            case VIDEO_SKIP:
                if (videoListener != null){
                    videoListener.onVideoSkipped();
                }
                break;
            case VIDEO_DISMISS:
                if (videoListener != null) {
                    if (extras != null) {
                        videoListener.onVideoDismissed(extras.getInt(VIDEO_PROGRESS, -1));
                    } else {
                        videoListener.onVideoDismissed(-1);
                    }
                }
                break;
            case VIDEO_FINISH:
                if (videoListener != null) {
                    videoListener.onVideoFinished();
                }
                break;
            case NONE:
                break;
        }
    }
}
