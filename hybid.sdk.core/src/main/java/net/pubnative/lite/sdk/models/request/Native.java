package net.pubnative.lite.sdk.models.request;

import net.pubnative.lite.sdk.utils.json.BindField;
import net.pubnative.lite.sdk.utils.json.JsonModel;

import org.json.JSONObject;

import java.util.List;

public class Native extends JsonModel {
    @BindField
    private String request;
    @BindField
    private String ver;
    @BindField
    private List<Integer> api;
    @BindField
    private List<Integer> battr;

    public Native() {
    }

    public Native(JSONObject jsonObject) throws Exception {
        fromJson(jsonObject);
    }

    public String getRequest() {
        return request;
    }

    public void setRequest(String request) {
        this.request = request;
    }

    public String getVer() {
        return ver;
    }

    public void setVer(String ver) {
        this.ver = ver;
    }

    public List<Integer> getApi() {
        return api;
    }

    public void setApi(List<Integer> api) {
        this.api = api;
    }

    public List<Integer> getBlockedAttr() {
        return battr;
    }

    public void setBlockedAttr(List<Integer> bAttr) {
        this.battr = bAttr;
    }
}
