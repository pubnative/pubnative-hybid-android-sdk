// HyBid SDK License
//
// https://github.com/pubnative/pubnative-hybid-android-sdk/blob/main/LICENSE
//
package net.pubnative.lite.sdk.models;

import net.pubnative.lite.sdk.utils.json.BindField;
import net.pubnative.lite.sdk.utils.json.JsonModel;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.List;

public class BuyerSignal extends JsonModel {
    @BindField
    public String origin;
    @BindField
    public List<String> buyerdata;
    @BindField
    public String buyer_experiment_group_id;

    public BuyerSignal() {

    }

    public BuyerSignal(JSONObject jsonObject) throws Exception {
        fromJson(jsonObject);
    }

    public String getOrigin() {
        return origin;
    }

    public List<String> getBuyerData() {
        return buyerdata;
    }

    public String getBuyerExperimentGroupId() {
        return buyer_experiment_group_id;
    }

    public String getBuyerDataJson() {
        JSONArray array = new JSONArray();
        if (buyerdata != null && !buyerdata.isEmpty()) {
            for (String data : buyerdata) {
                array.put(data);
            }
        }
        return array.toString();
    }
}
