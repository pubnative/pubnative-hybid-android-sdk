package net.pubnative.lite.demo.models;

import net.pubnative.lite.sdk.utils.json.BindField;
import net.pubnative.lite.sdk.utils.json.JsonModel;

import org.json.JSONObject;

public class RemoteConfigParam extends JsonModel {

    @BindField
    public String name;
    @BindField
    public Object value;

    public RemoteConfigParam() {
    }

    public RemoteConfigParam(JSONObject jsonObject) throws Exception {
        fromJson(jsonObject);
    }
}
