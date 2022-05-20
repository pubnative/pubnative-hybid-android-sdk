package net.pubnative.lite.sdk.models;

public enum PositionY {
    TOP("top"),
    BOTTOM("bottom");

    PositionY(String value) {
        this.value = value;
    }

    private String value;

    public String getValue() {
        return value;
    }
}
