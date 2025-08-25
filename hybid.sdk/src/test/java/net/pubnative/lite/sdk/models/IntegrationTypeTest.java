// HyBid SDK License
//
// https://github.com/pubnative/pubnative-hybid-android-sdk/blob/main/LICENSE
//
package net.pubnative.lite.sdk.models;

import static org.junit.Assert.assertEquals;
import org.junit.Test;

public class IntegrationTypeTest {

    @Test
    public void getCode_forHeaderBidding_returnsCorrectCode() {
        assertEquals("hb", IntegrationType.HEADER_BIDDING.getCode());
    }

    @Test
    public void getCode_forInAppBidding_returnsCorrectCode() {
        assertEquals("b", IntegrationType.IN_APP_BIDDING.getCode());
    }

    @Test
    public void getCode_forMediation_returnsCorrectCode() {
        assertEquals("m", IntegrationType.MEDIATION.getCode());
    }

    @Test
    public void getCode_forStandalone_returnsCorrectCode() {
        assertEquals("s", IntegrationType.STANDALONE.getCode());
    }
}
