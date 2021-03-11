package net.pubnative.lite.sdk.models;

import net.pubnative.lite.sdk.utils.json.BindField;
import net.pubnative.lite.sdk.utils.json.JsonModel;

import org.json.JSONObject;

import java.util.List;

public class RemoteConfigAppInfo extends JsonModel {
    @BindField
    public String app_store_id;
    @BindField
    public List<String> iab_categories;
    @BindField
    public Double pf;
    @BindField
    public Double pm;


    public RemoteConfigAppInfo() {

    }

    public RemoteConfigAppInfo(JSONObject jsonObject) throws Exception {
        fromJson(jsonObject);
    }
}
