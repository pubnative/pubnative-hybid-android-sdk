// HyBid SDK License
//
// https://github.com/pubnative/pubnative-hybid-android-sdk/blob/main/LICENSE
//
package net.pubnative.lite.sdk.models;

import net.pubnative.lite.sdk.utils.json.BindField;
import net.pubnative.lite.sdk.utils.json.JsonModel;

import org.json.JSONObject;

public class IdBattery extends JsonModel {

    @BindField
    public String capacity;

    @BindField
    public Boolean charging;



    public IdBattery(){

    }

    public IdBattery(JSONObject jsonObject) throws Exception{
        fromJson(jsonObject);
    }

}
