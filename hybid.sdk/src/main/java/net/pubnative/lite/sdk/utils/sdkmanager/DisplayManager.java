package net.pubnative.lite.sdk.utils.sdkmanager;

import android.text.TextUtils;

import net.pubnative.lite.sdk.BuildConfig;
import net.pubnative.lite.sdk.models.IntegrationType;

import java.util.Locale;

public class DisplayManager {

    private final String displayManagerName;
    private final String displayManagerVer;
    private final Boolean isWrapped;
    private static final String DISPLAY_MANAGER_ENGINE = "sdkandroid";

    private DisplayManager(Builder builder) {
        this.displayManagerName = builder.displayManagerName;
        this.displayManagerVer = builder.displayManagerVer;
        this.isWrapped = builder.isWrapped;
    }

    public String getDisplayManagerName() {
        return displayManagerName;
    }

    public String getDisplayManagerVersion() {
        return getDisplayManagerVersion(IntegrationType.IN_APP_BIDDING);
    }

    public String getDisplayManagerVersion(IntegrationType integrationType) {
        return getDisplayManagerVersion(null, integrationType);
    }

    public String getDisplayManagerVersion(String mediationVendor, IntegrationType integrationType) {
        if (isWrapped) {
            if (!TextUtils.isEmpty(displayManagerVer)) {
                return displayManagerVer;
            } else {
                return String.format("%s_%s", "sdk", BuildConfig.SDK_WRAPPER_VERSION);
            }
        }
        String mediationValue = "";

        if (!TextUtils.isEmpty(mediationVendor)) {
            mediationValue = String.format(Locale.ENGLISH, "_%s", mediationVendor);
        }

        return String.format(Locale.ENGLISH, "%s_%s%s_%s",
                DISPLAY_MANAGER_ENGINE, integrationType.getCode(), mediationValue, BuildConfig.SDK_WRAPPER_VERSION);
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private Boolean isWrapped = false;
        private String displayManagerName;
        private String displayManagerVer;

        public Builder setDisplayManagerName(String displayManagerName) {
            this.displayManagerName = displayManagerName;
            return this;
        }

        public Builder setDisplayManagerVersion(String displayManagerVer) {
            this.displayManagerVer = displayManagerVer;
            return this;
        }

        public Builder setIsWrapped(Boolean isWrapped) {
            this.isWrapped = isWrapped;
            return this;
        }

        public DisplayManager build() {
            return new DisplayManager(this);
        }
    }
}
