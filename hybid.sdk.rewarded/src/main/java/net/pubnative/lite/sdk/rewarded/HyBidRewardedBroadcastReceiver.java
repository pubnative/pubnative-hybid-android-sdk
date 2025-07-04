// HyBid SDK License
//
// https://github.com/pubnative/pubnative-hybid-android-sdk/blob/main/LICENSE
//
package net.pubnative.lite.sdk.rewarded;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;

import net.pubnative.lite.sdk.CustomEndCardListener;
import net.pubnative.lite.sdk.VideoListener;
import net.pubnative.lite.sdk.analytics.Reporting;
import net.pubnative.lite.sdk.rewarded.presenter.RewardedPresenter;
import net.pubnative.lite.sdk.utils.PNLocalBroadcastManager;

import java.util.Random;

public class HyBidRewardedBroadcastReceiver extends BroadcastReceiver {
    public interface Listener {
        void onReceivedAction(Action action, Bundle extras);
    }

    public enum Action {
        OPEN("net.pubnative.hybid.rewarded.open"),
        CLICK("net.pubnative.hybid.rewarded.click"),
        CLOSE("net.pubnative.hybid.rewarded.close"),
        ERROR("net.pubnative.hybid.rewarded.error"),
        VIDEO_ERROR("net.pubnative.hybid.rewarded.video_error"),
        VIDEO_START("net.pubnative.hybid.rewarded.video_start"),
        VIDEO_SKIP("net.pubnative.hybid.rewarded.video_skip"),
        VIDEO_DISMISS("net.pubnative.hybid.rewarded.video_dismiss"),
        VIDEO_FINISH("net.pubnative.hybid.rewarded.video_finish"),
        CUSTOM_END_CARD_SHOW("net.pubnative.hybid.rewarded.custom_end_card_show"),
        CUSTOM_END_CARD_CLICK("net.pubnative.hybid.rewarded.custom_end_card_click"),
        DEFAULT_END_CARD_SHOW("net.pubnative.hybid.rewarded.default_end_card_show"),
        DEFAULT_END_CARD_CLICK("net.pubnative.hybid.rewarded.default_end_card_click"),
        END_CARD_LOAD_SUCCESS("net.pubnative.hybid.interstitial.end_card_load_success"),
        END_CARD_LOAD_FAILURE("net.pubnative.hybid.interstitial.end_card_load_failure"),
        NONE("none");

