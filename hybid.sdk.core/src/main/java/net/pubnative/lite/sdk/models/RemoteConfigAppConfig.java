package net.pubnative.lite.sdk.models;

import net.pubnative.lite.sdk.utils.json.BindField;
import net.pubnative.lite.sdk.utils.json.JsonModel;

import org.json.JSONObject;

public class RemoteConfigAppConfig extends JsonModel {

    @BindField
    public String app_token;

    @BindField
    public String api;


    public RemoteConfigAppConfig(){

    }

    public RemoteConfigAppConfig(JSONObject jsonObject) throws Exception{
        fromJson(jsonObject);
    }

}
