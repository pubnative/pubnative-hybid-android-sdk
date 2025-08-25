// HyBid SDK License
//
// https://github.com/pubnative/pubnative-hybid-android-sdk/blob/main/LICENSE
//
package net.pubnative.lite.sdk;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.robolectric.RobolectricTestRunner;

import java.util.HashMap;
import java.util.Map;

/**
 * Unit tests for the UserDataManager class.
 * This test class is specifically designed to handle the complex dependencies of UserDataManager,
 * including multiple SharedPreferences instances and static method calls.
 */
@RunWith(RobolectricTestRunner.class)
public class UserDataManagerTest {

    // --- Mocks for Dependencies ---
    @Mock
    private Context mockContext;
    @Mock
    private SharedPreferences mockInternalPreferences; // For "net.pubnative.lite.dataconsent"
    @Mock
    private SharedPreferences mockAppPreferences;      // For DefaultSharedPreferences

    private MockedStatic<PreferenceManager> mockedPreferenceManager;
    private AutoCloseable mockitoCloseable;

    private UserDataManager userDataManager;

    private static final String KEY_GDPR_APPLIES = "IABTCF_gdprApplies";
    private static final String PREFERENCES_CONSENT = "net.pubnative.lite.dataconsent";


    @Before
    public void setUp() {
        mockitoCloseable = MockitoAnnotations.openMocks(this);

        mockedPreferenceManager = Mockito.mockStatic(PreferenceManager.class);
        mockedPreferenceManager.when(() -> PreferenceManager.getDefaultSharedPreferences(mockContext))
                .thenReturn(mockAppPreferences);

        when(mockContext.getApplicationContext()).thenReturn(mockContext);
        when(mockContext.getSharedPreferences(eq(PREFERENCES_CONSENT), anyInt()))
                .thenReturn(mockInternalPreferences);

        userDataManager = new UserDataManager(mockContext);
    }

    @After
    public void tearDown() throws Exception {
        mockitoCloseable.close();
        mockedPreferenceManager.close();
    }

    /**
     * Helper method to mock the value for the 'gdprApplies' key in the correct
     * SharedPreferences instance (mAppPreferences).
     *
     * @param value The value to simulate (String, Integer, Boolean, or null for non-existent).
     */
    private void mockGdprAppliesValue(Object value) {
        Map<String, Object> preferencesMap = new HashMap<>();

        if (value != null) {
            preferencesMap.put(KEY_GDPR_APPLIES, value);
            when(mockAppPreferences.contains(KEY_GDPR_APPLIES)).thenReturn(true);
        } else {
            when(mockAppPreferences.contains(KEY_GDPR_APPLIES)).thenReturn(false);
        }

        Mockito.doReturn(preferencesMap).when(mockAppPreferences).getAll();
    }


    // --- Tests for gdprApplies() method ---

    @Test
    public void gdprApplies_whenValueIsBooleanTrue_returnsTrue() {
        // Arrange
        mockGdprAppliesValue(true);
        // Act
        boolean result = userDataManager.gdprApplies();
        // Assert
        assertTrue("Should return true for boolean 'true'", result);
    }

    @Test
    public void gdprApplies_whenValueIsBooleanFalse_returnsFalse() {
        // Arrange
        mockGdprAppliesValue(false);
        // Act
        boolean result = userDataManager.gdprApplies();
        // Assert
        assertFalse("Should return false for boolean 'false'", result);
    }

    @Test
    public void gdprApplies_whenValueIsIntegerOne_returnsTrue() {
        // Arrange
        mockGdprAppliesValue(1);
        // Act
        boolean result = userDataManager.gdprApplies();
        // Assert
        assertTrue("Should return true for integer '1'", result);
    }

    @Test
    public void gdprApplies_whenValueIsIntegerZero_returnsFalse() {
        // Arrange
        mockGdprAppliesValue(0);
        // Act
        boolean result = userDataManager.gdprApplies();
        // Assert
        assertFalse("Should return false for integer '0'", result);
    }

    @Test
    public void gdprApplies_whenValueIsStringOne_returnsTrue() {
        // Arrange
        mockGdprAppliesValue("1");
        // Act
        boolean result = userDataManager.gdprApplies();
        // Assert
        assertTrue("Should return true for string '1'", result);
    }

    @Test
    public void gdprApplies_whenValueIsStringTrue_returnsTrue() {
        // Arrange
        mockGdprAppliesValue("true");
        // Act
        boolean result = userDataManager.gdprApplies();
        // Assert
        assertTrue("Should return true for string 'true' (case-insensitive)", result);
    }

    @Test
    public void gdprApplies_whenValueIsStringFalse_returnsFalse() {
        // Arrange
        mockGdprAppliesValue("false");
        // Act
        boolean result = userDataManager.gdprApplies();
        // Assert
        assertFalse("Should return false for string 'false'", result);
    }

    @Test
    public void gdprApplies_whenValueIsAnyOtherString_returnsFalse() {
        // Arrange
        mockGdprAppliesValue("not_a_valid_flag");
        // Act
        boolean result = userDataManager.gdprApplies();
        // Assert
        assertFalse("Should return false for any other string", result);
    }

    @Test
    public void gdprApplies_whenKeyDoesNotExist_returnsFalse() {
        // Arrange
        mockGdprAppliesValue(null);
        // Act
        boolean result = userDataManager.gdprApplies();
        // Assert
        assertFalse("Should return false if the key does not exist", result);
    }

    @Test
    public void gdprApplies_whenAppPreferencesIsNull_returnsFalse() {
        mockedPreferenceManager.when(() -> PreferenceManager.getDefaultSharedPreferences(mockContext))
                .thenReturn(null);
        // Re-initialize the manager to get the null preferences
        userDataManager = new UserDataManager(mockContext);

        // Act
        boolean result = userDataManager.gdprApplies();

        // Assert
        assertFalse("Should return false if the SharedPreferences object is null", result);
    }
}