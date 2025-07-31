// HyBid SDK License
//
// https://github.com/pubnative/pubnative-hybid-android-sdk/blob/main/LICENSE
//
package net.pubnative.lite.sdk.models.request;

import net.pubnative.lite.sdk.utils.json.BindField;
import net.pubnative.lite.sdk.utils.json.JsonModel;

import org.json.JSONObject;

import java.util.List;

public class Banner extends JsonModel {
    @BindField
    private List<Format> format;
    @BindField
    private Integer w;
    @BindField
    private Integer h;
    @BindField
    private Integer wmax;
    @BindField
    private Integer hmax;
    @BindField
    private Integer wmin;
    @BindField
    private Integer hmin;
    @BindField
    private List<Integer> btype;
    @BindField
    private List<Integer> battr;
    @BindField
    private Integer pos;
    @BindField
    private List<String> mimes;
    @BindField
    private Integer topframe;
    @BindField
    private List<Integer> expdir;
    @BindField
    private List<Integer> api;
    @BindField
    private String id;
    @BindField
    private Integer vcm;

    public Banner() {
    }

    public Banner(JSONObject jsonObject) throws Exception {
        fromJson(jsonObject);
    }

    public List<Format> getFormat() {
        return format;
    }

    public void setFormat(List<Format> format) {
        this.format = format;
    }

    public Integer getW() {
        return w;
    }

    public void setW(Integer w) {
        this.w = w;
    }

    public Integer getH() {
        return h;
    }

    public void setH(Integer h) {
        this.h = h;
    }

    public Integer getWmax() {
        return wmax;
    }

    public void setWmax(Integer wmax) {
        this.wmax = wmax;
    }

    public Integer getHmax() {
        return hmax;
    }

    public void setHmax(Integer hmax) {
        this.hmax = hmax;
    }

    public Integer getWmin() {
        return wmin;
    }

    public void setWmin(Integer wmin) {
        this.wmin = wmin;
    }

    public Integer getHmin() {
        return hmin;
    }

    public void setHmin(Integer hmin) {
        this.hmin = hmin;
    }

    public List<Integer> getBtype() {
        return btype;
    }

    public void setBtype(List<Integer> btype) {
        this.btype = btype;
    }

    public List<Integer> getBattr() {
        return battr;
    }

    public void setBattr(List<Integer> battr) {
        this.battr = battr;
    }

    public Integer getPos() {
        return pos;
    }

    public void setPos(Integer pos) {
        this.pos = pos;
    }

    public List<String> getMimes() {
        return mimes;
    }

    public void setMimes(List<String> mimes) {
        this.mimes = mimes;
    }

    public Integer getTopframe() {
        return topframe;
    }

    public void setTopframe(Integer topframe) {
        this.topframe = topframe;
    }

    public List<Integer> getExpdir() {
        return expdir;
    }

    public void setExpdir(List<Integer> expdir) {
        this.expdir = expdir;
    }

    public List<Integer> getApi() {
        return api;
    }

    public void setApi(List<Integer> api) {
        this.api = api;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Integer getVcm() {
        return vcm;
    }

    public void setVcm(Integer vcm) {
        this.vcm = vcm;
    }
}
