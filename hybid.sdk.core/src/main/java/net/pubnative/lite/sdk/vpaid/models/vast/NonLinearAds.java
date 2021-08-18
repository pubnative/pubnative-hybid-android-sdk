package net.pubnative.lite.sdk.vpaid.models.vast;

import net.pubnative.lite.sdk.vpaid.xml.Tag;

import java.util.List;

public class NonLinearAds {
    @Tag("NonLinear")
    private List<NonLinear> nonLinearList;

    @Tag
    private TrackingEvents trackingEvents;

    public List<NonLinear> getNonLinearList() {
        return nonLinearList;
    }

    public TrackingEvents getTrackingEvents() {
        return trackingEvents;
    }
}
