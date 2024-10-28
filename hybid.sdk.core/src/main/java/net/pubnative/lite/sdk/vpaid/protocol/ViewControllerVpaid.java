package net.pubnative.lite.sdk.vpaid.protocol;

import android.content.Context;
import android.graphics.Color;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.FrameLayout;

import net.pubnative.lite.sdk.HyBid;
import net.pubnative.lite.sdk.analytics.Reporting;
import net.pubnative.lite.sdk.analytics.ReportingEvent;
import net.pubnative.lite.sdk.views.endcard.HyBidEndCardView;
import net.pubnative.lite.sdk.vpaid.VideoAdController;
import net.pubnative.lite.sdk.vpaid.VideoAdView;

public class ViewControllerVpaid {

    private final VideoAdController mAdController;

    private WebView mWebView;
    private HyBidEndCardView mEndCardView;

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

        mEndCardView = new HyBidEndCardView(mWebView.getContext());

        bannerView.addView(mEndCardView, params);
        bannerView.addView(webView, params);
        webView.setBackgroundColor(Color.TRANSPARENT);
        bannerView.setBackgroundColor(Color.BLACK);
    }

    private void showControls() {
        mEndCardView.hide();
        mWebView.setVisibility(View.VISIBLE);
    }

    public void showEndCard(String imageUri) {
        mEndCardView.show(imageUri);
        mWebView.setVisibility(View.GONE);

        if (HyBid.getReportingController() != null && HyBid.isReportingEnabled()) {
            ReportingEvent event = new ReportingEvent();
            event.setEventType(Reporting.EventType.COMPANION_VIEW);
            event.setCreativeType(Reporting.CreativeType.VIDEO);
            event.setTimestamp(System.currentTimeMillis());
            HyBid.getReportingController().reportEvent(event);
        }
    }
}
