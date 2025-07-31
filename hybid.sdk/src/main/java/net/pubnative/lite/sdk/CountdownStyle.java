// HyBid SDK License
//
// https://github.com/pubnative/pubnative-hybid-android-sdk/blob/main/LICENSE
//
package net.pubnative.lite.sdk;

public enum CountdownStyle {

    PIE_CHART("net.pubnative.lite.sdk.countdown.pie_chart"),
    TIMER("net.pubnative.lite.sdk.countdown.timer"),
    PROGRESS("net.pubnative.lite.sdk.countdown.progress");

    public static CountdownStyle from(String style) {

        if (PIE_CHART.getId().equals(style)) {
            return PIE_CHART;
        } else if (TIMER.getId().equals(style)) {
            return TIMER;
        } else if (PROGRESS.getId().equals(style)) {
            return PROGRESS;
        }

        return PIE_CHART;
    }

    private final String mId;

    CountdownStyle(String id) {
        mId = id;
    }

    public String getId() {
        return mId;
    }
}
