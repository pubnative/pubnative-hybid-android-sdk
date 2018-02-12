package net.pubnative.lite.sdk.tracking;

import java.io.IOException;

/**
 * Created by erosgarciaponte on 12.02.18.
 */

public class Notifier implements JsonStream.Streamable {
    static final String NOTIFIER_NAME = "PNLite Notifier";
    static final String NOTIFIER_VERSION = "0.1.5";
    static final String NOTIFIER_URL = "https://pubnative.net";
    private String name;
    private String version;
    private String url;

    private static final Notifier instance = new Notifier();

    public static Notifier getInstance() {
        return instance;
    }

    Notifier() {
        this.name = NOTIFIER_NAME;
        this.version = NOTIFIER_VERSION;
        this.url = NOTIFIER_URL;
    }

    @Override
    public void toStream(JsonStream writer) throws IOException {
        writer.beginObject();
        writer.name("name").value(name);
        writer.name("version").value(version);
        writer.name("url").value(url);
        writer.endObject();
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public void setURL(String url) {
        this.url = url;
    }

    public void setName(String name) {
        this.name = name;
    }
}
