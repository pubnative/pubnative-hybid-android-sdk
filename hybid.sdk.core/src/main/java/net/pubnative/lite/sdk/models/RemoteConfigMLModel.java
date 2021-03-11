package net.pubnative.lite.sdk.models;

import net.pubnative.lite.sdk.utils.json.BindField;
import net.pubnative.lite.sdk.utils.json.JsonModel;

import org.json.JSONObject;

public class RemoteConfigMLModel extends JsonModel {
    @BindField
    public String name;
    @BindField
    public Double min_score;
    @BindField
    public RemoteConfigLastVersion last_version;

    public RemoteConfigMLModel() {

    }

    public RemoteConfigMLModel(JSONObject jsonObject) throws Exception {
        fromJson(jsonObject);
    }
}
