package net.pubnative.lite.sdk.models;

import net.pubnative.lite.sdk.utils.json.BindField;
import net.pubnative.lite.sdk.utils.json.JsonModel;

import org.json.JSONObject;

import java.util.List;

public class RemoteConfigVoyager  extends JsonModel {
    @BindField
    public int audience_refresh_frequency;
    @BindField
    public int session_sample;
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






    //todo need to create some similar find method?
    /*protected RemoteConfigVoyagerData find(String type, List<RemoteConfigVoyagerData> list) {

        RemoteConfigVoyagerData result = null;
        if (list != null) {
            for (RemoteConfigVoyagerData data : list) {
                if (type.equals(data.name)) {
                    result = data;
                    break;
                }
            }
        }
        return result;
    }*/
}
