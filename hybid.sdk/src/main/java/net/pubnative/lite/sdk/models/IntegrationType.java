package net.pubnative.lite.sdk.models;

public enum IntegrationType {
    HEADER_BIDDING("hb"),
    MEDIATION("m"),
    STANDALONE("s");

    private String code;

    private IntegrationType(String code) {
        this.code = code;
    }

    public String getCode() {
        return code;
    }
}
