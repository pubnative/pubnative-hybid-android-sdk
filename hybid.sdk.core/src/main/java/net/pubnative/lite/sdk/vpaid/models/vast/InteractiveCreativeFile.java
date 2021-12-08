package net.pubnative.lite.sdk.vpaid.models.vast;

import android.text.TextUtils;

import net.pubnative.lite.sdk.vpaid.xml.Attribute;
import net.pubnative.lite.sdk.vpaid.xml.Text;

public class InteractiveCreativeFile {
    @Attribute
    private String type;

    @Attribute
    private String apiFramework;

    @Attribute
    private boolean variableDuration;

    @Text
    private String text;

    public String getType() {
        return type;
    }

    public String getApiFramework() {
        return apiFramework;
    }

    public boolean isVariableDuration() {
        return variableDuration;
    }

    public String getText() {
        return TextUtils.isEmpty(text) ? "" : text.trim();
    }
}
