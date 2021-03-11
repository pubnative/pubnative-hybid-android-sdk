package net.pubnative.lite.sdk.models;

import net.pubnative.lite.sdk.utils.json.BindField;
import net.pubnative.lite.sdk.utils.json.JsonModel;

import org.json.JSONObject;

import java.util.List;

public class IdUser extends JsonModel {

    @BindField
    public String SUID;

    @BindField
    public List<IdEmail> emails;

    @BindField
    public IdUserVendor vendors;

    @BindField
    public List<IdLocation> locations;

    @BindField
    public List<IdAudience> audiences;

    public IdUser(){

    }

    public IdUser(JSONObject jsonObject) throws Exception{
        fromJson(jsonObject);
    }

}
