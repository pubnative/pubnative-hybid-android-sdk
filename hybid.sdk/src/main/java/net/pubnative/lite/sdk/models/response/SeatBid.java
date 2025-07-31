// HyBid SDK License
//
// https://github.com/pubnative/pubnative-hybid-android-sdk/blob/main/LICENSE
//
package net.pubnative.lite.sdk.models.response;

import net.pubnative.lite.sdk.utils.json.BindField;
import net.pubnative.lite.sdk.utils.json.JsonModel;

import org.json.JSONObject;

import java.util.List;

public class SeatBid extends JsonModel {

    @BindField
    private List<Bid> bid;
    @BindField
    private String seat;
    @BindField
    private Integer group = 0;

    public SeatBid() {
    }

    public SeatBid(JSONObject jsonObject) throws Exception {
        fromJson(jsonObject);
    }

    public List<Bid> getBids() {
        return bid;
    }

    public String getSeat() {
        return seat;
    }

    public Integer getGroup() {
        return group;
    }
}
