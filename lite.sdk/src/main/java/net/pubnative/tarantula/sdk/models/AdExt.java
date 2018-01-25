package net.pubnative.tarantula.sdk.models;

import net.pubnative.tarantula.sdk.utils.json.BindField;
import net.pubnative.tarantula.sdk.utils.json.JsonModel;

import org.json.JSONObject;

import java.util.Map;

public class AdExt extends JsonModel {

    @BindField
    protected Map meta;

    public AdExt(JSONObject jsonObject) throws Exception {
        fromJson(jsonObject);
    }
}
