package net.pubnative.lite.sdk.views;

public interface HyBidHtmlWebViewListener {
    void onLoaded(HyBidHtmlWebView mHtmlWebView);

    void onFailed(Throwable error);

    void onClicked();

    void onCollapsed();
}
