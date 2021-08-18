package net.pubnative.lite.sdk.vpaid.models.vast;

import net.pubnative.lite.sdk.vpaid.xml.Tag;

import java.util.List;

public class IconClicks {
    @Tag
    private IconClickThrough iconClickThrough;

    @Tag("IconClickTracking")
    private List<IconClickTracking> iconClickTrackingList;

    @Tag
    private IconClickFallbackImages iconClickFallbackImages;

    public IconClickThrough getIconClickThrough() {
        return iconClickThrough;
    }

    public List<IconClickTracking> getIconClickTrackingList() {
        return iconClickTrackingList;
    }

    public IconClickFallbackImages getIconClickFallbackImages() {
        return iconClickFallbackImages;
    }
}
