package net.pubnative.lite.sdk.interstitial;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;

import net.pubnative.lite.sdk.interstitial.presenter.InterstitialPresenter;
import net.pubnative.lite.sdk.utils.PNLocalBroadcastManager;

import java.util.Random;

public class HyBidInterstitialBroadcastReceiver extends BroadcastReceiver {
    public interface Listener {
        void onReceivedAction(Action action);
    }

    public enum Action {
        SHOW("net.pubnative.hybid.interstitial.show"),
        CLICK("net.pubnative.hybid.interstitial.click"),
        DISMISS("net.pubnative.hybid.interstitial.dismiss"),
        ERROR("net.pubnative.hybid.interstitial.error"),
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
                             InterstitialPresenter presenter,
                             InterstitialPresenter.Listener listener) {
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
            case NONE:
                break;
        }
    }
}
