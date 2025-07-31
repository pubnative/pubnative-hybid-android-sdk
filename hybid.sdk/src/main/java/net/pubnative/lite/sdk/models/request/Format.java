// HyBid SDK License
//
// https://github.com/pubnative/pubnative-hybid-android-sdk/blob/main/LICENSE
//
package net.pubnative.lite.sdk.models.request;

import net.pubnative.lite.sdk.utils.json.BindField;
import net.pubnative.lite.sdk.utils.json.JsonModel;

import org.json.JSONObject;

public class Format extends JsonModel {
    @BindField
    private Integer w;
    @BindField
    private Integer h;
    @BindField
    private Integer wratio;
    @BindField
    private Integer hratio;
    @BindField
    private Integer wmin;

    public Format() {
    }

    public Format(JSONObject jsonObject) throws Exception {
        fromJson(jsonObject);
    }

    public Integer getWidth() {
        return w;
    }

    public void setWidth(Integer width) {
        this.w = width;
    }

    public Integer getHeight() {
        return h;
    }

    public void setHeight(Integer height) {
        this.h = height;
    }

    public Integer getWidthRatio() {
        return wratio;
    }

    public void setWidthRatio(Integer widthRatio) {
        this.wratio = widthRatio;
    }

    public Integer getHeightRatio() {
        return hratio;
    }

    public void setHeightRatio(Integer heightRatio) {
        this.hratio = heightRatio;
    }

    public Integer getWidthMin() {
        return wmin;
    }

    public void setWidthMin(Integer widthMin) {
        this.wmin = widthMin;
    }
}
