package net.pubnative.lite.sdk.vpaid.models.vast;

import net.pubnative.lite.sdk.vpaid.xml.Attribute;
import net.pubnative.lite.sdk.vpaid.xml.Tag;

public class Linear {

    @Tag
    private Duration duration;

    @Tag
    private TrackingEvents trackingEvents;

    @Tag
    private VideoClicks videoClicks;

    @Tag
    private MediaFiles mediaFiles;

    @Tag
    private AdParameters adParameters;

    @Attribute
    private String skipoffset;

    public Duration getDuration() {
        return duration;
    }

    public TrackingEvents getTrackingEvents() {
        return trackingEvents;
    }

    public VideoClicks getVideoClicks() {
        return videoClicks;
    }

    public MediaFiles getMediaFiles() {
        return mediaFiles;
    }

    public AdParameters getAdParameters() {
        return adParameters;
    }

    public String getSkipoffset() {
        return skipoffset;
    }
}
