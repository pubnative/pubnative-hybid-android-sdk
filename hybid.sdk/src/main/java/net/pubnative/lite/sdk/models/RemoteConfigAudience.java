package net.pubnative.lite.sdk.models;

import net.pubnative.lite.sdk.utils.json.BindField;
import net.pubnative.lite.sdk.utils.json.JsonModel;

import org.json.JSONObject;

import java.util.Map;

public class RemoteConfigAudience extends JsonModel {
    @BindField
    public String name;
    @BindField
    public String min_score;
    @BindField
    public boolean requires_geolocation;
    @BindField
    public RemoteConfigDependencies dependencies;
    @BindField
    public Map<String, Object> params;

    public RemoteConfigAudience() {

    }

    public RemoteConfigAudience(JSONObject jsonObject) throws Exception {
        fromJson(jsonObject);
    }
}
