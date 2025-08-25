package net.pubnative.lite.sdk;

import net.pubnative.lite.sdk.models.IntegrationType;
import net.pubnative.lite.sdk.utils.sdkmanager.DisplayManager;
import net.pubnative.lite.sdk.utils.sdkmanager.SdkManager;
import net.pubnative.lite.sdk.vpaid.enums.AudioState;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.robolectric.RobolectricTestRunner;

import static org.mockito.Mockito.when;

@RunWith(RobolectricTestRunner.class)
public class HyBidTest {

    @Mock
    private SdkManager mockSdkManager;

    @Mock
    private DisplayManager mockDisplayManager;

    private AutoCloseable closeable;

    @Before
    public void setUp() {
        closeable = MockitoAnnotations.openMocks(this);
        when(mockSdkManager.getDisplayManager()).thenReturn(mockDisplayManager);
    }

    @After
    public void tearDown() throws Exception {
        closeable.close();
    }

    @Test
    public void testGetHyBidVersion() {
        String version = HyBid.getHyBidVersion();
        Assert.assertEquals(BuildConfig.SDK_VERSION, version);
    }

    @Test
    public void testSetAndGetAppToken() {
        // Save original token
        String originalToken = HyBid.getAppToken();

        try {
            // Test setting and getting app token
            String testToken = "test_token_123";
            HyBid.setAppToken(testToken);
            Assert.assertEquals(testToken, HyBid.getAppToken());
        } finally {
            // Restore original token
            HyBid.setAppToken(originalToken);
        }
    }

    @Test
    public void testSetAndGetCoppaEnabled() {
        // Save original value
        boolean originalValue = HyBid.isCoppaEnabled();

        try {
            // Test setting and getting COPPA
            HyBid.setCoppaEnabled(true);
            Assert.assertTrue(HyBid.isCoppaEnabled());

            HyBid.setCoppaEnabled(false);
            Assert.assertFalse(HyBid.isCoppaEnabled());
        } finally {
            // Restore original value
            HyBid.setCoppaEnabled(originalValue);
        }
    }

    @Test
    public void testSetAndGetTestMode() {
        // Save original value
        boolean originalValue = HyBid.isTestMode();

        try {
            // Test setting and getting test mode
            HyBid.setTestMode(true);
            Assert.assertTrue(HyBid.isTestMode());

            HyBid.setTestMode(false);
            Assert.assertFalse(HyBid.isTestMode());
        } finally {
            // Restore original value
            HyBid.setTestMode(originalValue);
        }
    }

    @Test
    public void testSetAndGetLocationSettings() {
        // Save original values
        boolean originalUpdates = HyBid.areLocationUpdatesEnabled();
        boolean originalTracking = HyBid.isLocationTrackingEnabled();

        try {
            // Test updates enabled setting
            HyBid.setLocationUpdatesEnabled(true);
            Assert.assertTrue(HyBid.areLocationUpdatesEnabled());

            HyBid.setLocationUpdatesEnabled(false);
            Assert.assertFalse(HyBid.areLocationUpdatesEnabled());

            // Test tracking enabled setting
            HyBid.setLocationTrackingEnabled(true);
            Assert.assertTrue(HyBid.isLocationTrackingEnabled());

            HyBid.setLocationTrackingEnabled(false);
            Assert.assertFalse(HyBid.isLocationTrackingEnabled());
        } finally {
            // Restore original values
            HyBid.setLocationUpdatesEnabled(originalUpdates);
            HyBid.setLocationTrackingEnabled(originalTracking);
        }
    }

    @Test
    public void testSetAndGetUserData() {
        // Save original values
        String originalAge = HyBid.getAge();
        String originalGender = HyBid.getGender();
        String originalKeywords = HyBid.getKeywords();

        try {
            // Test age
            String testAge = "25";
            HyBid.setAge(testAge);
            Assert.assertEquals(testAge, HyBid.getAge());

            // Test gender
            String testGender = "m";
            HyBid.setGender(testGender);
            Assert.assertEquals(testGender, HyBid.getGender());

            // Test keywords
            String testKeywords = "sports,news,gaming";
            HyBid.setKeywords(testKeywords);
            Assert.assertEquals(testKeywords, HyBid.getKeywords());
        } finally {
            // Restore original values
            HyBid.setAge(originalAge);
            HyBid.setGender(originalGender);
            HyBid.setKeywords(originalKeywords);
        }
    }

