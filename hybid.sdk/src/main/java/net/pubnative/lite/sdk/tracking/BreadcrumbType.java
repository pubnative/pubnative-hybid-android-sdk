package net.pubnative.lite.sdk.tracking;

/**
 * Created by erosgarciaponte on 12.02.18.
 */

public enum BreadcrumbType {
    ERROR("error"),
    LOG("log"),
    MANUAL("manual"),
    NAVIGATION("navigation"),
    PROCESS("process"),
    REQUEST("request"),
    STATE("state"),
    USER("user");

    private final String type;

    BreadcrumbType(String type) {
        this.type = type;
    }

    @Override
    public String toString() {
        return type;
    }
}
