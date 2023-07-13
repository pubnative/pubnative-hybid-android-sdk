package net.pubnative.lite.sdk.models.request;

import net.pubnative.lite.sdk.utils.json.BindField;
import net.pubnative.lite.sdk.utils.json.JsonModel;

import org.json.JSONObject;

public class Metric extends JsonModel {
    @BindField
    private String type;
    @BindField
    private Float value;
    @BindField
    private String vendor;

    public Metric() {
    }

    public Metric(JSONObject jsonObject) throws Exception {
        fromJson(jsonObject);
    }

    public String getType() {
        return type;
    }

    public Float getValue() {
        return value;
    }

    public String getVendor() {
        return vendor;
    }
}
