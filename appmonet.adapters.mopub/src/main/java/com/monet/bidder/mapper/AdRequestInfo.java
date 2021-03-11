package com.monet.bidder.mapper;

public class AdRequestInfo {
    private String appToken;
    private String zoneId;

    public AdRequestInfo(String appToken, String zoneId) {
        this.appToken = appToken;
        this.zoneId = zoneId;
    }

    public String getAppToken() {
        return appToken;
    }

    public String getZoneId() {
        return zoneId;
    }
}
