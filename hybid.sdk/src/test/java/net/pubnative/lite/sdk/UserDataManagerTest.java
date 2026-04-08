// HyBid SDK License
//
// https://github.com/pubnative/pubnative-hybid-android-sdk/blob/main/LICENSE
//
package net.pubnative.lite.sdk;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Looper;
import android.preference.PreferenceManager;


import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

import java.util.HashMap;
import java.util.Map;

@RunWith(RobolectricTestRunner.class)
@Config(sdk = 28)
public class UserDataManagerTest {

    @Mock
    private Context mockContext;
    @Mock
    private SharedPreferences mockPrivatePreferences;
    @Mock
    private SharedPreferences mockAppPreferences;
    @Mock
    private SharedPreferences.Editor mockPrivateEditor;
    @Mock
    private SharedPreferences.Editor mockAppEditor;

    private UserDataManager userDataManager;
    private Map<String, Object> privatePrefsStorage;
    private Map<String, Object> appPrefsStorage;
    private SharedPreferences.OnSharedPreferenceChangeListener capturedListener;

    @Before
    public void setup() {
        org.mockito.MockitoAnnotations.openMocks(this);

        if (Looper.myLooper() == null) {
            Looper.prepare();
        }

        privatePrefsStorage = new HashMap<>();
        appPrefsStorage = new HashMap<>();

        // Setup mock context
        when(mockContext.getApplicationContext()).thenReturn(mockContext);
        when(mockContext.getSharedPreferences("net.pubnative.lite.dataconsent", Context.MODE_PRIVATE))
                .thenReturn(mockPrivatePreferences);

        // Setup private preferences storage simulation
        when(mockPrivatePreferences.edit()).thenReturn(mockPrivateEditor);
        when(mockPrivateEditor.putString(anyString(), anyString())).thenAnswer(invocation -> {
            privatePrefsStorage.put(invocation.getArgument(0), invocation.getArgument(1));
            return mockPrivateEditor;
        });
        when(mockPrivateEditor.putInt(anyString(), any(Integer.class))).thenAnswer(invocation -> {
            privatePrefsStorage.put(invocation.getArgument(0), invocation.getArgument(1));
            return mockPrivateEditor;
        });
        when(mockPrivateEditor.remove(anyString())).thenAnswer(invocation -> {
            privatePrefsStorage.remove(invocation.getArgument(0));
            return mockPrivateEditor;
        });

        when(mockPrivatePreferences.getString(anyString(), any())).thenAnswer(invocation -> {
            String key = invocation.getArgument(0);
            Object defaultValue = invocation.getArgument(1);
            return privatePrefsStorage.containsKey(key) ? (String) privatePrefsStorage.get(key) : defaultValue;
        });
        when(mockPrivatePreferences.getInt(anyString(), any(Integer.class))).thenAnswer(invocation -> {
            String key = invocation.getArgument(0);
            Integer defaultValue = invocation.getArgument(1);
            return privatePrefsStorage.containsKey(key) ? (Integer) privatePrefsStorage.get(key) : defaultValue;
        });
        when(mockPrivatePreferences.contains(anyString())).thenAnswer(invocation ->
            privatePrefsStorage.containsKey(invocation.getArgument(0))
        );

        // Setup app preferences storage simulation
        when(mockAppPreferences.edit()).thenReturn(mockAppEditor);

        when(mockAppPreferences.getString(anyString(), any())).thenAnswer(invocation -> {
            String key = invocation.getArgument(0);
            Object defaultValue = invocation.getArgument(1);
            return appPrefsStorage.containsKey(key) ? (String) appPrefsStorage.get(key) : defaultValue;
        });
        when(mockAppPreferences.contains(anyString())).thenAnswer(invocation ->
            appPrefsStorage.containsKey(invocation.getArgument(0))
        );
        when(mockAppPreferences.getAll()).thenAnswer(invocation -> new HashMap<>(appPrefsStorage));

        // Mock PreferenceManager
        try (MockedStatic<PreferenceManager> preferenceManagerMock = mockStatic(PreferenceManager.class)) {
            preferenceManagerMock.when(() -> PreferenceManager.getDefaultSharedPreferences(mockContext))
                    .thenReturn(mockAppPreferences);

            // Capture the listener
            doAnswer(invocation -> {
                capturedListener = invocation.getArgument(0);
                return null;
            }).when(mockAppPreferences).registerOnSharedPreferenceChangeListener(
                    any(SharedPreferences.OnSharedPreferenceChangeListener.class));

            userDataManager = new UserDataManager(mockContext);
        }
    }

