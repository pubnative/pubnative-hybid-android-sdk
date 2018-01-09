package net.pubnative.tarantula.sdk.interstitial.view;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.View;

import net.pubnative.tarantula.sdk.Tarantula;
import net.pubnative.tarantula.sdk.views.HtmlWebView;
import net.pubnative.tarantula.sdk.views.HtmlWebViewClient;

/**
 * Created by erosgarciaponte on 09.01.18.
 */

public class InterstitialActivityViewModuleImpl implements InterstitialActivityViewModule, View.OnClickListener {
    @NonNull private final HtmlWebView mHtmlWebView;
    @Nullable private Listener mListener;

    public InterstitialActivityViewModuleImpl(@NonNull Context context, @NonNull HtmlWebView htmlWebView) {
        mHtmlWebView = htmlWebView;
        mHtmlWebView.setWebViewClient(new HtmlWebViewClient(context));
        mHtmlWebView.setOnClickListener(this);
    }

    @Override
    public void setListener(@Nullable Listener listener) {
        mListener = listener;
    }

    @Override
    public void show(@NonNull String html) {
        mHtmlWebView.loadDataWithBaseURL("http://" + Tarantula.HOST + "/", html, "text/html", "utf-8", null);
    }

    @Override
    public void destroy() {
        mHtmlWebView.destroy();
    }

    @Override
    public void onClick(View view) {
        if (mListener == null) {
            return;
        }

        if (view == mHtmlWebView) {
            mListener.onInterstitialClicked();
        }
    }
}
