// HyBid SDK License
//
// https://github.com/pubnative/pubnative-hybid-android-sdk/blob/main/LICENSE
//
package net.pubnative.lite.sdk.utils;

import net.pubnative.lite.sdk.models.Ad;
import net.pubnative.lite.sdk.models.APIAsset;
import net.pubnative.lite.sdk.models.AdData;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.robolectric.RobolectricTestRunner;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

@RunWith(RobolectricTestRunner.class)
public class AdCustomCTAManagerTest {
    @Test
    public void testIsAbleShow_AllTrue() {
        Ad ad = mock(Ad.class);
        AdData adData = mockAdDataWithIcon("https://valid.url/icon.png");
        when(ad.isCustomCTAEnabled()).thenReturn(true);
        when(ad.hasCustomCTA()).thenReturn(true);
        when(ad.getAsset(APIAsset.CUSTOM_CTA)).thenReturn(adData);
        try (var mockedUrlValidator = Mockito.mockStatic(URLValidator.class)) {
            mockedUrlValidator.when(() -> URLValidator.isValidURL(anyString())).thenReturn(true);
            assertTrue(AdCustomCTAManager.isAbleShow(ad));
        }
    }

    @Test
    public void testIsAbleShow_Disabled() {
        Ad ad = mock(Ad.class);
        when(ad.isCustomCTAEnabled()).thenReturn(false);
        assertFalse(AdCustomCTAManager.isAbleShow(ad));
    }

    @Test
    public void testIsAbleShow_EnabledNull() {
        Ad ad = mock(Ad.class);
        when(ad.isCustomCTAEnabled()).thenReturn(null);
        assertFalse(AdCustomCTAManager.isAbleShow(ad));
    }

    @Test
    public void testIsAbleShow_HasCustomCTAFalse() {
        Ad ad = mock(Ad.class);
        when(ad.isCustomCTAEnabled()).thenReturn(true);
        when(ad.hasCustomCTA()).thenReturn(false);
        assertFalse(AdCustomCTAManager.isAbleShow(ad));
    }

    @Test
    public void testIsAbleShow_AssetNull() {
        Ad ad = mock(Ad.class);
        when(ad.isCustomCTAEnabled()).thenReturn(true);
        when(ad.hasCustomCTA()).thenReturn(true);
        when(ad.getAsset(APIAsset.CUSTOM_CTA)).thenReturn(null);
        assertFalse(AdCustomCTAManager.isAbleShow(ad));
    }

    @Test
    public void testIsAbleShow_IconNull() {
        Ad ad = mock(Ad.class);
        AdData adData = mockAdDataWithIcon(null);
        when(ad.isCustomCTAEnabled()).thenReturn(true);
        when(ad.hasCustomCTA()).thenReturn(true);
        when(ad.getAsset(APIAsset.CUSTOM_CTA)).thenReturn(adData);
        try (var mockedUrlValidator = Mockito.mockStatic(URLValidator.class)) {
            mockedUrlValidator.when(() -> URLValidator.isValidURL(anyString())).thenReturn(true);
            assertFalse(AdCustomCTAManager.isAbleShow(ad));
        }
    }

    @Test
    public void testIsAbleShow_IconEmpty() {
        Ad ad = mock(Ad.class);
        AdData adData = mockAdDataWithIcon("");
        when(ad.isCustomCTAEnabled()).thenReturn(true);
        when(ad.hasCustomCTA()).thenReturn(true);
        when(ad.getAsset(APIAsset.CUSTOM_CTA)).thenReturn(adData);
        try (var mockedUrlValidator = Mockito.mockStatic(URLValidator.class)) {
            mockedUrlValidator.when(() -> URLValidator.isValidURL(anyString())).thenReturn(true);
            assertFalse(AdCustomCTAManager.isAbleShow(ad));
        }
    }

