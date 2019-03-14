package net.pubnative.lite.sdk.vpaid.models.vast;

import net.pubnative.lite.sdk.vpaid.xml.Attribute;
import net.pubnative.lite.sdk.vpaid.xml.Text;

public class StaticResource {

    @Attribute
    private String creativeType;

    @Text
    private String text;

    public String getText() {
        return text;
    }

    public String getCreativeType() {
        return creativeType;
    }
}
