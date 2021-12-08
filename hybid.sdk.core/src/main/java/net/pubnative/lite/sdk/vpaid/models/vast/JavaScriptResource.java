package net.pubnative.lite.sdk.vpaid.models.vast;

import android.text.TextUtils;

import net.pubnative.lite.sdk.vpaid.xml.Attribute;
import net.pubnative.lite.sdk.vpaid.xml.Text;

public class JavaScriptResource {
    @Attribute
    private String apiFramework;

    @Attribute
    private String browserOptional;

    @Text
    private String text;

    public String getApiFramework() {
        return apiFramework;
    }

    public String isBrowserOptional() {
        return browserOptional;
    }

    public String getText() {
        return TextUtils.isEmpty(text) ? "" : text.trim();
    }
}
