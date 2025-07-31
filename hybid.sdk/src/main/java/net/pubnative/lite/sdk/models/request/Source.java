// HyBid SDK License
//
// https://github.com/pubnative/pubnative-hybid-android-sdk/blob/main/LICENSE
//
package net.pubnative.lite.sdk.models.request;

import net.pubnative.lite.sdk.utils.json.BindField;
import net.pubnative.lite.sdk.utils.json.JsonModel;

import org.json.JSONObject;

public class Source extends JsonModel {
    @BindField
    private Integer fd;
    @BindField
    private String tid;
    @BindField
    private String pchain;

    public Source() {
    }

    public Source(JSONObject jsonObject) throws Exception {
        fromJson(jsonObject);
    }

    public Integer getFinalDecision() {
        return fd;
    }

    public String getTransactionId() {
        return tid;
    }

    public String getPaymentIdChain() {
        return pchain;
    }
}
