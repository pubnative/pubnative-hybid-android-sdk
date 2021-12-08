package net.pubnative.lite.sdk.models;

import net.pubnative.lite.sdk.utils.json.BindField;
import net.pubnative.lite.sdk.utils.json.JsonModel;

import org.json.JSONObject;

import java.util.List;

public class RemoteConfigAppConfig extends JsonModel {

    @BindField
    public String app_token;

    @BindField
    public String api;

    @BindField
    public List<String> enabled_apis;

    @BindField
    public List<String> enabled_protocols;

    @BindField
    public RemoteConfigAppFeatures features;

    public RemoteConfigAppConfig() {

    }

    public RemoteConfigAppConfig(JSONObject jsonObject) throws Exception {
        fromJson(jsonObject);
    }
}
