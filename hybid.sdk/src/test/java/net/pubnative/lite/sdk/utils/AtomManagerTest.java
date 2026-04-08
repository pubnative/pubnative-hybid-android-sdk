// HyBid SDK License
//
// https://github.com/pubnative/pubnative-hybid-android-sdk/blob/main/LICENSE
//
package net.pubnative.lite.sdk.utils;

import android.content.Context;
import net.pubnative.lite.sdk.HyBid;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.robolectric.RobolectricTestRunner;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@RunWith(RobolectricTestRunner.class)
@SuppressWarnings("ResultOfMethodCallIgnored")
public class AtomManagerTest {
    static class TestAtomManager extends AtomManager {
        public Class<?> findClassResult;
        public Method getDeclaredMethodResult;
        public Object invokeMethodResult;
        public Exception toThrow;
        public Object createProxyResult;
        public List<Object> invokeArgs;
        @Override
        protected Class<?> findClass(String className) throws ClassNotFoundException {
            if (toThrow instanceof ClassNotFoundException) throw (ClassNotFoundException) toThrow;
            return findClassResult;
        }
        @Override
        protected Method getDeclaredMethod(Class<?> clazz, String name, Class<?>... params) throws NoSuchMethodException {
            if (toThrow instanceof NoSuchMethodException) throw (NoSuchMethodException) toThrow;
            return getDeclaredMethodResult;
        }
        @Override
        protected Object invokeMethod(Method method, Object obj, Object... args) throws Exception {
            if (toThrow != null && !(toThrow instanceof ClassNotFoundException) && !(toThrow instanceof NoSuchMethodException)) throw toThrow;
            if (invokeArgs != null) invokeArgs.addAll(Arrays.asList(args));
            return invokeMethodResult;
        }
        protected Object createProxy(ClassLoader loader, Class<?> iface, ProxyUtils.ProxyMethodHandler handler) {
            return createProxyResult;
        }
    }

    @Test
    public void testGetInstance_Singleton() {
        AtomManager inst1 = AtomManager.getInstance();
        AtomManager inst2 = AtomManager.getInstance();
        assertNotNull(inst1);
        assertSame(inst1, inst2);
    }

    @Test
    public void testGetAtomCohorts_Success() {
        TestAtomManager mgr = new TestAtomManager();
        List<Object> fakeList = Arrays.asList("a", "b");
        mgr.findClassResult = Object.class;
        mgr.getDeclaredMethodResult = mock(Method.class);
        mgr.invokeMethodResult = fakeList;
        List<Object> result = mgr.getAtomCohortsInstance();
        assertEquals(fakeList, result);
    }

    @Test
    public void testGetAtomCohorts_NotAList() {
        TestAtomManager mgr = new TestAtomManager();
        mgr.findClassResult = Object.class;
        mgr.getDeclaredMethodResult = mock(Method.class);
        mgr.invokeMethodResult = "notalist";
        List<Object> result = mgr.getAtomCohortsInstance();
        assertTrue(result.isEmpty());
    }

    @Test
    public void testGetAtomCohorts_Exception() {
        TestAtomManager mgr = new TestAtomManager();
        mgr.toThrow = new ClassNotFoundException();
        try (MockedStatic<Logger> loggerMock = Mockito.mockStatic(Logger.class)) {
            List<Object> result = mgr.getAtomCohortsInstance();
            assertTrue(result.isEmpty());
            loggerMock.verify(() -> Logger.d(anyString(), contains(AtomManager.ATOM_NOT_FOUND_MESSAGE)));
        }
    }

    @Test
    public void testIsAtomSdkDisabled_True() {
        TestAtomManager mgr = new TestAtomManager();
        mgr.findClassResult = Object.class;
        mgr.getDeclaredMethodResult = mock(Method.class);
        mgr.invokeMethodResult = true;
        assertTrue(mgr.isAtomSdkDisabledInstance());
    }

    @Test
    public void testIsAtomSdkDisabled_False() {
        TestAtomManager mgr = new TestAtomManager();
        mgr.findClassResult = Object.class;
        mgr.getDeclaredMethodResult = mock(Method.class);
        mgr.invokeMethodResult = false;
        assertFalse(mgr.isAtomSdkDisabledInstance());
    }

