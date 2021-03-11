package net.pubnative.lite.sdk.auction;

public class AuctionError {
    private final String adSourceName;
    private final Throwable error;

    public AuctionError(String adSourceName, Throwable error) {
        this.adSourceName = adSourceName;
        this.error = error;
    }

    public String getAdSourceName() {
        return adSourceName;
    }

    public Throwable getError() {
        return error;
    }
}
