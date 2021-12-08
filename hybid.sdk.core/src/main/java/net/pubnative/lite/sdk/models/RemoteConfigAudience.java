package net.pubnative.lite.sdk.models;

import net.pubnative.lite.sdk.utils.json.BindField;
import net.pubnative.lite.sdk.utils.json.JsonModel;

import org.json.JSONObject;

public class RemoteConfigAudience extends JsonModel {
    @BindField
    public String name;
    @BindField
    public Double min_score;
    @BindField
    public Boolean requires_geolocation;
    @BindField
    public RemoteConfigDependencies dependencies;
    @BindField
    public RemoteConfigParams params;


    public RemoteConfigAudience() {

    }

    public RemoteConfigAudience(JSONObject jsonObject) throws Exception {
        fromJson(jsonObject);
    }
}
