package net.pubnative.lite.sdk.models.bidstream;

public class Impression extends Signal {
    @BidParam(name = "instl")
    public int isInterstitial = 0;
    @BidParam(name = "clickbrowser")
    public int clickBrowser = 1;

    public Impression() {
    }

    public Impression(int isInterstitial, int clickBrowser) {
        this.isInterstitial = isInterstitial;
        this.clickBrowser = clickBrowser;
    }
}
