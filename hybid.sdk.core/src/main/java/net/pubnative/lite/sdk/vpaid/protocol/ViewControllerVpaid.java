package net.pubnative.lite.sdk.vpaid.protocol;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.FrameLayout;
import android.widget.ImageView;

import net.pubnative.lite.sdk.core.R;
import net.pubnative.lite.sdk.vpaid.VideoAdController;
import net.pubnative.lite.sdk.vpaid.VideoAdView;
import net.pubnative.lite.sdk.vpaid.utils.ImageUtils;

public class ViewControllerVpaid {

    private final VideoAdController mAdController;

    private WebView mWebView;
    private View mEndCardLayout;
    private ImageView mEndCardView;

    public ViewControllerVpaid(VideoAdController adController) {
        mAdController = adController;
    }

    public void buildVideoAdView(VideoAdView bannerView, WebView webView) {
        Context context = bannerView.getContext();
        mWebView = webView;

        bannerView.removeAllViews();
        if (mWebView.getParent() != null) {
            ((ViewGroup) mWebView.getParent()).removeAllViews();
        }

        FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.MATCH_PARENT);

        mEndCardLayout = LayoutInflater.from(context).inflate(R.layout.end_card, bannerView, false);
        mEndCardLayout.setVisibility(View.GONE);

        mEndCardView = mEndCardLayout.findViewById(R.id.endCardView);
        ImageView closeView = mEndCardLayout.findViewById(R.id.closeView);
        closeView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mAdController.closeSelf();
            }
        });

        ImageView replayView = mEndCardLayout.findViewById(R.id.replayView);
        replayView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showControls();
                mAdController.playAd();
            }
        });

        bannerView.addView(mEndCardLayout, params);
        bannerView.addView(webView, params);
        webView.setBackgroundColor(Color.TRANSPARENT);
        bannerView.setBackgroundColor(Color.BLACK);
    }

    private void showControls() {
        mEndCardLayout.setVisibility(View.GONE);
        mWebView.setVisibility(View.VISIBLE);
    }

    public void showEndCard(String imageUri) {
        mEndCardLayout.setVisibility(View.VISIBLE);
        mWebView.setVisibility(View.GONE);
        ImageUtils.setScaledImage(mEndCardView, imageUri);
    }


}