    @Test
    public void testSetAndGetReportingEnabled() {
        // Save original value
        Boolean originalValue = HyBid.isReportingEnabled();

        try {
            // Test setting and getting reporting enabled
            HyBid.setReportingEnabled(true);
            Assert.assertTrue(HyBid.isReportingEnabled());

            HyBid.setReportingEnabled(false);
            Assert.assertFalse(HyBid.isReportingEnabled());
        } finally {
            // Restore original value
            HyBid.setReportingEnabled(originalValue);
        }
    }

    @Test
    public void testSetAndGetDiagnosticsEnabled() {
        // Save original value
        Boolean originalValue = HyBid.isDiagnosticsEnabled();

        try {
            // Test setting and getting diagnostics enabled
            HyBid.setDiagnosticsEnabled(true);
            Assert.assertTrue(HyBid.isDiagnosticsEnabled());

            HyBid.setDiagnosticsEnabled(false);
            Assert.assertFalse(HyBid.isDiagnosticsEnabled());
        } finally {
            // Restore original value
            HyBid.setDiagnosticsEnabled(originalValue);
        }
    }

    @Test
    public void testSetAndGetTopicsApiEnabled() {
        // Save original value
        Boolean originalValue = HyBid.isTopicsApiEnabled();

        try {
            // Test setting and getting Topics API enabled
            HyBid.setTopicsApiEnabled(true);
            Assert.assertTrue(HyBid.isTopicsApiEnabled());

            HyBid.setTopicsApiEnabled(false);
            Assert.assertFalse(HyBid.isTopicsApiEnabled());
        } finally {
            // Restore original value
            HyBid.setTopicsApiEnabled(originalValue);
        }
    }

    @Test
    public void testSetAndGetAtomStarted() {
        // Save original value
        Boolean originalValue = HyBid.isAtomStarted();

        try {
            // Test setting and getting atom started
            HyBid.setAtomStarted(true);
            Assert.assertTrue(HyBid.isAtomStarted());

            HyBid.setAtomStarted(false);
            Assert.assertFalse(HyBid.isAtomStarted());
        } finally {
            // Restore original value
            HyBid.setAtomStarted(originalValue);
        }
    }

    @Test
    public void testCustomContentSettings() {
        // Save original values
        String originalIabCategory = HyBid.getIabCategory();
        String originalIabSubcategory = HyBid.getsIabSubcategory();
        String originalAppVersion = HyBid.getAppVersion();
        String originalDeveloperDomain = HyBid.getDeveloperDomain();
        String originalContentAgeRating = HyBid.getContentAgeRating();

        try {
            // Test IAB category
            String testCategory = "IAB1";
            HyBid.setIabCategory(testCategory);
            Assert.assertEquals(testCategory, HyBid.getIabCategory());

            // Test IAB subcategory
            String testSubcategory = "IAB1-1";
            HyBid.setIabSubcategory(testSubcategory);
            Assert.assertEquals(testSubcategory, HyBid.getsIabSubcategory());

            // Test app version
            String testAppVersion = "1.2.3";
            HyBid.setAppVersion(testAppVersion);
            Assert.assertEquals(testAppVersion, HyBid.getAppVersion());

            // Test developer domain
            String testDomain = "example.com";
            HyBid.setDeveloperDomain(testDomain);
            Assert.assertEquals(testDomain, HyBid.getDeveloperDomain());

            // Test content age rating
            String testRating = "12+";
            HyBid.setContentAgeRating(testRating);
            Assert.assertEquals(testRating, HyBid.getContentAgeRating());
        } finally {
            // Restore original values
            HyBid.setIabCategory(originalIabCategory);
            HyBid.setIabSubcategory(originalIabSubcategory);
            HyBid.setAppVersion(originalAppVersion);
            HyBid.setDeveloperDomain(originalDeveloperDomain);
            HyBid.setContentAgeRating(originalContentAgeRating);
        }
    }

    @Test
    public void testGetVideoAudioStatus() {
        // Save original value
        AudioState originalState = HyBid.getVideoAudioStatus();

        try {
            // Test setting and getting video audio status
            HyBid.setVideoAudioStatus(AudioState.MUTED);
            Assert.assertEquals(AudioState.MUTED, HyBid.getVideoAudioStatus());

            HyBid.setVideoAudioStatus(AudioState.ON);
            Assert.assertEquals(AudioState.ON, HyBid.getVideoAudioStatus());
        } finally {
            // Restore original value
            HyBid.setVideoAudioStatus(originalState);
        }
    }

