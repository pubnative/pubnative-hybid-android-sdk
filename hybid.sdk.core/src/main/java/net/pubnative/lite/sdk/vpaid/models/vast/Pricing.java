package net.pubnative.lite.sdk.vpaid.models.vast;

import android.text.TextUtils;

import net.pubnative.lite.sdk.vpaid.xml.Attribute;
import net.pubnative.lite.sdk.vpaid.xml.Text;

public class Pricing {
    @Attribute
    private String model;

    @Attribute
    private String currency;

    @Text
    private String text;

    public String getModel() {
        return model;
    }

    public String getCurrency() {
        return currency;
    }

    public String getText() {
        return TextUtils.isEmpty(text) ? "" : text.trim();
    }
}
