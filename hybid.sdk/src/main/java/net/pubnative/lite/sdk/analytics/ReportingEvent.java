// HyBid SDK License
//
// https://github.com/pubnative/pubnative-hybid-android-sdk/blob/main/LICENSE
//
package net.pubnative.lite.sdk.analytics;

import android.os.Bundle;
import android.text.TextUtils;

import net.pubnative.lite.sdk.utils.Logger;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Iterator;

public class ReportingEvent {

    private static final String TAG = ReportingEvent.class.getSimpleName();
    private final JSONObject eventObject;

    public ReportingEvent() {
        eventObject = new JSONObject();
    }

    public synchronized void setCategoryId(String categoryId) {
        setCustomString(Reporting.Key.CATEGORY_ID, categoryId);
    }

    public String getCategoryId() {
        return getCustomString(Reporting.Key.CATEGORY_ID);
    }

    public synchronized void setCampaignId(String campaignId) {
        setCustomString(Reporting.Key.CAMPAIGN_ID, campaignId);
    }

    public String getCampaignId() {
        return getCustomString(Reporting.Key.CAMPAIGN_ID);
    }

    public synchronized void setCreativeId(String creativeId) {
        setCustomString(Reporting.Key.CREATIVE_ID, creativeId);
    }

    public String getCreativeId() {
        return getCustomString(Reporting.Key.CREATIVE_ID);
    }

    public synchronized void setCreativeType(String creativeType) {
        setCustomString(Reporting.Key.CREATIVE_TYPE, creativeType);
    }

    public String getCreativeType() {
        return getCustomString(Reporting.Key.CREATIVE_TYPE);
    }

    public synchronized void setCreative(String creative) {
        setCustomString(Reporting.Key.CREATIVE, creative);
    }

    public String getCreative() {
        return getCustomString(Reporting.Key.CREATIVE);
    }

    public synchronized void setTimestamp(long date) {
        setCustomString(Reporting.Key.TIMESTAMP, String.valueOf(date));
    }

    public synchronized void setTimestamp(String timestamp) {
        setCustomString(Reporting.Key.TIMESTAMP, timestamp);
    }

    public String getTimestamp() {
        return getCustomString(Reporting.Key.TIMESTAMP);
    }

    public synchronized void setEventType(String eventType) {
        setCustomString(Reporting.Key.EVENT_TYPE, eventType);
    }

    public String getEventType() {
        return getCustomString(Reporting.Key.EVENT_TYPE);
    }

    public synchronized void setErrorCode(int errorCode) {
        setCustomInteger(Reporting.Key.ERROR_CODE, errorCode);
    }

    public long getErrorCode() {
        return getCustomInteger(Reporting.Key.ERROR_CODE);
    }

    public synchronized void setErrorMessage(String errorMessage) {
        setCustomString(Reporting.Key.ERROR_MESSAGE, errorMessage);
    }

    public String getErrorMessage() {
        return getCustomString(Reporting.Key.ERROR_MESSAGE);
    }

    public synchronized void setAdFormat(String adFormat) {
        setCustomString(Reporting.Key.AD_FORMAT, adFormat);
    }

    public String getAdFormat() {
        return getCustomString(Reporting.Key.AD_FORMAT);
    }

    public synchronized void setAdSize(String adSize) {
        setCustomString(Reporting.Key.AD_SIZE, adSize);
    }

    public String getAdSize() {
        return getCustomString(Reporting.Key.AD_SIZE);
    }

    public synchronized void setHasEndCard(boolean hasEndCard) {
        setCustomBoolean(Reporting.Key.HAS_END_CARD, hasEndCard);
    }

    public boolean getHasEndCard() {
        return getCustomBoolean(Reporting.Key.HAS_END_CARD);
    }

    public synchronized void setZoneId(String zoneId) {
        setCustomString(Reporting.Key.ZONE_ID, zoneId);
    }

    public String getZoneId() {
        return getCustomString(Reporting.Key.ZONE_ID);
    }

    public synchronized void setAdType(String adType) {
        setCustomString(Reporting.Key.AD_TYPE, adType);
    }

    public String getAdType() {
        return getCustomString(Reporting.Key.AD_TYPE);
    }

    public synchronized void setPlatform(String platform) {
        setCustomString(Reporting.Key.PLATFORM, platform);
    }

    public String getPlatform() {
        return getCustomString(Reporting.Key.PLATFORM);
    }

    public synchronized void setSdkVersion(String sdkVersion) {
        setCustomString(Reporting.Key.HYBID_VERSION, sdkVersion);
    }

    public String getSdkVersion() {
        return getCustomString(Reporting.Key.HYBID_VERSION);
    }

    public synchronized void setAppToken(String appToken) {
        setCustomString(Reporting.Key.APP_TOKEN, appToken);
    }

    public String getAppToken() {
        return getCustomString(Reporting.Key.APP_TOKEN);
    }

    public synchronized void setPlacementId(String placementId) {
        setCustomString(Reporting.Key.PLACEMENT_ID, placementId);
    }

    public String getPlacementId() {
        return getCustomString(Reporting.Key.PLACEMENT_ID);
    }


