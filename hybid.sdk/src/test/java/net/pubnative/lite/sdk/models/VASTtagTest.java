// HyBid SDK License
//
// https://github.com/pubnative/pubnative-hybid-android-sdk/blob/main/LICENSE
//
package net.pubnative.lite.sdk.models;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;

@RunWith(RobolectricTestRunner.class)
public class VASTtagTest {

    @Test
    public void build_withAllParameters_replacesAllPlaceholders() {
        String urlTemplate = "https://example.com?adid={{adid}}&bundle={{bundle}}&dnt={{dnt}}" +
                "&lat={{lat}}&lon={{lon}}&ua={{user_agent}}&w={{width}}&h={{height}}" +
                "&gdpr={{gdpr}}&consent={{gdpr_consent}}&privacy={{us_privacy}}";

        VASTtag vastTag = new VASTtag.VASTtagBuilder(urlTemplate)
                .adId("test-ad-id")
                .bundle("com.test.app")
                .connection("wifi") // This macro isn't in the template, but we test the builder
                .dnt("1")
                .lat("34.05")
                .lon("-118.24")
                .userAgent("Test-User-Agent")
                .width("320")
                .height("50")
                .gdpr("1")
                .gdprConsent("test-consent-string")
                .usPrivacy("1YNY")
                .build();

        assertNotNull(vastTag);
        String expectedUrl = "https://example.com?adid=test-ad-id&bundle=com.test.app&dnt=1" +
                "&lat=34.05&lon=-118.24&ua=Test-User-Agent&w=320&h=50" +
                "&gdpr=1&consent=test-consent-string&privacy=1YNY";
        assertEquals(expectedUrl, vastTag.getFormattedURL());
    }

    @Test
    public void build_withPartialParameters_replacesOnlyAvailablePlaceholders() {
        String urlTemplate = "https://example.com?adid={{adid}}&bundle={{bundle}}&dnt={{dnt}}";

        VASTtag vastTag = new VASTtag.VASTtagBuilder(urlTemplate)
                .adId("test-ad-id")
                .dnt("0")
                .build();

        assertNotNull(vastTag);
        String expectedUrl = "https://example.com?adid=test-ad-id&bundle={{bundle}}&dnt=0";
        assertEquals(expectedUrl, vastTag.getFormattedURL());
    }

    @Test
    public void build_withEmptyOrNullParameters_doesNotReplacePlaceholders() {
        String urlTemplate = "https://example.com?adid={{adid}}&bundle={{bundle}}&dnt={{dnt}}";

        VASTtag vastTag = new VASTtag.VASTtagBuilder(urlTemplate)
                .adId(null)
                .bundle("")
                .dnt("1")
                .build();

        assertNotNull(vastTag);
        String expectedUrl = "https://example.com?adid={{adid}}&bundle={{bundle}}&dnt=1";
        assertEquals(expectedUrl, vastTag.getFormattedURL());
    }
}