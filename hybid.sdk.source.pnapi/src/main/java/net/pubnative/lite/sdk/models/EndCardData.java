package net.pubnative.lite.sdk.models;

public class EndCardData {
    public enum Type {
        STATIC_RESOURCE, IFRAME_RESOURCE, HTML_RESOURCE
    }

    private final String content;
    private final Type type;

    private final Boolean isCustom;

    public EndCardData(Type type, String content) {
        this.content = content;
        this.type = type;
        this.isCustom = false;
    }

    public EndCardData(Type type, String content, Boolean isCustom) {
        this.content = content;
        this.type = type;
        this.isCustom = isCustom;
    }

    public String getContent() {
        return content;
    }

    public Type getType() {
        return type;
    }

    public Boolean isCustom() { return isCustom; }
}
