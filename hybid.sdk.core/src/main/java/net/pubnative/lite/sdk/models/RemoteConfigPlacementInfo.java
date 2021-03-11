package net.pubnative.lite.sdk.models;

import net.pubnative.lite.sdk.utils.json.BindField;
import net.pubnative.lite.sdk.utils.json.JsonModel;

import org.json.JSONObject;

import java.util.Map;

public class RemoteConfigPlacementInfo extends JsonModel {
    @BindField
    public Integer timeout;
    @BindField
    public Map<String, RemoteConfigPlacement> placements;

    public RemoteConfigPlacementInfo(){

    }

    public RemoteConfigPlacementInfo(JSONObject jsonObject) throws Exception{
        fromJson(jsonObject);
    }
}