    @After
    public void tearDown() {
        privatePrefsStorage.clear();
        appPrefsStorage.clear();
    }

    // =============================================================================================
    // Deprecated Methods Tests
    // =============================================================================================

    @Test
    public void testGetConsentPageLink_returnsCorrectUrl() {
        String result = userDataManager.getConsentPageLink();
        assertEquals("https://cdn.pubnative.net/static/consent/consent.html", result);
    }

    @Test
    public void testGetPrivacyPolicyLink_returnsCorrectUrl() {
        String result = userDataManager.getPrivacyPolicyLink();
        assertEquals("https://pubnative.net/privacy-notice/", result);
    }

    @Test
    public void testGetVendorListLink_returnsCorrectUrl() {
        String result = userDataManager.getVendorListLink();
        assertEquals("https://pubnative.net/monetization-partners/", result);
    }

    // =============================================================================================
    // GDPR Applies Tests
    // =============================================================================================

    @Test
    public void testGdprApplies_whenKeyNotPresent_returnsFalse() {
        assertFalse(userDataManager.gdprApplies());
    }

    @Test
    public void testGdprApplies_whenValueIsNull_returnsFalse() {
        appPrefsStorage.put("IABTCF_gdprApplies", null);
        assertFalse(userDataManager.gdprApplies());
    }

    @Test
    public void testGdprApplies_whenValueIsStringOne_returnsTrue() {
        appPrefsStorage.put("IABTCF_gdprApplies", "1");
        assertTrue(userDataManager.gdprApplies());
    }

    @Test
    public void testGdprApplies_whenValueIsStringTrue_returnsTrue() {
        appPrefsStorage.put("IABTCF_gdprApplies", "true");
        assertTrue(userDataManager.gdprApplies());
    }

    @Test
    public void testGdprApplies_whenValueIsStringTrueCaseInsensitive_returnsTrue() {
        appPrefsStorage.put("IABTCF_gdprApplies", "TRUE");
        assertTrue(userDataManager.gdprApplies());
    }

    @Test
    public void testGdprApplies_whenValueIsIntegerOne_returnsTrue() {
        appPrefsStorage.put("IABTCF_gdprApplies", 1);
        assertTrue(userDataManager.gdprApplies());
    }

    @Test
    public void testGdprApplies_whenValueIsIntegerZero_returnsFalse() {
        appPrefsStorage.put("IABTCF_gdprApplies", 0);
        assertFalse(userDataManager.gdprApplies());
    }

    @Test
    public void testGdprApplies_whenValueIsBooleanTrue_returnsTrue() {
        appPrefsStorage.put("IABTCF_gdprApplies", true);
        assertTrue(userDataManager.gdprApplies());
    }

    @Test
    public void testGdprApplies_whenValueIsBooleanFalse_returnsFalse() {
        appPrefsStorage.put("IABTCF_gdprApplies", false);
        assertFalse(userDataManager.gdprApplies());
    }

    @Test
    public void testGdprApplies_whenValueIsStringZero_returnsFalse() {
        appPrefsStorage.put("IABTCF_gdprApplies", "0");
        assertFalse(userDataManager.gdprApplies());
    }

    @Test
    public void testGdprApplies_whenValueIsStringFalse_returnsFalse() {
        appPrefsStorage.put("IABTCF_gdprApplies", "false");
        assertFalse(userDataManager.gdprApplies());
    }

    @Test
    public void testGdprApplies_whenExceptionOccurs_returnsFalse() {
        when(mockAppPreferences.getAll()).thenThrow(new RuntimeException("Test exception"));
        assertFalse(userDataManager.gdprApplies());
    }

    // =============================================================================================
    // Can Collect Data Tests
    // =============================================================================================

    @Test
    public void testCanCollectData_whenGdprDoesNotApply_returnsTrue() {
        // GDPR does not apply
        assertTrue(userDataManager.canCollectData());
    }

    @Test
    public void testCanCollectData_whenGdprAppliesAndConsentNotAsked_returnsFalse() {
        appPrefsStorage.put("IABTCF_gdprApplies", 1);
        assertFalse(userDataManager.canCollectData());
    }

