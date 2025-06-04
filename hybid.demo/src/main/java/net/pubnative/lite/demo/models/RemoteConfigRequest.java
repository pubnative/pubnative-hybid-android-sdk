// HyBid SDK License
//
// https://github.com/pubnative/pubnative-hybid-android-sdk/blob/main/LICENSE
//
package net.pubnative.lite.demo.models;

import net.pubnative.lite.sdk.utils.json.BindField;
import net.pubnative.lite.sdk.utils.json.JsonModel;

import org.json.JSONObject;

import java.lang.Boolean;
import java.lang.String;
import java.util.List;

public class RemoteConfigRequest extends JsonModel {
    @BindField
    public String format;
    @BindField
    public String os;
    @BindField
    public Integer width;
    @BindField
    public Integer height;
    @BindField
    public Boolean fullscreen;
    @BindField
    public Boolean rewarded;
    @BindField
    public String adm_type;
    @BindField
    public String adm;
    @BindField
    public String encoded_adm;
    @BindField
    public String custom_cta_app_name;
    @BindField
    public String custom_cta_value;
    @BindField
    public String bundle_id_value;
    @BindField
    public String custom_endcard_value;
    @BindField
    public List<RemoteConfigParam> configs;

    public RemoteConfigRequest() {
    }

    public RemoteConfigRequest(JSONObject jsonObject) throws Exception {
        fromJson(jsonObject);
    }
}