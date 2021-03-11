package net.pubnative.lite.sdk.models;

import net.pubnative.lite.sdk.utils.json.BindField;
import net.pubnative.lite.sdk.utils.json.JsonModel;

import org.json.JSONObject;

import java.util.List;

public class IdModel extends JsonModel {

    @BindField
    public List<IdApp> apps;

    @BindField
    public IdDevice device;

    @BindField
    public List<IdUser> users;

    public IdModel(){

    }

    public IdModel(JSONObject jsonObject) throws Exception {
        fromJson(jsonObject);
    }
}
