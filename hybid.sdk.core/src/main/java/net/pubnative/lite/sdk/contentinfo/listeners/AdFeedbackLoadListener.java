// HyBid SDK License
//
// https://github.com/pubnative/pubnative-hybid-android-sdk/blob/main/LICENSE
//
package net.pubnative.lite.sdk.contentinfo.listeners;

public interface AdFeedbackLoadListener {

    void onLoad(String url);

    void onLoadFinished();

    void onLoadFailed(Throwable error);

    void onFormClosed();
}
