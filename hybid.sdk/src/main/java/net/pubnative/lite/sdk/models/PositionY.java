// HyBid SDK License
//
// https://github.com/pubnative/pubnative-hybid-android-sdk/blob/main/LICENSE
//
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
