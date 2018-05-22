package net.pubnative.lite.sdk;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.text.TextUtils;
import android.util.Log;

import net.pubnative.lite.sdk.consent.UserConsentActivity;
import net.pubnative.lite.sdk.location.GeoIpRequest;
import net.pubnative.lite.sdk.models.GeoIpResponse;
import net.pubnative.lite.sdk.utils.CountryUtils;
import net.pubnative.lite.sdk.utils.Logger;

public class UserDataManager {
    private static final String TAG = UserDataManager.class.getSimpleName();

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
        determineUserZone(context, initialisationListener);
    }

    public String getConsentPageLink() {
        return "consent_page";
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

    public void revokeConsent() {
        setConsentState(CONSENT_STATE_DENIED);

        //TODO sync with API
    }

    private void determineUserZone(Context context, final UserDataInitialisationListener listener) {
        GeoIpRequest request = new GeoIpRequest();
        request.fetchGeoIp(context, new GeoIpRequest.GeoIpRequestListener() {
            @Override
            public void onSuccess(GeoIpResponse geoIpResponse) {
                String countryCode = geoIpResponse.countryCode;
                boolean initialisedSuccessfully = false;

                if (TextUtils.isEmpty(countryCode)) {
                    Logger.w(TAG, "No country code was obtained. The default value will be used, therefore no user data consent will be required.");
                } else {
                    inGDPRZone = CountryUtils.isGDPRCountry(countryCode);
                    initialisedSuccessfully = true;
                }

                if (listener != null) {
                    listener.onDataInitialised(initialisedSuccessfully);
                }
            }

            @Override
            public void onFailure(Throwable exception) {
                Logger.e(TAG, exception.getMessage());
            }
        });
    }

    private boolean gdprApplies() {
        return inGDPRZone;
    }

    private boolean askedForGDPRConsent() {
        return mPreferences.contains(KEY_GDPR_CONSENT_STATE);
    }

    public void showConsentRequestScreen(Context context) {
        Intent intent = getConsentScreenIntent(context);
        context.startActivity(intent);
    }

    public Intent getConsentScreenIntent(Context context) {
        return new Intent(context, UserConsentActivity.class);
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
