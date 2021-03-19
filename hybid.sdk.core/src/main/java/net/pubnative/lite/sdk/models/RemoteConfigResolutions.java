package net.pubnative.lite.sdk.models;

import net.pubnative.lite.sdk.utils.json.BindField;
import net.pubnative.lite.sdk.utils.json.JsonModel;

import org.json.JSONObject;

import java.util.List;

public class RemoteConfigResolutions extends JsonModel {

    @BindField
    public String audience_id;

    @BindField
    public List<String> taxonomy_2_ids;

    @BindField
    public List<String> taxonomy_3_ids;

    @BindField
    public Integer start_time;

    @BindField
    public Integer end_time;

    @BindField
    public Integer upper_limit;


    public RemoteConfigResolutions() {

    }

    public RemoteConfigResolutions(JSONObject jsonObject) throws Exception {
        fromJson(jsonObject);
    }

}
