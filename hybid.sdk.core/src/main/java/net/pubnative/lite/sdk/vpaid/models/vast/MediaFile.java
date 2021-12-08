package net.pubnative.lite.sdk.vpaid.models.vast;

import android.text.TextUtils;

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
    private String width;

    @Attribute
    private String height;

    @Attribute
    private String codec;

    @Attribute
    private String id;

    @Attribute
    private String bitrate;

    @Attribute
    private String minBitrate;

    @Attribute
    private String maxBitrate;

    @Attribute
    private String scalable;

    @Attribute
    private String maintainAspectRatio;

    @Attribute
    private String apiFramework;

    @Attribute
    private String fileSize;

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

    public String getWidth() {
        return width;
    }

    public String getHeight() {
        return height;
    }

    public String getCodec() {
        return codec;
    }

    public String getId() {
        return id;
    }

    public String getBitrate() {
        return bitrate;
    }

    public String getMinBitrate() {
        return minBitrate;
    }

    public String getMaxBitrate() {
        return maxBitrate;
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

    public String getFileSize() {
        return fileSize;
    }

    public String getMediaType() {
        return mediaType;
    }

    public String getText() {
        return TextUtils.isEmpty(text) ? "" : text.trim();
    }
}
