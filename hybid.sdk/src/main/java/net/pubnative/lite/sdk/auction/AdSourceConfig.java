package net.pubnative.lite.sdk.auction;

import net.pubnative.lite.sdk.utils.json.BindField;
import net.pubnative.lite.sdk.utils.json.JsonModel;

import org.json.JSONObject;

public class AdSourceConfig extends JsonModel {
    @BindField
    private Double eCPM;
    @BindField
    private boolean enabled;
    @BindField
    private String name;
    @BindField
    private String vastTagUrl;
    @BindField
    private String zoneId;

    public AdSourceConfig() {

    }

    public AdSourceConfig(JSONObject jsonObject) throws Exception {
        fromJson(jsonObject);
    }

    public Double getECPM() {
        return eCPM;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public String getName() {
        return name;
    }

    public String getVastTagUrl() {
        return vastTagUrl;
    }

    public String getZoneId() {
        return zoneId;
    }
}
