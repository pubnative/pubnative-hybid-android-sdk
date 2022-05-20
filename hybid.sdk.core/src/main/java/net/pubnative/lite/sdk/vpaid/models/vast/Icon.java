package net.pubnative.lite.sdk.vpaid.models.vast;

import net.pubnative.lite.sdk.vpaid.xml.Attribute;
import net.pubnative.lite.sdk.vpaid.xml.Tag;

import java.util.List;

public class Icon {
    @Attribute
    private String program;

    @Attribute
    private String width;

    @Attribute
    private String height;

    @Attribute
    private String xPosition;

    @Attribute
    private String yPosition;

    @Attribute
    private String duration;

    @Attribute
    private String offset;

    @Attribute
    private String apiFramework;

    @Attribute
    private String pxratio;

    @Attribute
    private String altText;

    @Attribute
    private String hoverText;

    @Tag("StaticResource")
    private List<StaticResource> staticResources;

    @Tag("IFrameResource")
    private List<IFrameResource> iFrameResources;

    @Tag("HTMLResource")
    private List<HTMLResource> htmlResources;

    @Tag
    private IconClicks iconClicks;

    @Tag("IconViewTracking")
    private List<IconViewTracking> iconViewTrackingList;

    public String getProgram() {
        return program;
    }

    public String getWidth() {
        return width;
    }

    public String getHeight() {
        return height;
    }

    public String getXPosition() {
        return xPosition;
    }

    public String getYPosition() {
        return yPosition;
    }

    public String getDuration() {
        return duration;
    }

    public String getOffset() {
        return offset;
    }

    public String getApiFramework() {
        return apiFramework;
    }

    public String getPxratio() {
        return pxratio;
    }

    public String getAltText() {
        return altText;
    }

    public String getHoverText() {
        return hoverText;
    }

    public List<StaticResource> getStaticResources() {
        return staticResources;
    }

    public List<IFrameResource> getIFrameResources() {
        return iFrameResources;
    }

    public List<HTMLResource> getHtmlResources() {
        return htmlResources;
    }

    public IconClicks getIconClicks() {
        return iconClicks;
    }

    public List<IconViewTracking> getIconViewTrackingList() {
        return iconViewTrackingList;
    }
}
