package net.pubnative.lite.sdk.models;

import net.pubnative.lite.sdk.utils.json.BindField;
import net.pubnative.lite.sdk.utils.json.JsonModel;

import org.json.JSONObject;

public class UserConsentResponseModel extends JsonModel {
    @BindField
    private String status;
    @BindField
    private String error;
    @BindField
    private UserConsentModel consent;

    public UserConsentResponseModel() {
    }

    public UserConsentResponseModel(JSONObject jsonObject) throws Exception {
        fromJson(jsonObject);
    }

    public String getStatus() {
        return status;
    }

    public String getError() {
        return error;
    }

    public UserConsentModel getConsent() {
        return consent;
    }
}
