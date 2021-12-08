package net.pubnative.lite.sdk.vpaid.models.vast;

import android.text.TextUtils;

import net.pubnative.lite.sdk.vpaid.xml.Attribute;
import net.pubnative.lite.sdk.vpaid.xml.Text;

public class UniversalAdId {
    @Attribute
    private String idValue;

    @Attribute
    private String idRegistry;

    @Text
    private String text;

    public String getIdValue() {
        return idValue;
    }

    public String getIdRegistry() {
        return idRegistry;
    }

    public String getText() {
        return TextUtils.isEmpty(text) ? "" : text.trim();
    }
}
