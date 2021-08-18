package net.pubnative.lite.sdk.vpaid.models.vast;

import net.pubnative.lite.sdk.vpaid.xml.Attribute;
import net.pubnative.lite.sdk.vpaid.xml.Tag;

import java.util.List;

public class NonLinear {
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
    private boolean scalable;

    @Attribute
    private boolean maintainAspectRatio;

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

    public boolean isScalable() {
        return scalable;
    }

    public boolean isMaintainAspectRatio() {
        return maintainAspectRatio;
    }

    public String getApiFramework() {
        return apiFramework;
    }

    public String getMinSuggestedDuration() {
        return minSuggestedDuration;
    }
}
