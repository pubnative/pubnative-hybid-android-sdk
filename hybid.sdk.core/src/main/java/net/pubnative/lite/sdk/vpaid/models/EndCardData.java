package net.pubnative.lite.sdk.vpaid.models;

public class EndCardData {
    public enum Type {
        STATIC_RESOURCE, IFRAME_RESOURCE, HTML_RESOURCE
    }

    private final String content;
    private final Type type;

    public EndCardData(Type type, String content) {
        this.content = content;
        this.type = type;
    }

    public String getContent() {
        return content;
    }

    public Type getType() {
        return type;
    }
}
