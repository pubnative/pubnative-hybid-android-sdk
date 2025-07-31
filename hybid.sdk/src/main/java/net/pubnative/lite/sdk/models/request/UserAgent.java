// HyBid SDK License
//
// https://github.com/pubnative/pubnative-hybid-android-sdk/blob/main/LICENSE
//
package net.pubnative.lite.sdk.models.request;

import net.pubnative.lite.sdk.utils.json.BindField;
import net.pubnative.lite.sdk.utils.json.JsonModel;

import org.json.JSONObject;

import java.util.List;

public class UserAgent extends JsonModel {
    @BindField
    private List<BrandVersion> browsers;
    @BindField
    private BrandVersion platform;
    @BindField
    private Integer mobile;
    @BindField
    private String architecture;
    @BindField
    private String bitness;
    @BindField
    private String model;
    @BindField
    private Integer source = 0;

    public UserAgent() {
    }

    public UserAgent(JSONObject jsonObject) throws Exception {
        fromJson(jsonObject);
    }

    public List<BrandVersion> getBrowsers() {
        return browsers;
    }

    public void setBrowsers(List<BrandVersion> browsers) {
        this.browsers = browsers;
    }

    public BrandVersion getPlatform() {
        return platform;
    }

    public void setPlatform(BrandVersion platform) {
        this.platform = platform;
    }

    public Integer getMobile() {
        return mobile;
    }

    public void setMobile(Integer mobile) {
        this.mobile = mobile;
    }

    public String getArchitecture() {
        return architecture;
    }

    public void setArchitecture(String architecture) {
        this.architecture = architecture;
    }

    public String getBitness() {
        return bitness;
    }

    public void setBitness(String bitness) {
        this.bitness = bitness;
    }

    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public Integer getSource() {
        return source;
    }

    public void setSource(Integer source) {
        this.source = source;
    }
}
