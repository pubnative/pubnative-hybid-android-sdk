// HyBid SDK License
//
// https://github.com/pubnative/pubnative-hybid-android-sdk/blob/main/LICENSE
//
package net.pubnative.lite.sdk.utils;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.os.Build;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.MockitoAnnotations;
import org.robolectric.RobolectricTestRunner;

import java.util.Arrays;
import java.util.Collections;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.junit.Assert.*;

import net.pubnative.lite.sdk.HyBid;
import net.pubnative.lite.sdk.browser.BrowserManager;

@RunWith(RobolectricTestRunner.class)
public class IntentHandlerTest {
    @Mock
    Context mockContext;
    @Mock
    PackageManager mockPackageManager;
    @Mock
    BrowserManager mockBrowserManager;
    private MockedStatic<HyBid> hyBidStatic;

    IntentHandler intentHandler;

    @Before
    public void setUp() throws Exception {
        mockContext = mock(Context.class);
        mockPackageManager = mock(PackageManager.class);
        when(mockContext.getPackageManager()).thenReturn(mockPackageManager);
        intentHandler = new IntentHandler(mockContext);

        mockBrowserManager = mock(BrowserManager.class);
        hyBidStatic = mockStatic(HyBid.class);
        hyBidStatic.when(HyBid::getBrowserManager).thenReturn(mockBrowserManager);
    }

    @After
    public void tearDown() {
        hyBidStatic.close();
    }

    @Test
    public void testCanHandleIntent_withResolvableIntent() {
        Intent intent = new Intent(Intent.ACTION_VIEW);
        when(mockPackageManager.queryIntentActivities(any(Intent.class), anyInt()))
                .thenReturn(Collections.singletonList(new ResolveInfo()));

        // Mock the call for Android 13+ (API 33+)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            when(mockPackageManager.queryIntentActivities(any(Intent.class), any(PackageManager.ResolveInfoFlags.class)))
                    .thenReturn(Collections.singletonList(new ResolveInfo()));
        }
        assertTrue(intentHandler.canHandleIntent(intent));
    }

    @Test
    public void testCanHandleIntent_withUnresolvableIntent() {
        Intent intent = new Intent(Intent.ACTION_VIEW);
        when(mockPackageManager.queryIntentActivities(any(Intent.class), anyInt()))
                .thenReturn(Collections.emptyList());
        assertFalse(intentHandler.canHandleIntent(intent));
    }

    @Test
    public void testCanHandleIntent_withNullUri() {
        assertFalse(intentHandler.canHandleIntent((Uri) null));
    }

    @Test
    public void testHandleDeepLink_success() {
        doNothing().when(mockContext).startActivity(any(Intent.class));
        assertTrue(intentHandler.handleDeepLink(Uri.parse("http://test.com")));
    }

    @Test
    public void testHandleDeepLink_failure() {
        doThrow(new RuntimeException("fail")).when(mockContext).startActivity(any(Intent.class));
        assertFalse(intentHandler.handleDeepLink(Uri.parse("http://test.com")));
    }

    @Test
    public void testHandleBrowserLinkBrowserActivity_success() {
        doNothing().when(mockContext).startActivity(any(Intent.class));
        assertTrue(intentHandler.handleBrowserLinkBrowserActivity(Uri.parse("http://test.com")));
    }

    @Test
    public void testHandleBrowserLinkBrowserActivity_failure() {
        doThrow(new RuntimeException("fail")).when(mockContext).startActivity(any(Intent.class));
        assertFalse(intentHandler.handleBrowserLinkBrowserActivity(Uri.parse("http://test.com")));
    }

    @Test
    public void testCreateViewIntent() {
        Uri uri = Uri.parse("http://test.com");
        Intent intent = intentHandler.createViewIntent(uri);
        assertEquals(Intent.ACTION_VIEW, intent.getAction());
        assertEquals(uri, intent.getData());
    }

    @Test
    public void testHandleBrowserLink_withPriorities_andNoIntentFound() {
        when(mockBrowserManager.containsPriorities()).thenReturn(true);
        when(mockBrowserManager.getPackagePriorities()).thenReturn(Arrays.asList("com.browser1"));
        when(mockPackageManager.queryIntentActivities(any(Intent.class), anyInt()))
                .thenReturn(Collections.emptyList());
        doNothing().when(mockContext).startActivity(any(Intent.class));

        boolean result = intentHandler.handleBrowserLink(Uri.parse("http://test.com"));
        assertTrue(result); // falls back to handleDeepLink
    }

    @Test
    public void testHandleBrowserLink_withoutPriorities() {
        when(mockBrowserManager.containsPriorities()).thenReturn(false);
        doNothing().when(mockContext).startActivity(any(Intent.class));

        boolean result = intentHandler.handleBrowserLink(Uri.parse("http://test.com"));
        assertTrue(result);
    }

    @Test
    public void testGetPriorityBrowserIntent_returnsNullIfNoneResolvable() {
        when(mockBrowserManager.getPackagePriorities()).thenReturn(Arrays.asList("com.browser1"));
        when(mockPackageManager.queryIntentActivities(any(Intent.class), anyInt()))
                .thenReturn(Collections.emptyList());

        Intent result = intentHandler.getPriorityBrowserIntent(Uri.parse("http://test.com"));
        assertNull(result);
    }

    @Test
    public void testHandleBrowserLink_withPriorities_andIntentFound() {
        when(mockBrowserManager.containsPriorities()).thenReturn(true);
        when(mockBrowserManager.getPackagePriorities()).thenReturn(Arrays.asList("com.browser1"));
        // Can handle intent
        when(mockPackageManager.queryIntentActivities(any(Intent.class), anyInt()))
                .thenReturn(Collections.singletonList(new ResolveInfo()));
        doNothing().when(mockContext).startActivity(any(Intent.class));

        boolean result = intentHandler.handleBrowserLink(Uri.parse("http://test.com"));
        assertTrue(result);
    }
}
