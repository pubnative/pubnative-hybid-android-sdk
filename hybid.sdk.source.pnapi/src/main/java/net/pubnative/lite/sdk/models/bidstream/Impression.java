// HyBid SDK License
//
// https://github.com/pubnative/pubnative-hybid-android-sdk/blob/main/LICENSE
//
package net.pubnative.lite.sdk.models.bidstream;

public class Impression extends Signal {
    @BidParam(name = "instl")
    public Integer isInterstitial = 0;
    @BidParam(name = "clickbrowser")
    public int clickBrowser = 1;

    public Impression() {
    }

    public Impression(Integer isInterstitial, int clickBrowser) {
        this.isInterstitial = isInterstitial;
        this.clickBrowser = clickBrowser;
    }
}