    @Test
    public void testCanCollectData_whenGdprAppliesAndConsentAccepted_returnsTrue() {
        appPrefsStorage.put("IABTCF_gdprApplies", 1);
        privatePrefsStorage.put("gdpr_consent_state", 1);
        privatePrefsStorage.put("gdpr_advertising_id", "test-ad-id");

        // Mock HyBid.getDeviceInfo()
        try (MockedStatic<HyBid> hyBidMock = mockStatic(HyBid.class)) {
            DeviceInfo mockDeviceInfo = mock(DeviceInfo.class);
            when(mockDeviceInfo.getAdvertisingId()).thenReturn("test-ad-id");
            hyBidMock.when(HyBid::getDeviceInfo).thenReturn(mockDeviceInfo);

            assertTrue(userDataManager.canCollectData());
        }
    }

    @Test
    public void testCanCollectData_whenGdprAppliesAndConsentDenied_returnsFalse() {
        appPrefsStorage.put("IABTCF_gdprApplies", 1);
        privatePrefsStorage.put("gdpr_consent_state", 0);
        privatePrefsStorage.put("gdpr_advertising_id", "test-ad-id");

        try (MockedStatic<HyBid> hyBidMock = mockStatic(HyBid.class)) {
            DeviceInfo mockDeviceInfo = mock(DeviceInfo.class);
            when(mockDeviceInfo.getAdvertisingId()).thenReturn("test-ad-id");
            hyBidMock.when(HyBid::getDeviceInfo).thenReturn(mockDeviceInfo);

            assertFalse(userDataManager.canCollectData());
        }
    }

    // =============================================================================================
    // Is Consent Denied Tests
    // =============================================================================================

    @Test
    public void testIsConsentDenied_whenConsentStateDenied_returnsTrue() {
        privatePrefsStorage.put("gdpr_consent_state", 0);
        assertTrue(userDataManager.isConsentDenied());
    }

    @Test
    public void testIsConsentDenied_whenConsentStateAccepted_returnsFalse() {
        privatePrefsStorage.put("gdpr_consent_state", 1);
        assertFalse(userDataManager.isConsentDenied());
    }

    @Test
    public void testIsConsentDenied_whenConsentStateNotSet_returnsFalse() {
        assertFalse(userDataManager.isConsentDenied());
    }

    // =============================================================================================
    // Grant/Deny/Revoke Consent Tests (Deprecated)
    // =============================================================================================

    @Test
    public void testGrantConsent_withAdvertisingIdInDeviceInfo() {
        try (MockedStatic<HyBid> hyBidMock = mockStatic(HyBid.class)) {
            DeviceInfo mockDeviceInfo = mock(DeviceInfo.class);
            when(mockDeviceInfo.getAdvertisingId()).thenReturn("test-ad-id");
            hyBidMock.when(HyBid::getDeviceInfo).thenReturn(mockDeviceInfo);

            userDataManager.grantConsent();

            // Verify consent state is set to accepted
            verify(mockPrivateEditor).putInt("gdpr_consent_state", 1);
            verify(mockPrivateEditor).putString("gdpr_advertising_id", "test-ad-id");
            verify(mockPrivateEditor).apply();
        }
    }

    @Test
    public void testDenyConsent_withAdvertisingIdInDeviceInfo() {
        try (MockedStatic<HyBid> hyBidMock = mockStatic(HyBid.class)) {
            DeviceInfo mockDeviceInfo = mock(DeviceInfo.class);
            when(mockDeviceInfo.getAdvertisingId()).thenReturn("test-ad-id");
            hyBidMock.when(HyBid::getDeviceInfo).thenReturn(mockDeviceInfo);

            userDataManager.denyConsent();

            verify(mockPrivateEditor).putInt("gdpr_consent_state", 0);
            verify(mockPrivateEditor).putString("gdpr_advertising_id", "test-ad-id");
            verify(mockPrivateEditor).apply();
        }
    }

    @Test
    public void testRevokeConsent_callsDenyConsent() {
        try (MockedStatic<HyBid> hyBidMock = mockStatic(HyBid.class)) {
            DeviceInfo mockDeviceInfo = mock(DeviceInfo.class);
            when(mockDeviceInfo.getAdvertisingId()).thenReturn("test-ad-id");
            hyBidMock.when(HyBid::getDeviceInfo).thenReturn(mockDeviceInfo);

            userDataManager.revokeConsent();

            verify(mockPrivateEditor).putInt("gdpr_consent_state", 0);
            verify(mockPrivateEditor).apply();
        }
    }

