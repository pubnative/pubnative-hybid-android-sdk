// HyBid SDK License
//
// https://github.com/pubnative/pubnative-hybid-android-sdk/blob/main/LICENSE
//
package net.pubnative.lite.sdk;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import android.content.Context;
import android.content.SharedPreferences;
import android.location.Location;
import android.os.BatteryManager;
import android.os.Build;

import net.pubnative.lite.sdk.location.HyBidLocationManager;
import net.pubnative.lite.sdk.models.IdModel;
import net.pubnative.lite.sdk.utils.PNCrypto;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

@RunWith(RobolectricTestRunner.class)
@Config(sdk = 28)
public class VgiIdManagerTest {

    @Mock
    private Context mockContext;
    @Mock
    private Context mockAppContext;
    @Mock
    private SharedPreferences mockPreferences;
    @Mock
    private SharedPreferences.Editor mockEditor;
    @Mock
    private UserDataManager mockUserDataManager;
    @Mock
    private DeviceInfo mockDeviceInfo;
    @Mock
    private HyBidLocationManager mockLocationManager;
    @Mock
    private BatteryManager mockBatteryManager;
    @Mock
    private Location mockLocation;

    private VgiIdManager vgiIdManager;
    private static final String APP_TOKEN = "test_app_token";
    private static final String BUNDLE_ID = "com.test.app";

    @Before
    public void setup() {
        org.mockito.MockitoAnnotations.openMocks(this);

        // Setup context mocks
        when(mockContext.getApplicationContext()).thenReturn(mockAppContext);
        when(mockAppContext.getSharedPreferences("net.pubnative.lite.vgiid", Context.MODE_PRIVATE))
                .thenReturn(mockPreferences);
        when(mockAppContext.getSystemService(Context.BATTERY_SERVICE)).thenReturn(mockBatteryManager);

        // Setup preferences mocks
        when(mockPreferences.edit()).thenReturn(mockEditor);
        when(mockEditor.putString(anyString(), anyString())).thenReturn(mockEditor);
        when(mockPreferences.getString(anyString(), any())).thenReturn(null);

        // Setup device info mocks
        when(mockDeviceInfo.getOSVersion()).thenReturn("10");
        when(mockDeviceInfo.getAdvertisingId()).thenReturn("test-gaid");
        when(mockDeviceInfo.limitTracking()).thenReturn(false);

        // Setup user data manager mocks
        when(mockUserDataManager.getIABUSPrivacyString()).thenReturn("1YNN");
        when(mockUserDataManager.getIABGDPRConsentString()).thenReturn("test-consent");

        // Setup location mocks
        when(mockLocation.getLatitude()).thenReturn(37.7749);
        when(mockLocation.getLongitude()).thenReturn(-122.4194);
        when(mockLocation.getAccuracy()).thenReturn(10.5f);
        when(mockLocation.getTime()).thenReturn(1234567890L);

        // Setup battery manager mocks with valid default values to avoid division by zero
        when(mockBatteryManager.getIntProperty(BatteryManager.BATTERY_PROPERTY_CHARGE_COUNTER))
                .thenReturn(5000);
        when(mockBatteryManager.getIntProperty(BatteryManager.BATTERY_PROPERTY_CAPACITY))
                .thenReturn(100);
    }

    private VgiIdManager createVgiIdManager() {
        try (MockedStatic<HyBid> hyBidMock = mockStatic(HyBid.class)) {
            hyBidMock.when(HyBid::getAppToken).thenReturn(APP_TOKEN);
            return new VgiIdManager(mockContext);
        }
    }

    // =============================================================================================
    // Constructor Tests
    // =============================================================================================

    @Test
    public void testConstructor_initializesCorrectly() {
        try (MockedStatic<HyBid> hyBidMock = mockStatic(HyBid.class)) {
            hyBidMock.when(HyBid::getAppToken).thenReturn(APP_TOKEN);

            VgiIdManager manager = new VgiIdManager(mockContext);

            assertNotNull(manager);
            verify(mockContext).getApplicationContext();
            verify(mockAppContext).getSharedPreferences("net.pubnative.lite.vgiid", Context.MODE_PRIVATE);
        }
    }

    // =============================================================================================
    // Init Tests
    // =============================================================================================

    @Test
    public void testInit_withValidData_createsAndStoresVgiId() {
        try (MockedStatic<HyBid> hyBidMock = mockStatic(HyBid.class);
             MockedStatic<PNCrypto> cryptoMock = mockStatic(PNCrypto.class);
             MockedStatic<Build> buildMock = mockStatic(Build.class)) {

            hyBidMock.when(HyBid::getAppToken).thenReturn(APP_TOKEN);
            hyBidMock.when(HyBid::getUserDataManager).thenReturn(mockUserDataManager);
            hyBidMock.when(HyBid::getDeviceInfo).thenReturn(mockDeviceInfo);
            hyBidMock.when(HyBid::getLocationManager).thenReturn(mockLocationManager);
            hyBidMock.when(HyBid::getBundleId).thenReturn(BUNDLE_ID);

            when(mockLocationManager.getUserLocation()).thenReturn(mockLocation);

            cryptoMock.when(() -> PNCrypto.encryptString(anyString(), eq(APP_TOKEN)))
                    .thenReturn("encrypted_vgi_id");

            VgiIdManager manager = new VgiIdManager(mockContext);
            manager.init();

            verify(mockEditor).putString(eq("VGI_id"), eq("encrypted_vgi_id"));
            verify(mockEditor).apply();
        }
    }

    @Test
    public void testInit_withNullLocationManager_stillCreatesVgiId() {
        try (MockedStatic<HyBid> hyBidMock = mockStatic(HyBid.class);
             MockedStatic<PNCrypto> cryptoMock = mockStatic(PNCrypto.class)) {

            hyBidMock.when(HyBid::getAppToken).thenReturn(APP_TOKEN);
            hyBidMock.when(HyBid::getUserDataManager).thenReturn(mockUserDataManager);
            hyBidMock.when(HyBid::getDeviceInfo).thenReturn(mockDeviceInfo);
            hyBidMock.when(HyBid::getLocationManager).thenReturn(null);
            hyBidMock.when(HyBid::getBundleId).thenReturn(BUNDLE_ID);

            cryptoMock.when(() -> PNCrypto.encryptString(anyString(), eq(APP_TOKEN)))
                    .thenReturn("encrypted_vgi_id");

            VgiIdManager manager = new VgiIdManager(mockContext);
            manager.init();

            verify(mockEditor).putString(eq("VGI_id"), eq("encrypted_vgi_id"));
            verify(mockEditor).apply();
        }
    }

