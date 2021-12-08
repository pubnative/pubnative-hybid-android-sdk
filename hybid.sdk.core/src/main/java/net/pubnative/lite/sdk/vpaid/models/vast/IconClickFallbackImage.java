package net.pubnative.lite.sdk.vpaid.models.vast;

import net.pubnative.lite.sdk.vpaid.xml.Attribute;
import net.pubnative.lite.sdk.vpaid.xml.Tag;

import java.util.List;

public class IconClickFallbackImage {
    @Attribute
    private String width;

    @Attribute
    private String height;

    @Tag
    private AltText altText;

    @Tag("StaticResource")
    private List<StaticResource> staticResources;

    public String getWidth() {
        return width;
    }

    public String getHeight() {
        return height;
    }

    public AltText getAltText() {
        return altText;
    }

    public List<StaticResource> getStaticResources() {
        return staticResources;
    }
}
