package net.pubnative.lite.sdk.models;

import net.pubnative.lite.sdk.utils.json.BindField;
import net.pubnative.lite.sdk.utils.json.JsonModel;

import org.json.JSONObject;

public class RemoteConfigAdSource extends JsonModel {

    @BindField
    public String type;
    @BindField
    public Double eCPM;
    @BindField
    public Boolean enabled;
    @BindField
    public String name;
    @BindField
    public String vastTagUrl;

    public RemoteConfigAdSource(){

    }

    public RemoteConfigAdSource(JSONObject jsonObject) throws Exception {
        fromJson(jsonObject);
    }

}