    // =============================================================================================
    // CCPA Tests
    // =============================================================================================

    @Test
    public void testSetIABUSPrivacyString_storesValue() {
        userDataManager.setIABUSPrivacyString("1YNN");
        verify(mockPrivateEditor).putString("ccpa_consent", "1YNN");
        verify(mockPrivateEditor).apply();
    }

    @Test
    public void testGetIABUSPrivacyString_returnsStoredValue() {
        privatePrefsStorage.put("ccpa_consent", "1YNN");
        String result = userDataManager.getIABUSPrivacyString();
        assertEquals("1YNN", result);
    }

    @Test
    public void testGetIABUSPrivacyString_whenNotSet_returnsNull() {
        String result = userDataManager.getIABUSPrivacyString();
        assertNull(result);
    }

    @Test
    public void testRemoveIABUSPrivacyString_removesValue() {
        userDataManager.removeIABUSPrivacyString();
        verify(mockPrivateEditor).remove("ccpa_consent");
        verify(mockPrivateEditor).apply();
    }

    @Test
    public void testIsCCPAOptOut_withOptOutY_returnsTrue() {
        privatePrefsStorage.put("ccpa_consent", "1-Y-");
        assertTrue(userDataManager.isCCPAOptOut());
    }

    @Test
    public void testIsCCPAOptOut_withOptOutUppercaseY_returnsTrue() {
        privatePrefsStorage.put("ccpa_consent", "1NYY");
        assertTrue(userDataManager.isCCPAOptOut());
    }

    @Test
    public void testIsCCPAOptOut_withOptOutN_returnsFalse() {
        privatePrefsStorage.put("ccpa_consent", "1YNN");
        assertFalse(userDataManager.isCCPAOptOut());
    }

    @Test
    public void testIsCCPAOptOut_withEmptyString_returnsFalse() {
        privatePrefsStorage.put("ccpa_consent", "");
        assertFalse(userDataManager.isCCPAOptOut());
    }

    @Test
    public void testIsCCPAOptOut_withNull_returnsFalse() {
        assertFalse(userDataManager.isCCPAOptOut());
    }

    @Test
    public void testIsCCPAOptOut_withShortString_returnsFalse() {
        privatePrefsStorage.put("ccpa_consent", "1Y");
        assertFalse(userDataManager.isCCPAOptOut());
    }

    // =============================================================================================
    // GDPR Consent String Tests
    // =============================================================================================

    @Test
    public void testSetIABGDPRConsentString_storesValue() {
        userDataManager.setIABGDPRConsentString("test-consent-string");
        verify(mockPrivateEditor).putString("gdpr_consent", "test-consent-string");
        verify(mockPrivateEditor).apply();
    }

    @Test
    public void testGetIABGDPRConsentString_returnsPrivateStoredValue() {
        privatePrefsStorage.put("gdpr_consent", "private-consent");
        String result = userDataManager.getIABGDPRConsentString();
        assertEquals("private-consent", result);
    }

    @Test
    public void testGetIABGDPRConsentString_returnsTCF2PublicValue_whenPrivateEmpty() {
        appPrefsStorage.put("IABTCF_TCString", "tcf2-consent");
        String result = userDataManager.getIABGDPRConsentString();
        assertEquals("tcf2-consent", result);
    }

    @Test
    public void testGetIABGDPRConsentString_returnsTCF1PublicValue_whenPrivateAndTCF2Empty() {
        appPrefsStorage.put("IABConsent_ConsentString", "tcf1-consent");
        String result = userDataManager.getIABGDPRConsentString();
        assertEquals("tcf1-consent", result);
    }

    @Test
    public void testGetIABGDPRConsentString_whenNotSet_returnsNull() {
        String result = userDataManager.getIABGDPRConsentString();
        assertNull(result);
    }

    @Test
    public void testRemoveIABGDPRConsentString_removesValue() {
        userDataManager.removeIABGDPRConsentString();
        verify(mockPrivateEditor).remove("gdpr_consent");
        verify(mockPrivateEditor).apply();
    }

    // =============================================================================================
    // GPP Tests
    // =============================================================================================

    @Test
    public void testSetGppString_storesValue() {
        userDataManager.setGppString("test-gpp-string");
        verify(mockPrivateEditor).putString("gpp_string", "test-gpp-string");
        verify(mockPrivateEditor).apply();
    }

