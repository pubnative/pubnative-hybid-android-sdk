package net.pubnative.lite.sdk;

import net.pubnative.lite.sdk.core.BuildConfig;
import net.pubnative.lite.sdk.models.IntegrationType;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;

@RunWith(RobolectricTestRunner.class)
public class DisplayManagerTest {
    private DisplayManager displayManager;

    @Before
    public void setUp() {
        displayManager = new DisplayManager();
    }

    @After
    public void tearDown() {
        displayManager = null;
    }

    @Test
    public void testDisplayManagerName() {
        Assert.assertTrue(displayManager.getDisplayManager().equalsIgnoreCase("HyBid"));
    }

    @Test
    public void testDisplayManagerVersionStandalone() {
        String expectedVersion = "sdkandroid_s_" + BuildConfig.SDK_VERSION;
        Assert.assertTrue(displayManager.getDisplayManagerVersion(IntegrationType.STANDALONE).equalsIgnoreCase(expectedVersion));
    }

    @Test
    public void testDisplayManagerVersionMediation() {
        String expectedVersion = "sdkandroid_m_" + BuildConfig.SDK_VERSION;
        Assert.assertTrue(displayManager.getDisplayManagerVersion(IntegrationType.MEDIATION).equalsIgnoreCase(expectedVersion));
    }

    @Test
    public void testDisplayManagerVersionHeaderBidding() {
        String expectedVersion = "sdkandroid_hb_" + BuildConfig.SDK_VERSION;
        Assert.assertTrue(displayManager.getDisplayManagerVersion(IntegrationType.HEADER_BIDDING).equalsIgnoreCase(expectedVersion));
    }

    @Test
    public void testDisplayManagerVersionInAppBidding() {
        String expectedVersion = "sdkandroid_b_" + BuildConfig.SDK_VERSION;
        Assert.assertTrue(displayManager.getDisplayManagerVersion(IntegrationType.IN_APP_BIDDING).equalsIgnoreCase(expectedVersion));
    }

    @Test
    public void testDisplayManagerVersionNoIntegrationType() {
        String expectedVersion = "sdkandroid_b_" + BuildConfig.SDK_VERSION;
        Assert.assertTrue(displayManager.getDisplayManagerVersion().equalsIgnoreCase(expectedVersion));
    }
}
