package net.pubnative.lite.sdk.models.request;

import net.pubnative.lite.sdk.utils.json.BindField;
import net.pubnative.lite.sdk.utils.json.JsonModel;

import org.json.JSONObject;

public class Geo extends JsonModel {
    @BindField
    private Float lat;
    @BindField
    private Float lon;
    @BindField
    private Integer type;
    @BindField
    private Integer accuracy;
    @BindField
    private Integer lastfix;
    @BindField
    private Integer ipservice;
    @BindField
    private String country;
    @BindField
    private String region;
    @BindField
    private String regionfips104;
    @BindField
    private String metro;
    @BindField
    private String city;
    @BindField
    private String zip;
    @BindField
    private Integer utcoffset;

    public Geo() {
    }

    public Geo(JSONObject jsonObject) throws Exception {
        fromJson(jsonObject);
    }

    public Float getLat() {
        return lat;
    }

    public void setLat(Float lat) {
        this.lat = lat;
    }

    public Float getLon() {
        return lon;
    }

    public void setLon(Float lon) {
        this.lon = lon;
    }

    public Integer getType() {
        return type;
    }

    public void setType(Integer type) {
        this.type = type;
    }

    public Integer getAccuracy() {
        return accuracy;
    }

    public void setAccuracy(Integer accuracy) {
        this.accuracy = accuracy;
    }

    public Integer getLastfix() {
        return lastfix;
    }

    public void setLastfix(Integer lastfix) {
        this.lastfix = lastfix;
    }

    public Integer getIpservice() {
        return ipservice;
    }

    public void setIpservice(Integer ipservice) {
        this.ipservice = ipservice;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getRegion() {
        return region;
    }

    public void setRegion(String region) {
        this.region = region;
    }

    public String getRegionfips104() {
        return regionfips104;
    }

    public void setRegionfips104(String regionfips104) {
        this.regionfips104 = regionfips104;
    }

    public String getMetro() {
        return metro;
    }

    public void setMetro(String metro) {
        this.metro = metro;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getZip() {
        return zip;
    }

    public void setZip(String zip) {
        this.zip = zip;
    }

    public Integer getUtcoffset() {
        return utcoffset;
    }

    public void setUtcoffset(Integer utcoffset) {
        this.utcoffset = utcoffset;
    }
}
