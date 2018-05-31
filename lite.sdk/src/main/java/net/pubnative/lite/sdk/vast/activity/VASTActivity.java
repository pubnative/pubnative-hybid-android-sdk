package net.pubnative.lite.sdk.vast.activity;

import android.app.Activity;
import android.os.Bundle;
import android.widget.RelativeLayout;

import net.pubnative.lite.sdk.R;
import net.pubnative.lite.sdk.vast.VASTPlayer;

public class VASTActivity extends Activity implements VASTPlayer.Listener {
    private VASTPlayer mPlayer;
    private RelativeLayout mContentInfo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vast_interstitial);
        mPlayer = findViewById(R.id.pn_player);
        mContentInfo = findViewById(R.id.pn_content_info_container);

        mPlayer.setListener(this);

        //TODO missing Content info
    }

    @Override
    public void onVASTPlayerLoadFinish() {

    }

    @Override
    public void onVASTPlayerOpenOffer() {

    }

    @Override
    public void onVASTPlayerPlaybackFinish() {

    }

    @Override
    public void onVASTPlayerPlaybackStart() {

    }

    @Override
    public void onVASTPlayerFail(Exception exception) {

    }
}
