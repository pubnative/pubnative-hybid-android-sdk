// The MIT License (MIT)
//
// Copyright (c) 2020 PubNative GmbH
//
// Permission is hereby granted, free of charge, to any person obtaining a copy
// of this software and associated documentation files (the "Software"), to deal
// in the Software without restriction, including without limitation the rights
// to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
// copies of the Software, and to permit persons to whom the Software is
// furnished to do so, subject to the following conditions:
//
// The above copyright notice and this permission notice shall be included in all
// copies or substantial portions of the Software.
//
// THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
// IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
// FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
// AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
// LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
// OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
// SOFTWARE.
//
package net.pubnative.lite.sdk;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.provider.Settings;
import android.text.TextUtils;

import net.pubnative.lite.sdk.consent.CheckConsentRequest;
import net.pubnative.lite.sdk.consent.UserConsentActivity;
import net.pubnative.lite.sdk.consent.UserConsentRequest;
import net.pubnative.lite.sdk.location.GeoIpRequest;
import net.pubnative.lite.sdk.consent.models.UserConsentRequestModel;
import net.pubnative.lite.sdk.consent.models.UserConsentResponseModel;
import net.pubnative.lite.sdk.consent.models.UserConsentResponseStatus;
import net.pubnative.lite.sdk.utils.CountryUtils;
import net.pubnative.lite.sdk.utils.HyBidAdvertisingId;
import net.pubnative.lite.sdk.utils.Logger;
import net.pubnative.lite.sdk.utils.PNAsyncUtils;
import net.pubnative.lite.sdk.utils.PNCrypto;

import java.util.Locale;

public class UserDataManager {
    private static final String TAG = UserDataManager.class.getSimpleName();

    private static final String PREFERENCES_CONSENT = "net.pubnative.lite.dataconsent";
    private static final String KEY_GDPR_CONSENT_STATE = "gdpr_consent_state";
    private static final String KEY_GDPR_ADVERTISING_ID = "gdpr_advertising_id";
    private static final String KEY_CCPA_PUBLIC_CONSENT = "IABUSPrivacy_String";
    private static final String KEY_GDPR_PUBLIC_CONSENT = "IABConsent_ConsentString";
    private static final String KEY_GDPR_TCF_2_PUBLIC_CONSENT = "IABTCF_TCString";
    private static final String KEY_SUBJECT_TO_GDPR_PUBLIC = "IABConsent_SubjectToGDPR";
    private static final String KEY_CMP_PRESENT_PUBLIC = "IABConsent_CMPPresent";
    private static final String KEY_CCPA_CONSENT = "ccpa_consent";
    private static final String KEY_GDPR_CONSENT = "gdpr_consent";
    private static final String DEVICE_ID_TYPE = "gaid";

    private static final int CONSENT_STATE_ACCEPTED = 1;
    private static final int CONSENT_STATE_DENIED = 0;

    interface UserDataInitialisationListener {
        void onDataInitialised(boolean success);
    }

    private final Context mContext;
    private final String mAppToken;
    private String mAdvertisingId;
    private final SharedPreferences mPreferences;
    private final SharedPreferences mAppPreferences;
    private boolean inGDPRZone = false;

    public UserDataManager(Context context, String appToken) {
        mContext = context.getApplicationContext();
        mAppToken = appToken;
        mPreferences = mContext.getSharedPreferences(PREFERENCES_CONSENT, Context.MODE_PRIVATE);
        mAppPreferences = PreferenceManager.getDefaultSharedPreferences(mContext.getApplicationContext());
        mAppPreferences.registerOnSharedPreferenceChangeListener(mAppPrefsListener);
    }

    public void initialize(String advertisingId, UserDataInitialisationListener initialisationListener) {
        mAdvertisingId = advertisingId;
        determineUserZone(initialisationListener);
    }

    @Deprecated
    public String getConsentPageLink() {
        return "https://cdn.pubnative.net/static/consent/consent.html";
    }

    @Deprecated
    public String getPrivacyPolicyLink() {
        return "https://pubnative.net/privacy-notice/";
    }

    @Deprecated
    public String getVendorListLink() {
        return "https://pubnative.net/monetization-partners/";
    }

    @Deprecated
    public boolean shouldAskConsent() {
        return gdprApplies() && !askedForGDPRConsent();
    }

    public boolean canCollectData() {
        if (gdprApplies()) {
            if (askedForGDPRConsent()) {
                switch (mPreferences.getInt(KEY_GDPR_CONSENT_STATE, CONSENT_STATE_DENIED)) {
                    case CONSENT_STATE_ACCEPTED:
                        return true;
                    case CONSENT_STATE_DENIED:
                        return false;
                    default:
                        return false;
                }
            } else {
                return false;
            }
        } else {
            return true;
        }
    }

