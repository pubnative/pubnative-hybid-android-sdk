// HyBid SDK License
//
// https://github.com/pubnative/pubnative-hybid-android-sdk/blob/main/LICENSE
//
package net.pubnative.lite.sdk.vpaid.models.vpaid;

public class TrackingEvent {
    public final String url;
    public String name;
    public int timeMillis;

    public TrackingEvent(String url) {
        this.url = url;
    }

}
