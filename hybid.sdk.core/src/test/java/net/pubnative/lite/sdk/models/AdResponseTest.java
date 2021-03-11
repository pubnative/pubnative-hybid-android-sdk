package net.pubnative.lite.sdk.models;

import net.pubnative.lite.sdk.testing.TestUtil;

import org.json.JSONObject;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;

/**
 * Created by erosgarciaponte on 24.01.18.
 */
@RunWith(RobolectricTestRunner.class)
public class AdResponseTest {
    private AdResponse mSubject;

    @Before
    public void setup() {
        mSubject = TestUtil.createTestAdResponse();
    }

    @Test
    public void validateAdResponse() throws Exception {
        JSONObject json = mSubject.toJson();

        AdResponse parseResponse = new AdResponse(json);

        Assert.assertEquals("ok", parseResponse.status);
        Assert.assertEquals(1, parseResponse.ads.size());
    }
}
