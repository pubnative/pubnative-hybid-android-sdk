package net.pubnative.lite.sdk.vpaid.models.vast;

import net.pubnative.lite.sdk.vpaid.xml.Attribute;
import net.pubnative.lite.sdk.vpaid.xml.Text;

public class JavaScriptResource {
    @Attribute
    private String apiFramework;

    @Attribute
    private boolean browserOptional;

    @Text
    private String text;

    public String getApiFramework() {
        return apiFramework;
    }

    public boolean isBrowserOptional() {
        return browserOptional;
    }

    public String getText() {
        return text;
    }
}
