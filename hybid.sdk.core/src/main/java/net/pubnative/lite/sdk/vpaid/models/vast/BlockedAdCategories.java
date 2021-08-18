package net.pubnative.lite.sdk.vpaid.models.vast;

import net.pubnative.lite.sdk.vpaid.xml.Attribute;
import net.pubnative.lite.sdk.vpaid.xml.Text;

public class BlockedAdCategories {
    @Attribute
    private String authority;

    @Text
    private String text;

    public String getAuthority() {
        return authority;
    }

    public String getText() {
        return text;
    }
}
