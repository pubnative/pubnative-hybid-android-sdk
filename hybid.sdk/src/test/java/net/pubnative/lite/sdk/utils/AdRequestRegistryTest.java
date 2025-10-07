// HyBid SDK License
//
// https://github.com/pubnative/pubnative-hybid-android-sdk/blob/main/LICENSE
//
package net.pubnative.lite.sdk.utils;

import org.junit.Test;
import static org.junit.Assert.*;

public class AdRequestRegistryTest {
    @Test
    public void testSingletonInstance_NotNull() {
        assertNotNull(AdRequestRegistry.getInstance());
    }

    @Test
    public void testSingletonInstance_SameInstance() {
        AdRequestRegistry instance1 = AdRequestRegistry.getInstance();
        AdRequestRegistry instance2 = AdRequestRegistry.getInstance();
        assertSame(instance1, instance2);
    }

    @Test
    public void testSetLastAdRequest_ThreeArgs() {
        AdRequestRegistry registry = AdRequestRegistry.getInstance();
        registry.setLastAdRequest("url1", "resp1", 123L);
        AdRequestRegistry.RequestItem item = registry.getLastAdRequest();
        assertEquals("url1", item.getUrl());
        assertNull(item.getPostParams());
        assertEquals("resp1", item.getResponse());
        assertEquals(123L, item.getLatency());
    }

    @Test
    public void testSetLastAdRequest_FourArgs() {
        AdRequestRegistry registry = AdRequestRegistry.getInstance();
        registry.setLastAdRequest("url2", "resp2", "post2", 456L);
        AdRequestRegistry.RequestItem item = registry.getLastAdRequest();
        assertEquals("url2", item.getUrl());
        assertEquals("post2", item.getPostParams());
        assertEquals("resp2", item.getResponse());
        assertEquals(456L, item.getLatency());
    }

    @Test
    public void testSetLastAdRequest_NullAndEmptyValues() {
        AdRequestRegistry registry = AdRequestRegistry.getInstance();
        registry.setLastAdRequest(null, null, null, -1L);
        AdRequestRegistry.RequestItem item = registry.getLastAdRequest();
        assertNull(item.getUrl());
        assertNull(item.getPostParams());
        assertNull(item.getResponse());
        assertEquals(-1L, item.getLatency());
    }

    @Test
    public void testRequestItem_ThreeArgConstructor() {
        AdRequestRegistry.RequestItem item = new AdRequestRegistry.RequestItem("url3", "resp3", 789L);
        assertEquals("url3", item.getUrl());
        assertNull(item.getPostParams());
        assertEquals("resp3", item.getResponse());
        assertEquals(789L, item.getLatency());
    }

    @Test
    public void testRequestItem_FourArgConstructor() {
        AdRequestRegistry.RequestItem item = new AdRequestRegistry.RequestItem("url4", "post4", "resp4", 1011L);
        assertEquals("url4", item.getUrl());
        assertEquals("post4", item.getPostParams());
        assertEquals("resp4", item.getResponse());
        assertEquals(1011L, item.getLatency());
    }

    @Test
    public void testRequestItem_ZeroLatency() {
        AdRequestRegistry.RequestItem item = new AdRequestRegistry.RequestItem("url5", "post5", "resp5", 0L);
        assertEquals(0L, item.getLatency());
    }
}
