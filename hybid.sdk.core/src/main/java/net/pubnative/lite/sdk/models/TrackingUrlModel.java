package net.pubnative.lite.sdk.models;

import net.pubnative.lite.sdk.utils.json.BindField;
import net.pubnative.lite.sdk.utils.json.JsonModel;

import org.json.JSONObject;

public class TrackingUrlModel extends JsonModel {
    @BindField
    public String url = null;
    @BindField
    public long startTimestamp = 0;

    public TrackingUrlModel() {
    }

    public TrackingUrlModel(JSONObject jsonObject) throws Exception {
        fromJson(jsonObject);
    }
}
