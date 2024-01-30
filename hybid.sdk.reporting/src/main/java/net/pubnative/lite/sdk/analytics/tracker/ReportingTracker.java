package net.pubnative.lite.sdk.analytics.tracker;

public class ReportingTracker {

    private final String type;
    private String url = "";
    private String js = "";
    private int responseCode = 0;

    public ReportingTracker(String type, String url, int responseCode) {
        this.type = type;
        this.url = url;
        this.responseCode = responseCode;
    }

    public ReportingTracker(String type, String js){
        this.type = type;
        this.js = js;
    }

    public String getType() {
        return type;
    }

    public String getUrl() {
        return url;
    }

    public String getJs() {
        return js;
    }

    public int getResponseCode() {
        return responseCode;
    }
}
