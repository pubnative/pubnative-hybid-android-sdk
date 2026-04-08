// HyBid SDK License
//
// https://github.com/pubnative/pubnative-hybid-android-sdk/blob/main/LICENSE
//
package net.pubnative.lite.sdk.utils;

import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Environment;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.MockedStatic;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.shadows.ShadowLooper;

import java.io.File;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.TimeUnit;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import net.pubnative.lite.sdk.HyBid;
import net.pubnative.lite.sdk.HyBidError;
import net.pubnative.lite.sdk.HyBidErrorCode;

@RunWith(RobolectricTestRunner.class)
public class PNBitmapDownloaderTest {
    @Test
    public void download_withNullListener_pass() {
        PNBitmapDownloader downloader = new PNBitmapDownloader();
        downloader.download("valid_string", null);
    }

    @Test
    public void download_withEmptyUrl_callOnDownloadFailed() {
        String emptyUrl = "";
        PNBitmapDownloader downloader = new PNBitmapDownloader();
        PNBitmapDownloader.DownloadListener listener = spy(PNBitmapDownloader.DownloadListener.class);
        downloader.download(emptyUrl, listener);

        ShadowLooper.runUiThreadTasksIncludingDelayedTasks();

        verify(listener).onDownloadFailed(eq(emptyUrl), any(Exception.class));
    }

    @Test
    public void download_withEmptyUrl_failsWithInvalidUrlError() {
        String emptyUrl = "";
        PNBitmapDownloader downloader = new PNBitmapDownloader();
        PNBitmapDownloader.DownloadListener listener = spy(PNBitmapDownloader.DownloadListener.class);
        downloader.download(emptyUrl, listener);

        ShadowLooper.runUiThreadTasksIncludingDelayedTasks();

        ArgumentCaptor<Exception> captor = ArgumentCaptor.forClass(Exception.class);
        verify(listener).onDownloadFailed(eq(emptyUrl), captor.capture());
        assertTrue(captor.getValue() instanceof HyBidError);
        assertEquals(HyBidErrorCode.INVALID_URL, ((HyBidError) captor.getValue()).getErrorCode());
    }

    @Test
    public void download_withWrongUrl_callOnDownloadFailed() {
        String wrongUrl = "should_fail";
        PNBitmapDownloader downloader = new PNBitmapDownloader();
        PNBitmapDownloader.DownloadListener listener = spy(PNBitmapDownloader.DownloadListener.class);
        downloader.download(wrongUrl, listener);

        ShadowLooper.runUiThreadTasksIncludingDelayedTasks();

        verify(listener).onDownloadFailed(eq(wrongUrl), any(Exception.class));
    }

    @Test
    public void download_withWrongUrl_failsWithInvalidUrlError() {
        String wrongUrl = "should_fail";
        PNBitmapDownloader downloader = new PNBitmapDownloader();
        PNBitmapDownloader.DownloadListener listener = spy(PNBitmapDownloader.DownloadListener.class);
        downloader.download(wrongUrl, listener);

        ShadowLooper.runUiThreadTasksIncludingDelayedTasks();

        ArgumentCaptor<Exception> captor = ArgumentCaptor.forClass(Exception.class);
        verify(listener).onDownloadFailed(eq(wrongUrl), captor.capture());
        assertTrue(captor.getValue() instanceof HyBidError);
        assertEquals(HyBidErrorCode.INVALID_URL, ((HyBidError) captor.getValue()).getErrorCode());
    }