    @Test
    public void testIsAtomSdkDisabled_NotBoolean() {
        TestAtomManager mgr = new TestAtomManager();
        mgr.findClassResult = Object.class;
        mgr.getDeclaredMethodResult = mock(Method.class);
        mgr.invokeMethodResult = "notboolean";
        assertTrue(mgr.isAtomSdkDisabledInstance());
    }

    @Test
    public void testIsAtomSdkDisabled_Exception() {
        TestAtomManager mgr = new TestAtomManager();
        mgr.toThrow = new ClassNotFoundException();
        try (MockedStatic<Logger> loggerMock = Mockito.mockStatic(Logger.class)) {
            assertTrue(mgr.isAtomSdkDisabledInstance());
            loggerMock.verify(() -> Logger.d(anyString(), contains(AtomManager.ATOM_NOT_FOUND_MESSAGE)));
        }
    }

    @Test
    public void testSetAdSessionData_Success() throws Exception {
        TestAtomManager mgr = new TestAtomManager();
        mgr.findClassResult = Object.class;
        mgr.getDeclaredMethodResult = mock(Method.class);
        mgr.invokeMethodResult = null;
        HashMap<String, Object> data = new HashMap<>();
        mgr.setAdSessionDataInstance(data);
        // No exception means success
    }

    @Test
    public void testSetAdSessionData_Exception() {
        TestAtomManager mgr = new TestAtomManager();
        mgr.toThrow = new ClassNotFoundException();
        try (MockedStatic<Logger> loggerMock = Mockito.mockStatic(Logger.class)) {
            mgr.setAdSessionDataInstance(new HashMap<>());
            loggerMock.verify(() -> Logger.d(anyString(), contains(AtomManager.ATOM_NOT_FOUND_MESSAGE)));
        }
    }

    @Test
    public void testIsAtomSdkConfigurationFetchSuccessful_True() {
        TestAtomManager mgr = new TestAtomManager();
        mgr.findClassResult = Object.class;
        mgr.getDeclaredMethodResult = mock(Method.class);
        mgr.invokeMethodResult = true;
        assertTrue(mgr.isAtomSdkConfigurationFetchSuccessfulInstance());
    }

    @Test
    public void testIsAtomSdkConfigurationFetchSuccessful_False() {
        TestAtomManager mgr = new TestAtomManager();
        mgr.findClassResult = Object.class;
        mgr.getDeclaredMethodResult = mock(Method.class);
        mgr.invokeMethodResult = false;
        assertFalse(mgr.isAtomSdkConfigurationFetchSuccessfulInstance());
    }

    @Test
    public void testIsAtomSdkConfigurationFetchSuccessful_NotBoolean() {
        TestAtomManager mgr = new TestAtomManager();
        mgr.findClassResult = Object.class;
        mgr.getDeclaredMethodResult = mock(Method.class);
        mgr.invokeMethodResult = "notboolean";
        assertFalse(mgr.isAtomSdkConfigurationFetchSuccessfulInstance());
    }

    @Test
    public void testIsAtomSdkConfigurationFetchSuccessful_Exception() {
        TestAtomManager mgr = new TestAtomManager();
        mgr.toThrow = new ClassNotFoundException();
        try (MockedStatic<Logger> loggerMock = Mockito.mockStatic(Logger.class)) {
            assertFalse(mgr.isAtomSdkConfigurationFetchSuccessfulInstance());
            loggerMock.verify(() -> Logger.d(anyString(), contains(AtomManager.ATOM_NOT_FOUND_MESSAGE)));
        }
    }

    @Test
    public void testInitializeAtom_Success() throws Exception {
        TestAtomManager mgr = new TestAtomManager();
        Context context = mock(Context.class);
        when(context.getPackageName()).thenReturn("com.example");
        mgr.findClassResult = Object.class;
        mgr.getDeclaredMethodResult = mock(Method.class);
        mgr.createProxyResult = new Object();
        mgr.invokeMethodResult = null;
        try (MockedStatic<HyBid> hyBidMock = Mockito.mockStatic(HyBid.class)) {
            hyBidMock.when(HyBid::isTestMode).thenReturn(true);
            mgr.initializeAtomInstance(context);
            hyBidMock.verify(HyBid::isTestMode);
        }
    }

