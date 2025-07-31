// HyBid SDK License
//
// https://github.com/pubnative/pubnative-hybid-android-sdk/blob/main/LICENSE
//
package net.pubnative.lite.sdk.models;

import net.pubnative.lite.sdk.utils.json.BindField;
import net.pubnative.lite.sdk.utils.json.JsonModel;

import org.json.JSONObject;

public class SignalData extends JsonModel {
    @BindField
    public String status;

    @BindField
    public String tagid;

    @BindField
    public String admurl;

    @BindField
    public AdResponse adm;

    @BindField
    public String format;

    public SignalData() {

    }

    public SignalData(JSONObject jsonObject) throws Exception {
        fromJson(jsonObject);
    }
}
