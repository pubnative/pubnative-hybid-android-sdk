package net.pubnative.lite.sdk.rewarded;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;

import net.pubnative.lite.sdk.rewarded.presenter.RewardedPresenter;
import net.pubnative.lite.sdk.utils.PNLocalBroadcastManager;

import java.util.Random;

public class HyBidRewardedBroadcastReceiver extends BroadcastReceiver {
    public interface Listener {
        void onReceivedAction(Action action);
    }

    public enum Action {
        OPEN("net.pubnative.hybid.rewarded.open"),
        CLICK("net.pubnative.hybid.rewarded.click"),
        CLOSE("net.pubnative.hybid.rewarded.close"),
        FINISH("net.pubnative.hybid.rewarded.finish"),
        ERROR("net.pubnative.hybid.rewarded.error"),
        NONE("none");

        public static Action from(String action) {
            if (OPEN.getId().equals(action)) {
                return OPEN;
            } else if (CLICK.getId().equals(action)) {
                return CLICK;
            } else if (CLOSE.getId().equals(action)) {
                return CLOSE;
            } else if (FINISH.getId().equals(action)) {
                return FINISH;
            } else if (ERROR.getId().equals(action)) {
                return ERROR;
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
        mIntentFilter.addAction(Action.FINISH.getId());
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

        mListener.onReceivedAction(Action.from(intent.getAction()));
    }

    public void handleAction(Action action,
                             RewardedPresenter presenter,
                             RewardedPresenter.Listener listener) {
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
                listener.onRewardedClosed(presenter);
                break;
            case FINISH:
                listener.onRewardedFinished(presenter);
                break;
            case ERROR:
                listener.onRewardedError(presenter);
                break;
            case NONE:
                break;
        }
    }
}
