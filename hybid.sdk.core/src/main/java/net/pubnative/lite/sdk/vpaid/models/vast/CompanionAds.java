package net.pubnative.lite.sdk.vpaid.models.vast;

import net.pubnative.lite.sdk.vpaid.xml.Attribute;
import net.pubnative.lite.sdk.vpaid.xml.Tag;

import java.util.List;

public class CompanionAds {
    @Attribute
    private String required;

    @Tag("Companion")
    private List<Companion> companions;

    public String getRequired() {
        return required;
    }

    public List<Companion> getCompanions() {
        return companions;
    }
}
