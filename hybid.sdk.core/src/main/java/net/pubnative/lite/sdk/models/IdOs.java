package net.pubnative.lite.sdk.models;

import net.pubnative.lite.sdk.utils.json.BindField;
import net.pubnative.lite.sdk.utils.json.JsonModel;


import org.json.JSONObject;

public class IdOs extends JsonModel {

    @BindField
    public String name;

    @BindField
    public String version;

    @BindField
    public String build_signature;

    public IdOs(){

    }

    public IdOs(JSONObject jsonObject) throws Exception{
        fromJson(jsonObject);
    }
}