    @Test
    public void testIsAbleShow_UrlInvalid() {
        Ad ad = mock(Ad.class);
        AdData adData = mockAdDataWithIcon("https://invalid.url/icon.png");
        when(ad.isCustomCTAEnabled()).thenReturn(true);
        when(ad.hasCustomCTA()).thenReturn(true);
        when(ad.getAsset(APIAsset.CUSTOM_CTA)).thenReturn(adData);
        try (var mockedUrlValidator = Mockito.mockStatic(URLValidator.class)) {
            mockedUrlValidator.when(() -> URLValidator.isValidURL(anyString())).thenReturn(false);
            assertFalse(AdCustomCTAManager.isAbleShow(ad));
        }
    }

    @Test
    public void testIsAbleShow_AdNull() {
        assertFalse(AdCustomCTAManager.isAbleShow(null));
    }

    @Test
    public void testGetCustomCtaDelay_Null() {
        Ad ad = mock(Ad.class);
        when(ad.getCustomCTADelay()).thenReturn(null);
        assertEquals(AdCustomCTAManager.CUSTOM_CTA_DELAY_DEFAULT, AdCustomCTAManager.getCustomCtaDelay(ad));
    }

    @Test
    public void testGetCustomCtaDelay_Negative() {
        Ad ad = mock(Ad.class);
        when(ad.getCustomCTADelay()).thenReturn(-1);
        assertEquals(AdCustomCTAManager.CUSTOM_CTA_DELAY_DEFAULT, AdCustomCTAManager.getCustomCtaDelay(ad));
    }

    @Test
    public void testGetCustomCtaDelay_Zero() {
        Ad ad = mock(Ad.class);
        when(ad.getCustomCTADelay()).thenReturn(0);
        assertEquals(Integer.valueOf(0), AdCustomCTAManager.getCustomCtaDelay(ad));
    }

    @Test
    public void testGetCustomCtaDelay_WithinMax() {
        Ad ad = mock(Ad.class);
        when(ad.getCustomCTADelay()).thenReturn(5);
        assertEquals(Integer.valueOf(5), AdCustomCTAManager.getCustomCtaDelay(ad));
    }

    @Test
    public void testGetCustomCtaDelay_AboveMax() {
        Ad ad = mock(Ad.class);
        when(ad.getCustomCTADelay()).thenReturn(15);
        assertEquals(AdCustomCTAManager.CUSTOM_CTA_DELAY_MAX, AdCustomCTAManager.getCustomCtaDelay(ad));
    }

    @Test
    public void testGetCustomCtaType_Extended() {
        Ad ad = mock(Ad.class);
        when(ad.getCustomCTAType()).thenReturn("extended");
        assertEquals(AdCustomCTAManager.CtaType.EXTENDED, AdCustomCTAManager.getCustomCtaType(ad));
    }

    @Test
    public void testGetCustomCtaType_Default() {
        Ad ad = mock(Ad.class);
        when(ad.getCustomCTAType()).thenReturn("default");
        assertEquals(AdCustomCTAManager.CtaType.DEFAULT, AdCustomCTAManager.getCustomCtaType(ad));
    }

    @Test
    public void testGetCustomCtaType_Null() {
        Ad ad = mock(Ad.class);
        when(ad.getCustomCTAType()).thenReturn(null);
        assertEquals(AdCustomCTAManager.CtaType.DEFAULT, AdCustomCTAManager.getCustomCtaType(ad));
    }

    @Test
    public void testGetCustomCtaType_Other() {
        Ad ad = mock(Ad.class);
        when(ad.getCustomCTAType()).thenReturn("other");
        assertEquals(AdCustomCTAManager.CtaType.DEFAULT, AdCustomCTAManager.getCustomCtaType(ad));
    }

    @Test
    public void testCtaTypeToString() {
        assertEquals("default", AdCustomCTAManager.CtaType.DEFAULT.toString());
        assertEquals("extended", AdCustomCTAManager.CtaType.EXTENDED.toString());
    }

    // Helper to mock AdData with getStringField("icon")
    private AdData mockAdDataWithIcon(String iconValue) {
        AdData adData = mock(AdData.class);
        when(adData.getStringField("icon")).thenReturn(iconValue);
        return adData;
    }
}
