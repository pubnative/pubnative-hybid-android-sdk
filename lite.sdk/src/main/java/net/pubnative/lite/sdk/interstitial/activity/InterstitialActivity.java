package net.pubnative.lite.sdk.interstitial.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import net.pubnative.lite.sdk.R;
import net.pubnative.lite.sdk.interstitial.presenter.InterstitialActivityPresenter;
import net.pubnative.lite.sdk.interstitial.view.InterstitialActivityViewModule;
import net.pubnative.lite.sdk.interstitial.view.InterstitialActivityViewModuleImpl;
import net.pubnative.lite.sdk.views.HtmlWebView;

/**
 * Created by erosgarciaponte on 09.01.18.
 */

public class InterstitialActivity extends Activity {
    public static final String HTML_KEY = "html_key";
    public static final String BROADCAST_ID_KEY = "broadcast_id_key";

    private InterstitialActivityPresenter mInterstitialActivityPresenter;
    private String mHtml;
    private long mBroadcastId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_interstitial);

        // TODO do we care about locking the orientation of the device? How do we know?

        if (savedInstanceState != null) {
            mHtml = savedInstanceState.getString(HTML_KEY);
            mBroadcastId = savedInstanceState.getLong(BROADCAST_ID_KEY, -1);
        } else {
            final Intent intent = getIntent();
            mHtml = intent.getStringExtra(HTML_KEY);
            mBroadcastId = intent.getLongExtra(BROADCAST_ID_KEY, -1);
        }

        final InterstitialActivityViewModule interstitialActivityViewModule =
                new InterstitialActivityViewModuleImpl(this, (HtmlWebView) findViewById(R.id.web_view));
        mInterstitialActivityPresenter =
                new InterstitialActivityPresenter(this, interstitialActivityViewModule, mHtml, mBroadcastId);
        mInterstitialActivityPresenter.show();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mInterstitialActivityPresenter.destroy();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putString(HTML_KEY, mHtml);
        outState.putLong(BROADCAST_ID_KEY, mBroadcastId);
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        mInterstitialActivityPresenter.handleBackPressed();
    }
}
