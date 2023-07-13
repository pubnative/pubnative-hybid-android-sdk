package net.pubnative.lite.sdk.models.request;

import net.pubnative.lite.sdk.utils.json.BindField;
import net.pubnative.lite.sdk.utils.json.JsonModel;

import org.json.JSONObject;

public class Regs extends JsonModel {
    @BindField
    private Integer coppa;

    public Regs() {
    }

    public Regs(JSONObject jsonObject) throws Exception {
        fromJson(jsonObject);
    }

    public Integer getCOPPA() {
        return coppa;
    }
}
