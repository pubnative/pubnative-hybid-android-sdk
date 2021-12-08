package net.pubnative.lite.sdk.vpaid.models.vast;

import net.pubnative.lite.sdk.vpaid.xml.Attribute;
import net.pubnative.lite.sdk.vpaid.xml.Tag;

import java.util.List;

public class NonLinear {
    @Attribute
    private String id;

    @Attribute
    private String width;

    @Attribute
    private String height;

    @Attribute
    private String expandedWidth;

    @Attribute
    private String expandedHeight;

    @Attribute
    private String scalable;

    @Attribute
    private String maintainAspectRatio;

    @Attribute
    private String apiFramework;

    @Attribute
    private String minSuggestedDuration;

    @Tag("StaticResource")
    private List<StaticResource> staticResources;

    @Tag("IFrameResource")
    private List<IFrameResource> iFrameResources;

    @Tag("HTMLResource")
    private List<HTMLResource> htmlResources;

    @Tag
    private AdParameters adParameters;

    @Tag
    private NonLinearClickThrough nonLinearClickThrough;

    @Tag("NonLinearClickTracking")
    private List<NonLinearClickTracking> nonLinearClickTrackingList;

    public List<StaticResource> getStaticResources() {
        return staticResources;
    }

    public List<IFrameResource> getiFrameResources() {
        return iFrameResources;
    }

    public List<HTMLResource> getHtmlResources() {
        return htmlResources;
    }

    public AdParameters getAdParameters() {
        return adParameters;
    }

    public NonLinearClickThrough getNonLinearClickThrough() {
        return nonLinearClickThrough;
    }

    public List<NonLinearClickTracking> getNonLinearClickTrackingList() {
        return nonLinearClickTrackingList;
    }

    public String getId() {
        return id;
    }

    public String getWidth() {
        return width;
    }

    public String getHeight() {
        return height;
    }

    public String getExpandedWidth() {
        return expandedWidth;
    }

    public String getExpandedHeight() {
        return expandedHeight;
    }

    public String isScalable() {
        return scalable;
    }

    public String isMaintainAspectRatio() {
        return maintainAspectRatio;
    }

    public String getApiFramework() {
        return apiFramework;
    }

    public String getMinSuggestedDuration() {
        return minSuggestedDuration;
    }
}
