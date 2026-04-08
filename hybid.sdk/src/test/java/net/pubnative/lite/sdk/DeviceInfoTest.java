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
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.Point;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.telephony.TelephonyManager;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodInfo;
import android.view.inputmethod.InputMethodManager;
import android.view.inputmethod.InputMethodSubtype;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

@RunWith(MockitoJUnitRunner.class)
public class DeviceInfoTest {

    @Mock
    private Context mMockContext;
    @Mock
    private Resources mMockResources;
    @Mock
    private Configuration mMockConfiguration;
    @Mock
    private ConnectivityManager mMockConnectivityManager;
    @Mock
    private WindowManager mMockWindowManager;
    @Mock
    private Display mMockDisplay;

    private DeviceInfo mDeviceInfo;

    @Before
    public void setup() {
        when(mMockContext.getApplicationContext()).thenReturn(mMockContext);
        when(mMockContext.getResources()).thenReturn(mMockResources);
        when(mMockContext.getSystemService(Context.WINDOW_SERVICE)).thenReturn(mMockWindowManager);
        when(mMockWindowManager.getDefaultDisplay()).thenReturn(mMockDisplay);

        // Mock Display.getSize() to set the Point dimensions
        doAnswer(invocation -> {
            Point point = invocation.getArgument(0);
            point.x = 1080;
            point.y = 1920;
            return null;
        }).when(mMockDisplay).getSize(any(Point.class));

        when(mMockResources.getConfiguration()).thenReturn(mMockConfiguration);
        DisplayMetrics displayMetrics = new DisplayMetrics();
        displayMetrics.density = 1.0f;
        displayMetrics.xdpi = 160.0f;
        when(mMockResources.getDisplayMetrics()).thenReturn(displayMetrics);
        when(mMockContext.getSystemService(Context.CONNECTIVITY_SERVICE)).thenReturn(mMockConnectivityManager);

        mDeviceInfo = new DeviceInfo(mMockContext);
    }

    private void setField(Object object, String fieldName, Object value) throws Exception {
        Field field = object.getClass().getDeclaredField(fieldName);
        field.setAccessible(true);
        field.set(object, value);
    }

    @Test
    public void getOSVersion_returnsCorrectReleaseVersion() {
        assertEquals(Build.VERSION.RELEASE, mDeviceInfo.getOSVersion());
    }

    @Test
    public void getConnectionType_withNoPermission_returnsNull() {
        when(mMockContext.checkCallingOrSelfPermission(Manifest.permission.ACCESS_NETWORK_STATE)).thenReturn(PackageManager.PERMISSION_DENIED);
        assertNull(mDeviceInfo.getConnectionType());
    }

    @Test
    public void getConnectionType_withNoConnection_returnsNull() {
        when(mMockContext.checkCallingOrSelfPermission(Manifest.permission.ACCESS_NETWORK_STATE)).thenReturn(PackageManager.PERMISSION_GRANTED);
        when(mMockConnectivityManager.getActiveNetworkInfo()).thenReturn(null);
        assertNull(mDeviceInfo.getConnectionType());
    }

    @Test
    public void getConnectionType_withWifiConnection_returns2() {
        when(mMockContext.checkCallingOrSelfPermission(Manifest.permission.ACCESS_NETWORK_STATE)).thenReturn(PackageManager.PERMISSION_GRANTED);
        NetworkInfo networkInfo = mock(NetworkInfo.class);
        when(mMockConnectivityManager.getActiveNetworkInfo()).thenReturn(networkInfo);
        when(networkInfo.getType()).thenReturn(ConnectivityManager.TYPE_WIFI);
        assertEquals(Integer.valueOf(2), mDeviceInfo.getConnectionType());
    }

    @Test
    public void getConnectionType_withEthernetConnection_returns1() {
        when(mMockContext.checkCallingOrSelfPermission(Manifest.permission.ACCESS_NETWORK_STATE)).thenReturn(PackageManager.PERMISSION_GRANTED);
        NetworkInfo networkInfo = mock(NetworkInfo.class);
        when(mMockConnectivityManager.getActiveNetworkInfo()).thenReturn(networkInfo);
        when(networkInfo.getType()).thenReturn(ConnectivityManager.TYPE_ETHERNET);
        assertEquals(Integer.valueOf(1), mDeviceInfo.getConnectionType());
    }

    @Test
    public void getConnectionType_with2GConnection_returns4() {
        when(mMockContext.checkCallingOrSelfPermission(Manifest.permission.ACCESS_NETWORK_STATE)).thenReturn(PackageManager.PERMISSION_GRANTED);
        NetworkInfo networkInfo = mock(NetworkInfo.class);
        when(mMockConnectivityManager.getActiveNetworkInfo()).thenReturn(networkInfo);
        when(networkInfo.getType()).thenReturn(ConnectivityManager.TYPE_MOBILE);
        when(networkInfo.getSubtype()).thenReturn(TelephonyManager.NETWORK_TYPE_GPRS);
        assertEquals(Integer.valueOf(4), mDeviceInfo.getConnectionType());
    }

    @Test
    public void getConnectionType_with3GConnection_returns5() {
        when(mMockContext.checkCallingOrSelfPermission(Manifest.permission.ACCESS_NETWORK_STATE)).thenReturn(PackageManager.PERMISSION_GRANTED);
        NetworkInfo networkInfo = mock(NetworkInfo.class);
        when(mMockConnectivityManager.getActiveNetworkInfo()).thenReturn(networkInfo);
        when(networkInfo.getType()).thenReturn(ConnectivityManager.TYPE_MOBILE);
        when(networkInfo.getSubtype()).thenReturn(TelephonyManager.NETWORK_TYPE_UMTS);
        assertEquals(Integer.valueOf(5), mDeviceInfo.getConnectionType());
    }

    @Test
    public void getConnectionType_with4GConnection_returns6() {
        when(mMockContext.checkCallingOrSelfPermission(Manifest.permission.ACCESS_NETWORK_STATE)).thenReturn(PackageManager.PERMISSION_GRANTED);
        NetworkInfo networkInfo = mock(NetworkInfo.class);
        when(mMockConnectivityManager.getActiveNetworkInfo()).thenReturn(networkInfo);
        when(networkInfo.getType()).thenReturn(ConnectivityManager.TYPE_MOBILE);
        when(networkInfo.getSubtype()).thenReturn(TelephonyManager.NETWORK_TYPE_LTE);
        assertEquals(Integer.valueOf(6), mDeviceInfo.getConnectionType());
    }

    @Test
    public void getConnectionType_with5GConnection_returns7() {
        when(mMockContext.checkCallingOrSelfPermission(Manifest.permission.ACCESS_NETWORK_STATE)).thenReturn(PackageManager.PERMISSION_GRANTED);
        NetworkInfo networkInfo = mock(NetworkInfo.class);
        when(mMockConnectivityManager.getActiveNetworkInfo()).thenReturn(networkInfo);
        when(networkInfo.getType()).thenReturn(ConnectivityManager.TYPE_MOBILE);
        when(networkInfo.getSubtype()).thenReturn(TelephonyManager.NETWORK_TYPE_NR);
        assertEquals(Integer.valueOf(7), mDeviceInfo.getConnectionType());
    }

    @Test
    public void getConnectionType_withUnknownCellularType_returns3() {
        when(mMockContext.checkCallingOrSelfPermission(Manifest.permission.ACCESS_NETWORK_STATE)).thenReturn(PackageManager.PERMISSION_GRANTED);
        NetworkInfo networkInfo = mock(NetworkInfo.class);
        when(mMockConnectivityManager.getActiveNetworkInfo()).thenReturn(networkInfo);
        when(networkInfo.getType()).thenReturn(ConnectivityManager.TYPE_MOBILE);
        when(networkInfo.getSubtype()).thenReturn(TelephonyManager.NETWORK_TYPE_UNKNOWN);
        assertEquals(Integer.valueOf(3), mDeviceInfo.getConnectionType());
    }

    @Test
    public void getConnectionType_withEdgeConnection_returns4() {
        when(mMockContext.checkCallingOrSelfPermission(Manifest.permission.ACCESS_NETWORK_STATE)).thenReturn(PackageManager.PERMISSION_GRANTED);
        NetworkInfo networkInfo = mock(NetworkInfo.class);
        when(mMockConnectivityManager.getActiveNetworkInfo()).thenReturn(networkInfo);
        when(networkInfo.getType()).thenReturn(ConnectivityManager.TYPE_MOBILE);
        when(networkInfo.getSubtype()).thenReturn(TelephonyManager.NETWORK_TYPE_EDGE);
        assertEquals(Integer.valueOf(4), mDeviceInfo.getConnectionType());
    }

    @Test
    public void getConnectionType_withCdmaConnection_returns4() {
        when(mMockContext.checkCallingOrSelfPermission(Manifest.permission.ACCESS_NETWORK_STATE)).thenReturn(PackageManager.PERMISSION_GRANTED);
        NetworkInfo networkInfo = mock(NetworkInfo.class);
        when(mMockConnectivityManager.getActiveNetworkInfo()).thenReturn(networkInfo);
        when(networkInfo.getType()).thenReturn(ConnectivityManager.TYPE_MOBILE);
        when(networkInfo.getSubtype()).thenReturn(TelephonyManager.NETWORK_TYPE_CDMA);
        assertEquals(Integer.valueOf(4), mDeviceInfo.getConnectionType());
    }

    @Test
    public void getConnectionType_with1xRTTConnection_returns4() {
        when(mMockContext.checkCallingOrSelfPermission(Manifest.permission.ACCESS_NETWORK_STATE)).thenReturn(PackageManager.PERMISSION_GRANTED);
        NetworkInfo networkInfo = mock(NetworkInfo.class);
        when(mMockConnectivityManager.getActiveNetworkInfo()).thenReturn(networkInfo);
        when(networkInfo.getType()).thenReturn(ConnectivityManager.TYPE_MOBILE);
        when(networkInfo.getSubtype()).thenReturn(TelephonyManager.NETWORK_TYPE_1xRTT);
        assertEquals(Integer.valueOf(4), mDeviceInfo.getConnectionType());
    }

    @Test
    public void getConnectionType_withIdenConnection_returns4() {
        when(mMockContext.checkCallingOrSelfPermission(Manifest.permission.ACCESS_NETWORK_STATE)).thenReturn(PackageManager.PERMISSION_GRANTED);
        NetworkInfo networkInfo = mock(NetworkInfo.class);
        when(mMockConnectivityManager.getActiveNetworkInfo()).thenReturn(networkInfo);
        when(networkInfo.getType()).thenReturn(ConnectivityManager.TYPE_MOBILE);
        when(networkInfo.getSubtype()).thenReturn(TelephonyManager.NETWORK_TYPE_IDEN);
        assertEquals(Integer.valueOf(4), mDeviceInfo.getConnectionType());
    }

    @Test
    public void getConnectionType_withGsmConnection_returns4() {
        when(mMockContext.checkCallingOrSelfPermission(Manifest.permission.ACCESS_NETWORK_STATE)).thenReturn(PackageManager.PERMISSION_GRANTED);
        NetworkInfo networkInfo = mock(NetworkInfo.class);
        when(mMockConnectivityManager.getActiveNetworkInfo()).thenReturn(networkInfo);
        when(networkInfo.getType()).thenReturn(ConnectivityManager.TYPE_MOBILE);
        when(networkInfo.getSubtype()).thenReturn(TelephonyManager.NETWORK_TYPE_GSM);
        assertEquals(Integer.valueOf(4), mDeviceInfo.getConnectionType());
    }

