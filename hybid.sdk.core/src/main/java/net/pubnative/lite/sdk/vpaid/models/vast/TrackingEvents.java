package net.pubnative.lite.sdk.vpaid.models.vast;

import net.pubnative.lite.sdk.vpaid.xml.Tag;

import java.util.List;

public class TrackingEvents {
    @Tag("Tracking")
    private List<Tracking> trackingList;

    public List<Tracking> getTrackingList() {
        return trackingList;
    }
}
