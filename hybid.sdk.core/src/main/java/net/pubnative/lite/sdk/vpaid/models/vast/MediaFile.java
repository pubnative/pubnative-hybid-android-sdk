package net.pubnative.lite.sdk.vpaid.models.vast;

import net.pubnative.lite.sdk.vpaid.xml.Attribute;
import net.pubnative.lite.sdk.vpaid.xml.Text;

public class MediaFile {

    /**
     * streaming, progressive
     */
    @Attribute
    private String delivery;

    @Attribute
    private String type;

    @Attribute
    private int width;

    @Attribute
    private int height;

    @Attribute
    private String codec;

    @Attribute
    private String id;

    @Attribute
    private int bitrate;

    @Attribute
    private int minBitrate;

    @Attribute
    private int maxBitrate;

    @Attribute
    private boolean scalable;

    @Attribute
    private boolean maintainAspectRatio;

    @Attribute
    private String apiFramework;

    @Attribute
    private long fileSize;

    @Attribute
    private String mediaType;

    @Text
    private String text;

    public String getDelivery() {
        return delivery;
    }

    public String getType() {
        return type;
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    public String getCodec() {
        return codec;
    }

    public String getId() {
        return id;
    }

    public int getBitrate() {
        return bitrate;
    }

    public int getMinBitrate() {
        return minBitrate;
    }

    public int getMaxBitrate() {
        return maxBitrate;
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

    public long getFileSize() {
        return fileSize;
    }

    public String getMediaType() {
        return mediaType;
    }

    public String getText() {
        return text;
    }
}
