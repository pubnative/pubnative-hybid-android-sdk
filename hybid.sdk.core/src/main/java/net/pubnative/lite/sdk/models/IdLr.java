package net.pubnative.lite.sdk.models;

import net.pubnative.lite.sdk.utils.json.BindField;
import net.pubnative.lite.sdk.utils.json.JsonModel;

import org.json.JSONObject;

public class IdLr extends JsonModel {

    @BindField
    public String IDL;


    public IdLr(){

    }

    public IdLr(JSONObject jsonObject) throws Exception{
        fromJson(jsonObject);
    }

}
