package net.pubnative.lite.sdk.models;

import java.util.List;

public class ContentInfo {
    private final String iconUrl;
    private final String linkUrl;
    private final String text;
    private final List<String> viewTrackers;

    public ContentInfo(String iconUrl, String linkUrl, String text, List<String> viewTrackers) {
        this.iconUrl = iconUrl;
        this.linkUrl = linkUrl;
        this.text = text;
        this.viewTrackers = viewTrackers;
    }

    public String getIconUrl() {
        return iconUrl;
    }

    public String getLinkUrl() {
        return linkUrl;
    }

    public String getText() {
        return text;
    }

    public List<String> getViewTrackers() {
        return viewTrackers;
    }
}
