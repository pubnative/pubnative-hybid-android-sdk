// HyBid SDK License
//
// https://github.com/pubnative/pubnative-hybid-android-sdk/blob/main/LICENSE
//
package net.pubnative.lite.sdk.utils;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Environment;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.MockedStatic;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.GraphicsMode;
import org.robolectric.shadows.ShadowLooper;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.RejectedExecutionException;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.isNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.mockingDetails;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import net.pubnative.lite.sdk.HyBidError;
import net.pubnative.lite.sdk.HyBidErrorCode;

import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import okhttp3.mockwebserver.SocketPolicy;
import okio.Buffer;

@RunWith(RobolectricTestRunner.class)
@GraphicsMode(GraphicsMode.Mode.NATIVE)
public class PNBitmapDownloaderTest {

    private static final int MAX_IMAGE_SIZE_BYTES = 5 * 1024 * 1024;
    private static final long CALLBACK_TIMEOUT_MS = 5000;
    private static final String SVG_IMAGE =
            "<svg xmlns=\"http://www.w3.org/2000/svg\" width=\"10\" height=\"10\"><rect width=\"10\" height=\"10\"/></svg>";

    private MockWebServer server;

    @Before
    public void setUp() throws Exception {
        server = new MockWebServer();
        server.start();
    }

    @After
    public void tearDown() throws Exception {
        server.shutdown();
    }

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
    public void download_withLocalFile_callOnDownloadFinish() throws Exception {
        String url = Uri.fromFile(createPngFile(10, 10)).toString();

        Bitmap bitmap = captureBitmap(downloadAndAwait(url), url);

        assertEquals(10, bitmap.getWidth());
    }

