package net.pubnative.lite.sdk.models;

public class CustomCTAData {

    private final String iconURL;
    private final String label;

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
}
