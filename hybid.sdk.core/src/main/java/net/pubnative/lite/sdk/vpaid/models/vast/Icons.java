package net.pubnative.lite.sdk.vpaid.models.vast;

import net.pubnative.lite.sdk.vpaid.xml.Tag;

import java.util.List;

public class Icons {
    @Tag("Icon")
    private List<Icon> icons;

    public List<Icon> getIcons() {
        return icons;
    }
}
