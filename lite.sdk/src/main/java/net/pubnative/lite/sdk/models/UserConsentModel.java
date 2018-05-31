package net.pubnative.lite.sdk.models;

import net.pubnative.lite.sdk.utils.json.BindField;
import net.pubnative.lite.sdk.utils.json.JsonModel;

import org.json.JSONObject;

public class UserConsentModel extends JsonModel {
    @BindField
    private boolean found;
    @BindField
    private boolean consented;

    public UserConsentModel() {
    }

    public UserConsentModel(JSONObject jsonObject) throws Exception {
        fromJson(jsonObject);
    }

    public boolean isFound() {
        return found;
    }

    public boolean isConsented() {
        return consented;
    }
}
