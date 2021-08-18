package net.pubnative.lite.sdk.vpaid.models.vast;

import net.pubnative.lite.sdk.vpaid.xml.Tag;

import java.util.List;

public class CreativeExtensions {
    @Tag("CreativeExtension")
    private List<CreativeExtension> creativeExtensions;

    public List<CreativeExtension> getCreativeExtensions() {
        return creativeExtensions;
    }
}
