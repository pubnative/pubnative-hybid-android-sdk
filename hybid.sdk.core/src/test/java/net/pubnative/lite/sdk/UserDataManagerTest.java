package net.pubnative.lite.sdk;

import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;

import android.content.Context;
import android.content.SharedPreferences;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.robolectric.RobolectricTestRunner;

@RunWith(RobolectricTestRunner.class)
public class UserDataManagerTest {
    UserDataManager userDataManager;
    private SharedPreferences sharedPreferences;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        this.sharedPreferences = Mockito.mock(SharedPreferences.class);
        Context context = Mockito.mock(Context.class);
        Mockito.when(context.getApplicationContext()).thenReturn(context);
        Mockito.when(context.getSharedPreferences(anyString(), anyInt())).thenReturn(sharedPreferences);
        userDataManager = new UserDataManager(context);
    }

    @Test
    public void testGdprStringTrue() {
        Mockito.when(sharedPreferences.getString(anyString(), anyString())).thenReturn("1");
        Assert.assertTrue(userDataManager.gdprApplies());
    }

    @Test
    public void testGdprStringFalse() {
        Mockito.when(sharedPreferences.getString(anyString(), anyString())).thenReturn("0");
        Assert.assertFalse(userDataManager.gdprApplies());
    }

    @Test
    public void testGdprIntTrue() {
        Mockito.when(sharedPreferences.getInt(anyString(), anyInt())).thenReturn(1);
        Assert.assertTrue(userDataManager.gdprApplies());
    }

    @Test
    public void testGdprIntFalse() {
        Mockito.when(sharedPreferences.getInt(anyString(), anyInt())).thenReturn(0);
        Assert.assertFalse(userDataManager.gdprApplies());
    }

    @Test
    public void testGdprFalse() {
        Mockito.when(sharedPreferences.getString(anyString(), anyString())).thenReturn("test");
        Assert.assertFalse(userDataManager.gdprApplies());
    }
}