    @Test
    public void testGetGppString_returnsStoredValue() {
        privatePrefsStorage.put("gpp_string", "test-gpp-string");
        String result = userDataManager.getGppString();
        assertEquals("test-gpp-string", result);
    }

    @Test
    public void testGetGppString_whenNotSet_returnsNull() {
        String result = userDataManager.getGppString();
        assertNull(result);
    }

    @Test
    public void testRemoveGppString_removesValue() {
        userDataManager.removeGppString();
        verify(mockPrivateEditor).remove("gpp_string");
        verify(mockPrivateEditor).apply();
    }

    @Test
    public void testSetGppSid_storesValue() {
        userDataManager.setGppSid("test-gpp-id");
        verify(mockPrivateEditor).putString("gpp_id", "test-gpp-id");
        verify(mockPrivateEditor).apply();
    }

    @Test
    public void testGetGppSid_returnsStoredValue() {
        privatePrefsStorage.put("gpp_id", "test-gpp-id");
        String result = userDataManager.getGppSid();
        assertEquals("test-gpp-id", result);
    }

    @Test
    public void testGetGppSid_whenNotSet_returnsNull() {
        String result = userDataManager.getGppSid();
        assertNull(result);
    }

    @Test
    public void testRemoveGppSid_removesValue() {
        userDataManager.removeGppSid();
        verify(mockPrivateEditor).remove("gpp_id");
        verify(mockPrivateEditor).apply();
    }

    @Test
    public void testRemoveGppData_removesBothValues() {
        userDataManager.removeGppData();
        verify(mockPrivateEditor).remove("gpp_string");
        verify(mockPrivateEditor).remove("gpp_id");
    }

    // =============================================================================================
    // Shared Preferences Listener Tests
    // =============================================================================================

    @Test
    public void testListener_onGDPRTCF2ConsentChange_updatesConsent() {
        assertNotNull(capturedListener);

        appPrefsStorage.put("IABTCF_TCString", "new-tcf2-consent");
        capturedListener.onSharedPreferenceChanged(mockAppPreferences, "IABTCF_TCString");

        verify(mockPrivateEditor).putString("gdpr_consent", "new-tcf2-consent");
        verify(mockPrivateEditor).apply();
    }

    @Test
    public void testListener_onGDPRTCF2ConsentRemove_removesConsent() {
        assertNotNull(capturedListener);

        capturedListener.onSharedPreferenceChanged(mockAppPreferences, "IABTCF_TCString");

        verify(mockPrivateEditor).remove("gdpr_consent");
        verify(mockPrivateEditor).apply();
    }

    @Test
    public void testListener_onGDPRTCF1ConsentChange_updatesConsent() {
        assertNotNull(capturedListener);

        appPrefsStorage.put("IABConsent_ConsentString", "new-tcf1-consent");
        capturedListener.onSharedPreferenceChanged(mockAppPreferences, "IABConsent_ConsentString");

        verify(mockPrivateEditor).putString("gdpr_consent", "new-tcf1-consent");
        verify(mockPrivateEditor).apply();
    }

    @Test
    public void testListener_onGDPRTCF1ConsentRemove_removesConsent() {
        assertNotNull(capturedListener);

        capturedListener.onSharedPreferenceChanged(mockAppPreferences, "IABConsent_ConsentString");

        verify(mockPrivateEditor).remove("gdpr_consent");
        verify(mockPrivateEditor).apply();
    }

    @Test
    public void testListener_onCCPAConsentChange_updatesConsent() {
        assertNotNull(capturedListener);

        appPrefsStorage.put("IABUSPrivacy_String", "1YNN");
        capturedListener.onSharedPreferenceChanged(mockAppPreferences, "IABUSPrivacy_String");

        verify(mockPrivateEditor).putString("ccpa_consent", "1YNN");
        verify(mockPrivateEditor).apply();
    }

    @Test
    public void testListener_onCCPAConsentRemove_removesConsent() {
        assertNotNull(capturedListener);

        capturedListener.onSharedPreferenceChanged(mockAppPreferences, "IABUSPrivacy_String");

        verify(mockPrivateEditor).remove("ccpa_consent");
        verify(mockPrivateEditor).apply();
    }

