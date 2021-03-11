package net.pubnative.lite.sdk.vpaid.models.vast;

import net.pubnative.lite.sdk.vpaid.xml.Attribute;
import net.pubnative.lite.sdk.vpaid.xml.Tag;

import java.util.List;

public class Companion {

    @Attribute
    private String id;

    @Attribute
    private int width;

    @Attribute
    private int height;

    @Attribute
    private int expandedWidth;

    @Attribute
    private int expandedHeight;

    @Attribute
    private String apiFramework;

    @Tag
    private StaticResource staticResource;

    @Tag
    private TrackingEvents trackingEvents;

    @Tag
    private CompanionClickThrough companionClickThrough;

    @Tag
    private List<CompanionClickTracking> companionClickTracking;

    public String getId() {
        return id;
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    public int getExpandedWidth() {
        return expandedWidth;
    }

    public int getExpandedHeight() {
        return expandedHeight;
    }

    public String getApiFramework() {
        return apiFramework;
    }

    public StaticResource getStaticResource() {
        return staticResource;
    }

    public TrackingEvents getTrackingEvents() {
        return trackingEvents;
    }

    public CompanionClickThrough getCompanionClickThrough() {
        return companionClickThrough;
    }

    public List<CompanionClickTracking> getCompanionClickTracking() {
        return companionClickTracking;
    }
}
