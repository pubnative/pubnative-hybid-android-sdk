package net.pubnative.lite.sdk.models;

import net.pubnative.lite.sdk.utils.json.BindField;
import net.pubnative.lite.sdk.utils.json.JsonModel;

import org.json.JSONObject;

public class RemoteConfigLastVersion extends JsonModel {
    @BindField
    public String publish_date;
    @BindField
    public String version_no;
    @BindField
    public Integer input_size;

    public RemoteConfigLastVersion() {

    }

    public RemoteConfigLastVersion(JSONObject jsonObject) throws Exception {
        fromJson(jsonObject);
    }
}
