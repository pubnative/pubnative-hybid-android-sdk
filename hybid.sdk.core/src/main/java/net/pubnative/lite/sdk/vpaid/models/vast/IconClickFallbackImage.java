package net.pubnative.lite.sdk.vpaid.models.vast;

import net.pubnative.lite.sdk.vpaid.xml.Attribute;
import net.pubnative.lite.sdk.vpaid.xml.Tag;

import java.util.List;

public class IconClickFallbackImage {
    @Attribute
    private int width;

    @Attribute
    private int height;

    @Tag
    private AltText altText;

    @Tag("StaticResource")
    private List<StaticResource> staticResources;

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    public AltText getAltText() {
        return altText;
    }

    public List<StaticResource> getStaticResources() {
        return staticResources;
    }
}
