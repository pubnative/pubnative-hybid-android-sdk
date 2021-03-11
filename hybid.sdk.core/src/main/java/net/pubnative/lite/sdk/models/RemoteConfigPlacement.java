package net.pubnative.lite.sdk.models;

import net.pubnative.lite.sdk.utils.json.BindField;
import net.pubnative.lite.sdk.utils.json.JsonModel;

import org.json.JSONObject;

import java.util.List;

public class RemoteConfigPlacement extends JsonModel {
    @BindField
    public String type;
    @BindField
    public Long timeout;
    @BindField
    public List<RemoteConfigAdSource> ad_sources;


    public RemoteConfigPlacement() {

    }

    public RemoteConfigPlacement(JSONObject jsonObject) throws Exception {
        fromJson(jsonObject);
    }
}
