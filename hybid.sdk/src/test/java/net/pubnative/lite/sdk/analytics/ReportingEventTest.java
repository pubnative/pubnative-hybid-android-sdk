package net.pubnative.lite.sdk.analytics;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import android.os.Bundle;
import org.json.JSONArray;
import org.json.JSONObject;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;

@RunWith(RobolectricTestRunner.class)
public class ReportingEventTest {

    private ReportingEvent reportingEvent;

    @Before
    public void setUp() {
        reportingEvent = new ReportingEvent();
    }

    @Test
    public void testSettersAndGetters() {
        // Test a representative sample of specific setters/getters
        reportingEvent.setCampaignId("test_campaign");
        assertEquals("test_campaign", reportingEvent.getCampaignId());

        reportingEvent.setErrorCode(404);
        assertEquals(404L, reportingEvent.getErrorCode());

        reportingEvent.setHasEndCard(true);
        assertTrue(reportingEvent.getHasEndCard());
    }

    @Test
    public void setCustomString_withNullOrEmptyValue_shouldNotStoreValue() {
        // Test that TextUtils.isEmpty check works
        reportingEvent.setCustomString("test_key", "");
        assertNull(reportingEvent.getCustomString("test_key"));
        assertFalse(reportingEvent.getEventObject().has("test_key"));

        reportingEvent.setCustomString("test_key_2", null);
        assertNull(reportingEvent.getCustomString("test_key_2"));
        assertFalse(reportingEvent.getEventObject().has("test_key_2"));
    }

    @Test
    public void getCustomValue_whenKeyDoesNotExist_returnsNull() {
        assertNull(reportingEvent.getCustomString("non_existent_key"));
        assertNull(reportingEvent.getCustomInteger("non_existent_key"));
        assertNull(reportingEvent.getCustomDecimal("non_existent_key"));
        assertNull(reportingEvent.getCustomBoolean("non_existent_key"));
        assertNull(reportingEvent.getCustomJSONObject("non_existent_key"));
        assertNull(reportingEvent.getCustomJSONArray("non_existent_key"));
    }

    @Test
    public void mergeJSONObject_withValidSource_mergesKeysCorrectly() throws Exception {
        JSONObject source = new JSONObject();
        source.put(Reporting.Key.AD_FORMAT, Reporting.AdFormat.BANNER);
        source.put(Reporting.Key.AD_SIZE, "320x50");

        reportingEvent.mergeJSONObject(source);

        assertEquals(Reporting.AdFormat.BANNER, reportingEvent.getAdFormat());
        assertEquals("320x50", reportingEvent.getAdSize());
    }

    @Test
    public void mergeJSONObject_withNullSource_doesNotCrash() {
        int initialSize = reportingEvent.getEventObject().length();
        reportingEvent.mergeJSONObject(null);
        int finalSize = reportingEvent.getEventObject().length();

        assertEquals(initialSize, finalSize);
    }

    @Test
    public void getEventData_returnsBundleWithCorrectData() {
        reportingEvent.setEventType(Reporting.EventType.CLICK);
        reportingEvent.setZoneId("test-zone");

        Bundle bundle = reportingEvent.getEventData();

        assertNotNull(bundle);
        assertEquals(Reporting.EventType.CLICK, bundle.getString(Reporting.Key.EVENT_TYPE));
        assertEquals("test-zone", bundle.getString(Reporting.Key.ZONE_ID));
    }

    @Test
    public void testAllCustomDataTypes() throws Exception {
        // Test custom types to ensure they are stored and retrieved correctly.
        reportingEvent.setCustomInteger("custom_long", 123L);
        assertEquals(Long.valueOf(123L), reportingEvent.getCustomInteger("custom_long"));

        reportingEvent.setCustomDecimal("custom_double", 45.67);
        assertEquals(Double.valueOf(45.67), reportingEvent.getCustomDecimal("custom_double"));

        reportingEvent.setCustomBoolean("custom_boolean", true);
        assertEquals(Boolean.TRUE, reportingEvent.getCustomBoolean("custom_boolean"));

        JSONObject customJson = new JSONObject();
        customJson.put("inner_key", "inner_value");
        reportingEvent.setCustomJSONObject("custom_json", customJson);
        assertEquals("inner_value", reportingEvent.getCustomJSONObject("custom_json").getString("inner_key"));

        JSONArray customArray = new JSONArray();
        customArray.put("item1");
        reportingEvent.setCustomJSONArray("custom_array", customArray);
        assertEquals("item1", reportingEvent.getCustomJSONArray("custom_array").getString(0));
    }
}
