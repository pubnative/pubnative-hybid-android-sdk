// HyBid SDK License
//
// https://github.com/pubnative/pubnative-hybid-android-sdk/blob/main/LICENSE
//
package net.pubnative.lite.sdk.db;

import net.pubnative.lite.sdk.utils.json.BindField;
import net.pubnative.lite.sdk.utils.json.JsonModel;

import org.json.JSONObject;

public class SessionImpression extends JsonModel {
    @BindField
    private Long timestamp;
    @BindField
    private Long age_of_app;
    @BindField
    private String zone_id;
    @BindField
    private Long session_duration;
    @BindField
    private Integer count;

    public SessionImpression() {
    }

    public SessionImpression(JSONObject jsonObject) {
        try {
            fromJson(jsonObject);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public Long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Long timestamp) {
        this.timestamp = timestamp;
    }

    public Long getAgeOfApp() {
        return age_of_app;
    }

    public void setAgeOfApp(Long age_of_app) {
        this.age_of_app = age_of_app;
    }

    public String getZoneId() {
        return zone_id;
    }

    public void setZoneId(String zone_id) {
        this.zone_id = zone_id;
    }

    public Long getSessionDuration() {
        return session_duration;
    }

    public void setSessionDuration(Long session_duration) {
        this.session_duration = session_duration;
    }

    public Integer getCount() {
        if (count == null) return 0;
        return count;
    }

    public void setCount(Integer count) {
        this.count = count;
    }
}
