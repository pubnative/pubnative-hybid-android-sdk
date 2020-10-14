package net.pubnative.lite.sdk.models;

import net.pubnative.lite.sdk.utils.json.BindField;
import net.pubnative.lite.sdk.utils.json.JsonModel;

import org.json.JSONObject;

public class RemoteConfigPlacementInfo extends JsonModel {
    @BindField
    public int timeout;

    public RemoteConfigPlacementInfo(){

    }

    public RemoteConfigPlacementInfo(JSONObject jsonObject) throws Exception{
        fromJson(jsonObject);
    }
}
