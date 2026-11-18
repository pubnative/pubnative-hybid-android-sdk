// HyBid SDK License
//
// https://github.com/pubnative/pubnative-hybid-android-sdk/blob/main/LICENSE
//
package net.pubnative.lite.sdk.vpaid;

public interface AdCloseButtonListener {
    void showButton();

    /**
     * Triggered when the skip offset is reached and the skip button (not the close button) becomes
     * visible, which is what rewarded VAST ads with an end card show.
     */
    default void onSkipButtonShown() {
    }
}
