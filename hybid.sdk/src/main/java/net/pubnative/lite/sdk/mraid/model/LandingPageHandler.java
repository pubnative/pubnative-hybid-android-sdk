package net.pubnative.lite.sdk.mraid.model;

import android.text.TextUtils;
import android.util.Base64;

import androidx.annotation.NonNull;

import net.pubnative.lite.sdk.models.Ad;
import net.pubnative.lite.sdk.mraid.internal.MRAIDLog;
import net.pubnative.lite.sdk.utils.Logger;

public class LandingPageHandler {

    private static final String LANDING_PAGE_HANDLER_TAG = LandingPageHandler.class.getSimpleName();

    private static final int LANDING_PAGE_CLOSE_DELAY = 30000;
    private final Boolean isLandingPageEnabled;
    private String customisationString;
    private String landingBehaviourString;
    private Integer landingPageDelay = 30000;
    private boolean isFinalPage = false;
    private final Integer SKIP_OFFSET_ADJUSTMENT = 1000;

    private boolean isTimerFinished = false;

    private LandingPageCallback callback;

    public LandingPageHandler(@NonNull Ad ad) {
        isLandingPageEnabled = ad.isLandingPage();
    }

    public Boolean isLandingPageEnabled() {
        return isLandingPageEnabled != null && isLandingPageEnabled;
    }

    public String getCustomisationString() {
        return customisationString;
    }

    public void setCustomisationString(String customisationString) {
        this.customisationString = customisationString;
    }

    public String getLandingBehaviourString() {
        return landingBehaviourString;
    }

    public void setLandingBehaviourString(String landingBehaviourString) {
        this.landingBehaviourString = landingBehaviourString;
    }

    public Integer getLandingPageDelay() {
        return landingPageDelay;
    }

    public void setLandingPageDelay(Integer landingPageDelay) {
        this.landingPageDelay = landingPageDelay;
    }

    public boolean isFinalPage() {
        return isFinalPage;
    }

    public int getUpdatedDelay() {
        if (landingPageDelay >= SKIP_OFFSET_ADJUSTMENT)
            landingPageDelay = landingPageDelay - SKIP_OFFSET_ADJUSTMENT;
        return landingPageDelay;
    }

    public void setIsTimerFinished(boolean b) {
        isTimerFinished = b;
    }

    public boolean isTimerFinished() {
        return isTimerFinished;
    }


    //-----Parsing-----
    public void parseAdExperienceUrl(String commandUrl) {
        MRAIDLog.d(LANDING_PAGE_HANDLER_TAG, "parseAdExperienceUrl " + commandUrl);

        String setCustomisationPattern = "verveadexperience://setcustomisation\\?text=(.+)";
        String landingBehaviourPattern = "verveadexperience://landingbehaviour\\?text=(.+)";
        String closeDelayPattern = "verveadexperience://closedelay\\?text=(.+)";
        String finalPagePattern = "verveadexperience://setfinalpage";

        try {
            if (commandUrl.matches(setCustomisationPattern)) {
                String base64Text = commandUrl.replaceFirst(setCustomisationPattern, "$1");
                byte[] decodedBytes = Base64.decode(base64Text, Base64.DEFAULT);
                customisationString = new String(decodedBytes);
                if (!TextUtils.isEmpty(customisationString)) {
                    if (callback != null) {
                        callback.setLandingPageUseCustomClose(false);
                        callback.setLandingPageSkipTimer();
                    }
                }
            } else if (commandUrl.matches(landingBehaviourPattern)) {
                String base64Text = commandUrl.replaceFirst(landingBehaviourPattern, "$1");
                byte[] decodedBytes = Base64.decode(base64Text, Base64.DEFAULT);
                landingBehaviourString = new String(decodedBytes);
            } else if (commandUrl.matches(closeDelayPattern)) {
                String base64Text = commandUrl.replaceFirst(closeDelayPattern, "$1");
                byte[] decodedBytes = Base64.decode(base64Text, Base64.DEFAULT);
                String delayString = new String(decodedBytes);
                try {
                    landingPageDelay = Integer.parseInt(delayString);
                } catch (NumberFormatException e) {
                    // do nothing
                    Logger.d(LANDING_PAGE_HANDLER_TAG, "Error parsing Landing Page Delay: " + e);
                }
                validateDelay();
            } else if (commandUrl.matches(finalPagePattern)) {
                isFinalPage = true;
                handleLandingPageBehavior();
            }
        } catch (RuntimeException e) {
            Logger.d(LANDING_PAGE_HANDLER_TAG, "Error parsing Ad Experience: " + e);
        }
    }

    private void handleLandingPageBehavior() {
        if (landingBehaviourString != null) {
            switch (landingBehaviourString) {
                case "ic":
                    if (callback != null)
                        callback.cancelLandingPageBehaviour();
                    break;
                case "c":
                    if (callback != null) {
                        if (!isTimerFinished) {
                            callback.showCountDownTimer();
                        }
                    }
                    break;
                case "nc":
                    if (callback != null) {
                        callback.hideCountDownTimer();
                    }
                    break;
            }
        } else {
            if (callback != null) {
                callback.showCountDownTimer();
            }
        }
    }

    private void validateDelay() {
        if (landingPageDelay < 0 || landingPageDelay > 30000) {
            landingPageDelay = 30000;
        }
    }

    public void setCallback(LandingPageCallback callback) {
        this.callback = callback;
    }

    public interface LandingPageCallback {
        void setLandingPageUseCustomClose(boolean b);

        void setLandingPageSkipTimer();

        void cancelLandingPageBehaviour();

        void showCountDownTimer();

        void hideCountDownTimer();
    }
}