    /**
     * ]
     * Only for internal use in the ad request
     *
     * @return if consent has explicitly been denied.
     */
    public boolean isConsentDenied() {
        return mPreferences.contains(KEY_GDPR_CONSENT_STATE) && mPreferences.getInt(KEY_GDPR_CONSENT_STATE, CONSENT_STATE_DENIED) == CONSENT_STATE_DENIED;
    }

    /**
     * This method is being deprecated
     *
     * @deprecated use {@link #setIABGDPRConsentString(String gdprConsentString)} instead.
     */
    @Deprecated
    public void grantConsent() {
        processConsent(true);
    }

    /**
     * This method is being deprecated
     *
     * @deprecated use {@link #removeIABGDPRConsentString()} instead.
     */
    @Deprecated
    public void denyConsent() {
        processConsent(false);
    }

    /**
     * This method is being deprecated
     *
     * @deprecated use {@link #removeIABGDPRConsentString()} instead.
     */
    @Deprecated
    public void revokeConsent() {
        denyConsent();
    }

    private void processConsent(final boolean given) {
        String advertisingId = HyBid.getDeviceInfo().getAdvertisingId();
        if (!TextUtils.isEmpty(advertisingId)) {

            notifyConsentGiven(advertisingId, given);
        } else {
            try {
                PNAsyncUtils.safeExecuteOnExecutor(new HyBidAdvertisingId(mContext, new HyBidAdvertisingId.Listener() {
                    @Override
                    public void onHyBidAdvertisingIdFinish(String advertisingId, Boolean limitTracking) {
                        if (TextUtils.isEmpty(advertisingId)) {
                            Logger.e(TAG, "Consent request failed with an empty advertising ID.");
                        } else {
                            notifyConsentGiven(advertisingId, given);
                        }
                    }
                }));
            } catch (Exception exception) {
                Logger.e(TAG, "Error executing HyBidAdvertisingId AsyncTask");
            }
        }
    }

    private void notifyConsentGiven(String advertisingId, final boolean given) {
        setConsentState(given ? CONSENT_STATE_ACCEPTED : CONSENT_STATE_DENIED);
        UserConsentRequestModel requestModel = new UserConsentRequestModel(
                advertisingId,
                DEVICE_ID_TYPE, given);

        UserConsentRequest request = new UserConsentRequest();
        request.doRequest(mContext, HyBid.getAppToken(), requestModel, new UserConsentRequest.UserConsentListener() {
            @Override
            public void onSuccess(UserConsentResponseModel model) {
                if (model.getStatus().equals(UserConsentResponseStatus.OK)) {
                    Logger.d(TAG, String.format(Locale.ENGLISH, "%s user consent has been notified", given ? "Positive" : "Negative"));
                }
            }

            @Override
            public void onFailure(Throwable error) {
                Logger.e(TAG, error.getMessage());
            }
        });
    }

    private void determineUserZone(final UserDataInitialisationListener listener) {
        GeoIpRequest request = new GeoIpRequest();
        request.fetchGeoIp(mContext, new GeoIpRequest.GeoIpRequestListener() {
            @Override
            public void onSuccess(String countryCode) {
                if (TextUtils.isEmpty(countryCode)) {
                    Logger.w(TAG, "No country code was obtained. The default value will be used, therefore no user data consent will be required.");
                    if (listener != null) {
                        listener.onDataInitialised(false);
                    }
                } else {
                    inGDPRZone = CountryUtils.isGDPRCountry(countryCode);

                    if (inGDPRZone && !askedForGDPRConsent()) {
                        checkConsentGiven(listener);
                    } else {
                        if (listener != null) {
                            listener.onDataInitialised(true);
                        }
                    }
                }
            }

            @Override
            public void onFailure(Throwable exception) {
                Logger.e(TAG, exception.getMessage());
                if (listener != null) {
                    listener.onDataInitialised(false);
                }
            }
        });
    }

    private void checkConsentGiven(final UserDataInitialisationListener listener) {
        CheckConsentRequest checkRequest = new CheckConsentRequest();
        checkRequest.checkConsent(mContext, HyBid.getAppToken(), HyBid.getDeviceInfo().getAdvertisingId(), new CheckConsentRequest.CheckConsentListener() {
            @Override
            public void onSuccess(UserConsentResponseModel model) {
                if (model.getStatus().equals(UserConsentResponseStatus.OK)) {
                    if (model.getConsent() != null) {
                        if (model.getConsent().isConsented()) {
                            setConsentState(CONSENT_STATE_ACCEPTED);
                        } else {
                            setConsentState(CONSENT_STATE_DENIED);
                        }
                    }

                    if (listener != null) {
                        listener.onDataInitialised(true);
                    }
                }
            }

            @Override
            public void onFailure(Throwable error) {
                Logger.e(TAG, error.getMessage());
                if (listener != null) {
                    listener.onDataInitialised(false);
                }
            }
        });
    }


