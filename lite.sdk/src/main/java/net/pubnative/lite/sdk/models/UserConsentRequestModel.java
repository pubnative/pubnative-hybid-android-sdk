package net.pubnative.lite.sdk.models;

import net.pubnative.lite.sdk.utils.json.BindField;
import net.pubnative.lite.sdk.utils.json.JsonModel;

import org.json.JSONObject;

public class UserConsentRequestModel extends JsonModel {
    @BindField
    private String did;
    @BindField
    private String did_type;
    @BindField
    private boolean consented;

    public UserConsentRequestModel(String deviceId, String deviceIdType, boolean consented) {
        this.did = deviceId;
        this.did_type = deviceIdType;
        this.consented = consented;
    }

    public UserConsentRequestModel(JSONObject jsonObject) throws Exception {
        fromJson(jsonObject);
    }

    public String getDeviceId() {
        return did;
    }

    public String getDeviceIdType() {
        return did_type;
    }

    public boolean isConsented() {
        return consented;
    }
}