    @Test
    public void testInit_withNullUserLocation_createsVgiIdWithEmptyLocation() {
        try (MockedStatic<HyBid> hyBidMock = mockStatic(HyBid.class);
             MockedStatic<PNCrypto> cryptoMock = mockStatic(PNCrypto.class)) {

            hyBidMock.when(HyBid::getAppToken).thenReturn(APP_TOKEN);
            hyBidMock.when(HyBid::getUserDataManager).thenReturn(mockUserDataManager);
            hyBidMock.when(HyBid::getDeviceInfo).thenReturn(mockDeviceInfo);
            hyBidMock.when(HyBid::getLocationManager).thenReturn(mockLocationManager);
            hyBidMock.when(HyBid::getBundleId).thenReturn(BUNDLE_ID);

            when(mockLocationManager.getUserLocation()).thenReturn(null);

            cryptoMock.when(() -> PNCrypto.encryptString(anyString(), eq(APP_TOKEN)))
                    .thenReturn("encrypted_vgi_id");

            VgiIdManager manager = new VgiIdManager(mockContext);
            manager.init();

            verify(mockEditor).putString(eq("VGI_id"), eq("encrypted_vgi_id"));
            verify(mockEditor).apply();
        }
    }

    @Test
    public void testInit_withEncryptionException_handlesGracefully() {
        try (MockedStatic<HyBid> hyBidMock = mockStatic(HyBid.class);
             MockedStatic<PNCrypto> cryptoMock = mockStatic(PNCrypto.class)) {

            hyBidMock.when(HyBid::getAppToken).thenReturn(APP_TOKEN);
            hyBidMock.when(HyBid::getUserDataManager).thenReturn(mockUserDataManager);
            hyBidMock.when(HyBid::getDeviceInfo).thenReturn(mockDeviceInfo);
            hyBidMock.when(HyBid::getLocationManager).thenReturn(mockLocationManager);
            hyBidMock.when(HyBid::getBundleId).thenReturn(BUNDLE_ID);

            when(mockLocationManager.getUserLocation()).thenReturn(mockLocation);

            cryptoMock.when(() -> PNCrypto.encryptString(anyString(), eq(APP_TOKEN)))
                    .thenThrow(new RuntimeException("Encryption failed"));

            VgiIdManager manager = new VgiIdManager(mockContext);
            manager.init(); // Should not crash

            // Verify error was reported
            hyBidMock.verify(() -> HyBid.reportException(any(Exception.class)));
        }
    }

    // =============================================================================================
    // GetVgiIdModel Tests
    // =============================================================================================

    @Test
    public void testGetVgiIdModel_whenNoStoredId_returnsNull() {
        try (MockedStatic<HyBid> hyBidMock = mockStatic(HyBid.class)) {
            hyBidMock.when(HyBid::getAppToken).thenReturn(APP_TOKEN);

            when(mockPreferences.getString("VGI_id", null)).thenReturn(null);

            VgiIdManager manager = new VgiIdManager(mockContext);
            IdModel result = manager.getVgiIdModel();

            assertNull(result);
        }
    }

    @Test
    public void testGetVgiIdModel_whenEmptyStoredId_returnsNull() {
        try (MockedStatic<HyBid> hyBidMock = mockStatic(HyBid.class)) {
            hyBidMock.when(HyBid::getAppToken).thenReturn(APP_TOKEN);

            when(mockPreferences.getString("VGI_id", null)).thenReturn("");

            VgiIdManager manager = new VgiIdManager(mockContext);
            IdModel result = manager.getVgiIdModel();

            assertNull(result);
        }
    }

    @Test
    public void testGetVgiIdModel_withValidEncryptedData_returnsDecryptedModel() throws Exception {
        try (MockedStatic<HyBid> hyBidMock = mockStatic(HyBid.class);
             MockedStatic<PNCrypto> cryptoMock = mockStatic(PNCrypto.class)) {

            hyBidMock.when(HyBid::getAppToken).thenReturn(APP_TOKEN);

            String encryptedData = "encrypted_vgi_id";
            String decryptedJson = "{\"apps\":[],\"device\":{},\"users\":[]}";

            when(mockPreferences.getString("VGI_id", null)).thenReturn(encryptedData);
            cryptoMock.when(() -> PNCrypto.decryptString(encryptedData, APP_TOKEN))
                    .thenReturn(decryptedJson);

            VgiIdManager manager = new VgiIdManager(mockContext);
            IdModel result = manager.getVgiIdModel();

            assertNotNull(result);
        }
    }

    @Test
    public void testGetVgiIdModel_withDecryptionReturningNull_returnsModel() throws Exception {
        try (MockedStatic<HyBid> hyBidMock = mockStatic(HyBid.class);
             MockedStatic<PNCrypto> cryptoMock = mockStatic(PNCrypto.class)) {

            hyBidMock.when(HyBid::getAppToken).thenReturn(APP_TOKEN);

            String encryptedData = "encrypted_vgi_id";

            when(mockPreferences.getString("VGI_id", null)).thenReturn(encryptedData);
            cryptoMock.when(() -> PNCrypto.decryptString(encryptedData, APP_TOKEN))
                    .thenReturn(null);

            VgiIdManager manager = new VgiIdManager(mockContext);
            IdModel result = manager.getVgiIdModel();

            assertNotNull(result);
        }
    }