    @Test
    public void testInitializeAtom_Exception() {
        TestAtomManager mgr = new TestAtomManager();
        Context context = mock(Context.class);
        when(context.getPackageName()).thenReturn("com.example");
        mgr.toThrow = new ClassNotFoundException();
        try (MockedStatic<Logger> loggerMock = Mockito.mockStatic(Logger.class);
             MockedStatic<HyBid> hyBidMock = Mockito.mockStatic(HyBid.class)) {
            mgr.initializeAtomInstance(context);
            hyBidMock.verify(() -> HyBid.setAtomStarted(false));
            loggerMock.verify(() -> Logger.d(anyString(), contains(AtomManager.ATOM_NOT_FOUND_MESSAGE)));
        }
    }

    @Test
    public void testStopAtom_Success() throws Exception {
        TestAtomManager mgr = new TestAtomManager();
        mgr.findClassResult = Object.class;
        mgr.getDeclaredMethodResult = mock(Method.class);
        mgr.createProxyResult = new Object();
        mgr.invokeMethodResult = null;
        try (MockedStatic<HyBid> hyBidMock = Mockito.mockStatic(HyBid.class)) {
            mgr.stopAtomInstance();
        }
    }

    @Test
    public void testStopAtom_Exception() {
        TestAtomManager mgr = new TestAtomManager();
        mgr.toThrow = new ClassNotFoundException();
        try (MockedStatic<Logger> loggerMock = Mockito.mockStatic(Logger.class)) {
            mgr.stopAtomInstance();
            loggerMock.verify(() -> Logger.d(anyString(), contains(AtomManager.ATOM_NOT_FOUND_MESSAGE)));
        }
    }

    // Test static wrapper methods
    @Test
    public void testGetAtomCohorts_StaticWrapper() {
        List<Object> result = AtomManager.getAtomCohorts();
        assertNotNull(result);
    }

    @Test
    public void testIsAtomSdkDisabled_StaticWrapper() {
        boolean result = AtomManager.isAtomSdkDisabled();
        // Should return true by default (when Atom is not found)
        assertTrue(result);
    }

    @Test
    public void testSetAdSessionData_StaticWrapper() {
        HashMap<String, Object> data = new HashMap<>();
        data.put("test", "value");
        // Should not throw exception
        AtomManager.setAdSessionData(data);
    }

    @Test
    public void testIsAtomSdkConfigurationFetchSuccessful_StaticWrapper() {
        boolean result = AtomManager.isAtomSdkConfigurationFetchSuccessful();
        // Should return false by default (when Atom is not found)
        assertFalse(result);
    }

    @Test
    public void testInitializeAtom_StaticWrapper() {
        Context context = mock(Context.class);
        when(context.getPackageName()).thenReturn("com.example");
        // Should not throw exception
        AtomManager.initializeAtom(context);
    }

    @Test
    public void testStopAtom_StaticWrapper() {
        // Should not throw exception
        AtomManager.stopAtom();
    }