    public synchronized void setIntegrationType(String integrationType) {
        setCustomString(Reporting.Key.INTEGRATION_TYPE, integrationType);
    }

    public String getIntegrationType() {
        return getCustomString(Reporting.Key.INTEGRATION_TYPE);
    }

    public synchronized void setVast(String vast) {
        setCustomString(Reporting.Key.VAST, vast);
    }

    public String getVast() {
        return getCustomString(Reporting.Key.VAST);
    }

    public synchronized void setSessionDuration(String sessionDuration) {
        setCustomString(Reporting.Key.SESSION_DURATION, sessionDuration);
    }

    public synchronized void setRequestType(String requestType) {
        setCustomString(Reporting.Key.REQUEST_TYPE, requestType);
    }

    public synchronized String getRequestType() {
        return getCustomString(Reporting.Key.REQUEST_TYPE);
    }

    public String getSessionDuration() {
        return getCustomString(Reporting.Key.SESSION_DURATION);
    }

    public synchronized void setImpDepth(String impDepth) {
        setCustomString(Reporting.Key.IMP_DEPTH, impDepth);
    }

    public String getImpDepth() {
        return getCustomString(Reporting.Key.IMP_DEPTH);
    }

    public synchronized void setImpId(String impId) {
        setCustomString(Reporting.Key.IMP_ID, impId);
    }

    public String getImpId() {
        return getCustomString(Reporting.Key.IMP_ID);
    }

    public synchronized void setConfigId(String configId) {
        setCustomString(Reporting.Key.REMOTE_CONFIG_ID, configId);
    }

    public String getConfigId() {
        return getCustomString(Reporting.Key.REMOTE_CONFIG_ID);
    }

    public synchronized void setAgeOfApp(String ageOfApp) {
        setCustomString(Reporting.Key.AGE_OF_APP, ageOfApp);
    }

    public String getAgeOfApp() {
        return getCustomString(Reporting.Key.AGE_OF_APP);
    }

    public synchronized void setCustomString(String key, String value) {
        try {
            if (!TextUtils.isEmpty(value)) {
                eventObject.put(key, value);
            }
        } catch (JSONException e) {
            Logger.e(TAG, e.getMessage());
        }
    }

    public String getCustomString(String key) {
        try {
            return eventObject.getString(key);
        } catch (JSONException e) {
            Logger.e(TAG, e.getMessage());
            return null;
        }
    }

    public void setCustomInteger(String key, long value) {
        try {
            eventObject.put(key, value);
        } catch (JSONException e) {
            Logger.e(TAG, e.getMessage());
        }
    }

    public Long getCustomInteger(String key) {
        try {
            return eventObject.getLong(key);
        } catch (JSONException e) {
            Logger.e(TAG, e.getMessage());
            return null;
        }
    }

    public synchronized void setCustomDecimal(String key, double value) {
        try {
            eventObject.put(key, value);
        } catch (JSONException e) {
            Logger.e(TAG, e.getMessage());
        }
    }

    public Double getCustomDecimal(String key) {
        try {
            return eventObject.getDouble(key);
        } catch (JSONException e) {
            Logger.e(TAG, e.getMessage());
            return null;
        }
    }

    public synchronized void setCustomBoolean(String key, boolean value) {
        try {
            eventObject.put(key, value);
        } catch (JSONException e) {
            Logger.e(TAG, e.getMessage());
        }
    }

    public Boolean getCustomBoolean(String key) {
        try {
            return eventObject.getBoolean(key);
        } catch (JSONException e) {
            Logger.e(TAG, e.getMessage());
            return null;
        }
    }

    public synchronized void setCustomJSONObject(String key, JSONObject jsonObject) {
        try {
            eventObject.put(key, jsonObject);
        } catch (JSONException e) {
            Logger.e(TAG, e.getMessage());
        }
    }

    public JSONObject getCustomJSONObject(String key) {
        try {
            return eventObject.getJSONObject(key);
        } catch (JSONException e) {
            Logger.e(TAG, e.getMessage());
            return null;
        }
    }

    public synchronized void mergeJSONObject(JSONObject source) {
        if (source == null || source.length() == 0) {
            return;
        }

        JSONArray names = source.names();
        try {
            if (names != null) {
                for (int i = 0; i < names.length(); i++) {
                    String name = names.getString(i);
                    eventObject.put(name, source.get(name));
                }
            }
        } catch (JSONException ignored) {
        }
    }

    public synchronized void setCustomJSONArray(String key, JSONArray jsonArray) {
        try {
            eventObject.put(key, jsonArray);
        } catch (JSONException e) {
            Logger.e(TAG, e.getMessage());
        }
    }

    public JSONArray getCustomJSONArray(String key) {
        try {
            return eventObject.getJSONArray(key);
        } catch (JSONException e) {
            Logger.e(TAG, e.getMessage());
            return null;
        }
    }

    public Bundle getEventData() {
        Bundle bundle = new Bundle();
        Iterator<String> iterator = eventObject.keys();
        while (iterator.hasNext()) {
            String key = iterator.next();
            try {
                bundle.putString(key, eventObject.getString(key));
            } catch (JSONException ignored) {

            }
        }
        return bundle;
    }

    public JSONObject getEventObject() {
        return eventObject;
    }
}
