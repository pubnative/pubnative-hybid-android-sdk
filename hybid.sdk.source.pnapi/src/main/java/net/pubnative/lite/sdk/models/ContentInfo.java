package net.pubnative.lite.sdk.models;

import java.util.List;

public class ContentInfo {
    private final String iconUrl;
    private final String linkUrl;
    private final String text;
    private final int width;
    private final int height;
    private final PositionX positionX;
    private final PositionY positionY;
    private final List<String> viewTrackers;

    public ContentInfo(String iconUrl, String linkUrl, String text, List<String> viewTrackers) {
        this(iconUrl, linkUrl, text, -1, -1, PositionX.LEFT, PositionY.TOP, viewTrackers);
    }

    public ContentInfo(String iconUrl, String linkUrl, String text, PositionX positionX, PositionY positionY, List<String> viewTrackers) {
        this(iconUrl, linkUrl, text, -1, -1, positionX, positionY, viewTrackers);
    }

    public ContentInfo(String iconUrl, String linkUrl, String text, int width, int height, List<String> viewTrackers) {
        this(iconUrl, linkUrl, text, width, height, PositionX.LEFT, PositionY.TOP, viewTrackers);
    }

    public ContentInfo(String iconUrl, String linkUrl, String text, int width, int height, PositionX positionX, PositionY positionY, List<String> viewTrackers) {
        this.iconUrl = iconUrl;
        this.linkUrl = linkUrl;
        this.text = text;
        this.width = width;
        this.height = height;
        this.positionX = positionX;
        this.positionY = positionY;
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

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    public PositionX getPositionX() {
        return positionX;
    }

    public PositionY getPositionY() {
        return positionY;
    }
}
