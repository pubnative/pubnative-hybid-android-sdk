package net.pubnative.lite.sdk.vpaid.models.vast;

import net.pubnative.lite.sdk.vpaid.xml.Attribute;
import net.pubnative.lite.sdk.vpaid.xml.Tag;

import java.util.List;

public class Companion {
    @Attribute
    private String width;

    @Attribute
    private String height;

    @Attribute
    private String id;

    @Attribute
    private String assetWidth;

    @Attribute
    private String assetHeight;

    @Attribute
    private String expandedWidth;

    @Attribute
    private String expandedHeight;

    @Attribute
    private String apiFramework;

    @Attribute
    private String adSlotId;

    @Attribute
    private String pxratio;

    @Attribute
    private String renderingMode;

    @Tag("StaticResource")
    private List<StaticResource> staticResources;

    @Tag("IFrameResource")
    private List<IFrameResource> iFrameResources;

    @Tag("HTMLResource")
    private List<HTMLResource> htmlResources;

    @Tag
    private AdParameters adParameters;

    @Tag
    private AltText altText;

    @Tag
    private CompanionClickThrough companionClickThrough;

    @Tag("CompanionClickTracking")
    private List<CompanionClickTracking> companionClickTrackingList;

    @Tag
    private TrackingEvents trackingEvents;

    public String getWidth() {
        return width;
    }

    public String getHeight() {
        return height;
    }

    public String getId() {
        return id;
    }

    public String getAssetWidth() {
        return assetWidth;
    }

    public String getAssetHeight() {
        return assetHeight;
    }

    public String getExpandedWidth() {
        return expandedWidth;
    }

    public String getExpandedHeight() {
        return expandedHeight;
    }

    public String getApiFramework() {
        return apiFramework;
    }

    public String getAdSlotId() {
        return adSlotId;
    }

    public String getPxratio() {
        return pxratio;
    }

    public String getRenderingMode() {
        return renderingMode;
    }

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

    public AltText getAltText() {
        return altText;
    }

    public CompanionClickThrough getCompanionClickThrough() {
        return companionClickThrough;
    }

    public List<CompanionClickTracking> getCompanionClickTrackingList() {
        return companionClickTrackingList;
    }

    public TrackingEvents getTrackingEvents() {
        return trackingEvents;
    }
}
