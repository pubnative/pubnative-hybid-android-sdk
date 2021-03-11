package net.pubnative.lite.sdk.models;

import net.pubnative.lite.sdk.utils.json.BindField;
import net.pubnative.lite.sdk.utils.json.JsonModel;

import org.json.JSONObject;

import java.util.List;

public class IdApp extends JsonModel {

    @BindField
    public String bundle_id;

    @BindField
    public List<IdAppUser> users;

    @BindField
    public IdPrivacy privacy;


    public IdApp(){

    }

    public IdApp(JSONObject jsonObject) throws Exception{
        fromJson(jsonObject);
    }

}
