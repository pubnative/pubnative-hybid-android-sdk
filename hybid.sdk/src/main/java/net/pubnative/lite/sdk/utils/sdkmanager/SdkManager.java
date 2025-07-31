package net.pubnative.lite.sdk.utils.sdkmanager;

import net.pubnative.lite.sdk.viewability.baseom.BaseViewabilityManager;

public class SdkManager {

    private final BaseViewabilityManager visibilityManager;
    private final DisplayManager displayManager;

    private SdkManager(Builder builder) {
        this.visibilityManager = builder.visibilityManager;
        this.displayManager = builder.displayManager;
    }

    public BaseViewabilityManager getVisibilityManager() {
        return visibilityManager;
    }

    public DisplayManager getDisplayManager() {
        return displayManager;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private BaseViewabilityManager visibilityManager;
        private DisplayManager displayManager;

        public Builder visibilityManager(BaseViewabilityManager visibilityManager) {
            this.visibilityManager = visibilityManager;
            return this;
        }

        public Builder displayManager(DisplayManager displayManager) {
            this.displayManager = displayManager;
            return this;
        }

        public SdkManager build() {
            return new SdkManager(this);
        }
    }
}