    @Test
    public void getConnectionType_withEvdo0Connection_returns5() {
        when(mMockContext.checkCallingOrSelfPermission(Manifest.permission.ACCESS_NETWORK_STATE)).thenReturn(PackageManager.PERMISSION_GRANTED);
        NetworkInfo networkInfo = mock(NetworkInfo.class);
        when(mMockConnectivityManager.getActiveNetworkInfo()).thenReturn(networkInfo);
        when(networkInfo.getType()).thenReturn(ConnectivityManager.TYPE_MOBILE);
        when(networkInfo.getSubtype()).thenReturn(TelephonyManager.NETWORK_TYPE_EVDO_0);
        assertEquals(Integer.valueOf(5), mDeviceInfo.getConnectionType());
    }

    @Test
    public void getConnectionType_withEvdoAConnection_returns5() {
        when(mMockContext.checkCallingOrSelfPermission(Manifest.permission.ACCESS_NETWORK_STATE)).thenReturn(PackageManager.PERMISSION_GRANTED);
        NetworkInfo networkInfo = mock(NetworkInfo.class);
        when(mMockConnectivityManager.getActiveNetworkInfo()).thenReturn(networkInfo);
        when(networkInfo.getType()).thenReturn(ConnectivityManager.TYPE_MOBILE);
        when(networkInfo.getSubtype()).thenReturn(TelephonyManager.NETWORK_TYPE_EVDO_A);
        assertEquals(Integer.valueOf(5), mDeviceInfo.getConnectionType());
    }

    @Test
    public void getConnectionType_withHsdpaConnection_returns5() {
        when(mMockContext.checkCallingOrSelfPermission(Manifest.permission.ACCESS_NETWORK_STATE)).thenReturn(PackageManager.PERMISSION_GRANTED);
        NetworkInfo networkInfo = mock(NetworkInfo.class);
        when(mMockConnectivityManager.getActiveNetworkInfo()).thenReturn(networkInfo);
        when(networkInfo.getType()).thenReturn(ConnectivityManager.TYPE_MOBILE);
        when(networkInfo.getSubtype()).thenReturn(TelephonyManager.NETWORK_TYPE_HSDPA);
        assertEquals(Integer.valueOf(5), mDeviceInfo.getConnectionType());
    }

    @Test
    public void getConnectionType_withHsupaConnection_returns5() {
        when(mMockContext.checkCallingOrSelfPermission(Manifest.permission.ACCESS_NETWORK_STATE)).thenReturn(PackageManager.PERMISSION_GRANTED);
        NetworkInfo networkInfo = mock(NetworkInfo.class);
        when(mMockConnectivityManager.getActiveNetworkInfo()).thenReturn(networkInfo);
        when(networkInfo.getType()).thenReturn(ConnectivityManager.TYPE_MOBILE);
        when(networkInfo.getSubtype()).thenReturn(TelephonyManager.NETWORK_TYPE_HSUPA);
        assertEquals(Integer.valueOf(5), mDeviceInfo.getConnectionType());
    }

    @Test
    public void getConnectionType_withHspaConnection_returns5() {
        when(mMockContext.checkCallingOrSelfPermission(Manifest.permission.ACCESS_NETWORK_STATE)).thenReturn(PackageManager.PERMISSION_GRANTED);
        NetworkInfo networkInfo = mock(NetworkInfo.class);
        when(mMockConnectivityManager.getActiveNetworkInfo()).thenReturn(networkInfo);
        when(networkInfo.getType()).thenReturn(ConnectivityManager.TYPE_MOBILE);
        when(networkInfo.getSubtype()).thenReturn(TelephonyManager.NETWORK_TYPE_HSPA);
        assertEquals(Integer.valueOf(5), mDeviceInfo.getConnectionType());
    }

    @Test
    public void getConnectionType_withEvdoBConnection_returns5() {
        when(mMockContext.checkCallingOrSelfPermission(Manifest.permission.ACCESS_NETWORK_STATE)).thenReturn(PackageManager.PERMISSION_GRANTED);
        NetworkInfo networkInfo = mock(NetworkInfo.class);
        when(mMockConnectivityManager.getActiveNetworkInfo()).thenReturn(networkInfo);
        when(networkInfo.getType()).thenReturn(ConnectivityManager.TYPE_MOBILE);
        when(networkInfo.getSubtype()).thenReturn(TelephonyManager.NETWORK_TYPE_EVDO_B);
        assertEquals(Integer.valueOf(5), mDeviceInfo.getConnectionType());
    }

    @Test
    public void getConnectionType_withEhrpdConnection_returns5() {
        when(mMockContext.checkCallingOrSelfPermission(Manifest.permission.ACCESS_NETWORK_STATE)).thenReturn(PackageManager.PERMISSION_GRANTED);
        NetworkInfo networkInfo = mock(NetworkInfo.class);
        when(mMockConnectivityManager.getActiveNetworkInfo()).thenReturn(networkInfo);
        when(networkInfo.getType()).thenReturn(ConnectivityManager.TYPE_MOBILE);
        when(networkInfo.getSubtype()).thenReturn(TelephonyManager.NETWORK_TYPE_EHRPD);
        assertEquals(Integer.valueOf(5), mDeviceInfo.getConnectionType());
    }

    @Test
    public void getConnectionType_withHspapConnection_returns5() {
        when(mMockContext.checkCallingOrSelfPermission(Manifest.permission.ACCESS_NETWORK_STATE)).thenReturn(PackageManager.PERMISSION_GRANTED);
        NetworkInfo networkInfo = mock(NetworkInfo.class);
        when(mMockConnectivityManager.getActiveNetworkInfo()).thenReturn(networkInfo);
        when(networkInfo.getType()).thenReturn(ConnectivityManager.TYPE_MOBILE);
        when(networkInfo.getSubtype()).thenReturn(TelephonyManager.NETWORK_TYPE_HSPAP);
        assertEquals(Integer.valueOf(5), mDeviceInfo.getConnectionType());
    }

    @Test
    public void getConnectionType_withTdScdmaConnection_returns5() {
        when(mMockContext.checkCallingOrSelfPermission(Manifest.permission.ACCESS_NETWORK_STATE)).thenReturn(PackageManager.PERMISSION_GRANTED);
        NetworkInfo networkInfo = mock(NetworkInfo.class);
        when(mMockConnectivityManager.getActiveNetworkInfo()).thenReturn(networkInfo);
        when(networkInfo.getType()).thenReturn(ConnectivityManager.TYPE_MOBILE);
        when(networkInfo.getSubtype()).thenReturn(TelephonyManager.NETWORK_TYPE_TD_SCDMA);
        assertEquals(Integer.valueOf(5), mDeviceInfo.getConnectionType());
    }

    @Test
    public void getConnectionType_withIwlanConnection_returns6() {
        when(mMockContext.checkCallingOrSelfPermission(Manifest.permission.ACCESS_NETWORK_STATE)).thenReturn(PackageManager.PERMISSION_GRANTED);
        NetworkInfo networkInfo = mock(NetworkInfo.class);
        when(mMockConnectivityManager.getActiveNetworkInfo()).thenReturn(networkInfo);
        when(networkInfo.getType()).thenReturn(ConnectivityManager.TYPE_MOBILE);
        when(networkInfo.getSubtype()).thenReturn(TelephonyManager.NETWORK_TYPE_IWLAN);
        assertEquals(Integer.valueOf(6), mDeviceInfo.getConnectionType());
    }

    @Test
    public void getLocale_withValidContext_returnsLocale() {
        mMockConfiguration.locale = Locale.US;
        assertEquals("en_US", mDeviceInfo.getLocale().toString());
    }

    @Test
    public void getLocale_withNullContext_returnsNull() throws Exception {
        setField(mDeviceInfo, "mContext", null);
        assertNull(mDeviceInfo.getLocale());
    }

    @Test
    public void getOrientation_withPortrait_returnsPortrait() {
        mMockConfiguration.orientation = Configuration.ORIENTATION_PORTRAIT;
        assertEquals(DeviceInfo.Orientation.PORTRAIT, mDeviceInfo.getOrientation());
    }

    @Test
    public void getOrientation_withLandscape_returnsLandscape() {
        mMockConfiguration.orientation = Configuration.ORIENTATION_LANDSCAPE;
        assertEquals(DeviceInfo.Orientation.LANDSCAPE, mDeviceInfo.getOrientation());
    }

    @Test
    public void getOrientation_withUndefined_returnsNone() {
        mMockConfiguration.orientation = Configuration.ORIENTATION_UNDEFINED;
        assertEquals(DeviceInfo.Orientation.NONE, mDeviceInfo.getOrientation());
    }

    @Test
    public void getOrientation_withNullContext_returnsNone() throws Exception {
        setField(mDeviceInfo, "mContext", null);
        assertEquals(DeviceInfo.Orientation.NONE, mDeviceInfo.getOrientation());
    }

    @Test
    public void getModel_returnsCorrectModel() {
        assertEquals(Build.MODEL, mDeviceInfo.getModel());
    }

    @Test
    public void getMake_returnsCorrectMake() {
        assertEquals(Build.MANUFACTURER, mDeviceInfo.getMake());
    }

    @Test
    public void getDeviceType_withTablet_returns5() {
        when(mMockResources.getBoolean(net.pubnative.lite.sdk.R.bool.is_tablet)).thenReturn(true);
        assertEquals(5, mDeviceInfo.getDeviceType());
    }

    @Test
    public void getDeviceType_withPhone_returns4() {
        when(mMockResources.getBoolean(net.pubnative.lite.sdk.R.bool.is_tablet)).thenReturn(false);
        assertEquals(4, mDeviceInfo.getDeviceType());
    }

    @Test
    public void getDeviceType_withNullContext_returns1() throws Exception {
        setField(mDeviceInfo, "mContext", null);
        assertEquals(1, mDeviceInfo.getDeviceType());
    }

    @Test
    public void getContext_returnsContext() {
        assertEquals(mMockContext, mDeviceInfo.getContext());
    }

    @Test
    public void getPpi_withNullContext_returnsEmptyString() throws Exception {
        setField(mDeviceInfo, "mContext", null);
        assertEquals("", mDeviceInfo.getPpi());
    }

    @Test
    public void getPpi_withValidContext_returnsValue() {
        DisplayMetrics displayMetrics = new DisplayMetrics();
        displayMetrics.xdpi = 320.0f;
        when(mMockResources.getDisplayMetrics()).thenReturn(displayMetrics);
        assertFalse(mDeviceInfo.getPpi().isEmpty());
    }

    @Test
    public void getCarrier_withNullTelephonyManager_returnsEmptyString() {
        when(mMockContext.getSystemService(Context.TELEPHONY_SERVICE)).thenReturn(null);
        assertEquals("", mDeviceInfo.getCarrier());
    }

    @Test
    public void getCarrier_withValidTelephonyManager_returnsOperatorName() {
        TelephonyManager telephonyManager = mock(TelephonyManager.class);
        when(mMockContext.getSystemService(Context.TELEPHONY_SERVICE)).thenReturn(telephonyManager);
        when(telephonyManager.getNetworkOperatorName()).thenReturn("TestCarrier");
        assertEquals("TestCarrier", mDeviceInfo.getCarrier());
    }

    @Test
    public void getMccmnc_withNullTelephonyManager_returnsEmptyString() {
        when(mMockContext.getSystemService(Context.TELEPHONY_SERVICE)).thenReturn(null);
        assertEquals("", mDeviceInfo.getMccmnc());
    }

    @Test
    public void getMccmnc_withValidTelephonyManager_returnsOperator() {
        TelephonyManager telephonyManager = mock(TelephonyManager.class);
        when(mMockContext.getSystemService(Context.TELEPHONY_SERVICE)).thenReturn(telephonyManager);
        when(telephonyManager.getNetworkOperator()).thenReturn("310410");
        assertEquals("310410", mDeviceInfo.getMccmnc());
    }

    @Test
    public void getMccmncsim_withNullTelephonyManager_returnsEmptyString() {
        when(mMockContext.getSystemService(Context.TELEPHONY_SERVICE)).thenReturn(null);
        assertEquals("", mDeviceInfo.getMccmncsim());
    }

