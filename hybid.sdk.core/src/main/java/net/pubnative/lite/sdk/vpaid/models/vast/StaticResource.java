package net.pubnative.lite.sdk.vpaid.models.vast;

import android.text.TextUtils;

import net.pubnative.lite.sdk.vpaid.xml.Attribute;
import net.pubnative.lite.sdk.vpaid.xml.Text;

public class StaticResource {

    @Attribute
    private String creativeType;

    @Text
    private String text;

    public String getText() {
        return TextUtils.isEmpty(text) ? "" : text.trim();
    }

    public String getCreativeType() {
        return creativeType;
    }
}
