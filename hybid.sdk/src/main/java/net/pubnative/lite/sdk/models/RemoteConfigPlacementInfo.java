package net.pubnative.lite.sdk.models;

import net.pubnative.lite.sdk.utils.json.BindField;
import net.pubnative.lite.sdk.utils.json.JsonModel;

import org.json.JSONObject;

import java.util.List;

public class RemoteConfigPlacementInfo extends JsonModel {
    @BindField
    public int timeout;
    @BindField
    public List<RemoteConfigPlacement> placements;

    public RemoteConfigPlacementInfo(){

    }

    public RemoteConfigPlacementInfo(JSONObject jsonObject) throws Exception{
        fromJson(jsonObject);
    }
}
