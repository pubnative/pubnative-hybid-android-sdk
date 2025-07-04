// HyBid SDK License
//
// https://github.com/pubnative/pubnative-hybid-android-sdk/blob/main/LICENSE
//
package net.pubnative.lite.sdk.models;

import net.pubnative.lite.sdk.utils.json.BindField;
import net.pubnative.lite.sdk.utils.json.JsonModel;

import org.json.JSONObject;

public class RemoteConfigModel extends JsonModel {


    //==============================================================================================
    // Fields
    //==============================================================================================
    @BindField
    public Integer ttl;
    @BindField
    public RemoteConfigAppConfig app_config;
    @BindField
    public RemoteConfigMeasurement measurement;
    @BindField
    public RemoteConfigVoyager voyager;
    @BindField
    public String key;

    public RemoteConfigModel(){

    }

    public RemoteConfigModel(JSONObject jsonObject) throws Exception{
        fromJson(jsonObject);
    }

}
