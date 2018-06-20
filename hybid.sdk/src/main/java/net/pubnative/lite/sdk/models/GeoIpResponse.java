package net.pubnative.lite.sdk.models;

import net.pubnative.lite.sdk.utils.json.BindField;
import net.pubnative.lite.sdk.utils.json.JsonModel;

import org.json.JSONObject;

public class GeoIpResponse extends JsonModel {
    @BindField
    public String status;
    @BindField
    public String country;
    @BindField
    public String countryCode;
    @BindField
    public String message;

    public GeoIpResponse() {

    }

    public GeoIpResponse(JSONObject jsonObject) throws Exception {

        fromJson(jsonObject);
    }
}
