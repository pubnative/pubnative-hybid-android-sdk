package net.pubnative.lite.sdk.models;

import net.pubnative.lite.sdk.utils.json.BindField;
import net.pubnative.lite.sdk.utils.json.JsonModel;

import org.json.JSONObject;

import java.util.List;

public class RemoteConfigsDebug extends JsonModel {
    @BindField
    public List<Integer> configids;

    @BindField
    public List<Integer> sliceids;

    public RemoteConfigsDebug() {

    }

    public RemoteConfigsDebug(JSONObject jsonObject) throws Exception {
        fromJson(jsonObject);
    }

    public List<Integer> getConfigIds() {
        return configids;
    }

    public List<Integer> getSliceIds() {
        return sliceids;
    }

}