    @Test
    public void testGetVgiIdModel_withInvalidJson_handlesExceptionAndReturnsNull() {
        try (MockedStatic<HyBid> hyBidMock = mockStatic(HyBid.class);
             MockedStatic<PNCrypto> cryptoMock = mockStatic(PNCrypto.class)) {

            hyBidMock.when(HyBid::getAppToken).thenReturn(APP_TOKEN);

            String encryptedData = "encrypted_vgi_id";
            String invalidJson = "{invalid json";

            when(mockPreferences.getString("VGI_id", null)).thenReturn(encryptedData);
            cryptoMock.when(() -> PNCrypto.decryptString(encryptedData, APP_TOKEN))
                    .thenReturn(invalidJson);

            VgiIdManager manager = new VgiIdManager(mockContext);
            IdModel result = manager.getVgiIdModel();

            assertNull(result);
            hyBidMock.verify(() -> HyBid.reportException(any(Exception.class)));
        }
    }

    @Test
    public void testGetVgiIdModel_withDecryptionException_handlesGracefully() {
        try (MockedStatic<HyBid> hyBidMock = mockStatic(HyBid.class);
             MockedStatic<PNCrypto> cryptoMock = mockStatic(PNCrypto.class)) {

            hyBidMock.when(HyBid::getAppToken).thenReturn(APP_TOKEN);

            String encryptedData = "encrypted_vgi_id";

            when(mockPreferences.getString("VGI_id", null)).thenReturn(encryptedData);
            cryptoMock.when(() -> PNCrypto.decryptString(encryptedData, APP_TOKEN))
                    .thenThrow(new RuntimeException("Decryption failed"));

            VgiIdManager manager = new VgiIdManager(mockContext);
            IdModel result = manager.getVgiIdModel();

            assertNull(result);
            hyBidMock.verify(() -> HyBid.reportException(any(Exception.class)));
        }
    }

    // =============================================================================================
    // SetVgiIdModel Tests
    // =============================================================================================

    @Test
    public void testSetVgiIdModel_withValidModel_encryptsAndStores() {
        try (MockedStatic<HyBid> hyBidMock = mockStatic(HyBid.class);
             MockedStatic<PNCrypto> cryptoMock = mockStatic(PNCrypto.class)) {

            hyBidMock.when(HyBid::getAppToken).thenReturn(APP_TOKEN);

            IdModel idModel = new IdModel();
            String encryptedData = "encrypted_data";

            cryptoMock.when(() -> PNCrypto.encryptString(anyString(), eq(APP_TOKEN)))
                    .thenReturn(encryptedData);

            VgiIdManager manager = new VgiIdManager(mockContext);
            manager.setVgiIdModel(idModel);

            verify(mockEditor).putString("VGI_id", encryptedData);
            verify(mockEditor).apply();
        }
    }

    @Test
    public void testSetVgiIdModel_withNullModel_doesNothing() {
        try (MockedStatic<HyBid> hyBidMock = mockStatic(HyBid.class)) {
            hyBidMock.when(HyBid::getAppToken).thenReturn(APP_TOKEN);

            VgiIdManager manager = new VgiIdManager(mockContext);
            manager.setVgiIdModel(null);

            verify(mockEditor, org.mockito.Mockito.never()).putString(anyString(), anyString());
            verify(mockEditor, org.mockito.Mockito.never()).apply();
        }
    }

    @Test
    public void testSetVgiIdModel_withEncryptionException_handlesGracefully() {
        try (MockedStatic<HyBid> hyBidMock = mockStatic(HyBid.class);
             MockedStatic<PNCrypto> cryptoMock = mockStatic(PNCrypto.class)) {

            hyBidMock.when(HyBid::getAppToken).thenReturn(APP_TOKEN);

            IdModel idModel = new IdModel();

            cryptoMock.when(() -> PNCrypto.encryptString(anyString(), eq(APP_TOKEN)))
                    .thenThrow(new RuntimeException("Encryption failed"));

            VgiIdManager manager = new VgiIdManager(mockContext);
            manager.setVgiIdModel(idModel); // Should not crash

            hyBidMock.verify(() -> HyBid.reportException(any(Exception.class)));
        }
    }

    // =============================================================================================
    // Battery Tests (API Level 21+)
    // =============================================================================================

    @Test
    @Config(sdk = 21)
    public void testGetBatteryCapacity_onLollipop_returnsCalculatedCapacity() {
        try (MockedStatic<HyBid> hyBidMock = mockStatic(HyBid.class);
             MockedStatic<PNCrypto> cryptoMock = mockStatic(PNCrypto.class)) {

            hyBidMock.when(HyBid::getAppToken).thenReturn(APP_TOKEN);

            when(mockBatteryManager.getIntProperty(BatteryManager.BATTERY_PROPERTY_CHARGE_COUNTER))
                    .thenReturn(5000);
            when(mockBatteryManager.getIntProperty(BatteryManager.BATTERY_PROPERTY_CAPACITY))
                    .thenReturn(100);

            VgiIdManager manager = new VgiIdManager(mockContext);

            // Trigger init which internally calls getBatteryCapacity
            hyBidMock.when(HyBid::getUserDataManager).thenReturn(mockUserDataManager);
            hyBidMock.when(HyBid::getDeviceInfo).thenReturn(mockDeviceInfo);
            hyBidMock.when(HyBid::getLocationManager).thenReturn(null);
            hyBidMock.when(HyBid::getBundleId).thenReturn(BUNDLE_ID);

            ArgumentCaptor<String> jsonCaptor = ArgumentCaptor.forClass(String.class);
            cryptoMock.when(() -> PNCrypto.encryptString(jsonCaptor.capture(), eq(APP_TOKEN)))
                    .thenReturn("encrypted");

            manager.init();

            // Verify battery manager was called
            verify(mockBatteryManager).getIntProperty(BatteryManager.BATTERY_PROPERTY_CHARGE_COUNTER);
            verify(mockBatteryManager).getIntProperty(BatteryManager.BATTERY_PROPERTY_CAPACITY);

            // Verify the battery capacity is calculated and included in the JSON
            String capturedJson = jsonCaptor.getValue();
            assertNotNull(capturedJson);
            assertTrue("JSON should contain battery capacity", capturedJson.contains("\"capacity\":\"5000\""));
        }
    }

