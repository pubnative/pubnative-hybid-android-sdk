package net.pubnative.tarantula.sdk.models;

import net.pubnative.tarantula.sdk.utils.json.BindField;
import net.pubnative.tarantula.sdk.utils.json.JsonModel;

import org.json.JSONObject;

import java.util.List;

public class AdResponse extends JsonModel {

    @BindField
    public String status;
    @BindField
    public String error_message;
    @BindField
    public List<Ad> ads;
    @BindField
    public List<AdExt> ext;

    public interface Status {

        String ERROR = "error";
        String OK    = "ok";
    }

    public AdResponse() {

    }

    public AdResponse(JSONObject jsonObject) throws Exception {

        fromJson(jsonObject);
    }
}