    public boolean gdprApplies() {
        return inGDPRZone;
    }

    private boolean askedForGDPRConsent() {
        boolean askedForConsent = mPreferences.contains(KEY_GDPR_CONSENT_STATE);

        if (askedForConsent) {
            String gaid = mPreferences.getString(KEY_GDPR_ADVERTISING_ID, "");

            if (!TextUtils.isEmpty(gaid) && !gaid.equals(HyBid.getDeviceInfo().getAdvertisingId())) {
                askedForConsent = false;
            }
        }

        return askedForConsent;
    }

    @Deprecated
    public void showConsentRequestScreen(Context context) {
        Intent intent = getConsentScreenIntent(context);
        context.startActivity(intent);
    }

    @Deprecated
    public Intent getConsentScreenIntent(Context context) {
        return new Intent(context, UserConsentActivity.class);
    }

    private void setConsentState(int consentState) {
        if (consentState != CONSENT_STATE_ACCEPTED && consentState != CONSENT_STATE_DENIED) {
            throw new RuntimeException("Illegal consent state provided");
        } else {
            SharedPreferences.Editor editor = mPreferences.edit();
            editor.putString(KEY_GDPR_ADVERTISING_ID, HyBid.getDeviceInfo().getAdvertisingId());
            editor.putInt(KEY_GDPR_CONSENT_STATE, consentState);
            editor.apply();
        }
    }

    private final SharedPreferences.OnSharedPreferenceChangeListener mAppPrefsListener = new SharedPreferences.OnSharedPreferenceChangeListener() {
        @Override
        public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
            if (!TextUtils.isEmpty(key)) {
                switch (key) {
                    case KEY_GDPR_PUBLIC_CONSENT: {
                        String consentString = sharedPreferences.getString(KEY_GDPR_PUBLIC_CONSENT, null);
                        if (!TextUtils.isEmpty(consentString)) {
                            setIABGDPRConsentString(consentString);
                        } else {
                            removeIABGDPRConsentString();
                        }
                        break;
                    }
                    case KEY_GDPR_TCF_2_PUBLIC_CONSENT: {
                        String consentString = sharedPreferences.getString(KEY_GDPR_TCF_2_PUBLIC_CONSENT, null);
                        if (!TextUtils.isEmpty(consentString)) {
                            setIABGDPRConsentString(consentString);
                        } else {
                            removeIABGDPRConsentString();
                        }
                        break;
                    }
                    case KEY_CCPA_PUBLIC_CONSENT: {
                        String consentString = sharedPreferences.getString(KEY_CCPA_PUBLIC_CONSENT, null);
                        if (!TextUtils.isEmpty(consentString)) {
                            setIABUSPrivacyString(consentString);
                        } else {
                            removeIABUSPrivacyString();
                        }
                        break;
                    }
                }
            }
        }
    };

    //------------------------------------------- CCPA ---------------------------------------------

    public void setIABUSPrivacyString(String IABUSPrivacyString) {
        mPreferences.edit().putString(KEY_CCPA_CONSENT, IABUSPrivacyString).apply();
    }

    public String getIABUSPrivacyString() {
        return mPreferences.getString(KEY_CCPA_CONSENT, null);
    }

    public void removeIABUSPrivacyString() {
        mPreferences.edit().remove(KEY_CCPA_CONSENT).apply();
    }

    public boolean isCCPAOptOut() {
        String usPrivacyString = getIABUSPrivacyString();
        if (!TextUtils.isEmpty(usPrivacyString) && usPrivacyString.length() >= 3) {
            char optOutChar = usPrivacyString.charAt(2);
            if (optOutChar == 'y' || optOutChar == 'Y') {
                return true;
            } else {
                return false;
            }
        } else {
            return false;
        }
    }

    //------------------------------------------- GDPR ---------------------------------------------

    public void setIABGDPRConsentString(String gdprConsentString) {
        mPreferences.edit().putString(KEY_GDPR_CONSENT, gdprConsentString).apply();
    }

    public String getIABGDPRConsentString() {
        String consentString = mPreferences.getString(KEY_GDPR_CONSENT, null);
        if (TextUtils.isEmpty(consentString)) {
            consentString = mAppPreferences.getString(KEY_GDPR_TCF_2_PUBLIC_CONSENT, null);
            if (TextUtils.isEmpty(consentString)) {
                consentString = mAppPreferences.getString(KEY_GDPR_PUBLIC_CONSENT, null);
            }
        }
        return consentString;
    }

    public void removeIABGDPRConsentString() {
        mPreferences.edit().remove(KEY_GDPR_CONSENT).apply();
    }
}
