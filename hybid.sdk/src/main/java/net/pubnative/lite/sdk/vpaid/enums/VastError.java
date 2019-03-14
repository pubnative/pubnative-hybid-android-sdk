package net.pubnative.lite.sdk.vpaid.enums;

public enum VastError {
    XML_PARSING(100),
    TRAFFICKING(200),
    WRAPPER(300),
    WRAPPER_TIMEOUT(301),
    WRAPPER_LIMIT(302),
    WRAPPER_NO_VAST(303),
    FILE_NOT_FOUND(401),
    TIMEOUT(402),
    MEDIA_FILE_NO_SUPPORTED_TYPE(403),
    MEDIA_FILE_UNSUPPORTED(405),
    COMPANION(600),
    UNDEFINED(900),
    VPAID(901);

    private int value;

    VastError(int value) {
        this.value = value;
    }

    public String getValue() {
        return String.valueOf(value);
    }
}
