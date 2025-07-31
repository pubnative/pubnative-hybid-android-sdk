// HyBid SDK License
//
// https://github.com/pubnative/pubnative-hybid-android-sdk/blob/main/LICENSE
//
package net.pubnative.lite.sdk;

public interface VideoListener {
    void onVideoError(int progressPercentage);

    void onVideoStarted();

    void onVideoDismissed(int progressPercentage);

    void onVideoFinished();

    void onVideoSkipped();
}
