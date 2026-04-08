// HyBid SDK License
//
// https://github.com/pubnative/pubnative-hybid-android-sdk/blob/main/LICENSE
//
package net.pubnative.lite.sdk.vpaid;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

import net.pubnative.lite.sdk.utils.Logger;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.MockedStatic;
import org.mockito.junit.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class VideoAdCacheTest {

    @Test
    public void testConstructor() {
        VideoAdCache cache = new VideoAdCache();
        assertNull(cache.inspect("nonexistent"));
        assertNull(cache.inspectLatest());
    }

    @Test
    public void testPutAndInspect() {
        VideoAdCache cache = new VideoAdCache();
        VideoAdCacheItem item = mock(VideoAdCacheItem.class);
        try (MockedStatic<Logger> loggerMock = mockStatic(Logger.class)) {
            cache.put("zone1", item);
            loggerMock.verify(() -> Logger.d(anyString(), eq("VideoAdCache putting video for key: zone1")));
            assertEquals(item, cache.inspect("zone1"));
            assertEquals(item, cache.inspectLatest());
        }
    }

    @Test
    public void testPutMultiple() {
        VideoAdCache cache = new VideoAdCache();
        VideoAdCacheItem item1 = mock(VideoAdCacheItem.class);
        VideoAdCacheItem item2 = mock(VideoAdCacheItem.class);
        try (MockedStatic<Logger> loggerMock = mockStatic(Logger.class)) {
            cache.put("zone1", item1);
            cache.put("zone2", item2);
            assertEquals(item1, cache.inspect("zone1"));
            assertEquals(item2, cache.inspect("zone2"));
            assertEquals(item2, cache.inspectLatest());
        }
    }

    @Test
    public void testInspectNonExistent() {
        VideoAdCache cache = new VideoAdCache();
        assertNull(cache.inspect("nonexistent"));
    }

    @Test
    public void testInspectLatestEmpty() {
        VideoAdCache cache = new VideoAdCache();
        assertNull(cache.inspectLatest());
    }

    @Test
    public void testRemoveExisting() {
        VideoAdCache cache = new VideoAdCache();
        VideoAdCacheItem item = mock(VideoAdCacheItem.class);
        try (MockedStatic<Logger> loggerMock = mockStatic(Logger.class)) {
            cache.put("zone1", item);
            VideoAdCacheItem removed = cache.remove("zone1");
            assertEquals(item, removed);
            assertNull(cache.inspect("zone1"));
            assertEquals(item, cache.inspectLatest());
        }
    }

    @Test
    public void testRemoveNonExistent() {
        VideoAdCache cache = new VideoAdCache();
        assertNull(cache.remove("nonexistent"));
    }

    @Test
    public void testRemoveLatest() {
        VideoAdCache cache = new VideoAdCache();
        VideoAdCacheItem item = mock(VideoAdCacheItem.class);
        try (MockedStatic<Logger> loggerMock = mockStatic(Logger.class)) {
            cache.put("zone1", item);
            VideoAdCacheItem removed = cache.remove("latestZoneId");
            assertEquals(item, removed);
            assertNull(cache.inspectLatest());
            assertEquals(item, cache.inspect("zone1"));
        }
    }
}
