package net.pubnative.lite.sdk.analytics.tracker;

public class ReportingTracker {

    private final String type;
    private final String url;
    private final int responseCode;

    public ReportingTracker(String type, String url, int responseCode) {
        this.type = type;
        this.url = url;
        this.responseCode = responseCode;
    }

    public String getType() {
        return type;
    }

    public String getUrl() {
        return url;
    }

    public int getResponseCode() {
        return responseCode;
    }
}
