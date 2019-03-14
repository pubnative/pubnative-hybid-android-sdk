package net.pubnative.lite.sdk.vpaid.models.vast;

import net.pubnative.lite.sdk.vpaid.xml.Attribute;
import net.pubnative.lite.sdk.vpaid.xml.Tag;

public class Vast {

    @Attribute
    private String version;

    @Tag
    private Ad ad;

    @Tag
    private Status status;

    public String getVersion() {
        return version;
    }

    public Ad getAd() {
        return ad;
    }

    public Status getStatus() {
        return status;
    }
}
