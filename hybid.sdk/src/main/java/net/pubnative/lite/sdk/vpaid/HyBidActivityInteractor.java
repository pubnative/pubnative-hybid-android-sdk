// HyBid SDK License
//
// https://github.com/pubnative/pubnative-hybid-android-sdk/blob/main/LICENSE
//
package net.pubnative.lite.sdk.vpaid;

public class HyBidActivityInteractor {

    private static HyBidActivityInteractor instance;
    private static boolean activityVisible;

    private HyBidActivityInteractor() {
        activityVisible = true;
    }

    public static HyBidActivityInteractor getInstance() {
        if (instance == null) {
            instance = new HyBidActivityInteractor();
        }
        return instance;
    }

    public void activityCreated() {
        activityVisible = true;
    }

    public void activityResumed() {
        activityVisible = true;
    }

    public void activityPaused() {
        activityVisible = false;
    }

    public boolean isActivityVisible() {
        return activityVisible;
    }

    public void activityDestroyed() {
        activityVisible = false;
        instance = null;
    }
}