package net.pubnative.lite.sdk.tracking;

import java.io.IOException;

/**
 * Created by erosgarciaponte on 12.02.18.
 */

public enum Severity implements JsonStream.Streamable {
    ERROR("error"),
    WARNING("warning"),
    INFO("info");

    private final String name;

    Severity(String name) {
        this.name = name;
    }

    @Override
    public void toStream(JsonStream writer) throws IOException {
        writer.value(name);
    }

    static Severity fromString(String input) {
        switch (input) {
            case "error":
                return ERROR;
            case "warning":
                return WARNING;
            case "info":
                return INFO;
            default:
                return null;
        }
    }

    public String getName() {
        return name;
    }
}