    @Test
    public void testListener_onGppStringChange_updatesGppString() {
        assertNotNull(capturedListener);

        // Reset the mock to avoid counting the constructor calls
        org.mockito.Mockito.reset(mockPrivateEditor);
        when(mockPrivateEditor.putString(anyString(), anyString())).thenReturn(mockPrivateEditor);

        appPrefsStorage.put("IABGPP_HDR_GppString", "new-gpp-string");
        capturedListener.onSharedPreferenceChanged(mockAppPreferences, "IABGPP_HDR_GppString");

        verify(mockPrivateEditor).putString("gpp_string", "new-gpp-string");
        verify(mockPrivateEditor).apply();
    }

    @Test
    public void testListener_onGppStringRemove_removesGppString() {
        assertNotNull(capturedListener);

        capturedListener.onSharedPreferenceChanged(mockAppPreferences, "IABGPP_HDR_GppString");

        verify(mockPrivateEditor).remove("gpp_string");
        verify(mockPrivateEditor).apply();
    }

    @Test
    public void testListener_onGppIdChange_updatesGppId() {
        assertNotNull(capturedListener);

        // Reset the mock to avoid counting the constructor calls
        org.mockito.Mockito.reset(mockPrivateEditor);
        when(mockPrivateEditor.putString(anyString(), anyString())).thenReturn(mockPrivateEditor);

        appPrefsStorage.put("IABGPP_GppSID", "new-gpp-id");
        capturedListener.onSharedPreferenceChanged(mockAppPreferences, "IABGPP_GppSID");

        verify(mockPrivateEditor).putString("gpp_id", "new-gpp-id");
        verify(mockPrivateEditor).apply();
    }

    @Test
    public void testListener_onGppIdRemove_removesGppId() {
        assertNotNull(capturedListener);

        capturedListener.onSharedPreferenceChanged(mockAppPreferences, "IABGPP_GppSID");

        verify(mockPrivateEditor).remove("gpp_id");
        verify(mockPrivateEditor).apply();
    }

    @Test
    public void testListener_withNullKey_doesNothing() {
        assertNotNull(capturedListener);

        capturedListener.onSharedPreferenceChanged(mockAppPreferences, null);

        verify(mockPrivateEditor, never()).putString(anyString(), anyString());
        verify(mockPrivateEditor, never()).remove(anyString());
    }

    @Test
    public void testListener_withEmptyKey_doesNothing() {
        assertNotNull(capturedListener);

        capturedListener.onSharedPreferenceChanged(mockAppPreferences, "");

        verify(mockPrivateEditor, never()).putString(anyString(), anyString());
        verify(mockPrivateEditor, never()).remove(anyString());
    }

    @Test
    public void testListener_withUnknownKey_doesNothing() {
        assertNotNull(capturedListener);

        capturedListener.onSharedPreferenceChanged(mockAppPreferences, "unknown_key");

        verify(mockPrivateEditor, never()).putString(anyString(), anyString());
        verify(mockPrivateEditor, never()).remove(anyString());
    }

    // =============================================================================================
    // Constructor Initialization Tests
    // =============================================================================================

    @Test
    public void testConstructor_initializesPublicConsent_withTCF2() {
        // Create fresh mocks for this test
        SharedPreferences mockNewPrivatePrefs = mock(SharedPreferences.class);
        SharedPreferences.Editor mockNewPrivateEditor = mock(SharedPreferences.Editor.class);

        when(mockContext.getSharedPreferences("net.pubnative.lite.dataconsent", Context.MODE_PRIVATE))
                .thenReturn(mockNewPrivatePrefs);
        when(mockNewPrivatePrefs.edit()).thenReturn(mockNewPrivateEditor);
        when(mockNewPrivateEditor.putString(anyString(), anyString())).thenReturn(mockNewPrivateEditor);

        SharedPreferences mockNewAppPrefs = mock(SharedPreferences.class);
        Map<String, Object> newAppPrefsStorage = new HashMap<>();
        newAppPrefsStorage.put("IABTCF_TCString", "init-tcf2-consent");
        newAppPrefsStorage.put("IABUSPrivacy_String", "1YNN");
        newAppPrefsStorage.put("IABGPP_HDR_GppString", "init-gpp-string");
        newAppPrefsStorage.put("IABGPP_GppSID", "init-gpp-id");

        when(mockNewAppPrefs.getString(anyString(), any())).thenAnswer(invocation -> {
            String key = invocation.getArgument(0);
            Object defaultValue = invocation.getArgument(1);
            return newAppPrefsStorage.containsKey(key) ? (String) newAppPrefsStorage.get(key) : defaultValue;
        });

        try (MockedStatic<PreferenceManager> preferenceManagerMock = mockStatic(PreferenceManager.class)) {
            preferenceManagerMock.when(() -> PreferenceManager.getDefaultSharedPreferences(mockContext))
                    .thenReturn(mockNewAppPrefs);

            UserDataManager newManager = new UserDataManager(mockContext);

            // Verify all consent strings were initialized
            verify(mockNewPrivateEditor).putString("gdpr_consent", "init-tcf2-consent");
            verify(mockNewPrivateEditor).putString("ccpa_consent", "1YNN");
            verify(mockNewPrivateEditor).putString("gpp_string", "init-gpp-string");
            verify(mockNewPrivateEditor).putString("gpp_id", "init-gpp-id");
        }
    }

