// The MIT License (MIT)
//
// Copyright (c) 2018 PubNative GmbH
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
import android.text.TextUtils;

import net.pubnative.lite.sdk.consent.CheckConsentRequest;
import net.pubnative.lite.sdk.consent.UserConsentActivity;
import net.pubnative.lite.sdk.consent.UserConsentRequest;
import net.pubnative.lite.sdk.location.GeoIpRequest;
import net.pubnative.lite.sdk.models.UserConsentRequestModel;
import net.pubnative.lite.sdk.models.UserConsentResponseModel;
import net.pubnative.lite.sdk.models.UserConsentResponseStatus;
import net.pubnative.lite.sdk.utils.CountryUtils;
import net.pubnative.lite.sdk.utils.Logger;

public class UserDataManager {
    private static final String TAG = UserDataManager.class.getSimpleName();

    private static final String PREFERENCES_CONSENT = "net.pubnative.lite.dataconsent";
    private static final String KEY_GDPR_CONSENT_STATE = "gdpr_consent_state";
    private static final String KEY_GDPR_ADVERTISING_ID = "gdpr_advertising_id";
    private static final String KEY_CCPA_CONSENT = "ccpa_consent";
    private static final String DEVICE_ID_TYPE = "gaid";

    private static final int CONSENT_STATE_ACCEPTED = 1;
    private static final int CONSENT_STATE_DENIED = 0;

    private Context mContext;
    private SharedPreferences mPreferences;
    private boolean inGDPRZone = false;

    public UserDataManager(Context context) {
        mContext = context.getApplicationContext();
        mPreferences = mContext.getSharedPreferences(PREFERENCES_CONSENT, Context.MODE_PRIVATE);
    }

    public void initialize(UserDataInitialisationListener initialisationListener) {
        determineUserZone(initialisationListener);
    }

    public String getConsentPageLink() {
        return "https://cdn.pubnative.net/static/consent/consent.html";
    }

    public String getPrivacyPolicyLink() {
        return "https://pubnative.net/privacy-notice/";
    }

    public String getVendorListLink() {
        return "https://pubnative.net/monetization-partners/";
    }

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

    public void grantConsent() {

        notifyConsentGiven();
    }

    public void denyConsent() {
        notifyConsentDenied();
    }

    public void revokeConsent() {
        notifyConsentDenied();
    }

    private void notifyConsentGiven() {
        UserConsentRequestModel requestModel = new UserConsentRequestModel(
                HyBid.getDeviceInfo().getAdvertisingId(),
                DEVICE_ID_TYPE, true);

        UserConsentRequest request = new UserConsentRequest();
        request.doRequest(mContext, HyBid.getAppToken(), requestModel, new UserConsentRequest.UserConsentListener() {
            @Override
            public void onSuccess(UserConsentResponseModel model) {
                if (model.getStatus().equals(UserConsentResponseStatus.OK)) {
                    setConsentState(CONSENT_STATE_ACCEPTED);
                }
            }

            @Override
            public void onFailure(Throwable error) {
                Logger.e(TAG, error.getMessage());
            }
        });
    }

    private void notifyConsentDenied() {
        UserConsentRequestModel requestModel = new UserConsentRequestModel(
                HyBid.getDeviceInfo().getAdvertisingId(),
                DEVICE_ID_TYPE, false);

        UserConsentRequest request = new UserConsentRequest();
        request.doRequest(mContext, HyBid.getAppToken(), requestModel, new UserConsentRequest.UserConsentListener() {
            @Override
            public void onSuccess(UserConsentResponseModel model) {
                if (model.getStatus().equals(UserConsentResponseStatus.OK)) {
                    setConsentState(CONSENT_STATE_DENIED);
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


    private boolean gdprApplies() {
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
            editor.putString(KEY_GDPR_ADVERTISING_ID, HyBid.getDeviceInfo().getAdvertisingId());
            editor.putInt(KEY_GDPR_CONSENT_STATE, consentState);
            editor.apply();
        }
    }

    interface UserDataInitialisationListener {
        void onDataInitialised(boolean success);
    }

    public void setIABUSPrivacyString(Context context, String IABUSPrivacyString){
        SharedPreferences.Editor editor = mPreferences.edit();
        editor.putString(KEY_CCPA_CONSENT, IABUSPrivacyString).apply();
    }

    public String getIABUSPrivacyString(Context context){
        return mPreferences.getString(KEY_CCPA_CONSENT, null);
    }

    public void removeIABUSPrivacyString(Context context){
        SharedPreferences.Editor editor = mPreferences.edit();
        editor.putString(KEY_CCPA_CONSENT, null).apply();
    }

    public boolean isCCPAOptOut(){
        String usPrivacyString = getIABUSPrivacyString(HyBid.getDeviceInfo().getContext());
        if (usPrivacyString.length() >= 3){
            char optOutChar = usPrivacyString.charAt(2);
            if (optOutChar == 'n' || optOutChar == 'N'){
                return false;
            } else {
                return true;
            }
        }
        else {
            return false;
        }
    }
}
