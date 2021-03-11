package net.pubnative.lite.sdk.models;

import net.pubnative.lite.sdk.utils.json.BindField;
import net.pubnative.lite.sdk.utils.json.JsonModel;

import org.json.JSONObject;

import java.util.List;

public class RemoteConfigVoyager  extends JsonModel {
    @BindField
    public Integer audience_refresh_frequency;
    @BindField
    public Integer session_sample;
    @BindField
    public String vg_encoding;
    @BindField
    public String vg_targeting_key;
    @BindField
    public RemoteConfigAppInfo app_info;
    @BindField
    public List<RemoteConfigMLModel> models;
    @BindField
    public List<RemoteConfigAudience> audiences;
    @BindField
    public List<RemoteConfigMetadata> metadata;


    public RemoteConfigVoyager() {

    }

    public RemoteConfigVoyager(JSONObject jsonObject) throws Exception {
        fromJson(jsonObject);
    }
}
