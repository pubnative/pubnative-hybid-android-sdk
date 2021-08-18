package net.pubnative.lite.sdk.vpaid.models.vast;

import net.pubnative.lite.sdk.vpaid.xml.Attribute;
import net.pubnative.lite.sdk.vpaid.xml.Tag;

import java.util.List;

public class Companion {
    @Attribute
    private int width;

    @Attribute
    private int height;

    @Attribute
    private String id;

    @Attribute
    private int assetWidth;

    @Attribute
    private int assetHeight;

    @Attribute
    private int expandedWidth;

    @Attribute
    private int expandedHeight;

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

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    public String getId() {
        return id;
    }

    public int getAssetWidth() {
        return assetWidth;
    }

    public int getAssetHeight() {
        return assetHeight;
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