    // Test proxy listener callbacks
    @Test
    public void testInitializeAtom_ProxyListenerCallback() {
        TestAtomManager mgr = new TestAtomManager();
        Context context = mock(Context.class);
        when(context.getPackageName()).thenReturn("com.example");
        mgr.findClassResult = Object.class;
        mgr.getDeclaredMethodResult = mock(Method.class);
        mgr.invokeMethodResult = null;

        // Capture the proxy handler to test the callback
        final ProxyUtils.ProxyMethodHandler[] capturedHandler = new ProxyUtils.ProxyMethodHandler[1];
        mgr = new TestAtomManager() {
            @Override
            protected Object createProxy(ClassLoader loader, Class<?> iface, ProxyUtils.ProxyMethodHandler handler) {
                capturedHandler[0] = handler;
                return new Object();
            }
            @Override
            protected Class<?> findClass(String className) throws ClassNotFoundException {
                return Object.class;
            }
            @Override
            protected Method getDeclaredMethod(Class<?> clazz, String name, Class<?>... params) throws NoSuchMethodException {
                return mock(Method.class);
            }
            @Override
            protected Object invokeMethod(Method method, Object obj, Object... args) throws Exception {
                return null;
            }
        };

        try (MockedStatic<HyBid> hyBidMock = Mockito.mockStatic(HyBid.class)) {
            hyBidMock.when(HyBid::isTestMode).thenReturn(true);
            mgr.initializeAtomInstance(context);

            // Test the captured proxy handler
            if (capturedHandler[0] != null) {
                try {
                    Method mockMethod = mock(Method.class);
                    when(mockMethod.getName()).thenReturn(AtomManager.ATOM_ON_INITIALISED_METHOD_NAME);

                    // Test callback with true
                    capturedHandler[0].handleMethod(new Object(), mockMethod, new Object[]{true});
                    hyBidMock.verify(() -> HyBid.setAtomStarted(true));

                    // Test callback with false
                    capturedHandler[0].handleMethod(new Object(), mockMethod, new Object[]{false});
                    hyBidMock.verify(() -> HyBid.setAtomStarted(false));

                    // Test callback with wrong method name
                    when(mockMethod.getName()).thenReturn("wrongMethod");
                    Object result = capturedHandler[0].handleMethod(new Object(), mockMethod, new Object[]{true});
                    assertNull(result);

                    // Test callback with wrong args length
                    when(mockMethod.getName()).thenReturn(AtomManager.ATOM_ON_INITIALISED_METHOD_NAME);
                    result = capturedHandler[0].handleMethod(new Object(), mockMethod, new Object[]{});
                    assertNull(result);

                    // Test callback with non-Boolean arg
                    result = capturedHandler[0].handleMethod(new Object(), mockMethod, new Object[]{"not boolean"});
                    assertNull(result);

                } catch (Throwable e) {
                    fail("Proxy handler should not throw: " + e.getMessage());
                }
            }
        }
    }

    @Test
    public void testStopAtom_ProxyListenerCallback() {
        TestAtomManager mgr = new TestAtomManager();
        mgr.findClassResult = Object.class;
        mgr.getDeclaredMethodResult = mock(Method.class);
        mgr.invokeMethodResult = null;

        // Capture the proxy handler to test the callback
        final ProxyUtils.ProxyMethodHandler[] capturedHandler = new ProxyUtils.ProxyMethodHandler[1];
        mgr = new TestAtomManager() {
            @Override
            protected Object createProxy(ClassLoader loader, Class<?> iface, ProxyUtils.ProxyMethodHandler handler) {
                capturedHandler[0] = handler;
                return new Object();
            }
            @Override
            protected Class<?> findClass(String className) throws ClassNotFoundException {
                return Object.class;
            }
            @Override
            protected Method getDeclaredMethod(Class<?> clazz, String name, Class<?>... params) throws NoSuchMethodException {
                return mock(Method.class);
            }
            @Override
            protected Object invokeMethod(Method method, Object obj, Object... args) throws Exception {
                return null;
            }
        };

        try (MockedStatic<HyBid> hyBidMock = Mockito.mockStatic(HyBid.class)) {
            mgr.stopAtomInstance();

            // Test the captured proxy handler
            if (capturedHandler[0] != null) {
                try {
                    Method mockMethod = mock(Method.class);
                    when(mockMethod.getName()).thenReturn(AtomManager.ATOM_ON_STOPPED_METHOD_NAME);

                    // Test callback with true (should set HyBid.setAtomStarted(false))
                    capturedHandler[0].handleMethod(new Object(), mockMethod, new Object[]{true});
                    hyBidMock.verify(() -> HyBid.setAtomStarted(false));

                    // Test callback with false (should set HyBid.setAtomStarted(true))
                    capturedHandler[0].handleMethod(new Object(), mockMethod, new Object[]{false});
                    hyBidMock.verify(() -> HyBid.setAtomStarted(true));

                    // Test callback with wrong method name
                    when(mockMethod.getName()).thenReturn("wrongMethod");
                    Object result = capturedHandler[0].handleMethod(new Object(), mockMethod, new Object[]{true});
                    assertNull(result);

                    // Test callback with wrong args length
                    when(mockMethod.getName()).thenReturn(AtomManager.ATOM_ON_STOPPED_METHOD_NAME);
                    result = capturedHandler[0].handleMethod(new Object(), mockMethod, new Object[]{});
                    assertNull(result);

                    // Test callback with non-Boolean arg
                    result = capturedHandler[0].handleMethod(new Object(), mockMethod, new Object[]{"not boolean"});
                    assertNull(result);

                } catch (Throwable e) {
                    fail("Proxy handler should not throw: " + e.getMessage());
                }
            }
        }
    }

