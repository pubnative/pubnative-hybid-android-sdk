package net.pubnative.lite.sdk.interstitial.presenter;

import android.content.Context;
import android.content.Intent;

import net.pubnative.lite.sdk.interstitial.InterstitialBroadcastReceiver;
import net.pubnative.lite.sdk.interstitial.view.InterstitialActivityViewModule;
import net.pubnative.lite.sdk.utils.PNLocalBroadcastManager;

/**
 * Created by erosgarciaponte on 09.01.18.
 */

public class InterstitialActivityPresenter implements InterstitialActivityViewModule.Listener {
    private final Context mContext;
    private final InterstitialActivityViewModule mInterstitialActivityViewModule;
    private final String mHtml;
    private final long mBroadcastId;

    public InterstitialActivityPresenter(Context context,
                                         InterstitialActivityViewModule interstitialActivityViewModule,
                                         String html,
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

    private void broadcastAction(String action) {
        Intent intent = new Intent(action);
        intent.putExtra(InterstitialBroadcastReceiver.BROADCAST_ID, mBroadcastId);
        PNLocalBroadcastManager.getInstance(mContext).sendBroadcast(intent);
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
