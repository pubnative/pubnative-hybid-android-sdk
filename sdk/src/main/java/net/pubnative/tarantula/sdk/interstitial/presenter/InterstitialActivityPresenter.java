package net.pubnative.tarantula.sdk.interstitial.presenter;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v4.content.LocalBroadcastManager;

import net.pubnative.tarantula.sdk.interstitial.InterstitialBroadcastReceiver;
import net.pubnative.tarantula.sdk.interstitial.view.InterstitialActivityViewModule;

/**
 * Created by erosgarciaponte on 09.01.18.
 */

public class InterstitialActivityPresenter implements InterstitialActivityViewModule.Listener {
    @NonNull private final Context mContext;
    @NonNull private final InterstitialActivityViewModule mInterstitialActivityViewModule;
    @NonNull private final String mHtml;
    private final long mBroadcastId;

    public InterstitialActivityPresenter(@NonNull Context context,
                                         @NonNull InterstitialActivityViewModule interstitialActivityViewModule,
                                         @NonNull String html,
                                         long broadcastId) {
        mContext = context;
        mInterstitialActivityViewModule = interstitialActivityViewModule;
        mInterstitialActivityViewModule.setListener(this);
        mHtml = html;
        mBroadcastId = broadcastId;
    }

    public void show() {
        mInterstitialActivityViewModule.show(mHtml);
        broadcastAction(InterstitialBroadcastReceiver.INTERSTITIAL_SHOW);
    }

    public void destroy() {
        mInterstitialActivityViewModule.destroy();
    }

    public void handleBackPressed() {
        broadcastAction(InterstitialBroadcastReceiver.INTERSTITIAL_DISMISS);
    }

    private void broadcastAction(@NonNull String action) {
        Intent intent = new Intent(action);
        intent.putExtra(InterstitialBroadcastReceiver.BROADCAST_ID, mBroadcastId);
        LocalBroadcastManager.getInstance(mContext).sendBroadcast(intent);
    }

    @Override
    public void onInterstitialClicked() {
        broadcastAction(InterstitialBroadcastReceiver.INTERSTITIAL_CLICK);
    }

    @Override
    public void onDismissClicked() {
        broadcastAction(InterstitialBroadcastReceiver.INTERSTITIAL_DISMISS);
    }
}
