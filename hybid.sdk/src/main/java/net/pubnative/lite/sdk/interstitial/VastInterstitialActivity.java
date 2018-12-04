package net.pubnative.lite.sdk.interstitial;

import android.os.Bundle;
import android.view.View;

import net.pubnative.lite.sdk.vast.VASTParser;
import net.pubnative.lite.sdk.vast.VASTPlayer;
import net.pubnative.lite.sdk.vast.model.VASTModel;

public class VastInterstitialActivity extends HyBidInterstitialActivity implements VASTPlayer.Listener {
    private VASTPlayer mPlayer;
    private boolean mReady = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getAd() != null) {
            new VASTParser(this).setListener(new VASTParser.Listener() {
                @Override
                public void onVASTParserError(int error) {
                    getBroadcastSender().sendBroadcast(HyBidInterstitialBroadcastReceiver.Action.ERROR);
                    getBroadcastSender().sendBroadcast(HyBidInterstitialBroadcastReceiver.Action.DISMISS);
                    finish();
                }

                @Override
                public void onVASTParserFinished(VASTModel model) {
                    if (mPlayer != null) {
                        mPlayer.load(model);
                    }
                }
            }).execute(getAd().getVast());
        }
    }

    @Override
    public View getAdView() {
        if (getAd() != null) {
            mPlayer = new VASTPlayer(this);
            mPlayer.setListener(this);
            return mPlayer;
        }

        return null;
    }

    @Override
    public void onVASTPlayerLoadFinish() {
        mReady = true;
        mPlayer.onMuteClick();
        mPlayer.play();
        getBroadcastSender().sendBroadcast(HyBidInterstitialBroadcastReceiver.Action.SHOW);
    }

    @Override
    public void onVASTPlayerFail(Exception exception) {
        getBroadcastSender().sendBroadcast(HyBidInterstitialBroadcastReceiver.Action.ERROR);
        getBroadcastSender().sendBroadcast(HyBidInterstitialBroadcastReceiver.Action.DISMISS);
        finish();
    }

    @Override
    public void onVASTPlayerOpenOffer() {
        getBroadcastSender().sendBroadcast(HyBidInterstitialBroadcastReceiver.Action.CLICK);
    }

    @Override
    public void onVASTPlayerPlaybackStart() {

    }

    @Override
    public void onVASTPlayerPlaybackFinish() {
        mReady = false;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mPlayer != null) {
            mPlayer.destroy();
            mReady = false;
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (mReady) {
            mPlayer.play();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (mReady) {
            mPlayer.pause();
        }
    }
}