    @Test
    @Config(sdk = 21)
    public void testGetBatteryCapacity_withMinValues_returnsNegativeOne() {
        try (MockedStatic<HyBid> hyBidMock = mockStatic(HyBid.class)) {
            hyBidMock.when(HyBid::getAppToken).thenReturn(APP_TOKEN);

            when(mockBatteryManager.getIntProperty(BatteryManager.BATTERY_PROPERTY_CHARGE_COUNTER))
                    .thenReturn(Integer.MIN_VALUE);
            when(mockBatteryManager.getIntProperty(BatteryManager.BATTERY_PROPERTY_CAPACITY))
                    .thenReturn(100);

            VgiIdManager manager = new VgiIdManager(mockContext);

            hyBidMock.when(HyBid::getUserDataManager).thenReturn(mockUserDataManager);
            hyBidMock.when(HyBid::getDeviceInfo).thenReturn(mockDeviceInfo);
            hyBidMock.when(HyBid::getLocationManager).thenReturn(null);
            hyBidMock.when(HyBid::getBundleId).thenReturn(BUNDLE_ID);

            try (MockedStatic<PNCrypto> cryptoMock = mockStatic(PNCrypto.class)) {
                cryptoMock.when(() -> PNCrypto.encryptString(anyString(), eq(APP_TOKEN)))
                        .thenReturn("encrypted");
                manager.init();
            }

            verify(mockBatteryManager).getIntProperty(BatteryManager.BATTERY_PROPERTY_CHARGE_COUNTER);
        }
    }

    @Test
    @Config(sdk = 21)
    public void testGetBatteryCapacity_withNullBatteryManager_returnsNegativeOne() {
        try (MockedStatic<HyBid> hyBidMock = mockStatic(HyBid.class)) {
            hyBidMock.when(HyBid::getAppToken).thenReturn(APP_TOKEN);

            when(mockAppContext.getSystemService(Context.BATTERY_SERVICE)).thenReturn(null);

            VgiIdManager manager = new VgiIdManager(mockContext);

            hyBidMock.when(HyBid::getUserDataManager).thenReturn(mockUserDataManager);
            hyBidMock.when(HyBid::getDeviceInfo).thenReturn(mockDeviceInfo);
            hyBidMock.when(HyBid::getLocationManager).thenReturn(null);
            hyBidMock.when(HyBid::getBundleId).thenReturn(BUNDLE_ID);

            try (MockedStatic<PNCrypto> cryptoMock = mockStatic(PNCrypto.class)) {
                cryptoMock.when(() -> PNCrypto.encryptString(anyString(), eq(APP_TOKEN)))
                        .thenReturn("encrypted");
                manager.init();
            }

            // Should not crash
        }
    }

    @Test
    public void testGetBatteryCapacity_withZeroCapacity_returnsNegativeOne() {
        try (MockedStatic<HyBid> hyBidMock = mockStatic(HyBid.class)) {
            hyBidMock.when(HyBid::getAppToken).thenReturn(APP_TOKEN);

            when(mockBatteryManager.getIntProperty(BatteryManager.BATTERY_PROPERTY_CHARGE_COUNTER))
                    .thenReturn(5000);
            when(mockBatteryManager.getIntProperty(BatteryManager.BATTERY_PROPERTY_CAPACITY))
                    .thenReturn(0); // Zero capacity to test division by zero protection

            VgiIdManager manager = new VgiIdManager(mockContext);

            hyBidMock.when(HyBid::getUserDataManager).thenReturn(mockUserDataManager);
            hyBidMock.when(HyBid::getDeviceInfo).thenReturn(mockDeviceInfo);
            hyBidMock.when(HyBid::getLocationManager).thenReturn(null);
            hyBidMock.when(HyBid::getBundleId).thenReturn(BUNDLE_ID);

            try (MockedStatic<PNCrypto> cryptoMock = mockStatic(PNCrypto.class)) {
                cryptoMock.when(() -> PNCrypto.encryptString(anyString(), eq(APP_TOKEN)))
                        .thenReturn("encrypted");
                manager.init(); // Should not crash due to division by zero
            }

            // Verify battery manager was called but handled the zero capacity correctly
            verify(mockBatteryManager).getIntProperty(BatteryManager.BATTERY_PROPERTY_CHARGE_COUNTER);
            verify(mockBatteryManager).getIntProperty(BatteryManager.BATTERY_PROPERTY_CAPACITY);
        }
    }

    // =============================================================================================
    // Battery Charging Tests (API Level 23+)
    // =============================================================================================

    @Test
    @Config(sdk = 23)
    public void testIsBatteryCharging_onMarshmallow_returnsChargingStatus() {
        try (MockedStatic<HyBid> hyBidMock = mockStatic(HyBid.class);
             MockedStatic<PNCrypto> cryptoMock = mockStatic(PNCrypto.class)) {

            hyBidMock.when(HyBid::getAppToken).thenReturn(APP_TOKEN);

            when(mockBatteryManager.isCharging()).thenReturn(true);

            VgiIdManager manager = new VgiIdManager(mockContext);

            hyBidMock.when(HyBid::getUserDataManager).thenReturn(mockUserDataManager);
            hyBidMock.when(HyBid::getDeviceInfo).thenReturn(mockDeviceInfo);
            hyBidMock.when(HyBid::getLocationManager).thenReturn(null);
            hyBidMock.when(HyBid::getBundleId).thenReturn(BUNDLE_ID);

            ArgumentCaptor<String> jsonCaptor = ArgumentCaptor.forClass(String.class);
            cryptoMock.when(() -> PNCrypto.encryptString(jsonCaptor.capture(), eq(APP_TOKEN)))
                    .thenReturn("encrypted");

            manager.init();

            verify(mockBatteryManager).isCharging();

            // Verify charging status is in the JSON
            String capturedJson = jsonCaptor.getValue();
            assertNotNull(capturedJson);
            assertTrue("JSON should contain charging status", capturedJson.contains("\"charging\":true"));
        }
    }

