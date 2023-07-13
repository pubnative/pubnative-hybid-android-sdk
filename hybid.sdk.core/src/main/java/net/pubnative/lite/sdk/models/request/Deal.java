package net.pubnative.lite.sdk.models.request;

import net.pubnative.lite.sdk.utils.json.BindField;
import net.pubnative.lite.sdk.utils.json.JsonModel;

import org.json.JSONObject;

import java.util.List;

public class Deal extends JsonModel {
    @BindField
    private String id;
    @BindField
    private Float bidfloor = 0.0f;
    @BindField
    private String bidfloorcur = "USD";
    @BindField
    private Integer at;
    @BindField
    private List<String> wseat;
    @BindField
    private List<String> wadomain;

    public Deal() {
    }

    public Deal(JSONObject jsonObject) throws Exception {
        fromJson(jsonObject);
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Float getBidFloor() {
        return bidfloor;
    }

    public void setBidFloor(Float bidFloor) {
        this.bidfloor = bidFloor;
    }

    public String getBidFloorCurrency() {
        return bidfloorcur;
    }

    public void setBidFloorCurrency(String bidFloorCurrency) {
        this.bidfloorcur = bidFloorCurrency;
    }

    public Integer getAuctionType() {
        return at;
    }

    public void setAuctionType(Integer auctionType) {
        this.at = auctionType;
    }

    public List<String> getWSeat() {
        return wseat;
    }

    public void setWSeat(List<String> wSeat) {
        this.wseat = wSeat;
    }

    public List<String> getWAdomain() {
        return wadomain;
    }

    public void setWAdomain(List<String> wAdomain) {
        this.wadomain = wAdomain;
    }
}
