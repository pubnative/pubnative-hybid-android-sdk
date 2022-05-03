package net.pubnative.lite.sdk.vpaid.models.vast;

import net.pubnative.lite.sdk.vpaid.xml.Tag;

public class VerveCTAButton {
    @Tag
    private HTMLResource htmlResource;
    @Tag
    private TrackingEvents trackingEvents;

    public HTMLResource getHtmlResource() {
        return htmlResource;
    }

    public TrackingEvents getTrackingEvents() {
        return trackingEvents;
    }
}
