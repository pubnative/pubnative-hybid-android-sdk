package net.pubnative.tarantula.sdk.banner.presenter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.View;

import net.pubnative.tarantula.sdk.Tarantula;
import net.pubnative.tarantula.sdk.models.Ad;
import net.pubnative.tarantula.sdk.views.HtmlWebView;
import net.pubnative.tarantula.sdk.views.HtmlWebViewClient;

/**
 * Created by erosgarciaponte on 08.01.18.
 */

public class HtmlBannerPresenter implements BannerPresenter, View.OnClickListener {
    @NonNull private final HtmlWebView mHtmlWebView;
    @NonNull private final Ad mAd;
    @Nullable private BannerPresenter.Listener mListener;

    public HtmlBannerPresenter(@NonNull Context context, @NonNull Ad ad) {
        mAd = ad;
        mHtmlWebView = new HtmlWebView(context);
        mHtmlWebView.setWebViewClient(new HtmlWebViewClient(context));
        mHtmlWebView.setOnClickListener(this);
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
        mHtmlWebView.loadDataWithBaseURL("http://" + Tarantula.HOST + "/", mAd.getCreative(), "text/html", "utf-8", null);
        if (mListener != null) {
            mListener.onBannerLoaded(this, mHtmlWebView);
        }
    }

    @Override
    public void destroy() {
        mHtmlWebView.destroy();
        mListener = null;
    }

    @Override
    public void onClick(View view) {
        if (mListener != null) {
            mListener.onBannerClicked(this);
        }
    }
}
