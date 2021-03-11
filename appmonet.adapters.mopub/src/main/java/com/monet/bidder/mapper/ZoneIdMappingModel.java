package com.monet.bidder.mapper;

import net.pubnative.lite.sdk.utils.json.BindField;
import net.pubnative.lite.sdk.utils.json.JsonModel;

import org.json.JSONObject;

import java.util.Map;

public class ZoneIdMappingModel extends JsonModel {
    @BindField
    public String app_token;
    @BindField
    public Map<String, Mappings> ad_sizes;

    public ZoneIdMappingModel() {
    }

    public ZoneIdMappingModel(JSONObject jsonObject) throws Exception {
        fromJson(jsonObject);
    }

    public String getAppToken() {
        return app_token;
    }

    public Map<String, Mappings> getAdSizes() {
        return ad_sizes;
    }
}
