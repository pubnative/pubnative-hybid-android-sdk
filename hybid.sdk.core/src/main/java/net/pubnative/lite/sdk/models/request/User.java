// HyBid SDK License
//
// https://github.com/pubnative/pubnative-hybid-android-sdk/blob/main/LICENSE
//
package net.pubnative.lite.sdk.models.request;

import net.pubnative.lite.sdk.utils.json.BindField;
import net.pubnative.lite.sdk.utils.json.JsonModel;

import org.json.JSONObject;

import java.util.List;

public class User extends JsonModel {
    @BindField
    private String id;
    @BindField
    private String buyeruid;
    @BindField
    private Integer yob;
    @BindField
    private String gender;
    @BindField
    private String keywords;
    @BindField
    private String customdata;
    @BindField
    private Geo geo;
    @BindField
    private List<Data> data;

    public User() {
    }

    public User(JSONObject jsonObject) throws Exception {
        fromJson(jsonObject);
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getBuyeruid() {
        return buyeruid;
    }

    public void setBuyeruid(String buyeruid) {
        this.buyeruid = buyeruid;
    }

    public Integer getYearOfBirth() {
        return yob;
    }

    public void setYearOfBirth(Integer yob) {
        this.yob = yob;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getKeywords() {
        return keywords;
    }

    public void setKeywords(String keywords) {
        this.keywords = keywords;
    }

    public String getCustomData() {
        return customdata;
    }

    public void setCustomData(String customdata) {
        this.customdata = customdata;
    }

    public Geo getGeo() {
        return geo;
    }

    public void setGeo(Geo geo) {
        this.geo = geo;
    }

    public List<Data> getData() {
        return data;
    }

    public void setData(List<Data> data) {
        this.data = data;
    }
}
