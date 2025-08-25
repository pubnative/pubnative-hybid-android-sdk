// HyBid SDK License
//
// https://github.com/pubnative/pubnative-hybid-android-sdk/blob/main/LICENSE
//
package net.pubnative.lite.sdk;

import net.pubnative.lite.sdk.models.IntegrationType;
import net.pubnative.lite.sdk.utils.sdkmanager.DisplayManager;

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
        displayManager = DisplayManager
                .builder()
                .setDisplayManagerName("HyBid")
                .build();
    }

    @After
    public void tearDown() {
        displayManager = null;
    }

    @Test
    public void testDisplayManagerName() {
        Assert.assertTrue(displayManager.getDisplayManagerName().equalsIgnoreCase("HyBid"));
    }

    @Test
    public void testDisplayManagerVersionStandalone() {
        String expectedVersion = "sdkandroid_s_" + BuildConfig.SDK_WRAPPER_VERSION;
        Assert.assertTrue(displayManager.getDisplayManagerVersion(IntegrationType.STANDALONE).equalsIgnoreCase(expectedVersion));
    }

    @Test
    public void testDisplayManagerVersionMediation() {
        String expectedVersion = "sdkandroid_m_" + BuildConfig.SDK_WRAPPER_VERSION;
        Assert.assertTrue(displayManager.getDisplayManagerVersion(IntegrationType.MEDIATION).equalsIgnoreCase(expectedVersion));
    }

    @Test
    public void testDisplayManagerVersionHeaderBidding() {
        String expectedVersion = "sdkandroid_hb_" + BuildConfig.SDK_WRAPPER_VERSION;
        Assert.assertTrue(displayManager.getDisplayManagerVersion(IntegrationType.HEADER_BIDDING).equalsIgnoreCase(expectedVersion));
    }

    @Test
    public void testDisplayManagerVersionInAppBidding() {
        String expectedVersion = "sdkandroid_b_" + BuildConfig.SDK_WRAPPER_VERSION;
        Assert.assertTrue(displayManager.getDisplayManagerVersion(IntegrationType.IN_APP_BIDDING).equalsIgnoreCase(expectedVersion));
    }

    @Test
    public void testDisplayManagerVersionNoIntegrationType() {
        String expectedVersion = "sdkandroid_b_" + BuildConfig.SDK_WRAPPER_VERSION;
        Assert.assertTrue(displayManager.getDisplayManagerVersion().equalsIgnoreCase(expectedVersion));
    }

    @Test
    public void testDisplayManagerVersionWithMediationVendor() {
        String mediationVendor = "lp";
        String expectedVersion = "sdkandroid_b" + "_" + mediationVendor + "_" + BuildConfig.SDK_WRAPPER_VERSION;
        Assert.assertTrue(displayManager.getDisplayManagerVersion(mediationVendor, IntegrationType.IN_APP_BIDDING).equalsIgnoreCase(expectedVersion));
    }

    @Test
    public void testDisplayManagerVersionWithWrappedFlag() {
        DisplayManager wrappedManager = DisplayManager
                .builder()
                .setDisplayManagerName("HyBid")
                .setIsWrapped(true)
                .build();

        String expectedVersion = "sdk_" + BuildConfig.SDK_WRAPPER_VERSION;
        Assert.assertTrue(wrappedManager.getDisplayManagerVersion().equalsIgnoreCase(expectedVersion));
    }

    @Test
    public void testDisplayManagerVersionWithWrappedFlagAndVersion() {
        String customVersion = "custom_version_1.0";
        DisplayManager wrappedManager = DisplayManager
                .builder()
                .setDisplayManagerName("HyBid")
                .setIsWrapped(true)
                .setDisplayManagerVersion(customVersion)
                .build();

        Assert.assertTrue(wrappedManager.getDisplayManagerVersion().equalsIgnoreCase(customVersion));
    }

    @Test
    public void testDisplayManagerBuilder() {
        DisplayManager customManager = DisplayManager
                .builder()
                .setDisplayManagerName("CustomSDK")
                .setDisplayManagerVersion("2.0.0")
                .setIsWrapped(true)
                .build();

        Assert.assertEquals("CustomSDK", customManager.getDisplayManagerName());
        Assert.assertEquals("2.0.0", customManager.getDisplayManagerVersion());
    }

    @Test
    public void testDisplayManagerVersionWithDifferentIntegrationAndMediation() {
        String mediationVendor = "lp";
        String expectedVersion = "sdkandroid_m" + "_" + mediationVendor + "_" + BuildConfig.SDK_WRAPPER_VERSION;
        Assert.assertTrue(displayManager.getDisplayManagerVersion(mediationVendor, IntegrationType.MEDIATION).equalsIgnoreCase(expectedVersion));
    }

    @Test
    public void testDisplayManagerVersionWithEmptyMediationVendor() {
        String mediationVendor = "";
        String expectedVersion = "sdkandroid_s_" + BuildConfig.SDK_WRAPPER_VERSION;
        Assert.assertTrue(displayManager.getDisplayManagerVersion(mediationVendor, IntegrationType.STANDALONE).equalsIgnoreCase(expectedVersion));
    }

    @Test
    public void testDisplayManagerVersionWithNullMediationVendor() {
        String expectedVersion = "sdkandroid_b_" + BuildConfig.SDK_WRAPPER_VERSION;
        Assert.assertTrue(displayManager.getDisplayManagerVersion(null, IntegrationType.IN_APP_BIDDING).equalsIgnoreCase(expectedVersion));
    }

    @Test
    public void testDisplayManagerWithNullName() {
        DisplayManager nullNameManager = DisplayManager
                .builder()
                .setDisplayManagerName(null)
                .build();
        
        Assert.assertNull(nullNameManager.getDisplayManagerName());
    }

    @Test
    public void testDisplayManagerBuilderWithoutWrappedFlag() {
        DisplayManager manager = DisplayManager
                .builder()
                .setDisplayManagerName("TestSDK")
                .setDisplayManagerVersion("3.0.0")
                .build();

        Assert.assertEquals("TestSDK", manager.getDisplayManagerName());

        // Should not be using the wrapped version format
        String expectedVersion = "sdkandroid_b_" + BuildConfig.SDK_WRAPPER_VERSION;
        Assert.assertEquals(expectedVersion, manager.getDisplayManagerVersion());
    }

    @Test
    public void testDisplayManagerVersionWithMediationVendorContainingSpecialChars() {
        String mediationVendor = "test-vendor_123";
        String expectedVersion = "sdkandroid_b_" + mediationVendor + "_" + BuildConfig.SDK_WRAPPER_VERSION;
        Assert.assertTrue(displayManager.getDisplayManagerVersion(mediationVendor, IntegrationType.IN_APP_BIDDING).equalsIgnoreCase(expectedVersion));
    }

    @Test
    public void testDisplayManagerVersionWithLongMediationVendorName() {
        String mediationVendor = "very_long_mediation_vendor_name_that_is_still_valid";
        String expectedVersion = "sdkandroid_b_" + mediationVendor + "_" + BuildConfig.SDK_WRAPPER_VERSION;
        Assert.assertTrue(displayManager.getDisplayManagerVersion(mediationVendor, IntegrationType.IN_APP_BIDDING).equalsIgnoreCase(expectedVersion));
    }

    @Test
    public void testDisplayManagerNameIsChangedAfterCreation() {
        DisplayManager manager = DisplayManager
                .builder()
                .setDisplayManagerName("InitialName")
                .build();

        Assert.assertEquals("InitialName", manager.getDisplayManagerName());

        // Create a new instance with different name
        DisplayManager updatedManager = DisplayManager
                .builder()
                .setDisplayManagerName("UpdatedName")
                .build();

        Assert.assertEquals("UpdatedName", updatedManager.getDisplayManagerName());
        Assert.assertNotEquals(manager.getDisplayManagerName(), updatedManager.getDisplayManagerName());
    }
}