    @Test
    public void download_withLocalFile_callOnDownloadFinish() {
        File file = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES), "test_image_file.jpg");

        String url = Uri.fromFile(file).toString();
        PNBitmapDownloader downloader = new PNBitmapDownloader();
        PNBitmapDownloader.DownloadListener listener = spy(PNBitmapDownloader.DownloadListener.class);
        downloader.download(url, listener);

        CountDownLatch latch = new CountDownLatch(1);
        try {
            latch.await(2, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            HyBid.reportException(e);
            e.printStackTrace();
        }

        ShadowLooper.runUiThreadTasksIncludingDelayedTasks();

        verify(listener).onDownloadFinish(eq(url), any(Bitmap.class));
    }

    // ===== downloadImage() error handling =====

    @Test
    public void downloadImage_rejectedExecution_failsWithResourceExhaustion() {
        String httpUrl = "https://example.com/image.png";
        ExecutorService mockExecutor = mock(ExecutorService.class);
        when(mockExecutor.submit(any(Runnable.class)))
                .thenThrow(new RejectedExecutionException("pool full"));

        try (MockedStatic<BitmapDownloaderExecutor> mockedStatic = mockStatic(BitmapDownloaderExecutor.class)) {
            mockedStatic.when(BitmapDownloaderExecutor::getExecutor).thenReturn(mockExecutor);

            PNBitmapDownloader downloader = new PNBitmapDownloader();
            PNBitmapDownloader.DownloadListener listener = spy(PNBitmapDownloader.DownloadListener.class);
            downloader.download(httpUrl, listener);

            ShadowLooper.runUiThreadTasksIncludingDelayedTasks();

            ArgumentCaptor<Exception> captor = ArgumentCaptor.forClass(Exception.class);
            verify(listener).onDownloadFailed(eq(httpUrl), captor.capture());
            assertTrue(captor.getValue() instanceof HyBidError);
            assertEquals(HyBidErrorCode.RESOURCE_EXHAUSTION, ((HyBidError) captor.getValue()).getErrorCode());
        }
    }

    @Test
    public void downloadImage_threadCreationError_failsWithResourceExhaustion() {
        String httpUrl = "https://example.com/image.png";
        ExecutorService mockExecutor = mock(ExecutorService.class);
        when(mockExecutor.submit(any(Runnable.class)))
                .thenThrow(new OutOfMemoryError("unable to create native thread"));

        try (MockedStatic<BitmapDownloaderExecutor> mockedStatic = mockStatic(BitmapDownloaderExecutor.class)) {
            mockedStatic.when(BitmapDownloaderExecutor::getExecutor).thenReturn(mockExecutor);

            PNBitmapDownloader downloader = new PNBitmapDownloader();
            PNBitmapDownloader.DownloadListener listener = spy(PNBitmapDownloader.DownloadListener.class);
            downloader.download(httpUrl, listener);

            ShadowLooper.runUiThreadTasksIncludingDelayedTasks();

            ArgumentCaptor<Exception> captor = ArgumentCaptor.forClass(Exception.class);
            verify(listener).onDownloadFailed(eq(httpUrl), captor.capture());
            assertTrue(captor.getValue() instanceof HyBidError);
            assertEquals(HyBidErrorCode.RESOURCE_EXHAUSTION, ((HyBidError) captor.getValue()).getErrorCode());
            assertNotNull(captor.getValue().getCause());
        }
    }

    // ===== loadCachedImage() error handling =====

    @Test
    public void loadCachedImage_rejectedExecution_failsWithResourceExhaustion() {
        File file = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES), "test.jpg");
        String fileUrl = Uri.fromFile(file).toString();
        ExecutorService mockExecutor = mock(ExecutorService.class);
        when(mockExecutor.submit(any(Runnable.class)))
                .thenThrow(new RejectedExecutionException("pool full"));

        try (MockedStatic<BitmapDownloaderExecutor> mockedStatic = mockStatic(BitmapDownloaderExecutor.class)) {
            mockedStatic.when(BitmapDownloaderExecutor::getExecutor).thenReturn(mockExecutor);

            PNBitmapDownloader downloader = new PNBitmapDownloader();
            PNBitmapDownloader.DownloadListener listener = spy(PNBitmapDownloader.DownloadListener.class);
            downloader.download(fileUrl, listener);

            ShadowLooper.runUiThreadTasksIncludingDelayedTasks();

            ArgumentCaptor<Exception> captor = ArgumentCaptor.forClass(Exception.class);
            verify(listener).onDownloadFailed(eq(fileUrl), captor.capture());
            assertTrue(captor.getValue() instanceof HyBidError);
            assertEquals(HyBidErrorCode.RESOURCE_EXHAUSTION, ((HyBidError) captor.getValue()).getErrorCode());
        }
    }

    @Test
    public void loadCachedImage_threadCreationError_failsWithResourceExhaustion() {
        File file = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES), "test.jpg");
        String fileUrl = Uri.fromFile(file).toString();
        ExecutorService mockExecutor = mock(ExecutorService.class);
        when(mockExecutor.submit(any(Runnable.class)))
                .thenThrow(new OutOfMemoryError("unable to create native thread"));

        try (MockedStatic<BitmapDownloaderExecutor> mockedStatic = mockStatic(BitmapDownloaderExecutor.class)) {
            mockedStatic.when(BitmapDownloaderExecutor::getExecutor).thenReturn(mockExecutor);

            PNBitmapDownloader downloader = new PNBitmapDownloader();
            PNBitmapDownloader.DownloadListener listener = spy(PNBitmapDownloader.DownloadListener.class);
            downloader.download(fileUrl, listener);

            ShadowLooper.runUiThreadTasksIncludingDelayedTasks();

            ArgumentCaptor<Exception> captor = ArgumentCaptor.forClass(Exception.class);
            verify(listener).onDownloadFailed(eq(fileUrl), captor.capture());
            assertTrue(captor.getValue() instanceof HyBidError);
            assertEquals(HyBidErrorCode.RESOURCE_EXHAUSTION, ((HyBidError) captor.getValue()).getErrorCode());
            assertNotNull(captor.getValue().getCause());
        }
    }
}
