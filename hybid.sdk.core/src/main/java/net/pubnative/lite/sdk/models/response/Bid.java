package net.pubnative.lite.sdk.models.response;

import net.pubnative.lite.sdk.utils.json.BindField;
import net.pubnative.lite.sdk.utils.json.JsonModel;

import org.json.JSONObject;

import java.util.List;

public class Bid extends JsonModel {
    @BindField
    private String id;
    @BindField
    private String impid;
    @BindField
    private Float price;
    @BindField
    private String nurl;
    @BindField
    private String burl;
    @BindField
    private String lurl;
    @BindField
    private String adm;
    @BindField
    private String adid;
    @BindField
    private List<String> adomain;
    @BindField
    private String bundle;
    @BindField
    private String iurl;
    @BindField
    private String cid;
    @BindField
    private String crid;
    @BindField
    private String tactic;
    @BindField
    private List<String> cat;
    @BindField
    private List<Integer> attr;
    @BindField
    private Integer api;
    @BindField
    private Integer protocol;
    @BindField
    private Integer qagmediarating;
    @BindField
    private String language;
    @BindField
    private String dealid;
    @BindField
    private Integer w;
    @BindField
    private Integer h;
    @BindField
    private Integer wratio;
    @BindField
    private Integer hratio;
    @BindField
    private Integer exp;

    public Bid() {
    }

    public Bid(JSONObject jsonObject) throws Exception {
        fromJson(jsonObject);
    }

    public String getId() {
        return id;
    }

    public String getImpressionid() {
        return impid;
    }

    public Float getPrice() {
        return price;
    }

    public String getNoticeUrl() {
        return nurl;
    }

    public String getBillingUrl() {
        return burl;
    }

    public String getLossNoticeUrl() {
        return lurl;
    }

    public String getAdMarkup() {
        return adm;
    }

    public String getAdId() {
        return adid;
    }

    public List<String> getAdvertiserDomains() {
        return adomain;
    }

    public String getBundle() {
        return bundle;
    }

    public String getIurl() {
        return iurl;
    }

    public String getCampaignId() {
        return cid;
    }

    public String getCreativeId() {
        return crid;
    }

    public String getTactic() {
        return tactic;
    }

    public List<String> getCategories() {
        return cat;
    }

    public List<Integer> getAttributes() {
        return attr;
    }

    public Integer getApi() {
        return api;
    }

    public Integer getProtocol() {
        return protocol;
    }

    public Integer getQagMediaRating() {
        return qagmediarating;
    }

    public String getLanguage() {
        return language;
    }

    public String getDealId() {
        return dealid;
    }

    public Integer getWidth() {
        return w;
    }

    public Integer getHeight() {
        return h;
    }

    public Integer getWidthRatio() {
        return wratio;
    }

    public Integer getHeightRatio() {
        return hratio;
    }

    public Integer getExpiration() {
        return exp;
    }
}
