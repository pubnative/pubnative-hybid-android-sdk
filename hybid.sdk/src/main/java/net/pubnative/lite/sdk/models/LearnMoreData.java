// HyBid SDK License
//
// https://github.com/pubnative/pubnative-hybid-android-sdk/blob/main/LICENSE
//

package net.pubnative.lite.sdk.models;

public class LearnMoreData {
    private final LearnMoreSize size;
    private final LearnMoreLocation location;

    public LearnMoreData(String sizeStr, String locationStr) {
        this.size = LearnMoreSize.fromString(sizeStr);
        this.location = LearnMoreLocation.fromString(locationStr);
    }

    public LearnMoreSize getSize() {
        return size;
    }

    public LearnMoreLocation getLocation() {
        return location;
    }
}

