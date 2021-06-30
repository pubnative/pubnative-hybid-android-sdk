package net.pubnative.lite.sdk;

import net.pubnative.lite.sdk.core.BuildConfig;
import net.pubnative.lite.sdk.models.IntegrationType;

import java.util.Locale;

public class DisplayManager {
    private static final String DISPLAY_MANAGER_NAME = "HyBid";
    private static final String DISPLAY_MANAGER_ENGINE = "sdkandroid";

    public String getDisplayManagerVersion() {
        return getDisplayManagerVersion(IntegrationType.IN_APP_BIDDING);
    }

    public String getDisplayManagerVersion(IntegrationType integrationType) {
        return String.format(Locale.ENGLISH, "%s_%s_%s",
                DISPLAY_MANAGER_ENGINE, integrationType.getCode(), BuildConfig.SDK_VERSION);
    }

    public String getDisplayManager() {
        return DISPLAY_MANAGER_NAME;
    }
}
