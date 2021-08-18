package net.pubnative.lite.sdk.vpaid.models.vast;

import net.pubnative.lite.sdk.vpaid.xml.Tag;

import java.util.List;

public class Creatives {
    @Tag("Creative")
    private List<Creative> creatives;

    public List<Creative> getCreatives() {
        return creatives;
    }
}