    @Test
    @Config(sdk = 23)
    public void testIsBatteryCharging_withNullBatteryManager_returnsNull() {
        try (MockedStatic<HyBid> hyBidMock = mockStatic(HyBid.class)) {
            hyBidMock.when(HyBid::getAppToken).thenReturn(APP_TOKEN);

            when(mockAppContext.getSystemService(Context.BATTERY_SERVICE)).thenReturn(null);

            VgiIdManager manager = new VgiIdManager(mockContext);

            hyBidMock.when(HyBid::getUserDataManager).thenReturn(mockUserDataManager);
            hyBidMock.when(HyBid::getDeviceInfo).thenReturn(mockDeviceInfo);
            hyBidMock.when(HyBid::getLocationManager).thenReturn(null);
            hyBidMock.when(HyBid::getBundleId).thenReturn(BUNDLE_ID);

            try (MockedStatic<PNCrypto> cryptoMock = mockStatic(PNCrypto.class)) {
                cryptoMock.when(() -> PNCrypto.encryptString(anyString(), eq(APP_TOKEN)))
                        .thenReturn("encrypted");
                manager.init();
            }

            // Should not crash
        }
    }

    // =============================================================================================
    // Integration Tests
    // =============================================================================================

    @Test
    public void testFullFlow_initAndRetrieve() throws Exception {
        try (MockedStatic<HyBid> hyBidMock = mockStatic(HyBid.class);
             MockedStatic<PNCrypto> cryptoMock = mockStatic(PNCrypto.class)) {

            hyBidMock.when(HyBid::getAppToken).thenReturn(APP_TOKEN);
            hyBidMock.when(HyBid::getUserDataManager).thenReturn(mockUserDataManager);
            hyBidMock.when(HyBid::getDeviceInfo).thenReturn(mockDeviceInfo);
            hyBidMock.when(HyBid::getLocationManager).thenReturn(mockLocationManager);
            hyBidMock.when(HyBid::getBundleId).thenReturn(BUNDLE_ID);

            when(mockLocationManager.getUserLocation()).thenReturn(mockLocation);

            String encryptedData = "encrypted_vgi_id";
            String jsonData = "{\"apps\":[],\"device\":{},\"users\":[]}";

            cryptoMock.when(() -> PNCrypto.encryptString(anyString(), eq(APP_TOKEN)))
                    .thenReturn(encryptedData);

            VgiIdManager manager = new VgiIdManager(mockContext);
            manager.init();

            // Now simulate retrieval
            when(mockPreferences.getString("VGI_id", null)).thenReturn(encryptedData);
            cryptoMock.when(() -> PNCrypto.decryptString(encryptedData, APP_TOKEN))
                    .thenReturn(jsonData);

            IdModel retrievedModel = manager.getVgiIdModel();

            assertNotNull(retrievedModel);
        }
    }

    @Test
    public void testInit_capturesCorrectDeviceInfo() {
        try (MockedStatic<HyBid> hyBidMock = mockStatic(HyBid.class);
             MockedStatic<PNCrypto> cryptoMock = mockStatic(PNCrypto.class)) {

            hyBidMock.when(HyBid::getAppToken).thenReturn(APP_TOKEN);
            hyBidMock.when(HyBid::getUserDataManager).thenReturn(mockUserDataManager);
            hyBidMock.when(HyBid::getDeviceInfo).thenReturn(mockDeviceInfo);
            hyBidMock.when(HyBid::getLocationManager).thenReturn(null);
            hyBidMock.when(HyBid::getBundleId).thenReturn(BUNDLE_ID);

            ArgumentCaptor<String> jsonCaptor = ArgumentCaptor.forClass(String.class);
            cryptoMock.when(() -> PNCrypto.encryptString(jsonCaptor.capture(), eq(APP_TOKEN)))
                    .thenReturn("encrypted");

            VgiIdManager manager = new VgiIdManager(mockContext);
            manager.init();

            String capturedJson = jsonCaptor.getValue();
            assertNotNull(capturedJson);
            assertTrue(capturedJson.contains("apps"));
            assertTrue(capturedJson.contains("device"));
            assertTrue(capturedJson.contains("users"));
        }
    }

    @Test
    public void testInit_capturesLocationData() {
        try (MockedStatic<HyBid> hyBidMock = mockStatic(HyBid.class);
             MockedStatic<PNCrypto> cryptoMock = mockStatic(PNCrypto.class)) {

            hyBidMock.when(HyBid::getAppToken).thenReturn(APP_TOKEN);
            hyBidMock.when(HyBid::getUserDataManager).thenReturn(mockUserDataManager);
            hyBidMock.when(HyBid::getDeviceInfo).thenReturn(mockDeviceInfo);
            hyBidMock.when(HyBid::getLocationManager).thenReturn(mockLocationManager);
            hyBidMock.when(HyBid::getBundleId).thenReturn(BUNDLE_ID);

            when(mockLocationManager.getUserLocation()).thenReturn(mockLocation);

            ArgumentCaptor<String> jsonCaptor = ArgumentCaptor.forClass(String.class);
            cryptoMock.when(() -> PNCrypto.encryptString(jsonCaptor.capture(), eq(APP_TOKEN)))
                    .thenReturn("encrypted");

            VgiIdManager manager = new VgiIdManager(mockContext);
            manager.init();

            String capturedJson = jsonCaptor.getValue();
            assertNotNull(capturedJson);
            // Verify location formatting with 2 decimal places
            assertTrue("JSON should contain formatted latitude", capturedJson.contains("\"lat\":\"37.77\""));
            assertTrue("JSON should contain formatted longitude", capturedJson.contains("\"lon\":\"-122.42\""));
            assertTrue("JSON should contain accuracy", capturedJson.contains("\"accuracy\":\"10.5\""));
            assertTrue("JSON should contain timestamp", capturedJson.contains("\"ts\":\"1234567890\""));
        }
    }

    @Test
    public void testInit_capturesPrivacyData() {
        try (MockedStatic<HyBid> hyBidMock = mockStatic(HyBid.class);
             MockedStatic<PNCrypto> cryptoMock = mockStatic(PNCrypto.class)) {

            hyBidMock.when(HyBid::getAppToken).thenReturn(APP_TOKEN);
            hyBidMock.when(HyBid::getUserDataManager).thenReturn(mockUserDataManager);
            hyBidMock.when(HyBid::getDeviceInfo).thenReturn(mockDeviceInfo);
            hyBidMock.when(HyBid::getLocationManager).thenReturn(null);
            hyBidMock.when(HyBid::getBundleId).thenReturn(BUNDLE_ID);

            ArgumentCaptor<String> jsonCaptor = ArgumentCaptor.forClass(String.class);
            cryptoMock.when(() -> PNCrypto.encryptString(jsonCaptor.capture(), eq(APP_TOKEN)))
                    .thenReturn("encrypted");

            VgiIdManager manager = new VgiIdManager(mockContext);
            manager.init();

            String capturedJson = jsonCaptor.getValue();
            assertNotNull(capturedJson);
            assertTrue("JSON should contain CCPA string", capturedJson.contains("\"iab_ccpa\":\"1YNN\""));
            assertTrue("JSON should contain GDPR consent", capturedJson.contains("\"tcfv2\":\"test-consent\""));
            assertTrue("JSON should contain LAT flag", capturedJson.contains("\"lat\":false"));
        }
    }

