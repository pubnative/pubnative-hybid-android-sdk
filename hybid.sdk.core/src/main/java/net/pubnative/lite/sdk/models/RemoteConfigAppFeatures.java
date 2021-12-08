package net.pubnative.lite.sdk.models;

import net.pubnative.lite.sdk.utils.json.BindField;
import net.pubnative.lite.sdk.utils.json.JsonModel;

import org.json.JSONObject;

import java.util.List;

public class RemoteConfigAppFeatures extends JsonModel {
    @BindField
    public List<String> ad_formats;
    @BindField
    public List<String> rendering;
    @BindField
    public List<String> reporting;
    @BindField
    public List<String> user_consent;

    public RemoteConfigAppFeatures() {

    }

    public RemoteConfigAppFeatures(JSONObject jsonObject) throws Exception {
        fromJson(jsonObject);
    }
}
