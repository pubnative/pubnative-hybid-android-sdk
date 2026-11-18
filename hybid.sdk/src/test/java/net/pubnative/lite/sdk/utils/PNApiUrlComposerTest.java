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
    @SuppressWarnings("deprecation")
    public void testBuildUrl_withLegacyTopicsSet_doesNotEmitPsut() {
        // Topics API was removed. Even if the deprecated topics field is populated directly,
        // the composer must never transmit the psut (topics) query parameter.
        PNAdRequest request = new PNAdRequest();
        request.topics = Arrays.asList(
                new Topic(1, 100L, "TaxonomyA"),
                new Topic(2, 200L, "TaxonomyB"));
        String url = PNApiUrlComposer.buildUrl(BASE_URL, request);
        assertFalse(url.contains("psut="));
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
        request.deviceType = "4";
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
        assertTrue(url.contains("devicetype=4"));
        assertTrue(url.contains("hver=hver"));
        // ... more asserts can be added for each field
    }

    @Test
    public void testBuildUrl_goldenUrlForFullyPopulatedRequest() {
        PNAdRequest request = new PNAdRequest();
        request.appToken = "v_apptoken";
        request.os = "v_os";
        request.osver = "v_osver";
        request.devicemodel = "v_devicemodel";
        request.make = "v_make";
        request.deviceHeight = "v_dh";
        request.deviceWidth = "v_dw";
        request.deviceType = "v_devicetype";
        request.orientation = "v_scro";
        request.ppi = "v_ppi";
        request.pxratio = "v_pxratio";
        request.js = "v_js";
        request.soundSetting = "v_aud";
        request.dnt = "v_dnt";
        request.al = "v_al";
        request.width = "v_w";
        request.height = "v_h";
        request.mf = "v_mf";
        request.af = "v_af";
        request.zoneId = "v_zoneid";
        request.testMode = "v_test";
        request.locale = "v_locale";
        request.language = "v_language";
        request.langb = "v_langb";
        request.latitude = "v_lat";
        request.longitude = "v_long";
        request.gender = "v_gender";
        request.age = "v_age";
        request.bundleid = "v_bundleid";
        request.keywords = "v_keywords";
        request.coppa = "v_coppa";
        request.gid = "v_gid";
        request.gidmd5 = "v_gidmd5";
        request.gidsha1 = "v_gidsha1";
        request.displaymanager = "v_displaymanager";
        request.displaymanagerver = "v_displaymanagerver";
        request.omidpn = "v_omidpn";
        request.omidpv = "v_omidpv";
        request.rv = "v_rv";
        request.usprivacy = "v_usprivacy";
        request.userconsent = "v_userconsent";
        request.gppstring = "v_gpp";
        request.gppsid = "v_gppsid";
        request.carrier = "v_carrier";
        request.connectiontype = "v_connectiontype";
        request.mccmnc = "v_mccmnc";
        request.mccmncsim = "v_mccmncsim";
        request.geofetch = "v_geofetch";
        request.sua = "v_sua";
        request.ae = "v_ae";
        request.protocol = "v_protocol";
        request.api = "v_api";
        request.impdepth = "v_impdepth";
        request.ageofapp = "v_ageofapp";
        request.sessionduration = "v_sessionduration";
        request.mediationAdUnitId = "v_mediationadunitid";
        request.vg = "v_vg";
        request.hver = "v_hver";
        String expected = BASE_URL + "/api/v3/native?"
                + "apptoken=v_apptoken&os=v_os&osver=v_osver&devicemodel=v_devicemodel&make=v_make&dh=v_dh&dw=v_dw&devicetype=v_devicetype&scro=v_scro&ppi=v_ppi&pxratio=v_pxratio&js=v_js&aud=v_aud&dnt=v_dnt&al=v_al&w=v_w&h=v_h&mf=v_mf&af=v_af&zoneid=v_zoneid&test=v_test&locale=v_locale&language=v_language&langb=v_langb&lat=v_lat&long=v_long&gender=v_gender&age=v_age&bundleid=v_bundleid&keywords=v_keywords&coppa=v_coppa&gid=v_gid&gidmd5=v_gidmd5&gidsha1=v_gidsha1&displaymanager=v_displaymanager&displaymanagerver=v_displaymanagerver&omidpn=v_omidpn&omidpv=v_omidpv&rv=v_rv&usprivacy=v_usprivacy&userconsent=v_userconsent&gpp=v_gpp&gppsid=v_gppsid&carrier=v_carrier&connectiontype=v_connectiontype&mccmnc=v_mccmnc&mccmncsim=v_mccmncsim&geofetch=v_geofetch&sua=v_sua&ae=v_ae&protocol=v_protocol&api=v_api&impdepth=v_impdepth&ageofapp=v_ageofapp&sessionduration=v_sessionduration&mediationadunitid=v_mediationadunitid&vg=v_vg&hver=v_hver";
        assertEquals(expected, PNApiUrlComposer.buildUrl(BASE_URL, request));
    }

    @Test
    public void testBuildUrl_skipsNullAndEmptyFields() {
        PNAdRequest request = new PNAdRequest();
        request.appToken = "";
        request.os = null;
        request.hver = "hver";
        assertEquals(BASE_URL + "/api/v3/native?hver=hver", PNApiUrlComposer.buildUrl(BASE_URL, request));
    }

    @Test
    public void testBuildUrl_everyPublicStringFieldOfRequestIsSent() throws Exception {
        PNAdRequest request = new PNAdRequest();
        java.util.List<Field> fields = new java.util.ArrayList<>();
        for (Field field : PNAdRequest.class.getFields()) {
            if (field.getType() == String.class && !java.lang.reflect.Modifier.isStatic(field.getModifiers())) {
                field.set(request, "x_" + field.getName());
                fields.add(field);
            }
        }
        String query = PNApiUrlComposer.getUrlQuery(BASE_URL, request);
        java.util.List<String> sentValues = new java.util.ArrayList<>();
        for (String pair : query.split("&")) {
            sentValues.add(pair.substring(pair.indexOf('=') + 1));
        }
        // Exact value match: "x_gid" must not be satisfied by "x_gidmd5", and each field must be sent exactly once.
        for (Field field : fields) {
            assertEquals("PNAdRequest." + field.getName() + " must be sent exactly once",
                    1, java.util.Collections.frequency(sentValues, "x_" + field.getName()));
        }
        assertEquals("Unexpected extra params in query", fields.size(), sentValues.size());
    }

    @Test
    public void testBuildUrl_goldenUrlWithSignalSitsBetweenMediationAdUnitIdAndVg() {
        PNAdRequest request = new PNAdRequest();
        request.mediationAdUnitId = "v_mediationadunitid";
        request.vg = "v_vg";
        request.hver = "v_hver";
        class TestSignal extends Signal {
            @BidParam(name = "testparam")
            public String value = "signalValue";
        }
        request.addSignal(new TestSignal());
        assertEquals(BASE_URL + "/api/v3/native?mediationadunitid=v_mediationadunitid&testparam=signalValue&vg=v_vg&hver=v_hver",
                PNApiUrlComposer.buildUrl(BASE_URL, request));
    }

    @Test
    public void testBuildUrl_withEmptyDeviceType() {
        PNAdRequest request = new PNAdRequest();
        request.deviceType = "";
        String url = PNApiUrlComposer.buildUrl(BASE_URL, request);
        assertFalse(url.contains("devicetype="));
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

    @Test
    public void testBuildUrl_withMediationAdUnitId() {
        PNAdRequest request = new PNAdRequest();
        request.mediationAdUnitId = "max_ad_unit_123";
        String url = PNApiUrlComposer.buildUrl(BASE_URL, request);
        assertTrue(url.contains("mediationadunitid=max_ad_unit_123"));
    }

    @Test
    public void testBuildUrl_withMediationAdUnitIdNull() {
        PNAdRequest request = new PNAdRequest();
        request.mediationAdUnitId = null;
        String url = PNApiUrlComposer.buildUrl(BASE_URL, request);
        assertFalse(url.contains("mediationadunitid"));
    }

    @Test
    public void testBuildUrl_withMediationAdUnitIdEmpty() {
        PNAdRequest request = new PNAdRequest();
        request.mediationAdUnitId = "";
        String url = PNApiUrlComposer.buildUrl(BASE_URL, request);
        assertFalse(url.contains("mediationadunitid"));
    }

    @Test
    public void testBuildUrl_withMediationAdUnitIdAndZoneId() {
        // zoneid is a separate identifier and must not be replaced by the mediation ad unit ID.
        PNAdRequest request = new PNAdRequest();
        request.zoneId = "zone";
        request.mediationAdUnitId = "max_ad_unit_123";
        String url = PNApiUrlComposer.buildUrl(BASE_URL, request);
        assertTrue(url.contains("zoneid=zone"));
        assertTrue(url.contains("mediationadunitid=max_ad_unit_123"));
    }

    @Test
    public void testBuildUrl_withMediationAdUnitIdIsNotNamedGpid() {
        // What is sent is a mediation ad unit ID, not a publisher declared GPID.
        PNAdRequest request = new PNAdRequest();
        request.mediationAdUnitId = "max_ad_unit_123";
        String url = PNApiUrlComposer.buildUrl(BASE_URL, request);
        assertFalse(url.contains("gpid="));
        assertFalse(url.contains("tagid="));
    }

    @Test
    public void testBuildUrl_withMediationAdUnitIdReservedCharacters() {
        // buildUrl percent encodes reserved characters, so the parameter cannot be split apart.
        PNAdRequest request = new PNAdRequest();
        request.mediationAdUnitId = "a&b=c";
        String url = PNApiUrlComposer.buildUrl(BASE_URL, request);
        assertTrue(url.contains("mediationadunitid=a%26b%3Dc"));
    }

    @Test
    public void testGetUrlQuery_withMediationAdUnitId() {
        // getUrlQuery is what the mediation signal actually carries.
        PNAdRequest request = new PNAdRequest();
        request.mediationAdUnitId = "max_ad_unit_123";
        String query = PNApiUrlComposer.getUrlQuery(BASE_URL, request);
        assertTrue(query.contains("mediationadunitid=max_ad_unit_123"));
    }

    @Test
    public void testGetUrlQuery_withMediationAdUnitIdNull() {
        PNAdRequest request = new PNAdRequest();
        request.appToken = "token";
        request.mediationAdUnitId = null;
        String query = PNApiUrlComposer.getUrlQuery(BASE_URL, request);
        assertFalse(query.contains("mediationadunitid"));
    }
}