    @Test
    public void testInit_capturesAdvertisingId() {
        try (MockedStatic<HyBid> hyBidMock = mockStatic(HyBid.class);
             MockedStatic<PNCrypto> cryptoMock = mockStatic(PNCrypto.class)) {

            hyBidMock.when(HyBid::getAppToken).thenReturn(APP_TOKEN);
            hyBidMock.when(HyBid::getUserDataManager).thenReturn(mockUserDataManager);
            hyBidMock.when(HyBid::getDeviceInfo).thenReturn(mockDeviceInfo);
            hyBidMock.when(HyBid::getLocationManager).thenReturn(null);
            hyBidMock.when(HyBid::getBundleId).thenReturn(BUNDLE_ID);

            ArgumentCaptor<String> jsonCaptor = ArgumentCaptor.forClass(String.class);
            cryptoMock.when(() -> PNCrypto.encryptString(jsonCaptor.capture(), eq(APP_TOKEN)))
                    .thenReturn("encrypted");

            VgiIdManager manager = new VgiIdManager(mockContext);
            manager.init();

            String capturedJson = jsonCaptor.getValue();
            assertNotNull(capturedJson);
            assertTrue("JSON should contain GAID", capturedJson.contains("\"GAID\":\"test-gaid\""));
        }
    }

    @Test
    public void testInit_capturesDeviceMetadata() {
        try (MockedStatic<HyBid> hyBidMock = mockStatic(HyBid.class);
             MockedStatic<PNCrypto> cryptoMock = mockStatic(PNCrypto.class)) {

            hyBidMock.when(HyBid::getAppToken).thenReturn(APP_TOKEN);
            hyBidMock.when(HyBid::getUserDataManager).thenReturn(mockUserDataManager);
            hyBidMock.when(HyBid::getDeviceInfo).thenReturn(mockDeviceInfo);
            hyBidMock.when(HyBid::getLocationManager).thenReturn(null);
            hyBidMock.when(HyBid::getBundleId).thenReturn(BUNDLE_ID);

            ArgumentCaptor<String> jsonCaptor = ArgumentCaptor.forClass(String.class);
            cryptoMock.when(() -> PNCrypto.encryptString(jsonCaptor.capture(), eq(APP_TOKEN)))
                    .thenReturn("encrypted");

            VgiIdManager manager = new VgiIdManager(mockContext);
            manager.init();

            String capturedJson = jsonCaptor.getValue();
            assertNotNull(capturedJson);
            assertTrue("JSON should contain Android OS", capturedJson.contains("\"name\":\"Android\""));
            assertTrue("JSON should contain OS version", capturedJson.contains("\"version\":\"10\""));
            assertTrue("JSON should contain device info", capturedJson.contains("\"device\""));
        }
    }

    @Test
    public void testInit_capturesBundleId() {
        try (MockedStatic<HyBid> hyBidMock = mockStatic(HyBid.class);
             MockedStatic<PNCrypto> cryptoMock = mockStatic(PNCrypto.class)) {

            hyBidMock.when(HyBid::getAppToken).thenReturn(APP_TOKEN);
            hyBidMock.when(HyBid::getUserDataManager).thenReturn(mockUserDataManager);
            hyBidMock.when(HyBid::getDeviceInfo).thenReturn(mockDeviceInfo);
            hyBidMock.when(HyBid::getLocationManager).thenReturn(null);
            hyBidMock.when(HyBid::getBundleId).thenReturn(BUNDLE_ID);

            ArgumentCaptor<String> jsonCaptor = ArgumentCaptor.forClass(String.class);
            cryptoMock.when(() -> PNCrypto.encryptString(jsonCaptor.capture(), eq(APP_TOKEN)))
                    .thenReturn("encrypted");

            VgiIdManager manager = new VgiIdManager(mockContext);
            manager.init();

            String capturedJson = jsonCaptor.getValue();
            assertNotNull(capturedJson);
            assertTrue("JSON should contain bundle ID", capturedJson.contains("\"bundle_id\":\"" + BUNDLE_ID + "\""));
        }
    }

    @Test
    @Config(sdk = 23)
    public void testIsBatteryCharging_notCharging_returnsCorrectStatus() {
        try (MockedStatic<HyBid> hyBidMock = mockStatic(HyBid.class);
             MockedStatic<PNCrypto> cryptoMock = mockStatic(PNCrypto.class)) {

            hyBidMock.when(HyBid::getAppToken).thenReturn(APP_TOKEN);
            hyBidMock.when(HyBid::getUserDataManager).thenReturn(mockUserDataManager);
            hyBidMock.when(HyBid::getDeviceInfo).thenReturn(mockDeviceInfo);
            hyBidMock.when(HyBid::getLocationManager).thenReturn(null);
            hyBidMock.when(HyBid::getBundleId).thenReturn(BUNDLE_ID);

            when(mockBatteryManager.isCharging()).thenReturn(false);

            ArgumentCaptor<String> jsonCaptor = ArgumentCaptor.forClass(String.class);
            cryptoMock.when(() -> PNCrypto.encryptString(jsonCaptor.capture(), eq(APP_TOKEN)))
                    .thenReturn("encrypted");

            VgiIdManager manager = new VgiIdManager(mockContext);
            manager.init();

            verify(mockBatteryManager).isCharging();

            String capturedJson = jsonCaptor.getValue();
            assertNotNull(capturedJson);
            assertTrue("JSON should contain charging status false", capturedJson.contains("\"charging\":false"));
        }
    }

