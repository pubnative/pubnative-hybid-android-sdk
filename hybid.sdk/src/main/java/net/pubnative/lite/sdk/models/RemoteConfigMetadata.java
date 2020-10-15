package net.pubnative.lite.sdk.models;

import net.pubnative.lite.sdk.utils.json.BindField;
import net.pubnative.lite.sdk.utils.json.JsonModel;

import org.json.JSONObject;

public class RemoteConfigMetadata extends JsonModel {
    @BindField
    public String name;
    @BindField
    public RemoteConfigLastVersion last_version;

    public RemoteConfigMetadata() {

    }

    public RemoteConfigMetadata(JSONObject jsonObject) throws Exception {
        fromJson(jsonObject);
    }
}
