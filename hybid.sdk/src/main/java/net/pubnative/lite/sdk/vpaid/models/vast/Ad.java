package net.pubnative.lite.sdk.vpaid.models.vast;

import net.pubnative.lite.sdk.vpaid.xml.Attribute;
import net.pubnative.lite.sdk.vpaid.xml.Tag;

public class Ad {

    @Attribute
    private String id;

    @Tag
    private InLine inLine;

    public String getId() {
        return id;
    }

    public InLine getInLine() {
        return inLine;
    }
}
