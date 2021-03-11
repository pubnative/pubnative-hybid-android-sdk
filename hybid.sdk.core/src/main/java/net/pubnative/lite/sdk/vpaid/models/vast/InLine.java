package net.pubnative.lite.sdk.vpaid.models.vast;

import net.pubnative.lite.sdk.vpaid.xml.Tag;

import java.util.List;

public class InLine implements VastAdSource {

    @Tag
    private AdSystem adSystem;

    @Tag
    private Error error;

    @Tag("Impression")
    private List<Impression> impressionList;

    @Tag
    private Creatives creatives;

    @Tag("Extensions")
    private List<Extension> extensionList;

    @Override
    public AdSystem getAdSystem() {
        return adSystem;
    }

    @Override
    public Error getError() {
        return error;
    }

    @Override
    public Creatives getCreatives() {
        return creatives;
    }

    @Override
    public List<Impression> getImpressionList() {
        return impressionList;
    }

    public List<Extension> getExtensions() {
        return extensionList;
    }
}
