// HyBid SDK License
//
// https://github.com/pubnative/pubnative-hybid-android-sdk/blob/main/LICENSE
//
package net.pubnative.lite.sdk.models;

import net.pubnative.lite.sdk.utils.json.BindField;
import net.pubnative.lite.sdk.utils.json.JsonModel;

import org.json.JSONObject;

public class IdApl extends JsonModel {

    @BindField
    public String IDFA;

    @BindField
    public String IDFV;


    public IdApl(){

    }

    public IdApl(JSONObject jsonObject) throws Exception{
        fromJson(jsonObject);
    }
}
