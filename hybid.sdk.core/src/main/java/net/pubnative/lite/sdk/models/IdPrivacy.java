// HyBid SDK License
//
// https://github.com/pubnative/pubnative-hybid-android-sdk/blob/main/LICENSE
//
package net.pubnative.lite.sdk.models;

import net.pubnative.lite.sdk.utils.json.BindField;
import net.pubnative.lite.sdk.utils.json.JsonModel;

import org.json.JSONObject;

public class IdPrivacy extends JsonModel {

    @BindField
    public Boolean lat;

    @BindField
    public String tcfv1;

    @BindField
    public String tcfv2;

    @BindField
    public String iab_ccpa;


    public IdPrivacy(){

    }

    public IdPrivacy(JSONObject jsonObject) throws Exception{
        fromJson(jsonObject);
    }
}