    @Test
    public void testSetAndGetSkipXmlResource() {
        // Save original value
        Integer originalResource = HyBid.getSkipXmlResource();

        try {
            // Test setting and getting skip XML resource
            Integer testResource = 12345;
            HyBid.setSkipXmlResource(testResource);
            Assert.assertEquals(testResource, HyBid.getSkipXmlResource());
        } finally {
            // Restore original value
            HyBid.setSkipXmlResource(originalResource);
        }
    }

    @Test
    public void testSetAndGetCloseXmlResources() {
        // Save original values
        Integer originalNormalResource = HyBid.getNormalCloseXmlResource();
        Integer originalPressedResource = HyBid.getPressedCloseXmlResource();

        try {
            // Test setting and getting close XML resources
            Integer testNormalResource = 12345;
            Integer testPressedResource = 67890;
            HyBid.setCloseXmlResource(testNormalResource, testPressedResource);

            Assert.assertEquals(testNormalResource, HyBid.getNormalCloseXmlResource());
            Assert.assertEquals(testPressedResource, HyBid.getPressedCloseXmlResource());
        } finally {
            // Restore original values
            HyBid.setCloseXmlResource(originalNormalResource, originalPressedResource);
        }
    }

    @Test
    public void testGetCustomRequestSignalDataWithNullParams() {
        String signalData = HyBid.getCustomRequestSignalData();
        // We can only verify it doesn't throw an exception and returns something non-null
        Assert.assertNotNull(signalData);
    }

    @Test
    public void testGetSDKVersionInfo_withSdkManager() {
        // Setup
        String expectedVersion = "sdkandroid_b_" + BuildConfig.SDK_WRAPPER_VERSION;
        when(mockDisplayManager.getDisplayManagerVersion(IntegrationType.IN_APP_BIDDING))
                .thenReturn(expectedVersion);

        // Save the original SdkManager
        SdkManager originalSdkManager = HyBid.getSdkManager();

        try {
            // Set our mock SdkManager
            HyBid.setSdkManager(mockSdkManager);

            // Test
            String versionInfo = HyBid.getSDKVersionInfo();

            // Verify
            Assert.assertEquals(expectedVersion, versionInfo);
        } finally {
            // Restore original SdkManager to avoid affecting other tests
            HyBid.setSdkManager(originalSdkManager);
        }
    }

    @Test
    public void testGetSDKVersionInfoWithIntegrationType_withSdkManager() {
        // Setup
        String expectedVersion = "sdkandroid_m_" + BuildConfig.SDK_WRAPPER_VERSION;
        when(mockDisplayManager.getDisplayManagerVersion(IntegrationType.MEDIATION))
                .thenReturn(expectedVersion);

        // Save the original SdkManager
        SdkManager originalSdkManager = HyBid.getSdkManager();

        try {
            // Set our mock SdkManager
            HyBid.setSdkManager(mockSdkManager);

            // Test
            String versionInfo = HyBid.getSDKVersionInfo(IntegrationType.MEDIATION);

            // Verify
            Assert.assertEquals(expectedVersion, versionInfo);
        } finally {
            // Restore original SdkManager to avoid affecting other tests
            HyBid.setSdkManager(originalSdkManager);
        }
    }

    @Test
    public void testGetSDKVersionInfo_withoutSdkManager() {
        // Save the original SdkManager
        SdkManager originalSdkManager = HyBid.getSdkManager();

        try {
            // Set SdkManager to null
            HyBid.setSdkManager(null);

            // Test
            String versionInfo = HyBid.getSDKVersionInfo();

            // Verify - should use the default DisplayManager
            String expectedVersion = "sdkandroid_b_" + BuildConfig.SDK_WRAPPER_VERSION;
            Assert.assertTrue(versionInfo.contains("sdkandroid_b_"));
        } finally {
            // Restore original SdkManager
            HyBid.setSdkManager(originalSdkManager);
        }
    }

    @Test
    public void testGetSDKVersionInfo_withNullIntegrationType() {
        // Setup
        String expectedVersion = "sdkandroid_b_" + BuildConfig.SDK_WRAPPER_VERSION;
        when(mockDisplayManager.getDisplayManagerVersion(IntegrationType.IN_APP_BIDDING))
                .thenReturn(expectedVersion);

        // Save the original SdkManager
        SdkManager originalSdkManager = HyBid.getSdkManager();

        try {
            // Set our mock SdkManager
            HyBid.setSdkManager(mockSdkManager);

            // Test with null integration type
            String versionInfo = HyBid.getSDKVersionInfo(null);

            // Verify - should default to IN_APP_BIDDING
            Assert.assertEquals(expectedVersion, versionInfo);
        } finally {
            // Restore original SdkManager
            HyBid.setSdkManager(originalSdkManager);
        }
    }

