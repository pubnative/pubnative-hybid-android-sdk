// HyBid SDK License
//
// https://github.com/pubnative/pubnative-hybid-android-sdk/blob/main/LICENSE
//
package net.pubnative.lite.sdk;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.text.TextUtils;

import net.pubnative.lite.sdk.consent.UserConsentActivity;
import net.pubnative.lite.sdk.utils.HyBidAdvertisingId;
import net.pubnative.lite.sdk.utils.Logger;

import java.util.concurrent.RejectedExecutionException;

public class UserDataManager {
    private static final String TAG = UserDataManager.class.getSimpleName();

    private static final String PREFERENCES_CONSENT = "net.pubnative.lite.dataconsent";
    private static final String KEY_GDPR_CONSENT_STATE = "gdpr_consent_state";
    private static final String KEY_GDPR_ADVERTISING_ID = "gdpr_advertising_id";
    private static final String KEY_CCPA_PUBLIC_CONSENT = "IABUSPrivacy_String";
    private static final String KEY_GDPR_PUBLIC_CONSENT = "IABConsent_ConsentString";
    private static final String KEY_GDPR_TCF_2_PUBLIC_CONSENT = "IABTCF_TCString";
    private static final String KEY_GDPR_APPLIES = "IABTCF_gdprApplies";
    private static final String KEY_CCPA_CONSENT = "ccpa_consent";
    private static final String KEY_GDPR_CONSENT = "gdpr_consent";
    private static final String KEY_PUBLIC_GPP_STRING = "IABGPP_HDR_GppString";
    private static final String KEY_PUBLIC_GPP_ID = "IABGPP_GppSID";
    private static final String KEY_GPP_STRING = "gpp_string";
    private static final String KEY_GPP_ID = "gpp_id";
    private static final int CONSENT_STATE_ACCEPTED = 1;
    private static final int CONSENT_STATE_DENIED = 0;

    private final Context mContext;
    private final SharedPreferences mPreferences;
    private final SharedPreferences mAppPreferences;

