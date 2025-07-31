// HyBid SDK License
//
// https://github.com/pubnative/pubnative-hybid-android-sdk/blob/main/LICENSE
//
package net.pubnative.lite.sdk.models.request;

import net.pubnative.lite.sdk.utils.json.BindField;
import net.pubnative.lite.sdk.utils.json.JsonModel;

import org.json.JSONObject;

import java.util.List;

public class Pmp extends JsonModel {
    @BindField
    private Integer private_auction = 0;
    @BindField
    private List<Deal> deals;

    public Pmp() {
    }

    public Pmp(JSONObject jsonObject) throws Exception {
        fromJson(jsonObject);
    }

    public Integer getPrivateAuction() {
        return private_auction;
    }

    public void setPrivateAuction(Integer privateAuction) {
        this.private_auction = privateAuction;
    }

    public List<Deal> getDeals() {
        return deals;
    }

    public void setDeals(List<Deal> deals) {
        this.deals = deals;
    }
}