    @Test
    public void testPutAtomJSData_Success_PutsValueInMap() {
        TestAtomManager mgr = new TestAtomManager();
        HashMap<String, String> fakeMap = new HashMap<>();
        mgr.findClassResult = Object.class;
        mgr.getDeclaredMethodResult = mock(Method.class);
        mgr.invokeMethodResult = fakeMap;

        mgr.putAtomJSData("testKey", "testValue");

        assertEquals("testValue", fakeMap.get("testKey"));
    }

    @Test
    public void testPutAtomJSData_OverwritesExistingValue() {
        TestAtomManager mgr = new TestAtomManager();
        HashMap<String, String> fakeMap = new HashMap<>();
        fakeMap.put("testKey", "oldValue");
        mgr.findClassResult = Object.class;
        mgr.getDeclaredMethodResult = mock(Method.class);
        mgr.invokeMethodResult = fakeMap;

        mgr.putAtomJSData("testKey", "newValue");

        assertEquals("newValue", fakeMap.get("testKey"));
    }

    @Test
    public void testPutAtomJSData_NotAHashMap_DoesNotThrow() {
        TestAtomManager mgr = new TestAtomManager();
        mgr.findClassResult = Object.class;
        mgr.getDeclaredMethodResult = mock(Method.class);
        mgr.invokeMethodResult = "not a hashmap";

        // Should not throw
        mgr.putAtomJSData("testKey", "testValue");
    }

    @Test
    public void testPutAtomJSData_NullResult_DoesNotThrow() {
        TestAtomManager mgr = new TestAtomManager();
        mgr.findClassResult = Object.class;
        mgr.getDeclaredMethodResult = mock(Method.class);
        mgr.invokeMethodResult = null;

        // Should not throw
        mgr.putAtomJSData("testKey", "testValue");
    }

    @Test
    public void testPutAtomJSData_Exception_LogsError() {
        TestAtomManager mgr = new TestAtomManager();
        mgr.toThrow = new ClassNotFoundException();

        try (MockedStatic<Logger> loggerMock = Mockito.mockStatic(Logger.class)) {
            mgr.putAtomJSData("testKey", "testValue");
            loggerMock.verify(() -> Logger.d(anyString(), contains(AtomManager.ATOM_NOT_FOUND_MESSAGE)));
        }
    }

    @Test
    public void testPutAtomJSData_MultipleKeys() {
        TestAtomManager mgr = new TestAtomManager();
        HashMap<String, String> fakeMap = new HashMap<>();
        mgr.findClassResult = Object.class;
        mgr.getDeclaredMethodResult = mock(Method.class);
        mgr.invokeMethodResult = fakeMap;

        mgr.putAtomJSData("key1", "value1");
        mgr.putAtomJSData("key2", "value2");
        mgr.putAtomJSData("key3", "value3");

        assertEquals(3, fakeMap.size());
        assertEquals("value1", fakeMap.get("key1"));
        assertEquals("value2", fakeMap.get("key2"));
        assertEquals("value3", fakeMap.get("key3"));
    }

    @Test
    public void testSurveyDataKeyConstant() {
        assertEquals("SurveyData", AtomManager.SURVEY_DATA_KEY);
    }

    @Test
    public void testSurveyHtmlKeyConstant() {
        assertEquals("SurveyHtml", AtomManager.SURVEY_HTML_KEY);
    }

    @Test
    public void testAtomGetJsDataMethodNameConstant() {
        assertEquals("getAtomJSData", AtomManager.ATOM_GET_JS_DATA_METHOD_NAME);
    }
}