    public UserDataManager(Context context) {
        mContext = context.getApplicationContext();
        mPreferences = mContext.getSharedPreferences(PREFERENCES_CONSENT, Context.MODE_PRIVATE);
        mAppPreferences = PreferenceManager.getDefaultSharedPreferences(mContext.getApplicationContext());
        if (mAppPreferences != null)
            mAppPreferences.registerOnSharedPreferenceChangeListener(mAppPrefsListener);
        updatePublicConsent(mAppPreferences);
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
    public synchronized boolean isConsentDenied() {
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

    private synchronized void processConsent(final boolean given) {
        String deviceInfoAdvertisingId = HyBid.getDeviceInfo().getAdvertisingId();
        if (!TextUtils.isEmpty(deviceInfoAdvertisingId)) {
            notifyConsentGiven(deviceInfoAdvertisingId, given);
        } else {
            try {
                HyBidAdvertisingId advertisingIdTask = new HyBidAdvertisingId(mContext);
                advertisingIdTask.execute((advertisingId, limitTracking) -> {
                    if (TextUtils.isEmpty(advertisingId)) {
                        Logger.e(TAG, "Consent request failed with an empty advertising ID.");
                    } else {
                        notifyConsentGiven(advertisingId, given);
                    }
                });
            } catch (RejectedExecutionException exception) {
                Logger.e(TAG, "processConsent", exception);
                HyBid.reportException(exception);
            } catch (Exception exception) {
                Logger.e(TAG, "Error executing HyBidAdvertisingId AsyncTask");
                HyBid.reportException(exception);
            }
        }
    }

    private synchronized void notifyConsentGiven(String advertisingId, final boolean given) {
        setConsentState(given ? CONSENT_STATE_ACCEPTED : CONSENT_STATE_DENIED);
    }

    public synchronized boolean gdprApplies() {
        if (mAppPreferences == null || !mAppPreferences.contains(KEY_GDPR_APPLIES)) {
            return false;
        }

        try {
            Object gdprValue = mAppPreferences.getAll().get(KEY_GDPR_APPLIES);

            int gdprAppliesFlag = getGdprAppliesFlag(gdprValue);

            return gdprAppliesFlag == 1;

        } catch (Exception e) {
            Logger.e("UserDataManager", "Error reading GDPR value.", e);
            return false;
        }
    }

    private static int getGdprAppliesFlag(Object gdprValue) {
        int gdprAppliesFlag = 0;

        // Handle null value explicitly
        if (gdprValue == null) {
            Logger.w("UserDataManager", "GDPR value is null. Defaulting to 0.");
            return gdprAppliesFlag;
        }
        if (gdprValue instanceof String) {
            String gdprString = (String) gdprValue;
            if ("1".equals(gdprString) || "true".equalsIgnoreCase(gdprString)) {
                gdprAppliesFlag = 1;
            }
        } else if (gdprValue instanceof Integer) {
            gdprAppliesFlag = (Integer) gdprValue;
        } else if (gdprValue instanceof Boolean) {
            gdprAppliesFlag = ((Boolean) gdprValue) ? 1 : 0;
        }
        return gdprAppliesFlag;
    }

    private synchronized boolean askedForGDPRConsent() {
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

    private synchronized String getPublicTCFConsent(SharedPreferences sharedPreferences) {
        return sharedPreferences.getString(KEY_GDPR_PUBLIC_CONSENT, "");
    }

    private synchronized String getPublicTCF2Consent(SharedPreferences sharedPreferences) {
        return sharedPreferences.getString(KEY_GDPR_TCF_2_PUBLIC_CONSENT, "");
    }

    private synchronized String getPublicCCPAConsent(SharedPreferences sharedPreferences) {
        return sharedPreferences.getString(KEY_CCPA_PUBLIC_CONSENT, "");
    }

    private synchronized void updatePublicConsent(SharedPreferences publicPrefs) {
        if (publicPrefs != null) {
            String tcf2Consent = getPublicTCF2Consent(publicPrefs);
            String tcf1Consent = getPublicTCFConsent(publicPrefs);
            String ccpaConsent = getPublicCCPAConsent(publicPrefs);
            String gppString = getPublicGppString(publicPrefs);
            String gppId = getPublicGppId(publicPrefs);

            if (!TextUtils.isEmpty(tcf2Consent)) {
                setIABGDPRConsentString(tcf2Consent);
            } else if (!TextUtils.isEmpty(tcf1Consent)) {
                setIABGDPRConsentString(tcf1Consent);
            }

            if (!TextUtils.isEmpty(ccpaConsent)) {
                setIABUSPrivacyString(ccpaConsent);
            }

            if (!TextUtils.isEmpty(gppString)) {
                setGppString(gppString);
            }

            if (!TextUtils.isEmpty(gppId)) {
                setGppSid(gppId);
            }
        }
    }

    private final SharedPreferences.OnSharedPreferenceChangeListener mAppPrefsListener = new SharedPreferences.OnSharedPreferenceChangeListener() {
        @Override
        public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
            if (!TextUtils.isEmpty(key)) {
                switch (key) {
                    case KEY_GDPR_PUBLIC_CONSENT: {
                        String consentString = getPublicTCFConsent(sharedPreferences);
                        if (!TextUtils.isEmpty(consentString)) {
                            setIABGDPRConsentString(consentString);
                        } else {
                            removeIABGDPRConsentString();
                        }
                        break;
                    }
                    case KEY_GDPR_TCF_2_PUBLIC_CONSENT: {
                        String consentString = getPublicTCF2Consent(sharedPreferences);
                        if (!TextUtils.isEmpty(consentString)) {
                            setIABGDPRConsentString(consentString);
                        } else {
                            removeIABGDPRConsentString();
                        }
                        break;
                    }
                    case KEY_CCPA_PUBLIC_CONSENT: {
                        String consentString = getPublicCCPAConsent(sharedPreferences);
                        if (!TextUtils.isEmpty(consentString)) {
                            setIABUSPrivacyString(consentString);
                        } else {
                            removeIABUSPrivacyString();
                        }
                        break;
                    }
                    case KEY_PUBLIC_GPP_STRING: {
                        String gppString = getPublicGppString(sharedPreferences);
                        if (!TextUtils.isEmpty(gppString)) {
                            setGppString(gppString);
                        } else {
                            removeGppString();
                        }
                        break;
                    }
                    case KEY_PUBLIC_GPP_ID: {
                        String gppId = getPublicGppId(sharedPreferences);
                        if (!TextUtils.isEmpty(gppId)) {
                            setGppSid(gppId);
                        } else {
                            removeGppSid();
                        }
                        break;
                    }
                }
            }
        }
    };

    //------------------------------------------- CCPA ---------------------------------------------

    public synchronized void setIABUSPrivacyString(String IABUSPrivacyString) {
        if (mPreferences != null) {
            mPreferences.edit().putString(KEY_CCPA_CONSENT, IABUSPrivacyString).apply();
        }
    }

    public synchronized String getIABUSPrivacyString() {
        if (mPreferences != null) {
            return mPreferences.getString(KEY_CCPA_CONSENT, null);
        } else {
            return null;
        }

    }

    public synchronized void removeIABUSPrivacyString() {
        if (mPreferences != null) {
            mPreferences.edit().remove(KEY_CCPA_CONSENT).apply();
        }
    }

    public synchronized boolean isCCPAOptOut() {
        String usPrivacyString = getIABUSPrivacyString();
        if (!TextUtils.isEmpty(usPrivacyString) && usPrivacyString.length() >= 3) {
            char optOutChar = usPrivacyString.charAt(2);
            return optOutChar == 'y' || optOutChar == 'Y';
        } else {
            return false;
        }
    }

    //------------------------------------------- GDPR ---------------------------------------------

    public synchronized void setIABGDPRConsentString(String gdprConsentString) {
        if (mPreferences != null) {
            mPreferences.edit().putString(KEY_GDPR_CONSENT, gdprConsentString).apply();
        }
    }

    public synchronized String getIABGDPRConsentString() {
        if (mPreferences != null) {
            String consentString = mPreferences.getString(KEY_GDPR_CONSENT, null);
            if (TextUtils.isEmpty(consentString)) {
                consentString = mAppPreferences.getString(KEY_GDPR_TCF_2_PUBLIC_CONSENT, null);
                if (TextUtils.isEmpty(consentString)) {
                    consentString = mAppPreferences.getString(KEY_GDPR_PUBLIC_CONSENT, null);
                }
            }
            return consentString;
        } else {
            return null;
        }
    }

    public synchronized void removeIABGDPRConsentString() {
        if (mPreferences != null) {
            mPreferences.edit().remove(KEY_GDPR_CONSENT).apply();
        }
    }

    //------------------------------------------- GPP ---------------------------------------------

    private synchronized String getPublicGppString(SharedPreferences sharedPreferences) {
        return sharedPreferences.getString(KEY_PUBLIC_GPP_STRING, null);
    }

    private synchronized String getPublicGppId(SharedPreferences sharedPreferences) {
        return sharedPreferences.getString(KEY_PUBLIC_GPP_ID, null);
    }

    public synchronized String getGppString() {
        if (mPreferences != null) {
            return mPreferences.getString(KEY_GPP_STRING, null);
        } else {
            return null;
        }
    }

    public synchronized void setGppString(String gppString) {
        if (mPreferences != null) {
            mPreferences.edit().putString(KEY_GPP_STRING, gppString).apply();
        }
    }

    public void removeGppString() {
        if (mPreferences != null) {
            mPreferences.edit().remove(KEY_GPP_STRING).apply();
        }
    }

    public String getGppSid() {
        if (mPreferences != null) {
            return mPreferences.getString(KEY_GPP_ID, null);
        } else {
            return null;
        }
    }

    public void setGppSid(String gppId) {
        if (mPreferences != null) {
            mPreferences.edit().putString(KEY_GPP_ID, gppId).apply();
        }
    }

    public void removeGppSid() {
        if (mPreferences != null) {
            mPreferences.edit().remove(KEY_GPP_ID).apply();
        }
    }

    public void removeGppData() {
        if (mPreferences != null) {
            mPreferences.edit().remove(KEY_GPP_STRING).apply();
            mPreferences.edit().remove(KEY_GPP_ID).apply();
        }
    }
}