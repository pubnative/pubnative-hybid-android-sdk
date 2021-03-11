package net.pubnative.lite.sdk.models;

import net.pubnative.lite.sdk.utils.json.BindField;
import net.pubnative.lite.sdk.utils.json.JsonModel;

import org.json.JSONObject;

public class IdLocation extends JsonModel {

    @BindField
    public String lat;

    @BindField
    public String lon;

    @BindField
    public String type;

    @BindField
    public String category;

    @BindField
    public String accuracy;

    @BindField
    public String ts;


    public IdLocation(){

    }

    public IdLocation(JSONObject jsonObject) throws Exception{
        fromJson(jsonObject);
    }
}