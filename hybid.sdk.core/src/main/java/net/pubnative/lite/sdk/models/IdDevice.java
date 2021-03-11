package net.pubnative.lite.sdk.models;

import net.pubnative.lite.sdk.utils.json.BindField;
import net.pubnative.lite.sdk.utils.json.JsonModel;

import org.json.JSONObject;

public class IdDevice extends JsonModel {

    @BindField
    public String id;

    @BindField
    public IdOs os;

    @BindField
    public String manufacture;

    @BindField
    public String model;

    @BindField
    public String brand;

    @BindField
    public IdBattery battery;


    public IdDevice(){

    }

    public IdDevice(JSONObject jsonObject)throws Exception {
        fromJson(jsonObject);
    }
}
