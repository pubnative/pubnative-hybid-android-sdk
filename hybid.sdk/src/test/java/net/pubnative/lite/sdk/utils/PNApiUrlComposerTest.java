// HyBid SDK License
//
// https://github.com/pubnative/pubnative-hybid-android-sdk/blob/main/LICENSE
//
package net.pubnative.lite.sdk.utils;

import net.pubnative.lite.sdk.models.PNAdRequest;
import net.pubnative.lite.sdk.models.Topic;
import net.pubnative.lite.sdk.models.bidstream.BidParam;
import net.pubnative.lite.sdk.models.bidstream.Signal;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.Collections;
import java.util.Map;

import static org.junit.Assert.*;

@RunWith(RobolectricTestRunner.class)
public class PNApiUrlComposerTest {
    private static final String BASE_URL = "https://test.url";

    @Test
    public void testBuildUrl_withMinimalRequest() {
        PNAdRequest request = new PNAdRequest();
        String url = PNApiUrlComposer.buildUrl(BASE_URL, request);
        assertTrue(url.endsWith("/api/v3/native"));
        assertFalse(url.contains("?"));
    }

    @Test
    public void testBuildUrl_withAllFields() {
        PNAdRequest request = new PNAdRequest();
        request.appToken = "token";
        request.os = "android";
        request.osver = "12";
        request.devicemodel = "Pixel";
        request.make = "Google";
        request.deviceHeight = "1920";
        request.deviceWidth = "1080";
        request.orientation = "portrait";
        request.ppi = "400";
        request.pxratio = "2.0";
        request.js = "1";
        request.soundSetting = "on";
        request.dnt = "0";
        request.al = "en";
        request.width = "320";
        request.height = "50";
        request.mf = "mf";
        request.af = "af";
        request.zoneId = "zone";
        request.testMode = "1";
        request.locale = "en_US";
        request.language = "en";
        request.langb = "en";
        request.latitude = "52.5";
        request.longitude = "13.4";
        request.gender = "M";
        request.age = "30";
        request.bundleid = "com.test.app";
        request.keywords = "test,ad";
        request.coppa = "1";
        request.gid = "gid";
        request.gidmd5 = "md5";
        request.gidsha1 = "sha1";
        request.displaymanager = "dm";
        request.displaymanagerver = "1.0";
        request.omidpn = "pn";
        request.omidpv = "pv";
        request.rv = "rv";
        request.usprivacy = "usp";
        request.userconsent = "consent";
        request.gppstring = "gpp";
        request.gppsid = "sid";
        request.carrier = "carrier";
        request.connectiontype = "wifi";
        request.mccmnc = "mccmnc";
        request.mccmncsim = "mccmncsim";
        request.geofetch = "1";
        request.sua = "sua";
        request.ae = "ae";
        request.protocol = "2";
        request.api = "5";
        request.impdepth = "1";
        request.ageofapp = "100";
        request.sessionduration = "200";
        request.vg = "vg";
        request.hver = "hver";
        String url = PNApiUrlComposer.buildUrl(BASE_URL, request);
        assertTrue(url.contains("apptoken=token"));
        assertTrue(url.contains("os=android"));
        assertTrue(url.contains("hver=hver"));
        // ... more asserts can be added for each field
    }

    @Test
    public void testBuildUrl_withSignalAnnotatedField() {
        PNAdRequest request = new PNAdRequest();
        class TestSignal extends Signal {
            @BidParam(name = "testparam")
            public String value = "signalValue";
        }
        request.addSignal(new TestSignal());
        String url = PNApiUrlComposer.buildUrl(BASE_URL, request);
        assertTrue(url.contains("testparam=signalValue"));
    }

    @Test
    public void testBuildUrl_withSignalAnnotatedFieldEmpty() {
        PNAdRequest request = new PNAdRequest();
        class TestSignal extends Signal {
            @BidParam(name = "testparam")
            public String value = "";
        }
        request.addSignal(new TestSignal());
        String url = PNApiUrlComposer.buildUrl(BASE_URL, request);
        assertFalse(url.contains("testparam="));
    }

    @Test
    public void testBuildUrl_withSignalAnnotatedFieldNull() {
        PNAdRequest request = new PNAdRequest();
        class TestSignal extends Signal {
            @BidParam(name = "testparam")
            public String value = null;
        }
        request.addSignal(new TestSignal());
        String url = PNApiUrlComposer.buildUrl(BASE_URL, request);
        assertFalse(url.contains("testparam="));
    }

    @Test
    public void testBuildUrl_withSignalIterableField() {
        PNAdRequest request = new PNAdRequest();
        class TestSignal extends Signal {
            @BidParam(name = "listparam")
            public java.util.List<String> values = Arrays.asList("a", "b", "c");
        }
        request.addSignal(new TestSignal());
        String url = PNApiUrlComposer.buildUrl(BASE_URL, request);
        // Accept encoded value
        assertTrue(url.contains("listparam=a%2Cb%2Cc"));
    }

    @Test
    public void testBuildUrl_withSignalNoBidParam() {
        PNAdRequest request = new PNAdRequest();
        class TestSignal extends Signal {
            public String value = "shouldNotAppear";
        }
        request.addSignal(new TestSignal());
        String url = PNApiUrlComposer.buildUrl(BASE_URL, request);
        assertFalse(url.contains("shouldNotAppear"));
    }