    @Test
    @SuppressWarnings("deprecation")
    public void download_withLocalFile_doesNotPopulateBitmapCache() throws Exception {
        String url = Uri.fromFile(createPngFile(10, 10)).toString();

        captureBitmap(downloadAndAwait(url), url);

        assertNull(PNBitmapLruCache.getBitmapFromMemCache(url));
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

    // ===== downloadTask network handling =====

    @Test
    public void download_fromNetwork_makesSingleRequestAndReturnsBitmap() {
        server.enqueue(new MockResponse().setHeader("Content-Type", "image/png").setBody(new Buffer().write(createPng(10, 10))));
        String url = server.url("/image.png").toString();

        Bitmap bitmap = captureBitmap(downloadAndAwait(url), url);

        assertEquals(10, bitmap.getWidth());
        assertEquals(1, server.getRequestCount());
    }

    @Test
    public void download_fromNetwork_withSvgContentType_rendersSvg() {
        server.enqueue(new MockResponse().setHeader("Content-Type", "image/svg+xml").setBody(SVG_IMAGE));
        String url = server.url("/image.svg").toString();

        PNBitmapDownloader.DownloadListener listener = downloadAndAwait(url);

        ArgumentCaptor<Bitmap> captor = ArgumentCaptor.forClass(Bitmap.class);
        verify(listener).onDownloadFinish(eq(url), captor.capture());
        assertEquals(10, captor.getValue().getWidth());
        assertEquals(10, captor.getValue().getHeight());
        assertEquals(1, server.getRequestCount());
    }

    @Test
    public void download_fromNetwork_withErrorResponse_failsWithInternalError() {
        server.enqueue(new MockResponse().setResponseCode(404));
        String url = server.url("/missing.png").toString();

        PNBitmapDownloader.DownloadListener listener = downloadAndAwait(url);

        HyBidError error = captureFailure(listener, url);
        assertEquals(HyBidErrorCode.INTERNAL_ERROR, error.getErrorCode());
        assertEquals(1, server.getRequestCount());
    }

    @Test
    public void download_fromNetwork_withSameProtocolRedirect_followsRedirectAndReturnsBitmap() throws Exception {
        server.enqueue(new MockResponse().setResponseCode(302).setHeader("Location", server.url("/final.png").toString()));
        server.enqueue(new MockResponse().setHeader("Content-Type", "image/png").setBody(new Buffer().write(createPng(10, 10))));
        String url = server.url("/redirect-same-protocol.png").toString();

        Bitmap bitmap = captureBitmap(downloadAndAwait(url), url);

        assertEquals(10, bitmap.getWidth());
        assertEquals(2, server.getRequestCount());
        assertEquals("/redirect-same-protocol.png", server.takeRequest().getPath());
        assertEquals("/final.png", server.takeRequest().getPath());
    }

    @Test
    public void download_fromNetwork_withUndecodableBody_finishesWithNullBitmap() {
        server.enqueue(new MockResponse().setHeader("Content-Type", "image/png").setBody("not an image"));
        String url = server.url("/broken.png").toString();

        PNBitmapDownloader.DownloadListener listener = downloadAndAwait(url);

        verify(listener).onDownloadFinish(eq(url), isNull());
    }

    @Test
    public void download_fromNetwork_withBodyShorterThanContentLength_failsWithInternalError() {
        server.enqueue(new MockResponse().setHeader("Content-Type", "image/png").setBody("abc")
                .setHeader("Content-Length", "10").setSocketPolicy(SocketPolicy.DISCONNECT_AT_END));
        String url = server.url("/truncated.png").toString();

        PNBitmapDownloader.DownloadListener listener = downloadAndAwait(url);

        assertEquals(HyBidErrorCode.INTERNAL_ERROR, captureFailure(listener, url).getErrorCode());
    }

    @Test
    public void download_fromNetwork_withContentLengthAtLimit_isAccepted() {
        server.enqueue(new MockResponse().setHeader("Content-Type", "image/png").setBody(new Buffer().write(new byte[MAX_IMAGE_SIZE_BYTES])));
        String url = server.url("/at-limit.png").toString();

        PNBitmapDownloader.DownloadListener listener = downloadAndAwait(url);

        verify(listener).onDownloadFinish(eq(url), isNull());
        verify(listener, never()).onDownloadFailed(any(), any());
    }

    @Test
    public void download_fromNetwork_withChunkedBody_returnsBitmap() {
        byte[] png = createNoisyPng(100, 100);
        assertTrue("PNG must exceed the initial read buffer, got " + png.length + " bytes", png.length > 8 * 1024);
        server.enqueue(new MockResponse().setHeader("Content-Type", "image/png").setChunkedBody(new Buffer().write(png), 4 * 1024));
        String url = server.url("/chunked.png").toString();

        Bitmap bitmap = captureBitmap(downloadAndAwait(url), url);

        assertEquals(100, bitmap.getWidth());
        assertEquals(100, bitmap.getHeight());
    }

    @Test
    public void download_fromNetwork_withChunkedSvg_rendersSvg() {
        server.enqueue(new MockResponse().setHeader("Content-Type", "image/svg+xml").setChunkedBody(SVG_IMAGE, 16));
        String url = server.url("/chunked.svg").toString();

        Bitmap bitmap = captureBitmap(downloadAndAwait(url), url);

        assertEquals(10, bitmap.getWidth());
        assertEquals(10, bitmap.getHeight());
    }

    @Test
    public void download_fromNetwork_withChunkedBodyAtLimit_isAccepted() {
        server.enqueue(new MockResponse().setHeader("Content-Type", "image/png").setChunkedBody(new Buffer().write(new byte[MAX_IMAGE_SIZE_BYTES]), 64 * 1024));
        String url = server.url("/at-limit-chunked.png").toString();

        PNBitmapDownloader.DownloadListener listener = downloadAndAwait(url);

        verify(listener).onDownloadFinish(eq(url), isNull());
        verify(listener, never()).onDownloadFailed(any(), any());
    }

    @Test
    public void download_fromNetwork_withContentLengthAboveLimit_failsWithResourceExhaustion() {
        server.enqueue(new MockResponse().setHeader("Content-Type", "image/png").setBody(new Buffer().write(new byte[MAX_IMAGE_SIZE_BYTES + 1])));
        String url = server.url("/huge.png").toString();

        PNBitmapDownloader.DownloadListener listener = downloadAndAwait(url);

        assertEquals(HyBidErrorCode.RESOURCE_EXHAUSTION, captureFailure(listener, url).getErrorCode());
    }

    @Test
    public void download_fromNetwork_withChunkedBodyAboveLimit_failsWithResourceExhaustion() {
        server.enqueue(new MockResponse().setHeader("Content-Type", "image/png").setChunkedBody(new Buffer().write(new byte[MAX_IMAGE_SIZE_BYTES + 1]), 64 * 1024));
        String url = server.url("/huge-chunked.png").toString();

        PNBitmapDownloader.DownloadListener listener = downloadAndAwait(url);

        assertEquals(HyBidErrorCode.RESOURCE_EXHAUSTION, captureFailure(listener, url).getErrorCode());
    }

    // ===== downsampling =====

    @Test
    public void download_fromNetwork_withTargetSize_downsamplesBitmap() throws Exception {
        server.enqueue(new MockResponse().setHeader("Content-Type", "image/png").setBody(new Buffer().write(createPng(400, 400))));
        String url = server.url("/large.png").toString();

        Bitmap bitmap = captureBitmap(downloadAndAwait(url, 100, 100), url);

        assertEquals(100, bitmap.getWidth());
        assertEquals(100, bitmap.getHeight());
    }

    @Test
    public void download_fromNetwork_withoutTargetSize_isNotDownsampled() {
        server.enqueue(new MockResponse().setHeader("Content-Type", "image/png").setBody(new Buffer().write(createPng(400, 400))));
        String url = server.url("/no-target.png").toString();

        Bitmap bitmap = captureBitmap(downloadAndAwait(url, 0, 0), url);

        assertEquals(400, bitmap.getWidth());
        assertEquals(400, bitmap.getHeight());
    }

    @Test
    public void download_fromNetwork_withPartialTargetSize_isNotDownsampled() {
        server.enqueue(new MockResponse().setHeader("Content-Type", "image/png").setBody(new Buffer().write(createPng(400, 400))));
        String url = server.url("/partial-target.png").toString();

        Bitmap bitmap = captureBitmap(downloadAndAwait(url, 100, 0), url);

        assertEquals(400, bitmap.getWidth());
        assertEquals(400, bitmap.getHeight());
    }

    @Test
    public void download_fromNetwork_smallerThanTarget_isNotDownsampled() throws Exception {
        server.enqueue(new MockResponse().setHeader("Content-Type", "image/png").setBody(new Buffer().write(createPng(50, 50))));
        String url = server.url("/small.png").toString();

        Bitmap bitmap = captureBitmap(downloadAndAwait(url, 100, 100), url);

        assertEquals(50, bitmap.getWidth());
        assertEquals(50, bitmap.getHeight());
    }

    @Test
    public void download_withLocalFile_withTargetSize_downsamplesBitmap() throws Exception {
        String url = Uri.fromFile(createPngFile(400, 400)).toString();

        Bitmap bitmap = captureBitmap(downloadAndAwait(url, 100, 100), url);

        assertEquals(100, bitmap.getWidth());
        assertEquals(100, bitmap.getHeight());
    }

    @Test
    public void calculateInSampleSize_returnsLargestPowerOfTwoKeepingBothSidesAboveTarget() {
        PNBitmapDownloader downloader = new PNBitmapDownloader();

        assertEquals(1, downloader.calculateInSampleSize(boundsOf(100, 100), 100, 100));
        assertEquals(1, downloader.calculateInSampleSize(boundsOf(50, 50), 100, 100));
        assertEquals(4, downloader.calculateInSampleSize(boundsOf(400, 400), 100, 100));
        assertEquals(4, downloader.calculateInSampleSize(boundsOf(1000, 500), 100, 100));
        assertEquals(2, downloader.calculateInSampleSize(boundsOf(1000, 300), 100, 100));
    }

    private static BitmapFactory.Options boundsOf(int width, int height) {
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.outWidth = width;
        options.outHeight = height;
        return options;
    }

    private static File createPngFile(int width, int height) throws Exception {
        File file = File.createTempFile("local_image", ".png");
        file.deleteOnExit();
        try (FileOutputStream outputStream = new FileOutputStream(file)) {
            outputStream.write(createPng(width, height));
        }
        return file;
    }

    private static byte[] createNoisyPng(int width, int height) {
        int[] pixels = new int[width * height];
        Random random = new Random(42);
        for (int i = 0; i < pixels.length; i++) {
            pixels[i] = 0xFF000000 | random.nextInt(0x1000000);
        }
        Bitmap bitmap = Bitmap.createBitmap(pixels, width, height, Bitmap.Config.ARGB_8888);
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream);
        return outputStream.toByteArray();
    }

