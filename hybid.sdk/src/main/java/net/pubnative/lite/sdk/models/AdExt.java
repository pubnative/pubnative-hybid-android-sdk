// HyBid SDK License
//
// https://github.com/pubnative/pubnative-hybid-android-sdk/blob/main/LICENSE
//
package net.pubnative.lite.sdk.models;

import net.pubnative.lite.sdk.utils.json.BindField;
import net.pubnative.lite.sdk.utils.json.JsonModel;

import org.json.JSONObject;

import java.util.Map;

public class AdExt extends JsonModel {

    @BindField
    protected Map<String, Object> meta;

    public AdExt() {
    }

    public AdExt(JSONObject jsonObject) throws Exception {
        fromJson(jsonObject);
    }
}
