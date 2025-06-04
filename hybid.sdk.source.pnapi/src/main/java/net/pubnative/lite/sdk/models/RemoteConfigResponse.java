// HyBid SDK License
//
// https://github.com/pubnative/pubnative-hybid-android-sdk/blob/main/LICENSE
//
package net.pubnative.lite.sdk.models;

import net.pubnative.lite.sdk.utils.json.BindField;
import net.pubnative.lite.sdk.utils.json.JsonModel;

import org.json.JSONObject;

public class RemoteConfigResponse extends JsonModel {

    @BindField
    public String status;
    @BindField
    public SdkConfig configs;
    @BindField
    public String error_message;

    public interface Status {
        String ERROR = "error";
        String OK = "ok";
    }

    public RemoteConfigResponse() {
    }

    public RemoteConfigResponse(JSONObject jsonObject) throws Exception {
        fromJson(jsonObject);
    }
}