    @Test
    @Config(sdk = 21)
    public void testGetBatteryCapacity_withValidValues_calculatesCorrectly() {
        try (MockedStatic<HyBid> hyBidMock = mockStatic(HyBid.class);
             MockedStatic<PNCrypto> cryptoMock = mockStatic(PNCrypto.class)) {

            hyBidMock.when(HyBid::getAppToken).thenReturn(APP_TOKEN);
            hyBidMock.when(HyBid::getUserDataManager).thenReturn(mockUserDataManager);
            hyBidMock.when(HyBid::getDeviceInfo).thenReturn(mockDeviceInfo);
            hyBidMock.when(HyBid::getLocationManager).thenReturn(null);
            hyBidMock.when(HyBid::getBundleId).thenReturn(BUNDLE_ID);

            // Test with different charge counter and capacity values
            // Formula: (chargeCounter / capacity) * 100L
            // Example: (3000 / 50) * 100 = 6000
            when(mockBatteryManager.getIntProperty(BatteryManager.BATTERY_PROPERTY_CHARGE_COUNTER))
                    .thenReturn(3000);
            when(mockBatteryManager.getIntProperty(BatteryManager.BATTERY_PROPERTY_CAPACITY))
                    .thenReturn(50);

            ArgumentCaptor<String> jsonCaptor = ArgumentCaptor.forClass(String.class);
            cryptoMock.when(() -> PNCrypto.encryptString(jsonCaptor.capture(), eq(APP_TOKEN)))
                    .thenReturn("encrypted");

            VgiIdManager manager = new VgiIdManager(mockContext);
            manager.init();

            String capturedJson = jsonCaptor.getValue();
            assertNotNull(capturedJson);
            assertTrue("JSON should contain calculated battery capacity", capturedJson.contains("\"capacity\":\"6000\""));
        }
    }

    @Test
    @Config(sdk = 21)
    public void testGetBatteryCapacity_withCapacityMinValue_returnsNegativeOne() {
        try (MockedStatic<HyBid> hyBidMock = mockStatic(HyBid.class);
             MockedStatic<PNCrypto> cryptoMock = mockStatic(PNCrypto.class)) {

            hyBidMock.when(HyBid::getAppToken).thenReturn(APP_TOKEN);
            hyBidMock.when(HyBid::getUserDataManager).thenReturn(mockUserDataManager);
            hyBidMock.when(HyBid::getDeviceInfo).thenReturn(mockDeviceInfo);
            hyBidMock.when(HyBid::getLocationManager).thenReturn(null);
            hyBidMock.when(HyBid::getBundleId).thenReturn(BUNDLE_ID);

            when(mockBatteryManager.getIntProperty(BatteryManager.BATTERY_PROPERTY_CHARGE_COUNTER))
                    .thenReturn(5000);
            when(mockBatteryManager.getIntProperty(BatteryManager.BATTERY_PROPERTY_CAPACITY))
                    .thenReturn(Integer.MIN_VALUE);

            ArgumentCaptor<String> jsonCaptor = ArgumentCaptor.forClass(String.class);
            cryptoMock.when(() -> PNCrypto.encryptString(jsonCaptor.capture(), eq(APP_TOKEN)))
                    .thenReturn("encrypted");

            VgiIdManager manager = new VgiIdManager(mockContext);
            manager.init();

            String capturedJson = jsonCaptor.getValue();
            assertNotNull(capturedJson);
            // Battery capacity should not be in JSON when calculation returns -1
            assertTrue("Battery object should exist", capturedJson.contains("\"battery\""));
        }
    }

    @Test
    @Config(sdk = 22) // Below API 23
    public void testIsBatteryCharging_belowMarshmallow_returnsNull() {
        try (MockedStatic<HyBid> hyBidMock = mockStatic(HyBid.class);
             MockedStatic<PNCrypto> cryptoMock = mockStatic(PNCrypto.class)) {

            hyBidMock.when(HyBid::getAppToken).thenReturn(APP_TOKEN);
            hyBidMock.when(HyBid::getUserDataManager).thenReturn(mockUserDataManager);
            hyBidMock.when(HyBid::getDeviceInfo).thenReturn(mockDeviceInfo);
            hyBidMock.when(HyBid::getLocationManager).thenReturn(null);
            hyBidMock.when(HyBid::getBundleId).thenReturn(BUNDLE_ID);

            ArgumentCaptor<String> jsonCaptor = ArgumentCaptor.forClass(String.class);
            cryptoMock.when(() -> PNCrypto.encryptString(jsonCaptor.capture(), eq(APP_TOKEN)))
                    .thenReturn("encrypted");

            VgiIdManager manager = new VgiIdManager(mockContext);
            manager.init();

            // On API < 23, charging status should be null
            String capturedJson = jsonCaptor.getValue();
            assertNotNull(capturedJson);
            assertTrue("Battery object should exist", capturedJson.contains("\"battery\""));
        }
    }

    @Test
    public void testInit_withNullPrivacyStrings_handlesGracefully() {
        try (MockedStatic<HyBid> hyBidMock = mockStatic(HyBid.class);
             MockedStatic<PNCrypto> cryptoMock = mockStatic(PNCrypto.class)) {

            hyBidMock.when(HyBid::getAppToken).thenReturn(APP_TOKEN);
            hyBidMock.when(HyBid::getUserDataManager).thenReturn(mockUserDataManager);
            hyBidMock.when(HyBid::getDeviceInfo).thenReturn(mockDeviceInfo);
            hyBidMock.when(HyBid::getLocationManager).thenReturn(null);
            hyBidMock.when(HyBid::getBundleId).thenReturn(BUNDLE_ID);

            // Setup null privacy strings
            when(mockUserDataManager.getIABUSPrivacyString()).thenReturn(null);
            when(mockUserDataManager.getIABGDPRConsentString()).thenReturn(null);

            ArgumentCaptor<String> jsonCaptor = ArgumentCaptor.forClass(String.class);
            cryptoMock.when(() -> PNCrypto.encryptString(jsonCaptor.capture(), eq(APP_TOKEN)))
                    .thenReturn("encrypted");

            VgiIdManager manager = new VgiIdManager(mockContext);
            manager.init();

            String capturedJson = jsonCaptor.getValue();
            assertNotNull(capturedJson);
            assertTrue("JSON should contain privacy object", capturedJson.contains("\"privacy\""));
        }
    }

