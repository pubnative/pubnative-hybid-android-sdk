package net.pubnative.lite.sdk.models.request;

import net.pubnative.lite.sdk.utils.json.BindField;
import net.pubnative.lite.sdk.utils.json.JsonModel;

import org.json.JSONObject;

public class Device extends JsonModel {
    @BindField
    private String ua;
    @BindField
    private Geo geo;
    @BindField
    private Integer dnt;
    @BindField
    private Integer lmt;
    @BindField
    private String ip;
    @BindField
    private String ipv6;
    @BindField
    private Integer devicetype;
    @BindField
    private String make;
    @BindField
    private String model;
    @BindField
    private String os;
    @BindField
    private String osv;
    @BindField
    private String hmw;
    @BindField
    private Integer h;
    @BindField
    private Integer w;
    @BindField
    private Integer ppi;
    @BindField
    private Float pxratio;
    @BindField
    private Integer js;
    @BindField
    private Integer geofetch;
    @BindField
    private String flashver;
    @BindField
    private String language;
    @BindField
    private String carrier;
    @BindField
    private String mccmnc;
    @BindField
    private Integer connectiontype;
    @BindField
    private String ifa;
    @BindField
    private String didsha1;
    @BindField
    private String didmd5;
    @BindField
    private String dpidsha1;
    @BindField
    private String dpidmd5;
    @BindField
    private String macsha1;
    @BindField
    private String macmd5;

    public Device() {
    }

    public Device(JSONObject jsonObject) throws Exception {
        fromJson(jsonObject);
    }

    public String getUserAgent() {
        return ua;
    }

    public void setUserAgent(String ua) {
        this.ua = ua;
    }

    public Geo getGeo() {
        return geo;
    }

    public void setGeo(Geo geo) {
        this.geo = geo;
    }

    public Integer getDnt() {
        return dnt;
    }

    public void setDnt(Integer dnt) {
        this.dnt = dnt;
    }

    public Integer getLmt() {
        return lmt;
    }

    public void setLmt(Integer lmt) {
        this.lmt = lmt;
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public String getIpv6() {
        return ipv6;
    }

    public void setIpv6(String ipv6) {
        this.ipv6 = ipv6;
    }

    public Integer getDeviceType() {
        return devicetype;
    }

    public void setDeviceType(Integer devicetype) {
        this.devicetype = devicetype;
    }

    public String getMake() {
        return make;
    }

    public void setMake(String make) {
        this.make = make;
    }

    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public String getOs() {
        return os;
    }

    public void setOs(String os) {
        this.os = os;
    }

    public String getOsVersion() {
        return osv;
    }

    public void setOsVersion(String osv) {
        this.osv = osv;
    }

    public String getHmw() {
        return hmw;
    }

    public void setHmw(String hmw) {
        this.hmw = hmw;
    }

    public Integer getH() {
        return h;
    }

    public void setH(Integer h) {
        this.h = h;
    }

    public Integer getW() {
        return w;
    }

    public void setW(Integer w) {
        this.w = w;
    }

    public Integer getPpi() {
        return ppi;
    }

    public void setPpi(Integer ppi) {
        this.ppi = ppi;
    }

    public Float getPxratio() {
        return pxratio;
    }

    public void setPxratio(Float pxratio) {
        this.pxratio = pxratio;
    }

    public Integer getJs() {
        return js;
    }

    public void setJs(Integer js) {
        this.js = js;
    }

    public Integer getGeofetch() {
        return geofetch;
    }

    public void setGeofetch(Integer geofetch) {
        this.geofetch = geofetch;
    }

    public String getFlashver() {
        return flashver;
    }

    public void setFlashver(String flashver) {
        this.flashver = flashver;
    }

    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
        this.language = language;
    }

    public String getCarrier() {
        return carrier;
    }

    public void setCarrier(String carrier) {
        this.carrier = carrier;
    }

    public String getMccmnc() {
        return mccmnc;
    }

    public void setMccmnc(String mccmnc) {
        this.mccmnc = mccmnc;
    }

    public Integer getConnectiontype() {
        return connectiontype;
    }

    public void setConnectiontype(Integer connectiontype) {
        this.connectiontype = connectiontype;
    }

    public String getIfa() {
        return ifa;
    }

    public void setIfa(String ifa) {
        this.ifa = ifa;
    }

    public String getDidsha1() {
        return didsha1;
    }

    public void setDidsha1(String didsha1) {
        this.didsha1 = didsha1;
    }

    public String getDidmd5() {
        return didmd5;
    }

    public void setDidmd5(String didmd5) {
        this.didmd5 = didmd5;
    }

    public String getDpidsha1() {
        return dpidsha1;
    }

    public void setDpidsha1(String dpidsha1) {
        this.dpidsha1 = dpidsha1;
    }

    public String getDpidmd5() {
        return dpidmd5;
    }

    public void setDpidmd5(String dpidmd5) {
        this.dpidmd5 = dpidmd5;
    }

    public String getMacsha1() {
        return macsha1;
    }

    public void setMacsha1(String macsha1) {
        this.macsha1 = macsha1;
    }

    public String getMacmd5() {
        return macmd5;
    }

    public void setMacmd5(String macmd5) {
        this.macmd5 = macmd5;
    }
}
