package net.pubnative.lite.sdk.models.request;

import net.pubnative.lite.sdk.utils.json.BindField;
import net.pubnative.lite.sdk.utils.json.JsonModel;

import java.util.List;

public class Ext extends JsonModel {

    @BindField
    private Integer gdpr;
    @BindField
    private String gpp;
    @BindField
    private List<Integer> gpp_sid;
    @BindField
    private String us_privacy;

    public Ext() {}

    public Ext(Integer gdpr, String gpp, List<Integer> gpp_sid, String us_privacy) {
        this.gdpr = gdpr;
        this.gpp = gpp;
        this.gpp_sid = gpp_sid;
        this.us_privacy = us_privacy;
    }

    public void setGdpr(Integer gdpr) {
        this.gdpr = gdpr;
    }

    public void setGpp(String gpp) {
        this.gpp = gpp;
    }

    public void setGppSid(List<Integer> gppSid) {
        this.gpp_sid = gppSid;
    }

    public void setUsPrivacy(String usPrivacy) {
        this.us_privacy = usPrivacy;
    }
}
