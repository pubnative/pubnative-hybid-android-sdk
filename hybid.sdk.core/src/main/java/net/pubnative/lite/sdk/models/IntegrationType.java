package net.pubnative.lite.sdk.models;

public enum IntegrationType {
    HEADER_BIDDING("hb"),
    IN_APP_BIDDING("b"),
    MEDIATION("m"),
    STANDALONE("s");

    private final String code;

    IntegrationType(String code) {
        this.code = code;
    }

    public String getCode() {
        return code;
    }
}
