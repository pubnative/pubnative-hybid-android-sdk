// HyBid SDK License
//
// https://github.com/pubnative/pubnative-hybid-android-sdk/blob/main/LICENSE
//
package net.pubnative.lite.sdk.models.bidstream;

public class GeoLocation extends Signal {
    @BidParam(name = "accuracy")
    public Integer accuracy;
    @BidParam(name = "utcoffset")
    public Integer utcoffset;

    public GeoLocation() {
    }

    public GeoLocation(Integer accuracy, Integer utcoffset) {
        this.accuracy = accuracy;
        this.utcoffset = utcoffset;
    }
}
