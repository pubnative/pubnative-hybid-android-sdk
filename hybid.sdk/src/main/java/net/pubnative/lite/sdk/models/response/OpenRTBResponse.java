// HyBid SDK License
//
// https://github.com/pubnative/pubnative-hybid-android-sdk/blob/main/LICENSE
//
package net.pubnative.lite.sdk.models.response;

import net.pubnative.lite.sdk.utils.json.BindField;
import net.pubnative.lite.sdk.utils.json.JsonModel;

import org.json.JSONObject;

import java.util.List;

public class OpenRTBResponse extends JsonModel {
    @BindField
    private String id;
    @BindField
    private List<SeatBid> seatbid;
    @BindField
    private String cur = "USD";
    @BindField
    private String bidid;
    @BindField
    private String customData;
    @BindField
    private Integer nbr;

    public OpenRTBResponse() {
    }

    public OpenRTBResponse(JSONObject jsonObject) throws Exception {
        fromJson(jsonObject);
    }

    public String getId() {
        return id;
    }

    public List<SeatBid> getSeatBids() {
        return seatbid;
    }

    public String getCurrency() {
        return cur;
    }

    public String getBidId() {
        return bidid;
    }

    public String getCustomData() {
        return customData;
    }

    public Integer getNoBidReason() {
        return nbr;
    }
}
