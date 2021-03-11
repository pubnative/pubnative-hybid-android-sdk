package net.pubnative.lite.sdk.models;

import java.util.Locale;

public enum AdSize {
    SIZE_320x50(320, 50, "s"),
    SIZE_300x250(300, 250, "m"),
    SIZE_300x50(300, 50, "s"),
    SIZE_320x480(320, 480, "l"),
    SIZE_1024x768(1024, 768, "l"),
    SIZE_768x1024(768, 1024, "l"),
    SIZE_728x90(728, 90, "s"),
    SIZE_160x600(160, 600, "m"),
    SIZE_250x250(250, 250, "m"),
    SIZE_300x600(300, 600, "l"),
    SIZE_320x100(320, 100, "s"),
    SIZE_480x320(480, 320, "l"),
    SIZE_INTERSTITIAL(0, 0, "l");

    private final int width;
    private final int height;
    private final String adLayoutSize;

    AdSize(int width, int height, String adLayoutSize) {
        this.width = width;
        this.height = height;
        this.adLayoutSize = adLayoutSize;
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    public String getAdLayoutSize() {
        return adLayoutSize;
    }

    @Override
    public String toString() {
        return String.format(Locale.ENGLISH, "(%d x %d)", getWidth(), getHeight());
    }
}
