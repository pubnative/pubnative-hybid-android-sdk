package net.pubnative.lite.sdk.models;

import net.pubnative.lite.sdk.utils.json.JsonModel;

import org.json.JSONObject;

public class RemoteConfigPlacement extends JsonModel {
    public RemoteConfigPlacement() {

    }

    public RemoteConfigPlacement(JSONObject jsonObject) throws Exception {
        fromJson(jsonObject);
    }
}
