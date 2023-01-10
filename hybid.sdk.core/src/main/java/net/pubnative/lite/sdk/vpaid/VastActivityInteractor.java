package net.pubnative.lite.sdk.vpaid;

public class VastActivityInteractor {

    private static VastActivityInteractor instance;
    private static boolean activityVisible;
    private boolean isDependentOnActivityLifecycle = false;

    private VastActivityInteractor() {
        activityVisible = true;
    }

    public static VastActivityInteractor getInstance() {
        if (instance == null) {
            instance = new VastActivityInteractor();
        }
        return instance;
    }

    public void activityResumed() {
        activityVisible = true;
    }

    public void activityStarted() {
        activityVisible = true;
        isDependentOnActivityLifecycle = true;
    }

    public void activityPaused() {
        activityVisible = false;
    }

    public boolean isActivityVisible() {
        return activityVisible;
    }

    public boolean isDependentOnActivityLifecycle() {
        return isDependentOnActivityLifecycle;
    }

    public void activityDestroyed() {
        activityVisible = false;
        isDependentOnActivityLifecycle = false;
    }
}
