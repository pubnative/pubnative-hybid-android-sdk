package net.pubnative.lite.sdk.vast.model;

public class Tracking {
    private String value;
    private TRACKING_EVENTS_TYPE event;

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public TRACKING_EVENTS_TYPE getEvent() {
        return event;
    }

    public void setEvent(TRACKING_EVENTS_TYPE event) {
        this.event = event;
    }

    @Override
    public String toString() {
        return "Tracking [event=" + event + ", value=" + value + "]";
    }
}
