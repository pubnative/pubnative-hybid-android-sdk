package net.pubnative.lite.sdk.interstitial;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;

import net.pubnative.lite.sdk.interstitial.presenter.InterstitialPresenter;
import net.pubnative.lite.sdk.utils.PNLocalBroadcastManager;

/**
 * Created by erosgarciaponte on 08.01.18.
 */

public class InterstitialBroadcastReceiver extends BroadcastReceiver {
    public static final String BROADCAST_ID = "broadcastId";
    public static final String INTERSTITIAL_SHOW = "net.pubnative.lite.interstitial.show";
    public static final String INTERSTITIAL_CLICK = "net.pubnative.lite.interstitial.click";
    public static final String INTERSTITIAL_DISMISS = "net.pubnative.lite.interstitial.dismiss";
    public static final String INTERSTITIAL_ERROR = "net.pubnative.lite.interstitial.error";

    private final Context mContext;
    private final InterstitialPresenter mInterstitialPresenter;
    private final long mBroadcastId;
    private InterstitialPresenter.Listener mListener;

    public InterstitialBroadcastReceiver(Context context,
                                         InterstitialPresenter interstitialPresenter,
                                         long broadcastId) {
        mContext = context;
        mInterstitialPresenter = interstitialPresenter;
        mBroadcastId = broadcastId;
    }

    public void setListener(InterstitialPresenter.Listener listener) {
        mListener = listener;
    }

    public void register(IntentFilter intentFilter) {
        PNLocalBroadcastManager.getInstance(mContext).registerReceiver(this, intentFilter);
    }

    public void unregister() {
        PNLocalBroadcastManager.getInstance(mContext).unregisterReceiver(this);
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        if (mListener == null) {
            return;
        }

        final long receivedId = intent.getLongExtra(BROADCAST_ID, -1);
        if (mBroadcastId != receivedId) {
            return;
        }

        final String action = intent.getAction();
        if (INTERSTITIAL_SHOW.equals(action)) {
            mListener.onInterstitialShown(mInterstitialPresenter);
        } else if (INTERSTITIAL_CLICK.equals(action)) {
            mListener.onInterstitialClicked(mInterstitialPresenter);
        } else if (INTERSTITIAL_DISMISS.equals(action)) {
            mListener.onInterstitialDismissed(mInterstitialPresenter);
            unregister();
        } else if (INTERSTITIAL_ERROR.equals(action)) {
            mListener.onInterstitialError(mInterstitialPresenter);
        }
    }
}
