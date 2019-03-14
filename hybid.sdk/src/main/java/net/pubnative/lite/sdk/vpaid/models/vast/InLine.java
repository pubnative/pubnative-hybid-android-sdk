package net.pubnative.lite.sdk.vpaid.models.vast;

import net.pubnative.lite.sdk.vpaid.xml.Tag;

import java.util.List;

public class InLine {

    @Tag
    private AdSystem adSystem;

    @Tag
    private Error error;

    @Tag("Impression")
    private List<Impression> impressionList;

    @Tag
    private Creatives creatives;

    public AdSystem getAdSystem() {
        return adSystem;
    }

    public Error getError() {
        return error;
    }

    public Creatives getCreatives() {
        return creatives;
    }

    public List<Impression> getImpressionList() {
        return impressionList;
    }
}
