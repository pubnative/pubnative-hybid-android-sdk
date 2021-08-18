package net.pubnative.lite.sdk.vpaid.models.vast;

import net.pubnative.lite.sdk.vpaid.xml.Attribute;
import net.pubnative.lite.sdk.vpaid.xml.Text;

public class Mezzanine {
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

    public String getFileSize() {
        return fileSize;
    }

    public String getMediaType() {
        return mediaType;
    }

    public String getText() {
        return text;
    }
}
