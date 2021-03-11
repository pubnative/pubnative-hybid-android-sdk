package net.pubnative.lite.sdk.analytics;

import android.text.TextUtils;

import net.pubnative.lite.sdk.utils.Logger;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class ReportingEvent {

    private static final String TAG = ReportingEvent.class.getSimpleName();
    private final JSONObject eventObject;

    public ReportingEvent() {
        eventObject = new JSONObject();
    }

    public ReportingEvent(String adFormat) {
        eventObject = new JSONObject();

        try {
            if (!TextUtils.isEmpty(adFormat)) {
                eventObject.put(Reporting.Key.AD_FORMAT, adFormat);
            }
        } catch (JSONException e) {
            Logger.e(TAG, e.getMessage());
        }
    }

    public ReportingEvent(String adFormat, String adSize) {
        eventObject = new JSONObject();

        try {
            if (!TextUtils.isEmpty(adFormat)) {
                eventObject.put(Reporting.Key.AD_FORMAT, adFormat);
            }

            this.eventObject.put(Reporting.Key.AD_SIZE, adSize);
        } catch (JSONException e) {
            Logger.e(TAG, e.getMessage());
        }
    }

    public void setCategoryId(String categoryId) {
        try {
            if (!TextUtils.isEmpty(categoryId)) {
                eventObject.put(Reporting.Key.CATEGORY_ID, categoryId);
            }
        } catch (JSONException e) {
            Logger.e(TAG, e.getMessage());
        }
    }

    public String getCategoryId() {
        try {
            return eventObject.getString(Reporting.Key.CATEGORY_ID);
        } catch (JSONException e) {
            Logger.e(TAG, e.getMessage());
            return null;
        }
    }

    public void setCampaignId(String campaignId) {
        try {
            if (!TextUtils.isEmpty(campaignId)) {
                eventObject.put(Reporting.Key.CAMPAIGN_ID, campaignId);
            }
        } catch (JSONException e) {
            Logger.e(TAG, e.getMessage());
        }
    }

    public String getCampaignId() {
        try {
            return eventObject.getString(Reporting.Key.CAMPAIGN_ID);
        } catch (JSONException e) {
            Logger.e(TAG, e.getMessage());
            return null;
        }
    }

    public void setCreativeId(String creativeId) {
        try {
            if (!TextUtils.isEmpty(creativeId)) {
                eventObject.put(Reporting.Key.CREATIVE_ID, creativeId);
            }
        } catch (JSONException e) {
            Logger.e(TAG, e.getMessage());
        }
    }

    public String getCreativeId() {
        try {
            return eventObject.getString(Reporting.Key.CREATIVE_ID);
        } catch (JSONException e) {
            Logger.e(TAG, e.getMessage());
            return null;
        }
    }

    public void setTimestamp(long date) {
        String timestamp = String.valueOf(date);
        try {
            if (!TextUtils.isEmpty(timestamp)) {
                eventObject.put(Reporting.Key.TIMESTAMP, timestamp);
            }
        } catch (JSONException e) {
            Logger.e(TAG, e.getMessage());
        }
    }

    public void setTimestamp(String timestamp) {
        try {
            if (!TextUtils.isEmpty(timestamp)) {
                eventObject.put(Reporting.Key.TIMESTAMP, timestamp);
            }
        } catch (JSONException e) {
            Logger.e(TAG, e.getMessage());
        }
    }

    public String getTimestamp() {
        try {
            return eventObject.getString(Reporting.Key.TIMESTAMP);
        } catch (JSONException e) {
            Logger.e(TAG, e.getMessage());
            return null;
        }
    }

    public void setEventType(String eventType) {
        try {
            if (!TextUtils.isEmpty(eventType)) {
                eventObject.put(Reporting.Key.EVENT_TYPE, eventType);
            }
        } catch (JSONException e) {
            Logger.e(TAG, e.getMessage());
        }
    }

    public String getEventType() {
        try {
            return eventObject.getString(Reporting.Key.EVENT_TYPE);
        } catch (JSONException e) {
            Logger.e(TAG, e.getMessage());
            return null;
        }
    }

    public void setAdFormat(String adFormat) {
        try {
            if (!TextUtils.isEmpty(adFormat)) {
                eventObject.put(Reporting.Key.AD_FORMAT, adFormat);
            }
        } catch (JSONException e) {
            Logger.e(TAG, e.getMessage());
        }
    }

    public String getAdFormat() {
        try {
            return eventObject.getString(Reporting.Key.AD_FORMAT);
        } catch (JSONException e) {
            Logger.e(TAG, e.getMessage());
            return null;
        }
    }

    public void setAdSize(String adSize) {
        try {
            eventObject.put(Reporting.Key.AD_SIZE, adSize);
        } catch (JSONException e) {
            Logger.e(TAG, e.getMessage());
        }
    }

    public String getAdSize() {
        try {
            return eventObject.getString(Reporting.Key.AD_SIZE);
        } catch (JSONException e) {
            Logger.e(TAG, e.getMessage());
            return null;
        }
    }

    public void setCustomString(String key, String value) {
        try {
            if (!TextUtils.isEmpty(value)) {
                eventObject.put(key, value);
            }
        } catch (JSONException e) {
            Logger.e(TAG, e.getMessage());
        }
    }

    public void setCustomInteger(String key, long value) {
        try {
            eventObject.put(key, value);
        } catch (JSONException e) {
            Logger.e(TAG, e.getMessage());
        }
    }

    public void setCustomDecimal(String key, double value) {
        try {
            eventObject.put(key, value);
        } catch (JSONException e) {
            Logger.e(TAG, e.getMessage());
        }
    }

    public void setCustomBoolean(String key, boolean value) {
        try {
            eventObject.put(key, value);
        } catch (JSONException e) {
            Logger.e(TAG, e.getMessage());
        }
    }

    public void setCustomJSONObject(String key, JSONObject jsonObject){
        try {
            eventObject.put(key, jsonObject);
        } catch (JSONException e) {
            Logger.e(TAG, e.getMessage());
        }
    }

    public void setCustomJSONArray(String key, JSONArray jsonArray){
        try {
            eventObject.put(key, jsonArray);
        } catch (JSONException e) {
            Logger.e(TAG, e.getMessage());
        }
    }
}
