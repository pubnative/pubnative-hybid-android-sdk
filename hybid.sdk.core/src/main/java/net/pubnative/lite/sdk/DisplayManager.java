package net.pubnative.lite.sdk;

import android.text.TextUtils;

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
        return getDisplayManagerVersion(null, integrationType);
    }

    public String getDisplayManagerVersion(String mediationVendor, IntegrationType integrationType) {
        String mediationValue = "";

        if (!TextUtils.isEmpty(mediationVendor)) {
            mediationValue = String.format(Locale.ENGLISH, "_%s", mediationVendor);
        }

        return String.format(Locale.ENGLISH, "%s_%s%s_%s",
                DISPLAY_MANAGER_ENGINE, integrationType.getCode(), mediationValue, BuildConfig.SDK_VERSION);
    }

    public String getDisplayManager() {
        return DISPLAY_MANAGER_NAME;
    }
}
