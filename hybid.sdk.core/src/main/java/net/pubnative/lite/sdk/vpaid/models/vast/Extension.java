package net.pubnative.lite.sdk.vpaid.models.vast;

import net.pubnative.lite.sdk.vpaid.xml.Attribute;
import net.pubnative.lite.sdk.vpaid.xml.Tag;

public class Extension {
    @Attribute
    private String type;

    @Tag
    private AdVerifications adVerifications;

    public String getType() {
        return type;
    }

    public AdVerifications getAdVerifications() {
        return adVerifications;
    }
}