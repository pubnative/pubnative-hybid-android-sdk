package net.pubnative.lite.sdk.models;

import net.pubnative.lite.sdk.utils.json.BindField;
import net.pubnative.lite.sdk.utils.json.JsonModel;

import org.json.JSONObject;

import java.util.List;

public class RemoteConfigModel extends JsonModel {


    //==============================================================================================
    // Fields
    //==============================================================================================
    @BindField
    public int ttl;
    @BindField
    private List<RemoteConfigPlacementInfo> placement_info;
    @BindField
    public List<RemoteConfigMeasurement> measurement;
    @BindField
    public List<RemoteConfigVoyager> voyager;

    public RemoteConfigModel(){

    }

    public RemoteConfigModel(JSONObject jsonObject) throws Exception{
        fromJson(jsonObject);
    }

}
