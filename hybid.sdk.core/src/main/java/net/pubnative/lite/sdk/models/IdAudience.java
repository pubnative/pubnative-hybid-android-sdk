package net.pubnative.lite.sdk.models;

import net.pubnative.lite.sdk.utils.json.BindField;
import net.pubnative.lite.sdk.utils.json.JsonModel;

import org.json.JSONObject;

public class IdAudience extends JsonModel {

    @BindField
    public String id;

    @BindField
    public String type;

    @BindField
    public String ts;


    public IdAudience(){

    }

    public IdAudience(JSONObject jsonObject) throws Exception{
        fromJson(jsonObject);
    }
}
