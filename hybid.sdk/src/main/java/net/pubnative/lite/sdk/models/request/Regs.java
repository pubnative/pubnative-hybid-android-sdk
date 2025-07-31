// HyBid SDK License
//
// https://github.com/pubnative/pubnative-hybid-android-sdk/blob/main/LICENSE
//
package net.pubnative.lite.sdk.models.request;

import net.pubnative.lite.sdk.utils.json.BindField;
import net.pubnative.lite.sdk.utils.json.JsonModel;

import org.json.JSONObject;

public class Regs extends JsonModel {
    @BindField
    private Integer coppa;
    @BindField
    private Ext ext;

    public Regs() {
    }

    public Regs(JSONObject jsonObject) throws Exception {
        fromJson(jsonObject);
    }

    public Integer getCOPPA() {
        return coppa;
    }

    public Ext getExt() {return ext;}

    public void setExt(Ext ext) {
        this.ext = ext;
    }
}
