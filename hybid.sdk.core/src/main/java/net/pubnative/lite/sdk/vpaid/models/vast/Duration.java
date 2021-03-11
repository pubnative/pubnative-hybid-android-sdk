package net.pubnative.lite.sdk.vpaid.models.vast;

import android.text.TextUtils;

import net.pubnative.lite.sdk.vpaid.xml.Text;

public class Duration {

    @Text
    private String text;

    public String getText() {
        if (!TextUtils.isEmpty(text)){
            return text;
        } else {
            return "00:00:10";
        }
    }
}
