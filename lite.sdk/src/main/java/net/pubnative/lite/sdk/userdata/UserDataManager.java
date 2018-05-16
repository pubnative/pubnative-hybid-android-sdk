package net.pubnative.lite.sdk.userdata;

public class UserDataManager {
    private static final String LANGUAGE_ENGLISH = "en";

    public String getPrivacyPolicyLink() {
        return getPrivacyPolicyLink(LANGUAGE_ENGLISH);
    }

    public String getPrivacyPolicyLink(String language) {
        String baseUrl = "https://pubnative.net/privacy-policy/";
        return baseUrl;
    }

    public String getVendorListLink() {
        return getVendorListLink(LANGUAGE_ENGLISH);
    }

    public String getVendorListLink(String language) {
        String baseUrl = "https://pubnative.net/vendor-list/";
        return baseUrl;
    }
}
