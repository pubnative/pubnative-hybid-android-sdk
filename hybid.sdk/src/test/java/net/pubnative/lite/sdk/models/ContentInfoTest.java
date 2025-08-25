// HyBid SDK License
//
// https://github.com/pubnative/pubnative-hybid-android-sdk/blob/main/LICENSE
//
package net.pubnative.lite.sdk.models;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import java.util.List;
import org.junit.Test;

public class ContentInfoTest {

    @Test
    public void mainConstructor_withAllParameters_assignsAllFieldsCorrectly() {
        String iconUrl = "https://example.com/icon.png";
        String linkUrl = "https://example.com/link";
        String text = "Test Ad";
        int width = 100;
        int height = 50;
        PositionX posX = PositionX.RIGHT;
        PositionY posY = PositionY.BOTTOM;
        List<String> viewTrackers = List.of("view_tracker_1");
        List<String> clickTrackers = List.of("click_tracker_1");

        ContentInfo contentInfo = new ContentInfo(iconUrl, linkUrl, text, width, height, posX, posY, viewTrackers, clickTrackers);

        assertEquals(iconUrl, contentInfo.getIconUrl());
        assertEquals(linkUrl, contentInfo.getLinkUrl());
        assertEquals(text, contentInfo.getText());
        assertEquals(width, contentInfo.getWidth());
        assertEquals(height, contentInfo.getHeight());
        assertEquals(posX, contentInfo.getPositionX());
        assertEquals(posY, contentInfo.getPositionY());
        assertEquals(viewTrackers, contentInfo.getViewTrackers());
        assertEquals(clickTrackers, contentInfo.getClickTrackers());
    }

    @Test
    public void convenienceConstructorOne_assignsFieldsAndDefaultsCorrectly() {
        String iconUrl = "icon_url";
        String linkUrl = "link_url";
        String text = "some_text";
        List<String> viewTrackers = List.of("view_tracker_2");

        ContentInfo contentInfo = new ContentInfo(iconUrl, linkUrl, text, viewTrackers);

        // Assert provided values
        assertEquals(iconUrl, contentInfo.getIconUrl());
        assertEquals(linkUrl, contentInfo.getLinkUrl());
        assertEquals(text, contentInfo.getText());
        assertEquals(viewTrackers, contentInfo.getViewTrackers());

        // Assert default values
        assertEquals(-1, contentInfo.getWidth());
        assertEquals(-1, contentInfo.getHeight());
        assertEquals(PositionX.LEFT, contentInfo.getPositionX());
        assertEquals(PositionY.TOP, contentInfo.getPositionY());
        assertNull(contentInfo.getClickTrackers());
    }

    @Test
    public void convenienceConstructorTwo_assignsFieldsAndDefaultsCorrectly() {
        String iconUrl = "icon_url";
        String linkUrl = "link_url";
        String text = "some_text";
        PositionX posX = PositionX.RIGHT;
        PositionY posY = PositionY.BOTTOM;
        List<String> viewTrackers = List.of("view_tracker_3");

        ContentInfo contentInfo = new ContentInfo(iconUrl, linkUrl, text, posX, posY, viewTrackers);

        // Assert provided values
        assertEquals(iconUrl, contentInfo.getIconUrl());
        assertEquals(posX, contentInfo.getPositionX());
        assertEquals(posY, contentInfo.getPositionY());

        // Assert default values
        assertEquals(-1, contentInfo.getWidth());
        assertEquals(-1, contentInfo.getHeight());
        assertNull(contentInfo.getClickTrackers());
    }

    @Test
    public void convenienceConstructorThree_assignsFieldsAndDefaultsCorrectly() {
        String iconUrl = "icon_url";
        String linkUrl = "link_url";
        String text = "some_text";
        int width = 200;
        int height = 100;
        List<String> viewTrackers = List.of("view_tracker_4");

        ContentInfo contentInfo = new ContentInfo(iconUrl, linkUrl, text, width, height, viewTrackers);

        // Assert provided values
        assertEquals(iconUrl, contentInfo.getIconUrl());
        assertEquals(width, contentInfo.getWidth());
        assertEquals(height, contentInfo.getHeight());

        // Assert default values
        assertEquals(PositionX.LEFT, contentInfo.getPositionX());
        assertEquals(PositionY.TOP, contentInfo.getPositionY());
        assertNull(contentInfo.getClickTrackers());
    }
}
