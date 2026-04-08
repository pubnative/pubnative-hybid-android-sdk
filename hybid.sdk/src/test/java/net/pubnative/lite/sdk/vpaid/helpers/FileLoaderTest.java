package net.pubnative.lite.sdk.vpaid.helpers;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.Looper;

import net.pubnative.lite.sdk.vpaid.PlayerInfo;
import net.pubnative.lite.sdk.vpaid.enums.ConnectionType;
import net.pubnative.lite.sdk.vpaid.utils.FileUtils;
import net.pubnative.lite.sdk.vpaid.utils.Utils;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.net.HttpURLConnection;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;
import static org.robolectric.Shadows.shadowOf;

@RunWith(RobolectricTestRunner.class)
@Config(sdk = 28)
public class FileLoaderTest {

    @Mock
    private Context mockContext;
    @Mock
    private FileLoader.Callback mockCallback;
    @Mock
    private File mockFile;
    @Mock
    private HttpURLConnection mockConnection;

    private FileLoader fileLoader;
    private final String testUrl = "https://example.com/test.mp4";

    @Before
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        // Reset static state
        FileLoader.setUseMobileNetworkForCaching(false);
    }

    @After
    public void tearDown() {
        // Clean up
        if (fileLoader != null) {
            fileLoader.stop();
        }
    }

    @Test
    public void testConstructor() {
        fileLoader = new FileLoader(testUrl, mockContext, mockCallback, false);
        assertNotNull(fileLoader);
    }

    @Test
    public void testConstructorWithEndCard() {
        fileLoader = new FileLoader(testUrl, mockContext, mockCallback, true);
        assertNotNull(fileLoader);
    }

    @Test
    public void testStartWithEmptyUrl() {
        fileLoader = new FileLoader("", mockContext, mockCallback, false);
        fileLoader.start();

        ArgumentCaptor<PlayerInfo> captor = ArgumentCaptor.forClass(PlayerInfo.class);
        verify(mockCallback).onError(captor.capture());
        assertEquals("FileUrl is empty", captor.getValue().getMessage());
    }

    @Test
    public void testStartWithNullUrl() {
        fileLoader = new FileLoader(null, mockContext, mockCallback, false);
        fileLoader.start();

        ArgumentCaptor<PlayerInfo> captor = ArgumentCaptor.forClass(PlayerInfo.class);
        verify(mockCallback).onError(captor.capture());
        assertEquals("FileUrl is empty", captor.getValue().getMessage());
    }

    @Test
    public void testSetUseMobileNetworkForCaching() {
        FileLoader.setUseMobileNetworkForCaching(true);
        // Verify the static field was set correctly using reflection
        try {
            Field field = FileLoader.class.getDeclaredField("useMobileNetworkForCaching");
            field.setAccessible(true);
            Boolean value = (Boolean) field.get(null);
            assertTrue(value != null && value);
        } catch (Exception e) {
            fail("Failed to access static field: " + e.getMessage());
        }

        FileLoader.setUseMobileNetworkForCaching(false);
        try {
            Field field = FileLoader.class.getDeclaredField("useMobileNetworkForCaching");
            field.setAccessible(true);
            Boolean value = (Boolean) field.get(null);
            assertTrue(value == null || !value);
        } catch (Exception e) {
            fail("Failed to access static field: " + e.getMessage());
        }
    }

    @Test
    public void testStop() {
        fileLoader = new FileLoader(testUrl, mockContext, mockCallback, false);

        // Test that stop() doesn't throw exceptions
        fileLoader.stop();

        // Verify stop flag is set using reflection
        try {
            Field stopField = FileLoader.class.getDeclaredField("mStop");
            stopField.setAccessible(true);
            Boolean stopValue = (Boolean) stopField.get(fileLoader);
            assertTrue(stopValue != null && stopValue);
        } catch (Exception e) {
            fail("Failed to access stop field: " + e.getMessage());
        }
    }

    @Test
    public void testCallbackInterface() {
        // Test that callback interface methods can be called
        FileLoader.Callback callback = new FileLoader.Callback() {
            @Override
            public void onFileLoaded(String filePath) {
                assertNotNull(filePath);
            }

            @Override
            public void onError(PlayerInfo info) {
                assertNotNull(info);
            }

            @Override
            public void onProgress(double progress) {
                assertTrue(progress >= 0 && progress <= 1);
            }
        };

        callback.onFileLoaded("/test/path");
        callback.onError(new PlayerInfo("test error"));
        callback.onProgress(0.5);
    }

    @Test
    public void testFileHeadersInnerClass() {
        // Test the inner FileHeaders class using reflection
        try {
            Class<?> fileHeadersClass = Class.forName("net.pubnative.lite.sdk.vpaid.helpers.FileLoader$FileHeaders");
            assertNotNull(fileHeadersClass);

            // Test constructor with 2 parameters
            Object headers1 = fileHeadersClass.getDeclaredConstructor(String.class, int.class)
                    .newInstance("test-etag", 1024);
            assertNotNull(headers1);

            // Test constructor with 3 parameters
            Bitmap mockBitmap = mock(Bitmap.class);
            Object headers2 = fileHeadersClass.getDeclaredConstructor(String.class, int.class, Bitmap.class)
                    .newInstance("test-etag", 1024, mockBitmap);
            assertNotNull(headers2);

            // Verify fields
            Field eTagField = fileHeadersClass.getDeclaredField("eTag");
            Field fileLengthField = fileHeadersClass.getDeclaredField("fileLength");
            Field bitmapField = fileHeadersClass.getDeclaredField("bitmap");

            eTagField.setAccessible(true);
            fileLengthField.setAccessible(true);
            bitmapField.setAccessible(true);

            assertEquals("test-etag", eTagField.get(headers1));
            assertEquals(1024, fileLengthField.get(headers1));
            assertNull(bitmapField.get(headers1));

            assertEquals("test-etag", eTagField.get(headers2));
            assertEquals(1024, fileLengthField.get(headers2));
            assertEquals(mockBitmap, bitmapField.get(headers2));

        } catch (Exception e) {
            fail("Failed to test FileHeaders inner class: " + e.getMessage());
        }
    }

    @Test
    public void testPrivateFieldsInitialization() {
        fileLoader = new FileLoader(testUrl, mockContext, mockCallback, false);

        try {
            // Test that private fields are properly initialized
            Field contextField = FileLoader.class.getDeclaredField("mContext");
            contextField.setAccessible(true);
            assertEquals(mockContext, contextField.get(fileLoader));

            Field callbackField = FileLoader.class.getDeclaredField("mCallback");
            callbackField.setAccessible(true);
            assertEquals(mockCallback, callbackField.get(fileLoader));

            Field urlField = FileLoader.class.getDeclaredField("mRemoteFileUrl");
            urlField.setAccessible(true);
            assertEquals(testUrl, urlField.get(fileLoader));

            Field loadingFileField = FileLoader.class.getDeclaredField("mLoadingFile");
            loadingFileField.setAccessible(true);
            assertNotNull(loadingFileField.get(fileLoader));

            Field isEndCardField = FileLoader.class.getDeclaredField("mIsEndCard");
            isEndCardField.setAccessible(true);
            Boolean isEndCardValue = (Boolean) isEndCardField.get(fileLoader);
            assertFalse(isEndCardValue != null && isEndCardValue);

        } catch (Exception e) {
            fail("Failed to test private fields: " + e.getMessage());
        }
    }

    @Test
    public void testPrivateFieldsInitializationWithEndCard() {
        fileLoader = new FileLoader(testUrl, mockContext, mockCallback, true);

        try {
            Field isEndCardField = FileLoader.class.getDeclaredField("mIsEndCard");
            isEndCardField.setAccessible(true);
            Boolean isEndCardValue = (Boolean) isEndCardField.get(fileLoader);
            assertTrue(isEndCardValue != null && isEndCardValue);
        } catch (Exception e) {
            fail("Failed to test end card field: " + e.getMessage());
        }
    }

    @Test
    public void testProgressFlags() {
        fileLoader = new FileLoader(testUrl, mockContext, mockCallback, false);

        try {
            // Test that progress flags are initialized to false
            Field firstQuartileField = FileLoader.class.getDeclaredField("firstQuartile");
            firstQuartileField.setAccessible(true);
            Boolean firstQuartileValue = (Boolean) firstQuartileField.get(fileLoader);
            assertFalse(firstQuartileValue != null && firstQuartileValue);

            Field midpointField = FileLoader.class.getDeclaredField("midpoint");
            midpointField.setAccessible(true);
            Boolean midpointValue = (Boolean) midpointField.get(fileLoader);
            assertFalse(midpointValue != null && midpointValue);

            Field thirdQuartileField = FileLoader.class.getDeclaredField("thirdQuartile");
            thirdQuartileField.setAccessible(true);
            Boolean thirdQuartileValue = (Boolean) thirdQuartileField.get(fileLoader);
            assertFalse(thirdQuartileValue != null && thirdQuartileValue);

        } catch (Exception e) {
            fail("Failed to test progress flags: " + e.getMessage());
        }
    }

    @Test
    public void testHandleProgressMethod() {
        fileLoader = new FileLoader(testUrl, mockContext, mockCallback, false);

        try {
            Method handleProgressMethod = FileLoader.class.getDeclaredMethod("handleProgress", double.class);
            handleProgressMethod.setAccessible(true);

            // Test first quartile
            handleProgressMethod.invoke(fileLoader, 0.3);
            verify(mockCallback).onProgress(0.25);

            // Test midpoint
            handleProgressMethod.invoke(fileLoader, 0.6);
            verify(mockCallback).onProgress(0.5);

            // Test third quartile
            handleProgressMethod.invoke(fileLoader, 0.8);
            verify(mockCallback).onProgress(0.75);

        } catch (Exception e) {
            fail("Failed to test handleProgress method: " + e.getMessage());
        }
    }

    @Test
    public void testHandleProgressWithNullCallback() {
        fileLoader = new FileLoader(testUrl, mockContext, null, false);

        try {
            Method handleProgressMethod = FileLoader.class.getDeclaredMethod("handleProgress", double.class);
            handleProgressMethod.setAccessible(true);

            // Should not throw exception with null callback
            handleProgressMethod.invoke(fileLoader, 0.5);

        } catch (Exception e) {
            fail("Failed to test handleProgress with null callback: " + e.getMessage());
        }
    }

    @Test
    public void testHandleFileFullDownloaded() {
        fileLoader = new FileLoader(testUrl, mockContext, mockCallback, false);

        try {
            Method handleFileFullDownloadedMethod = FileLoader.class.getDeclaredMethod("handleFileFullDownloaded");
            handleFileFullDownloadedMethod.setAccessible(true);

            // Call the method
            handleFileFullDownloadedMethod.invoke(fileLoader);

            // Verify that mIsFileFullyDownloaded is set to true
            Field isFileFullyDownloadedField = FileLoader.class.getDeclaredField("mIsFileFullyDownloaded");
            isFileFullyDownloadedField.setAccessible(true);
            Boolean isFullyDownloadedValue = (Boolean) isFileFullyDownloadedField.get(fileLoader);
            assertTrue(isFullyDownloadedValue != null && isFullyDownloadedValue);

        } catch (Exception e) {
            fail("Failed to test handleFileFullDownloaded method: " + e.getMessage());
        }
    }

    @Test
    public void testCloseStreamMethod() {
        try {
            Method closeStreamMethod = FileLoader.class.getDeclaredMethod("closeStream", java.io.Closeable.class);
            closeStreamMethod.setAccessible(true);

            // Test with null stream
            closeStreamMethod.invoke(null, (Object) null);

            // Test with mock closeable
            java.io.Closeable mockCloseable = mock(java.io.Closeable.class);
            closeStreamMethod.invoke(null, mockCloseable);
            verify(mockCloseable).close();

        } catch (Exception e) {
            fail("Failed to test closeStream method: " + e.getMessage());
        }
    }

    @Test
    public void testCloseStreamWithIOException() {
        try {
            Method closeStreamMethod = FileLoader.class.getDeclaredMethod("closeStream", java.io.Closeable.class);
            closeStreamMethod.setAccessible(true);

            // Test with mock closeable that throws IOException
            java.io.Closeable mockCloseable = mock(java.io.Closeable.class);
            doThrow(new java.io.IOException()).when(mockCloseable).close();

            // Should not throw exception, should handle IOException internally
            closeStreamMethod.invoke(null, mockCloseable);

        } catch (Exception e) {
            fail("Failed to test closeStream with IOException: " + e.getMessage());
        }
    }

    @Test
    public void testConstants() {
        try {
            Field logTagField = FileLoader.class.getDeclaredField("LOG_TAG");
            logTagField.setAccessible(true);
            assertEquals("FileLoader", logTagField.get(null));

            Field connectTimeoutField = FileLoader.class.getDeclaredField("CONNECT_TIMEOUT");
            connectTimeoutField.setAccessible(true);
            assertEquals(10000, connectTimeoutField.get(null));

            Field readTimeoutField = FileLoader.class.getDeclaredField("READ_TIMEOUT");
            readTimeoutField.setAccessible(true);
            assertEquals(10000, readTimeoutField.get(null));

        } catch (Exception e) {
            fail("Failed to test constants: " + e.getMessage());
        }
    }

    @Test
    public void testStartWithExistingFile() {
        try (MockedStatic<FileUtils> mockedFileUtils = Mockito.mockStatic(FileUtils.class)) {
            // Create a real temporary directory for testing
            File tempDir = new File(System.getProperty("java.io.tmpdir"), "test_file_loader");
            if (!tempDir.exists()) {
                tempDir.mkdirs();
            }

            String expectedFileName = "test_hash";
            mockedFileUtils.when(() -> FileUtils.getParentDir(mockContext)).thenReturn(tempDir);
            mockedFileUtils.when(() -> FileUtils.obtainHashName(testUrl)).thenReturn(expectedFileName);

            // Create FileLoader first to ensure it uses our mocked methods
            fileLoader = new FileLoader(testUrl, mockContext, mockCallback, false);

            // Now create the actual test file that matches what FileLoader expects
            File testFile = new File(tempDir, expectedFileName);
            try {
                testFile.createNewFile();
                assertTrue("Test file should exist", testFile.exists());

                // Start the loader - it should detect the existing file
                fileLoader.start();

                // Process any queued runnables in the main looper BEFORE verifying
                shadowOf(Looper.getMainLooper()).idle();

                // Verify that callback was called for existing file
                verify(mockCallback, timeout(2000)).onFileLoaded(testFile.getAbsolutePath());

            } catch (IOException e) {
                fail("IOException occurred: " + e.getMessage());
            } finally {
                // Clean up
                if (testFile.exists()) {
                    testFile.delete();
                }
                if (tempDir.exists()) {
                    tempDir.delete();
                }
            }
        }
    }

    @Test
    public void testMaybeLoadFileWithMobileNetwork() {
        try (MockedStatic<RequestParametersProvider> mockedProvider = Mockito.mockStatic(RequestParametersProvider.class)) {
            mockedProvider.when(() -> RequestParametersProvider.getConnectionType(mockContext))
                    .thenReturn(ConnectionType.MOBILE);

            fileLoader = new FileLoader(testUrl, mockContext, mockCallback, false);

            // Use reflection to call maybeLoadFile
            try {
                Method maybeLoadFileMethod = FileLoader.class.getDeclaredMethod("maybeLoadFile");
                maybeLoadFileMethod.setAccessible(true);
                maybeLoadFileMethod.invoke(fileLoader);

                // Verify error callback for mobile network
                ArgumentCaptor<PlayerInfo> captor = ArgumentCaptor.forClass(PlayerInfo.class);
                verify(mockCallback, timeout(1000)).onError(captor.capture());
                assertEquals("Mobile network. File will not be cached", captor.getValue().getMessage());

            } catch (Exception e) {
                fail("Failed to test mobile network scenario: " + e.getMessage());
            }
        }
    }

    @Test
    public void testMaybeLoadFileWithWifiConnection() {
        try (MockedStatic<RequestParametersProvider> mockedProvider = Mockito.mockStatic(RequestParametersProvider.class);
             MockedStatic<ExecutorHelper> mockedExecutor = Mockito.mockStatic(ExecutorHelper.class)) {

            mockedProvider.when(() -> RequestParametersProvider.getConnectionType(mockContext))
                    .thenReturn(ConnectionType.WIFI);

            // Mock executor
            java.util.concurrent.ExecutorService mockExecutorService = mock(java.util.concurrent.ExecutorService.class);
            mockedExecutor.when(() -> ExecutorHelper.getExecutor()).thenReturn(mockExecutorService);

            fileLoader = new FileLoader(testUrl, mockContext, mockCallback, false);

            // Use reflection to call maybeLoadFile
            try {
                Method maybeLoadFileMethod = FileLoader.class.getDeclaredMethod("maybeLoadFile");
                maybeLoadFileMethod.setAccessible(true);
                maybeLoadFileMethod.invoke(fileLoader);

                // Verify executor was called
                verify(mockExecutorService).submit(any(Runnable.class));

            } catch (Exception e) {
                fail("Failed to test wifi connection scenario: " + e.getMessage());
            }
        }
    }

    @Test
    public void testLoadWithStopFlag() {
        fileLoader = new FileLoader(testUrl, mockContext, mockCallback, false);

        try {
            // Set stop flag to true
            Field stopField = FileLoader.class.getDeclaredField("mStop");
            stopField.setAccessible(true);
            stopField.set(fileLoader, true);

            // Call load method
            Method loadMethod = FileLoader.class.getDeclaredMethod("load");
            loadMethod.setAccessible(true);
            loadMethod.invoke(fileLoader);

            // Should return early, no callback should be invoked
            verify(mockCallback, never()).onError(any());
            verify(mockCallback, never()).onFileLoaded(anyString());

        } catch (Exception e) {
            fail("Failed to test load with stop flag: " + e.getMessage());
        }
    }

    @Test
    public void testStopWithIncompleteFile() {
        try (MockedStatic<ExecutorHelper> mockedExecutor = Mockito.mockStatic(ExecutorHelper.class)) {
            java.util.concurrent.ExecutorService mockExecutorService = mock(java.util.concurrent.ExecutorService.class);
            mockedExecutor.when(() -> ExecutorHelper.getExecutor()).thenReturn(mockExecutorService);

            fileLoader = new FileLoader(testUrl, mockContext, mockCallback, false);

            // Mock a file that exists but is not fully downloaded
            File mockLoadingFile = mock(File.class);
            when(mockLoadingFile.exists()).thenReturn(true);

            try {
                // Set up incomplete download state
                Field loadingFileField = FileLoader.class.getDeclaredField("mLoadingFile");
                loadingFileField.setAccessible(true);
                loadingFileField.set(fileLoader, mockLoadingFile);

                Field fullyDownloadedField = FileLoader.class.getDeclaredField("mIsFileFullyDownloaded");
                fullyDownloadedField.setAccessible(true);
                fullyDownloadedField.set(fileLoader, false);

                Field connectionField = FileLoader.class.getDeclaredField("mConnection");
                connectionField.setAccessible(true);
                connectionField.set(fileLoader, mockConnection);

                fileLoader.stop();

                // Verify file deletion and connection disconnect
                verify(mockLoadingFile).delete();
                verify(mockExecutorService).submit(any(Runnable.class));

            } catch (Exception e) {
                fail("Failed to test stop with incomplete file: " + e.getMessage());
            }
        }
    }

    @Test
    public void testHandleEmulatorWhenRunningOnEmulator() {
        try (MockedStatic<Utils> mockedUtils = Mockito.mockStatic(Utils.class)) {
            mockedUtils.when(() -> Utils.isEmulator()).thenReturn(true);

            fileLoader = new FileLoader(testUrl, mockContext, mockCallback, false);

            try {
                Method handleEmulatorMethod = FileLoader.class.getDeclaredMethod("handleEmulator");
                handleEmulatorMethod.setAccessible(true);
                handleEmulatorMethod.invoke(fileLoader);

                // Verify that useMobileNetworkForCaching was set to true
                Field field = FileLoader.class.getDeclaredField("useMobileNetworkForCaching");
                field.setAccessible(true);
                boolean value = (boolean) field.get(null);
                assertTrue(value);

            } catch (Exception e) {
                fail("Failed to test handleEmulator: " + e.getMessage());
            }
        }
    }

    @Test
    public void testHandleEmulatorWhenNotRunningOnEmulator() {
        try (MockedStatic<Utils> mockedUtils = Mockito.mockStatic(Utils.class)) {
            mockedUtils.when(() -> Utils.isEmulator()).thenReturn(false);

            fileLoader = new FileLoader(testUrl, mockContext, mockCallback, false);

            try {
                Method handleEmulatorMethod = FileLoader.class.getDeclaredMethod("handleEmulator");
                handleEmulatorMethod.setAccessible(true);
                handleEmulatorMethod.invoke(fileLoader);

                // Verify that useMobileNetworkForCaching remains false
                Field field = FileLoader.class.getDeclaredField("useMobileNetworkForCaching");
                field.setAccessible(true);
                boolean value = (boolean) field.get(null);
                assertFalse(value);

            } catch (Exception e) {
                fail("Failed to test handleEmulator when not on emulator: " + e.getMessage());
            }
        }
    }

    @Test
    public void testMobileNetworkWithUseMobileNetworkEnabled() {
        try (MockedStatic<RequestParametersProvider> mockedProvider = Mockito.mockStatic(RequestParametersProvider.class);
             MockedStatic<ExecutorHelper> mockedExecutor = Mockito.mockStatic(ExecutorHelper.class)) {

            mockedProvider.when(() -> RequestParametersProvider.getConnectionType(mockContext))
                    .thenReturn(ConnectionType.MOBILE);

            java.util.concurrent.ExecutorService mockExecutorService = mock(java.util.concurrent.ExecutorService.class);
            mockedExecutor.when(() -> ExecutorHelper.getExecutor()).thenReturn(mockExecutorService);

            // Enable mobile network caching
            FileLoader.setUseMobileNetworkForCaching(true);

            fileLoader = new FileLoader(testUrl, mockContext, mockCallback, false);

            try {
                Method maybeLoadFileMethod = FileLoader.class.getDeclaredMethod("maybeLoadFile");
                maybeLoadFileMethod.setAccessible(true);
                maybeLoadFileMethod.invoke(fileLoader);

                // Should proceed to load since mobile network is enabled
                verify(mockExecutorService).submit(any(Runnable.class));
                verify(mockCallback, never()).onError(any());

            } catch (Exception e) {
                fail("Failed to test mobile network with caching enabled: " + e.getMessage());
            }
        }
    }
}
