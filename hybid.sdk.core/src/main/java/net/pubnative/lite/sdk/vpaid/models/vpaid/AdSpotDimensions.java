package net.pubnative.lite.sdk.vpaid.models.vpaid;

public class AdSpotDimensions {

    public AdSpotDimensions(int width, int height) {
        this.width = width;
        this.height = height;
    }

    private int width;

    private int height;

    public int getWidth() {
        return width;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    public int getHeight() {
        return height;
    }

    public void setHeight(int height) {
        this.height = height;
    }
}
