package net.pubnative.lite.sdk.vpaid.macros;

import android.text.TextUtils;

import net.pubnative.lite.sdk.DeviceInfo;
import net.pubnative.lite.sdk.HyBid;
import net.pubnative.lite.sdk.UserDataManager;

public class RegulationMacros {
    private static final String MACRO_LIMIT_AD_TRACKING = "[LIMITADTRACKING]";
    private static final String MACRO_REGULATIONS = "[REGULATIONS]";
    private static final String MACRO_GDPR_CONSENT = "[GDPRCONSENT]";

    private final DeviceInfo mDeviceInfo;
    private final UserDataManager mUserDataManager;

    public RegulationMacros() {
        this(HyBid.getDeviceInfo(), HyBid.getUserDataManager());
    }

    RegulationMacros(DeviceInfo deviceInfo, UserDataManager userDataManager) {
        this.mDeviceInfo = deviceInfo;
        this.mUserDataManager = userDataManager;
    }

    public String processUrl(String url) {
        return url
                .replace(MACRO_LIMIT_AD_TRACKING, getLimitAdTracking())
                .replace(MACRO_REGULATIONS, getRegulations())
                .replace(MACRO_GDPR_CONSENT, getGdprConsent());
    }

    private String getLimitAdTracking() {
        if (mDeviceInfo != null) {
            return mDeviceInfo.limitTracking() ? "1" : "0";
        } else {
            return String.valueOf(MacroDefaultValues.VALUE_UNKNOWN);
        }
    }

    private String getRegulations() {
        StringBuilder regulationBuilder = new StringBuilder();
        if (mUserDataManager != null) {
            if (mUserDataManager.gdprApplies() || !TextUtils.isEmpty(mUserDataManager.getIABGDPRConsentString())) {
                regulationBuilder.append("gdpr");
            }

            if (HyBid.isCoppaEnabled()) {
                if (regulationBuilder.length() > 0) {
                    regulationBuilder.append(",");
                }
                regulationBuilder.append("coppa");
            }
        }
        String regulations = regulationBuilder.toString();
        return TextUtils.isEmpty(regulations) ? String.valueOf(MacroDefaultValues.VALUE_UNKNOWN) : regulations;
    }

    private String getGdprConsent() {
        if (mUserDataManager != null && !TextUtils.isEmpty(mUserDataManager.getIABGDPRConsentString())) {
            return mUserDataManager.getIABGDPRConsentString();
        } else {
            return String.valueOf(MacroDefaultValues.VALUE_UNKNOWN);
        }
    }
}