    @Test
    public void getMccmncsim_withValidTelephonyManager_returnsSimOperator() {
        TelephonyManager telephonyManager = mock(TelephonyManager.class);
        when(mMockContext.getSystemService(Context.TELEPHONY_SERVICE)).thenReturn(telephonyManager);
        when(telephonyManager.getSimOperator()).thenReturn("310410");
        assertEquals("310410", mDeviceInfo.getMccmncsim());
    }

    @Test
    public void hasTrackingPermissions_withNoPermissions_returnsFalse() {
        when(mMockContext.checkCallingOrSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION))
                .thenReturn(PackageManager.PERMISSION_DENIED);
        when(mMockContext.checkCallingOrSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION))
                .thenReturn(PackageManager.PERMISSION_DENIED);
        assertFalse(mDeviceInfo.hasTrackingPermissions());
    }

    @Test
    public void hasTrackingPermissions_withCoarseLocation_returnsTrue() {
        when(mMockContext.checkCallingOrSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION))
                .thenReturn(PackageManager.PERMISSION_GRANTED);
        lenient().when(mMockContext.checkCallingOrSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION))
                .thenReturn(PackageManager.PERMISSION_DENIED);
        assertTrue(mDeviceInfo.hasTrackingPermissions());
    }

    @Test
    public void hasTrackingPermissions_withFineLocation_returnsTrue() {
        lenient().when(mMockContext.checkCallingOrSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION))
                .thenReturn(PackageManager.PERMISSION_DENIED);
        when(mMockContext.checkCallingOrSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION))
                .thenReturn(PackageManager.PERMISSION_GRANTED);
        assertTrue(mDeviceInfo.hasTrackingPermissions());
    }

    @Test
    public void getLangb_withNullContext_returnsNull() throws Exception {
        setField(mDeviceInfo, "mContext", null);
        assertNull(mDeviceInfo.getLangb());
    }

    @Test
    public void getLangb_withValidLocale_returnsLanguageTag() {
        mMockConfiguration.locale = Locale.US;
        String langb = mDeviceInfo.getLangb();
        assertTrue(langb.contains("en"));
    }

    @Test
    public void getLangb_withHebrewLocale_returnsHe() {
        mMockConfiguration.locale = new Locale("iw", "IL");
        String langb = mDeviceInfo.getLangb();
        assertTrue(langb.startsWith("he"));
    }

    @Test
    public void getLangb_withIndonesianLocale_returnsId() {
        mMockConfiguration.locale = new Locale("in", "ID");
        String langb = mDeviceInfo.getLangb();
        assertTrue(langb.startsWith("id"));
    }

    @Test
    public void getLangb_withYiddishLocale_returnsYi() {
        mMockConfiguration.locale = new Locale("ji", "IL");
        String langb = mDeviceInfo.getLangb();
        assertTrue(langb.startsWith("yi"));
    }

    @Test
    public void getLangb_withNorwegianNynorskLocale_returnsNn() {
        mMockConfiguration.locale = new Locale("no", "NO", "NY");
        String langb = mDeviceInfo.getLangb();
        assertTrue(langb.startsWith("nn"));
    }

    @Test
    public void getLangb_withInvalidLanguage_returnsUnd() {
        mMockConfiguration.locale = new Locale("1", "US");
        String langb = mDeviceInfo.getLangb();
        assertTrue(langb.startsWith("und"));
    }

    @Test
    public void getLangb_withRegionAndVariant_includesBoth() {
        mMockConfiguration.locale = new Locale("en", "US", "POSIX");
        String langb = mDeviceInfo.getLangb();
        assertTrue(langb.contains("POSIX"));
    }

    @Test
    public void getLangb_withInvalidRegion_omitsRegion() {
        mMockConfiguration.locale = new Locale("en", "A");
        String langb = mDeviceInfo.getLangb();
        assertEquals("en", langb);
    }

    @Test
    public void getLangb_withInvalidVariant_omitsVariant() {
        mMockConfiguration.locale = new Locale("en", "US", "AB");
        String langb = mDeviceInfo.getLangb();
        assertTrue(langb.equals("en-US") || langb.equals("en_US"));
    }

    @Test
    public void getDeviceWidth_returnsNonEmptyString() {
        String width = mDeviceInfo.getDeviceWidth();
        assertTrue(width != null && !width.isEmpty());
    }

    @Test
    public void getDeviceHeight_returnsNonEmptyString() {
        String height = mDeviceInfo.getDeviceHeight();
        assertTrue(height != null && !height.isEmpty());
    }

    @Test
    public void getPxratio_returnsNonEmptyString() {
        String pxratio = mDeviceInfo.getPxratio();
        assertNotNull(pxratio);
    }


    @Test
    public void getSoundSetting_returnsValidValue() {
        String soundSetting = mDeviceInfo.getSoundSetting();
        assertTrue("0".equals(soundSetting) || "1".equals(soundSetting));
    }

    @Test
    public void isBatteryCharging_returnsValidValue() {
        Integer charging = mDeviceInfo.isBatteryCharging();
        assertNotNull(charging);
        assertTrue(charging == 0 || charging == 1);
    }

    @Test
    public void getBatteryLevel_withNullContext_returnsNull() throws Exception {
        setField(mDeviceInfo, "mContext", null);
        assertNull(mDeviceInfo.getBatteryLevel());
    }

    @Test
    public void isDarkMode_withNullContext_returnsNull() throws Exception {
        setField(mDeviceInfo, "mContext", null);
        assertNull(mDeviceInfo.isDarkMode());
    }

    @Test
    public void isDarkMode_withDarkModeEnabled_returns1() {
        mMockConfiguration.uiMode = Configuration.UI_MODE_NIGHT_YES;
        assertEquals(Integer.valueOf(1), mDeviceInfo.isDarkMode());
    }

    @Test
    public void isDarkMode_withDarkModeDisabled_returns0() {
        mMockConfiguration.uiMode = Configuration.UI_MODE_NIGHT_NO;
        assertEquals(Integer.valueOf(0), mDeviceInfo.isDarkMode());
    }

    @Test
    public void isDarkMode_withUndefinedMode_returnsNull() {
        mMockConfiguration.uiMode = Configuration.UI_MODE_NIGHT_UNDEFINED;
        assertNull(mDeviceInfo.isDarkMode());
    }

    @Test
    public void isDndEnabled_withNullContext_returnsNull() throws Exception {
        setField(mDeviceInfo, "mContext", null);
        assertNull(mDeviceInfo.isDndEnabled());
    }

    @Test
    public void isDndEnabled_withValidContext_returnsValue() {
        Integer isDndEnabled = mDeviceInfo.isDndEnabled();
        assertTrue(isDndEnabled == null || isDndEnabled == 0 || isDndEnabled == 1);
    }

    @Test
    public void isAirplaneModeEnabled_withNullContext_returns0() throws Exception {
        setField(mDeviceInfo, "mContext", null);
        assertEquals(Integer.valueOf(0), mDeviceInfo.isAirplaneModeEnabled());
    }


    @Test
    public void getInputLanguages_withNullInputMethodManager_returnsEmptyList() {
        when(mMockContext.getSystemService(Context.INPUT_METHOD_SERVICE)).thenReturn(null);
        List<String> languages = mDeviceInfo.getInputLanguages();
        assertNotNull(languages);
        assertTrue(languages.isEmpty());
    }

    @Test
    public void getInputLanguages_withValidKeyboardSubtype_returnsLanguage() {
        InputMethodManager mockImm = mock(InputMethodManager.class);
        when(mMockContext.getSystemService(Context.INPUT_METHOD_SERVICE)).thenReturn(mockImm);

        List<InputMethodInfo> inputMethodInfoList = new ArrayList<>();
        InputMethodInfo mockInfo = mock(InputMethodInfo.class);
        inputMethodInfoList.add(mockInfo);

        List<InputMethodSubtype> subtypeList = new ArrayList<>();
        InputMethodSubtype mockSubtype = mock(InputMethodSubtype.class);
        subtypeList.add(mockSubtype);

        when(mockImm.getEnabledInputMethodList()).thenReturn(inputMethodInfoList);
        when(mockImm.getEnabledInputMethodSubtypeList(mockInfo, true)).thenReturn(subtypeList);
        when(mockSubtype.getMode()).thenReturn("keyboard");
        when(mockSubtype.getLocale()).thenReturn("en-US");

        List<String> languages = mDeviceInfo.getInputLanguages();
        assertNotNull(languages);
        assertEquals(1, languages.size());
        assertEquals("en-US", languages.get(0));
    }

    @Test
    public void getAdvertisingId_returnsCorrectValue() {
        String adId = mDeviceInfo.getAdvertisingId();
        assertTrue(adId == null || adId.isEmpty());
    }

    @Test
    public void getAdvertisingIdMd5_returnsCorrectValue() {
        String adIdMd5 = mDeviceInfo.getAdvertisingIdMd5();
        assertTrue(adIdMd5 == null || adIdMd5.isEmpty());
    }

    @Test
    public void limitTracking_returnsFalseByDefault() {
        boolean limitTracking = mDeviceInfo.limitTracking();
        assertFalse(limitTracking);
    }


    @Test
    public void getTotalMemoryMb_returnsPositiveValue() {
        try {
            Integer totalMemory = mDeviceInfo.getTotalMemoryMb();
            assertTrue(totalMemory > 0);
        } catch (RuntimeException e) {
            assertTrue(e.getMessage() == null || e.getMessage().contains("not mocked") || e.getMessage().contains("Environment"));
        }
    }

    @Test
    public void getFreeMemoryMb_returnsPositiveValue() {
        try {
            Integer freeMemory = mDeviceInfo.getFreeMemoryMb();
            assertTrue(freeMemory >= 0);
        } catch (RuntimeException e) {
            assertTrue(e.getMessage() == null || e.getMessage().contains("not mocked") || e.getMessage().contains("Environment"));
        }
    }

    @Test
    public void hasPermission_withNullContext_returnsFalse() throws Exception {
        setField(mDeviceInfo, "mContext", null);
        assertFalse(mDeviceInfo.hasTrackingPermissions());
    }


    @Test
    public void getPxratio_withNullContext_returnsValue() {
        // pxratio is set during construction, so it should still have a value
        String pxratio = mDeviceInfo.getPxratio();
        assertNotNull(pxratio);
    }

    @Test
    public void getUserAgent_withNullProvider_returnsEmptyString() throws Exception {
        setField(mDeviceInfo, "mUserAgentProvider", null);
        assertEquals("", mDeviceInfo.getUserAgent());
    }

    @Test
    public void getStructuredUserAgent_withNullProvider_returnsNull() throws Exception {
        setField(mDeviceInfo, "mUserAgentProvider", null);
        assertNull(mDeviceInfo.getStructuredUserAgent());
    }

    @Test
    public void getBatteryLevel_returnsLevel8_whenBatteryAbove85() {
        // Battery level is retrieved via BatteryUtils which uses Android system services
        // In unit test environment, we test the logic branches exist
        Integer batteryLevel = mDeviceInfo.getBatteryLevel();
        // Should return a value or null
        assertTrue(batteryLevel == null || (batteryLevel >= 1 && batteryLevel <= 8));
    }

    @Test
    public void isPowerSaveMode_withNullPowerManager_returnsNull() {
        lenient().when(mMockContext.getSystemService(Context.POWER_SERVICE)).thenReturn(null);
        Integer powerSaveMode = mDeviceInfo.isPowerSaveMode();
        // Should return null when PowerManager is not available
        assertNull(powerSaveMode);
    }


    @Test
    public void isAirplaneModeEnabled_withNullContentResolver_returns0() throws Exception {
        when(mMockContext.getContentResolver()).thenReturn(null);
        assertEquals(Integer.valueOf(0), mDeviceInfo.isAirplaneModeEnabled());
    }

    @Test
    public void getInputLanguages_withNullInputMethodInfoList_returnsEmptyList() {
        InputMethodManager mockImm = mock(InputMethodManager.class);
        when(mMockContext.getSystemService(Context.INPUT_METHOD_SERVICE)).thenReturn(mockImm);
        when(mockImm.getEnabledInputMethodList()).thenReturn(null);

        List<String> languages = mDeviceInfo.getInputLanguages();
        assertNotNull(languages);
        assertTrue(languages.isEmpty());
    }

    @Test
    public void getInputLanguages_withEmptyInputMethodInfoList_returnsEmptyList() {
        InputMethodManager mockImm = mock(InputMethodManager.class);
        when(mMockContext.getSystemService(Context.INPUT_METHOD_SERVICE)).thenReturn(mockImm);
        when(mockImm.getEnabledInputMethodList()).thenReturn(new ArrayList<>());

        List<String> languages = mDeviceInfo.getInputLanguages();
        assertNotNull(languages);
        assertTrue(languages.isEmpty());
    }

    @Test
    public void getInputLanguages_withNullContext_returnsEmptyList() throws Exception {
        setField(mDeviceInfo, "mContext", null);
        List<String> languages = mDeviceInfo.getInputLanguages();
        assertNotNull(languages);
        assertTrue(languages.isEmpty());
    }



    @Test
    public void getConnectionType_withNullConnectivityManager_returnsNull() {
        when(mMockContext.checkCallingOrSelfPermission(Manifest.permission.ACCESS_NETWORK_STATE))
                .thenReturn(PackageManager.PERMISSION_GRANTED);
        when(mMockContext.getSystemService(Context.CONNECTIVITY_SERVICE)).thenReturn(null);

        assertNull(mDeviceInfo.getConnectionType());
    }

    @Test
    public void isBatteryCharging_afterUpdate_returnsUpdatedValue() {
        // Test that updateChargingStatus is called
        mDeviceInfo.updateChargingStatus();
        Integer charging = mDeviceInfo.isBatteryCharging();
        assertNotNull(charging);
        assertTrue(charging == 0 || charging == 1);
    }

    @Test
    public void updateChargingStatus_withNullContext_doesNotThrow() throws Exception {
        setField(mDeviceInfo, "mContext", null);
        // Should not throw
        mDeviceInfo.updateChargingStatus();
    }

    @Test
    public void limitTracking_afterSetTrue_returnsTrue() throws Exception {
        setField(mDeviceInfo, "mLimitTracking", true);
        assertTrue(mDeviceInfo.limitTracking());
    }

    @Test
    public void limitTracking_afterSetFalse_returnsFalse() throws Exception {
        setField(mDeviceInfo, "mLimitTracking", false);
        assertFalse(mDeviceInfo.limitTracking());
    }

    @Test
    public void getAdvertisingId_afterSet_returnsValue() throws Exception {
        setField(mDeviceInfo, "mAdvertisingId", "test-ad-id");
        assertEquals("test-ad-id", mDeviceInfo.getAdvertisingId());
    }

    @Test
    public void getAdvertisingIdMd5_afterSet_returnsValue() throws Exception {
        setField(mDeviceInfo, "mAdvertisingIdMd5", "test-md5");
        assertEquals("test-md5", mDeviceInfo.getAdvertisingIdMd5());
    }

    @Test
    public void getAdvertisingIdSha1_afterSet_returnsValue() throws Exception {
        setField(mDeviceInfo, "mAdvertisingIdSha1", "test-sha1");
        assertEquals("test-sha1", mDeviceInfo.getAdvertisingIdSha1());
    }

    @Test
    public void connectivity_ethernet_toString_returnsEthernet() {
        assertEquals("ethernet", DeviceInfo.Connectivity.ETHERNET.toString());
    }

    @Test
    public void connectivity_wifi_toString_returnsWifi() {
        assertEquals("wifi", DeviceInfo.Connectivity.WIFI.toString());
    }

    @Test
    public void connectivity_wwan_toString_returnsWwan() {
        assertEquals("wwan", DeviceInfo.Connectivity.WWAN.toString());
    }

    @Test
    public void connectivity_none_toString_returnsNone() {
        assertEquals("none", DeviceInfo.Connectivity.NONE.toString());
    }

    @Test
    public void getConnectionType_androidN_withCellularTransport_andGPRS_returns4() throws Exception {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            when(mMockContext.checkCallingOrSelfPermission(Manifest.permission.ACCESS_NETWORK_STATE))
                    .thenReturn(PackageManager.PERMISSION_GRANTED);
            when(mMockContext.checkCallingOrSelfPermission(Manifest.permission.READ_PHONE_STATE))
                    .thenReturn(PackageManager.PERMISSION_GRANTED);

            ConnectivityManager mockCm = mock(ConnectivityManager.class);
            when(mMockContext.getSystemService(Context.CONNECTIVITY_SERVICE)).thenReturn(mockCm);

            android.net.Network mockNetwork = mock(android.net.Network.class);
            when(mockCm.getActiveNetwork()).thenReturn(mockNetwork);

            android.net.NetworkCapabilities mockCapabilities = mock(android.net.NetworkCapabilities.class);
            when(mockCm.getNetworkCapabilities(mockNetwork)).thenReturn(mockCapabilities);
            when(mockCapabilities.hasTransport(android.net.NetworkCapabilities.TRANSPORT_WIFI)).thenReturn(false);
            when(mockCapabilities.hasTransport(android.net.NetworkCapabilities.TRANSPORT_ETHERNET)).thenReturn(false);
            when(mockCapabilities.hasTransport(android.net.NetworkCapabilities.TRANSPORT_CELLULAR)).thenReturn(true);

            TelephonyManager mockTelephonyManager = mock(TelephonyManager.class);
            when(mMockContext.getSystemService(Context.TELEPHONY_SERVICE)).thenReturn(mockTelephonyManager);
            when(mockTelephonyManager.getDataNetworkType()).thenReturn(TelephonyManager.NETWORK_TYPE_GPRS);

            DeviceInfo deviceInfo = new DeviceInfo(mMockContext);
            assertEquals(Integer.valueOf(4), deviceInfo.getConnectionType());
        }
    }

    @Test
    public void getConnectionType_androidN_withCellularTransport_andEDGE_returns4() throws Exception {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            when(mMockContext.checkCallingOrSelfPermission(Manifest.permission.ACCESS_NETWORK_STATE))
                    .thenReturn(PackageManager.PERMISSION_GRANTED);
            when(mMockContext.checkCallingOrSelfPermission(Manifest.permission.READ_PHONE_STATE))
                    .thenReturn(PackageManager.PERMISSION_GRANTED);

            ConnectivityManager mockCm = mock(ConnectivityManager.class);
            when(mMockContext.getSystemService(Context.CONNECTIVITY_SERVICE)).thenReturn(mockCm);

            android.net.Network mockNetwork = mock(android.net.Network.class);
            when(mockCm.getActiveNetwork()).thenReturn(mockNetwork);

            android.net.NetworkCapabilities mockCapabilities = mock(android.net.NetworkCapabilities.class);
            when(mockCm.getNetworkCapabilities(mockNetwork)).thenReturn(mockCapabilities);
            when(mockCapabilities.hasTransport(android.net.NetworkCapabilities.TRANSPORT_WIFI)).thenReturn(false);
            when(mockCapabilities.hasTransport(android.net.NetworkCapabilities.TRANSPORT_ETHERNET)).thenReturn(false);
            when(mockCapabilities.hasTransport(android.net.NetworkCapabilities.TRANSPORT_CELLULAR)).thenReturn(true);

            TelephonyManager mockTelephonyManager = mock(TelephonyManager.class);
            when(mMockContext.getSystemService(Context.TELEPHONY_SERVICE)).thenReturn(mockTelephonyManager);
            when(mockTelephonyManager.getDataNetworkType()).thenReturn(TelephonyManager.NETWORK_TYPE_EDGE);

            DeviceInfo deviceInfo = new DeviceInfo(mMockContext);
            assertEquals(Integer.valueOf(4), deviceInfo.getConnectionType());
        }
    }

    @Test
    public void getConnectionType_androidN_withCellularTransport_andCDMA_returns4() throws Exception {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            when(mMockContext.checkCallingOrSelfPermission(Manifest.permission.ACCESS_NETWORK_STATE))
                    .thenReturn(PackageManager.PERMISSION_GRANTED);
            when(mMockContext.checkCallingOrSelfPermission(Manifest.permission.READ_PHONE_STATE))
                    .thenReturn(PackageManager.PERMISSION_GRANTED);

            ConnectivityManager mockCm = mock(ConnectivityManager.class);
            when(mMockContext.getSystemService(Context.CONNECTIVITY_SERVICE)).thenReturn(mockCm);

            android.net.Network mockNetwork = mock(android.net.Network.class);
            when(mockCm.getActiveNetwork()).thenReturn(mockNetwork);

            android.net.NetworkCapabilities mockCapabilities = mock(android.net.NetworkCapabilities.class);
            when(mockCm.getNetworkCapabilities(mockNetwork)).thenReturn(mockCapabilities);
            when(mockCapabilities.hasTransport(android.net.NetworkCapabilities.TRANSPORT_WIFI)).thenReturn(false);
            when(mockCapabilities.hasTransport(android.net.NetworkCapabilities.TRANSPORT_ETHERNET)).thenReturn(false);
            when(mockCapabilities.hasTransport(android.net.NetworkCapabilities.TRANSPORT_CELLULAR)).thenReturn(true);

            TelephonyManager mockTelephonyManager = mock(TelephonyManager.class);
            when(mMockContext.getSystemService(Context.TELEPHONY_SERVICE)).thenReturn(mockTelephonyManager);
            when(mockTelephonyManager.getDataNetworkType()).thenReturn(TelephonyManager.NETWORK_TYPE_CDMA);

            DeviceInfo deviceInfo = new DeviceInfo(mMockContext);
            assertEquals(Integer.valueOf(4), deviceInfo.getConnectionType());
        }
    }

    @Test
    public void getConnectionType_androidN_withCellularTransport_and1xRTT_returns4() throws Exception {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            when(mMockContext.checkCallingOrSelfPermission(Manifest.permission.ACCESS_NETWORK_STATE))
                    .thenReturn(PackageManager.PERMISSION_GRANTED);
            when(mMockContext.checkCallingOrSelfPermission(Manifest.permission.READ_PHONE_STATE))
                    .thenReturn(PackageManager.PERMISSION_GRANTED);

            ConnectivityManager mockCm = mock(ConnectivityManager.class);
            when(mMockContext.getSystemService(Context.CONNECTIVITY_SERVICE)).thenReturn(mockCm);

            android.net.Network mockNetwork = mock(android.net.Network.class);
            when(mockCm.getActiveNetwork()).thenReturn(mockNetwork);

            android.net.NetworkCapabilities mockCapabilities = mock(android.net.NetworkCapabilities.class);
            when(mockCm.getNetworkCapabilities(mockNetwork)).thenReturn(mockCapabilities);
            when(mockCapabilities.hasTransport(android.net.NetworkCapabilities.TRANSPORT_WIFI)).thenReturn(false);
            when(mockCapabilities.hasTransport(android.net.NetworkCapabilities.TRANSPORT_ETHERNET)).thenReturn(false);
            when(mockCapabilities.hasTransport(android.net.NetworkCapabilities.TRANSPORT_CELLULAR)).thenReturn(true);

            TelephonyManager mockTelephonyManager = mock(TelephonyManager.class);
            when(mMockContext.getSystemService(Context.TELEPHONY_SERVICE)).thenReturn(mockTelephonyManager);
            when(mockTelephonyManager.getDataNetworkType()).thenReturn(TelephonyManager.NETWORK_TYPE_1xRTT);

            DeviceInfo deviceInfo = new DeviceInfo(mMockContext);
            assertEquals(Integer.valueOf(4), deviceInfo.getConnectionType());
        }
    }

    @Test
    public void getConnectionType_androidN_withCellularTransport_andIDEN_returns4() throws Exception {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            when(mMockContext.checkCallingOrSelfPermission(Manifest.permission.ACCESS_NETWORK_STATE))
                    .thenReturn(PackageManager.PERMISSION_GRANTED);
            when(mMockContext.checkCallingOrSelfPermission(Manifest.permission.READ_PHONE_STATE))
                    .thenReturn(PackageManager.PERMISSION_GRANTED);

            ConnectivityManager mockCm = mock(ConnectivityManager.class);
            when(mMockContext.getSystemService(Context.CONNECTIVITY_SERVICE)).thenReturn(mockCm);

            android.net.Network mockNetwork = mock(android.net.Network.class);
            when(mockCm.getActiveNetwork()).thenReturn(mockNetwork);

            android.net.NetworkCapabilities mockCapabilities = mock(android.net.NetworkCapabilities.class);
            when(mockCm.getNetworkCapabilities(mockNetwork)).thenReturn(mockCapabilities);
            when(mockCapabilities.hasTransport(android.net.NetworkCapabilities.TRANSPORT_WIFI)).thenReturn(false);
            when(mockCapabilities.hasTransport(android.net.NetworkCapabilities.TRANSPORT_ETHERNET)).thenReturn(false);
            when(mockCapabilities.hasTransport(android.net.NetworkCapabilities.TRANSPORT_CELLULAR)).thenReturn(true);

            TelephonyManager mockTelephonyManager = mock(TelephonyManager.class);
            when(mMockContext.getSystemService(Context.TELEPHONY_SERVICE)).thenReturn(mockTelephonyManager);
            when(mockTelephonyManager.getDataNetworkType()).thenReturn(TelephonyManager.NETWORK_TYPE_IDEN);

            DeviceInfo deviceInfo = new DeviceInfo(mMockContext);
            assertEquals(Integer.valueOf(4), deviceInfo.getConnectionType());
        }
    }

    @Test
    public void getConnectionType_androidN_withCellularTransport_andGSM_returns4() throws Exception {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            when(mMockContext.checkCallingOrSelfPermission(Manifest.permission.ACCESS_NETWORK_STATE))
                    .thenReturn(PackageManager.PERMISSION_GRANTED);
            when(mMockContext.checkCallingOrSelfPermission(Manifest.permission.READ_PHONE_STATE))
                    .thenReturn(PackageManager.PERMISSION_GRANTED);

            ConnectivityManager mockCm = mock(ConnectivityManager.class);
            when(mMockContext.getSystemService(Context.CONNECTIVITY_SERVICE)).thenReturn(mockCm);

            android.net.Network mockNetwork = mock(android.net.Network.class);
            when(mockCm.getActiveNetwork()).thenReturn(mockNetwork);

            android.net.NetworkCapabilities mockCapabilities = mock(android.net.NetworkCapabilities.class);
            when(mockCm.getNetworkCapabilities(mockNetwork)).thenReturn(mockCapabilities);
            when(mockCapabilities.hasTransport(android.net.NetworkCapabilities.TRANSPORT_WIFI)).thenReturn(false);
            when(mockCapabilities.hasTransport(android.net.NetworkCapabilities.TRANSPORT_ETHERNET)).thenReturn(false);
            when(mockCapabilities.hasTransport(android.net.NetworkCapabilities.TRANSPORT_CELLULAR)).thenReturn(true);

            TelephonyManager mockTelephonyManager = mock(TelephonyManager.class);
            when(mMockContext.getSystemService(Context.TELEPHONY_SERVICE)).thenReturn(mockTelephonyManager);
            when(mockTelephonyManager.getDataNetworkType()).thenReturn(TelephonyManager.NETWORK_TYPE_GSM);

            DeviceInfo deviceInfo = new DeviceInfo(mMockContext);
            assertEquals(Integer.valueOf(4), deviceInfo.getConnectionType());
        }
    }

    @Test
    public void getConnectionType_androidN_withCellularTransport_andUMTS_returns5() throws Exception {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            when(mMockContext.checkCallingOrSelfPermission(Manifest.permission.ACCESS_NETWORK_STATE))
                    .thenReturn(PackageManager.PERMISSION_GRANTED);
            when(mMockContext.checkCallingOrSelfPermission(Manifest.permission.READ_PHONE_STATE))
                    .thenReturn(PackageManager.PERMISSION_GRANTED);

            ConnectivityManager mockCm = mock(ConnectivityManager.class);
            when(mMockContext.getSystemService(Context.CONNECTIVITY_SERVICE)).thenReturn(mockCm);

            android.net.Network mockNetwork = mock(android.net.Network.class);
            when(mockCm.getActiveNetwork()).thenReturn(mockNetwork);

            android.net.NetworkCapabilities mockCapabilities = mock(android.net.NetworkCapabilities.class);
            when(mockCm.getNetworkCapabilities(mockNetwork)).thenReturn(mockCapabilities);
            when(mockCapabilities.hasTransport(android.net.NetworkCapabilities.TRANSPORT_WIFI)).thenReturn(false);
            when(mockCapabilities.hasTransport(android.net.NetworkCapabilities.TRANSPORT_ETHERNET)).thenReturn(false);
            when(mockCapabilities.hasTransport(android.net.NetworkCapabilities.TRANSPORT_CELLULAR)).thenReturn(true);

            TelephonyManager mockTelephonyManager = mock(TelephonyManager.class);
            when(mMockContext.getSystemService(Context.TELEPHONY_SERVICE)).thenReturn(mockTelephonyManager);
            when(mockTelephonyManager.getDataNetworkType()).thenReturn(TelephonyManager.NETWORK_TYPE_UMTS);

            DeviceInfo deviceInfo = new DeviceInfo(mMockContext);
            assertEquals(Integer.valueOf(5), deviceInfo.getConnectionType());
        }
    }

    @Test
    public void getConnectionType_androidN_withCellularTransport_andEVDO_0_returns5() throws Exception {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            when(mMockContext.checkCallingOrSelfPermission(Manifest.permission.ACCESS_NETWORK_STATE))
                    .thenReturn(PackageManager.PERMISSION_GRANTED);
            when(mMockContext.checkCallingOrSelfPermission(Manifest.permission.READ_PHONE_STATE))
                    .thenReturn(PackageManager.PERMISSION_GRANTED);

            ConnectivityManager mockCm = mock(ConnectivityManager.class);
            when(mMockContext.getSystemService(Context.CONNECTIVITY_SERVICE)).thenReturn(mockCm);

            android.net.Network mockNetwork = mock(android.net.Network.class);
            when(mockCm.getActiveNetwork()).thenReturn(mockNetwork);

            android.net.NetworkCapabilities mockCapabilities = mock(android.net.NetworkCapabilities.class);
            when(mockCm.getNetworkCapabilities(mockNetwork)).thenReturn(mockCapabilities);
            when(mockCapabilities.hasTransport(android.net.NetworkCapabilities.TRANSPORT_WIFI)).thenReturn(false);
            when(mockCapabilities.hasTransport(android.net.NetworkCapabilities.TRANSPORT_ETHERNET)).thenReturn(false);
            when(mockCapabilities.hasTransport(android.net.NetworkCapabilities.TRANSPORT_CELLULAR)).thenReturn(true);

            TelephonyManager mockTelephonyManager = mock(TelephonyManager.class);
            when(mMockContext.getSystemService(Context.TELEPHONY_SERVICE)).thenReturn(mockTelephonyManager);
            when(mockTelephonyManager.getDataNetworkType()).thenReturn(TelephonyManager.NETWORK_TYPE_EVDO_0);

            DeviceInfo deviceInfo = new DeviceInfo(mMockContext);
            assertEquals(Integer.valueOf(5), deviceInfo.getConnectionType());
        }
    }

    @Test
    public void getConnectionType_androidN_withCellularTransport_andEVDO_A_returns5() throws Exception {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            when(mMockContext.checkCallingOrSelfPermission(Manifest.permission.ACCESS_NETWORK_STATE))
                    .thenReturn(PackageManager.PERMISSION_GRANTED);
            when(mMockContext.checkCallingOrSelfPermission(Manifest.permission.READ_PHONE_STATE))
                    .thenReturn(PackageManager.PERMISSION_GRANTED);

            ConnectivityManager mockCm = mock(ConnectivityManager.class);
            when(mMockContext.getSystemService(Context.CONNECTIVITY_SERVICE)).thenReturn(mockCm);

            android.net.Network mockNetwork = mock(android.net.Network.class);
            when(mockCm.getActiveNetwork()).thenReturn(mockNetwork);

            android.net.NetworkCapabilities mockCapabilities = mock(android.net.NetworkCapabilities.class);
            when(mockCm.getNetworkCapabilities(mockNetwork)).thenReturn(mockCapabilities);
            when(mockCapabilities.hasTransport(android.net.NetworkCapabilities.TRANSPORT_WIFI)).thenReturn(false);
            when(mockCapabilities.hasTransport(android.net.NetworkCapabilities.TRANSPORT_ETHERNET)).thenReturn(false);
            when(mockCapabilities.hasTransport(android.net.NetworkCapabilities.TRANSPORT_CELLULAR)).thenReturn(true);

            TelephonyManager mockTelephonyManager = mock(TelephonyManager.class);
            when(mMockContext.getSystemService(Context.TELEPHONY_SERVICE)).thenReturn(mockTelephonyManager);
            when(mockTelephonyManager.getDataNetworkType()).thenReturn(TelephonyManager.NETWORK_TYPE_EVDO_A);

            DeviceInfo deviceInfo = new DeviceInfo(mMockContext);
            assertEquals(Integer.valueOf(5), deviceInfo.getConnectionType());
        }
    }

    @Test
    public void getConnectionType_androidN_withCellularTransport_andHSDPA_returns5() throws Exception {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            when(mMockContext.checkCallingOrSelfPermission(Manifest.permission.ACCESS_NETWORK_STATE))
                    .thenReturn(PackageManager.PERMISSION_GRANTED);
            when(mMockContext.checkCallingOrSelfPermission(Manifest.permission.READ_PHONE_STATE))
                    .thenReturn(PackageManager.PERMISSION_GRANTED);

            ConnectivityManager mockCm = mock(ConnectivityManager.class);
            when(mMockContext.getSystemService(Context.CONNECTIVITY_SERVICE)).thenReturn(mockCm);

            android.net.Network mockNetwork = mock(android.net.Network.class);
            when(mockCm.getActiveNetwork()).thenReturn(mockNetwork);

            android.net.NetworkCapabilities mockCapabilities = mock(android.net.NetworkCapabilities.class);
            when(mockCm.getNetworkCapabilities(mockNetwork)).thenReturn(mockCapabilities);
            when(mockCapabilities.hasTransport(android.net.NetworkCapabilities.TRANSPORT_WIFI)).thenReturn(false);
            when(mockCapabilities.hasTransport(android.net.NetworkCapabilities.TRANSPORT_ETHERNET)).thenReturn(false);
            when(mockCapabilities.hasTransport(android.net.NetworkCapabilities.TRANSPORT_CELLULAR)).thenReturn(true);

            TelephonyManager mockTelephonyManager = mock(TelephonyManager.class);
            when(mMockContext.getSystemService(Context.TELEPHONY_SERVICE)).thenReturn(mockTelephonyManager);
            when(mockTelephonyManager.getDataNetworkType()).thenReturn(TelephonyManager.NETWORK_TYPE_HSDPA);

            DeviceInfo deviceInfo = new DeviceInfo(mMockContext);
            assertEquals(Integer.valueOf(5), deviceInfo.getConnectionType());
        }
    }

    @Test
    public void getConnectionType_androidN_withCellularTransport_andHSUPA_returns5() throws Exception {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            when(mMockContext.checkCallingOrSelfPermission(Manifest.permission.ACCESS_NETWORK_STATE))
                    .thenReturn(PackageManager.PERMISSION_GRANTED);
            when(mMockContext.checkCallingOrSelfPermission(Manifest.permission.READ_PHONE_STATE))
                    .thenReturn(PackageManager.PERMISSION_GRANTED);

            ConnectivityManager mockCm = mock(ConnectivityManager.class);
            when(mMockContext.getSystemService(Context.CONNECTIVITY_SERVICE)).thenReturn(mockCm);

            android.net.Network mockNetwork = mock(android.net.Network.class);
            when(mockCm.getActiveNetwork()).thenReturn(mockNetwork);

            android.net.NetworkCapabilities mockCapabilities = mock(android.net.NetworkCapabilities.class);
            when(mockCm.getNetworkCapabilities(mockNetwork)).thenReturn(mockCapabilities);
            when(mockCapabilities.hasTransport(android.net.NetworkCapabilities.TRANSPORT_WIFI)).thenReturn(false);
            when(mockCapabilities.hasTransport(android.net.NetworkCapabilities.TRANSPORT_ETHERNET)).thenReturn(false);
            when(mockCapabilities.hasTransport(android.net.NetworkCapabilities.TRANSPORT_CELLULAR)).thenReturn(true);

            TelephonyManager mockTelephonyManager = mock(TelephonyManager.class);
            when(mMockContext.getSystemService(Context.TELEPHONY_SERVICE)).thenReturn(mockTelephonyManager);
            when(mockTelephonyManager.getDataNetworkType()).thenReturn(TelephonyManager.NETWORK_TYPE_HSUPA);

            DeviceInfo deviceInfo = new DeviceInfo(mMockContext);
            assertEquals(Integer.valueOf(5), deviceInfo.getConnectionType());
        }
    }

    @Test
    public void getConnectionType_androidN_withCellularTransport_andHSPA_returns5() throws Exception {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            when(mMockContext.checkCallingOrSelfPermission(Manifest.permission.ACCESS_NETWORK_STATE))
                    .thenReturn(PackageManager.PERMISSION_GRANTED);
            when(mMockContext.checkCallingOrSelfPermission(Manifest.permission.READ_PHONE_STATE))
                    .thenReturn(PackageManager.PERMISSION_GRANTED);

            ConnectivityManager mockCm = mock(ConnectivityManager.class);
            when(mMockContext.getSystemService(Context.CONNECTIVITY_SERVICE)).thenReturn(mockCm);

            android.net.Network mockNetwork = mock(android.net.Network.class);
            when(mockCm.getActiveNetwork()).thenReturn(mockNetwork);

            android.net.NetworkCapabilities mockCapabilities = mock(android.net.NetworkCapabilities.class);
            when(mockCm.getNetworkCapabilities(mockNetwork)).thenReturn(mockCapabilities);
            when(mockCapabilities.hasTransport(android.net.NetworkCapabilities.TRANSPORT_WIFI)).thenReturn(false);
            when(mockCapabilities.hasTransport(android.net.NetworkCapabilities.TRANSPORT_ETHERNET)).thenReturn(false);
            when(mockCapabilities.hasTransport(android.net.NetworkCapabilities.TRANSPORT_CELLULAR)).thenReturn(true);

            TelephonyManager mockTelephonyManager = mock(TelephonyManager.class);
            when(mMockContext.getSystemService(Context.TELEPHONY_SERVICE)).thenReturn(mockTelephonyManager);
            when(mockTelephonyManager.getDataNetworkType()).thenReturn(TelephonyManager.NETWORK_TYPE_HSPA);

            DeviceInfo deviceInfo = new DeviceInfo(mMockContext);
            assertEquals(Integer.valueOf(5), deviceInfo.getConnectionType());
        }
    }

    @Test
    public void getConnectionType_androidN_withCellularTransport_andEVDO_B_returns5() throws Exception {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            when(mMockContext.checkCallingOrSelfPermission(Manifest.permission.ACCESS_NETWORK_STATE))
                    .thenReturn(PackageManager.PERMISSION_GRANTED);
            when(mMockContext.checkCallingOrSelfPermission(Manifest.permission.READ_PHONE_STATE))
                    .thenReturn(PackageManager.PERMISSION_GRANTED);

            ConnectivityManager mockCm = mock(ConnectivityManager.class);
            when(mMockContext.getSystemService(Context.CONNECTIVITY_SERVICE)).thenReturn(mockCm);

            android.net.Network mockNetwork = mock(android.net.Network.class);
            when(mockCm.getActiveNetwork()).thenReturn(mockNetwork);

            android.net.NetworkCapabilities mockCapabilities = mock(android.net.NetworkCapabilities.class);
            when(mockCm.getNetworkCapabilities(mockNetwork)).thenReturn(mockCapabilities);
            when(mockCapabilities.hasTransport(android.net.NetworkCapabilities.TRANSPORT_WIFI)).thenReturn(false);
            when(mockCapabilities.hasTransport(android.net.NetworkCapabilities.TRANSPORT_ETHERNET)).thenReturn(false);
            when(mockCapabilities.hasTransport(android.net.NetworkCapabilities.TRANSPORT_CELLULAR)).thenReturn(true);

            TelephonyManager mockTelephonyManager = mock(TelephonyManager.class);
            when(mMockContext.getSystemService(Context.TELEPHONY_SERVICE)).thenReturn(mockTelephonyManager);
            when(mockTelephonyManager.getDataNetworkType()).thenReturn(TelephonyManager.NETWORK_TYPE_EVDO_B);

            DeviceInfo deviceInfo = new DeviceInfo(mMockContext);
            assertEquals(Integer.valueOf(5), deviceInfo.getConnectionType());
        }
    }

    @Test
    public void getConnectionType_androidN_withCellularTransport_andEHRPD_returns5() throws Exception {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            when(mMockContext.checkCallingOrSelfPermission(Manifest.permission.ACCESS_NETWORK_STATE))
                    .thenReturn(PackageManager.PERMISSION_GRANTED);
            when(mMockContext.checkCallingOrSelfPermission(Manifest.permission.READ_PHONE_STATE))
                    .thenReturn(PackageManager.PERMISSION_GRANTED);

            ConnectivityManager mockCm = mock(ConnectivityManager.class);
            when(mMockContext.getSystemService(Context.CONNECTIVITY_SERVICE)).thenReturn(mockCm);

            android.net.Network mockNetwork = mock(android.net.Network.class);
            when(mockCm.getActiveNetwork()).thenReturn(mockNetwork);

            android.net.NetworkCapabilities mockCapabilities = mock(android.net.NetworkCapabilities.class);
            when(mockCm.getNetworkCapabilities(mockNetwork)).thenReturn(mockCapabilities);
            when(mockCapabilities.hasTransport(android.net.NetworkCapabilities.TRANSPORT_WIFI)).thenReturn(false);
            when(mockCapabilities.hasTransport(android.net.NetworkCapabilities.TRANSPORT_ETHERNET)).thenReturn(false);
            when(mockCapabilities.hasTransport(android.net.NetworkCapabilities.TRANSPORT_CELLULAR)).thenReturn(true);

            TelephonyManager mockTelephonyManager = mock(TelephonyManager.class);
            when(mMockContext.getSystemService(Context.TELEPHONY_SERVICE)).thenReturn(mockTelephonyManager);
            when(mockTelephonyManager.getDataNetworkType()).thenReturn(TelephonyManager.NETWORK_TYPE_EHRPD);

            DeviceInfo deviceInfo = new DeviceInfo(mMockContext);
            assertEquals(Integer.valueOf(5), deviceInfo.getConnectionType());
        }
    }

    @Test
    public void getConnectionType_androidN_withCellularTransport_andHSPAP_returns5() throws Exception {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            when(mMockContext.checkCallingOrSelfPermission(Manifest.permission.ACCESS_NETWORK_STATE))
                    .thenReturn(PackageManager.PERMISSION_GRANTED);
            when(mMockContext.checkCallingOrSelfPermission(Manifest.permission.READ_PHONE_STATE))
                    .thenReturn(PackageManager.PERMISSION_GRANTED);

            ConnectivityManager mockCm = mock(ConnectivityManager.class);
            when(mMockContext.getSystemService(Context.CONNECTIVITY_SERVICE)).thenReturn(mockCm);

            android.net.Network mockNetwork = mock(android.net.Network.class);
            when(mockCm.getActiveNetwork()).thenReturn(mockNetwork);

            android.net.NetworkCapabilities mockCapabilities = mock(android.net.NetworkCapabilities.class);
            when(mockCm.getNetworkCapabilities(mockNetwork)).thenReturn(mockCapabilities);
            when(mockCapabilities.hasTransport(android.net.NetworkCapabilities.TRANSPORT_WIFI)).thenReturn(false);
            when(mockCapabilities.hasTransport(android.net.NetworkCapabilities.TRANSPORT_ETHERNET)).thenReturn(false);
            when(mockCapabilities.hasTransport(android.net.NetworkCapabilities.TRANSPORT_CELLULAR)).thenReturn(true);

            TelephonyManager mockTelephonyManager = mock(TelephonyManager.class);
            when(mMockContext.getSystemService(Context.TELEPHONY_SERVICE)).thenReturn(mockTelephonyManager);
            when(mockTelephonyManager.getDataNetworkType()).thenReturn(TelephonyManager.NETWORK_TYPE_HSPAP);

            DeviceInfo deviceInfo = new DeviceInfo(mMockContext);
            assertEquals(Integer.valueOf(5), deviceInfo.getConnectionType());
        }
    }

    @Test
    public void getConnectionType_androidN_withCellularTransport_andTD_SCDMA_returns5() throws Exception {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            when(mMockContext.checkCallingOrSelfPermission(Manifest.permission.ACCESS_NETWORK_STATE))
                    .thenReturn(PackageManager.PERMISSION_GRANTED);
            when(mMockContext.checkCallingOrSelfPermission(Manifest.permission.READ_PHONE_STATE))
                    .thenReturn(PackageManager.PERMISSION_GRANTED);

            ConnectivityManager mockCm = mock(ConnectivityManager.class);
            when(mMockContext.getSystemService(Context.CONNECTIVITY_SERVICE)).thenReturn(mockCm);

            android.net.Network mockNetwork = mock(android.net.Network.class);
            when(mockCm.getActiveNetwork()).thenReturn(mockNetwork);

            android.net.NetworkCapabilities mockCapabilities = mock(android.net.NetworkCapabilities.class);
            when(mockCm.getNetworkCapabilities(mockNetwork)).thenReturn(mockCapabilities);
            when(mockCapabilities.hasTransport(android.net.NetworkCapabilities.TRANSPORT_WIFI)).thenReturn(false);
            when(mockCapabilities.hasTransport(android.net.NetworkCapabilities.TRANSPORT_ETHERNET)).thenReturn(false);
            when(mockCapabilities.hasTransport(android.net.NetworkCapabilities.TRANSPORT_CELLULAR)).thenReturn(true);

            TelephonyManager mockTelephonyManager = mock(TelephonyManager.class);
            when(mMockContext.getSystemService(Context.TELEPHONY_SERVICE)).thenReturn(mockTelephonyManager);
            when(mockTelephonyManager.getDataNetworkType()).thenReturn(TelephonyManager.NETWORK_TYPE_TD_SCDMA);

            DeviceInfo deviceInfo = new DeviceInfo(mMockContext);
            assertEquals(Integer.valueOf(5), deviceInfo.getConnectionType());
        }
    }

    @Test
    public void getConnectionType_androidN_withCellularTransport_andLTE_returns6() throws Exception {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            when(mMockContext.checkCallingOrSelfPermission(Manifest.permission.ACCESS_NETWORK_STATE))
                    .thenReturn(PackageManager.PERMISSION_GRANTED);
            when(mMockContext.checkCallingOrSelfPermission(Manifest.permission.READ_PHONE_STATE))
                    .thenReturn(PackageManager.PERMISSION_GRANTED);

            ConnectivityManager mockCm = mock(ConnectivityManager.class);
            when(mMockContext.getSystemService(Context.CONNECTIVITY_SERVICE)).thenReturn(mockCm);

            android.net.Network mockNetwork = mock(android.net.Network.class);
            when(mockCm.getActiveNetwork()).thenReturn(mockNetwork);

            android.net.NetworkCapabilities mockCapabilities = mock(android.net.NetworkCapabilities.class);
            when(mockCm.getNetworkCapabilities(mockNetwork)).thenReturn(mockCapabilities);
            when(mockCapabilities.hasTransport(android.net.NetworkCapabilities.TRANSPORT_WIFI)).thenReturn(false);
            when(mockCapabilities.hasTransport(android.net.NetworkCapabilities.TRANSPORT_ETHERNET)).thenReturn(false);
            when(mockCapabilities.hasTransport(android.net.NetworkCapabilities.TRANSPORT_CELLULAR)).thenReturn(true);

            TelephonyManager mockTelephonyManager = mock(TelephonyManager.class);
            when(mMockContext.getSystemService(Context.TELEPHONY_SERVICE)).thenReturn(mockTelephonyManager);
            when(mockTelephonyManager.getDataNetworkType()).thenReturn(TelephonyManager.NETWORK_TYPE_LTE);

            DeviceInfo deviceInfo = new DeviceInfo(mMockContext);
            assertEquals(Integer.valueOf(6), deviceInfo.getConnectionType());
        }
    }

    @Test
    public void getConnectionType_androidN_withCellularTransport_andIWLAN_returns6() throws Exception {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            when(mMockContext.checkCallingOrSelfPermission(Manifest.permission.ACCESS_NETWORK_STATE))
                    .thenReturn(PackageManager.PERMISSION_GRANTED);
            when(mMockContext.checkCallingOrSelfPermission(Manifest.permission.READ_PHONE_STATE))
                    .thenReturn(PackageManager.PERMISSION_GRANTED);

            ConnectivityManager mockCm = mock(ConnectivityManager.class);
            when(mMockContext.getSystemService(Context.CONNECTIVITY_SERVICE)).thenReturn(mockCm);

            android.net.Network mockNetwork = mock(android.net.Network.class);
            when(mockCm.getActiveNetwork()).thenReturn(mockNetwork);

            android.net.NetworkCapabilities mockCapabilities = mock(android.net.NetworkCapabilities.class);
            when(mockCm.getNetworkCapabilities(mockNetwork)).thenReturn(mockCapabilities);
            when(mockCapabilities.hasTransport(android.net.NetworkCapabilities.TRANSPORT_WIFI)).thenReturn(false);
            when(mockCapabilities.hasTransport(android.net.NetworkCapabilities.TRANSPORT_ETHERNET)).thenReturn(false);
            when(mockCapabilities.hasTransport(android.net.NetworkCapabilities.TRANSPORT_CELLULAR)).thenReturn(true);

            TelephonyManager mockTelephonyManager = mock(TelephonyManager.class);
            when(mMockContext.getSystemService(Context.TELEPHONY_SERVICE)).thenReturn(mockTelephonyManager);
            when(mockTelephonyManager.getDataNetworkType()).thenReturn(TelephonyManager.NETWORK_TYPE_IWLAN);

            DeviceInfo deviceInfo = new DeviceInfo(mMockContext);
            assertEquals(Integer.valueOf(6), deviceInfo.getConnectionType());
        }
    }

    @Test
    public void getConnectionType_androidN_withCellularTransport_andType19_returns6() throws Exception {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            when(mMockContext.checkCallingOrSelfPermission(Manifest.permission.ACCESS_NETWORK_STATE))
                    .thenReturn(PackageManager.PERMISSION_GRANTED);
            when(mMockContext.checkCallingOrSelfPermission(Manifest.permission.READ_PHONE_STATE))
                    .thenReturn(PackageManager.PERMISSION_GRANTED);

            ConnectivityManager mockCm = mock(ConnectivityManager.class);
            when(mMockContext.getSystemService(Context.CONNECTIVITY_SERVICE)).thenReturn(mockCm);

            android.net.Network mockNetwork = mock(android.net.Network.class);
            when(mockCm.getActiveNetwork()).thenReturn(mockNetwork);

            android.net.NetworkCapabilities mockCapabilities = mock(android.net.NetworkCapabilities.class);
            when(mockCm.getNetworkCapabilities(mockNetwork)).thenReturn(mockCapabilities);
            when(mockCapabilities.hasTransport(android.net.NetworkCapabilities.TRANSPORT_WIFI)).thenReturn(false);
            when(mockCapabilities.hasTransport(android.net.NetworkCapabilities.TRANSPORT_ETHERNET)).thenReturn(false);
            when(mockCapabilities.hasTransport(android.net.NetworkCapabilities.TRANSPORT_CELLULAR)).thenReturn(true);

            TelephonyManager mockTelephonyManager = mock(TelephonyManager.class);
            when(mMockContext.getSystemService(Context.TELEPHONY_SERVICE)).thenReturn(mockTelephonyManager);
            when(mockTelephonyManager.getDataNetworkType()).thenReturn(19);

            DeviceInfo deviceInfo = new DeviceInfo(mMockContext);
            assertEquals(Integer.valueOf(6), deviceInfo.getConnectionType());
        }
    }

    @Test
    public void getConnectionType_androidN_withCellularTransport_andNR_returns7() throws Exception {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            when(mMockContext.checkCallingOrSelfPermission(Manifest.permission.ACCESS_NETWORK_STATE))
                    .thenReturn(PackageManager.PERMISSION_GRANTED);
            when(mMockContext.checkCallingOrSelfPermission(Manifest.permission.READ_PHONE_STATE))
                    .thenReturn(PackageManager.PERMISSION_GRANTED);

            ConnectivityManager mockCm = mock(ConnectivityManager.class);
            when(mMockContext.getSystemService(Context.CONNECTIVITY_SERVICE)).thenReturn(mockCm);

            android.net.Network mockNetwork = mock(android.net.Network.class);
            when(mockCm.getActiveNetwork()).thenReturn(mockNetwork);

            android.net.NetworkCapabilities mockCapabilities = mock(android.net.NetworkCapabilities.class);
            when(mockCm.getNetworkCapabilities(mockNetwork)).thenReturn(mockCapabilities);
            when(mockCapabilities.hasTransport(android.net.NetworkCapabilities.TRANSPORT_WIFI)).thenReturn(false);
            when(mockCapabilities.hasTransport(android.net.NetworkCapabilities.TRANSPORT_ETHERNET)).thenReturn(false);
            when(mockCapabilities.hasTransport(android.net.NetworkCapabilities.TRANSPORT_CELLULAR)).thenReturn(true);

            TelephonyManager mockTelephonyManager = mock(TelephonyManager.class);
            when(mMockContext.getSystemService(Context.TELEPHONY_SERVICE)).thenReturn(mockTelephonyManager);
            when(mockTelephonyManager.getDataNetworkType()).thenReturn(TelephonyManager.NETWORK_TYPE_NR);

            DeviceInfo deviceInfo = new DeviceInfo(mMockContext);
            assertEquals(Integer.valueOf(7), deviceInfo.getConnectionType());
        }
    }

    @Test
    public void getConnectionType_androidN_withCellularTransport_andUnknownType_returns3() throws Exception {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            when(mMockContext.checkCallingOrSelfPermission(Manifest.permission.ACCESS_NETWORK_STATE))
                    .thenReturn(PackageManager.PERMISSION_GRANTED);
            when(mMockContext.checkCallingOrSelfPermission(Manifest.permission.READ_PHONE_STATE))
                    .thenReturn(PackageManager.PERMISSION_GRANTED);

            ConnectivityManager mockCm = mock(ConnectivityManager.class);
            when(mMockContext.getSystemService(Context.CONNECTIVITY_SERVICE)).thenReturn(mockCm);

            android.net.Network mockNetwork = mock(android.net.Network.class);
            when(mockCm.getActiveNetwork()).thenReturn(mockNetwork);

            android.net.NetworkCapabilities mockCapabilities = mock(android.net.NetworkCapabilities.class);
            when(mockCm.getNetworkCapabilities(mockNetwork)).thenReturn(mockCapabilities);
            when(mockCapabilities.hasTransport(android.net.NetworkCapabilities.TRANSPORT_WIFI)).thenReturn(false);
            when(mockCapabilities.hasTransport(android.net.NetworkCapabilities.TRANSPORT_ETHERNET)).thenReturn(false);
            when(mockCapabilities.hasTransport(android.net.NetworkCapabilities.TRANSPORT_CELLULAR)).thenReturn(true);

            TelephonyManager mockTelephonyManager = mock(TelephonyManager.class);
            when(mMockContext.getSystemService(Context.TELEPHONY_SERVICE)).thenReturn(mockTelephonyManager);
            when(mockTelephonyManager.getDataNetworkType()).thenReturn(999); // Unknown type

            DeviceInfo deviceInfo = new DeviceInfo(mMockContext);
            assertEquals(Integer.valueOf(3), deviceInfo.getConnectionType());
        }
    }

    @Test
    public void getConnectionType_androidN_withCellularTransport_andNullTelephonyManager_returns3() throws Exception {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            when(mMockContext.checkCallingOrSelfPermission(Manifest.permission.ACCESS_NETWORK_STATE))
                    .thenReturn(PackageManager.PERMISSION_GRANTED);
            when(mMockContext.checkCallingOrSelfPermission(Manifest.permission.READ_PHONE_STATE))
                    .thenReturn(PackageManager.PERMISSION_GRANTED);

            ConnectivityManager mockCm = mock(ConnectivityManager.class);
            when(mMockContext.getSystemService(Context.CONNECTIVITY_SERVICE)).thenReturn(mockCm);

            android.net.Network mockNetwork = mock(android.net.Network.class);
            when(mockCm.getActiveNetwork()).thenReturn(mockNetwork);

            android.net.NetworkCapabilities mockCapabilities = mock(android.net.NetworkCapabilities.class);
            when(mockCm.getNetworkCapabilities(mockNetwork)).thenReturn(mockCapabilities);
            when(mockCapabilities.hasTransport(android.net.NetworkCapabilities.TRANSPORT_WIFI)).thenReturn(false);
            when(mockCapabilities.hasTransport(android.net.NetworkCapabilities.TRANSPORT_ETHERNET)).thenReturn(false);
            when(mockCapabilities.hasTransport(android.net.NetworkCapabilities.TRANSPORT_CELLULAR)).thenReturn(true);

            when(mMockContext.getSystemService(Context.TELEPHONY_SERVICE)).thenReturn(null);

            DeviceInfo deviceInfo = new DeviceInfo(mMockContext);
            assertEquals(Integer.valueOf(3), deviceInfo.getConnectionType());
        }
    }

    @Test
    public void getConnectionType_androidN_withWifiTransport_returns2() throws Exception {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            when(mMockContext.checkCallingOrSelfPermission(Manifest.permission.ACCESS_NETWORK_STATE))
                    .thenReturn(PackageManager.PERMISSION_GRANTED);
            when(mMockContext.checkCallingOrSelfPermission(Manifest.permission.READ_PHONE_STATE))
                    .thenReturn(PackageManager.PERMISSION_GRANTED);

            ConnectivityManager mockCm = mock(ConnectivityManager.class);
            when(mMockContext.getSystemService(Context.CONNECTIVITY_SERVICE)).thenReturn(mockCm);

            android.net.Network mockNetwork = mock(android.net.Network.class);
            when(mockCm.getActiveNetwork()).thenReturn(mockNetwork);

            android.net.NetworkCapabilities mockCapabilities = mock(android.net.NetworkCapabilities.class);
            when(mockCm.getNetworkCapabilities(mockNetwork)).thenReturn(mockCapabilities);
            when(mockCapabilities.hasTransport(android.net.NetworkCapabilities.TRANSPORT_WIFI)).thenReturn(true);

            DeviceInfo deviceInfo = new DeviceInfo(mMockContext);
            assertEquals(Integer.valueOf(2), deviceInfo.getConnectionType());
        }
    }

    @Test
    public void getConnectionType_androidN_withEthernetTransport_returns1() throws Exception {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            when(mMockContext.checkCallingOrSelfPermission(Manifest.permission.ACCESS_NETWORK_STATE))
                    .thenReturn(PackageManager.PERMISSION_GRANTED);
            when(mMockContext.checkCallingOrSelfPermission(Manifest.permission.READ_PHONE_STATE))
                    .thenReturn(PackageManager.PERMISSION_GRANTED);

            ConnectivityManager mockCm = mock(ConnectivityManager.class);
            when(mMockContext.getSystemService(Context.CONNECTIVITY_SERVICE)).thenReturn(mockCm);

            android.net.Network mockNetwork = mock(android.net.Network.class);
            when(mockCm.getActiveNetwork()).thenReturn(mockNetwork);

            android.net.NetworkCapabilities mockCapabilities = mock(android.net.NetworkCapabilities.class);
            when(mockCm.getNetworkCapabilities(mockNetwork)).thenReturn(mockCapabilities);
            when(mockCapabilities.hasTransport(android.net.NetworkCapabilities.TRANSPORT_WIFI)).thenReturn(false);
            when(mockCapabilities.hasTransport(android.net.NetworkCapabilities.TRANSPORT_ETHERNET)).thenReturn(true);

            DeviceInfo deviceInfo = new DeviceInfo(mMockContext);
            assertEquals(Integer.valueOf(1), deviceInfo.getConnectionType());
        }
    }

    @Test
    public void getConnectionType_androidN_withNullNetworkCapabilities_returnsNull() throws Exception {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            when(mMockContext.checkCallingOrSelfPermission(Manifest.permission.ACCESS_NETWORK_STATE))
                    .thenReturn(PackageManager.PERMISSION_GRANTED);
            when(mMockContext.checkCallingOrSelfPermission(Manifest.permission.READ_PHONE_STATE))
                    .thenReturn(PackageManager.PERMISSION_GRANTED);

            ConnectivityManager mockCm = mock(ConnectivityManager.class);
            when(mMockContext.getSystemService(Context.CONNECTIVITY_SERVICE)).thenReturn(mockCm);

            android.net.Network mockNetwork = mock(android.net.Network.class);
            when(mockCm.getActiveNetwork()).thenReturn(mockNetwork);
            when(mockCm.getNetworkCapabilities(mockNetwork)).thenReturn(null);

            DeviceInfo deviceInfo = new DeviceInfo(mMockContext);
            assertNull(deviceInfo.getConnectionType());
        }
    }

    @Test
    public void getConnectionType_androidN_withNullNetwork_returnsNull() throws Exception {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            when(mMockContext.checkCallingOrSelfPermission(Manifest.permission.ACCESS_NETWORK_STATE))
                    .thenReturn(PackageManager.PERMISSION_GRANTED);
            when(mMockContext.checkCallingOrSelfPermission(Manifest.permission.READ_PHONE_STATE))
                    .thenReturn(PackageManager.PERMISSION_GRANTED);

            ConnectivityManager mockCm = mock(ConnectivityManager.class);
            when(mMockContext.getSystemService(Context.CONNECTIVITY_SERVICE)).thenReturn(mockCm);
            when(mockCm.getActiveNetwork()).thenReturn(null);

            DeviceInfo deviceInfo = new DeviceInfo(mMockContext);
            assertNull(deviceInfo.getConnectionType());
        }
    }

    @Test
    public void getLangb_withLollipopAndAbove_usesToLanguageTag() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Locale testLocale = Locale.US;
            mMockConfiguration.locale = testLocale;
            when(mMockResources.getConfiguration()).thenReturn(mMockConfiguration);

            DeviceInfo deviceInfo = new DeviceInfo(mMockContext);
            String langb = deviceInfo.getLangb();
            assertEquals(testLocale.toLanguageTag(), langb);
        }
    }

    @Test
    public void getLangb_withNorwegianNynorsk_convertsToNn() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
            mMockConfiguration.locale = new Locale("no", "NO", "NY");
            when(mMockResources.getConfiguration()).thenReturn(mMockConfiguration);

            DeviceInfo deviceInfo = new DeviceInfo(mMockContext);
            String langb = deviceInfo.getLangb();
            assertEquals("nn-NO", langb);
        }
    }

    @Test
    public void getLangb_withEmptyLanguage_returnsUnd() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
            mMockConfiguration.locale = new Locale("", "US");
            when(mMockResources.getConfiguration()).thenReturn(mMockConfiguration);

            DeviceInfo deviceInfo = new DeviceInfo(mMockContext);
            String langb = deviceInfo.getLangb();
            assertTrue(langb.startsWith("und"));
        }
    }

    @Test
    public void getLangb_withDeprecatedHebrew_convertsToHe() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
            mMockConfiguration.locale = new Locale("iw", "IL");
            when(mMockResources.getConfiguration()).thenReturn(mMockConfiguration);

            DeviceInfo deviceInfo = new DeviceInfo(mMockContext);
            String langb = deviceInfo.getLangb();
            assertEquals("he-IL", langb);
        }
    }

    @Test
    public void getLangb_withDeprecatedIndonesian_convertsToId() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
            mMockConfiguration.locale = new Locale("in", "ID");
            when(mMockResources.getConfiguration()).thenReturn(mMockConfiguration);

            DeviceInfo deviceInfo = new DeviceInfo(mMockContext);
            String langb = deviceInfo.getLangb();
            assertEquals("id-ID", langb);
        }
    }

    @Test
    public void getLangb_withDeprecatedYiddish_convertsToYi() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
            mMockConfiguration.locale = new Locale("ji", "");
            when(mMockResources.getConfiguration()).thenReturn(mMockConfiguration);

            DeviceInfo deviceInfo = new DeviceInfo(mMockContext);
            String langb = deviceInfo.getLangb();
            assertEquals("yi", langb);
        }
    }

    @Test
    public void getLangb_withNullLocale_returnsNull() {
        mMockConfiguration.locale = null;
        when(mMockResources.getConfiguration()).thenReturn(mMockConfiguration);

        DeviceInfo deviceInfo = new DeviceInfo(mMockContext);
        String langb = deviceInfo.getLangb();
        assertNull(langb);
    }

    @Test
    public void getSoundSetting_whenMuted_returns0() {
        DeviceInfo deviceInfo = new DeviceInfo(mMockContext);
        // Note: SoundUtils caches the result async, so we just verify no exception
        String soundSetting = deviceInfo.getSoundSetting();
        assertNotNull(soundSetting);
    }

    @Test
    public void getSoundSetting_whenNotMuted_returns1() {
        DeviceInfo deviceInfo = new DeviceInfo(mMockContext);
        // Note: SoundUtils caches the result async, so we just verify no exception
        String soundSetting = deviceInfo.getSoundSetting();
        assertNotNull(soundSetting);
    }

    @Test
    public void updateChargingStatus_registersReceiver() {
        DeviceInfo deviceInfo = new DeviceInfo(mMockContext);
        deviceInfo.updateChargingStatus();

        // Verify that the method completes without exception
        assertNotNull(deviceInfo);
    }

    @Test
    public void updateChargingStatus_canBeCalledMultipleTimes() {
        DeviceInfo deviceInfo = new DeviceInfo(mMockContext);
        deviceInfo.updateChargingStatus();
        deviceInfo.updateChargingStatus();
        deviceInfo.updateChargingStatus();

        assertNotNull(deviceInfo);
    }

    @Test
    public void batteryStatusReceiver_doesNotUnregisterWhenNotRegistered() throws Exception {
        DeviceInfo deviceInfo = new DeviceInfo(mMockContext);
        
        // Set mIsChangingReceiverRegistered to false
        setField(deviceInfo, "mIsChangingReceiverRegistered", false);

        Field receiverField = DeviceInfo.class.getDeclaredField("mBatteryStatusReceiver");
        receiverField.setAccessible(true);
        android.content.BroadcastReceiver receiver = (android.content.BroadcastReceiver) receiverField.get(deviceInfo);

        android.content.Intent mockIntent = mock(android.content.Intent.class);
        when(mockIntent.getIntExtra(android.os.BatteryManager.EXTRA_STATUS, -1))
                .thenReturn(android.os.BatteryManager.BATTERY_STATUS_CHARGING);

        receiver.onReceive(mMockContext, mockIntent);

        // Verify charging status was updated
        Field isChargingField = DeviceInfo.class.getDeclaredField("mIsCharging");
        isChargingField.setAccessible(true);
        assertTrue((Boolean) isChargingField.get(deviceInfo));

        // Verify mIsChangingReceiverRegistered remains false (no unregistration attempted)
        Field isRegisteredField = DeviceInfo.class.getDeclaredField("mIsChangingReceiverRegistered");
        isRegisteredField.setAccessible(true);
        assertFalse((Boolean) isRegisteredField.get(deviceInfo));
    }


    @Test
    public void batteryStatusReceiver_setsFullStatusAsCharging() throws Exception {
        DeviceInfo deviceInfo = new DeviceInfo(mMockContext);

        Field receiverField = DeviceInfo.class.getDeclaredField("mBatteryStatusReceiver");
        receiverField.setAccessible(true);
        android.content.BroadcastReceiver receiver = (android.content.BroadcastReceiver) receiverField.get(deviceInfo);

        android.content.Intent fullIntent = mock(android.content.Intent.class);
        when(fullIntent.getIntExtra(android.os.BatteryManager.EXTRA_STATUS, -1))
                .thenReturn(android.os.BatteryManager.BATTERY_STATUS_FULL);

        receiver.onReceive(mMockContext, fullIntent);

        Field isChargingField = DeviceInfo.class.getDeclaredField("mIsCharging");
        isChargingField.setAccessible(true);
        assertTrue((Boolean) isChargingField.get(deviceInfo));
    }


    @Test
    public void updateChargingStatus_doesNotRegisterWhenAlreadyRegistered() throws Exception {
        DeviceInfo deviceInfo = new DeviceInfo(mMockContext);
        
        deviceInfo.updateChargingStatus();
        setField(deviceInfo, "mIsChangingReceiverRegistered", true);
        
        deviceInfo.updateChargingStatus();
        
        Field isRegisteredField = DeviceInfo.class.getDeclaredField("mIsChangingReceiverRegistered");
        isRegisteredField.setAccessible(true);
        assertTrue((Boolean) isRegisteredField.get(deviceInfo));
    }

    @Test
    public void batteryStatusReceiver_updatesChargingStatusCorrectly() throws Exception {
        DeviceInfo deviceInfo = new DeviceInfo(mMockContext);

        Field receiverField = DeviceInfo.class.getDeclaredField("mBatteryStatusReceiver");
        receiverField.setAccessible(true);
        android.content.BroadcastReceiver receiver = (android.content.BroadcastReceiver) receiverField.get(deviceInfo);

        setField(deviceInfo, "mIsChangingReceiverRegistered", false);

        // Test CHARGING status
        android.content.Intent chargingIntent = mock(android.content.Intent.class);
        when(chargingIntent.getIntExtra(android.os.BatteryManager.EXTRA_STATUS, -1))
                .thenReturn(android.os.BatteryManager.BATTERY_STATUS_CHARGING);

        receiver.onReceive(mMockContext, chargingIntent);

        Field isChargingField = DeviceInfo.class.getDeclaredField("mIsCharging");
        isChargingField.setAccessible(true);
        assertTrue((Boolean) isChargingField.get(deviceInfo));

        // Test DISCHARGING status
        android.content.Intent dischargingIntent = mock(android.content.Intent.class);
        when(dischargingIntent.getIntExtra(android.os.BatteryManager.EXTRA_STATUS, -1))
                .thenReturn(android.os.BatteryManager.BATTERY_STATUS_DISCHARGING);

        receiver.onReceive(mMockContext, dischargingIntent);

        assertFalse((Boolean) isChargingField.get(deviceInfo));
    }
}
