package net.pubnative.lite.sdk;

import android.content.Context;
import android.content.SharedPreferences;
import android.text.TextUtils;

public class UserDataManager {
    private static final String PREFERENCES_CONSENT = "net.pubnative.lite.dataconsent";
    private static final String KEY_GDPR_CONSENT_UUID = "gdpr_consent_uuid";
    private static final String KEY_GDPR_CONSENT_STATE = "gdpr_consent_state";

    private static final int CONSENT_STATE_ACCEPTED = 1;
    private static final int CONSENT_STATE_DENIED = 0;

    private SharedPreferences mPreferences;
    private boolean inGDPRZone = false;

    public UserDataManager(Context context, String appToken) {
        this(context, appToken, null);
    }

    public UserDataManager(Context context, String appToken, UserDataInitialisationListener initialisationListener) {
        mPreferences = context.getSharedPreferences(PREFERENCES_CONSENT, Context.MODE_PRIVATE);
        determineUserZone(appToken, initialisationListener);
    }

    public String getPrivacyPolicyLink() {
        return "https://pubnative.net/privacy-policy/";
    }

    public String getVendorListLink() {
        return "https://pubnative.net/vendor-list/";
    }

    public boolean shouldAskConsent() {
        return gdprApplies() && !askedForGDPRConsent();
    }

    public void grantConsent() {
        setConsentState(CONSENT_STATE_ACCEPTED);

        //TODO sync with API
    }

    public void denyConsent() {
        setConsentState(CONSENT_STATE_DENIED);

        //TODO sync with API
    }

    private void determineUserZone(String appToken, UserDataInitialisationListener listener) {
        // TODO get country from API
        if (listener != null) {
            listener.onDataInitialised(true);
        }
    }

    private boolean gdprApplies() {
        return inGDPRZone;
    }

    private boolean askedForGDPRConsent() {
        return mPreferences.contains(KEY_GDPR_CONSENT_STATE);
    }

    void setGDPRZone(boolean gdprZone) {
        this.inGDPRZone = gdprZone;
    }

    private void setConsentState(int consentState) {
        if (consentState != CONSENT_STATE_ACCEPTED && consentState != CONSENT_STATE_DENIED) {
            throw new RuntimeException("Illegal consent state provided");
        } else {
            SharedPreferences.Editor editor = mPreferences.edit();
            editor.putInt(KEY_GDPR_CONSENT_STATE, consentState);
            editor.apply();
        }
    }

    private void setGDPRConsentUUID(String uuid) {
        if (TextUtils.isEmpty(uuid)) {
            throw new RuntimeException("Illegal UUID provided");
        } else {
            SharedPreferences.Editor editor = mPreferences.edit();
            editor.putString(KEY_GDPR_CONSENT_UUID, uuid);
            editor.apply();
        }
    }

    interface UserDataInitialisationListener {
        void onDataInitialised(boolean success);
    }
}
