// HyBid SDK License
//
// https://github.com/pubnative/pubnative-hybid-android-sdk/blob/main/LICENSE
//
package net.pubnative.lite.sdk.models.response;

import net.pubnative.lite.sdk.utils.json.BindField;
import net.pubnative.lite.sdk.utils.json.JsonModel;

import org.json.JSONObject;

import java.util.List;

public class BidExtension extends JsonModel {

    @BindField
    private String crtype;
    @BindField
    private List<String> imptrackers;
    @BindField
    private String signaldata;

    public BidExtension() {
    }

    public BidExtension(JSONObject jsonObject) throws Exception {
        fromJson(jsonObject);
    }

    public String getCrtype() {
        return crtype;
    }

    public void setCrtype(String crtype) {
        this.crtype = crtype;
    }

    public List<String> getImptrackers() {
        return imptrackers;
    }

    public void setImptrackers(List<String> imptrackers) {
        this.imptrackers = imptrackers;
    }

    public String getSignaldata() {
        return signaldata;
    }

    public void setSignaldata(String signaldata) {
        this.signaldata = signaldata;
    }
}
