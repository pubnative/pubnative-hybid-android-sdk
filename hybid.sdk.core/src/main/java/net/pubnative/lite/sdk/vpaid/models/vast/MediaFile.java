package net.pubnative.lite.sdk.vpaid.models.vast;

import net.pubnative.lite.sdk.vpaid.xml.Attribute;
import net.pubnative.lite.sdk.vpaid.xml.Text;

public class MediaFile {

    @Attribute
    private String id;

    /**
     * streaming, progressive
     */
    @Attribute
    private String delivery;

    @Attribute
    private String type;

    @Attribute
    private int bitrate;

    @Attribute
    private int width;

    @Attribute
    private int height;

    @Attribute
    private boolean scalable;

    @Attribute
    private boolean maintainAspectRatio;

    @Attribute
    private String apiFramework;

    @Text
    private String text;

    public String getId() {
        return id;
    }

    public String getDelivery() {
        return delivery;
    }

    public String getType() {
        return type;
    }

    public int getBitrate() {
        return bitrate;
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
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

    public String getText() {
        return text;
    }
}