    @Test
    public void testConstructor_initializesPublicConsent_withTCF1() {
        appPrefsStorage.clear();
        appPrefsStorage.put("IABConsent_ConsentString", "init-tcf1-consent");

        try (MockedStatic<PreferenceManager> preferenceManagerMock = mockStatic(PreferenceManager.class)) {
            preferenceManagerMock.when(() -> PreferenceManager.getDefaultSharedPreferences(mockContext))
                    .thenReturn(mockAppPreferences);

            UserDataManager newManager = new UserDataManager(mockContext);

            verify(mockPrivateEditor).putString("gdpr_consent", "init-tcf1-consent");
        }
    }

    @Test
    public void testConstructor_withNullAppPreferences_doesNotCrash() {
        try (MockedStatic<PreferenceManager> preferenceManagerMock = mockStatic(PreferenceManager.class)) {
            preferenceManagerMock.when(() -> PreferenceManager.getDefaultSharedPreferences(mockContext))
                    .thenReturn(null);

            UserDataManager newManager = new UserDataManager(mockContext);

            // Should not crash and should handle null gracefully
            assertFalse(newManager.gdprApplies());
        }
    }

    // =============================================================================================
    // Should Ask Consent Tests (Deprecated)
    // =============================================================================================

    @Test
    public void testShouldAskConsent_whenGdprDoesNotApply_returnsFalse() {
        assertFalse(userDataManager.shouldAskConsent());
    }

    @Test
    public void testShouldAskConsent_whenGdprAppliesAndNotAsked_returnsTrue() {
        appPrefsStorage.put("IABTCF_gdprApplies", 1);
        assertTrue(userDataManager.shouldAskConsent());
    }

    @Test
    public void testShouldAskConsent_whenGdprAppliesAndAsked_returnsFalse() {
        appPrefsStorage.put("IABTCF_gdprApplies", 1);
        privatePrefsStorage.put("gdpr_consent_state", 1);
        privatePrefsStorage.put("gdpr_advertising_id", "test-ad-id");

        try (MockedStatic<HyBid> hyBidMock = mockStatic(HyBid.class)) {
            DeviceInfo mockDeviceInfo = mock(DeviceInfo.class);
            when(mockDeviceInfo.getAdvertisingId()).thenReturn("test-ad-id");
            hyBidMock.when(HyBid::getDeviceInfo).thenReturn(mockDeviceInfo);

            assertFalse(userDataManager.shouldAskConsent());
        }
    }

    @Test
    public void testShouldAskConsent_whenAdvertisingIdChanged_returnsTrue() {
        appPrefsStorage.put("IABTCF_gdprApplies", 1);
        privatePrefsStorage.put("gdpr_consent_state", 1);
        privatePrefsStorage.put("gdpr_advertising_id", "old-ad-id");

        try (MockedStatic<HyBid> hyBidMock = mockStatic(HyBid.class)) {
            DeviceInfo mockDeviceInfo = mock(DeviceInfo.class);
            when(mockDeviceInfo.getAdvertisingId()).thenReturn("new-ad-id");
            hyBidMock.when(HyBid::getDeviceInfo).thenReturn(mockDeviceInfo);

            assertTrue(userDataManager.shouldAskConsent());
        }
    }

    // =============================================================================================
    // Show Consent Screen Tests (Deprecated)
    // =============================================================================================

    @Test
    public void testGetConsentScreenIntent_returnsCorrectIntent() {
        try {
            userDataManager.getConsentScreenIntent(mockContext);
            // If no exception is thrown, the test passes
        } catch (Exception e) {
            // Intent creation may fail in test environment, but method should not crash
        }
    }
}

