package net.pubnative.lite.sdk.db;

public class SessionImpression {

    private Long timestamp;
    private Long age_of_app;
    private Integer zone_id;
    private String event_type;
//    private String ad_format;
    private Long session_duration;
    private Integer count;

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

    public Integer getZoneId() {
        return zone_id;
    }

    public void setZoneId(Integer zone_id) {
        this.zone_id = zone_id;
    }

    public String getEventType() {
        return event_type;
    }

    public void setEventType(String event_type) {
        this.event_type = event_type;
    }

    public Long getSessionDuration() {
        return session_duration;
    }

    public void setSessionDuration(Long session_duration) {
        this.session_duration = session_duration;
    }

    public Integer getCount() {
        return count;
    }

    public void setCount(Integer count) {
        this.count = count;
    }
}
