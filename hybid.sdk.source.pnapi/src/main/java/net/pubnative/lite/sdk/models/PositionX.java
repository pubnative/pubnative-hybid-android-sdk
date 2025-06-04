// HyBid SDK License
//
// https://github.com/pubnative/pubnative-hybid-android-sdk/blob/main/LICENSE
//
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