        public static Action from(String action) {
            if (OPEN.getId().equals(action)) {
                return OPEN;
            } else if (CLICK.getId().equals(action)) {
                return CLICK;
            } else if (CLOSE.getId().equals(action)) {
                return CLOSE;
            } else if (VIDEO_START.getId().equals(action)) {
                return VIDEO_START;
            } else if (VIDEO_SKIP.getId().equals(action)) {
                return VIDEO_SKIP;
            } else if (VIDEO_FINISH.getId().equals(action)) {
                return VIDEO_FINISH;
            } else if (VIDEO_DISMISS.getId().equals(action)) {
                return VIDEO_DISMISS;
            } else if (VIDEO_ERROR.getId().equals(action)) {
                return VIDEO_ERROR;
            } else if (ERROR.getId().equals(action)) {
                return ERROR;
            } else if (CUSTOM_END_CARD_SHOW.getId().equals(action)) {
                return CUSTOM_END_CARD_SHOW;
            } else if (CUSTOM_END_CARD_CLICK.getId().equals(action)) {
                return CUSTOM_END_CARD_CLICK;
            } else if (DEFAULT_END_CARD_SHOW.getId().equals(action)) {
                return DEFAULT_END_CARD_SHOW;
            } else if (DEFAULT_END_CARD_CLICK.getId().equals(action)) {
                return DEFAULT_END_CARD_CLICK;
            } else if (END_CARD_LOAD_SUCCESS.getId().equals(action)) {
                return END_CARD_LOAD_SUCCESS;
            } else if (END_CARD_LOAD_FAILURE.getId().equals(action)) {
                return END_CARD_LOAD_FAILURE;
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

    public static final String BROADCAST_ID = "pn_rewarded_broadcastId";
    public static final String VIDEO_PROGRESS = "pn_video_progress";

    private final long mBroadcastId;
    private final PNLocalBroadcastManager mLocalBroadcastManager;
    private final IntentFilter mIntentFilter;
    private boolean mDestroyed;

    private Listener mListener;

    public HyBidRewardedBroadcastReceiver(Context context) {
        this(new Random().nextLong(), PNLocalBroadcastManager.getInstance(context), new IntentFilter());
    }

    HyBidRewardedBroadcastReceiver(long broadcastId,
                                   PNLocalBroadcastManager localBroadcastManager,
                                   IntentFilter intentFilter) {
        mBroadcastId = broadcastId;
        mLocalBroadcastManager = localBroadcastManager;

        mIntentFilter = intentFilter;
        mIntentFilter.addAction(Action.OPEN.getId());
        mIntentFilter.addAction(Action.CLICK.getId());
        mIntentFilter.addAction(Action.CLOSE.getId());
        mIntentFilter.addAction(Action.VIDEO_START.getId());
        mIntentFilter.addAction(Action.VIDEO_SKIP.getId());
        mIntentFilter.addAction(Action.VIDEO_FINISH.getId());
        mIntentFilter.addAction(Action.VIDEO_DISMISS.getId());
        mIntentFilter.addAction(Action.VIDEO_ERROR.getId());
        mIntentFilter.addAction(Action.CUSTOM_END_CARD_SHOW.getId());
        mIntentFilter.addAction(Action.CUSTOM_END_CARD_CLICK.getId());

        mIntentFilter.addAction(Action.DEFAULT_END_CARD_SHOW.getId());
        mIntentFilter.addAction(Action.DEFAULT_END_CARD_CLICK.getId());
        mIntentFilter.addAction(Action.END_CARD_LOAD_SUCCESS.getId());
        mIntentFilter.addAction(Action.END_CARD_LOAD_FAILURE.getId());
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
                             RewardedPresenter presenter,
                             Bundle extras,
                             RewardedPresenter.Listener listener,
                             VideoListener videoListener,
                             CustomEndCardListener customEndCardListener) {
        if (listener == null) {
            return;
        }

        switch (action) {
            case OPEN:
                listener.onRewardedOpened(presenter);
                break;
            case CLICK:
                listener.onRewardedClicked(presenter);
                break;
            case CLOSE:
                listener.onRewardedFinished(presenter);
                listener.onRewardedClosed(presenter);
                break;
            case ERROR:
                listener.onRewardedError(presenter);
                break;
            case VIDEO_START:
                if (videoListener != null) {
                    videoListener.onVideoStarted();
                }
                break;
            case VIDEO_SKIP:
                if (videoListener != null) {
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
            case CUSTOM_END_CARD_SHOW:
                if (customEndCardListener != null) {
                    customEndCardListener.onCustomEndCardShow();
                }
                break;
            case CUSTOM_END_CARD_CLICK:
                if (customEndCardListener != null) {
                    customEndCardListener.onCustomEndCardClick();
                }
                break;
            case DEFAULT_END_CARD_SHOW:
                if (customEndCardListener != null) {
                    customEndCardListener.onDefaultEndCardShow();
                }
                break;
            case DEFAULT_END_CARD_CLICK:
                if (customEndCardListener != null) {
                    customEndCardListener.onDefaultEndCardClick();
                }
                break;
            case END_CARD_LOAD_SUCCESS:
                if (customEndCardListener != null) {
                    customEndCardListener.onEndCardLoadSuccess(extras.getBoolean(Reporting.Key.IS_CUSTOM_END_CARD, false));
                }
                break;
            case END_CARD_LOAD_FAILURE:
                if (customEndCardListener != null) {
                    customEndCardListener.onEndCardLoadFailure(extras.getBoolean(Reporting.Key.IS_CUSTOM_END_CARD, false));
                }
                break;
            case NONE:
                break;
        }
    }
}
