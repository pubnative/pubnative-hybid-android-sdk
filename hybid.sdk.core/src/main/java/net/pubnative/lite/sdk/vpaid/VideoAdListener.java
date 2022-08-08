package net.pubnative.lite.sdk.vpaid;

public abstract class VideoAdListener {

    /**
     * Triggered when the AdSpot has successfully loaded the ad content
     */
    public abstract void onAdLoadSuccess();

    /**
     * Triggered when AdSpot failed to load ad content
     *
     * @param info info about reason of fail. The special case - info.isNoAdsFound()
     */
    public abstract void onAdLoadFail(PlayerInfo info);

    /**
     * Triggered when the AdSpot appears on the screen
     */
    public void onAdStarted() {
    }

    /**
     * Triggered when the AdSpot disappears on the screen
     */
    public void onAdDismissed() {
    }

    public void onAdDismissed(int progressPercentage) {
    }

    /**
     * Triggered when the user taps the AdSpot and the AdSpot is about to perform extra actions
     * Those actions may lead to displaying a modal browser or leaving your application.
     */
    public void onAdClicked() {
    }

    /**
     * Triggered only when AdSpot's video was played until the end.
     * It won't be sent if the video was skipped or the AdSpot was dismissed during the displaying process
     */
    public void onAdDidReachEnd() {
    }

    /**
     * Triggered when the AdSpot's loaded ad content is expired.
     * Expiration happens when loaded ad content wasn't displayed during some period of time, approximately 10 minutes.
     * Once the AdSpot is presented on the screen, the expiration is no longer tracked and AdSpot won't
     * receive this message
     */
    public void onAdExpired() {
    }

    /**
     * Triggered when your application is about to go to the background, initiated by the SDK.
     * This may happen in various ways, f.e if user wants open the SDK's browser web page in native browser or clicks
     * on `mailto:` links...
     */
    public void onLeaveApp() {
    }

    public abstract void onAdSkipped();
}
