package net.pubnative.tarantula.sdk.interstitial.presenter;

import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import net.pubnative.tarantula.sdk.interstitial.InterstitialBroadcastReceiver;
import net.pubnative.tarantula.sdk.interstitial.activity.InterstitialActivity;
import net.pubnative.tarantula.sdk.models.Ad;

import java.util.Random;

/**
 * Created by erosgarciaponte on 09.01.18.
 */

public class HtmlInterstitialPresenter implements InterstitialPresenter {
    @NonNull private final Context mContext;
    @NonNull private final Ad mAd;
    private final long mBroadcastId;

    @Nullable private InterstitialBroadcastReceiver mInterstitialBroadcastReceiver;
    @Nullable private InterstitialPresenter.Listener mListener;

    public HtmlInterstitialPresenter(@NonNull Context context, @NonNull Ad ad) {
        mContext = context;
        mAd = ad;
        mBroadcastId = new Random().nextLong();
    }

    @Override
    public void setListener(@Nullable Listener listener) {
        mListener = listener;
    }

    @NonNull
    @Override
    public Ad getAd() {
        return mAd;
    }

    @Override
    public void load() {
        mInterstitialBroadcastReceiver = new InterstitialBroadcastReceiver(mContext, this, mBroadcastId);
        mInterstitialBroadcastReceiver.setListener(mListener);

        final IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(InterstitialBroadcastReceiver.INTERSTITIAL_SHOW);
        intentFilter.addAction(InterstitialBroadcastReceiver.INTERSTITIAL_CLICK);
        intentFilter.addAction(InterstitialBroadcastReceiver.INTERSTITIAL_DISMISS);
        intentFilter.addAction(InterstitialBroadcastReceiver.INTERSTITIAL_ERROR);

        mInterstitialBroadcastReceiver.register(intentFilter);

        if (mListener != null) {
            mListener.onInterstitialLoaded(this);
        }
    }

    @Override
    public void show() {
        final Intent intent = new Intent(mContext, InterstitialActivity.class);
        intent.putExtra(InterstitialActivity.HTML_KEY, mAd.getCreative());
        intent.putExtra(InterstitialActivity.BROADCAST_ID_KEY, mBroadcastId);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        mContext.startActivity(intent);
    }

    @Override
    public void destroy() {
        if (mInterstitialBroadcastReceiver != null) {
            mInterstitialBroadcastReceiver.unregister();
        }
    }
}
