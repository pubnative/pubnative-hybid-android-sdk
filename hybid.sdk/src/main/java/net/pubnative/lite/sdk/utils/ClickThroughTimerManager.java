// HyBid SDK License
//
// https://github.com/pubnative/pubnative-hybid-android-sdk/blob/main/LICENSE
//
package net.pubnative.lite.sdk.utils;

public class ClickThroughTimerManager {

    private static final int MIN_CLICK_THROUGH_TIMER = 5;
    private static final int MAX_CLICK_THROUGH_TIMER = 35;
    private static final int DEFAULT_CLICK_THROUGH_TIMER = 10;

    public static int getClickThroughTimer(Integer remoteConfigClickThroughTimer) {
        if (remoteConfigClickThroughTimer != null) {
            if (remoteConfigClickThroughTimer > MAX_CLICK_THROUGH_TIMER) {
                return MAX_CLICK_THROUGH_TIMER * 1000;
            } else if (remoteConfigClickThroughTimer < MIN_CLICK_THROUGH_TIMER) {
                return MIN_CLICK_THROUGH_TIMER * 1000;
            } else {
                return remoteConfigClickThroughTimer * 1000;
            }
        } else {
            return DEFAULT_CLICK_THROUGH_TIMER * 1000;
        }
    }

    public interface ClickThroughTimerListener {
        void onClickThroughTriggered();
    }
}
