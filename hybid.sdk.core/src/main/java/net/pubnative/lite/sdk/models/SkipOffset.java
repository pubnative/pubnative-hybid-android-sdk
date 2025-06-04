// HyBid SDK License
//
// https://github.com/pubnative/pubnative-hybid-android-sdk/blob/main/LICENSE
//
package net.pubnative.lite.sdk.models;

public class SkipOffset {
    private final int offset;
    private final boolean isCustom;

    public SkipOffset(int offset, boolean isCustom) {
        this.offset = offset;
        this.isCustom = isCustom;
    }

    public int getOffset() {
        return offset;
    }

    public boolean isCustom() {
        return isCustom;
    }
}
