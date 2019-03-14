package net.pubnative.lite.sdk.vpaid.models.vast;

import net.pubnative.lite.sdk.vpaid.xml.Tag;

import java.util.List;

public class CompanionAds {

    @Tag("Companion")
    private List<Companion> companionList;

    public List<Companion> getCompanionList() {
        return companionList;
    }
}