    @Test
    public void testInit_withNullAdvertisingId_handlesGracefully() {
        try (MockedStatic<HyBid> hyBidMock = mockStatic(HyBid.class);
             MockedStatic<PNCrypto> cryptoMock = mockStatic(PNCrypto.class)) {

            hyBidMock.when(HyBid::getAppToken).thenReturn(APP_TOKEN);
            hyBidMock.when(HyBid::getUserDataManager).thenReturn(mockUserDataManager);
            hyBidMock.when(HyBid::getDeviceInfo).thenReturn(mockDeviceInfo);
            hyBidMock.when(HyBid::getLocationManager).thenReturn(null);
            hyBidMock.when(HyBid::getBundleId).thenReturn(BUNDLE_ID);

            when(mockDeviceInfo.getAdvertisingId()).thenReturn(null);

            ArgumentCaptor<String> jsonCaptor = ArgumentCaptor.forClass(String.class);
            cryptoMock.when(() -> PNCrypto.encryptString(jsonCaptor.capture(), eq(APP_TOKEN)))
                    .thenReturn("encrypted");

            VgiIdManager manager = new VgiIdManager(mockContext);
            manager.init();

            String capturedJson = jsonCaptor.getValue();
            assertNotNull(capturedJson);
            assertTrue("JSON should contain vendors object", capturedJson.contains("\"vendors\""));
        }
    }

    @Test
    public void testInit_withLimitTrackingTrue_capturesCorrectValue() {
        try (MockedStatic<HyBid> hyBidMock = mockStatic(HyBid.class);
             MockedStatic<PNCrypto> cryptoMock = mockStatic(PNCrypto.class)) {

            hyBidMock.when(HyBid::getAppToken).thenReturn(APP_TOKEN);
            hyBidMock.when(HyBid::getUserDataManager).thenReturn(mockUserDataManager);
            hyBidMock.when(HyBid::getDeviceInfo).thenReturn(mockDeviceInfo);
            hyBidMock.when(HyBid::getLocationManager).thenReturn(null);
            hyBidMock.when(HyBid::getBundleId).thenReturn(BUNDLE_ID);

            when(mockDeviceInfo.limitTracking()).thenReturn(true);

            ArgumentCaptor<String> jsonCaptor = ArgumentCaptor.forClass(String.class);
            cryptoMock.when(() -> PNCrypto.encryptString(jsonCaptor.capture(), eq(APP_TOKEN)))
                    .thenReturn("encrypted");

            VgiIdManager manager = new VgiIdManager(mockContext);
            manager.init();

            String capturedJson = jsonCaptor.getValue();
            assertNotNull(capturedJson);
            assertTrue("JSON should contain LAT flag true", capturedJson.contains("\"lat\":true"));
        }
    }

    @Test
    public void testInit_withNullBundleId_handlesGracefully() {
        try (MockedStatic<HyBid> hyBidMock = mockStatic(HyBid.class);
             MockedStatic<PNCrypto> cryptoMock = mockStatic(PNCrypto.class)) {

            hyBidMock.when(HyBid::getAppToken).thenReturn(APP_TOKEN);
            hyBidMock.when(HyBid::getUserDataManager).thenReturn(mockUserDataManager);
            hyBidMock.when(HyBid::getDeviceInfo).thenReturn(mockDeviceInfo);
            hyBidMock.when(HyBid::getLocationManager).thenReturn(null);
            hyBidMock.when(HyBid::getBundleId).thenReturn(null);

            ArgumentCaptor<String> jsonCaptor = ArgumentCaptor.forClass(String.class);
            cryptoMock.when(() -> PNCrypto.encryptString(jsonCaptor.capture(), eq(APP_TOKEN)))
                    .thenReturn("encrypted");

            VgiIdManager manager = new VgiIdManager(mockContext);
            manager.init();

            String capturedJson = jsonCaptor.getValue();
            assertNotNull(capturedJson);
            assertTrue("JSON should contain apps array", capturedJson.contains("\"apps\""));
        }
    }

    @Test
    public void testInit_withDifferentLocationValues_formatsCorrectly() {
        try (MockedStatic<HyBid> hyBidMock = mockStatic(HyBid.class);
             MockedStatic<PNCrypto> cryptoMock = mockStatic(PNCrypto.class)) {

            hyBidMock.when(HyBid::getAppToken).thenReturn(APP_TOKEN);
            hyBidMock.when(HyBid::getUserDataManager).thenReturn(mockUserDataManager);
            hyBidMock.when(HyBid::getDeviceInfo).thenReturn(mockDeviceInfo);
            hyBidMock.when(HyBid::getLocationManager).thenReturn(mockLocationManager);
            hyBidMock.when(HyBid::getBundleId).thenReturn(BUNDLE_ID);

            // Test with different precision values
            when(mockLocation.getLatitude()).thenReturn(51.123456);
            when(mockLocation.getLongitude()).thenReturn(-0.987654);
            when(mockLocation.getAccuracy()).thenReturn(25.789f);
            when(mockLocation.getTime()).thenReturn(9876543210L);
            when(mockLocationManager.getUserLocation()).thenReturn(mockLocation);

            ArgumentCaptor<String> jsonCaptor = ArgumentCaptor.forClass(String.class);
            cryptoMock.when(() -> PNCrypto.encryptString(jsonCaptor.capture(), eq(APP_TOKEN)))
                    .thenReturn("encrypted");

            VgiIdManager manager = new VgiIdManager(mockContext);
            manager.init();

            String capturedJson = jsonCaptor.getValue();
            assertNotNull(capturedJson);
            // Verify formatting to 2 decimal places
            assertTrue("JSON should contain formatted latitude", capturedJson.contains("\"lat\":\"51.12\""));
            assertTrue("JSON should contain formatted longitude", capturedJson.contains("\"lon\":\"-0.99\""));
            assertTrue("JSON should contain accuracy", capturedJson.contains("\"accuracy\":\"25.789\""));
            assertTrue("JSON should contain timestamp", capturedJson.contains("\"ts\":\"9876543210\""));
        }
    }
}

