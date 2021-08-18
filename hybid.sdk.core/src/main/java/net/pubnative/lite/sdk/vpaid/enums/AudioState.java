package net.pubnative.lite.sdk.vpaid.enums;

public enum AudioState {
    MUTED("muted"),
    ON("on"),
    DEFAULT("default");

    final String stateName;

    AudioState(String stateName) {
        this.stateName = stateName;
    }

    public String getStateName() {
        return stateName;
    }
}
