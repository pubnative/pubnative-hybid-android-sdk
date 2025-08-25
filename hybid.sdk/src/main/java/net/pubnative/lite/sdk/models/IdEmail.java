// HyBid SDK License
//
// https://github.com/pubnative/pubnative-hybid-android-sdk/blob/main/LICENSE
//
package net.pubnative.lite.sdk.models;

import net.pubnative.lite.sdk.utils.json.BindField;
import net.pubnative.lite.sdk.utils.json.JsonModel;

import org.json.JSONObject;

public class IdEmail extends JsonModel {

    @BindField
    public String email;


    public IdEmail() {

    }

    public IdEmail(JSONObject jsonObject) throws Exception {
        fromJson(jsonObject);
    }
}
