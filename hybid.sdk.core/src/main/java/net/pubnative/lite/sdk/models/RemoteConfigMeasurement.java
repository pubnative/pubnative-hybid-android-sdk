package net.pubnative.lite.sdk.models;

import net.pubnative.lite.sdk.utils.json.BindField;
import net.pubnative.lite.sdk.utils.json.JsonModel;

import org.json.JSONObject;

public class RemoteConfigMeasurement  extends JsonModel {
    @BindField
    public Boolean viewability;

    public RemoteConfigMeasurement(){

    }

    public RemoteConfigMeasurement(JSONObject jsonObject) throws Exception{
        fromJson(jsonObject);
    }
}
