package net.pubnative.tarantula.sdk.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by erosgarciaponte on 10.01.18.
 */

public class AdRequest {
    @SerializedName("apptoken")
    @Expose
    public String apptoken;

    @SerializedName("os")
    @Expose
    public String os;

    @SerializedName("osver")
    @Expose
    public String osver;

    @SerializedName("devicemodel")
    @Expose
    public String devicemodel;

    @SerializedName("dnt")
    @Expose
    public String dnt;

    @SerializedName("al")
    @Expose
    public String al;

    @SerializedName("mf")
    @Expose
    public String mf;

    @SerializedName("zoneid")
    @Expose
    public String zoneid;

    @SerializedName("locale")
    @Expose
    public String locale;

    @SerializedName("lat")
    @Expose
    public String latitude;

    @SerializedName("long")
    @Expose
    public String longitude;

    @SerializedName("gender")
    @Expose
    public String gender;

    @SerializedName("age")
    @Expose
    public String age;

    @SerializedName("bundleid")
    @Expose
    public String bundleid;

    @SerializedName("keywords")
    @Expose
    public String keywords;

    @SerializedName("coppa")
    @Expose
    public String coppa;

    @SerializedName("gid")
    @Expose
    public String gid;

    @SerializedName("gidmd5")
    @Expose
    public String gidmd5;

    @SerializedName("gidsha1")
    @Expose
    public String gidsha1;

    @SerializedName("test")
    @Expose
    public String testMode;
}
