package net.pubnative.lite.sdk.vpaid.models.vast;

import net.pubnative.lite.sdk.vpaid.xml.Attribute;
import net.pubnative.lite.sdk.vpaid.xml.Tag;

import java.util.List;

public class Vast {

    @Attribute
    private String version;

    @Tag("Ad")
    private List<Ad> ads;

    @Tag
    private Status status;

    @Tag("Error")
    private List<Error> errors;

    public String getVersion() {
        return version;
    }

    public List<Ad> getAds() {
        return ads;
    }

    public Status getStatus() {
        return status;
    }

    public List<Error> getErrors() {
        return errors;
    }
}
