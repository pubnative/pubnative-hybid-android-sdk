package net.pubnative.lite.sdk.models;

import net.pubnative.lite.sdk.utils.json.BindField;
import net.pubnative.lite.sdk.utils.json.JsonModel;

import org.json.JSONObject;

public class AtomConfigResponse extends JsonModel {

    @BindField
    public String status;
    @BindField
    public AtomConfig configs;
    @BindField
    public String error_message;

    public interface Status {
        String ERROR = "error";
        String OK = "ok";
    }

    public AtomConfigResponse() {
    }

    public AtomConfigResponse(JSONObject jsonObject) throws Exception {
        fromJson(jsonObject);
    }
}
