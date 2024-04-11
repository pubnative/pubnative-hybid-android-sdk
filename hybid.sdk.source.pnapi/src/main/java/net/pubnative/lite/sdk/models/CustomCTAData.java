package net.pubnative.lite.sdk.models;

import android.graphics.Bitmap;

public class CustomCTAData {

    private final String iconURL;
    private final String label;
    private Bitmap bitmap;

    public CustomCTAData(String iconURL, String label) {
        this.iconURL = iconURL;
        this.label = label;
    }

    public String getIconURL() {
        return iconURL;
    }

    public String getLabel() {
        return label;
    }

    public Bitmap getBitmap() {
        return bitmap;
    }

    public void setBitmap(Bitmap bitmap) {
        this.bitmap = bitmap;
    }
}
