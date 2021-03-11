package net.pubnative.lite.sdk.models;

import net.pubnative.lite.sdk.utils.json.BindField;
import net.pubnative.lite.sdk.utils.json.JsonModel;

import org.json.JSONObject;

public class IdUserVendor extends JsonModel {

    @BindField
    public IdGgl GGL;

    @BindField
    public IdApl APL;


    public IdUserVendor(){

    }

    public IdUserVendor(JSONObject jsonObject) throws Exception{
        fromJson(jsonObject);
    }
}
