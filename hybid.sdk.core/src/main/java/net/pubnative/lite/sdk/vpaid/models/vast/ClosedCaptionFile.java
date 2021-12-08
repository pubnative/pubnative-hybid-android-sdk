package net.pubnative.lite.sdk.vpaid.models.vast;

import android.text.TextUtils;

import net.pubnative.lite.sdk.vpaid.xml.Attribute;
import net.pubnative.lite.sdk.vpaid.xml.Text;

public class ClosedCaptionFile {
    @Attribute
    private String type;

    @Attribute
    private String language;

    @Text
    private String text;

    public String isType() {
        return type;
    }

    public String isLanguage() {
        return language;
    }

    public String getText() {
        return TextUtils.isEmpty(text) ? "" : text.trim();
    }
}
