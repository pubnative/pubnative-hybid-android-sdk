// HyBid SDK License
//
// https://github.com/pubnative/pubnative-hybid-android-sdk/blob/main/LICENSE
//
package net.pubnative.lite.sdk.models.request;

import net.pubnative.lite.sdk.utils.json.BindField;
import net.pubnative.lite.sdk.utils.json.JsonModel;

import org.json.JSONObject;

import java.util.List;

public class Imp extends JsonModel {
    @BindField
    private String id;
    @BindField
    private List<Metric> metric;
    @BindField
    private Banner banner;
    @BindField
    private Video video;
    @BindField
    private Audio audio;
    @BindField
    private Native aNative;
    @BindField
    private Pmp pmp;
    @BindField
    private String displaymanager;
    @BindField
    private String displaymanagerver;
    @BindField
    private Integer instl = 0;
    @BindField
    private String tagid;
    @BindField
    private Float bidfloor = 0.0f;
    @BindField
    private String bidfloorcur = "USD";
    @BindField
    private Integer clickbrowser;
    @BindField
    private Integer secure;
    @BindField
    private List<String> iframebuster;
    @BindField
    private Integer exp;

    public Imp() {
    }

    public Imp(JSONObject jsonObject) throws Exception {
        fromJson(jsonObject);
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public List<Metric> getMetric() {
        return metric;
    }

    public void setMetric(List<Metric> metric) {
        this.metric = metric;
    }

    public Banner getBanner() {
        return banner;
    }

    public void setBanner(Banner banner) {
        this.banner = banner;
    }

    public Video getVideo() {
        return video;
    }

    public void setVideo(Video video) {
        this.video = video;
    }

    public Audio getAudio() {
        return audio;
    }

    public void setAudio(Audio audio) {
        this.audio = audio;
    }

    public Native getNative() {
        return aNative;
    }

    public void setNative(Native aNative) {
        this.aNative = aNative;
    }

    public Pmp getPmp() {
        return pmp;
    }

    public void setPmp(Pmp pmp) {
        this.pmp = pmp;
    }

    public String getDisplaymanager() {
        return displaymanager;
    }

    public void setDisplaymanager(String displaymanager) {
        this.displaymanager = displaymanager;
    }

    public String getDisplaymanagerver() {
        return displaymanagerver;
    }

    public void setDisplaymanagerver(String displaymanagerver) {
        this.displaymanagerver = displaymanagerver;
    }

    public Integer getInstl() {
        return instl;
    }

    public void setInstl(Integer instl) {
        this.instl = instl;
    }

    public String getTagid() {
        return tagid;
    }

    public void setTagid(String tagid) {
        this.tagid = tagid;
    }

    public Float getBidfloor() {
        return bidfloor;
    }

    public void setBidfloor(Float bidfloor) {
        this.bidfloor = bidfloor;
    }

    public String getBidfloorcur() {
        return bidfloorcur;
    }

    public void setBidfloorcur(String bidfloorcur) {
        this.bidfloorcur = bidfloorcur;
    }

    public Integer getClickbrowser() {
        return clickbrowser;
    }

    public void setClickbrowser(Integer clickbrowser) {
        this.clickbrowser = clickbrowser;
    }

    public Integer getSecure() {
        return secure;
    }

    public void setSecure(Integer secure) {
        this.secure = secure;
    }

    public List<String> getIframebuster() {
        return iframebuster;
    }

    public void setIframebuster(List<String> iframebuster) {
        this.iframebuster = iframebuster;
    }

    public Integer getExp() {
        return exp;
    }

    public void setExp(Integer exp) {
        this.exp = exp;
    }
}
