package net.pubnative.lite.sdk.models;

import net.pubnative.lite.sdk.utils.json.BindField;
import net.pubnative.lite.sdk.utils.json.JsonModel;

import org.json.JSONObject;

import java.util.List;

public class RemoteConfigDependencies extends JsonModel {
    @BindField
    public List<String> models;
    @BindField
    public List<String> metadata;

    public RemoteConfigDependencies() {

    }

    public RemoteConfigDependencies(JSONObject jsonObject) throws Exception {
        fromJson(jsonObject);
    }
}
