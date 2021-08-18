package net.pubnative.lite.sdk.vpaid.models.vast;

import net.pubnative.lite.sdk.vpaid.xml.Tag;

import java.util.List;

public class VideoClicks {

    @Tag
    private ClickThrough clickThrough;

    @Tag("ClickTracking")
    private List<ClickTracking> clickTrackingList;

    @Tag("CustomClick")
    private List<CustomClick> customClickList;

    public List<ClickTracking> getClickTrackingList() {
        return clickTrackingList;
    }

    public ClickThrough getClickThrough() {
        return clickThrough;
    }

    public List<CustomClick> getCustomClickList() {
        return customClickList;
    }
}
