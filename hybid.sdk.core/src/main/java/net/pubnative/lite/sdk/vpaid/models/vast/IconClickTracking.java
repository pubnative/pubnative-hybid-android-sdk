package net.pubnative.lite.sdk.vpaid.models.vast;

import net.pubnative.lite.sdk.vpaid.xml.Attribute;
import net.pubnative.lite.sdk.vpaid.xml.Text;

public class IconClickTracking {
    @Attribute
    private String id;

    @Text
    private String text;

    public String getId() {
        return id;
    }

    public String getText() {
        return text;
    }
}