    @Test
    public void testGetSDKVersionInfoWithMediationVendor() {
        // Setup
        String mediationVendor = "testVendor";
        String expectedVersion = "sdkandroid_m_" + BuildConfig.SDK_WRAPPER_VERSION;
        when(mockDisplayManager.getDisplayManagerVersion(mediationVendor, IntegrationType.MEDIATION))
                .thenReturn(expectedVersion);

        // Save the original SdkManager
        SdkManager originalSdkManager = HyBid.getSdkManager();

        try {
            // Set our mock SdkManager
            HyBid.setSdkManager(mockSdkManager);

            // Test
            String versionInfo = HyBid.getSDKVersionInfo(IntegrationType.MEDIATION);

            // Verify
            Assert.assertEquals(expectedVersion, versionInfo);
        } finally {
            // Restore original SdkManager
            HyBid.setSdkManager(originalSdkManager);
        }
    }

    @Test
    public void testGetSDKVersionInfoWithNullMediationVendor() {
        // Setup
        String expectedVersion = "sdkandroid_hb_" + BuildConfig.SDK_WRAPPER_VERSION;
        when(mockDisplayManager.getDisplayManagerVersion(IntegrationType.HEADER_BIDDING))
                .thenReturn(expectedVersion);

        // Save the original SdkManager
        SdkManager originalSdkManager = HyBid.getSdkManager();

        try {
            // Set our mock SdkManager
            HyBid.setSdkManager(mockSdkManager);

            // Test with null mediation vendor
            String versionInfo = HyBid.getSDKVersionInfo(IntegrationType.HEADER_BIDDING);

            // Verify
            Assert.assertEquals(expectedVersion, versionInfo);
        } finally {
            // Restore original SdkManager
            HyBid.setSdkManager(originalSdkManager);
        }
    }

    @Test
    public void testGetSDKVersionInfo_withoutSdkManager_withIntegrationType() {
        // Save the original SdkManager
        SdkManager originalSdkManager = HyBid.getSdkManager();

        try {
            // Set SdkManager to null
            HyBid.setSdkManager(null);

            // Test
            String versionInfo = HyBid.getSDKVersionInfo(IntegrationType.STANDALONE);

            // Verify - should use the default DisplayManager with STANDALONE integration type
            Assert.assertTrue(versionInfo.contains("sdkandroid_s_"));
        } finally {
            // Restore original SdkManager
            HyBid.setSdkManager(originalSdkManager);
        }
    }

    @Test
    public void testGetSDKVersionInfo_withoutSdkManager_withMediationVendor() {
        // Save the original SdkManager
        SdkManager originalSdkManager = HyBid.getSdkManager();

        try {
            // Set SdkManager to null
            HyBid.setSdkManager(null);

            // Test
            String mediationVendor = "testMediation";
            String versionInfo = HyBid.getSDKVersionInfo(IntegrationType.MEDIATION);

            // Verify - should use the default DisplayManager with MEDIATION integration type
            Assert.assertTrue(!versionInfo.contains("sdkandroid_m_" + mediationVendor));
        } finally {
            // Restore original SdkManager
            HyBid.setSdkManager(originalSdkManager);
        }
    }

    @Test
    public void testGetSDKVersionInfo_withEmptyMediationVendor() {
        // Setup
        String expectedVersion = "sdkandroid_b_" + BuildConfig.SDK_WRAPPER_VERSION;
        when(mockDisplayManager.getDisplayManagerVersion("", IntegrationType.IN_APP_BIDDING))
                .thenReturn(expectedVersion);

        // Save the original SdkManager
        SdkManager originalSdkManager = HyBid.getSdkManager();

        try {
            // Set our mock SdkManager
            HyBid.setSdkManager(mockSdkManager);

            // Test with empty mediation vendor
            String versionInfo = HyBid.getSDKVersionInfo(IntegrationType.IN_APP_BIDDING);

            // Verify
            Assert.assertEquals(expectedVersion, versionInfo);
        } finally {
            // Restore original SdkManager
            HyBid.setSdkManager(originalSdkManager);
        }
    }
}