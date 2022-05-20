package net.pubnative.lite.sdk.models;

public enum PositionX {
    LEFT("left"),
    RIGHT("right");

    PositionX(String value) {
        this.value = value;
    }

    private String value;

    public String getValue() {
        return value;
    }
}
