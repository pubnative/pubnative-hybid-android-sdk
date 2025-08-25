// HyBid SDK License
//
// https://github.com/pubnative/pubnative-hybid-android-sdk/blob/main/LICENSE
//
package net.pubnative.lite.sdk.models.request;

import net.pubnative.lite.sdk.utils.json.BindField;
import net.pubnative.lite.sdk.utils.json.JsonModel;

import org.json.JSONObject;

public class DataExtension extends JsonModel {

    @BindField
    public Long segtax;
    @BindField
    public String segclass;

    public DataExtension() {
    }

    public DataExtension(JSONObject jsonObject) throws Exception {
        fromJson(jsonObject);
    }

    public DataExtension(Long segtax, String segclass) {
        this.segtax = segtax;
        this.segclass = segclass;
    }
}
