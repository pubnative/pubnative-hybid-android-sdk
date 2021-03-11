package net.pubnative.lite.sdk.auction;

import net.pubnative.lite.sdk.utils.json.BindField;
import net.pubnative.lite.sdk.utils.json.JsonModel;

import org.json.JSONObject;

public class AdSourceConfig {
    private double eCPM;
    private boolean enabled;
    private String name;
    private String vastTagUrl;
    private String zoneId;

    public AdSourceConfig() {

    }

    public double getECPM() {
        return eCPM;
    }

    public void setECPM(double eCPM) {
        this.eCPM = eCPM;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getVastTagUrl() {
        return vastTagUrl;
    }

    public void setVastTagUrl(String vastTagUrl) {
        this.vastTagUrl = vastTagUrl;
    }

    public String getZoneId() {
        return zoneId;
    }

    public void setZoneId(String zoneId) {
        this.zoneId = zoneId;
    }
}