    private static byte[] createPng(int width, int height) {
        Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream);
        return outputStream.toByteArray();
    }

    private static Bitmap captureBitmap(PNBitmapDownloader.DownloadListener listener, String url) {
        ArgumentCaptor<Bitmap> captor = ArgumentCaptor.forClass(Bitmap.class);
        verify(listener).onDownloadFinish(eq(url), captor.capture());
        assertNotNull(captor.getValue());
        return captor.getValue();
    }

    private PNBitmapDownloader.DownloadListener downloadAndAwait(String url) {
        return downloadAndAwait(url, 0, 0);
    }

    private PNBitmapDownloader.DownloadListener downloadAndAwait(String url, int width, int height) {
        PNBitmapDownloader.DownloadListener listener = mock(PNBitmapDownloader.DownloadListener.class);
        new PNBitmapDownloader().download(url, width, height, listener);

        long deadline = System.currentTimeMillis() + CALLBACK_TIMEOUT_MS;
        while (mockingDetails(listener).getInvocations().isEmpty() && System.currentTimeMillis() < deadline) {
            ShadowLooper.idleMainLooper();
            try {
                Thread.sleep(10);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                break;
            }
        }
        if (mockingDetails(listener).getInvocations().isEmpty()) {
            fail("Download callback was not invoked within " + CALLBACK_TIMEOUT_MS + " ms");
        }
        return listener;
    }

    private HyBidError captureFailure(PNBitmapDownloader.DownloadListener listener, String url) {
        ArgumentCaptor<Exception> captor = ArgumentCaptor.forClass(Exception.class);
        verify(listener).onDownloadFailed(eq(url), captor.capture());
        assertTrue(captor.getValue() instanceof HyBidError);
        return (HyBidError) captor.getValue();
    }
}
