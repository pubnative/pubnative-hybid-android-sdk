package net.pubnative.lite.sdk.vpaid.models.vast;

import net.pubnative.lite.sdk.vpaid.xml.Attribute;
import net.pubnative.lite.sdk.vpaid.xml.Tag;

public class Linear {
    @Attribute
    private String skipoffset;

    @Tag
    private Duration duration;

    @Tag
    private MediaFiles mediaFiles;

    @Tag
    private AdParameters adParameters;

    @Tag
    private TrackingEvents trackingEvents;

    @Tag
    private VideoClicks videoClicks;

    @Tag
    private Icons icons;

    public String getSkipOffset() {
        return skipoffset;
    }

    public Duration getDuration() {
        return duration;
    }

    public MediaFiles getMediaFiles() {
        return mediaFiles;
    }

    public AdParameters getAdParameters() {
        return adParameters;
    }

    public TrackingEvents getTrackingEvents() {
        return trackingEvents;
    }

    public VideoClicks getVideoClicks() {
        return videoClicks;
    }

    public Icons getIcons() {
        return icons;
    }
}