    @Test
    public void testSignalFieldCache_CacheAndReuse() throws Exception {
        /**
         * Test that cache is reused across multiple requests with same Signal class
         */
        PNAdRequest request1 = new PNAdRequest();
        class TestSignal extends Signal {
            @BidParam(name = "field1")
            public String value1 = "test1";
        }
        request1.addSignal(new TestSignal());

        // First time it should build the cache
        String url1 = PNApiUrlComposer.buildUrl(BASE_URL, request1);
        assertTrue(url1.contains("field1=test1"));

        // Verify cache
        Field cacheField = PNApiUrlComposer.class.getDeclaredField("SIGNAL_FIELD_CACHE");
        cacheField.setAccessible(true);
        Map<Class<?>, Map<Field, BidParam>> cache = (Map<Class<?>, Map<Field, BidParam>>) cacheField.get(null);
        assertTrue("Cache should contain TestSignal.class", cache.containsKey(TestSignal.class));

        // Get the cached entry for this signal
        Map<Field, BidParam> cachedFields = cache.get(TestSignal.class);
        assertNotNull("Cached fields should not be null", cachedFields);
        assertFalse("Cached fields should not be empty", cachedFields.isEmpty());

        // Record cache size before second request
        int cacheSizeBeforeSecond = cache.size();

        // Second time it should get from cache
        PNAdRequest request2 = new PNAdRequest();
        request2.addSignal(new TestSignal());

        String url2 = PNApiUrlComposer.buildUrl(BASE_URL, request2);
        assertTrue(url2.contains("field1=test1"));

        // Verify cache size not changed
        int cacheSizeAfterSecond = cache.size();
        assertEquals("Cache size should remain same after second request with same Signal class", cacheSizeBeforeSecond, cacheSizeAfterSecond);

        // Verify same cached entry is used
        Map<Field, BidParam> cachedFieldsAfter = cache.get(TestSignal.class);
        assertEquals("Should reuse same cached entry", cachedFields, cachedFieldsAfter);
    }

    /**
     * Test that different Signal classes have independent cache entries
     */
    @Test
    public void testSignalFieldCache_MultipleDifferentSignals() {
        PNAdRequest request = new PNAdRequest();
        class SignalA extends Signal {
            @BidParam(name = "paramA")
            public String valueA = "testA";
        }

        class SignalB extends Signal {
            @BidParam(name = "paramB")
            public String valueB = "testB";
        }

        request.addSignal(new SignalA());
        request.addSignal(new SignalB());

        String url = PNApiUrlComposer.buildUrl(BASE_URL, request);
        assertTrue(url.contains("paramA=testA"));
        assertTrue(url.contains("paramB=testB"));
    }


    @Test
    public void testSignalFieldCache_MultipleRequestsSameSignal() {
        class TestSignal extends Signal {
            @BidParam(name = "param")
            public String v;
            TestSignal(String v) { this.v = v; }
        }

        PNAdRequest request1 = new PNAdRequest();
        request1.addSignal(new TestSignal("first"));
        String url1 = PNApiUrlComposer.buildUrl(BASE_URL, request1);
        assertTrue(url1.contains("param=first"));

        PNAdRequest request2 = new PNAdRequest();
        request2.addSignal(new TestSignal("second"));
        String url2 = PNApiUrlComposer.buildUrl(BASE_URL, request2);
        assertTrue(url2.contains("param=second"));
    }

    @Test
    public void testBuildUrl_withNoSignals() {
        PNAdRequest request = new PNAdRequest();
        String url = PNApiUrlComposer.buildUrl(BASE_URL, request);
        assertTrue(url.endsWith("/api/v3/native"));
        assertFalse(url.contains("?"));
    }

    @Test
    public void testBuildUrl_withNullTopics() {
        PNAdRequest request = new PNAdRequest();
        request.topics = null;
        String url = PNApiUrlComposer.buildUrl(BASE_URL, request);
        assertFalse(url.contains("psut="));
    }

    @Test
    public void testBuildUrl_withEmptyTopics() {
        PNAdRequest request = new PNAdRequest();
        request.topics = Collections.emptyList();
        String url = PNApiUrlComposer.buildUrl(BASE_URL, request);
        assertFalse(url.contains("psut="));
    }

    @Test
    public void testBuildUrl_withMultipleTopics() {
        PNAdRequest request = new PNAdRequest();
        Topic t1 = new Topic(1, 100L, "TaxonomyA");
        Topic t2 = new Topic(2, 100L, "TaxonomyA");
        Topic t3 = new Topic(3, 200L, "TaxonomyB");
        request.topics = Arrays.asList(t1, t2, t3);
        String url = PNApiUrlComposer.buildUrl(BASE_URL, request);
        // Accept either order, encoded
        boolean order1 = url.contains("psut=100%2CTaxonomyA%2C1%2C2_200%2CTaxonomyB%2C3");
        boolean order2 = url.contains("psut=200%2CTaxonomyB%2C3_100%2CTaxonomyA%2C1%2C2");
        assertTrue(order1 || order2);
    }

    @Test
    public void testBuildUrl_withVgAndHverEmpty() {
        PNAdRequest request = new PNAdRequest();
        request.vg = "";
        request.hver = "";
        String url = PNApiUrlComposer.buildUrl(BASE_URL, request);
        assertFalse(url.contains("vg="));
        assertFalse(url.contains("hver="));
    }

    @Test
    public void testGetUrlQuery() {
        PNAdRequest request = new PNAdRequest();
        request.appToken = "token";
        String query = PNApiUrlComposer.getUrlQuery(BASE_URL, request);
        assertTrue(query.contains("apptoken=token"));
    }
}
