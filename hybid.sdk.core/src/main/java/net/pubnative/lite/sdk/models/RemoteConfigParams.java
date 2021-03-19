package net.pubnative.lite.sdk.models;

import net.pubnative.lite.sdk.utils.json.BindField;
import net.pubnative.lite.sdk.utils.json.JsonModel;

import org.json.JSONObject;

import java.util.List;

public class RemoteConfigParams extends JsonModel {

    @BindField
    public List<String> wl_taxonomy2;

    @BindField
    public List<String> wl_taxonomy3;

    @BindField
    public RemoteConfigResolutions resolutions;

    @BindField
    public Integer distance_threshold;


    public RemoteConfigParams() {

    }

    public RemoteConfigParams(JSONObject jsonObject) throws Exception {
        fromJson(jsonObject);
    }

}
