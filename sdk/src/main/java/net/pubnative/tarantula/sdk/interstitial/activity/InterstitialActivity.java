package net.pubnative.tarantula.sdk.interstitial.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import net.pubnative.tarantula.sdk.R;
import net.pubnative.tarantula.sdk.interstitial.presenter.InterstitialActivityPresenter;
import net.pubnative.tarantula.sdk.interstitial.view.InterstitialActivityViewModule;
import net.pubnative.tarantula.sdk.interstitial.view.InterstitialActivityViewModuleImpl;
import net.pubnative.tarantula.sdk.views.HtmlWebView;

/**
 * Created by erosgarciaponte on 09.01.18.
 */

public class InterstitialActivity extends Activity {
    @NonNull
    public static final String HTML_KEY = "html_key";
    @NonNull
    public static final String BROADCAST_ID_KEY = "broadcast_id_key";

    @NonNull
    private InterstitialActivityPresenter mInterstitialActivityPresenter;
    @Nullable
    private String mHtml;
    private long mBroadcastId;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
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
