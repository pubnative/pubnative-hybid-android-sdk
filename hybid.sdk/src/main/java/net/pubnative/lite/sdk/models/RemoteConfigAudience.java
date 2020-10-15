package net.pubnative.lite.sdk.models;

import net.pubnative.lite.sdk.utils.json.BindField;
import net.pubnative.lite.sdk.utils.json.JsonModel;

import org.json.JSONObject;

import java.util.Map;

public class RemoteConfigAudience extends JsonModel {
    @BindField
    public String name;
    @BindField
    public float min_score;
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


    public Integer getDistanceThreshold(){
        return (Integer) getParamsField("distance_threshold");
    }

    protected Object getParamsField(String param) {
        Object result = null;
        if (params != null && params.containsKey(param)) {
            result = params.get(param);
        }
        return result;
    }

}
