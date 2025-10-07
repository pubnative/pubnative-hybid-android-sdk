// HyBid SDK License
//
// https://github.com/pubnative/pubnative-hybid-android-sdk/blob/main/LICENSE
//
package net.pubnative.lite.sdk.models;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;

@RunWith(RobolectricTestRunner.class)
public class SdkConfigTest {

    private SdkConfig sdkConfig;

    @Before
    public void setUp() {
        sdkConfig = new SdkConfig();
    }

    @Test
    public void isAtomEnabled_whenListIsNull_returnsFalse() {
        sdkConfig.app_level = null;
        assertFalse(sdkConfig.isAtomEnabled());
    }

    @Test
    public void isAtomEnabled_whenListIsEmpty_returnsFalse() {
        sdkConfig.app_level = new ArrayList<>();
        assertFalse(sdkConfig.isAtomEnabled());
    }

    @Test
    public void isAtomEnabled_whenAtomDataIsMissing_returnsFalse() {
        AdData otherData = new AdData();
        otherData.type = "some_other_config";
        sdkConfig.app_level = List.of(otherData);

        assertFalse(sdkConfig.isAtomEnabled());
    }

    @Test
    public void isAtomEnabled_whenAtomDataIsTrue_returnsTrue() {
        AdData atomData = new AdData();
        atomData.type = ConfigAssets.ATOM_ENABLED;
        atomData.data = new HashMap<>();
        atomData.data.put("boolean", true);
        sdkConfig.app_level = List.of(atomData);

        assertTrue(sdkConfig.isAtomEnabled());
    }

    @Test
    public void isAtomEnabled_whenAtomDataIsFalse_returnsFalse() {
        AdData atomData = new AdData();
        atomData.type = ConfigAssets.ATOM_ENABLED;
        atomData.data = new HashMap<>();
        atomData.data.put("boolean", false);
        sdkConfig.app_level = List.of(atomData);

        assertFalse(sdkConfig.isAtomEnabled());
    }

    // The tests for isExperienceEnabled would follow the exact same pattern
    @Test
    public void isExperienceEnabled_whenExperienceDataIsTrue_returnsTrue() {
        AdData experienceData = new AdData();
        experienceData.type = ConfigAssets.EXPERIENCE_ENABLED;
        experienceData.data = new HashMap<>();
        experienceData.data.put("boolean", true);
        sdkConfig.app_level = List.of(experienceData);

        assertTrue(sdkConfig.isExperienceEnabled());
    }
}